package cz.uhk.graphstheory.model;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.Line;

public class Map {
    private ArrayList<Line> lines;
    private ArrayList<Coordinate> circles;


    public Map(ArrayList<Line> lines, ArrayList<Coordinate> circles) {
        this.lines = lines;
        this.circles = circles;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public ArrayList<Coordinate> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<Coordinate> circles) {
        this.circles = circles;
    }
}
