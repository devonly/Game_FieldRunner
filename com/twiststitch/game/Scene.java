package com.twiststitch.game;

import com.twiststitch.entity.Agent;
import com.twiststitch.entity.Entity;
import com.twiststitch.pathfinding.PathfindingDijkstra;
import com.twiststitch.primative.AnsiColors;
import com.twiststitch.primative.Dimension2d;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;

import java.util.ArrayList;

public class Scene {

    protected ArrayList<Entity> entities;
    protected Field playingField;

    final protected String playerChar = String.valueOf((char)0x26CA);
    final protected String enemyChar = String.valueOf((char)0x26D4);


    public Scene() {

        playingField = new Field();
        playingField.initializeRandomField();
        entities = new ArrayList<Entity>();

    }

    public void showField(boolean drawEntities) {

        String[][] displayField = new String[Field.width*2][Field.height*2];

        for (int y = 0; y < (Field.height*2); y++ ) {
            for (int x = 0; x < (Field.width*2); x++ ) {
                displayField[x][y] = " ";
            }
        }

        Dimension2d referencePosition, targetPosition;
        Node referenceNode, targetNode;
        Edge traversalEdge;
        referencePosition = new Dimension2d(0,0);
        targetPosition = new Dimension2d(0,0);

        // RENDER THE FIELD TO THE BUFFER
        for (int y = 0; y < Field.height; y++) {
            for (int x = 0; x < Field.width; x++) {

                referencePosition.x = x;
                referencePosition.y = y;
                referenceNode = playingField.getNode(referencePosition);
                displayField[x*2][y*2] = "*";

                if (referenceNode.position.y == 0) {

                    // if right most position we do not need to draw an edge
                    if (referenceNode.position.x < (Field.width -1) ) {

                        // if first row only draw east edge
                        targetPosition.x = x + 1;
                        targetPosition.y = y;
                        targetNode = playingField.getNode(targetPosition);
                        traversalEdge = playingField.getEdge(referenceNode, targetNode);

                        displayField[(x * 2) + 1][y] = AnsiColors.ANSI_BLUE + String.valueOf(traversalEdge.traversalCost) + AnsiColors.ANSI_RESET;
                    }

                } else {
                    // if not first row edges to north, north-east, east
                    for (int yOffset = -1; yOffset < 1; yOffset++) { //-1,0
                        for (int xOffset = 0; xOffset < 2; xOffset++ ) { //0,1

                            if ( ( (x != Field.width-1) && !((xOffset == 0) && (yOffset == 0)) ) // draw all edges as long as node is not targeting itself & it is not the last node in the row
                                    || ( (x == Field.width-1) && (xOffset == 0) && (yOffset == -1) ) ) {  // for last node in the row only draw north edge
                                targetPosition.x = x + xOffset;
                                targetPosition.y = y + yOffset;

                                targetNode = playingField.getNode(targetPosition);
                                traversalEdge = playingField.getEdge(referenceNode, targetNode);
                                displayField[((x * 2) + xOffset)][((y * 2) + yOffset)] = AnsiColors.ANSI_BLUE + String.valueOf(traversalEdge.traversalCost) + AnsiColors.ANSI_RESET;

                            }

                        }
                    }
                }

            }
        }

        // RENDER ENTITIES TO BUFFER
        Node entityPosition;
        if (drawEntities) {
            for (Entity entity : entities) {

                entityPosition = entity.getPosition();
                if (entity.getClass().getSimpleName().equals("Player")) {

                    System.out.print(entity.getName() + "@ (" + entityPosition.position.x + "," + entityPosition.position.y + ")");
                    if (entity.getTurnsToDelay() > 0 ) { System.out.print(" : Delayed By " + entity.getTurnsToDelay()); }
                    System.out.print("\n");
                    displayField[entityPosition.position.x * 2][entityPosition.position.y * 2] = AnsiColors.ANSI_CYAN + playerChar + AnsiColors.ANSI_RESET ;

                } else {

                    System.out.print(entity.getName() + "@ (" + entityPosition.position.x + "," + entityPosition.position.y + ")");
                    if (entity.getTurnsToDelay() > 0 ) { System.out.print(" : Delayed By " + entity.getTurnsToDelay()); }
                    System.out.print("\n");

                    if ( ((Agent)entity).getSearchAlgorithm().getClass().getSimpleName().equals("PathfindingDijkstra") ) {
                        PathfindingDijkstra pdj = (PathfindingDijkstra)((Agent)entity).getSearchAlgorithm();
                        for (Edge shortestEdge : ((PathfindingDijkstra)((Agent)entity).getSearchAlgorithm()).getCalculatedPath() ) {
//                                displayField[shortestEdge.targetNode.position.x * 2][shortestEdge.targetNode.position.y * 2] = AnsiColors.ANSI_YELLOW + "." + AnsiColors.ANSI_RESET ;
                            displayField[shortestEdge.referenceNode.position.x * 2][shortestEdge.referenceNode.position.y * 2] = AnsiColors.ANSI_YELLOW + "." + AnsiColors.ANSI_RESET ;
                        }
                    }

                    displayField[entityPosition.position.x * 2][entityPosition.position.y * 2] = AnsiColors.ANSI_RED + enemyChar + AnsiColors.ANSI_RESET ;
                }
            }
        }

        // RENDER BUFFER TO SCREEN
        for (int y = 0; y < (Field.height*2) -1; y++) { // draw all rows and edges except edge for last row

            System.out.print(AnsiColors.ANSI_PURPLE);
            if (y % 2 == 0) {
                System.out.print("<<ROW  " + String.format("%02d", (y /2) ) + ">> ");
            } else {
                System.out.print("<<EDGE " + String.format("%02d", (y /2) ) + ">> ");
            }
            System.out.print(AnsiColors.ANSI_RESET);

            for (int x = 0; x < (Field.width*2); x++) {
                System.out.print(displayField[x][y]);
            }
            System.out.print("\n");
        }


    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Field getField() {
        return this.playingField;
    }


}
