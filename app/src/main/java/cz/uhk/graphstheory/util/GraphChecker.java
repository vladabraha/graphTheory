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

    public static boolean checkIfGraphContainsCycle(Map map){
        ArrayList<CustomLine> redLineList = map.getRedLineList();
        if (redLineList.size() < 2) return false;
        CustomLine startingLine = redLineList.get(0);
        CustomLine lastLine = redLineList.get(redLineList.size() - 1);
        if (startingLine.getFrom().equal(lastLine.getTo())){
            return true;
        }else return startingLine.getFrom().equal(lastLine.getFrom());
    }

    public static boolean chechIfGraphIsBipartite(Map map){
        ArrayList<CustomLine> customLines = map.getCustomLines();
        ArrayList<CustomLine> customLinesCloned = (ArrayList<CustomLine>) map.getCustomLines().clone();
        ArrayList<Coordinate> alreadyFound = new ArrayList<>();
        ArrayList<Coordinate> circles = map.getCircles();

        if (customLines.size() < 2) return false;

        //vezmi prvni primku, hod si z ni body do seznamu
        //projdi vsechny primky a koukni se, jestli maji jeden bod koncici v nekterem z techto nodu
        //pokud ano, odstran tutu primku z dalsiho hledani a nove propojujici bod pridej do seznamu (pouze pokud tam jeste neni)
        //na konci porovnej, zdali je seznam s body stejne velky jako puvodni, pokud ano, neni tam zadny bipartnitni, jinak je
        alreadyFound.add(customLines.get(0).getFrom());
        alreadyFound.add(customLines.get(0).getTo());
        customLinesCloned.remove(0);

        boolean shouldRun;
        boolean shouldBreak; //this is for currentModificationException
        do {
            shouldBreak = false;
            for (CustomLine customLine : customLinesCloned){
                for (Coordinate coordinate : alreadyFound){
                    if (customLine.getFrom().equal(coordinate)){ //pokud je bod uz v nasem prvotnim seznamu
                           //kontrola, ze bod jeste nemame v seznamu
                        boolean isItNewNode = true;
                        for (Coordinate coordinateAlreadyAdded : alreadyFound){
                            if (coordinateAlreadyAdded.equal(customLine.getTo())){
                                isItNewNode = false;
                            }
                        }
                        customLinesCloned.remove(customLine); //smazani, abychom to nehledali do nekonecna, hledame od prazdneho seznamu
                        shouldBreak = true;
                        if (isItNewNode){ //pokud nemame, tak ho pridej do seznamu
                            alreadyFound.add(customLine.getTo());
                            break;
                        }
                    }else if(customLine.getTo().equal(coordinate)){
                        boolean isItNewNode = true;
                        for (Coordinate coordinateAlreadyAdded : alreadyFound){
                            if (coordinateAlreadyAdded.equal(customLine.getFrom())){
                                isItNewNode = false;
                            }
                        }
                        if (isItNewNode){
                            alreadyFound.add(customLine.getFrom());
                            break;
                        }
                        customLinesCloned.remove(customLine); //smazani, abychom to nehledali do nekonecna, hledame od prazdneho seznamu
                        shouldBreak = true;
                    }
                }
                if (shouldBreak) break;
            }
            //check if list of lines contains any point heading to our list of nodes
            //if not, algorithm stops
            shouldRun = false;
            for (CustomLine customLine : customLinesCloned){
                for (Coordinate coordinate : alreadyFound){
                    if (customLine.getTo().equal(coordinate) || customLine.getFrom().equal(coordinate)){
                        shouldRun = true;
                        break;
                    }
                }
                if (shouldRun) break;
            }
        }while (shouldRun);

        return alreadyFound.size() != circles.size();
    }
}
