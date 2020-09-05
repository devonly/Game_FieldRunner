package com.twiststitch.primative;

public class Node {

    public Dimension2d position;

    public Node(Dimension2d position) {
        this.position = position;
    }

    public Node(int x, int y) {
        this.position = new Dimension2d(x,y);
    }

    public String toString() {
        return " (" + position.toString() + ")";
    }

}
