package com.twiststitch.game;

import com.twiststitch.entity.Entity;
import com.twiststitch.primative.Graph;
import java.util.ArrayList;

public class GameScene {

    protected ArrayList<Entity> entities;
    protected Graph graph;

    public GameScene() {
        graph = new Graph();
        graph.initializeRandomField();
        entities = new ArrayList<Entity>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Graph getGraph() {
        return this.graph;
    }


}
