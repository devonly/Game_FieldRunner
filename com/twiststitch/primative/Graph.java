package com.twiststitch.primative;

import com.twiststitch.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Graph {

    public static int graphWidth = 980;
    public static int graphHeight = 720;
    public final static int nodeCount = 20;
    public final static int maxTerrainTraversalDifficulty = 9;
    public final static int minNodeSeparationDistance = 50;
    public final static int minNodeEdgeCount = 3;

    protected ArrayList<Node> nodes;
    protected ArrayList<Edge> edges;

    private Entity player;

    public Graph() {
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

        for (int count = 0; count < nodeCount; count++ ) {
            referenceNode = new Node( (int)( graphWidth * Math.random() ), (int)(graphHeight * Math.random()) );
            nodes.add(referenceNode);
        }

        createRandomEdges();
//        createDirectedEdges();
        enforceMinimalEdges(minNodeEdgeCount);
        separateNodes();
    }

    /***
     * Ensures two nodes don't overlap or touch each other OR if two nodes and less than minimum distance
     * seperates them to a minimum distance if they do
     */
    public void separateNodes() {
        boolean continueSeparation = true;
        double angle = 0;

        // keep searching through nodes until each one is separated
        while (continueSeparation) {

            continueSeparation = false; // assume false unless we have to fix / seperate a node

            for (Node node : nodes) {
                for (Node otherNode : nodes) {
                    if (node != otherNode) {

                        // keep nodes away from edge of view (assume 10 pixel clearance
                        if ( node.position.x <= node.size ) node.position.x = 10;
                        else if ( node.position.x > (graphWidth - 10) ) node.position.x = graphWidth - 10;

                        if ( node.position.y <= node.size ) node.position.y = 10;
                        else if ( node.position.y > (graphHeight - 10) ) node.position.y = graphHeight - 10;

                        if ( (node.intersect(otherNode) <= 0) || (node.position.distanceTo(otherNode.position) < minNodeSeparationDistance) ) { // if nodes touch seperate them out until they have a minimal distance apart
                            angle = Math.random() * 360; // choose a random direction
                            angle = angle * (Math.PI/180); // convert degrees to radians
                            otherNode.position.x = (int)( node.position.x + (minNodeSeparationDistance * Math.cos(angle)) ); // determine x component
                            otherNode.position.y = (int)( node.position.y + (minNodeSeparationDistance * Math.sin(angle)) ); // determine y component
                            continueSeparation = true; // change back to false to force another round of checks
                        }
                    }
                }
            }
        }
    }

    /***
     * Ensure the each node has a minimal number of edges to other nodes
     *
     * @param minimalEdges The number of edges each node should have
     */
    public void enforceMinimalEdges(int minimalEdges) {
        Optional<Edge> edgeOptional;
        long edgeCount;
        int traversalDistance = 0;

        for (Node node : nodes ) {
            edgeCount = edges.stream().filter(o -> o.referenceNode == node).count();

            while (edgeCount < minimalEdges) {
                // select a random node to link to

                Node targetNode = nodes.get( (int)(Math.random() * nodes.size()) );
                edgeOptional = edges.stream().filter(o -> o.equals(node, targetNode) ).findFirst();

                if ( (node != targetNode) && (!edgeOptional.isPresent()) ) { // ignore the same node or if there is already an edge to the target node
//                traversalDistance = (int) Math.sqrt( Math.pow( (targetNode.position.y - referenceNode.position.y), 2) + Math.pow( (targetNode.position.x - referenceNode.position.x), 2) );
                    edges.add(new Edge(node, targetNode, (int)(Math.random() * maxTerrainTraversalDifficulty) + traversalDistance) );
                    edgeCount++;
                }
            }
        }
    }

    /***
     * Create random set of edges
     */
    public void createRandomEdges() {

        int traversalDistance = 0;
        Node referenceNode = null;
        Node targetNode;
        Edge newEdge;
        Dimension2D adjacentPosition;
        adjacentPosition = new Dimension2D(0,0);

        for (int count = 0; count < nodeCount; count++) {
            referenceNode = nodes.get(count);
            targetNode = nodes.get( (int)(count * Math.random()) );

            if (referenceNode != targetNode) {
//                traversalDistance = (int) Math.sqrt( Math.pow( (targetNode.position.y - referenceNode.position.y), 2) + Math.pow( (targetNode.position.x - referenceNode.position.x), 2) );
                newEdge = new Edge(referenceNode, targetNode, (int)(Math.random() * maxTerrainTraversalDifficulty) + traversalDistance);
                edges.add(newEdge);
            }

        }
    }

    public void createDirectedEdges() {
        Collections.sort(nodes);
        int traversalDistance = 0;
        Node referenceNode, targetNode;
        Edge newEdge;

        for (int i = 0; i < nodes.size(); i++) {
            if ( (i) < (nodes.size()-1)) {
                referenceNode = nodes.get(i);
                targetNode = nodes.get(i+1);
//                traversalDistance = (int) Math.sqrt( Math.pow( (targetNode.position.y - referenceNode.position.y), 2) + Math.pow( (targetNode.position.x - referenceNode.position.x), 2) );
                newEdge = new Edge(referenceNode, targetNode, (int)(Math.random() * maxTerrainTraversalDifficulty) + traversalDistance);
                edges.add(newEdge);
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
        Edge searchEdge = getEdge(referenceNode, targetNode);
        if (searchEdge == null) {
            searchEdge = new Edge(referenceNode, targetNode, traversalCost);
            edges.add(searchEdge);
            return null;
        } else {
            searchEdge.traversalCost = traversalCost;
            return searchEdge;
        }
    }

    public Edge addEdge(Dimension2D referencePosition, Dimension2D targetPosition, int traversalCost) {
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
    public Node getNode(Dimension2D position) {
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
    public Edge getEdge(Dimension2D referencePosition, Dimension2D targetPosition) {
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