package com.twiststitch.primative;

public class Point2d {
    public int x;
    public int y;

    public Point2d() {
        this.x = 0;
        this.y = 0;
    }
    public Point2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2d(Point2d existingPoint2d) {
        this.x = existingPoint2d.x;
        this.y = existingPoint2d.y;
    }

    public boolean equals(int x, int y) {
        return (this.x == x && this.y == y);
    }

    public boolean equals(Point2d point2d) {
        return (this.x == point2d.x && this.y == point2d.y);
    }

    public void copy(Point2d existingPoint2d) {
        this.x = existingPoint2d.x;
        this.y = existingPoint2d.y;
    }

}
