package com.example.graphstheory;

public class Line {
    private Circle from;
    private Circle to;

    public Line(Circle from, Circle to) {
        this.from = from;
        this.to = to;
    }

    public Circle getFrom() {
        return from;
    }

    public void setFrom(Circle from) {
        this.from = from;
    }

    public Circle getTo() {
        return to;
    }

    public void setTo(Circle to) {
        this.to = to;
    }
}
