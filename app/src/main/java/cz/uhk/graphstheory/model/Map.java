package cz.uhk.graphstheory.model;


import java.util.ArrayList;


public class Map {
    private ArrayList<CustomLine> customLines;
    private ArrayList<Coordinate> circles;
    private ArrayList<CustomLine> redLineList = new ArrayList<>();
    private ArrayList<Coordinate> redCircles;


    public Map(ArrayList<CustomLine> customLines, ArrayList<Coordinate> circles) {
        this.customLines = customLines;
        this.circles = circles;
    }

    public Map(ArrayList<CustomLine> customLines, ArrayList<Coordinate> circles, ArrayList<CustomLine> redLineList) {
        this.customLines = customLines;
        this.circles = circles;
        this.redLineList = redLineList;
    }

    public Map(ArrayList<CustomLine> customLines, ArrayList<Coordinate> circles, ArrayList<CustomLine> redLineList, ArrayList<Coordinate> redCircles) {
        this.customLines = customLines;
        this.circles = circles;
        this.redLineList = redLineList;
        this.redCircles = redCircles;
    }



    public Map(Map map) {
        ArrayList<CustomLine> customLinesCopied = new ArrayList<>();
        ArrayList<Coordinate> circlesCopied = new ArrayList<>();
        ArrayList<CustomLine> redLineListCopied = new ArrayList<>();
        ArrayList<Coordinate> redCirclesCopied = new ArrayList<>();

        for (CustomLine customLine : map.customLines) {
            Coordinate from = new Coordinate(customLine.getFrom().x, customLine.getFrom().y);
            Coordinate to = new Coordinate(customLine.getTo().x, customLine.getTo().y);
            customLinesCopied.add(new CustomLine(from, to));
        }

        for (Coordinate coordinate : map.circles) {
            circlesCopied.add(new Coordinate(coordinate.x, coordinate.y));
        }

        for (Coordinate coordinate : map.redCircles) {
            redCirclesCopied.add(new Coordinate(coordinate.x, coordinate.y));
        }

        for (CustomLine customLine : map.redLineList) {
            Coordinate from = new Coordinate(customLine.getFrom().x, customLine.getFrom().y);
            Coordinate to = new Coordinate(customLine.getTo().x, customLine.getTo().y);
            redLineListCopied.add(new CustomLine(from, to));
        }

        this.customLines = customLinesCopied;
        this.circles = circlesCopied;
        this.redLineList = redLineListCopied;
    }

    public ArrayList<CustomLine> getRedLineList() {
        return redLineList;
    }

    public void setRedLineList(ArrayList<CustomLine> redLineList) {
        this.redLineList = redLineList;
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

    public ArrayList<Coordinate> getRedCircles() {
        return redCircles;
    }

    public void setRedCircles(ArrayList<Coordinate> redCircles) {
        this.redCircles = redCircles;
    }
}
