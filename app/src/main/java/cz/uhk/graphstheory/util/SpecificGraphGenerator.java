package cz.uhk.graphstheory.util;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class SpecificGraphGenerator {

    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
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

        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám novej bod, mám 2 samostný grafy, pridám mezi ne bod a ten propojím s kažodou polovinou - tadá artikulace
        Coordinate newNode = new Coordinate((float) Math.random() * width, (float) Math.random() * height);
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        CustomLine newCustomLine = new CustomLine(newNode, oneNode);
        CustomLine newCustomLine2 = new CustomLine(newNode, secondNode);

        //tohle jenom proto aby to bylo videt
        ArrayList<CustomLine> redLines = new ArrayList<>();
        redLines.add(newCustomLine);
        redLines.add(newCustomLine2);
        nodesToSet.add(newNode);

        //jeste aby to bylo i v normálních čarách
        firstPartOfBipartite.add(newCustomLine);
        firstPartOfBipartite.add(newCustomLine2);


        firstPartOfBipartite.addAll(secondPartOfBipartite);
        return new Map(firstPartOfBipartite, nodesToSet, redLines);
    }

    public static Map createMapWithABridge(int height, int width, int BRUSH_SIZE) {
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

        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám 2 samostatný grafy, spojím je jednou čarou - tadá artikulace
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        CustomLine newCustomLine = new CustomLine(oneNode, secondNode);


        //tohle jenom proto aby to bylo videt
        ArrayList<CustomLine> redLines = new ArrayList<>();
        redLines.add(newCustomLine);
        firstPartOfBipartite.add(newCustomLine);


        firstPartOfBipartite.addAll(secondPartOfBipartite);
        return new Map(firstPartOfBipartite, nodesToSet, redLines);
    }
}
