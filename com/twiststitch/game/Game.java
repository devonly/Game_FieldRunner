package com.twiststitch.game;

import com.twiststitch.entity.Agent;
import com.twiststitch.entity.Entity;
import com.twiststitch.entity.Player;
import com.twiststitch.primative.AnsiColors;

public class Game {

    private int turnNumber;
    private Scene mainScene;

    private Entity player;

    public Game() {
        System.out.println("Creating Playing Field...");
        mainScene = new Scene();
        System.out.println("New Field Created...");

        System.out.println("Adding Player...");
        Player newPlayer = new Player("Player 1", mainScene, 100);
        mainScene.addEntity(newPlayer);

        System.out.println("Adding Enemy...");
        Agent newAgent = new Agent("Enemy 1", mainScene, Agent.SearchAlgorithm.DIJKSTRA, 100);
        newAgent.setTraversalEaseFactor(1.5);
        mainScene.addEntity(newAgent);

        turnNumber = 0;
    }

    public void startGameLoop() {
        boolean continueGame = true;

        while (continueGame) {
            turnNumber++;
            System.out.println("\n<<< Turn Number: " + turnNumber + " >>>");
            mainScene.showField(true);

            for (Entity entity : mainScene.getEntities()) {
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
                mainScene.showField(true);
            }

        }
    }

    private boolean endGameConditionMet() {
        return !player.isAlive();
    }


}
