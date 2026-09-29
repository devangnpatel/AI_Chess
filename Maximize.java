package chess.players.ai;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.players.ChessPlayerCPU;
import game.utility.Properties;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Maximize extends Thread implements Callable<Integer> {
        long                   time;
        int                    depth;
        int                    alpha;
        int                    beta;
        int                    level;
        Properties.PlayerColor playerColor;
        ChessBoardState        boardState; 
    
    public int minimize_thread(long time,int depth, int alpha, int beta, int level, Properties.PlayerColor playerColor, ChessBoardState boardState){
        Callable<Integer> getScoreTask = new Minimize(time,depth+1,alpha,beta,level,playerColor,boardState);
        int score = 0;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> fscore = executorService.submit(getScoreTask);
            score = fscore.get();
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        } catch (Exception e) {

        }
        return score;
    }
    
    @Override
    public Integer call()
    {
        if (depth > ChessAI.minDepth[level]) {
                if (depth <= ChessAI.maxDepth[level] && ThreadLocalRandom.current().nextDouble() < ChessAI.pBoost[level])
                {
                    
                }
                else
                {
                    return ChessAI.evaluate(boardState,playerColor);
                }
            }
            if (System.currentTimeMillis() - time > ChessAI.maxTime[level])
            {
                return ChessAI.evaluate(ChessBoardState.copy(boardState),playerColor);
            }
            
            List<ChessMove> validMoves = ChessPlayerCPU.getValidMoves(boardState,playerColor);
            Collections.shuffle(validMoves);
            for (ChessMove move : validMoves)
            {
                if (System.currentTimeMillis() - time > ChessAI.maxTime[level])
                {
                    return ChessAI.evaluate(ChessBoardState.copy(boardState),playerColor);
                }
                ChessBoardState t_boardState = ChessBoardState.copy(boardState);
                move.commitMove(t_boardState);
                
                try {
                    int score = minimize_thread(time,depth+1,alpha,beta,level,Properties.oppositeColor(playerColor),t_boardState);

                    if (Math.random() < 0.5)
                    {
                        if (score >= beta)
                        {
                            return beta;
                        }
                    }
                    else 
                    if (score > beta)
                    {
                        return beta;
                    }
                    
                    if (score > alpha)
                    {
                        alpha = score;
                    }
                } catch (Exception e) {

                }
            }
            
            return alpha;
    }
    public Maximize(long mtime,
                    int mdepth,
                    int malpha, 
                    int mbeta,
                    int mlevel,
                    Properties.PlayerColor mplayerColor, 
                    ChessBoardState mboardState) {
        time        = mtime;
        depth       = mdepth;
        alpha       = malpha;
        beta        = mbeta;
        level       = mlevel;
        playerColor = mplayerColor;
        boardState  = mboardState;
    }
}
