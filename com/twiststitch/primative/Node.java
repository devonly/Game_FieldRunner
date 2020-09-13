package com.twiststitch.primative;

public class Node implements Comparable<Node> {

    public Dimension2D position;
    public double size; // node is envisioned as a circle with radius specified by size

    public Node(Dimension2D position) {
        this.position = position;
    }

    public Node(double x, double y) {
        this.position = new Dimension2D(x,y);
        size = 20; // set default size to 20 if none specified
    }

    public Node(double x, double y, double size) {
        this.position = new Dimension2D(x,y);
        this.size = size;
    }

    public int intersect(Node otherNode) {
        double distanceBetweenNodes = Math.sqrt( (Math.pow((this.position.x - otherNode.position.x),2) + Math.pow((this.position.y - otherNode.position.y),2)) );
        double sumRadius = this.size + otherNode.size;

        if ( distanceBetweenNodes > sumRadius ) {
            return 1; // node are not touching or overlapping
        } else if ( distanceBetweenNodes < sumRadius ) {
            return -1; // nodes are intersecting / overlapping
        } else {
            return 0; // nodes are touching
        }
    }

    /***
     *
     * @param otherNode
     * @return -1 if this node x or y is less than otherNode's x,y
     * Otherwise, 1 if this node x or y is greater than otherNode's x,y
     * Otherwise return 0 when x and y are same in both nodes
     */
    public int compareTo(Node otherNode) {
        if ( (this.position.x < otherNode.position.x) || (this.position.y < otherNode.position.y) ) {
            return -1;
        } else if ( (this.position.x > otherNode.position.x) || (this.position.y > otherNode.position.y) ) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        return new StringBuilder().append(" (").append(position.toString()).append(",s:").append(size).append(")").toString();
    }

}
