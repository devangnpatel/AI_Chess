package game.graphics;

import game.utility.Location;
import game.utility.Properties;
import java.awt.Color;
import java.awt.Graphics;

/**
 * contains information necessary to paint a graphical space, it's colors, and the piece
 * located on that space, as well as the operations used to actually paint that space
 * @author devang
 */
public class GraphicsSpace {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    protected boolean isSelected;
    protected boolean isHighlighted;
    protected boolean isHoveredOver;
    protected GraphicsPiece contents;
    
    protected Color spaceColor;
    protected Color hoveredOverColor;
    protected Color hoveredOverBorderColor;
    protected Color highlightedColor;
    protected Color highlightedBorderColor;
    protected Color selectedColor;
    protected Color selectedBorderColor;
    
    /**
     * paints this space and its contents to a canvas
     * @param g graphics object on which to paint this space
     */
    public void paint(Graphics g)
    {
        paintBackground(g);
        paintHighlight(g);
        paintSelected(g);
        paintHover(g);
        paintContents(g);
    }
    
    /**
     * paints the piece at this space, if there is one
     * @param g graphics object on which to paint this space piece contents
     */
    protected void paintContents(Graphics g)
    {
        // if the space has contents
        if (contents != null)
            contents.paint(g);
    }
    
    /**
     * paints the background color/picture for this space
     * @param g graphics object on which to paint this space
     */
    protected void paintBackground(Graphics g)
    {
        // paint plain space
        g.setColor(spaceColor);
        g.fillRect(x*width, y*height, width, height);
    }
    
    /**
     * paints the color-scheme for a selected space, if this space is currently selected
     * @param g graphics object on which to paint selected-status color-scheme
     */
    protected void paintSelected(Graphics g)
    {
        if (isSelected())
        {
            // paint highlighted space: border with inset
            g.setColor(selectedBorderColor);
            g.fillRect(x*width, y*height, width, height);

            // paint highlighted space: inset from border
            int outline = 1;
            g.setColor(selectedColor);
            g.fillRect(x*width + outline, y*height + outline, width - outline*2, height - outline*2);
        }
    }
    
    /**
     * paints the color-scheme for a highlighted space, if this space is currently highlighted
     * @param g graphics object on which to paint highlighted-status color-scheme
     */
    protected void paintHighlight(Graphics g)
    {
        if (isHighlighted())
        {
            // paint highlighted space: border with inset
            g.setColor(highlightedBorderColor);
            g.fillRect(x*width, y*height, width, height);

            // paint highlighted space: inset from border
            int outline = 1;
            g.setColor(highlightedColor);
            g.fillRect(x*width + outline, y*height + outline, width - outline*2, height - outline*2);
        }
    }
    
    /**
     * paints the color-scheme for a hovered-over space, if this space is currently hovered-over
     * @param g graphics object on which to paint hovered-over-status color-scheme
     */
    protected void paintHover(Graphics g)
    {
        if (isHoveredOver())
        {
            // paint highlighted space: border with inset
            g.setColor(hoveredOverBorderColor);
            g.fillRect(x*width, y*height, width, height);

            // paint highlighted space: inset from border
            int outline = 1;
            g.setColor(hoveredOverColor);
            g.fillRect(x*width + outline, y*height + outline, width - outline*2, height - outline*2);
        }
    }
            
    /**
     * returns whether this space is currently in highlighted state
     * @return true if this space should be highlighted, false otherwise
     */
    protected boolean isHighlighted()
    {
        return isHighlighted;
    }
    
    /**
     * sets the highlighted state for this space
     * @param highlight true if this space should be highlighted, false otherwise
     */
    protected void setHighlighted(boolean highlight)
    {
        isHighlighted = highlight;
    }
    
    /**
     * returns whether this space is currently selected
     * @return true if this space is currently selected by the player/gui, false otherwise
     */
    protected boolean isSelected()
    {
        return isSelected;
    }
    
