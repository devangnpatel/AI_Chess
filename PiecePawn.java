package chess.pieces;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.moves.MoveEnPassant;
import chess.moves.MovePromotion;
import chess.moves.MovePromotion.PieceType;
import chess.moves.MoveRegular;
import game.utility.Location;
import game.utility.Properties;
import game.utility.Properties.PlayerColor;
import static game.utility.Properties.Direction.UP;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn for Chess games
 * 
 * @author devang
 */
public class PiecePawn extends ChessPiece {
    
    private PiecePawn(PlayerColor pieceColor)
    {
        super(pieceColor);
    }
    
   /**
     * determines if this pawn can do en-passant
     * @param startLocation location of this pawn to evaluate en-passant
     * @param nextLocation location of the destination to evaluate en-passant
     * @param boardState state of the board to analyze for this move
     * @return Move object if the piece at startLocation can legally move to nextLocation
     */
     protected ChessMove validateEnPassantMove(Location startLocation, Location nextLocation, ChessBoardState boardState)
    {        
        PlayerColor playerColor = boardState.getPiece(startLocation).getColor();

        if (nextLocation != null)
        {
            ChessMove newMove = new MoveEnPassant(startLocation,nextLocation);
            ChessBoardState tempBoardState = ChessBoardState.copy(boardState);
            newMove.commitMove(tempBoardState);
            if (!tempBoardState.check(playerColor))
            {
                return newMove;
            }
        }
        
        return null;
    }
    
    /**
     * determines if this pawn at startLocation argument can legally move to nextLocation argument
     * @param startLocation location of this pawn to evaluate a move
     * @param nextLocation location of a destination of a move to validate
     * @param boardState state of the board to analyze for this move
     * @return Move object if this pawn at startLocation can legally move to nextLocation
     */
     @Override
    protected ChessMove validateMove(Location startLocation, Location nextLocation, ChessBoardState boardState)
    {        
        PlayerColor playerColor = boardState.getPiece(startLocation).getColor();

        if (nextLocation != null)
        {
            ChessMove newMove = null;
            if (Location.isEndRow(nextLocation))
            {
                newMove = new MovePromotion(startLocation,nextLocation,null);
            }
            else if (Math.abs(Location.getRow(startLocation) - Location.getRow(nextLocation)) == 2)
            {
                if (properties.getColorDirection(pieceColor) == UP)
                {    
                    if ((boardState.isEmpty(Location.up(startLocation))) &&
                        (boardState.isEmpty(nextLocation)))
                        newMove = new MoveRegular(startLocation,nextLocation);
                }
                else
                {
                    if ((boardState.isEmpty(Location.down(startLocation))) &&
                        (boardState.isEmpty(nextLocation)))
                        newMove = new MoveRegular(startLocation,nextLocation);
                }
            } 
            else
            {
                newMove = new MoveRegular(startLocation,nextLocation);
            }
                
            ChessBoardState tempBoardState = ChessBoardState.copy(boardState);
            
            if (newMove != null)
            {
                newMove.commitMove(tempBoardState);
            }
            
            if (!tempBoardState.check(playerColor))
            {
                return newMove;
            }
        }
        
        return null;
    }
    
