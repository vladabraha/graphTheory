package cz.uhk.graphstheory.model;


import java.util.ArrayList;

public class Map {
    private ArrayList<CustomLine> customLines;
    private ArrayList<Coordinate> circles;


    public Map(ArrayList<CustomLine> customLines, ArrayList<Coordinate> circles) {
        this.customLines = customLines;
        this.circles = circles;
    }

    public ArrayList<CustomLine> getCustomLines() {
        return customLines;
    }

    public void setCustomLines(ArrayList<CustomLine> customLines) {
        this.customLines = customLines;
    }

    public ArrayList<Coordinate> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<Coordinate> circles) {
        this.circles = circles;
    }
}
