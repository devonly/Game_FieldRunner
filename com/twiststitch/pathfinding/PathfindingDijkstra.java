package com.twiststitch.pathfinding;

import com.twiststitch.game.Scene;
import com.twiststitch.primative.Point2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class PathfindingDijkstra extends Pathfinding {

    private ArrayList<Point2d> calculatedPath; // this is the calculated shorted path
    private ArrayList<PathfindingNode> traversalPath;

    public PathfindingDijkstra(Scene playingField) {
        super(playingField);
        traversalPath = new ArrayList<PathfindingNode>();
        calculatedPath = new ArrayList<Point2d>();
    }

    public Point2d getNextPosition(Point2d startPosition, Point2d targetPosition) {

        traversalPath = new ArrayList<PathfindingNode>();
        calculatedPath = new ArrayList<Point2d>();

        calcTraversalCost(startPosition);
        return reverseTraversal(startPosition, targetPosition);
    }

    private Point2d reverseTraversal(Point2d startPosition, Point2d targetPosition) {

        ArrayList<PathfindingNode> shortestPath  = new ArrayList<PathfindingNode>();

        //1. start at end point
        PathfindingNode startNode;
        PathfindingNode searchNode;
        Optional<PathfindingNode> filterSearchNode = traversalPath.stream().filter(o -> o.position.equals(targetPosition)).findFirst();
        Optional<PathfindingNode> filterFirstNode = traversalPath.stream().filter(o -> o.position.equals(startPosition)).findFirst();


        if (filterSearchNode.isPresent()) {
            searchNode = filterSearchNode.get();
        } else {
            //??should probably do something else, return error perhaps
            System.out.println("Error: cannot find end node");
            searchNode = new PathfindingNode(new Point2d(0,0), null,  0);
        }
        startNode = searchNode;

        if (filterFirstNode.isPresent()) {
            startNode = filterFirstNode.get();
        } else {
            //??should probably do something else, return error perhaps
            System.out.println("Error: cannot find start node");
            startNode = new PathfindingNode(new Point2d(0,0), null,  0);
        }

        // backtrace until we get the next node to move to
        while ( !searchNode.previousNode.equals(startNode) ) {
            searchNode = searchNode.previousNode;
            calculatedPath.add(searchNode.position);
        }

        return searchNode.position;

    }

    private void calcTraversalCost(Point2d currentPosition) {

        boolean pathCalculated;
        pathCalculated = false;

        PathfindingNode referenceNode = new PathfindingNode(currentPosition, null, 0); // starting node with 0 cost
        traversalPath.add(referenceNode);
        Point2d referencePosition = new Point2d(currentPosition);

        int prevNode = -1;

        while (!pathCalculated) {
            searchAllDirections(referencePosition);

            prevNode++;
            if ( prevNode+1 < traversalPath.size()) {
                referencePosition = traversalPath.get(prevNode+1).position;
            } else {
                pathCalculated = true;
            }

        }

        Collections.sort(traversalPath);

    }

    private void searchAllDirections(Point2d referencePosition) {

        Point2d positionToCheck;
        PathfindingNode newNode;
        PathfindingNode referenceNode;
        int tempTraversalCost;

        Optional<PathfindingNode> filterCheckPosition;
        Optional<PathfindingNode> filterReferencePosition = traversalPath.stream().filter(o -> o.position.equals(referencePosition)).findFirst();

        if (filterReferencePosition.isPresent()) {
            referenceNode = filterReferencePosition.get();
        } else {
            System.out.println("Error cannot find reference");
            referenceNode = new PathfindingNode(new Point2d(0,0), null,  0);
        }

        for ( int i = 0; i < 8; i++) {

            switch(i) {
                case 0: // north
                    positionToCheck = new Point2d(referencePosition.x, referencePosition.y-1);
                    break;
                case 1: // north-east
                    positionToCheck = new Point2d(referencePosition.x+1, referencePosition.y-1);
                    break;
                case 2: // east
                    positionToCheck = new Point2d(referencePosition.x+1, referencePosition.y);
                    break;
                case 3: // south-east
                    positionToCheck = new Point2d(referencePosition.x+1, referencePosition.y+1);
                    break;
                case 4: // south
                    positionToCheck = new Point2d(referencePosition.x, referencePosition.y+1);
                    break;
                case 5: // south-west
                    positionToCheck = new Point2d(referencePosition.x-1, referencePosition.y+1);
                    break;
                case 6: // west
                    positionToCheck = new Point2d(referencePosition.x-1, referencePosition.y);
                    break;
                case 7: // north-west
                    positionToCheck = new Point2d(referencePosition.x-1, referencePosition.y-1);
                    break;
                default:
                    positionToCheck = new Point2d(0,0);
                    break;
            }

            if (isPathTraversable(positionToCheck) ) {
                Point2d finalPositionToCheck = positionToCheck;
                filterCheckPosition = traversalPath.stream().filter(o -> o.position.equals(finalPositionToCheck)).findFirst();

                if (filterCheckPosition.isPresent()) {
                    // adjust the node cost and previous node if the cost of the new traversal is less
                    newNode = filterCheckPosition.get();

                    tempTraversalCost = referenceNode.traversalCost + playingField.getField()[positionToCheck.x][positionToCheck.y];
                    if (tempTraversalCost < newNode.traversalCost) {
                        newNode.previousNode = referenceNode;
                        newNode.traversalCost = tempTraversalCost;
                    }

                } else {
                    // add a new traversal node
                    traversalPath.add(
                        new PathfindingNode(positionToCheck, referenceNode,
                referenceNode.traversalCost + playingField.getField()[positionToCheck.x][positionToCheck.y]) );
                }
            }
        }
    }

    private boolean isPathTraversable(Point2d node) {

        if ( node.x < 0 || node.x >= Scene.width || node.y < 0 || node.y >= Scene.height) {
            return false;
        } else {
            return true;
        }

    }

    public ArrayList<Point2d> getCalculatedPath() {
        return calculatedPath;
    }

}
