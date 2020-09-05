package com.twiststitch.game;

import com.twiststitch.primative.Dimension2d;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Field {

//    public final static int width = 64;
//    public final static int height = 16;
    public final static int width = 20;
    public final static int height = 5;
    public final static int maxTerrainTraversalDifficulty = 9;

    protected ArrayList<Node> nodes;
    protected ArrayList<Edge> edges;

    public Field() {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
    }

    /***
     * Creates a field with random traversal cost.
     * All nodes will be connected to each other and
     * will have same traversal cost in both directions.
     *
     */
    public void initializeRandomField() {
        Node referenceNode;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                referenceNode = new Node(x, y);
                nodes.add(referenceNode);
            }
        }

        int traversalCost = 0;
        referenceNode = null;
        Node targetNode;
        Edge newEdge;
        Dimension2d adjacentPosition;
        adjacentPosition = new Dimension2d(0,0);

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                // get the node at x,y
                referenceNode = getNode(new Dimension2d(x,y));

                if (referenceNode == null ) {
                    // !!! node doesn't exist, do something
                }

                // create edges between all adjacent nodes,
                // if an edge doesn't already exist
                for (int yOffset = -1; yOffset < 2; yOffset++) {
                    for (int xOffset = -1; xOffset < 2; xOffset++) {
                        adjacentPosition.x = referenceNode.position.x + xOffset;
                        adjacentPosition.y = referenceNode.position.y + yOffset;

                        // only create an edge if the adjacent position is within field bounds
                        if ( adjacentPosition.x >= 0 && adjacentPosition.x < width
                        && adjacentPosition.y >= 0 && adjacentPosition.y < height ) {
                            targetNode = getNode(adjacentPosition);
                            if (targetNode == null) {
                                // !!! can't find node? do something?
                            } else if (referenceNode != targetNode) { // alternatively we could include an edge to itself but have 0 terrain traversal difficulty
                                newEdge = getEdge(referenceNode, targetNode);

                                // only add new Edge if one doesn't already exist
                                if ( newEdge == null ) {
                                    // creates edges in both direction with same traversal cost
                                    traversalCost = (int)(Math.random() * maxTerrainTraversalDifficulty);
                                    newEdge = new Edge(referenceNode, targetNode, traversalCost);
                                    edges.add(newEdge);
                                    newEdge = new Edge(targetNode, referenceNode, traversalCost);
                                    edges.add(newEdge);
                                }

                            }
                        }
                    }
                }

            }
        }


    }

    /***
     * Check to see if any existing node already has location.
     * If no node with existing location exists then add new node
     *
     * @param node New node to be added
     * @return Return null if new node added. Otherwise, return the node that
     * already exists
     */
    public Node addNode(Node node) {

        Node searchNode = getNode(node.position);

        if (searchNode == null) {
            nodes.add(node);
            return null;
        } else {
            return searchNode;
        }
    }

    /***
     *
     * @param referenceNode
     * @param targetNode
     * @param traversalCost An int representing difficulty it will take to get from reference node
     *                      to target node. 0 represents no cost, the higher the value the longer or
     *                      more difficult it will take to get to target node.
     * @return Returns null if edge was added (due to existing edge with both referenceNode and targetNode did not exist)
     * Otherwise, returns the edge that already exists
     * Note: If edge already exists its traversal cost will be updated to that of traversalCost argument
     */
    public Edge addEdge(Node referenceNode, Node targetNode, int traversalCost) {
        Edge searcbEdge = getEdge(referenceNode, targetNode);
        if (searcbEdge == null) {
            searcbEdge = new Edge(referenceNode, targetNode, traversalCost);
            edges.add(searcbEdge);
            return null;
        } else {
            searcbEdge.traversalCost = traversalCost;
            return searcbEdge;
        }
    }

    public Edge addEdge(Dimension2d referencePosition, Dimension2d targetPosition, int traversalCost) {
        Optional<Edge> searchEdge = edges.stream().filter(o -> o.equals(referencePosition, targetPosition)).findFirst();
        if (searchEdge.isPresent()) {
            return searchEdge.get();
        } else {
            return null;
        }
    }

    /***
     * Find a node from a given location
     *
     * @param position
     * @return Returns null if node with location does not exists. Otherwise, return
     * existing node
     */
    public Node getNode(Dimension2d position) {
        Optional<Node> searchNode = nodes.stream().filter(o -> o.position.equals(position)).findFirst();
        if (searchNode.isPresent()) {
            return searchNode.get();
        } else {
            return null;
        }
    }

    /***
     * Find an edge based on a reference Node and target Node
     *
     * @param referenceNode The current node
     * @param targetNode The next node that current node links to
     * @return Returns null if nodes do not exist in edges list.
     * Otherwise, will return the edge that exists in the edges list.
     */
    public Edge getEdge(Node referenceNode, Node targetNode) {
        Optional<Edge> searchEdge = edges.stream().filter(o -> o.equals(referenceNode, targetNode)).findFirst();
        if (searchEdge.isPresent()) {
            return searchEdge.get();
        } else {
            return null;
        }
    }

    /***
     * Find an edge based on node with reference position and target position.
     *
     * @param referencePosition The position contained within a reference node
     * @param targetPosition The position contained within a target node that the reference node links to
     * @return Returns null if both nodes containing reference and target
     * positions do not exist in edges list.
     * Otherwise, will return the edge that exists in the edges list.
     */
    public Edge getEdge(Dimension2d referencePosition, Dimension2d targetPosition) {
        Optional<Edge> searchEdge = edges.stream().filter(o -> o.equals(referencePosition, targetPosition)).findFirst();
        if (searchEdge.isPresent()) {
            return searchEdge.get();
        } else {
            return null;
        }
    }

    /***
     *
     * @return Return unmodifiable list of edges
     */
    public List<Edge> getEdgeList() {
//        return (ArrayList<Edge>)Collections.unmodifiableList(edges);
        return Collections.unmodifiableList(edges);
    }

    /***
     * Returns an the stored node list.
     *
     * @return Returns unmodifiable list of nodes
     */
    public List<Node> getNodeList() {
//        return (ArrayList<Node>)Collections.unmodifiableList(nodes);
        return Collections.unmodifiableList(nodes);

    }

}