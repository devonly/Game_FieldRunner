package com.twiststitch.pathfinding;

import com.twiststitch.game.Scene;
import com.twiststitch.primative.Node;

public abstract class Pathfinding {

    protected Scene playingField;

    public Pathfinding(Scene playingField) {
        this.playingField = playingField;
    }

    public abstract Node getNextPosition(Node startingNode, Node targetNode);

}
