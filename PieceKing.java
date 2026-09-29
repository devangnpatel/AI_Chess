package chess.pieces;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.moves.MoveCastle;
import chess.moves.MoveRegular;
import game.utility.Location;
import game.utility.Properties.PlayerColor;
import static game.utility.Properties.NUM_COLS;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King for Chess games
 * 
 * @author devang
 */
public class PieceKing extends ChessPiece {
    public boolean hasCastled = false;
    
    private PieceKing(PlayerColor pieceColor)
    {
        super(pieceColor);
    }
        
    /**
     * gets a list of valid moves for the piece at the given location on the given board state<br>
     * - the piece at this location will be this king<br>
     * @param location location for this king on the board for which to determine all valid moves
     * @param boardState state of the board to analyze for current valid moves
     * @return List of valid moves for this king at the given location on the given board state
     */
    @Override
    public List<ChessMove> getValidMoves(Location location, ChessBoardState boardState)
    {
        List<ChessMove> validMoves = new ArrayList<>();
        if (location == null) return validMoves;
        ChessPiece thisPiece = boardState.getPiece(location);
        if (!(thisPiece instanceof PieceKing)) return validMoves;
        
        Location startLocation = Location.copy(location);
        Location[] moveLocations = { Location.left(startLocation),
                                     Location.right(startLocation),
                                     Location.up(startLocation),
                                     Location.down(startLocation),
                                     Location.upLeft(startLocation),
                                     Location.upRight(startLocation),
                                     Location.downLeft(startLocation),
                                     Location.downRight(startLocation)};
        
        for (Location nextLocation : moveLocations)
        {
            startLocation = Location.copy(location);
            ChessMove newMove = validateMove(startLocation,nextLocation,(ChessBoardState)boardState);
            if (newMove != null)
                validMoves.add(newMove);
        }
        
        Location kingLocation;
        Location newLocation;
        Location rookLocation;
        int rookFile;
        int kingRank;
        ChessPiece rook;
        
        // castle left
        kingLocation = Location.copy(location);
        newLocation = Location.left2(location);
        kingRank = Location.getRow(location);
        rookFile = 0;
        rookLocation = Location.at(rookFile,kingRank);
        rook = null;
        if (!boardState.isEmpty(rookLocation))
            rook = (ChessPiece)boardState.getPiece(rookLocation);
        if ((rook != null) && (rook instanceof PieceRook))
        {
            if ((getNumMovesMade() == 0) && (rook.getNumMovesMade() == 0))
            {
                MoveCastle moveCastle = validateLeftCastle(kingLocation,newLocation,rookLocation,boardState);
                if (moveCastle != null) validMoves.add(moveCastle);
            }
        }
        
        // castle right
        kingLocation = Location.copy(location);
        newLocation = Location.right2(location);
        kingRank = Location.getRow(location);
        rookFile = NUM_COLS - 1;
        rookLocation = Location.at(rookFile,kingRank);
        rook = null;
        if (!boardState.isEmpty(rookLocation))
            rook = (ChessPiece)boardState.getPiece(rookLocation);
        if ((rook != null) && (rook instanceof PieceRook))
        {
            if ((getNumMovesMade() == 0) && (rook.getNumMovesMade() == 0))
            {
                MoveCastle moveCastle = validateRightCastle(kingLocation,newLocation,rookLocation,boardState);
                if (moveCastle != null) validMoves.add(moveCastle);
            }
        }

        return validMoves;
    }
    
