package game.utility;

import static game.utility.Properties.NUM_COLS;
import static game.utility.Properties.NUM_ROWS;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author devang
 */
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final int x;
    private final int y;
    
    public int idx()
    {
        return y*8 + x;
    }
    public static int idx(Location location)
    {
        return location.idx();
    }
    
    private Location(int col, int row)
    {
        x = col;
        y = row;
    }
    
    public static Location rotate(Location location)
    {
        return new Location(NUM_COLS-1-location.x,NUM_ROWS-1-location.y);
    }
    
    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Location)) return false;
        Location location = (Location)obj;
        return ((location.x == x) && (location.y == y));
    }

    @Override
    public int hashCode() {
        int result = NUM_ROWS*NUM_COLS + 1;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
    
    public static boolean isEndRow(Location location)
    {
        if (location == null) return false;
        
        if ((Location.getRow(location) == 0) || (Location.getRow(location) == (NUM_ROWS-1)))
        {
            return true;
        }
        
        return false;
    }
    
    public static int getCol(Location location)
    {
        return location.x;
    }
    
    public static int getRow(Location location)
    {
        return location.y;
    }

    public static Location copy(Location location)
    {
        if (location == null) return null;
        int col = location.x;
        int row = location.y;
        return Location.of(col,row);
    }
    
    public static Location at(int col,int row)
    {
        return Location.of(col,row);
    }
    
    public static Location of(int col,int row)
    {
        if ((col < 0) || (col >= NUM_COLS))
        {
            return null;
        }
        
        if ((row < 0) || (row >= NUM_ROWS))
        {
            return null;
        }
        
        return new Location(col,row);
    }
    
    public static boolean isValid(int col,int row)
    {
        if ((col < 0) || (col >= NUM_COLS))
        {
            return false;
        }
        
        if ((row < 0) || (row >= NUM_ROWS))
        {
            return false;
        }
        
        return true;
    }
    
    public static boolean isValid(Location location)
    {
        if (location == null) return false;
        return isValid(location.x,location.y);
    }
    
    public static Location upLeftX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x-x,location.y-x);
    }
    
    public static Location upRightX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x+x,location.y-x);
    }
    
    public static Location downLeftX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x-x,location.y+x);
    }
    
    public static Location downRightX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x+x,location.y+x);
    }
    
    public static Location upX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y-x);
    }
    
    public static Location downX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y+x);
    }
        
    public static Location leftX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x-x,location.y);
    }
            
    public static Location rightX(Location location, int x)
    {
        if (location == null) return null;
        return Location.of(location.x+x,location.y);
    }
    
    public static Location upLeft(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-1,location.y-1);
    }
    
    public static Location upLeft2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-2,location.y-2);
    }
    
    public static Location upRight(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+1,location.y-1);
    }
    
    public static Location upRight2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+2,location.y-2);
    }
    
    public static Location downLeft(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-1,location.y+1);
    }
    
    public static Location downLeft2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-2,location.y+2);
    }
    
    public static Location downRight(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+1,location.y+1);
    }
    
    public static Location downRight2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+2,location.y+2);
    }
    
    public static Location right(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+1,location.y);
    }
    
    public static Location left(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-1,location.y);
    }
    
    public static Location down(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y+1);
    }
    
    public static Location up(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y-1);
    }

    public static Location up2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y-2);
    }

    public static Location down2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x,location.y+2);
    }

    public static Location left2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x-2,location.y);
    }

    public static Location right2(Location location)
    {
        if (location == null) return null;
        return Location.of(location.x+2,location.y);
    }
    
    public static List<Location> allLocations()
    {
        List<Location> locationsList = new ArrayList<>();
        for (int c = 0; c < NUM_COLS; c++)
        {
            for (int r = 0; r < NUM_ROWS; r++)
            {
                Location location = Location.of(c,r);
                if (location != null) locationsList.add(location);
            }
        }
        return locationsList;
    }
}
