package com.twiststitch.game;

import com.twiststitch.entity.Agent;
import com.twiststitch.entity.Entity;
import com.twiststitch.pathfinding.PathfindingDijkstra;
import com.twiststitch.primative.AnsiColors;
import com.twiststitch.primative.Point2d;

import java.util.ArrayList;

public class Scene {
    public final static int width = 64;
    public final static int height = 16;
    public final static int maxTerrainTraversalDifficulty = 9;
    protected ArrayList<Entity> entities;

    protected int[][] field;

    public Scene() {
        field = new int[width][height];
        entities = new ArrayList<Entity>();
        initializeField();
    }

    private void initializeField() {
        for (int y = 0; y < Scene.height; y++) {
            for (int x = 0; x < Scene.width; x++) {
                this.field[x][y] = (int) (Math.random() * maxTerrainTraversalDifficulty);
            }
        }
    }

    public void showField() {
        for (int y = 0; y < Scene.height; y++) {
            System.out.print("\n");
            for (int x = 0; x < Scene.width; x++) {
                System.out.print(this.field[x][y]);
            }
        }
        System.out.print("\n");
    }

    public void showFieldWithEntities() {

        String[][] displayField = new String[width][height];
        Point2d entityPosition;

        for (int x = 0; x < Scene.width; x++) {
            for (int y = 0; y < Scene.height; y++) {
                displayField[x][y] = String.valueOf(field[x][y]);
            }
        }

        for (Entity entity : entities) {

            entityPosition = entity.getPosition();
            if (entity.getClass().getSimpleName().equals("Player")) {
                System.out.print(entity.getName() + "@ (" + entityPosition.x + "," + entityPosition.y + ")");
                if (entity.getTurnsToDelay() > 0 ) { System.out.print(" : Delayed By " + entity.getTurnsToDelay()); }
                System.out.print("\n");
                displayField[entityPosition.x][entityPosition.y] = AnsiColors.ANSI_CYAN + "#" + AnsiColors.ANSI_RESET ;
//                displayField[entity.position.x][entity.position.y] = "#";
            } else {
                System.out.print(entity.getName() + "@ (" + entityPosition.x + "," + entityPosition.y + ")");
                if (entity.getTurnsToDelay() > 0 ) { System.out.print(" : Delayed By " + entity.getTurnsToDelay()); }
                System.out.print("\n");
                if ( ((Agent)entity).getSearchAlgorithm().getClass().getSimpleName().equals("PathfindingDijkstra") ) {
                    for (Point2d position : ((PathfindingDijkstra)((Agent)entity).getSearchAlgorithm()).getCalculatedPath() ) {
                        displayField[position.x][position.y] = AnsiColors.ANSI_YELLOW + "." + AnsiColors.ANSI_RESET ;
//                        displayField[position.x][position.y] = ".";
                    }
                }

                displayField[entityPosition.x][entityPosition.y] = AnsiColors.ANSI_RED + "X" + AnsiColors.ANSI_RESET ;
//                displayField[entity.position.x][entity.position.y] = "X";
            }
        }

        for (int y = 0; y < Scene.height; y++) {
            for (int x = 0; x < Scene.width; x++) {
                System.out.print(displayField[x][y]);
            }
            System.out.print("\n");
        }

    }

    public int getTerrainTraversalDifficulty(Point2d position) {
        return field[position.x][position.y];
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public int[][] getField() {
        return this.field;
    }

    public int getFieldTraversalCost(int x, int y) {
        return this.field[x][y];
    }

}
