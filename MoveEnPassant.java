package chess.moves;

import chess.board.ChessBoardState;
import chess.pieces.ChessPiece;
import game.utility.Location;
import java.io.Serializable;

/**
 * represents a pawn's en-passant for a Chess board
 * @author devang
 */
public class MoveEnPassant extends ChessMove implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Location from;
    private final Location to;

    /**
     * creates a representation of a pawn's en-passant move
     * @param from the original location of the pawn
     * @param to the destination location of the pawn
     */
    public MoveEnPassant(Location from, Location to)
    {
        this.from = from;
        this.to   = to;
    }
    
    /**
     * gets the origin location for the pawn in this en-passant Move
     * @return Location from which the pawn will move
     */
    public Location getFromLocation()
    {
        return from;
    }
    
    /**
     * gets the destination location for the pawn in this en-passant Move
     * @return Location to which the pawn will move
     */
    public Location getToLocation()
    {
        return to;
    }

    @Override
    public boolean isCapture()
    {
        return true;
    }    
    /**
     * creates and returns a deep-copy of this en-passant
     * @return newly-created deep copy of this
     */
    @Override
    public MoveEnPassant getCopy()
    {
        Location newFrom = Location.copy(from);
        Location newTo   = Location.copy(to);
        
        return new MoveEnPassant(newFrom,newTo);
    }
    
    /**
     * commits this en-passant Move to the board state in the argument
     * @param boardState state of a board of a game against to which to apply this move
     */
    @Override
    public void commitMove(ChessBoardState boardState)
    {
        // int startFile = Location.getCol(from);
        int startRank = Location.getRow(from);
        int endFile   = Location.getCol(to);
        // int endRank   = Location.getRow(to);
        
        Location locationCapture = Location.of(endFile,startRank);
        ChessPiece piece = boardState.getPiece(from);
        boardState.removePiece(from);
        boardState.removePiece(locationCapture);
        boardState.setPiece(piece,to);
    }
    
    @Override
    public ChessMove rotateMove()
    {
        Location rFrom = Location.rotate(from);
        Location rTo   = Location.rotate(to);
        
        return new MoveEnPassant(rFrom,rTo);
    }
}

