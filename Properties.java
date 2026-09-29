package game.utility;

import java.awt.Color;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.YELLOW;
import java.io.Serializable;

/**
 * This class extends from Properties (which has only static members)<br>
 * This class (CheckersProperties, ChessProperties, OthelloProperties...)<br>
 * allows different graphics, sizes and colors for any extended game<br>
 * - also, adjusting num_rows, num_cols, an atypical game can be created<br>
 *   i.e.) 10x10 board with different chess pieces (4 knights instead of just 2)
 * 
 * @author devang
 */
public class Properties {
    public static int NUM_ROWS = 8;
    public static int NUM_COLS = 8;
    
    public enum PlayerColor implements Serializable
    {
        WHITE,
        BLACK
    }
   
    public enum Direction implements Serializable
    {
        UP,
        DOWN
    }    
    private final static int BOARD_MARGIN = 0;
    
    public static final int BOARD_WIDTH  = 600;
    public static final int BOARD_HEIGHT = 600;
    
    protected Direction WHITE_DIRECTION;
    protected Direction BLACK_DIRECTION;
    
    public static final PlayerColor INITIAL_PLAYER_COLOR = PlayerColor.WHITE;
    
    public static final Color LIGHT_SPACE_COLOR               = new Color(170,145,100); //new Color(188,185,83);
    public static final Color DARK_SPACE_COLOR                = new Color(125,90,50); //new Color(101,147,90);
    public static final Color HIGHLIGHTED_SPACE_COLOR         = YELLOW.darker(); //ORANGE.darker().darker();
    public static final Color HIGHLIGHTED_SPACE_BORDER_COLOR  = new Color(100,200,100,175); // DARK_GRAY;
    public static final Color HOVERED_OVER_SPACE_COLOR        = new Color(200,105,0); //new Color(145,245,145,125);
    public static final Color HOVERED_OVER_SPACE_BORDER_COLOR = GREEN;
    public static final Color SELECTED_SPACE_COLOR            = ORANGE.darker(); //ORANGE.brighter();
    public static final Color SELECTED_SPACE_BORDER_COLOR     = GREEN; //DARK_GRAY; //LIGHT_GRAY;

    public int get_NUM_ROWS() { return NUM_ROWS; }
    public int get_NUM_COLS() { return NUM_COLS; }
    
    public int get_BOARD_WIDTH() { return BOARD_WIDTH; }
    public int get_BOARD_HEIGHT() { return BOARD_HEIGHT; }
    
    public Direction get_WHITE_DIRECTION() { return WHITE_DIRECTION; }
    public Direction get_BLACK_DIRECTION() { return BLACK_DIRECTION; }
    
    public PlayerColor get_INITIAL_PLAYER_COLOR() { return INITIAL_PLAYER_COLOR; }
        
    public Color get_LIGHT_SPACE_COLOR() { return LIGHT_SPACE_COLOR; }
    public Color get_DARK_SPACE_COLOR() { return DARK_SPACE_COLOR; }
    public Color get_HIGHLIGHTED_SPACE_COLOR() { return HIGHLIGHTED_SPACE_COLOR; }
    public Color get_HIGHLIGHTED_SPACE_BORDER_COLOR() { return HIGHLIGHTED_SPACE_BORDER_COLOR; }
    public Color get_HOVERED_OVER_SPACE_COLOR() { return HOVERED_OVER_SPACE_COLOR; }
    public Color get_HOVERED_OVER_SPACE_BORDER_COLOR() { return HOVERED_OVER_SPACE_BORDER_COLOR; }
    public Color get_SELECTED_SPACE_COLOR() { return SELECTED_SPACE_COLOR; }
    public Color get_SELECTED_SPACE_BORDER_COLOR() { return SELECTED_SPACE_BORDER_COLOR; }
    
    public Properties() { }
    
    public void setColorDirections(Direction whiteDirection, Direction blackDirection)
    {
        WHITE_DIRECTION = whiteDirection;
        BLACK_DIRECTION = blackDirection;
    }
    
    public int get_BOARD_MARGIN()
    {
        return BOARD_MARGIN;
    }
    
    public static Properties init(Direction whiteDirection, Direction blackDirection)
    {
        Properties properties = new Properties();
        properties.setColorDirections(whiteDirection, blackDirection);
        return properties;
    }
    
    /**
     * Gets the direction (up or down, forwards or backwards) the PlayerColor is assigned <br>
     * - With theses properties, you can set white to move up, or black to move up
     *   for a local game screen
     * @param color Color of which to get the direction
     * @return directions that this color is moving
     */    
    public Direction getColorDirection(PlayerColor color)
    {
        switch (color)
        {
            case WHITE:
                return WHITE_DIRECTION;
            case BLACK:
                return BLACK_DIRECTION;
        }
        return null;
    }

    public static PlayerColor oppositeColor(PlayerColor color)
    {
        switch (color)
        {
            case WHITE:
                return PlayerColor.BLACK;
            case BLACK:
                return PlayerColor.WHITE;
        }
        return null;
    }

}
