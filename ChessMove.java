package chess.moves;

import chess.board.ChessBoardState;
import game.utility.Location;
import java.io.Serializable;

/**
 * represents a move for a Chess board
 * @author devang
 */
public abstract class ChessMove implements Serializable {
	private static final long serialVersionUID = 1L;
	public abstract ChessMove getCopy();
    public abstract void      commitMove(ChessBoardState boardState);
    public abstract Location  getToLocation();
    public abstract Location  getFromLocation();
    public abstract ChessMove rotateMove();
    public abstract boolean   isCapture();
}
