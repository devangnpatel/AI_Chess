package game.utility;

/**
 *
 * @author devang
 */
public class Geometry {
    
    /**
     * Gets the slope of a line determined by 2 points
     * @param p1 end of line segment
     * @param p2 other end of line segment
     * @return slope of line identified by Points p1 and p2
     */
    public static double getSlope(Point p2, Point p1)
    {
        int dy = p1.y - p2.y;
        int dx = p1.x - p2.x;
        if (Math.abs(dx) < 0.0001) return 9999;
        return (double)dy/(double)dx;
    }
    
    /**
     * Gets the intercept of a line determined by a slope and a Point on that line
     * @param p1 end of line segment
     * @param slope slops of line segment
     * @return intercept of line identified by slope and a Point
     */
    public static double getIntercept(Point p1, double slope)
    {
        return (double)p1.y - slope*(double)p1.x;
    }
    
    /**
     * Gets the intersection coordinate of line defined by p1,p2 and line defined by p3,p4
     * @param p1 end of line segment 1
     * @param p2 other end of line segment 1
     * @param p3 end of line segment 2
     * @param p4 other end of line segment 2
     * @return intersection Point of two lines
     */
    public static Point getIntersectionPoint(Point p1, Point p2, Point p3, Point p4)
    {
        double m1 = Geometry.getSlope(p1,p2);
        double b1 = Geometry.getIntercept(p1,m1);
        
        double m2 = Geometry.getSlope(p3,p4);
        double b2 = Geometry.getIntercept(p3,m2);
        
        double x = 0.0;
        double y = 0.0;
        if (Math.abs(m1-m2) < 0.0001)
            x = 9999.0;
        else 
            x = (b2 - b1)/(m1 - m2);
        
        y = m1*x + b1;
        
        return new Point((int)Math.floor(x),(int)Math.floor(y));
    }
    
    /**
     * Tests whether an input Point is to the right of a line in slope-intercept form
     * @param p Point to test for location
     * @param slope slope of line to compare against
     * @param intercept intercept of line to compare against
     * @return true if p is to the right of line identified by input slope,intercept arguments
     */
    public static boolean isToRight(Point p, double slope, double intercept)
    {
        return (double)p.x > ((double)p.y - intercept)/slope;
    }
    
    /**
     * Tests whether an input Point is to the left of a line in slope-intercept form
     * @param p Point to test for location
     * @param slope slope of line to compare against
     * @param intercept intercept of line to compare against
     * @return true if p is to the left of line identified by input slope,intercept arguments
     */
    public static boolean isToLeft(Point p, double slope, double intercept)
    {
        return (double)p.x < ((double)p.y - intercept)/slope;
    }
    
    /**
     * Tests whether an input Point is below a line in slope-intercept form
     * @param p Point to test for location
     * @param slope slope of line to compare against
     * @param intercept intercept of line to compare against
     * @return true if p is below line identified by input slope,intercept arguments
     */
    public static boolean isBelow(Point p, double slope, double intercept)
    {
        return (double)p.y > (double)p.x*slope + intercept;
    }
    
    /**
     * Tests whether an input Point is above a line in slope-intercept form
     * @param p Point to test for location
     * @param slope slope of line to compare against
     * @param intercept intercept of line to compare against
     * @return true if p is above line identified by input slope,intercept arguments
     */
    public static boolean isAbove(Point p, double slope, double intercept)
    {
        return (double)p.y < (double)p.x*slope + intercept;
    }
    
    /**
     * Tests whether an input Point p is below a line identified by Points p1,p2
     * @param p Point to test for location
     * @param p1 Point on testing line segment
     * @param p2 second Point on testing line segment
     * @return true if p is below line identified by p1 and p2
     */
    public static boolean isBelow(Point p, Point p1, Point p2)
    {
        double slope = getSlope(p1,p2);
        double intercept = getIntercept(p1,slope);
        return isBelow(p,slope,intercept);
    }
    
    /**
     * Tests whether an input Point p is above a line identified by Points p1,p2
     * @param p Point to test for location
     * @param p1 Point on testing line segment
     * @param p2 second Point on testing line segment
     * @return true if p is above line identified by p1 and p2
     */
    public static boolean isAbove(Point p, Point p1, Point p2)
    {
        double slope = getSlope(p1,p2);
        double intercept = getIntercept(p1,slope);
        return isAbove(p,slope,intercept);
    }
    
    /**
     * Tests whether an input Point p is to the left of line identified by Points p1,p2
     * @param p Point to test for location
     * @param p1 Point on testing line segment
     * @param p2 second Point on testing line segment
     * @return true if p is to the left of line identified by p1 and p2
     */
    public static boolean isToLeft(Point p, Point p1, Point p2)
    {
        double slope = getSlope(p1,p2);
        double intercept = getIntercept(p1,slope);
        
        if (Math.abs(slope) < 0.0001) return p.x < p1.x;
        
        return isToLeft(p,slope,intercept);
    }
    
    /**
     * Tests whether an input Point p is to the right of line identified by Points p1,p2
     * @param p Point to test for location
     * @param p1 Point on testing line segment
     * @param p2 second Point on testing line segment
     * @return true if p is to the right of line identified by p1 and p2
     */
    public static boolean isToRight(Point p, Point p1, Point p2)
    {
        double slope = getSlope(p1,p2);
        double intercept = getIntercept(p1,slope);
        
        if (Math.abs(slope) < 0.0001) return p.x > p1.x;
        
        return isToRight(p,slope,intercept);
    }

}
