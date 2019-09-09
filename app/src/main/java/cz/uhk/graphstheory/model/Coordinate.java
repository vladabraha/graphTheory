package cz.uhk.graphstheory.model;

public class Coordinate{
    public float x;
    public float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equal(Coordinate coordinate){
        return coordinate.x == this.x && coordinate.y == this.y;
    }

    // Overriding clone() method of Object class
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
