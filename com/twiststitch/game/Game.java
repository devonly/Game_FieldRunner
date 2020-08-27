package com.twiststitch.game;

import com.twiststitch.entity.Agent;
import com.twiststitch.entity.Entity;
import com.twiststitch.entity.Player;
import com.twiststitch.primative.AnsiColors;

public class Game {

    private int turnNumber;
    private Scene mainField;

    private Entity player;

    public Game() {
        System.out.println("Creating Playing Field...");
        mainField = new Scene();
        System.out.println("New Field Created...");
        mainField.showField();

        System.out.println("Adding Player...");
        Player newPlayer = new Player("Player 1", mainField, 100);
        mainField.addEntity(newPlayer);

        System.out.println("Adding Enemy...");
        Agent newAgent = new Agent("Enemy 1", mainField, Agent.SearchAlgorithm.DIJKSTRA, 100);
        newAgent.setTraversalEaseFactor(1.5);
        mainField.addEntity(newAgent);

        turnNumber = 0;
    }

    public void startGameLoop() {
        boolean continueGame = true;

        while (continueGame) {
            turnNumber++;
            System.out.println("\n<<< Turn Number: " + turnNumber + " >>>");
            mainField.showFieldWithEntities();

            for (Entity entity : mainField.getEntities()) {
                System.out.println("[" + entity.getName() + " Turn]" );

                if (player == null) { player = entity; }

                if (entity.getClass().getSimpleName().equals("Player")) {
                    if ( entity.move() == Entity.MoveAction.QUIT) {
                        continueGame = false;
                        System.out.println("Quiting Game...");
                        return;
                    }
                } else {
                    entity.move();
                }
            }

            continueGame = !endGameConditionMet();
            if (!continueGame) {
                System.out.print("\n\n\n");
//                System.out.println("GAME OVER MAN! GAME OVER!");
//                System.out.println("YOU MANAGED TO SURVIVE " + turnNumber + " turns");
                System.out.println(AnsiColors.ANSI_RED + "GAME OVER MAN! GAME OVER!" + AnsiColors.ANSI_RESET);
                System.out.println(AnsiColors.ANSI_BLUE + "YOU MANAGED TO SURVIVE " + turnNumber + " turns" + AnsiColors.ANSI_RESET);
                mainField.showFieldWithEntities();
            }

        }
    }

    private boolean endGameConditionMet() {
        return !player.isAlive();
    }


}
