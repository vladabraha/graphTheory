package cz.uhk.graphtheory.model;

import androidx.annotation.NonNull;

public class Coordinate implements Comparable {
    public float x;
    public float y;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equal(Coordinate coordinate){
        return coordinate.x == this.x && coordinate.y == this.y;
    }

    @Override
    public int compareTo(@NonNull Object objectToCompare) {
        float difference =  this.y - ((Coordinate) objectToCompare).y ;
        return (int) difference;
    }
}
