package com.twiststitch.entity;

import com.twiststitch.game.GameScene;
import com.twiststitch.primative.Node;

/***
 * The Player class is a controllable entity (by the player)
 *
 * @author  Devon Ly
 * @version 1.1
 * @since   2020-08-07
 */
public class Player extends Entity {

    public enum MoveAction { MOVE, PASS, NOACTION }

    private Node targetNode;
    private MoveAction proposedAction;

    public Player(String name, GameScene playingField, int startingHealth, int attackDamage) {
        super(name, playingField, startingHealth, attackDamage);
        targetNode = null;
        proposedAction = MoveAction.NOACTION;
    }

    /***
     * This method needs to be called after setMoveAction
     * This was done so whether AI, network player or directly controlled
     * that the performAction method would be consistent across an classes
     * derived from Entity class
     *
     * @return The result of type MoveResult from performing the action set
     * from setMoveAction method
     */
    public ActionResult performAction() {
        ActionResult result;
        if (proposedAction == MoveAction.MOVE) {
             result = super.setNodePosition(targetNode);
             targetNode = null;
             proposedAction = MoveAction.NOACTION;
             return result;
        } else if (proposedAction == MoveAction.PASS) {
            super.decreaseTurnsToDelay();
            proposedAction = MoveAction.NOACTION;
            return ActionResult.PASSED;
        } else {
            return ActionResult.NOACTIONTAKEN;
        }

    }

    /***
     * This is need to be called before the perform action command
     *
     * @param moveAction Action of type MoveAction that player wants to take
     * @param targetNode If MoveAction is 'MOVE' then a target node is required to be specified
     * @return Return true if action is set, other returns false
     */
    public boolean setMoveAction(MoveAction moveAction, Node targetNode) {
        if ( (moveAction == MoveAction.MOVE) && (targetNode != null) ) {
            this.proposedAction = MoveAction.MOVE;
            this.targetNode = targetNode;
            return true;
        }
        else if ( moveAction == MoveAction.PASS) {
            this.proposedAction = MoveAction.PASS;
            this.targetNode = null;
            return true;
        } else {
            this.proposedAction = MoveAction.NOACTION;
            this.targetNode = null;
            return false;
        }

    }

}
