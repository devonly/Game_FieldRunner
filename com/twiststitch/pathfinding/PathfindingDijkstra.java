package com.twiststitch.pathfinding;

import com.twiststitch.game.Scene;
import com.twiststitch.primative.Dimension2d;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;
import java.util.*;
import java.util.stream.Collectors;

public class PathfindingDijkstra extends Pathfinding {

    private ArrayList<Edge> shortestPath; // this is the calculated shorted path
    private ArrayList<Edge> traversalPath;

    public PathfindingDijkstra(Scene playingField) {
        super(playingField);
        traversalPath = new ArrayList<Edge>();
        shortestPath = new ArrayList<Edge>();
    }

    /***
     * Returns the next position the agent should move to based on the shortest path found
     *
     * @param startingNode The position where the agent should start search from
     * @param targetNode The position where the target object / player is located
     * @return
     */
    public Node getNextPosition(Node startingNode, Node targetNode) {

        // reset these members variable each time. As paths and traversal costs need
        // to be recalculated
        traversalPath = new ArrayList<Edge>();
        shortestPath = new ArrayList<Edge>();

        // reset the edge checked flag
        for ( Edge edge : playingField.getField().getEdgeList() ) {
            edge.checkFlag = false;
        }

        generateTraversalPath(startingNode);
        calcShortestPath(startingNode, targetNode);
        return shortestPath.get(0).targetNode;
    }

   private void generateTraversalPath(Node startingNode) {

        boolean pathCalculated;
        pathCalculated = false;

        // Add the initial node or nothing else will work
        Edge referenceEdge = new Edge(null, startingNode, 0); // starting node with 0 cost
        traversalPath.add(referenceEdge);

        // perform initial traversal cost calculation and then start recursion through all descendants from the starting node
        calcTraversalCost(startingNode);
        recursiveNodeSearch(playingField.getField().getEdgeList().stream().filter(o -> o.referenceNode == startingNode).collect(Collectors.toList()));
        Collections.sort(traversalPath);
    }

    /***
     * Recusively search through connected edges from ancestor to descendant, calling calcTraversalCost
     *
     * @param edgeSubset A subset of the edges from the playing field of nodes linked to a reference node
     */
    private void recursiveNodeSearch(List<Edge> edgeSubset) {
        // Is there a better way to do this without recursion? Could very expensive with many many nodes
        ListIterator<Edge> connectedEdge = edgeSubset.listIterator();
        Edge nextEdge;

        while (connectedEdge.hasNext()) {
            nextEdge = connectedEdge.next();

            if (nextEdge.checkFlag == false) {
                Node connectedNode;
                connectedNode = nextEdge.targetNode;
                nextEdge.checkFlag = true;
                calcTraversalCost(connectedNode);
                recursiveNodeSearch(playingField.getField().getEdgeList().stream().filter(o -> o.referenceNode == connectedNode).collect(Collectors.toList()));
            }
        }
    }

    /***
     * Search all positions around the reference position
     * and calculates the aggregate traversal cost
     *
     * @param referenceNode the node which the traversal costs will be calculated from
     */
    private void calcTraversalCost(Node referenceNode) { //rename to searchAllEdges

        Dimension2d positionToCheck;
        int referenceTraversalCost = 0;
        int traversalCost;
        Node finalReferenceNode = referenceNode;

        // find the lowest traversal cost where the target node is the reference Node passed through argument
        Optional<Edge> filterTraversalEdgeToReferenceNode = traversalPath.stream().filter(o -> o.targetNode == finalReferenceNode).findFirst();

        if (filterTraversalEdgeToReferenceNode.isPresent()) { // this should always be true unless something has gone wrong
            referenceTraversalCost = filterTraversalEdgeToReferenceNode.get().traversalCost; // get the traversal cost to referenceNode

            // find the edges connected to reference node
            List<Edge> connectedEdges = playingField.getField().getEdgeList().stream().filter(o -> o.referenceNode == finalReferenceNode).collect(Collectors.toList());

            //search through edges in the playing field that are connected to the reference node (where reference node is is the "edge's reference node")
            Optional<Edge> filterEdge;
            Edge tempEdge;
            if (connectedEdges.size() > 0) {
                for (Edge checkEdge : connectedEdges ) {
                    // search through the traversal list to see if we have an edge where the edge we are checking already exists

                    // for each connected edge, check to see if we have already saved a value for traversal by searching the connectedEdges's target node
                    filterEdge = traversalPath.stream().filter(o -> o.targetNode == checkEdge.targetNode).findFirst();

                    if (filterEdge.isPresent()) {
                        // if we already have a traversal path for the target node then see if we can replace with one of lower cost
                        // replace the reference / previous node and traversal cost if new cost is lower
                        tempEdge = filterEdge.get();
                         if ( tempEdge.traversalCost > (referenceTraversalCost + checkEdge.traversalCost) ) {
                            tempEdge.traversalCost = (referenceTraversalCost + checkEdge.traversalCost);
                            tempEdge.referenceNode = referenceNode;
                        }
                    } else {
                        // create new edge
                        traversalPath.add(new Edge(referenceNode, checkEdge.targetNode, (referenceTraversalCost + checkEdge.traversalCost) ));
                    }

                }

            }

        } else {
            // !! UH OH we have a problem
        }
    }

    private void calcShortestPath(Node startingNode, Node targetNode) {

        // do not redo this calcuation if it has already been performed previously
        if (shortestPath.size() > 0) {
            return;
        }

        // 1. start at target node, find all connected edges and traverse backwards selecting edge
        // to previous node with the lowest aggregate cost
        Node previousNode = targetNode;
        Edge lowestCostEdge;
        List<Edge> connectedEdges;

        while (previousNode != startingNode) {
            Node finalPreviousNode = previousNode;
            connectedEdges = traversalPath.stream().filter(o -> o.targetNode == finalPreviousNode).collect(Collectors.toList());
            lowestCostEdge = null;

            for (Edge tempEdge : connectedEdges) {

                if (lowestCostEdge == null) {
                    lowestCostEdge = tempEdge;
                } else {
                    if (tempEdge.traversalCost < lowestCostEdge.traversalCost) {
                        lowestCostEdge = tempEdge;
                    }
                }
            }

            if (lowestCostEdge != null) {
                shortestPath.add(lowestCostEdge);
                previousNode = lowestCostEdge.referenceNode;
            }
        }

        if (shortestPath.size() > 0) {
            Collections.reverse(shortestPath);
        }

        System.out.print("Shortest Path from : " + startingNode.toString() + " -" + targetNode.toString() );
        for (Edge edge : shortestPath) {
            System.out.print(" >> " + edge.toString());
        }
        System.out.println("");
    }

    public ArrayList<Edge> getCalculatedPath() {
        return shortestPath;
    }


}
