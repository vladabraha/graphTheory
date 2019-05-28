package cz.uhk.graphstheory;

import java.util.ArrayList;

public class Map {
    private ArrayList<Line> lines;
    private ArrayList<Circle> circles;


    public Map(ArrayList<Line> lines, ArrayList<Circle> circles) {
        this.lines = lines;
        this.circles = circles;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<Circle> circles) {
        this.circles = circles;
    }
}
