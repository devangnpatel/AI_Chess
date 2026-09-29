package chess.moves;

import chess.board.ChessBoardState;
import chess.pieces.ChessPiece;
import game.utility.Location;
import java.io.Serializable;

/**
 * represents a move or capturing-move for a Chess board
 * @author devang
 */
public class MoveRegular extends ChessMove implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Location from;
    private final Location to;
    public boolean        isCapture = false;

    /**
     * creates a standard chess move, identified by the location from which the piece
     * moves and the destination of this Move
     * @param from the original location of the piece
     * @param to the destination location of the piece in this Move
     */
    public MoveRegular(Location from, Location to)
    {
        this.from = from;
        this.to   = to;
    }
    
    /**
     * gets the origin location for the piece of this Move
     * @return Location from which the piece will move
     */
    public Location getFromLocation()
    {
        return from;
    }
    
    /**
     * gets the destination location for the piece of this Move
     * @return Location to which the piece will move
     */
    public Location getToLocation()
    {
        return to;
    }

    @Override
    public boolean isCapture()
    {
        return isCapture;
            
    }    
    /**
     * creates and returns a deep-copy of this standard Move or Capturing-Move
     * @return newly-created deep copy of this
     */
    @Override
    public MoveRegular getCopy()
    {
        Location newFrom = Location.copy(from);
        Location newTo   = Location.copy(to);
        
        return new MoveRegular(newFrom,newTo);
    }
    
    /**
     * commits this Move to the board state in the argument
     * @param boardState state of a board of a game against to which to apply this move
     */
    @Override
    public void commitMove(ChessBoardState boardState)
    {
        ChessPiece piece = boardState.getPiece(from);
        
        boardState.removePiece(from);
        
        if (!boardState.isEmpty(to))
        {
            boardState.removePiece(to);
            isCapture = true;
        }
        
        boardState.setPiece(piece,to);
    }
        
    @Override
    public ChessMove rotateMove()
    {
        Location rFrom = Location.rotate(from);
        Location rTo   = Location.rotate(to);
        
        return new MoveRegular(rFrom,rTo);
    }
}

