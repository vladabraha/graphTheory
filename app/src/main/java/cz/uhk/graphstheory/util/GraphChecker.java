package cz.uhk.graphstheory.util;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphChecker {

    public static boolean checkIfGraphContainsCesta(Map map) {
        if (map != null) {

            //Jedu od zacatku a kazdy druhy bod zkontroluju, zdali posledni dva body nejsou nekde uz v seznamu predtim (krome poslednich dvou bodu samozrejme)
            ArrayList<CustomLine> path = map.getRedLineList();
            if (path.size() == 0) return false;
            ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();

            for (int i = 0; i < path.size(); i++) {
                coordinateArrayList.add(path.get(i).getFrom());
                coordinateArrayList.add(path.get(i).getTo());
                if (i > 2 && i % 2 != 1) {
                    for (int k = 0; k < coordinateArrayList.size() - 2; k++) {
                        if (coordinateArrayList.get(k) == path.get(i).getFrom()) {
                            return false;
                        } else if (coordinateArrayList.get(k) == path.get(i).getTo()) {
                            return false;
                        }
                    }
                } else if (i == (path.size()) - 1 && i > 1){
                    for (int k = 0; k < coordinateArrayList.size(); k++) {
                        if (coordinateArrayList.get(k).equal(path.get(i).getFrom()) && k != coordinateArrayList.size()) {
                            return false;
                        } else if (coordinateArrayList.get(k).equal(path.get(i).getTo()) && k != coordinateArrayList.size()) {
                            return false;
                        }
                    }
                }
            }
            if (path.size() > 0) return true;
        }
        return Boolean.parseBoolean(null);
    }

    public static boolean checkIfGraphContainsTah(Map map){
        ArrayList<CustomLine> redLineList = map.getRedLineList();
        if (redLineList.size() == 0) return false;
        ArrayList<CustomLine> alreadyChecked = new ArrayList<>();

        for (CustomLine customLine : redLineList){
            for (CustomLine customLineAlreadyChecked : alreadyChecked){
                if (customLine.isLineSame(customLineAlreadyChecked)){
                    return false;
                }
            }
            alreadyChecked.add(customLine);
        }
        return true;
    }
}
