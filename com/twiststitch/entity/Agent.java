package com.twiststitch.entity;

import com.twiststitch.game.GameScene;
import com.twiststitch.pathfinding.Pathfinding;
import com.twiststitch.pathfinding.PathfindingDijkstra;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * The Agent class serves as the player's opponent and/or enemy.
 * It's purpose is to seek and chase the player.
 *
 * @author  Devon Ly
 * @version 1.1
 * @since   2020-08-07
 */
public class Agent extends Entity {

    private Pathfinding searchAlgorithm;
    private Entity target;
    private double traversalEaseFactor;

    public enum SearchAlgorithm {DIJKSTRA, A_STAR, D_STAR}

    public Agent(String name, GameScene playingField, SearchAlgorithm algorithm, int startingHealth, int attackDamage) {
        super(name, playingField, startingHealth, attackDamage);

        acquireTarget();

        int nodeIndex;
        while (this.nodePosition == target.getNodePosition() ) { // ensure the agent's position is not the same as the target node
            nodeIndex = (int)(Math.random() * playingField.getGraph().getNodeList().size());
            this.nodePosition = playingField.getGraph().getNodeList().get(nodeIndex);
        }

        switch(algorithm) {
            case DIJKSTRA:
                searchAlgorithm = new PathfindingDijkstra(playingField);
                if (target != null ) ((PathfindingDijkstra) searchAlgorithm).getNextPosition(this.nodePosition, target.getNodePosition() );
                break;
            case A_STAR:
            case D_STAR:
            default:
        }
    }

    public ActionResult performAction() {
        decreaseTurnsToDelay();

        if (target == null) { // if no target aquired, find a target
            acquireTarget();
        }

        if (remainingTurnsToDelay == 0) {

            if ( isPlayerAtPosition() ) {
                // if agent and target player at the same location, attack the player
                target.damage(this.attackDamage);
                return ActionResult.ATTACKING;

            } else {
                // otherwise move towards the target player
                Node newPosition = searchAlgorithm.getNextPosition(this.nodePosition, target.nodePosition);
                ActionResult actionResult = setNodePosition(newPosition);
                searchAlgorithm.getNextPosition(this.nodePosition, target.nodePosition);
                return actionResult;
            }

        } else {
            return ActionResult.STILLDELAYED;
        }

    }

    private void acquireTarget() {

        Optional<Entity> filterPlayer;

        filterPlayer = playingField.getEntities().stream().filter(o -> o.getName().equals("Player 1")).findFirst();
        if (filterPlayer.isPresent()) {
            this.target = filterPlayer.get();
        } else {
            this.target = null;
        }

    }

    public Entity getTarget() {
        return target;

    }

    public void setTarget(Entity target) {
        this.target = target;
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

    private boolean isPlayerAtPosition() {
        return this.nodePosition == target.getNodePosition();
    }

    private boolean isPlayerAdjacent() {

        List<Edge> connectedEdges = playingField.getGraph().getEdgeList().stream().filter(o -> o.referenceNode == this.nodePosition).collect(Collectors.toList());
        for (Edge edge : connectedEdges) {
            if ( edge.targetNode == target.getNodePosition() ) {
                return true;
            }
        }

        return false;
    }

}
