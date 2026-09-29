package chess.moves;

import chess.board.ChessBoardState;
import chess.pieces.ChessPiece;
import chess.pieces.PieceBishop;
import chess.pieces.PieceKnight;
import chess.pieces.PieceQueen;
import chess.pieces.PieceRook;
import game.utility.Location;
import game.utility.Properties.PlayerColor;
import java.io.Serializable;

/**
 * represents a pawn's move that would result in a pawn-promotion for a Chess board
 * @author devang
 */
public class MovePromotion extends ChessMove implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Location from;
    private final Location to;
    
    private PieceType newPieceType;
    
    public enum PieceType {
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK
    }

    /**
     * creates a representation of a pawn-promoting move
     * @param from the original location of the pawn
     * @param to the destination location of the pawn
     * @param newPieceType the upgraded piece to which the pawn will upgrade
     */
    public MovePromotion(Location from, Location to, PieceType newPieceType)
    {
        this.from = from;
        this.to   = to;
        
        this.newPieceType = newPieceType;
    }
    
    /**
     * sets the pawn to which the pawn will upgrade
     * @param newPieceType the upgraded piece to which the pawn will upgrade
     */
    public void setNewPieceType(PieceType newPieceType)
    {
        this.newPieceType = newPieceType;
    }
    
    /**
     * gets the origin location for the pawn in a pawn-promoting Move
     * @return Location from which the pawn will move
     */
    public Location getFromLocation()
    {
        return from;
    }
    
    /**
     * gets the destination location for the pawn in a pawn-promoting Move
     * @return Location to which the pawn will move
     */
    public Location getToLocation()
    {
        return to;
    }

    @Override
    public boolean isCapture()
    {
        return false;
    }    
    /**
     * creates and returns a deep-copy of this pawn-promoting Move
     * @return newly-created deep copy of this
     */
    @Override
    public MovePromotion getCopy()
    {
        Location    newFrom = Location.copy(from);
        Location    newTo   = Location.copy(to);
        PieceType newPiece  = newPieceType;
        
        return new MovePromotion(newFrom,newTo,newPiece);
    }
    
    /**
     * commits this pawn-promoting Move to the board state in the argument
     * @param boardState state of a board of a game against to which to apply this move
     */
    @Override
    public void commitMove(ChessBoardState boardState)
    {
        ChessPiece oldPiece   = boardState.getPiece(from);
        ChessPiece newPiece   = null;
        PlayerColor pieceColor = oldPiece.getColor();
        if (newPieceType != null)
        {
            switch (newPieceType)
            {
                case QUEEN:
                    newPiece = PieceQueen.create(pieceColor);
                    break;
                case BISHOP:
                    newPiece = PieceBishop.create(pieceColor);
                    break;
                case KNIGHT:
                    newPiece = PieceKnight.create(pieceColor);
                    break;
                case ROOK:
                    newPiece = PieceRook.create(pieceColor);
                    break;
                default:
                    newPiece = PieceQueen.create(pieceColor);
                    break;
            }
        }
        
        if (newPiece != null)
            ((ChessPiece)newPiece).setProperties(((ChessPiece)oldPiece).getProperties());
        
        if (!boardState.isEmpty(to))
            boardState.removePiece(to);
        
        boardState.removePiece(from);
        
        if (newPiece != null)
            boardState.setPiece(newPiece,to);
        else
            boardState.setPiece(oldPiece,to);
    }
        
    @Override
    public ChessMove rotateMove()
    {
        Location rFrom = Location.rotate(from);
        Location rTo   = Location.rotate(to);
        
        return new MovePromotion(rFrom,rTo,newPieceType);
    }
}

