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
        int pathLength = (int) (Math.random() * map.getCircles().size());
        if (pathLength < 6) pathLength = 6;

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
}
