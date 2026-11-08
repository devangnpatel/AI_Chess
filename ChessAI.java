package chess.players.ai;

import chess.board.ChessBoardState;
import chess.pieces.ChessPiece;
import chess.pieces.PieceBishop;
import chess.pieces.PieceKing;
import chess.pieces.PieceKnight;
import chess.pieces.PiecePawn;
import chess.pieces.PieceQueen;
import chess.pieces.PieceRook;
import chess.moves.ChessMove;
import chess.players.ChessPlayerCPU;
import game.utility.Location;
import game.utility.Properties.PlayerColor;

/**
 *
 * @author devang
 */
public class ChessAI extends Thread {
    public static int level;
    public static int minDepth[] = {3,3,4,4,5};
    public static int maxDepth[] = {4,6,7,9,12};
    public static long maxTime[] = {5000,10000,15000,20000,30000};
    public static double pBoost[] = {0.5,0.5,0.5,0.25,0.25};

    PlayerColor cpuColor;
    ChessPlayerCPU cpuPlayer;
    
    public ChessAI(ChessPlayerCPU chessPlayer,PlayerColor playerCPUColor, int lev)
    {
        cpuPlayer = chessPlayer;
        cpuColor = playerCPUColor;
        level = lev;
    }
    
    public static int evaluate(ChessBoardState boardState,PlayerColor playerCPUColor)
    {
        int score = 0;

        for (Location location : Location.allLocations())
        {
            ChessPiece piece = boardState.getPiece(location);
            if (piece == null) continue;
            if (piece.getColor() == playerCPUColor)
                score += scorePieceValue(piece);
            else if (piece.getColor() != playerCPUColor)
                score -= scorePieceValue(piece);
        }
        
        return score;
    }
    
    private static int scorePieceValue(ChessPiece piece)
    {
        int score = 0;
        
        if (piece == null)
        {
            // do nothing
        }
        else
        {
            if (piece instanceof PiecePawn) score = 100;
            if (piece instanceof PieceKnight) score = 300;
            if (piece instanceof PieceBishop) score = 300;
            if (piece instanceof PieceRook) score = 500;
            if (piece instanceof PieceQueen) score = 900;
            if (piece instanceof PieceKing) score = 9000;
        }
        return score;
    }

    @Override
    public void run()
        {
	        ChessBoardState snapshot = ChessBoardState.copy(cpuPlayer.getBoardState());
	        ChessMove move = new Search(snapshot, cpuColor, level).findBestMove();
	        cpuPlayer.commitMove(move);
	        
           /* MiniMax minimax = new MiniMax(cpuPlayer.getBoardState(),cpuColor,level);
            ChessMove move = minimax.evaluate();
            cpuPlayer.commitMove(move); */
        }
}
