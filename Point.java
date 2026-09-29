package game.utility;

/**
 *
 * @author devang
 */
public class Point {
    public int x;
    public int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Point(double d_x, double d_y)
    {
        x = (int)Math.floor(d_x);
        y = (int)Math.floor(d_y);
    }
}
