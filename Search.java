package chess.players.ai;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.pieces.ChessPiece;
import chess.pieces.PieceKing;
import chess.players.ChessPlayerCPU;
import game.utility.Location;
import game.utility.Properties;
import game.utility.Properties.PlayerColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Replaces MiniMax / Minimize / Maximize.
 *
 *  - Negamax alpha-beta: one function instead of two mirrored classes.
 *    Every score is from the point of view of the side to move.
 *  - Iterative deepening: searches depth 1, 2, 3 ... until time runs out,
 *    always keeping the best move from the last COMPLETED depth.
 *  - Move ordering: captures (biggest material gain) first -> far more cutoffs.
 *  - Quiescence search: at the horizon keep searching captures so the engine
 *    doesn't stop in the middle of an exchange (replaces the random pBoost extension).
 *  - Root parallelism: the first root move is searched alone to get a good bound,
 *    then the remaining root moves are split across a fixed pool of CPU threads
 *    that share the best score found so far (alpha).
 *
 * Only uses APIs already in the project: ChessBoardState.copy, getPiece,
 * ChessMove.commitMove, ChessPlayerCPU.getValidMoves, ChessAI.evaluate.
 * NOTE: root moves are searched on several threads at once, so getValidMoves()
 * and commitMove() must not modify shared static state.
 */
public class Search {

    private static final int INF  = 1_000_000;
    private static final int MATE =   100_000;
    private static final int MAX_QUIESCENCE_DEPTH = 6;

    private final ChessBoardState root;
    private final PlayerColor     cpuColor;
    private final int             level;
    private final long            deadline;

    private volatile boolean enforceDeadline = false;
    private volatile boolean stop = false;

    public Search(ChessBoardState rootBoard, PlayerColor cpuColor, int level) {
        this.root     = rootBoard;
        this.cpuColor = cpuColor;
        this.level    = level;
        this.deadline = System.currentTimeMillis() + ChessAI.maxTime[level];
    }

