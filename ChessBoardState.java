package chess.board;

import chess.pieces.ChessPiece;
import chess.pieces.PieceKing;
import chess.moves.ChessMove;
import game.utility.Location;
import game.utility.Properties.PlayerColor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintains the state of the board for a game<br>
 * - Chess uses this to keep track of move history and king locations<br>
 *   for fast access to the King locations<br>
 * - Move history is necessary to validate castling, en-passant and king in check<br>
 * - a copy can be created to test Moves, as in testing if a King is in Check<br>
 * - copies of this board state (deep-copy) can also be made to evaluated<br>
 *   moves in an AI Player (i.e. alpha-beta pruning and tree-representation<br>
 * 
 * @author devang
 */
public class ChessBoardState {
    
    private Map<PlayerColor,PieceKing> kings;
    private Map<PieceKing,Location>    kingLocations;
    private ChessMove                  mostRecentMove;
    protected ChessBoard               board;
    protected Set<ChessPiece>          pieces;
    protected Map<Location,ChessPiece> pieceLocations;
    
    /**
     * constructor: <br>
     * after calling superclass, initializes the mapping to the king and location of the king
     */
    public ChessBoardState()
    {
        super();
        board          = new ChessBoard();
        pieces         = new HashSet<>();
        pieceLocations = new HashMap<>();
        kings         = new HashMap<>();
        kingLocations = new HashMap<>();
    }
    
    /**
     * sets a piece location on the board<br>
     * - this class inherits from the super-class to intervene and
     *   simultaneously make the mapping to the king location consistent and up-to-date
     * @param piece the piece to set on the board
     * @param location the location on the board, at which to set the piece
     */
    public void setPiece(ChessPiece piece, Location location)
    {
        if (piece instanceof PieceKing)
        {
            PlayerColor kingColor = ((PieceKing)piece).getColor();
            kings.put(kingColor,(PieceKing)piece);
            if (kingLocations.containsKey((PieceKing)piece))
                kingLocations.replace((PieceKing)piece,location);
            else
                kingLocations.put((PieceKing)piece,location);
        }
        pieces.add(piece);
        pieceLocations.put(location,piece);
    }
    
    /**
     * gets the piece at the location on the board
     * @param location location at which to get a reference to a piece
     * @return piece at the location, if not empty, null otherwise
     */
    public ChessPiece getPiece(Location location)
    {
        ChessPiece piece = pieceLocations.get(location);
        return piece;
    }
    
    /**
     * removes the piece at the location on the board
     * @param location location at which to remove the piece
     */
    public void removePiece(Location location)
    {
        ChessPiece piece = pieceLocations.remove(location);
        if (piece != null) pieces.remove(piece);
    }
    
    /**
     * determines whether the space at location on the board is empty
     * @param location location on the board at which to determine emptiness
     * @return true if the space on the board is empty, false otherwise
     */
    public boolean isEmpty(Location location)
    {
        if (pieceLocations.containsKey(location))
        {
            if (pieceLocations.get(location) != null)
            {
                return false;
            }
        }
        return true;
    }
        
    /**
     * deep-copy of this board state, for analyzing Moves offline
     * @return the copy of the board
     */
    protected ChessBoardState getCopy()
    {
        ChessBoardState newBoardState = new ChessBoardState();
        for (Location location : pieceLocations.keySet())
        {
            ChessPiece piece = pieceLocations.get(location);
            Location newLocation = Location.copy(location);
            ChessPiece newPiece = ChessPiece.copy(piece);
            newBoardState.setPiece(newPiece,newLocation);
        }
        return newBoardState;
    }
    /**
     * returns a new board state that is a deep-copy of the input argument
     * @param boardState board state of which to make a copy
     * @return newly-created deep-copied board state
     */
    public static ChessBoardState copy(ChessBoardState boardState)
    {
        return boardState.getCopy();
    }
    
    /**
     * returns a set of all the pieces on the board
     * @return set of all pieces on the board
     */
    public Set<ChessPiece> getPieces()
    {
        return pieces;
    }
    
    /**
     * Tests if the king (of the parameter player's color) is in Check
     * @param color the Player whose king is tested in check
     * @return True if the king is in Check, False otherwise
     */
    public boolean check(PlayerColor color)
    {
        PlayerColor playerColor   = color;
        
        PieceKing kingPiece   = kings.get(playerColor);
        Location kingLocation = kingLocations.get(kingPiece);

        return kingPiece.check(kingLocation,this);
    }
    
    /**
     * Gets the most recent move of this boardstate
     * @return ChessMove object representing most recent move
     */
    public ChessMove getMostRecentMove()
    {
        return mostRecentMove;
    }
    
    /**
     * Sets the most recent move of this boardstate
     * @param move object representing most recent move
     */
    public void setMostRecentMove(ChessMove move)
    {
        mostRecentMove = move;
    }
}
