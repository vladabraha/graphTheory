package cz.uhk.graphstheory.util;

import android.os.CpuUsageInfo;

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
        int pathLength = (int) (Math.random() * numberOfNodes);
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

    public static ArrayList<Coordinate> generateTah(Map map){
        ArrayList<Coordinate> nodes = map.getCircles();
        int numberOfNodes = map.getCircles().size();
        int randomNumber = (int) ((((numberOfNodes * (numberOfNodes - 1)) /2 )-1) * Math.random());
        ArrayList<CustomLine> customLines = new ArrayList<>();

        ArrayList<Coordinate> tah = new ArrayList<>();


        for (int i = 0; i < randomNumber; i++){
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
}
