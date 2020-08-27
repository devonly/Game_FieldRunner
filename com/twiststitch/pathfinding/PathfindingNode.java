package com.twiststitch.pathfinding;

import com.twiststitch.primative.Point2d;

public class PathfindingNode implements Comparable<PathfindingNode> {

    public Point2d position;
    public PathfindingNode previousNode;
    public int traversalCost;

    public PathfindingNode(Point2d position, PathfindingNode previousNode, int traversalCost) {

        this.position = new Point2d(position);
        this.previousNode = previousNode;
        this.traversalCost = traversalCost;

    }

    public int compareTo(PathfindingNode otherPathfindingNode) {
        return Integer.compare(traversalCost, otherPathfindingNode.traversalCost);
    }

    public boolean equals(PathfindingNode otherPathfindingNode) {
        return (traversalCost == otherPathfindingNode.traversalCost);
    }

}
