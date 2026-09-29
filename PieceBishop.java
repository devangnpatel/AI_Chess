package chess.pieces;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import game.utility.Location;
import game.utility.Properties.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Represents a Bishop for Chess games
 * 
 * @author devang
 */
public class PieceBishop extends ChessPiece {
    
    private PieceBishop(PlayerColor pieceColor)
    {
        super(pieceColor);
    }
    
    /**
     * gets a list of valid moves for the piece at the given location on the given board state<br>
     * - the piece at this location will be this bishop<br>
     * @param location location for this bishop on the board for which to determine all valid moves
     * @param boardState state of the board to analyze for current valid moves
     * @return List of valid moves for this bishop at the given location on the given board state
     */
    @Override
    public List<ChessMove> getValidMoves(Location location, ChessBoardState boardState)
    {
        List<ChessMove> validMoves = new ArrayList<>();
        if (location == null) return validMoves;
        
        ChessPiece thisPiece = boardState.getPiece(location);
        if (!(thisPiece instanceof PieceBishop)) return validMoves;
        
        Location startLocation;
        Location nextLocation;

        List<BiFunction<Location,Integer,Location>> locationDirections;
        locationDirections = new ArrayList<>();
        locationDirections.add(Location::upLeftX);
        locationDirections.add(Location::upRightX);
        locationDirections.add(Location::downLeftX);
        locationDirections.add(Location::downRightX);

        startLocation = Location.copy(location);
        for (BiFunction<Location,Integer,Location> function : locationDirections)
        {
            int next = 1;
            nextLocation = nextLocation(startLocation,next,function);
            while ((nextLocation != null ) && boardState.isEmpty(nextLocation))
            {
                ChessMove newMove = validateMove(startLocation,nextLocation,(ChessBoardState)boardState);
                if (newMove != null)
                {
                    validMoves.add(newMove);
                }
                nextLocation = nextLocation(startLocation,next++,function);
            }
            if ((nextLocation != null) && !boardState.isEmpty(nextLocation))
            {
                ChessMove newMove = validateMove(startLocation,nextLocation,(ChessBoardState)boardState);
                if (newMove != null)
                {
                    validMoves.add(newMove);
                }
            }
        }
        
        return validMoves;
    }
        
    /**
     * Creates a new bishop from deep-copy of this
     * @return deep-copy of this bishop
     */
    @Override
    public ChessPiece createCopy()
    {
        PieceBishop newBishop = new PieceBishop(pieceColor);
        newBishop.setProperties(getProperties());
        newBishop.setNumMovesMade(getNumMovesMade());
        newBishop.setMostRecentMove(getMostRecentMove());
        return newBishop;
    }
    
    /**
     * Creates a new bishop with parameter color
     * @param pieceColor color for this bishop
     * @return a newly constructed bishop
     */
    public static ChessPiece create(PlayerColor pieceColor)
    {
        return new PieceBishop(pieceColor);
    }
}
