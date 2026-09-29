package chess.moves;

import chess.pieces.ChessPiece;
import chess.board.ChessBoardState;
import game.utility.Location;
import java.io.Serializable;

/**
 * represents a king-castling move for a Chess board
 * @author devang
 */
public class MoveCastle extends ChessMove implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final Location kingFrom;
    public final Location kingTo;
    public final Location rookFrom;
    public final Location rookTo;

    /**
     * creates a representation of a castling move for a king
     * @param kingFrom the original location of the king
     * @param kingTo the destination location of the king after the castle
     * @param rookFrom the original location of the rook in this castling-move
     * @param rookTo the destination location of the rook after the castle
     */
    public MoveCastle(Location kingFrom, Location kingTo, Location rookFrom, Location rookTo)
    {
        this.kingFrom = kingFrom;
        this.kingTo   = kingTo;
        this.rookFrom = rookFrom;
        this.rookTo   = rookTo;
    }
    
    /**
     * gets the destination location for the king in this castling move
     * @return Location to which the king will move
     */
    public Location getToLocation()
    {
        return kingTo;
    }
    
    /**
     * gets the origin location for the king in this castling move
     * @return Location from which the king will move
     */
    public Location getFromLocation()
    {
        return kingFrom;
    }

    @Override
    public boolean isCapture()
    {
        return false;
    }
    
    /**
     * creates and returns a deep-copy of this castling
     * @return newly-created deep copy of this
     */
    @Override
    public MoveCastle getCopy()
    {
        Location    newKingFrom = Location.copy(kingFrom);
        Location    newKingTo   = Location.copy(kingTo);
        Location    newRookFrom = Location.copy(rookFrom);
        Location    newRookTo   = Location.copy(rookTo);
        
        return new MoveCastle(newKingFrom,newKingTo,newRookFrom,newRookTo);
    }
    
    /**
     * commits this castling move to the board state in the argument
     * @param boardState state of a board of a game against to which to apply this move
     */
    @Override
    public void commitMove(ChessBoardState boardState)
    {
        ChessPiece kingPiece = boardState.getPiece(kingFrom);
        ChessPiece rookPiece = boardState.getPiece(rookFrom);
        boardState.removePiece(kingFrom);
        boardState.removePiece(rookFrom);
        boardState.setPiece(kingPiece,kingTo);
        boardState.setPiece(rookPiece,rookTo);
        
        ((chess.pieces.PieceKing)kingPiece).hasCastled = true;
    }
    
    @Override 
    public ChessMove rotateMove()
    {
        Location rKingFrom = Location.rotate(kingFrom);
        Location rKingTo   = Location.rotate(kingTo);
        Location rRookFrom = Location.rotate(rookFrom);
        Location rRookTo   = Location.rotate(rookTo);
        
        return new MoveCastle(rKingFrom,rKingTo,rRookFrom,rRookTo);
    }
}

