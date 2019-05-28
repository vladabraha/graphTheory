package cz.uhk.graphstheory.model;

import cz.uhk.graphstheory.model.Coordinate;

public class Line {
    private Coordinate from;
    private Coordinate to;

    public Line(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    public Coordinate getFrom() {
        return from;
    }

    public void setFrom(Coordinate from) {
        this.from = from;
    }

    public Coordinate getTo() {
        return to;
    }

    public void setTo(Coordinate to) {
        this.to = to;
    }
}