    /**
     * determines if this king at startLocation argument can legally castle to the left<br>
     * - validates if the king is in check, or would move through check in castling<br>
     * - validates if the king has already made a move<br>
     * - validates if the left-side rook has already made a move<br>
     * @param kingLocation present location of this king
     * @param newLocation potential castling location for this king
     * @param rookLocation present location of the left-side rook
     * @param boardState state of the board to analyze for this castling
     * @return Move object if this king can legally castle to the left
     */
    protected MoveCastle validateLeftCastle(Location kingLocation, Location newLocation, Location rookLocation, ChessBoardState boardState)
    {   
        if ((kingLocation == null) || (newLocation == null) || (rookLocation == null))
            return null;
        
        PlayerColor playerColor = boardState.getPiece(kingLocation).getColor();
        int kingFile = Location.getCol(kingLocation);
        int rookFile = Location.getCol(rookLocation);
        Location[] leftLocations = new Location[kingFile-rookFile+1];
        for (int i = 0; i <= kingFile-rookFile; i++)
        {
            leftLocations[i] = Location.leftX(kingLocation,i);
            if (leftLocations[i] == null) return null;
        }
        
        for (int i = 1; i < kingFile-rookFile; i++)
        {
            if (!boardState.isEmpty(leftLocations[i])) return null;
        }
        
        ChessMove newMove;
        ChessBoardState tempBoardState;
        
        tempBoardState = ChessBoardState.copy(boardState);
        if (tempBoardState.check(playerColor))
            return null;
        
        newMove = new MoveRegular(kingLocation,leftLocations[1]);
        tempBoardState = ChessBoardState.copy(boardState);
        newMove.commitMove(tempBoardState);
        if (tempBoardState.check(playerColor))
            return null;
        
        newMove = new MoveRegular(kingLocation,leftLocations[2]);
        tempBoardState = ChessBoardState.copy(boardState);
        newMove.commitMove(tempBoardState);
        if (tempBoardState.check(playerColor))
            return null;
        
        Location newRookLocation = Location.left(kingLocation);
        MoveCastle moveCastle = new MoveCastle(kingLocation,newLocation,rookLocation,newRookLocation);
        
        return moveCastle;
    }
    
    /**
     * determines if this king at startLocation argument can legally castle to the right<br>
     * - validates if the king is in check, or would move through check in castling<br>
     * - validates if the king has already made a move<br>
     * - validates if the right-side rook has already made a move<br>
     * @param kingLocation present location of this king
     * @param newLocation potential castling location for this king
     * @param rookLocation present location of the right-side rook
     * @param boardState state of the board to analyze for this castling
     * @return Move object if this king can legally castle to the right
     */
     protected MoveCastle validateRightCastle(Location kingLocation, Location newLocation, Location rookLocation, ChessBoardState boardState)
    {   
        if ((kingLocation == null) || (newLocation == null) || (rookLocation == null))
            return null;
        
        PlayerColor playerColor = boardState.getPiece(kingLocation).getColor();
        int kingFile = Location.getCol(kingLocation);
        int rookFile = Location.getCol(rookLocation);
        Location[] rightLocations = new Location[rookFile-kingFile+1];
        for (int i = 0; i <= rookFile-kingFile; i++)
        {
            rightLocations[i] = Location.rightX(kingLocation,i);
            if (rightLocations[i] == null) return null;
        }
        
        for (int i = 1; i < rookFile-kingFile; i++)
        {
            if (!boardState.isEmpty(rightLocations[i])) return null;
        }
        
        ChessMove newMove;
        ChessBoardState tempBoardState;

        tempBoardState = ChessBoardState.copy(boardState);
        if (tempBoardState.check(playerColor))
            return null;
       
        newMove = new MoveRegular(kingLocation,rightLocations[1]);
        tempBoardState = ChessBoardState.copy(boardState);
        newMove.commitMove(tempBoardState);
        if (tempBoardState.check(playerColor))
            return null;
        
        newMove = new MoveRegular(kingLocation,rightLocations[2]);
        tempBoardState = ChessBoardState.copy(boardState);
        newMove.commitMove(tempBoardState);
        if (tempBoardState.check(playerColor))
            return null;
        
        Location newRookLocation = Location.right(kingLocation);
        MoveCastle moveCastle = new MoveCastle(kingLocation,newLocation,rookLocation,newRookLocation);
        
        return moveCastle;
    }

    /**
     * Creates a new king from deep-copy of this
     * @return deep-copy of this king
     */
    @Override
    public ChessPiece createCopy()
    {
        PieceKing newKing = new PieceKing(pieceColor);
        newKing.setProperties(getProperties());
        newKing.setNumMovesMade(getNumMovesMade());
        newKing.setMostRecentMove(getMostRecentMove());
        return newKing;
    }
    
    /**
     * Creates a new king with parameter color
     * @param pieceColor color for this king
     * @return a newly constructed king
     */
    public static ChessPiece create(PlayerColor pieceColor)
    {
        return new PieceKing(pieceColor);
    }
    
    /**
     * typical equals override for use in hash-map
     * @param obj piece to which to compare this
     * @return true if this equals the parameter object (shallow-basis), false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof PieceKing)) return false;
        PieceKing piece = (PieceKing)obj;
        return (piece.getColor() == getColor());
    }

    /**
     * hash-code value for use in hash-map
     * @return returns hash-code (determined to be unique for every piece)
     */
    @Override
    public int hashCode()
    {
        int result = 7;
        result = 17 * result + getColor().ordinal();
        return result;
    }
}
