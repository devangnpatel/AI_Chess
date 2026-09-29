package chess.players.ai;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.players.ChessPlayerCPU;
import game.utility.Properties;
import game.utility.Properties.PlayerColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.AbstractMap;
import java.util.concurrent.Future;

/**
 *
 * @author devang
 */
public class MiniMax {
    private final PlayerColor     cpuPlayerColor;
    private final ChessBoardState tempBoardState;
    private final int             level;
        
    public MiniMax(ChessBoardState boardState, Properties.PlayerColor playerColor, int lev)
    {
        cpuPlayerColor = playerColor;
        tempBoardState = boardState;
        level = lev;
    }

    public ChessMove evaluate()
    {
        // this method is the initial "maximizing" call
        
        List<ChessMove> validMoves = ChessPlayerCPU.getValidMoves(tempBoardState,cpuPlayerColor);
        Collections.shuffle(validMoves);
        List<AbstractMap.SimpleEntry<ChessMove,Integer>> moves = new ArrayList<>();
        ExecutorService eS = Executors.newCachedThreadPool();
        for (ChessMove move : validMoves)
        {
            ChessBoardState t_boardState = ChessBoardState.copy(tempBoardState);
            move.commitMove(t_boardState);
            
            int f_score = minimize_thread(System.currentTimeMillis(),0,Integer.MIN_VALUE,Integer.MAX_VALUE,level,Properties.oppositeColor(cpuPlayerColor),t_boardState);
            moves.add(new AbstractMap.SimpleEntry<>(move,f_score));
        }
        try {
            eS.shutdown();
            while (!eS.isTerminated()) {}
        } catch (Exception e) {
            
        }
        
        int bestScore;
        List<ChessMove> bestMoves  = new ArrayList<>();
        
        bestScore = moves.get(0).getValue();
        bestMoves.add(moves.get(0).getKey());
        
        for (int m = 1; m < moves.size(); m++)
        {
            ChessMove m_move  = moves.get(m).getKey();
            int       m_score = moves.get(m).getValue();
            
            if (m_score > bestScore)
            {
                bestScore = m_score;
                bestMoves.clear();
                bestMoves.add(m_move);
            }
            else if (m_score == bestScore)
            {
                bestMoves.add(m_move);
            }
        }
        if (bestMoves.isEmpty()) return null;
        double r = ThreadLocalRandom.current().nextDouble();
        Collections.shuffle(bestMoves);
        return bestMoves.get((int)(r*bestMoves.size()));
    }
    
    public int minimize_thread(long time,int depth, int alpha, int beta, int level, Properties.PlayerColor playerColor, ChessBoardState boardState){
        Callable<Integer> getScoreTask = new Minimize(time,depth+1,alpha,beta,level,playerColor,boardState);
        int score = 0;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> fscore = executorService.submit(getScoreTask);
            score = fscore.get();
            executorService.shutdownNow();
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        } catch (Exception e) {

        }
        return score;
    }
    
    public int maximize_thread(long time,int depth, int alpha, int beta, int level, Properties.PlayerColor playerColor, ChessBoardState boardState){
        Callable<Integer> getScoreTask = new Maximize(time,depth+1,alpha,beta,level,playerColor,boardState);
        int score = 0;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> fscore = executorService.submit(getScoreTask);
            score = fscore.get();
            executorService.shutdownNow();
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
            
        } catch (Exception e) {

        }
        return score;
    }
 }