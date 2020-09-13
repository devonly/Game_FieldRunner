package com.twiststitch.fieldrunner;

import com.twiststitch.entity.Agent;
import com.twiststitch.entity.Entity;
import com.twiststitch.entity.Player;
import com.twiststitch.game.GameScene;
import com.twiststitch.pathfinding.PathfindingDijkstra;
import com.twiststitch.primative.Dimension2D;
import com.twiststitch.primative.Edge;
import com.twiststitch.primative.Line2D;
import com.twiststitch.primative.Node;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;



public class mainViewController {

    @FXML private Canvas mvCanvas;
    @FXML private TextArea mvGameLog;
    @FXML private Button mvPassTurn;
    @FXML private Button mvQuit;
    @FXML private Button mvRestart;

    private int turnNumber;
    private GameScene mainGameScene;
    final private double drawScale = 1.0; // rescale drawing object
    final private double gameNodeSize = 20.0; // default size of game nodes

    private Image playerImage;
    private Image agentImage;
    private Media backgroundMusic;
    private MediaPlayer musicPlayer;

    // save the current entity whose turn it is
    Entity currentActiveEntity;
    Iterator<Entity> entityIterator;

    @FXML
    public void initialize() {

        mvGameLog.setWrapText(true);
        mvGameLog.appendText("Creating Playing Field...\n");
        mainGameScene = new GameScene();
        mvGameLog.appendText("New Field Created...\n");

        Player newPlayer = new Player("Player 1", mainGameScene, 100, 10);
        mvGameLog.appendText("Adding Player @ Position " + newPlayer.getNodePosition().position.toString() + "\n");
        mainGameScene.addEntity(newPlayer);

        Agent newAgent = new Agent("Enemy 1", mainGameScene, Agent.SearchAlgorithm.DIJKSTRA, 100, 10);
        mvGameLog.appendText("Adding Enemy @ Position " + newAgent.getNodePosition().position.toString() + "\n");
        newAgent.setTraversalEaseFactor(1.5);
        mainGameScene.addEntity(newAgent);

        File playerImageFile = new File("src/character_maleAdventurer.png");
        File agentImageFile = new File("src/character_zombie_attack2.png");
//        File playerImageFile = new File("character_maleAdventurer.png");
//        File agentImageFile = new File("character_zombie_attack2.png");
        playerImage = new Image(playerImageFile.toURI().toString());
        agentImage = new Image(agentImageFile.toURI().toString());

        if (musicPlayer != null ) musicPlayer.dispose();
        backgroundMusic = new Media(new File("src/The_Lights_Galaxia_-_02_-_While_She_Sleeps_Morning_Edit.mp3").toURI().toString());
        musicPlayer = new MediaPlayer(backgroundMusic);
        musicPlayer.play();
        
        turnNumber = 0;
        processTurn();

    }

    ////////////////////////////////////////////////////////////////////////

