package com.twiststitch.entity;

import com.twiststitch.game.Scene;
import com.twiststitch.primative.Point2d;

public abstract class Entity {
    protected Point2d position;
    protected int turnsToDelay;
    protected Scene playingField;
    protected String name;
    protected int health;

    public enum Direction { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }
    public enum MoveAction { MOVED, BLOCKED, STILLDELAYED, PASSED, QUIT, ERROR }

    public Entity(String name, Scene playingField, int startingHealth) {
        this.name = name;
        this.playingField = playingField;
        position = new Point2d( (int) (Scene.width * Math.random()),(int)(Scene.height * Math.random()));
        turnsToDelay = 0;
        this.health = startingHealth;
    }

    public abstract MoveAction move();

    protected MoveAction doMove(Direction direction) {

        if (turnsToDelay > 0 ) return MoveAction.STILLDELAYED;

        Point2d translation = calcTranslation(direction);
        if (isMoveable(translation) ) {
            this.position.x += translation.x;
            this.position.y += translation.y;
            turnsToDelay += this.playingField.getTerrainTraversalDifficulty(position);
            return MoveAction.MOVED;
        } else {
            return MoveAction.BLOCKED;
        }

    }

    protected void decreaseTurnsToDelay() {
        if (turnsToDelay > 0) turnsToDelay--;
    }

    private Point2d calcTranslation(Direction direction) {
        Point2d translation = new Point2d();

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

    private boolean isMoveable(Point2d translation) {

        Point2d newPosition = new Point2d(this.position);
        newPosition.x += translation.x;
        newPosition.y += translation.y;

        return (newPosition.x >= 0 && newPosition.x < Scene.width
            && newPosition.y >= 0 && newPosition.y < Scene.height );

    }

    public Point2d getPosition() {
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
