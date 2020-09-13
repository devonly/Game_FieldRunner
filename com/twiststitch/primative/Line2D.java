package com.twiststitch.primative;

public class Line2D {

    public Dimension2D startPoint;
    public Dimension2D endPoint;

    public Line2D(Double startPointx, Double startPointy, Double endPointx, Double endPointy) {
        this.startPoint = new Dimension2D(startPointx, startPointy);
        this.endPoint = new Dimension2D(endPointx, endPointy);
    }

    public Line2D(Dimension2D startPoint, Dimension2D endPoint) {
        this.startPoint = new Dimension2D(startPoint.x, startPoint.y);
        this.endPoint = new Dimension2D(endPoint.x, endPoint.y);
    }

    public Line2D(Line2D otherLine) {
        this.startPoint = new Dimension2D(otherLine.startPoint.x, otherLine.startPoint.y);
        this.endPoint = new Dimension2D(otherLine.endPoint.x, otherLine.endPoint.y);
    }

    public boolean equals(Dimension2D startPoint, Dimension2D endPoint) {
        return ( (this.startPoint.x == startPoint.x) && (this.startPoint.y == startPoint.y)
                && (this.endPoint.x == endPoint.x) && (this.endPoint.y == endPoint.y) );
    }

    public boolean equals(Line2D otherLine) {
        return ( (this.startPoint.x == otherLine.startPoint.x) && (this.startPoint.y == otherLine.startPoint.y)
                && (this.endPoint.x == otherLine.endPoint.x) && (this.endPoint.y == otherLine.endPoint.y) );
    }

    public double distance() {
        return this.startPoint.distanceTo(this.endPoint);
    }

    public double distanceSq() {
        return startPoint.distanceToSquared(this.endPoint);
    }

}