    public void redrawScene() {
        clearCanvas(mvCanvas.getGraphicsContext2D());
        drawBorder(mvCanvas.getGraphicsContext2D(), Color.DARKSLATEGREY);

        // draw edges
        Dimension2D newStartPoint, newEndPoint;
        for (Edge edge : mainGameScene.getGraph().getEdgeList()) {
            newStartPoint = edge.referenceNode.position.interpolate(edge.targetNode.position, 0.02); // shorten the start of the line
            newEndPoint = edge.referenceNode.position.interpolate(edge.targetNode.position, 0.98); // shorten the end of the line
            drawArrowLine(mvCanvas.getGraphicsContext2D(), String.valueOf(edge.traversalCost), newStartPoint.x, newStartPoint.y,
                    newEndPoint.x, newEndPoint.y, 5, 5, Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK);
        }

        // draw nodes
        for (Node node : mainGameScene.getGraph().getNodeList()) {
            drawCircleFromCenter(mvCanvas.getGraphicsContext2D(), "", node.position.x, node.position.y, 10, true, Color.DARKGRAY, Color.DARKGRAY);
        }

        ArrayList<Agent> agents = new ArrayList<Agent>();

        Double travelRatio;
        Dimension2D tempTraversalPosition = new Dimension2D(0.0, 0.0);
        // draw entities
        for (Entity entity : mainGameScene.getEntities() ) {

            if ( (entity.getTraversalEdge() == null) || (entity.getRemainingTurnsToDelay() == 0) ) {
                tempTraversalPosition.x =  entity.getNodePosition().position.x;
                tempTraversalPosition.y = entity.getNodePosition().position.y;
            } else {
                // draw entity as in between node as a percentage of how many turns left delayed
                travelRatio = 1.0 - ((double)entity.getRemainingTurnsToDelay() / (double)entity.getTraversalEdge().traversalCost);
                tempTraversalPosition.x = entity.getTraversalEdge().referenceNode.position.x;
                tempTraversalPosition.y = entity.getTraversalEdge().referenceNode.position.y;
                tempTraversalPosition = tempTraversalPosition.interpolate(entity.getTraversalEdge().targetNode.position, travelRatio);
            }

            String entityInfo;
            if (entity.getRemainingTurnsToDelay() != 0)
                entityInfo = entity.getName() + "\nHealth:" + entity.getHealth() + "\nEn route, will arrive in " + entity.getRemainingTurnsToDelay() + " turns";
            else entityInfo = entity.getName() + "\nHealth:" + entity.getHealth() + "\nCan Move";

            if (entity.getClass().getSimpleName().equals("Player")) {
                drawImageOffset(mvCanvas.getGraphicsContext2D(), playerImage, entityInfo, tempTraversalPosition.x, tempTraversalPosition.y, 0.5, Color.BLUE);
            } else {
                drawImageOffset(mvCanvas.getGraphicsContext2D(), agentImage, entityInfo, tempTraversalPosition.x, tempTraversalPosition.y, 0.5, Color.RED);
                agents.add((Agent)entity);
            }
        }

        // draw agent target path
        ArrayList<Dimension2D> targetPath;
        for (Agent agent : agents ) {
            targetPath = new ArrayList<Dimension2D>();
            for (Edge edge : ((PathfindingDijkstra)agent.getSearchAlgorithm()).getCalculatedPath() ) {
                if (targetPath.size() == 0) {
                    targetPath.add(edge.referenceNode.position);
                }
                targetPath.add(edge.targetNode.position);
            }
            if (targetPath.size() > 0) drawMultiLine(mvCanvas.getGraphicsContext2D(),"", targetPath, Color.FUCHSIA, Color.YELLOW );
        }

    }

    public void processTurn() {

        if (entityIterator == null) {
            entityIterator = mainGameScene.getEntities().iterator();
        } else if (!entityIterator.hasNext()) { // if no more to iterate to
            entityIterator = mainGameScene.getEntities().iterator(); // move back to the start
            turnNumber++;
        }

        currentActiveEntity = entityIterator.next(); // this assumes list has at least one item

        if (currentActiveEntity.getClass().getSimpleName().equals("Player") && (!currentActiveEntity.isAlive())) { // Game Over
            mvGameLog.setText("////////// YOU DIED! GAME OVER //////////\n");
            mvGameLog.appendText("You managed to survive " + this.turnNumber + " turns\n" );
        } else {
            mvGameLog.appendText("\n////////// TURN NUMBER : " + turnNumber + " //////////\n");
            mvGameLog.appendText(currentActiveEntity.getName() + " Turn @ (" + currentActiveEntity.getNodePosition().position.x
                    + "," + currentActiveEntity.getNodePosition().position.x + ") Health: " + currentActiveEntity.getHealth() + "\n");

            if (currentActiveEntity.getRemainingTurnsToDelay() == 0) mvGameLog.appendText( "Not delayed. Is Available To Move\n" );
            else mvGameLog.appendText( "En Route. Still delayed for " + currentActiveEntity.getRemainingTurnsToDelay() + " turns\n" );

            if (!currentActiveEntity.getClass().getSimpleName().equals("Player")) processEnemyAction();
        }
        redrawScene();
    }

