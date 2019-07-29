package cz.uhk.graphstheory.util;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphChecker {

    public static boolean checkIfGraphContainsCesta(Map map) {

        ArrayList<CustomLine> path = map.getRedLineList();

        int numberOfOccurrence = 0;
        for (CustomLine customLine : path) {
            for (int i = 0; i < path.size(); i++) {
                if (path.get(i).isPointInStartOrEndOfLine(customLine.getFrom())) {
                    numberOfOccurrence++;
                } else if (path.get(i).isPointInStartOrEndOfLine(customLine.getTo())) {
                    numberOfOccurrence++;
                }
            }
        }
        return numberOfOccurrence > 1;
    }
}
