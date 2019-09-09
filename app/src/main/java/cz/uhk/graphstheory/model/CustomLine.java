package cz.uhk.graphstheory.model;

public class CustomLine {
    private Coordinate from;
    private Coordinate to;

    public CustomLine(Coordinate from, Coordinate to) {
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

    public boolean isPointInStartOrEndOfLine(Coordinate coordinate){
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
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

}
