package com.twiststitch.pathfinding;

import com.twiststitch.game.GameScene;
import com.twiststitch.primative.Node;

public abstract class Pathfinding {

    protected GameScene playingField;

    public Pathfinding(GameScene playingField) {
        this.playingField = playingField;
    }

    public abstract Node getNextPosition(Node startingNode, Node targetNode);

}
