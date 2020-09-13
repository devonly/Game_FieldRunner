package com.twiststitch.entity;

import com.twiststitch.game.GameScene;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;
import java.util.Optional;

/***
 * The Entity class is the base class from which game entities are derived from
 * whether it is the player or an enemy. A child class must implement the move()
 * method to make clas concrete.
 *
 * @author  Devon Ly
 * @version 1.1
 * @since   2020-08-07
 */
public abstract class Entity {
    protected Node nodePosition;
    protected Edge traversalEdge;
    protected int remainingTurnsToDelay;
    protected GameScene playingField;
    protected String name;
    protected int health, attackDamage;

    public enum ActionResult { MOVED, BLOCKED, ATTACKING, STILLDELAYED, PASSED, QUIT, ERROR, UNTRAVERSABLE, NOACTIONTAKEN }

    public Entity(String name, GameScene playingField, int startingHealth, int attackDamage) {
        this.name = name;
        this.playingField = playingField;
        this.attackDamage = attackDamage;

        // perhaps it should be the game controller's responsibility to determine starting position rather than the entity itself
        int randomNode = (int)(Math.random() * playingField.getGraph().getNodeList().size());
        nodePosition = playingField.getGraph().getNodeList().get(randomNode);
        traversalEdge = null;
        remainingTurnsToDelay = 0;
        this.health = startingHealth;
    }

    public abstract ActionResult performAction();

    protected void decreaseTurnsToDelay() {
        if (remainingTurnsToDelay > 0) remainingTurnsToDelay--;
        else traversalEdge = null;
    }

    public Node getNodePosition() {
        return this.nodePosition;
    }

    protected ActionResult setNodePosition(Node targetNode) {

        if (remainingTurnsToDelay > 0) {
            return ActionResult.STILLDELAYED;
        }

        Optional<Edge> edgeOptional = playingField.getGraph().getEdgeList().stream().filter( o -> o.equals(this.nodePosition, targetNode)).findFirst();
        if (edgeOptional.isPresent()) { // a path to target edge exist, move is possible
            traversalEdge = edgeOptional.get();
            remainingTurnsToDelay += traversalEdge.traversalCost; // add traversal cost to turns to delay
            this.nodePosition = targetNode; // set new position
            return ActionResult.MOVED;
        } else { // there is no path to traverse to the target node
            return ActionResult.UNTRAVERSABLE;
        }
    }

    public int getRemainingTurnsToDelay() {
        return this.remainingTurnsToDelay;
    }

    public Edge getTraversalEdge() {
        return traversalEdge;
    }

    public String getName() {
        return this.name;
    }

    public void kill() {
        this.health = 0;
    }

    public void damage(int amount) {
        this.health = Math.max((this.health - amount), 0);
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public int getHealth() {
        return this.health;
    }

}
