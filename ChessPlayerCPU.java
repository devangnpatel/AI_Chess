package chess.players;

import chess.board.ChessBoardState;
import chess.ChessGame;
import chess.moves.ChessMove;
import chess.moves.MovePromotion;
import chess.moves.MovePromotion.PieceType;
import chess.pieces.ChessPiece;
import chess.players.ai.ChessAI;
import game.utility.Location;
import game.utility.Properties.PlayerColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Inherits from a PlayerCPU, a Board Game Player CPU to provide same
 * interface for a phantom AI player
 * and same interface across any game: Checkers, Chess, Othello, Poker, Chinese-Checkers, etc.
 * @author devang
 */
public class ChessPlayerCPU extends ChessPlayer {
    public int level;
    //public static PlayerColor color;
    private ChessPlayerCPU(ChessGame game, PlayerColor color,int level)
    {
        super(game,color);
        this.level = level;
        //this.color = color;
        
    }
    
    /**
     * Creates a Phantom AI Player, in Object factory fashion
     * @param game the game state representation
     * @param color the color for this AI-player
     * @return a newly constructed Phantom AI CPU Player
     */
    public static ChessPlayerCPU create(ChessGame game, PlayerColor color, int level)
    {
        return new ChessPlayerCPU(game,color,level);
    }
    
    /**
     * Sends move to update this Phantom AI's game state Game state
     * and indicates it is this Phantom AI's turn to Move
     * @param move Move that is made by the Human Player on the current game-state
     */
    @Override
    public void persistMove(ChessMove move)
    {
        //super.persistMove(move);
        determineMove();
        
    }
    
    /**
     * Called after this Phantom AI picks a move
     * @param move Move that this phantom AI made
     */
    public void commitMove(ChessMove move)
    {
        super.commitMove(move);
    }
    
    public void determineMove()
    {
        ChessAI chessAI = new ChessAI(this,this.getColor(),this.level);
        chessAI.start();
    }
  
    static public List<ChessMove> getValidMoves(ChessBoardState boardState,PlayerColor playerColor)
    {
        List<ChessMove> validMoves = new ArrayList<>();
        List<ChessMove> moves;
        
        for (Location location : Location.allLocations())
        {
            ChessPiece piece = boardState.getPiece(location);
            if (piece != null && playerColor.equals(piece.getColor()))
            {
                moves = piece.getValidMoves(location, boardState);
                if (moves != null && !moves.isEmpty()) {
                    for (ChessMove m : moves) {
                        if (m instanceof MovePromotion)
                        {
                            ((MovePromotion) m).setNewPieceType(PieceType.QUEEN);
                            for (PieceType p : PieceType.values())
                            {
                                if (p == PieceType.QUEEN) continue;
                                MovePromotion altMove = ((MovePromotion)m).getCopy();
                                altMove.setNewPieceType(p);
                                moves.add(altMove);
                            }
                        }
                    }
                    validMoves.addAll(moves);
                }
            }
        }
        
        return validMoves;
    }
    public ChessBoardState getBoardState()
    {
        return ((ChessGame)game).getBoardState();
    }
}
