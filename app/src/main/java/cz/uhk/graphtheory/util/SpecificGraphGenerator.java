package cz.uhk.graphtheory.util;

import java.util.ArrayList;
import java.util.Random;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Map;

public class SpecificGraphGenerator {

    private static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public static Map createMapWithArticulation(int height, int width, int BRUSH_SIZE) {
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);

        //myšlenka - mam body, vezmu polovinu a nějak je spojim
        ArrayList<Coordinate> firstPartOfNodes = new ArrayList<>();
        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i < (nodesToSet.size() / 2)) {
                firstPartOfNodes.add(nodesToSet.get(i));
            } else {
                secondPartOfNodes.add(nodesToSet.get(i));
            }
        }

        ArrayList<Edge> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<Edge> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám novej bod, mám 2 samostný grafy, pridám mezi ne bod a ten propojím s kažodou polovinou - tadá artikulace
        Coordinate newNode = new Coordinate((float) Math.random() * width, (float) Math.random() * height);
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        Edge newEdge = new Edge(newNode, oneNode);
        Edge newEdge2 = new Edge(newNode, secondNode);

        //tohle jenom proto aby to bylo videt
        ArrayList<Edge> redEdges = new ArrayList<>();
        nodesToSet.add(newNode);

        //jeste aby to bylo i v normálních čarách
        firstPartOfBipartite.add(newEdge);
        firstPartOfBipartite.add(newEdge2);
        ArrayList<Coordinate> redCircle = new ArrayList<>(1);
        redCircle.add(newNode);

        firstPartOfBipartite.addAll(secondPartOfBipartite);
        return new Map(firstPartOfBipartite, nodesToSet, redEdges, redCircle);
    }

    public static Map createMapWithABridge(int height, int width, int BRUSH_SIZE) {
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfNodes);

        //myšlenka - mam body, vezmu polovinu a nějak je spojim
        ArrayList<Coordinate> firstPartOfNodes = new ArrayList<>();
        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i < (nodesToSet.size() / 2)) {
                firstPartOfNodes.add(nodesToSet.get(i));
            } else {
                secondPartOfNodes.add(nodesToSet.get(i));
            }
        }

        ArrayList<Edge> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<Edge> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám 2 samostatný grafy, spojím je jednou čarou - tadá artikulace
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        Edge newEdge = new Edge(oneNode, secondNode);


        //tohle jenom proto aby to bylo videt
        ArrayList<Edge> redEdges = new ArrayList<>();
        redEdges.add(newEdge);
        firstPartOfBipartite.add(newEdge);


        firstPartOfBipartite.addAll(secondPartOfBipartite);
        return new Map(firstPartOfBipartite, nodesToSet, redEdges);
    }

    //Myšlenka - vytvořím si 2 seznamy vrcholů a ty mezi sebou všechny propojím
    public static Map generateBipartiteGraph(int height, int width, int BRUSH_SIZE){
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> firstPart = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfNodes);
        ArrayList<Coordinate> secondPart = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfNodes);
        ArrayList<Edge> edges = new ArrayList<>();

        for (Coordinate firstPartOfCoordinate : firstPart){
            for (Coordinate secondPartOfCoordinate : secondPart){
                edges.add(new Edge(firstPartOfCoordinate, secondPartOfCoordinate));
            }
        }
        firstPart.addAll(secondPart);
        return new Map(edges, firstPart);
    }

    /**
     * generate graph same as bipartite but without random 2 lines
     * @param height of viewport
     * @param width of viewport
     * @param BRUSH_SIZE size of node
     * @return map to set
     */
    public static Map generateGraphSimilarToBipartiteGraph(int height, int width, int BRUSH_SIZE){
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> firstPart = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfNodes);
        ArrayList<Coordinate> secondPart = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfNodes);
        ArrayList<Edge> edges = new ArrayList<>();

        for (Coordinate firstPartOfCoordinate : firstPart){
            for (Coordinate secondPartOfCoordinate : secondPart){
                edges.add(new Edge(firstPartOfCoordinate, secondPartOfCoordinate));
            }
        }
        for (int i = 0; i < 2; i++){
            Random random = new Random();
            int index = random.nextInt(edges.size() - 1);
            edges.remove(index);
        }
        firstPart.addAll(secondPart);
        return new Map(edges, firstPart);
    }
}
