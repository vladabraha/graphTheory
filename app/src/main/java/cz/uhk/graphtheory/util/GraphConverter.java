package cz.uhk.graphtheory.util;

import java.util.ArrayList;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Map;

public class GraphConverter {

    /**
     * rozdeli 2 grafy na 2 poloviny obrazovky
     * @param firstMap prvni mapa
     * @param height vyska viewportu
     * @return jedna mapa obsahujici obe mapy
     */
    public static Map convertMapsToSplitScreen(Map firstMap, int height){
        int halfHeight = height / 2;
        halfHeight = halfHeight - 100; //tohle je pro posunutí prostoru pro to, aby to nelezlo pod bottomNavigation view

        //rozdeleni prvniho grafu do prvni poloviny obrazovky
        ArrayList<Edge> edges = firstMap.getEdges();
        ArrayList<Edge> redEdges = firstMap.getRedEdgesList();
        ArrayList<Coordinate> nodes = firstMap.getNodes();

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

        Map secondMap = new Map(firstMap);
        //rozdeleni druheho grafu do druhe poloviny obrazovky
        ArrayList<Edge> customLines2 = secondMap.getEdges();
        ArrayList<Edge> redEdges2 = secondMap.getRedEdgesList();
        ArrayList<Coordinate> nodes2 = secondMap.getNodes();

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

        return firstMap;
    }

    public static ArrayList<Map> convertMapsToSplitScreenArray(Map firstMap, int height){
        int halfHeight = height / 2;
        halfHeight = halfHeight - 100; //tohle je pro posunutí prostoru pro to, aby to nelezlo pod bottomNavigation view

        //rozdeleni prvniho grafu do prvni poloviny obrazovky
        ArrayList<Edge> edges = firstMap.getEdges();
        ArrayList<Edge> redEdges = firstMap.getRedEdgesList();
        ArrayList<Coordinate> nodes = firstMap.getNodes();

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

        Map secondMap = new Map(firstMap);
        //rozdeleni druheho grafu do druhe poloviny obrazovky
        ArrayList<Edge> customLines2 = secondMap.getEdges();
        ArrayList<Edge> redEdges2 = secondMap.getRedEdgesList();
        ArrayList<Coordinate> nodes2 = secondMap.getNodes();

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

        ArrayList<Map> maps = new ArrayList<>();
        maps.add(firstMap);
        maps.add(secondMap);
        return maps;
    }

}
