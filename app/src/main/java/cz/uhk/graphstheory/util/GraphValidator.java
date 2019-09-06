package cz.uhk.graphstheory.util;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphValidator {

    //todo rename method
    public static boolean checkIfGraphHasDoplnek(Map userGraph) {
        ArrayList<CustomLine> customLines = userGraph.getCustomLines();
        ArrayList<CustomLine> redLines = userGraph.getRedLineList();
        ArrayList<Coordinate> circles = userGraph.getCircles();

        if (circles.size() < 2 || redLines.size() < 1 || customLines.size() < 1) return false;

        ArrayList<CustomLine> redLinesForCheck = new ArrayList<>();
        for (Coordinate coordinate : circles) {
            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
            for (CustomLine customLine : customLines) {
                if (customLine.getFrom().equal(coordinate)) {
                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getTo()))) {
                        alreadyFoundConnection.add(customLine.getTo());
                    }
                } else if (customLine.getTo().equal(coordinate)) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getFrom()))) {
                        alreadyFoundConnection.add(customLine.getFrom());
                    }
                }
            }
            //podivam se na seznam všech bodů a s těma bodama, se kterejma nejsem propojenej udelam linu
            if (alreadyFoundConnection.size() != (circles.size() - 1)) {
                for (Coordinate allNodes : circles) {
                    CustomLine customLineToAdd = new CustomLine(allNodes, coordinate);
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes)) && !allNodes.equal(coordinate)
                            && redLinesForCheck.stream().noneMatch(point -> point.isLineSame(customLineToAdd))) {
                        redLinesForCheck.add(customLineToAdd);
                    }
                }
            }
        }
        //kontrola, zdali mame redlinelist stejnej, jako kdybychom ho vytvareli pro education graph
        for (CustomLine customLineForCheck : redLinesForCheck) {
            boolean isLineInTheList = false;
            for (CustomLine customRedLine : redLines) {
                if (customRedLine.isLineSame(customLineForCheck)) {
                    isLineInTheList = true;
                    break;
                }
            }
            if (!isLineInTheList) return false;
        }
        return true;
    }
}
