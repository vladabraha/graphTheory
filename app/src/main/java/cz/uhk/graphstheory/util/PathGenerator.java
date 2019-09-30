package cz.uhk.graphstheory.util;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class PathGenerator {

    //todo prejmenovat metody do anglictiny

    /**
     * z predane mapy vrati nahodne vytvorenou cestu (kazdy vrchol max. jednou)
     * @param map mapa
     * @return list primek, kterymi prochazi cesta
     */
    public static ArrayList<Coordinate> generateCesta(Map map) {
        int numberOfNodes = map.getCircles().size();
        int pathLength = (int) Math.round(Math.random() * numberOfNodes);
        if (pathLength < ((map.getCircles().size() / 2) + 1)) pathLength = (map.getCircles().size() / 2) + 1;

        List<Integer> usedIndexes = new ArrayList<>(pathLength);
        for (int i = 0; i < pathLength; i++) {
            boolean found = false;
            while (!found) {
                int randomIndex = (int) (Math.random() * map.getCircles().size());
                if (!usedIndexes.contains(randomIndex)) {
                    usedIndexes.add(randomIndex);
                    found = true;
                }
            }
        }
        ArrayList<Coordinate> path = new ArrayList<>();
        ArrayList<Coordinate> circleCoordinates = map.getCircles();
        for (int i = 0; i < usedIndexes.size(); i++) {
            if (i != 0) {
                path.add(circleCoordinates.get(i - 1));
                path.add(circleCoordinates.get(i));
            }
        }
        return path;
    }

    /**
     * z predane mapy vrati nahodne vytvoren tah (kazdy hrana max. jednou)
     * @param map mapa
     * @return list primek, kterymi prochazi tah
     */
    public static ArrayList<Coordinate> generateTah(Map map){
        ArrayList<Coordinate> nodes = map.getCircles();
        int numberOfNodes = map.getCircles().size();
        int randomNumberOfRedLines = (int) ((((numberOfNodes * (numberOfNodes - 1)) /2 )-1) * Math.random());
        if (randomNumberOfRedLines < 2) randomNumberOfRedLines = 2;
        ArrayList<CustomLine> customLines = new ArrayList<>();

        ArrayList<Coordinate> tah = new ArrayList<>();

        for (int i = 0; i < randomNumberOfRedLines; i++){
            boolean find = false;
            do{
                boolean isAvailable = true;
                int randomNode = (int) (numberOfNodes * Math.random());
                int randomNode2 = (int) (numberOfNodes * Math.random());
                for (CustomLine customLine : customLines){
                    if (customLine.isPointInStartOrEndOfLine(nodes.get(randomNode)) && customLine.isPointInStartOrEndOfLine(nodes.get(randomNode2))) {
                        isAvailable = false;
                    }
                }
                if (isAvailable){
                    find = true;
                    customLines.add(new CustomLine(nodes.get(randomNode), nodes.get(randomNode2)));
                    tah.add(nodes.get(randomNode));
                    tah.add(nodes.get(randomNode2));
                }
            } while(!find);
        }
        return tah;
    }

    public static ArrayList<Coordinate> generateKruznice(Map map) {
        ArrayList<Coordinate> path;
        do {
            path = generateCesta(map);
        }while (path.size() < (map.getCircles().size() - 1));

        path.add(path.get(path.size() - 1));
        path.add(path.get(0));
       return path;
    }

    public static Map createDoplnekToGraph(Map firstMap) {
        ArrayList<Coordinate> nodes = firstMap.getCircles();
        ArrayList<CustomLine> lines = firstMap.getCustomLines();
        ArrayList<CustomLine> redLines = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
            for (CustomLine customLine : lines) {
                if (customLine.getFrom().equal(coordinate)) {
                    CustomLine testLine = new CustomLine(customLine.getTo(), coordinate);
                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getTo())) && redLines.stream().noneMatch(m -> m.isLineSame(testLine))
                            && !coordinate.equal(customLine.getTo())) {
                        alreadyFoundConnection.add(customLine.getTo());
                    }
                } else if (customLine.getTo().equal(coordinate)) {
                    CustomLine testLine = new CustomLine(customLine.getFrom(), coordinate);
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getFrom())) && redLines.stream().noneMatch(m -> m.isLineSame(testLine))
                            && !coordinate.equal(customLine.getFrom())) {
                        alreadyFoundConnection.add(customLine.getFrom());
                    }
                }
            }
            //pocet nalezenych uzlu může být max. o jedna menší než všechny uzly (sám sebe tam nepřidá)
            if (alreadyFoundConnection.size() != (nodes.size() - 1)) {
                for (Coordinate allNodes : nodes) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes)) && !allNodes.equal(coordinate)) {
                        redLines.add(new CustomLine(allNodes, coordinate));
                    }
                }
            }
        }
        firstMap.setRedLineList(redLines);
        firstMap.setCustomLines(new ArrayList<>());
        return firstMap;
    }

    //myšlenka, projedu postupne vrcholy a propojim je jak jdou za sebou a kdyz tam ještě nemaj normální caru z generatoru, tak ho tam taky pridam
    public static Map createHamiltonMapWithoutRedLines(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, 15, amountOfEdges);
        ArrayList<CustomLine> lines = new ArrayList<>();
        ArrayList<CustomLine> redLines = new ArrayList<>();
        ArrayList<CustomLine> preGeneratedLines = GraphGenerator.generateRandomEdges(nodesToSet);
        ArrayList<CustomLine> hamiltonRedLines = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++) {
            CustomLine line;
            CustomLine redline;
            if (i < nodesToSet.size() - 1) {
                line = new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 1));
                redline = new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 1));
            } else {
                line = new CustomLine(nodesToSet.get(i), nodesToSet.get(0));
                redline = new CustomLine(nodesToSet.get(i), nodesToSet.get(0));
            }
            lines.add(line);
            hamiltonRedLines.add(redline);
        }

        for (int j = 0; j < hamiltonRedLines.size(); j++) {
            int finalJ = j;
            if (preGeneratedLines.stream().noneMatch(line -> line.isLineSame(hamiltonRedLines.get(finalJ)))) {
                preGeneratedLines.add(lines.get(j));
            }
        }

        return new Map(preGeneratedLines, nodesToSet, redLines);
    }

    public static Map createEulerMapWithoutRedLines(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, 15, amountOfEdges);
        ArrayList<CustomLine> lines = new ArrayList<>();
        ArrayList<CustomLine> redLines = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++){
            if (i < nodesToSet.size() - 1){
                lines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i+1)));
            }else {
                lines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(2)));
            }
        }
        if ( nodesToSet.size() > 4){
            lines.add(new CustomLine(nodesToSet.get(0), nodesToSet.get(2)));
            lines.add(new CustomLine(nodesToSet.get(2), nodesToSet.get(4)));
            lines.add(new CustomLine(nodesToSet.get(4), nodesToSet.get(1)));
        }

        return new Map(lines, nodesToSet, redLines);
    }
}
