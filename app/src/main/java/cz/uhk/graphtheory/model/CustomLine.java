package cz.uhk.graphtheory.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class CustomLine {
    private Coordinate from;
    private Coordinate to;

    public CustomLine(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    public CustomLine(CustomLine customLine) {
        Coordinate from = customLine.getFrom();
        Coordinate to = customLine.getTo();
        this.from = new Coordinate(from.x, from.y);
        this.to = new Coordinate(to.x, to.y);
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

    public boolean isPointInStartOrEndOfLine(@NotNull Coordinate coordinate){
        if (coordinate.x == from.x && coordinate.y == from.y){
            return true;
        }else return coordinate.x == to.x && coordinate.y == to.y;
    }

    public boolean isLineSame(CustomLine customLine){
        if (from.equal(customLine.getFrom()) || from.equal(customLine.getTo())){
            return to.equal(customLine.getFrom()) || to.equal(customLine.getTo());
        }
        return false;
    }

    // Overriding clone() method of Object class
    @NonNull
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

}
