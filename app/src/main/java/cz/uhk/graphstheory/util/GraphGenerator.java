package cz.uhk.graphstheory.util;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.Map;

public class GraphGenerator {

    /**
     * generate random map
     * @param height dimension x
     * @param width dimension y
     * @return generated map
     */
    public static Map generateMap(int height, int width, int BRUSH_SIZE){
        ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();


        boolean shouldRun = false;
        do {
            float xCoordinate = (float) (Math.random() % height);
            float yCoordinte = (float) (Math.random() % width);
            Coordinate newCoordinate = new Coordinate(xCoordinate, yCoordinte);

            boolean isInOtherCircle = false;
            for (Coordinate coordinate : coordinateArrayList){
                Vector2D vectorOfNewcoordinte = new Vector2D(newCoordinate.x, newCoordinate.y);
                double distance = vectorOfNewcoordinte.distance(new Vector2D(coordinate.x, coordinate.y));
                if (distance < BRUSH_SIZE){
                    isInOtherCircle = true;
                    break;
                }
            }
            if (!isInOtherCircle){
                coordinateArrayList.add(newCoordinate);
            }
            //todo zastavit algoritmus podle poctu bodu, ktere se maji vygenerovat a predat brush size


        }while (shouldRun);
        return null;
    }
}
