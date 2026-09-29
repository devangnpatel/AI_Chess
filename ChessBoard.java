package chess.board;

import game.utility.Location;
import game.utility.Properties;

/**
 * not really used: only holds a 2d-array of spaces where each space holds a row,col-indexed location
 * @author devang
 */
public class ChessBoard {
    private ChessBoardSpace[][] spaces;
    
    public ChessBoard()
    {
        spaces = new ChessBoardSpace[Properties.NUM_COLS][Properties.NUM_ROWS];
        for (int col = 0; col < Properties.NUM_COLS; col++)
        {
            for (int row = 0; row < Properties.NUM_ROWS; row++)
            {
                Location location = Location.of(col,row);
                spaces[col][row] = new ChessBoardSpace(location);
            }
        }
    }
    
}
