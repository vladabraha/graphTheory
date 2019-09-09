package cz.uhk.graphstheory.util;

import android.util.Log;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

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
        ArrayList<CustomLine> customLines = firstMap.getCustomLines();
        ArrayList<CustomLine> redLines = firstMap.getRedLineList();
        ArrayList<Coordinate> circles = firstMap.getCircles();

        for (Coordinate circleCoordinate : circles){
            if (Float.compare(circleCoordinate.y, (float) halfHeight) > 0) circleCoordinate.y = circleCoordinate.y / 2;
        }

        for (CustomLine customLine : customLines){
            Coordinate from = customLine.getFrom();
            Coordinate to = customLine.getTo();
            if (Float.compare(from.y, (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        for (CustomLine redLine : redLines){
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
        ArrayList<CustomLine> customLines2 = secondMap.getCustomLines();
        ArrayList<CustomLine> redLines2 = secondMap.getRedLineList();
        ArrayList<Coordinate> circles2 = secondMap.getCircles();

        for (Coordinate circleCoordinate : circles2){
            if (Float.compare(circleCoordinate.y , (float) halfHeight) < 0){
                circleCoordinate.y = circleCoordinate.y + halfHeight;
            }
        }

        for (CustomLine customLine : customLines2){
            Coordinate from = customLine.getFrom();
            Coordinate to = customLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        for (CustomLine redLine : redLines2){
            Coordinate from = redLine.getFrom();
            Coordinate to = redLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        customLines.addAll(customLines2);
        redLines.addAll(redLines2);
        circles.addAll(circles2);

        return firstMap;
    }

    public static ArrayList<Map> convertMapsToSplitScreenArray(Map firstMap, int height){
        int halfHeight = height / 2;
        halfHeight = halfHeight - 100; //tohle je pro posunutí prostoru pro to, aby to nelezlo pod bottomNavigation view

        //rozdeleni prvniho grafu do prvni poloviny obrazovky
        ArrayList<CustomLine> customLines = firstMap.getCustomLines();
        ArrayList<CustomLine> redLines = firstMap.getRedLineList();
        ArrayList<Coordinate> circles = firstMap.getCircles();

        for (Coordinate circleCoordinate : circles){
            if (Float.compare(circleCoordinate.y, (float) halfHeight) > 0) circleCoordinate.y = circleCoordinate.y / 2;
        }

        for (CustomLine customLine : customLines){
            Coordinate from = customLine.getFrom();
            Coordinate to = customLine.getTo();
            if (Float.compare(from.y, (float) halfHeight) > 0){
                from.y = from.y / 2;
            }
            if (Float.compare(to.y , (float) halfHeight) > 0){
                to.y = to.y / 2;
            }
        }

        for (CustomLine redLine : redLines){
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
        ArrayList<CustomLine> customLines2 = secondMap.getCustomLines();
        ArrayList<CustomLine> redLines2 = secondMap.getRedLineList();
        ArrayList<Coordinate> circles2 = secondMap.getCircles();

        for (Coordinate circleCoordinate : circles2){
            if (Float.compare(circleCoordinate.y , (float) halfHeight) < 0){
                circleCoordinate.y = circleCoordinate.y + halfHeight;
            }
        }

        for (CustomLine customLine : customLines2){
            Coordinate from = customLine.getFrom();
            Coordinate to = customLine.getTo();
            if (Float.compare(from.y , (float) halfHeight) < 0){
                from.y = from.y + halfHeight;
            }
            if (Float.compare(to.y , (float) halfHeight) < 0){
                to.y = to.y + halfHeight;
            }
        }

        for (CustomLine redLine : redLines2){
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
