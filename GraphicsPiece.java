package game.graphics;

import chess.pieces.ChessPiece;
import chess.pieces.PieceBishop;
import chess.pieces.PieceKing;
import chess.pieces.PieceKnight;
import chess.pieces.PiecePawn;
import chess.pieces.PieceQueen;
import chess.pieces.PieceRook;
import game.utility.Properties;
import game.utility.Properties.PlayerColor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * contains information necessary to paint a board game piece
 * @author devang
 */
public class GraphicsPiece {    

    private final PlayerColor pieceColor;
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    private String pieceTypeString;
    private String pieceColorString;
    private GraphicsSprites pieceGraphics;
    protected Properties properties =  null;

    protected GraphicsPiece(GraphicsSprites pieceGraphics,ChessPiece piece, int x, int y,int width,int height)
    {
        this(piece.getColor(),x,y,width,height);
        
        this.pieceGraphics = pieceGraphics;
        PlayerColor color = piece.getColor();
        
        if (color.compareTo(PlayerColor.BLACK) == 0) this.pieceColorString = "black";
        if (color.compareTo(PlayerColor.WHITE)   == 0) this.pieceColorString = "white";
        
        if (piece instanceof PieceBishop) this.pieceTypeString = "bishop";
        if (piece instanceof PieceKnight) this.pieceTypeString = "knight";
        if (piece instanceof PieceRook)   this.pieceTypeString = "rook";
        if (piece instanceof PiecePawn)   this.pieceTypeString = "pawn";
        if (piece instanceof PieceQueen)  this.pieceTypeString = "queen";
        if (piece instanceof PieceKing)   this.pieceTypeString = "king";

    }
    
    /**
     * constructor for a new object that displays graphical items of a piece
     * @param pieceColor color of this piece
     * @param x java canvas pixel location
     * @param y java canvas pixel location
     * @param width java canvas pixel-width dimension for a piece/space
     * @param height java canvas pixel-height dimension for a piece/space
     */
    public GraphicsPiece(PlayerColor pieceColor, int x, int y, int width, int height)
    {
        this.pieceColor = pieceColor;
        this.x = x;
        this.y = y;
        this.width  = width;
        this.height = height;

    }
    
    /**
     * constructor for a new object that displays graphical items of a piece
     * @param pieceColor color of this piece
     * @param x java canvas pixel location
     * @param y java canvas pixel location
     */
    public GraphicsPiece(PlayerColor pieceColor, int x, int y)
    {
        this.pieceColor = pieceColor;
        this.x = x;
        this.y = y;
    }
    
    /**
     * The player-color (not java.awt.Color) of this piece, i.e. red-black
     * @return color of this piece's player
     */
    public PlayerColor getColor()
    {
        return pieceColor;
    }
    /**
     * Creates a new Object used in painting on graphics object of a Canvas
     * @param pieceGraphics object that contains file I/O for sprite images of pieces
     * @param piece Chess piece (white or black), (red or black) etc...
     * @param x Canvas pixel location for x-coordinate of this piece
     * @param y Canvas pixel location for y-coordinate of this piece
     * @param spaceWidth number of pixels for width of space and sprite
     * @param spaceHeight number of pixels for height of space and sprite
     * @return newly-created Chess piece graphics object
     */
    public static GraphicsPiece create(GraphicsSprites pieceGraphics,ChessPiece piece,int x,int y,int spaceWidth,int spaceHeight)
    {
        return new GraphicsPiece(pieceGraphics,piece,x,y,spaceWidth,spaceHeight);
    }
    /**
     * gets the static variables for color-scheme for a Chess game
     * @return Properties object that holds static values for Chess game attributes
     */
    public Properties getGameProperties()
    {
        return properties;
    }
        
    /**
     * sets the static variables for color-scheme for a Chess game
     * @param properties game properties object that holds static values for Chess game attributes
     */
    public void setGameProperties(Properties properties)
    {
        this.properties = properties;
    }
    /**
     * paints this Chess Piece (real photo) to the graphics object in arguments
     * @param g Graphics object on which to draw this piece
     */
    public void paint(Graphics g)
    {
        BufferedImage pieceImage = pieceGraphics.getPieceImage(pieceTypeString, pieceColorString);
        g.drawImage(pieceImage,x*width,y*height,null);
    }    


}
