package com.twiststitch.entity;

import com.twiststitch.game.Field;
import com.twiststitch.game.Scene;
import com.twiststitch.primative.Dimension2d;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;

/***
 * The Entity class is the base class from which game entities are derived from
 * whether it is the player or an enemy. A child class must implement the move()
 * method to make clas concrete.
 *
 * @author  Devon Ly
 * @version 1.0
 * @since   2020-08-07
 */
public abstract class Entity {
    protected Node position;
    protected int turnsToDelay;
    protected Scene playingField;
    protected String name;
    protected int health;

    public enum Direction { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }
    public enum MoveAction { MOVED, BLOCKED, STILLDELAYED, PASSED, QUIT, ERROR }

    public Entity(String name, Scene playingField, int startingHealth) {
        this.name = name;
        this.playingField = playingField;

        // perhaps it should be the game controller's responsibility to determine starting position rather than the entity itself
        position = playingField.getField().getNode( new Dimension2d( (int) (Field.width * Math.random()),(int)(Field.height * Math.random())) );

        turnsToDelay = 0;
        this.health = startingHealth;
    }

    public abstract MoveAction move();

    protected MoveAction doMove(Direction direction) {

        if (turnsToDelay > 0 ) return MoveAction.STILLDELAYED;

        Dimension2d translation = calcTranslation(direction);
        if (isMoveable(translation) ) {
            // find the node with the destination position
            Node targetNode = playingField.getField().getNode( new Dimension2d(this.position.position.x + translation.x , this.position.position.y + translation.y ) );
            // get the edge from current position to destination position
            Edge traversalEdge = playingField.getField().getEdge(this.position, targetNode);

            turnsToDelay += traversalEdge.traversalCost;
            this.position = targetNode;
            return MoveAction.MOVED;
        } else {
            return MoveAction.BLOCKED;
        }

    }

    protected void decreaseTurnsToDelay() {
        if (turnsToDelay > 0) turnsToDelay--;
    }

    private Dimension2d calcTranslation(Direction direction) {
        Dimension2d translation = new Dimension2d();

        switch(direction) {
            case NORTH:
                translation.x = 0;  translation.y = -1; break;
            case NORTHEAST:
                translation.x = 1;  translation.y = -1; break;
            case EAST:
                translation.x = 1;  translation.y = 0; break;
            case SOUTHEAST:
                translation.x = 1;  translation.y = 1; break;
            case SOUTH:
                translation.x = 0;  translation.y = 1; break;
            case SOUTHWEST:
                translation.x = -1; translation.y = 1; break;
            case WEST:
                translation.x = -1; translation.y = 0; break;
            case NORTHWEST:
                translation.x = -1; translation.y = -1; break;
            default:
                break;
        }

        return translation;
    }

    private boolean isMoveable(Dimension2d translation) {

        Dimension2d newPosition = new Dimension2d(this.position.position);
        newPosition.x += translation.x;
        newPosition.y += translation.y;

        return (newPosition.x >= 0 && newPosition.x < Field.width
            && newPosition.y >= 0 && newPosition.y < Field.height );

    }

    public Node getPosition() {
        return this.position;
    }

    public int getTurnsToDelay() {
        return this.turnsToDelay;
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

}
