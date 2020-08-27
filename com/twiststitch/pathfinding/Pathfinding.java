package com.twiststitch.pathfinding;

import com.twiststitch.game.Scene;
import com.twiststitch.primative.Point2d;

public abstract class Pathfinding {

    protected Scene playingField;

    public Pathfinding(Scene playingField) {
        this.playingField = playingField;
    }

    public abstract Point2d getNextPosition(Point2d currentPosition, Point2d targetPosition);

}
