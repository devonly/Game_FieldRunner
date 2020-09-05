package com.twiststitch.primative;

/**
 *  The Dimension2d class allows for the storage and retrieval of any measure the
 *  requires two dimensions. Such a point on a 2 dimensional plane or length in 2 dimensions.
 *
 * @author  Devon Ly
 * @version 1.0
 * @since   2020-08-27
 */
public class Dimension2d {
    public int x;
    public int y;

    public Dimension2d() {
        this.x = 0;
        this.y = 0;
    }
    public Dimension2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Dimension2d(Dimension2d existingDimension2D) {
        this.x = existingDimension2D.x;
        this.y = existingDimension2D.y;
    }

    public boolean equals(int x, int y) {
        return (this.x == x && this.y == y);
    }

    public boolean equals(Dimension2d dimension2D) {
        return (this.x == dimension2D.x && this.y == dimension2D.y);
    }

    public void copy(Dimension2d existingDimension2D) {
        this.x = existingDimension2D.x;
        this.y = existingDimension2D.y;
    }

    public String toString() {
        return x + "," + y;
    }

}