    ////////////////////////////////////////////////////////////////////////

    public void clearCanvas(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0,0, mvCanvas.getWidth(), mvCanvas.getHeight());
    }

    public void drawBorder(GraphicsContext graphicsContext, Color strokeColor) {
        graphicsContext.setStroke(strokeColor);
        graphicsContext.beginPath();
        graphicsContext.moveTo(0.0,0.0);
        graphicsContext.lineTo(mvCanvas.getWidth(), 0.0);
        graphicsContext.lineTo(mvCanvas.getWidth(), mvCanvas.getHeight());
        graphicsContext.lineTo(0.0, mvCanvas.getHeight());
        graphicsContext.lineTo(0.0,0.0);
        graphicsContext.stroke();
    }

    public void drawLine(GraphicsContext graphicsContext,String text, double xStart, double yStart,
                                    double xEnd, double yEnd, Color strokeColor, Color textColor) {
        drawLine(graphicsContext, text, xStart, yStart, xEnd, yEnd, 0, 0, strokeColor, textColor);
    }

    public void drawLine(GraphicsContext graphicsContext,String text, double xStart, double yStart,
                         double xEnd, double yEnd, double lineOffset, double textOffset, Color strokeColor, Color textColor) {

        graphicsContext.setStroke(strokeColor);
        graphicsContext.beginPath();
        if (lineOffset > 0.0) {
            // create line at required offset
            Line2D parallelLine = Geometry.getParallelLine(new Line2D(xStart, yStart, xEnd, yEnd), lineOffset);
            assert parallelLine != null;
            graphicsContext.moveTo(parallelLine.startPoint.x, parallelLine.startPoint.y);
            graphicsContext.lineTo(parallelLine.endPoint.x, parallelLine.endPoint.y);
        } else {
            graphicsContext.moveTo(xStart,yStart);
            graphicsContext.lineTo(xEnd, yEnd);
        }
        graphicsContext.stroke();

        graphicsContext.setFill(textColor);
        Dimension2D startPoint = new Dimension2D(xStart, yStart);
        Dimension2D textPoint = startPoint.interpolate(new Dimension2D(xEnd, yEnd), 0.4);

        if (textOffset > 0.0) {
            Line2D parallelLine = Geometry.getParallelLine(xStart, yStart, textPoint.x, textPoint.y, textOffset);
            graphicsContext.fillText(text, parallelLine.endPoint.x, parallelLine.endPoint.y);
        } else {
            graphicsContext.fillText(text, textPoint.x, textPoint.y);
        }
    }

    public void drawArrowLine(GraphicsContext graphicsContext,String text, double xStart, double yStart,
                              double xEnd, double yEnd, double lineOffset, double textOffset, Color strokeColor, Color arrowColor, Color textColor) {
        double theta_radians = Math.atan2( (yEnd - yStart), (xEnd - xStart));
        drawLine(graphicsContext, text, xStart, yStart, xEnd, yEnd, lineOffset, textOffset, strokeColor, textColor);

        // only need to draw one side of arrow
        double arrow_radians = theta_radians - (150 * (Math.PI/180));

        Line2D arrowLine = Geometry.getParallelLine(xStart, yStart, xEnd, yEnd, textOffset);
        arrowLine.startPoint.x = arrowLine.endPoint.x;
        arrowLine.startPoint.y = arrowLine.endPoint.y;
        arrowLine.endPoint.x = arrowLine.endPoint.x + (10 * Math.cos(arrow_radians));
        arrowLine.endPoint.y = arrowLine.endPoint.y + (10 * Math.sin(arrow_radians));

        drawLine(graphicsContext, "", arrowLine.startPoint.x, arrowLine.startPoint.y, arrowLine.endPoint.x , arrowLine.endPoint.y,
                0.0, 0.0, arrowColor, textColor);
    }

    public void drawArrowLine(GraphicsContext graphicsContext,String text, double xStart, double yStart,
                              double xEnd, double yEnd, Color strokeColor, Color arrowColor, Color textColor) {
        drawArrowLine(graphicsContext, text, xStart, yStart, xEnd, yEnd,0.0, 0.0, strokeColor, arrowColor, textColor);
    }

    public void drawRectangle(GraphicsContext graphicsContext, String text, double xTopLeft, double yTopLeft,
                                double xBottomRight, double yBottomRight,boolean filled, Color strokeColor, Color fillColor) {
        if (filled) {
            graphicsContext.setFill(fillColor);
            graphicsContext.fillRect(xTopLeft, yTopLeft, xBottomRight, yBottomRight);
        }

        graphicsContext.setStroke(strokeColor);
        graphicsContext.strokeRect(xTopLeft, yTopLeft, xBottomRight, yBottomRight);
    }

    public void drawRectangleFromCenter(GraphicsContext graphicsContext, String text, double xCenter, double yCenter,
                                            double xBorderDistance, double yBorderDistance, boolean filled, Color strokeColor, Color fillColor, Color textColor) {
        if (filled) {
            graphicsContext.setFill(fillColor);
            graphicsContext.fillRect( (xCenter - xBorderDistance), (yCenter - yBorderDistance), (2 * xBorderDistance), (2 * yBorderDistance) );
        }

        graphicsContext.setStroke(strokeColor);
        graphicsContext.strokeRect( (xCenter - xBorderDistance), (yCenter - yBorderDistance), (2 * xBorderDistance), (2 * yBorderDistance) );

        graphicsContext.setFill(textColor);
        graphicsContext.fillText(text, xCenter, yCenter);

    }

    public void drawOval(GraphicsContext graphicsContext, String text, double upperLeftX, double upperLeftY,
                            double width, double height, boolean filled, Color strokeColor, Color fillColor ) {

        if ( filled ) {
            graphicsContext.setFill(fillColor);
            graphicsContext.fillOval(upperLeftX, upperLeftY, width, height);
        }

        graphicsContext.setStroke(strokeColor);
        graphicsContext.strokeOval(upperLeftX, upperLeftY, width, height);

        graphicsContext.fillText(text, (upperLeftX + (width/2)) , (upperLeftY + (height/2)) );
    }

    public void drawCircleFromCenter(GraphicsContext graphicsContext, String text, double xPosition, double YPosition,
                                        double radius, boolean filled, Color strokeColor, Color fillColor) {
        drawOval(graphicsContext, text, (xPosition - (radius/2) ), (YPosition - (radius/2) ), (radius), (radius), filled, strokeColor, fillColor );
    }

    public void drawMultiLine(GraphicsContext graphicsContext,String text, ArrayList<Dimension2D> points,
                              Color strokeColor, Color textColor) {

        graphicsContext.setStroke(strokeColor);
        graphicsContext.setFill(strokeColor);

        graphicsContext.beginPath();
        graphicsContext.moveTo(points.get(0).x, points.get(0).y);

        for ( int i = 1; i < points.size(); i++) {
            graphicsContext.lineTo(points.get(i).x, points.get(i).y);
        }
        graphicsContext.stroke();
    }

    public void drawImageOffset(GraphicsContext graphicsContext, Image image, String text, double xPosition, double yPosition, double drawScale, Color textColor) {
        Double halfDrawScale = (drawScale/2);
        graphicsContext.drawImage(image,xPosition - (image.getWidth() * halfDrawScale), yPosition - (image.getHeight() * halfDrawScale),
                image.getWidth() * drawScale, image.getHeight() * drawScale);
        graphicsContext.setFill(textColor);
        graphicsContext.fillText(text, xPosition + (image.getWidth() * halfDrawScale ) , yPosition - (image.getHeight() * (drawScale/8)) );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void mvCanvasHandleMouseEntered(MouseEvent event) {

    }

    @FXML
    public void mvCanvasHandleMouseExited(MouseEvent event) {

    }

    @FXML
    public void mvCanvasHandleMouseMoved(MouseEvent event) {
        Node mouseNode = new Node(event.getX(), event.getY(), 0.5 );
        redrawScene();

        Node matchedNode = null;

        for (Node node :  mainGameScene.getGraph().getNodeList()) {
            if (mouseNode.intersect(node) <= 0) { // if mouse intersects a node
                drawCircleFromCenter(mvCanvas.getGraphicsContext2D(), "Mouse", node.position.x, node.position.y, 6, true, Color.DEEPPINK, Color.DEEPPINK );
                matchedNode = node;
            }
        }

        // draw line connected to current node
        Node finalMatchedNode = matchedNode;
        List<Edge> connectedEdges = mainGameScene.getGraph().getEdgeList().stream().filter(o -> o.referenceNode == finalMatchedNode).collect(Collectors.toList());
        for (Edge edge : connectedEdges) { // highlight lines connected to this node
            drawArrowLine(mvCanvas.getGraphicsContext2D(), String.valueOf(edge.traversalCost), edge.referenceNode.position.x, edge.referenceNode.position.y,
                    edge.targetNode.position.x, edge.targetNode.position.y, Color.ORANGE, Color.ORANGERED, Color.MAGENTA );
        }
    }

    @FXML
    public void mvCanvasHandleMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Node mouseNode = new Node(event.getX(), event.getY(), 0.5 );

            for (Node node :  mainGameScene.getGraph().getNodeList()) {
                if (mouseNode.intersect(node) <= 0) { // if mouse intersects a node
                    processPlayerAction(node);
                }
            }
        }
    }

    @FXML
    public void mvPassTurnHandleClick(MouseEvent event) {
        ((Player)currentActiveEntity).setMoveAction(Player.MoveAction.PASS, null);
        if ( ((Player)currentActiveEntity).performAction() == Entity.ActionResult.PASSED ) {
            processTurn();
        }
        ((Player)currentActiveEntity).performAction();
    }

    @FXML
    public void mvQuitHandleClick(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void mvRestartHandleClick(MouseEvent event) {
        initialize();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void processPlayerAction(Node targetNode) {
        if (targetNode != null) {
            ((Player)currentActiveEntity).setMoveAction(Player.MoveAction.MOVE, targetNode);
            Entity.ActionResult actionResult = ((Player)currentActiveEntity).performAction();
            switch(actionResult) {
                case STILLDELAYED:
                    mvGameLog.appendText(currentActiveEntity.getName() + " : Unable to move still en route for " + currentActiveEntity.getRemainingTurnsToDelay() + " turns\n");
                    break;
                case MOVED:
                    mvGameLog.appendText(currentActiveEntity.getName() + " : Moving to (" + targetNode.position.x + "," + targetNode.position.y + ")\n");
                    mvGameLog.appendText(currentActiveEntity.getName() + " : Will arrive at new location in " + currentActiveEntity.getRemainingTurnsToDelay() + " turns\n");
                    processTurn();
                    break;
                case UNTRAVERSABLE:
                    mvGameLog.appendText(currentActiveEntity.getName() + " : Unable to move to that location\n");
                    break;
            }
        } else {
            System.out.println("Error");
        }
    }

    public void processEnemyAction() {
        Entity.ActionResult actionResult = currentActiveEntity.performAction(); // process the agent's action
        switch(actionResult) {
            case STILLDELAYED:
                mvGameLog.appendText(currentActiveEntity.getName() + " : Unable to move still en route for " + currentActiveEntity.getRemainingTurnsToDelay() + " turn(s)\n");
                break;
            case MOVED:
                mvGameLog.appendText(currentActiveEntity.getName() + " : Moving to (" + currentActiveEntity.getNodePosition().position.x + "," + currentActiveEntity.getNodePosition().position.y + ")\n");
                mvGameLog.appendText(currentActiveEntity.getName() + " : Will arrive at new location in " + currentActiveEntity.getRemainingTurnsToDelay() + " turn(s)\n");
                break;
            case UNTRAVERSABLE:
                mvGameLog.appendText(currentActiveEntity.getName() + " : Unable to move to that location\n");
                break;
            case ATTACKING:
                mvGameLog.appendText(currentActiveEntity.getName() + " : Attacked A Player\n");
                break;
        }
        processTurn();
    }


}
