package cz.uhk.graphtheory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;

public class PathGenerator {

    /**
     * z predane mapy vrati nahodne vytvorenou cestu (kazdy vrchol max. jednou)
     *
     * @param graph mapa
     * @return list primek, kterymi prochazi cesta
     */
    public static ArrayList<Coordinate> generatePath(Graph graph) {
        int numberOfNodes = graph.getNodes().size();
        int pathLength = (int) Math.round(Math.random() * numberOfNodes);
        if (pathLength < ((graph.getNodes().size() / 2) + 1))
            pathLength = (graph.getNodes().size() / 2) + 1;

        List<Integer> usedIndexes = new ArrayList<>(pathLength);
        for (int i = 0; i < pathLength; i++) {
            boolean found = false;
            while (!found) {
                int randomIndex = (int) (Math.random() * graph.getNodes().size());
                if (!usedIndexes.contains(randomIndex)) {
                    usedIndexes.add(randomIndex);
                    found = true;
                }
            }
        }
        ArrayList<Coordinate> path = new ArrayList<>();
        ArrayList<Coordinate> circleCoordinates = graph.getNodes();
        for (int i = 0; i < usedIndexes.size(); i++) {
            if (i != 0) {
                path.add(circleCoordinates.get(i - 1));
                path.add(circleCoordinates.get(i));
            }
        }
        return path;
    }

    /**
     * z predaneho grafu vrati nahodne vytvoren tah (kazdy hrana max. jednou)
     *
     * @param graph graf
     * @return list primek, kterymi prochazi tah
     */
    public static ArrayList<Coordinate> generateTrail(Graph graph) {
        ArrayList<Coordinate> nodes = graph.getNodes();
        int numberOfNodes = graph.getNodes().size();

        Random ran = new Random();
        int randomNumberOfredEdges = ran.nextInt(((numberOfNodes * (numberOfNodes - 1)) / 2)); //definice úplného grafu
        if (randomNumberOfredEdges < 2) randomNumberOfredEdges = 2;

        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<Coordinate> tah = new ArrayList<>();

        int randomNode = 0;
        int randomNode2 = 0;
        for (int i = 0; i < randomNumberOfredEdges; i++) {
            boolean find = false;
            boolean isAvailable = true;
            //pokud cyklus doběhne do bodu, ze kterého už není dostupná cesta ven, cyklus skončí
            int run = 0;
            do {
                if (isAvailable) randomNode2 = randomNode;
                isAvailable = true;

                //najdi index, který není stejný
                do {
                    randomNode = (int) (numberOfNodes * Math.random());
                } while (randomNode == randomNode2);

                for (Edge edge : edges) {
                    if (edge.isPointInStartOrEndOfLine(nodes.get(randomNode)) && edge.isPointInStartOrEndOfLine(nodes.get(randomNode2))) {
                        isAvailable = false;
                        break;
                    }
                }
                if (isAvailable) {
                    find = true;
                    edges.add(new Edge(nodes.get(randomNode), nodes.get(randomNode2)));
                    tah.add(nodes.get(randomNode));
                    tah.add(nodes.get(randomNode2));
                }
                run++;
            } while (!find && run < 500);
        }
        return tah;
    }

    public static ArrayList<Coordinate> generateCycle(Graph graph) {
        ArrayList<Coordinate> path;
        do {
            path = generatePath(graph);
        } while (path.size() < (graph.getNodes().size() - 1));

        path.add(path.get(path.size() - 1));
        path.add(path.get(0));
        return path;
    }

    public static Graph createComplementToGraph(Graph firstGraph) {
        ArrayList<Coordinate> nodes = firstGraph.getNodes();
        ArrayList<Edge> edges = firstGraph.getEdges();
        ArrayList<Edge> redEdges = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge.getFrom().equal(coordinate)) {
                    Edge testLine = new Edge(edge.getTo(), coordinate);
                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(edge.getTo())) && redEdges.stream().noneMatch(m -> m.isEdgeSame(testLine))
                            && !coordinate.equal(edge.getTo())) {
                        alreadyFoundConnection.add(edge.getTo());
                    }
                } else if (edge.getTo().equal(coordinate)) {
                    Edge testLine = new Edge(edge.getFrom(), coordinate);
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(edge.getFrom())) && redEdges.stream().noneMatch(m -> m.isEdgeSame(testLine))
                            && !coordinate.equal(edge.getFrom())) {
                        alreadyFoundConnection.add(edge.getFrom());
                    }
                }
            }
            //pocet nalezenych uzlu může být max. o jedna menší než všechny uzly (sám sebe tam nepřidá)
            if (alreadyFoundConnection.size() != (nodes.size() - 1)) {
                for (Coordinate allNodes : nodes) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes)) && !allNodes.equal(coordinate)) {
                        redEdges.add(new Edge(allNodes, coordinate));
                    }
                }
            }
        }
        firstGraph.setRedEdgesList(redEdges);
        firstGraph.setEdges(new ArrayList<>());
        return firstGraph;
    }

    //myšlenka, projedu postupne vrcholy a propojim je jak jdou za sebou a kdyz tam ještě nemaj normální caru z generatoru, tak ho tam taky pridam
    public static Graph createHamiltonGraphWithoutredEdges(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES)
            amountOfNodes = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, 15, amountOfNodes);
        ArrayList<Edge> lines = new ArrayList<>();
        ArrayList<Edge> redEdges = new ArrayList<>();
        ArrayList<Edge> preGeneratedLines = GraphGenerator.generateRandomEdges(nodesToSet);
        ArrayList<Edge> hamiltonredEdges = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++) {
            Edge line;
            Edge redLine;
            if (i < nodesToSet.size() - 1) {
                line = new Edge(nodesToSet.get(i), nodesToSet.get(i + 1));
                redLine = new Edge(nodesToSet.get(i), nodesToSet.get(i + 1));
            } else {
                line = new Edge(nodesToSet.get(i), nodesToSet.get(0));
                redLine = new Edge(nodesToSet.get(i), nodesToSet.get(0));
            }
            lines.add(line);
            hamiltonredEdges.add(redLine);
        }

        for (int j = 0; j < hamiltonredEdges.size(); j++) {
            int finalJ = j;
            if (preGeneratedLines.stream().noneMatch(line -> line.isEdgeSame(hamiltonredEdges.get(finalJ)))) {
                preGeneratedLines.add(lines.get(j));
            }
        }

        return new Graph(preGeneratedLines, nodesToSet, redEdges);
    }

    public static Graph createEulerGraphWithoutredEdges(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, 15, amountOfEdges);
        ArrayList<Edge> lines = new ArrayList<>();
        ArrayList<Edge> redEdges = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i < nodesToSet.size() - 1) {
                lines.add(new Edge(nodesToSet.get(i), nodesToSet.get(i + 1)));
            } else {
                lines.add(new Edge(nodesToSet.get(i), nodesToSet.get(2)));
            }
        }
        if (nodesToSet.size() > 4) {
            lines.add(new Edge(nodesToSet.get(0), nodesToSet.get(2)));
            lines.add(new Edge(nodesToSet.get(2), nodesToSet.get(4)));
            lines.add(new Edge(nodesToSet.get(4), nodesToSet.get(1)));
        }

        return new Graph(lines, nodesToSet, redEdges);
    }
}