    /**
     * gets a list of valid moves for the piece at the given location on the given board state<br>
     * - the piece at this location will be this pawn<br>
     * - calls to validate en-passant<br>
     * - calls to validate two-space move<br>
     * - calls to validate a capture<br>
     * - calls to trigger event in case of pawn-promotion<br>
     * @param location location for this pawn on the board for which to determine all valid moves
     * @param boardState state of the board to analyze for current valid moves
     * @return List of valid moves for this pawn at the given location on the given board state
     */
    @Override
    public List<ChessMove> getValidMoves(Location location, ChessBoardState boardState)
    {
        List<ChessMove> validMoves = new ArrayList<>();
        if (location == null) return validMoves;
        ChessPiece thisPiece = boardState.getPiece(location);
        if (!(thisPiece instanceof PiecePawn)) return validMoves;
        PlayerColor playerColor = thisPiece.getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location startLocation;
        ChessMove newMove;
        startLocation = Location.copy(location);
        List<Location> moveLocations = new ArrayList<>();
        List<Location> captureLocations = new ArrayList<>();
        
        if (properties.getColorDirection(pieceColor) == UP)
        {
            moveLocations.add(Location.up(startLocation));
            if ((getMostRecentMove() == null) || (getNumMovesMade() == 0))
                moveLocations.add(Location.up2(startLocation));
            captureLocations.add(Location.left(Location.up(startLocation)));
            captureLocations.add(Location.right(Location.up(startLocation)));
        }
        else // Properties.getColorDirection(pieceColor) == DOWN
        {
            moveLocations.add(Location.down(startLocation));
            if ((getMostRecentMove() == null) || (getNumMovesMade() == 0))
                moveLocations.add(Location.down2(startLocation));
            captureLocations.add(Location.left(Location.down(startLocation)));
            captureLocations.add(Location.right(Location.down(startLocation)));
        }

        //////////////////////////
        // regular moves
        for (Location nextLocation : moveLocations)
        {
            startLocation = Location.copy(location);
            if ((nextLocation != null) && boardState.isEmpty(nextLocation))
            {
                newMove = validateMove(startLocation,nextLocation,(ChessBoardState)boardState);
                if (newMove != null && newMove instanceof MovePromotion)
                {
                    for (PieceType p : PieceType.values())
                    {
                        MovePromotion newMovePromotion = ((MovePromotion)newMove).getCopy();
                        newMovePromotion.setNewPieceType(p);
                        validMoves.add(newMovePromotion);
                    }
                }
                else if (newMove != null) validMoves.add(newMove);
            }
        }
        
        //////////////////////////
        // capture moves
        for (Location nextLocation : captureLocations)
        {
            startLocation = Location.copy(location);
            if ((nextLocation != null) && !boardState.isEmpty(nextLocation))
            {
                if (boardState.getPiece(nextLocation).getColor() == opponentColor)
                {
                    newMove = validateMove(startLocation,nextLocation,(ChessBoardState)boardState);
                    if (newMove != null && newMove instanceof MovePromotion)
                    {
                        for (PieceType p : PieceType.values())
                        {
                            MovePromotion newMovePromotion = ((MovePromotion)newMove).getCopy();
                            newMovePromotion.setNewPieceType(p);
                            validMoves.add(newMovePromotion);
                        }
                    }
                    else if (newMove != null) validMoves.add(newMove);
                }
            }
        }
        
        //////////////////////////
        // en-passant
        Location currentLocation = Location.copy(location);
        Location leftLocation = Location.left(currentLocation);
        Location rightLocation = Location.right(currentLocation);
        PiecePawn pawnToLeft = null;
        PiecePawn pawnToRight = null;
        if ((leftLocation != null) && !boardState.isEmpty(leftLocation))
        {
            ChessPiece piece = boardState.getPiece(leftLocation);
            if ((piece.getColor() == opponentColor) && (piece instanceof PiecePawn))
            {
                pawnToLeft = (PiecePawn)piece;
            }
        }
        if ((rightLocation != null) && !boardState.isEmpty(rightLocation))
        {
            ChessPiece piece = boardState.getPiece(rightLocation);
            if ((piece.getColor() == opponentColor) && (piece instanceof PiecePawn))
            {
                pawnToRight = (PiecePawn)piece;
            }
        }        
        
        if (properties.getColorDirection(pieceColor) == UP)
        {
            if (pawnToLeft != null)
            {
                ChessMove mostRecentPawnMove = pawnToLeft.getMostRecentMove();
                ChessMove mostRecentGameMove = boardState.getMostRecentMove();
                
                if ((mostRecentPawnMove != null) && (mostRecentGameMove != null) && (mostRecentPawnMove == mostRecentGameMove))
                {
                    Location toLocation = mostRecentPawnMove.getToLocation();
                    Location fromLocation = mostRecentPawnMove.getFromLocation();
                    int toLocationIdx = Location.getRow(toLocation);
                    int fromLocationIdx = Location.getRow(fromLocation);

                    if ((pawnToLeft.getNumMovesMade() == 1) && ((toLocationIdx - fromLocationIdx) == 2))
                    {
                        Location newFromLocation = Location.copy(location);
                        Location newToLocation = Location.upLeft(newFromLocation);
                        newMove = validateEnPassantMove(newFromLocation,newToLocation,boardState);
                        if (newMove != null) validMoves.add(newMove);
                    }
                }
            }
            
            if (pawnToRight != null)
            {
                ChessMove mostRecentPawnMove = pawnToRight.getMostRecentMove();
                ChessMove mostRecentGameMove = boardState.getMostRecentMove();
                
                if ((mostRecentPawnMove != null) && (mostRecentGameMove != null) && (mostRecentPawnMove == mostRecentGameMove))
                {
                    Location toLocation = mostRecentPawnMove.getToLocation();
                    Location fromLocation = mostRecentPawnMove.getFromLocation();
                    int toLocationIdx = Location.getRow(toLocation);
                    int fromLocationIdx = Location.getRow(fromLocation);

                    if ((pawnToRight.getNumMovesMade() == 1) && ((toLocationIdx - fromLocationIdx) == 2))
                    {
                        Location newFromLocation = Location.copy(location);
                        Location newToLocation = Location.upRight(newFromLocation);
                        newMove = validateEnPassantMove(newFromLocation,newToLocation,boardState);
                        if (newMove != null) validMoves.add(newMove);
                    }
                }
            }
        }
        else // Properties.getColorDirection(pieceColor) == DOWN
        {
            if (pawnToLeft != null)
            {
                ChessMove mostRecentPawnMove = pawnToLeft.getMostRecentMove();
                ChessMove mostRecentGameMove = boardState.getMostRecentMove();
                
                if ((mostRecentPawnMove != null) && (mostRecentGameMove != null) && (mostRecentPawnMove == mostRecentGameMove))
                {
                    Location toLocation = mostRecentPawnMove.getToLocation();
                    Location fromLocation = mostRecentPawnMove.getFromLocation();
                    int toLocationIdx = Location.getRow(toLocation);
                    int fromLocationIdx = Location.getRow(fromLocation);

                    if ((pawnToLeft.getNumMovesMade() == 1) && ((fromLocationIdx - toLocationIdx) == 2))
                    {
                        Location newFromLocation = Location.copy(location);
                        Location newToLocation = Location.downLeft(newFromLocation);
                        newMove = validateEnPassantMove(newFromLocation,newToLocation,boardState);
                        if (newMove != null) validMoves.add(newMove);
                    }
                }
            }
            
            if (pawnToRight != null)
            {
                ChessMove mostRecentPawnMove = pawnToRight.getMostRecentMove();
                ChessMove mostRecentGameMove = boardState.getMostRecentMove();
                
                if ((mostRecentPawnMove != null) && (mostRecentGameMove != null) && (mostRecentPawnMove == mostRecentGameMove))
                {
                    Location toLocation = mostRecentPawnMove.getToLocation();
                    Location fromLocation = mostRecentPawnMove.getFromLocation();
                    int toLocationIdx = Location.getRow(toLocation);
                    int fromLocationIdx = Location.getRow(fromLocation);

                    if ((pawnToRight.getNumMovesMade() == 1) && ((fromLocationIdx - toLocationIdx) == 2))
                    {
                        Location newFromLocation = Location.copy(location);
                        Location newToLocation = Location.downRight(newFromLocation);
                        newMove = validateEnPassantMove(newFromLocation,newToLocation,boardState);
                        if (newMove != null) validMoves.add(newMove);
                    }
                }
            } 
        }

        return validMoves;
    }
        
    /**
     * Creates a new pawn from deep-copy of this
     * @return deep-copy of this pawn
     */
    @Override
    public ChessPiece createCopy()
    {
        PiecePawn newPawn = new PiecePawn(pieceColor);
        newPawn.setProperties(getProperties());
        newPawn.setNumMovesMade(getNumMovesMade());
        newPawn.setMostRecentMove(getMostRecentMove());
        return newPawn;
    }
    
    /**
     * Creates a new pawn with parameter color
     * @param pieceColor color for this pawn
     * @return a newly constructed pawn
     */
    public static ChessPiece create(PlayerColor pieceColor)
    {
        return new PiecePawn(pieceColor);
    }
}
