package cz.uhk.graphtheory.model;

public class Circle {
    private Coordinate coordinate;

    public Circle(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
