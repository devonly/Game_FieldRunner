package com.twiststitch.primative;

/**
 *  The Dimension2d class allows for the storage and retrieval of any measure the
 *  requires two dimensions. Such a point on a 2 dimensional plane or length in 2 dimensions.
 *
 * @author  Devon Ly
 * @version 1.1
 * @since   2020-08-27
 */
public class Dimension2D {
    public double x;
    public double y;

    public Dimension2D() {
        this.x = 0;
        this.y = 0;
    }
    public Dimension2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Dimension2D(Dimension2D existingDimension2D) {
        this.x = existingDimension2D.x;
        this.y = existingDimension2D.y;
    }

    public boolean equals(int x, int y) {
        return (this.x == x && this.y == y);
    }

    public boolean equals(Dimension2D dimension2D) {
        return (this.x == dimension2D.x && this.y == dimension2D.y);
    }

    public void copy(Dimension2D existingDimension2D) {
        this.x = existingDimension2D.x;
        this.y = existingDimension2D.y;
    }

    public String toString() {
        return x + "," + y;
    }

    /***
     *
     * @param otherDimension2D
     * @return distance to other 2d dimensional point
     */
    public double distanceTo(Dimension2D otherDimension2D) {
        return Math.sqrt( Math.pow( (this.y - otherDimension2D.y), 2) + Math.pow( (this.x - otherDimension2D.x), 2) );
    }

    public double distanceToSquared(Dimension2D otherDimension2D) {
        double xDelta = otherDimension2D.x - this.x;
        double yDelta = otherDimension2D.y - this.y;

        return ( (xDelta * xDelta) + (yDelta * yDelta) );
    }

    /***
     * Return a point midway between this point and
     * the other point
     *
     * @param otherPoint The point to calculate the mid point to
     * @return The calculated mid point
     */
    public Dimension2D midPoint(Dimension2D otherPoint) {
        return new Dimension2D( (this.x + (otherPoint.x - this.x) / 2) , (this.y + (otherPoint.y - this.y)/2) );
    }

    /***
     * Calculate the angle from this point to other point
     * @param otherPoint
     * @return angle from this point to other point in radians
     */
    public double angleTo(Dimension2D otherPoint) {
        return Math.atan2( (otherPoint.y - this.y), (otherPoint.x - this.x) );
    }

    /***
     *
     * @param otherPoint The end point interpolating to
     * @param delta A number between 0.0 to 1.0 to determine delta to interpolate to <br>
     *              0.0 closer to start point , 1.0 close to end point
     * @return A new point between this point and the other point with delta applied
     */
    public Dimension2D interpolate(Dimension2D otherPoint, double delta) {
        return interpolate(otherPoint.x, otherPoint.y, delta);
    }

    public Dimension2D interpolate(double otherX, double otherY, double delta) {
        double xDelta = otherX - this.x;
        double yDelta = otherY - this.y;

        return new Dimension2D(this.x + (xDelta * delta), this.y + (yDelta * delta) );
    }

    public static double distanceTo(double xStart, double yStart, double xEnd, double yEnd) {
        return Math.sqrt( Math.pow( (yStart - yEnd), 2) + Math.pow( (xStart - xEnd), 2) );
    }

}