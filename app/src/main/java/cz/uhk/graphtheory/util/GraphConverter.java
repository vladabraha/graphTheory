package cz.uhk.graphtheory.util;

import java.util.ArrayList;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;

public class GraphConverter {

    /**
     * rozdeli 2 grafy na 2 poloviny obrazovky
     * @param firstGraph prvni graf
     * @param height vyska viewportu
     * @return jeden graf obsahujici oba grafy
     */
    public static Graph convertGraphsToSplitScreen(Graph firstGraph, int height){
        int halfHeight = height / 2;
        halfHeight = halfHeight - 100; //tohle je pro posunutí prostoru pro to, aby to nelezlo pod bottomNavigation view

        //rozdeleni prvniho grafu do prvni poloviny obrazovky
        ArrayList<Edge> edges = firstGraph.getEdges();
        ArrayList<Edge> redEdges = firstGraph.getRedEdgesList();
        ArrayList<Coordinate> nodes = firstGraph.getNodes();

        for (Coordinate circleCoordinate : nodes){
            if (Float.compare(circleCoordinate.y, (float) halfHeight) > 0) circleCoordinate.y = circleCoordinate.y / 2;
        }

        for (Edge edge : edges){
            Coordinate from = edge.getFrom();
            Coordinate to = edge.getTo();
            if (Float.compare(from.y, (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        for (Edge redLine : redEdges){
            Coordinate from = redLine.getFrom();
            Coordinate to = redLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        Graph secondGraph = new Graph(firstGraph);
        //rozdeleni druheho grafu do druhe poloviny obrazovky
        ArrayList<Edge> customLines2 = secondGraph.getEdges();
        ArrayList<Edge> redEdges2 = secondGraph.getRedEdgesList();
        ArrayList<Coordinate> nodes2 = secondGraph.getNodes();

        for (Coordinate circleCoordinate : nodes2){
            if (Float.compare(circleCoordinate.y , (float) halfHeight) < 0){
                circleCoordinate.y = circleCoordinate.y + halfHeight;
            }
        }

        for (Edge edge : customLines2){
            Coordinate from = edge.getFrom();
            Coordinate to = edge.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        for (Edge redLine : redEdges2){
            Coordinate from = redLine.getFrom();
            Coordinate to = redLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        edges.addAll(customLines2);
        redEdges.addAll(redEdges2);
        nodes.addAll(nodes2);

        return firstGraph;
    }

    public static ArrayList<Graph> convertGraphsToSplitScreenArray(Graph firstGraph, int height){
        int halfHeight = height / 2;
        halfHeight = halfHeight - 100; //tohle je pro posunutí prostoru pro to, aby to nelezlo pod bottomNavigation view

        //rozdeleni prvniho grafu do prvni poloviny obrazovky
        ArrayList<Edge> edges = firstGraph.getEdges();
        ArrayList<Edge> redEdges = firstGraph.getRedEdgesList();
        ArrayList<Coordinate> nodes = firstGraph.getNodes();

        for (Coordinate circleCoordinate : nodes){
            if (Float.compare(circleCoordinate.y, (float) halfHeight) > 0) circleCoordinate.y = circleCoordinate.y / 2;
        }

        for (Edge edge : edges){
            Coordinate from = edge.getFrom();
            Coordinate to = edge.getTo();
            if (Float.compare(from.y, (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        for (Edge redLine : redEdges){
            Coordinate from = redLine.getFrom();
            Coordinate to = redLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        Graph secondGraph = new Graph(firstGraph);
        //rozdeleni druheho grafu do druhe poloviny obrazovky
        ArrayList<Edge> customLines2 = secondGraph.getEdges();
        ArrayList<Edge> redEdges2 = secondGraph.getRedEdgesList();
        ArrayList<Coordinate> nodes2 = secondGraph.getNodes();

        for (Coordinate circleCoordinate : nodes2){
            if (Float.compare(circleCoordinate.y , (float) halfHeight) < 0){
                circleCoordinate.y = circleCoordinate.y + halfHeight;
            }
        }

        for (Edge edge : customLines2){
            Coordinate from = edge.getFrom();
            Coordinate to = edge.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        for (Edge redLine : redEdges2){
            Coordinate from = redLine.getFrom();
            Coordinate to = redLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        ArrayList<Graph> graphs = new ArrayList<>();
        graphs.add(firstGraph);
        graphs.add(secondGraph);
        return graphs;
    }

}
