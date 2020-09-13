package com.twiststitch.fieldrunner;

import com.twiststitch.primative.Dimension2D;
import com.twiststitch.primative.Line2D;

public class Geometry {

    /***
     * 
     * @param referenceLine The line from which to create a parallel line from
     * @param offsetDistance The distance the parallel line will created from the reference line <br>
     *                 If > 0 angle between referenceLine and parallelLine is +90? <br>
     *                 If < 0 angle between referenceLine and parallelLine is -+90?
     * @return The new parallel name
     */
    public static Line2D getParallelLine(Line2D referenceLine, double offsetDistance) {

        if ((referenceLine.startPoint == null) || (referenceLine.endPoint == null) || (referenceLine.distance() == 0.0)) return null;

        double lineDistance = referenceLine.distance();
        Dimension2D newStart = new Dimension2D(
                referenceLine.startPoint.x + offsetDistance * (referenceLine.endPoint.y - referenceLine.startPoint.y) / lineDistance,
                referenceLine.startPoint.y + offsetDistance * (referenceLine.startPoint.x - referenceLine.endPoint.x) / lineDistance
        );
        Dimension2D newEnd = new Dimension2D(
                referenceLine.endPoint.x + offsetDistance * (referenceLine.endPoint.y - referenceLine.startPoint.y) / lineDistance,
                referenceLine.endPoint.y + offsetDistance * (referenceLine.startPoint.x - referenceLine.endPoint.x) / lineDistance
        );
        return new Line2D(newStart, newEnd);

    }

    public static Line2D getParallelLine(double xStart, double yStart, double xEnd, double yEnd, double offsetDistance) {

        if ( (xStart == xEnd) && (yStart == yEnd) ) return null;

        double lineDistance = Dimension2D.distanceTo(xStart, yStart, xEnd, yEnd);

        Dimension2D newStart = new Dimension2D(
                xStart + offsetDistance * (yEnd - yStart) / lineDistance,
                yStart + offsetDistance * (xStart - xEnd) / lineDistance
        );
        Dimension2D newEnd = new Dimension2D(
                xEnd + offsetDistance * (yEnd - yStart) / lineDistance,
                yEnd + offsetDistance * (xStart - xEnd) / lineDistance
        );
        return new Line2D(newStart, newEnd);
    }

    public static Dimension2D interpolate(Dimension2D startPoint, Dimension2D endPoint, double delta ) {
        if  ( (startPoint == null) || (endPoint == null) || (startPoint.equals(endPoint)) ) {
            return null;
        } else {
            return interpolate(startPoint.x, startPoint.y, endPoint.x, endPoint.y, delta);
        }
    }

    public static Dimension2D interpolate(double startX, double startY, double endX, double endY, double delta) {
        double xDelta = endX - startX;
        double yDelta = endY - startY;
        return new Dimension2D(startX + (xDelta * delta), startY + (yDelta * delta) );
    }

}