    /** Returns the best move, or null if there are no legal moves. */
    public ChessMove findBestMove() {
        List<ChessMove> rootMoves = ChessPlayerCPU.getValidMoves(root, cpuColor);
        if (rootMoves.isEmpty()) return null;
        Collections.shuffle(rootMoves);          // variety between equally good moves

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        ChessMove best = rootMoves.get(0);

        try {
            for (int depth = 1; depth <= ChessAI.maxDepth[level]; depth++) {
                enforceDeadline = depth > 1;     // depth 1 always completes
                List<ScoredMove> results = searchRoot(pool, rootMoves, depth);
                if (results == null) break;      // out of time: keep previous depth's answer

                // Exact scores first (highest first), then fail-low moves.
                // List.sort is stable, so ties keep their shuffled order.
                results.sort((a, b) -> {
                    if (a.exact != b.exact) return a.exact ? -1 : 1;
                    return Integer.compare(b.score, a.score);
                });
                best = results.get(0).move;

                // Next iteration searches the best moves first -> better pruning.
                rootMoves = new ArrayList<>();
                for (ScoredMove r : results) rootMoves.add(r.move);

                if (results.get(0).score > MATE - 1000) break;   // found a forced win
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            pool.shutdownNow();
        }
        return best;
    }

    // ------------------------------------------------------------------ root

    private List<ScoredMove> searchRoot(ExecutorService pool, List<ChessMove> moves, int depth)
            throws InterruptedException {
        AtomicInteger alpha = new AtomicInteger(-INF);
        List<ScoredMove> results = new ArrayList<>(moves.size());

        // 1) First (best-guess) move alone, to establish a strong alpha.
        results.add(searchRootMove(moves.get(0), depth, alpha));
        if (stop) return null;

        // 2) The rest in parallel, sharing alpha.
        List<Callable<ScoredMove>> tasks = new ArrayList<>();
        for (int i = 1; i < moves.size(); i++) {
            final ChessMove m = moves.get(i);
            tasks.add(() -> searchRootMove(m, depth, alpha));
        }
        List<Future<ScoredMove>> futures = pool.invokeAll(tasks);
        if (stop) return null;

        for (Future<ScoredMove> f : futures) {
            try {
                results.add(f.get());
            } catch (ExecutionException e) {
                // Don't hide bugs the way the old code did.
                throw new RuntimeException("Search thread failed", e.getCause());
            }
        }
        return results;
    }

    private ScoredMove searchRootMove(ChessMove move, int depth, AtomicInteger alpha) {
        ChessBoardState child = ChessBoardState.copy(root);
        move.commitMove(child);
        int a = alpha.get();
        int score = -negamax(child, Properties.oppositeColor(cpuColor), depth - 1, 1, -INF, -a);
        alpha.accumulateAndGet(score, Math::max);
        // If score <= a the move "failed low": we only know it is no better than a.
        return new ScoredMove(move, score, score > a);
    }

    // --------------------------------------------------------------- negamax

    private int negamax(ChessBoardState board, PlayerColor side,
                        int depth, int ply, int alpha, int beta) {
        if (timeUp()) return 0;
        if (!hasKing(board, side)) return -MATE + ply;   // our king was captured
        if (depth <= 0) return quiesce(board, side, ply, alpha, beta, 0);

        List<Child> children = expand(board, side, false);
        if (children.isEmpty()) {
            // No moves. If you add an isInCheck() helper:
            //   in check -> return -MATE + ply;  otherwise -> return 0 (stalemate)
            return ChessAI.evaluate(board, side);
        }

        PlayerColor opp = Properties.oppositeColor(side);
        int best = -INF;
        for (Child c : children) {
            int score = -negamax(c.board, opp, depth - 1, ply + 1, -beta, -alpha);
            if (stop) return 0;
            if (score > best)  best = score;
            if (score > alpha) alpha = score;
            if (alpha >= beta) break;                    // cutoff
        }
        return best;
    }

    /** Keep searching captures until the position is quiet. */
    private int quiesce(ChessBoardState board, PlayerColor side,
                        int ply, int alpha, int beta, int qDepth) {
        if (timeUp()) return 0;
        if (!hasKing(board, side)) return -MATE + ply;

        int standPat = ChessAI.evaluate(board, side);
        if (standPat >= beta) return standPat;
        if (standPat > alpha) alpha = standPat;
        if (qDepth >= MAX_QUIESCENCE_DEPTH) return standPat;

        PlayerColor opp = Properties.oppositeColor(side);
        int best = standPat;
        for (Child c : expand(board, side, true)) {
            int score = -quiesce(c.board, opp, ply + 1, -beta, -alpha, qDepth + 1);
            if (stop) return 0;
            if (score > best)  best = score;
            if (score > alpha) alpha = score;
            if (alpha >= beta) break;
        }
        return best;
    }

    // --------------------------------------------------------------- helpers

    /**
     * Generates children, scores each by material gained (captures and
     * promotions are positive) and sorts best-first.
     * If ChessMove can tell you directly whether it captures, use that instead:
     * it avoids copying a board just to find out.
     */
    private static List<Child> expand(ChessBoardState board, PlayerColor side, boolean capturesOnly) {
        List<ChessMove> moves = ChessPlayerCPU.getValidMoves(board, side);
        int before = ChessAI.evaluate(board, side);
        List<Child> out = new ArrayList<>(moves.size());
        for (ChessMove m : moves) {
            ChessBoardState c = ChessBoardState.copy(board);
            m.commitMove(c);
            int gain = ChessAI.evaluate(c, side) - before;
            if (capturesOnly && gain <= 0) continue;
            out.add(new Child(c, gain));
        }
        out.sort((a, b) -> Integer.compare(b.gain, a.gain));
        return out;
    }

    private static boolean hasKing(ChessBoardState board, PlayerColor color) {
        for (Location loc : Location.allLocations()) {
            ChessPiece p = board.getPiece(loc);
            if (p instanceof PieceKing && p.getColor() == color) return true;
        }
        return false;
    }

    private boolean timeUp() {
        if (!stop && enforceDeadline && System.currentTimeMillis() > deadline) stop = true;
        return stop;
    }

    private static final class Child {
        final ChessBoardState board;
        final int gain;
        Child(ChessBoardState board, int gain) { this.board = board; this.gain = gain; }
    }

    private static final class ScoredMove {
        final ChessMove move;
        final int score;
        final boolean exact;
        ScoredMove(ChessMove move, int score, boolean exact) {
            this.move = move; this.score = score; this.exact = exact;
        }
    }
}
