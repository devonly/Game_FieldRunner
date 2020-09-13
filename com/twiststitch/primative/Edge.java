package com.twiststitch.primative;

public class Edge implements Comparable<Edge> {

    public Node referenceNode;
    public Node targetNode;
    public int traversalCost;
    public boolean checkFlag;

    public Edge(Node referenceNode, Node targetNode, int traversalCost) {
        this.referenceNode = referenceNode;
        this.targetNode = targetNode;
        this.traversalCost = traversalCost;
        this.checkFlag = false;
    }

    public int compareTo(Edge otherEdge) {
        return Integer.compare(traversalCost, otherEdge.traversalCost);
    }

    public boolean equals(Edge otherEdge) {
        return (traversalCost == otherEdge.traversalCost);
    }

    public boolean equals(Node referenceNode, Node targetNode) {
        return (this.referenceNode == referenceNode && this.targetNode == targetNode);
    }

    public boolean equals(Dimension2D referencePosition, Dimension2D targetPosition) {
        return this.referenceNode.position.equals(referencePosition) && this.targetNode.position.equals(targetPosition);
    }

    public boolean equals(Node referenceNode, Node targetNode, int traversalCost) {
        return (this.referenceNode == referenceNode && this.targetNode == targetNode
                && this.traversalCost == traversalCost);
    }

    public boolean equals(Dimension2D referencePosition, Dimension2D targetPosition, int traversalCost) {
        return this.referenceNode.position.equals(referencePosition) && this.targetNode.position.equals(targetPosition)
                && this.traversalCost == traversalCost;
    }

    public String toString() {
        String tempString = "Edge [";
        tempString += this.referenceNode == null ? "null" : this.referenceNode.toString();
        tempString += this.targetNode == null ? ",null" : "," + this.targetNode.toString();
        tempString += ", Cost=" + traversalCost + "]";
        return tempString;
    }

}