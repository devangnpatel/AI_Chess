package chess.pieces;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import game.utility.Location;
import game.utility.Properties.PlayerColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Represents a Rook for Chess games
 * 
 * @author devang
 */
public class PieceRook extends ChessPiece {
    
    private PieceRook(PlayerColor pieceColor)
    {
        super(pieceColor);
    }
          
    /**
     * gets a list of valid moves for the piece at the given location on the given board state<br>
     * - the piece at this location will be this rook<br>
     * @param location location for this rook on the board for which to determine all valid moves
     * @param boardState state of the board to analyze for current valid moves
     * @return List of valid moves for this rook at the given location on the given board state
     */
    @Override
    public List<ChessMove> getValidMoves(Location location, ChessBoardState boardState)
    {
        List<ChessMove> validMoves = new ArrayList<>();
        if (location == null) return validMoves;
        ChessPiece thisPiece = boardState.getPiece(location);
        if (!(thisPiece instanceof PieceRook)) return validMoves;
        
        Location startLocation;
        Location nextLocation;

        List<BiFunction<Location,Integer,Location>> locationDirections;
        locationDirections = new ArrayList<>();
        locationDirections.add(Location::leftX);
        locationDirections.add(Location::rightX);
        locationDirections.add(Location::upX);
        locationDirections.add(Location::downX);

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
     * Creates a new rook from deep-copy of this
     * @return deep-copy of this rook
     */
    @Override
    public ChessPiece createCopy()
    {
        PieceRook newRook = new PieceRook(pieceColor);
        newRook.setProperties(getProperties());
        newRook.setNumMovesMade(getNumMovesMade());
        newRook.setMostRecentMove(getMostRecentMove());
        return newRook;
    }
    
    /**
     * Creates a new rook with parameter color
     * @param pieceColor color for this rook
     * @return a newly constructed rook
     */
    public static ChessPiece create(PlayerColor pieceColor)
    {
        return new PieceRook(pieceColor);
    }
}
