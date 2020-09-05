package com.twiststitch.entity;

import com.twiststitch.game.Scene;
import java.util.Scanner;

/***
 * The Player class is a controllable entity (by the player)
 *
 * @author  Devon Ly
 * @version 1.0
 * @since   2020-08-07
 */
public class Player extends Entity {

    public Player(String name, Scene playingField, int startingHealth) {
        super(name, playingField, startingHealth);
    }

    public MoveAction move() {
        String inputCommand;
        String legalCharacters;
        boolean validCommand = false;
        MoveAction requestedAction = MoveAction.ERROR;
        legalCharacters = turnsToDelay > 0 ? "DQ" : "DQERFVCXSW";

        displayMoveOptions();
        decreaseTurnsToDelay();
        Scanner inputScanner = new Scanner(System.in);

        while (!validCommand) {

            if (inputScanner.hasNext()) {
                inputCommand = Character.toString(inputScanner.next().charAt(0)).toUpperCase();
                System.out.println("You have selected " + inputCommand);
                if (!legalCharacters.contains(inputCommand)) {
                    System.out.println("You have not selected a valid option");
                    displayMoveOptions();
                } else {

                    switch(inputCommand) {
                        case "Q":
                            return MoveAction.QUIT;
                        case "D":
                            System.out.println("Passing Turn");
                            return MoveAction.PASSED;
                        default:
                            requestedAction = doMove(calcDirection(inputCommand));
                    }

                    switch(requestedAction) {

                        case QUIT:
                        case MOVED:
                            validCommand = true;
                            break;
                        case BLOCKED:
                            System.out.println("\nYou cannot move in that direction!");
                            displayMoveOptions();
                            break;
                        case STILLDELAYED: // should ever get here due to prior check
                            System.out.println("You are still delayed for " + turnsToDelay + " turns");
                            System.out.println("You can only pass this turn");
                            break;
                    }

                }
            }
        }

        return requestedAction;

    }

    private Direction calcDirection(String directionKey) {

        switch(directionKey) {
            case "R":
                return Direction.NORTHEAST;
            case "F":
                return Direction.EAST;
            case "V":
                return Direction.SOUTHEAST;
            case "C":
                return Direction.SOUTH;
            case "X":
                return Direction.SOUTHWEST;
            case "S":
                return Direction.WEST;
            case "W":
                return Direction.NORTHWEST;
            case "E":
            default:
                return Direction.NORTH;
        }

    }

    private void displayMoveOptions() {
        if(turnsToDelay > 0) {
            System.out.println("You are still delayed by " + turnsToDelay + " turns");
            System.out.println("Options: (D) Pass Turn, (Q) Quit Game");
        } else {
            System.out.println("Please Select A Command");
            System.out.println("Movement Options: (E) North, (R) North East, (F) East, (V) South East, (C) South, (X) South West, (S) West, (W) North West ");
            System.out.println("Other Options: (D) Pass Turn, (Q) Quit Game");
        }
    }
}