    /**
     * sets the selected-state status for this space
     * @param select true if this space should be set to selected-status, false otherwise
     */
    protected void setSelected(boolean select)
    {
        isSelected = select;
    }
    
    /**
     * returns whether this space is currently hovered-over
     * @return true if this space is currently hovered-over, false otherwise
     */
    protected boolean isHoveredOver()
    {
        return isHoveredOver;
    }
    
    /**
     * sets the hovered-over status for this space
     * @param hoveredOver true if this space is hovered-over, false otherwise
     */
    protected void setHoveredOver(boolean hoveredOver)
    {
        isHoveredOver = hoveredOver;
    }
    
    /**
     * sets the piece graphical properties for this space, if the board at this space
     * has contents
     * @param contents piece graphical object for this space, null if should be empty
     */
    public void setContents(GraphicsPiece contents)
    {
        this.contents = contents;
    }
        
    /**
     * Creates an object to hold, modify and display graphical properties for a game space and game piece
     * @param properties game properties objects from which to get color-scheme attributes
     * @param location col,row-indexed position for this graphical space
     * @param width java Canvas pixel-width dimension for a single space
     * @param height java Canvas pixel-height dimension for a single space
     * @param spaceColor background color (light,dark), (white,black), (red,black) for this space
     */
    public GraphicsSpace(Properties properties, Location location, int width, int height, Color spaceColor)
    {
        this(properties,location,spaceColor);
        
        this.width      = width;
        this.height     = height;
    }
    /**
     * Creates an object to hold, modify and display graphical properties for a game space and game piece
     * @param properties game properties objects from which to get color-scheme attributes
     * @param location col,row-indexed position for this graphical space
     * @param spaceColor background color (light,dark), (white,black), (red,black) for this space
     */
    public GraphicsSpace(Properties properties, Location location, Color spaceColor)
    {
        this.x          = Location.getCol(location);
        this.y          = Location.getRow(location);
        
        setSpaceColor(spaceColor);
        setHighlightedColor(properties.get_HIGHLIGHTED_SPACE_COLOR());
        setHighlightedBorderColor(properties.get_HIGHLIGHTED_SPACE_BORDER_COLOR());
        setHoveredOverColor(properties.get_HOVERED_OVER_SPACE_COLOR());
        setHoveredOverBorderColor(properties.get_HOVERED_OVER_SPACE_BORDER_COLOR());
        setSelectedColor(properties.get_SELECTED_SPACE_COLOR());
        setSelectedBorderColor(properties.get_SELECTED_SPACE_BORDER_COLOR());
        
        isSelected    = false;
        isHighlighted = false;
        isHoveredOver = false;
        contents      = null;
    }
    
    public Color getSelectedBorderColor()
    {
        return selectedBorderColor;
    }
    
    public Color getSelectedColor()
    {
        return selectedColor;
    }
    
    public Color getHoveredOverBorderColor()
    {
        return hoveredOverBorderColor;
    }
    
    public Color getHoveredOverColor()
    {
        return hoveredOverColor;
    }
    
    public Color getHighlightedBorderColor()
    {
        return highlightedBorderColor;
    }
    
    public Color getHighlightedColor()
    {
        return highlightedColor;
    }
    
    public Color getSpaceColor()
    {
        return spaceColor;
    }
    
    public void setSelectedBorderColor(Color color)
    {
        this.selectedBorderColor = color;
    }
    
    public void setSelectedColor(Color color)
    {
        this.selectedColor = color;
    }
    
    public void setHoveredOverBorderColor(Color color)
    {
        this.hoveredOverBorderColor = color;
    }
    
    public void setHoveredOverColor(Color color)
    {
        this.hoveredOverColor = color;
    }
    
    public void setHighlightedBorderColor(Color color)
    {
        this.highlightedBorderColor = color;
    }
    
    public void setHighlightedColor(Color color)
    {
        this.highlightedColor = color;
    }
    
    public void setSpaceColor(Color color)
    {
        this.spaceColor = color;
    }

    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getWidth()
    {
        return width;
    }
}
