package chess.pieces;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.moves.MoveRegular;
import game.utility.Location;
import game.utility.Properties;
import game.utility.Properties.PlayerColor;
import static game.utility.Properties.Direction.UP;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Abstract class for represents a Chess Piece, and their possible moves
 * 
 * @author devang
 */
public abstract class ChessPiece {    
    public abstract List<ChessMove> getValidMoves(Location location, ChessBoardState boardState);
    public abstract ChessPiece createCopy();
    
    private   ChessMove       mostRecentMove;
    protected Properties properties;
    private   int             numMovesMade;
    protected final PlayerColor pieceColor;

    protected Location nextLocation(Location start, int i, BiFunction<Location,Integer,Location> locationMethod) {
        Location newLocation = locationMethod.apply(start, i);
        return newLocation;
    }
    
    /**
     * Creates a new chess piece with the color in the argument
     * @param pieceColor color to set for this new piece
     */
    public ChessPiece(PlayerColor pieceColor)
    {
        this.pieceColor = pieceColor;
        setMostRecentMove(null);
        setNumMovesMade(0);
    }
    
    /**
     * mainly used to access the directions of pawns
     * @param properties Properties (directions,dimensions,colors) for this chess game
     */
    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }
    /**
     * gets the properties utility for this pieces
     * @return Properties (directions,dimensions,colors) for this chess game
     */
    public Properties getProperties()
    {
        return properties;
    }
    
    /**
     * used to keep track of the number of moves this piece has made:<br>
     * - used in en-passant<br>
     * - used in castling [i.e. to check if rook or king has already made a move]<br>
     * - this will be deprecated after implementing algebraic-chess-notation game recording
     * @param numMoves number of moves this piece has made
     */
    public final void setNumMovesMade(int numMoves)
    {
        numMovesMade = numMoves;
    }
    
    /**
     * used to keep track of the number of moves this piece has made:<br>
     * - used in en-passant<br>
     * - used in castling [i.e. to check if rook or king has already made a move]<br>
     * - this will be deprecated after implementing algebraic-chess-notation game recording
     * @return number of moves this piece has made
     */
    public int getNumMovesMade()
    {
        return numMovesMade;
    }
    
    /**
     * used to keep track of the most recent move made<br>
     * - used in en-passant<br>
     * - this will be deprecated after implementing algebraic-chess-notation game recording
     * @param move the most recent move made
     */
    public final void setMostRecentMove(ChessMove move)
    {
        mostRecentMove = move;
    }
    
    /**
     * used to keep track of the most recent move made<br>
     * - used in en-passant<br>
     * - this will be deprecated after implementing algebraic-chess-notation game recording
     * @return the most recent move made
     */
    public ChessMove getMostRecentMove()
    {
        return mostRecentMove;
    }
    
    /**
     * gets a list of valid moves for the piece at the given location on the given board state
     * @param location location to a piece on the board for which to determine all valid moves
     * @param boardState state of the board to analyze for current valid moves
     * @return List of valid moves for the piece at the given location on the given board state
     */
    //public List<ChessMove> getValidMoves(Location location, ChessBoardState boardState)
    //{
    //    return getValidMoves(location,boardState);
    //}
    
    /**
     * determines if a move for the piece at startLocation argument can legally move to nextLocation argument
     * @param startLocation location of a piece to evaluate a move
     * @param nextLocation location of a destination of a move to validate
     * @param boardState state of the board to analyze for this move
     * @return Move object if the piece at startLocation can legally move to nextLocation
     */
    protected ChessMove validateMove(Location startLocation, Location nextLocation, ChessBoardState boardState)
    {        
        PlayerColor playerColor = boardState.getPiece(startLocation).getColor();
        
        if ((startLocation == null) || (nextLocation == null)) return null;
        if (!boardState.isEmpty(nextLocation))
        {
            ChessPiece piece = boardState.getPiece(nextLocation);
            if (piece.getColor() == playerColor)
                return null;
        }
        
        ChessMove newMove = new MoveRegular(startLocation,nextLocation);
        ChessBoardState tempBoardState = ChessBoardState.copy(boardState);
        newMove.commitMove(tempBoardState);
        if (!tempBoardState.check(playerColor))
            return newMove;
        
        return null;
    }
    
    /**
     * checks the king for attack from pawns
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from a pawn
     */
    public boolean checkFromPawns(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        // Direction playerDirection = properties.getColorDirection(playerColor);
        // Direction opponentDirection = properties.getColorDirection(opponentColor);
        
        List<Location> attackLocations = new ArrayList<>();
        if (properties.getColorDirection(pieceColor) == UP)
        {
            attackLocations.add(Location.upLeft(location));
            attackLocations.add(Location.upRight(location));
        }
        else // Properties.getColorDirection(pieceColor) == DOWN
        {
            attackLocations.add(Location.downLeft(location));
            attackLocations.add(Location.downRight(location));
        }
        
        for (Location attackLocation : attackLocations)
        {
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PiecePawn))
                {
                    return true;
                }
            }
        }
        
        return false;
        
    }
    /**
     * determines if the piece is protected by its own pawns
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for protection
     * @return true if the piece at the location argument is protected by a pawn
     */
    public boolean isProtectedByPawns(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        // PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        // Direction playerDirection = properties.getColorDirection(playerColor);
        // Direction opponentDirection = properties.getColorDirection(opponentColor);
        
        List<Location> protectionLocations = new ArrayList<>();
        if (properties.getColorDirection(pieceColor) == UP)
        {
            protectionLocations.add(Location.downLeft(location));
            protectionLocations.add(Location.downRight(location));
        }
        else // Properties.getColorDirection(pieceColor) == DOWN
        {
            protectionLocations.add(Location.upLeft(location));
            protectionLocations.add(Location.upRight(location));
        }
        
        for (Location protectionLocation : protectionLocations)
        {
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PiecePawn))
                {
                    return true;
                }
            }
        }
        
        return false;
        
    }
    
    /**
     * checks the king for attack from bishops
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from a bishop
     */
    public boolean checkFromBishops(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location startLocation;
        Location attackLocation;

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
            attackLocation = nextLocation(startLocation,next,function);
            while ((attackLocation != null ) && boardState.isEmpty(attackLocation))
            {
                next++;
                attackLocation = nextLocation(startLocation,next,function);
            }
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PieceBishop))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * determines if the piece is protected by its own bishops
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze piece for protection
     * @return true if the piece at the location argument is protected by its bishop
     */
    public boolean isProtectedByBishops(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        
        Location startLocation;
        Location protectionLocation;

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
            protectionLocation = nextLocation(startLocation,next,function);
            while ((protectionLocation != null ) && boardState.isEmpty(protectionLocation))
            {
                next++;
                protectionLocation = nextLocation(startLocation,next,function);
            }
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PieceBishop))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * checks the king for attack from knights
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from a knight
     */
    public boolean checkFromKnights(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location[] attackLocations = { Location.left(Location.up2(location)),
                                       Location.right(Location.up2(location)),
                                       Location.left(Location.down2(location)),
                                       Location.right(Location.down2(location)),
                                       Location.up(Location.left2(location)),
                                       Location.down(Location.left2(location)),
                                       Location.up(Location.right2(location)),
                                       Location.down(Location.right2(location))};
        
        for (Location attackLocation : attackLocations)
        {
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PieceKnight))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * determines if the piece at location is protected by knights
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for piece protection
     * @return true if the piece at the location argument is protected by a knight
     */
    public boolean isProtectedByKnights(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        
        Location[] protectionLocations = { Location.left(Location.up2(location)),
                                       Location.right(Location.up2(location)),
                                       Location.left(Location.down2(location)),
                                       Location.right(Location.down2(location)),
                                       Location.up(Location.left2(location)),
                                       Location.down(Location.left2(location)),
                                       Location.up(Location.right2(location)),
                                       Location.down(Location.right2(location))};
        
        for (Location protectionLocation : protectionLocations)
        {
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PieceKnight))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
        
    /**
     * checks the king for attack from rooks
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from a rook
     */
    public boolean checkFromRooks(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location startLocation;
        Location attackLocation;

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
            attackLocation = nextLocation(startLocation,next,function);
            while ((attackLocation != null ) && boardState.isEmpty(attackLocation))
            {
                next++;
                attackLocation = nextLocation(startLocation,next,function);
            }
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PieceRook))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
       
    /**
     * determines if the piece is protected by rooks
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for piece protection
     * @return true if the piece at the location argument is protected by a rook
     */
    public boolean isProtectedByRooks(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        
        Location startLocation;
        Location protectionLocation;

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
            protectionLocation = nextLocation(startLocation,next,function);
            while ((protectionLocation != null ) && boardState.isEmpty(protectionLocation))
            {
                next++;
                protectionLocation = nextLocation(startLocation,next,function);
            }
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PieceRook))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * checks the king for attack from a queen
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from a queen
     */
    public boolean checkFromQueen(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location startLocation;
        Location attackLocation;

        List<BiFunction<Location,Integer,Location>> locationDirections;
        locationDirections = new ArrayList<>();
        locationDirections.add(Location::leftX);
        locationDirections.add(Location::rightX);
        locationDirections.add(Location::upX);
        locationDirections.add(Location::downX);
        locationDirections.add(Location::upLeftX);
        locationDirections.add(Location::upRightX);
        locationDirections.add(Location::downLeftX);
        locationDirections.add(Location::downRightX);

        startLocation = Location.copy(location);
        for (BiFunction<Location,Integer,Location> function : locationDirections)
        {
            int next = 1;
            attackLocation = nextLocation(startLocation,next,function);
            while ((attackLocation != null ) && boardState.isEmpty(attackLocation))
            {
                next++;
                attackLocation = nextLocation(startLocation,next,function);
            }
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PieceQueen))
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * determines if the piece is protected by its queen
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for piece protection
     * @return true if the piece at the location argument is protected by its queen
     */
    public boolean isProtectedByQueen(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        
        Location startLocation;
        Location protectionLocation;

        List<BiFunction<Location,Integer,Location>> locationDirections;
        locationDirections = new ArrayList<>();
        locationDirections.add(Location::leftX);
        locationDirections.add(Location::rightX);
        locationDirections.add(Location::upX);
        locationDirections.add(Location::downX);
        locationDirections.add(Location::upLeftX);
        locationDirections.add(Location::upRightX);
        locationDirections.add(Location::downLeftX);
        locationDirections.add(Location::downRightX);

        startLocation = Location.copy(location);
        for (BiFunction<Location,Integer,Location> function : locationDirections)
        {
            int next = 1;
            protectionLocation = nextLocation(startLocation,next,function);
            while ((protectionLocation != null ) && boardState.isEmpty(protectionLocation))
            {
                next++;
                protectionLocation = nextLocation(startLocation,next,function);
            }
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PieceQueen))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * checks the king for attack from the opponent's king
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from opponent's king
     */
    public boolean checkFromKing(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        
        Location[] attackLocations = { Location.up(location),
                                       Location.down(location),
                                       Location.left(location),
                                       Location.right(location),
                                       Location.upLeft(location),
                                       Location.downLeft(location),
                                       Location.upRight(location),
                                       Location.downRight(location) };
        
        for (Location attackLocation : attackLocations)
        {
            if ((attackLocation != null) && !boardState.isEmpty(attackLocation))
            {
                ChessPiece piece = boardState.getPiece(attackLocation);
                if ((piece.getColor() == opponentColor) && (piece instanceof PieceKing))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
   /**
     * determines if the piece is protected by its king
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for piece protection
     * @return true if the piece at the location argument is protected by its king
     */
    public boolean isProtectedByKing(Location location, ChessBoardState boardState)
    {
        PlayerColor playerColor = getColor();
        
        Location[] protectionLocations = { Location.up(location),
                                       Location.down(location),
                                       Location.left(location),
                                       Location.right(location),
                                       Location.upLeft(location),
                                       Location.downLeft(location),
                                       Location.upRight(location),
                                       Location.downRight(location) };
        
        for (Location protectionLocation : protectionLocations)
        {
            if ((protectionLocation != null) && !boardState.isEmpty(protectionLocation))
            {
                ChessPiece piece = boardState.getPiece(protectionLocation);
                if ((piece.getColor() == playerColor) && (piece instanceof PieceKing))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
     
    
    /**
     * checks the king, at the location argument for attack from opponent
     * @param location location for the king to check from
     * @param boardState state of the board to analyze for a king in check
     * @return true if the king at the location argument is in check from an opponent's piece
     */
    public boolean check(Location location, ChessBoardState boardState)
    {
        // PlayerColor playerColor = getColor();
        // PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        // Direction playerDirection = properties.getColorDirection(playerColor);
        // Direction opponentDirection = properties.getColorDirection(opponentColor);

        // check pawn up-left,up-right or down-left,down-right (2 cases)
        if (checkFromPawns(location,boardState))
            return true;
        
        // check bishop up-rightX ... (4 cases)
        if (checkFromBishops(location,boardState))
            return true;
        
        // check knight up-left2, up-right2, up2-left, up2-right ... (8 cases)
        if (checkFromKnights(location,boardState))
            return true;
        
        // check rook upX,leftX ... (4 cases)
        if (checkFromRooks(location,boardState))
            return true;
        
        // check queen up-leftX, downX ... (8 cases)
        if (checkFromQueen(location,boardState))
            return true;
        
        // check king up, down, left, right (4 cases)
        if (checkFromKing(location,boardState))
            return true;
        
        return false;
    }
    /**
     * determines if the piece is protected by another piece
     * @param location location of the piece to check for protection
     * @param boardState state of the board to analyze for piece protection
     * @return true if the piece at the location argument is protected by another piece
     */
    public boolean isProtected(Location location, ChessBoardState boardState)
    {
        // PlayerColor playerColor = getColor();
        // PlayerColor opponentColor = Properties.oppositeColor(playerColor);
        // Direction playerDirection = properties.getColorDirection(playerColor);
        // Direction opponentDirection = properties.getColorDirection(opponentColor);

        // check pawn up-left,up-right or down-left,down-right (2 cases)
        if (isProtectedByPawns(location,boardState))
            return true;
        
        // check bishop up-rightX ... (4 cases)
        if (isProtectedByBishops(location,boardState))
            return true;
        
        // check knight up-left2, up-right2, up2-left, up2-right ... (8 cases)
        if (isProtectedByKnights(location,boardState))
            return true;
        
        // check rook upX,leftX ... (4 cases)
        if (isProtectedByRooks(location,boardState))
            return true;
        
        // check queen up-leftX, downX ... (8 cases)
        if (isProtectedByQueen(location,boardState))
            return true;
        
        // check king up, down, left, right (4 cases)
        if (isProtectedByKing(location,boardState))
            return true;
        
        return false;
    }

    public PlayerColor getColor()
    {
        return pieceColor;
    }
    
    /**
     * initiates deep-copy creation of a piece
     * @param piece piece of which to create a deep-copy
     * @return newly-created deep copy of the piece in the argument
     */
    public static ChessPiece copy(ChessPiece piece)
    {
        return piece.createCopy();
    }
}
