package com.twiststitch.entity;

import com.twiststitch.game.Scene;
import com.twiststitch.pathfinding.Pathfinding;
import com.twiststitch.pathfinding.PathfindingDijkstra;
import com.twiststitch.primative.Dimension2d;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;

import java.util.Optional;

/***
 * The Agent class serves as the player's opponent and/or enemy.
 * It's purpose is to seek and chase the player.
 *
 * @author  Devon Ly
 * @version 1.0
 * @since   2020-08-07
 */
public class Agent extends Entity {

    private Pathfinding searchAlgorithm;
    private Entity player;
    private double traversalEaseFactor;

    public enum SearchAlgorithm {DIJKSTRA, A_STAR, D_STAR}

    public Agent(String name, Scene playingField, SearchAlgorithm algorithm, int startingHealth) {
        super(name, playingField, startingHealth);
        getPlayer();

        switch(algorithm) {
            case DIJKSTRA: searchAlgorithm = new PathfindingDijkstra(playingField); break;
            case A_STAR:
            case D_STAR:
            default:
        }
    }

    public MoveAction move() {
        decreaseTurnsToDelay();

        if (turnsToDelay == 0) {
            Node newPosition = searchAlgorithm.getNextPosition(this.position, player.position );
            Edge traversalEdge = playingField.getField().getEdge(this.position, newPosition);


            if (traversalEdge.traversalCost != 0) {
                turnsToDelay += (int)Math.ceil(traversalEdge.traversalCost / traversalEaseFactor) ;
            }

            this.position = newPosition;
            System.out.println(this.name + " moved to (" + newPosition.position.x + "," + newPosition.position.y + ")");

            System.out.println("Seeking Player...");
            if (actionAgainstPlayer()) {
                System.out.println("Player found & killed");
            } else {
                System.out.println("Player not found adjacent");
            }

            return MoveAction.MOVED;

        } else {
            System.out.println( this.name + " still delayed for " + turnsToDelay + " turns");
            System.out.println( this.name + " passing turn");
            return MoveAction.STILLDELAYED;
        }

    }

    private void getPlayer() {

        Optional<Entity> filterPlayer;

        filterPlayer = playingField.getEntities().stream().filter(o -> o.getName().equals("Player 1")).findFirst();
        if (filterPlayer.isPresent()) {
            this.player = filterPlayer.get();
        } else {
            this.player = null;
        }

    }

    public Pathfinding getSearchAlgorithm() {
        return searchAlgorithm;
    }

    public void setTraversalEaseFactor(double traversalEaseFactor) {
        this.traversalEaseFactor = traversalEaseFactor;
    }

    public double getTraversalEaseFactor() {
        return traversalEaseFactor;
    }

    private boolean isPlayerAdjacent() {

        Dimension2d searchPoint = new Dimension2d();

        // check around to set if player is adjacent or at the same location
        for (int yOffset = -1; yOffset < 2; yOffset++) {
            for (int xOffset = -1; xOffset < 2; xOffset++) {

                searchPoint.copy(this.position.position);
                searchPoint.x += xOffset;
                searchPoint.y += yOffset;

                // kill the player if next to an agent / enemy
                if (searchPoint.equals(player.position) ) {
                    player.kill();
                    return true;
                }

            }
        }
        return false;
    }

    // kill the player if found
    private boolean actionAgainstPlayer() {
        if (isPlayerAdjacent()) {
            player.kill();
            return true;
        }

        return false;
    }

}
