package cz.uhk.graphstheory;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.PathGenerator;

public class SpecificGraphGeneratorTest {

    @Test
    public void testGeneratedPath(){
        Map map = GraphGenerator.generateMap(1000, 1000, 15, 12);
        ArrayList<Coordinate> path = PathGenerator.generatePath(map);
        ArrayList<CustomLine> redLines = new ArrayList<>();

        for (int index = 0; index < path.size(); index++){
            if (index > 0){
                redLines.add(new CustomLine(path.get(index - 1), path.get(index)));
            }
            index++;
        }

        map.setRedLineList(redLines);
        boolean isValid = GraphChecker.checkIfGraphContainsPath(map);
        Assert.assertTrue("Path is not generated",isValid);
    }
}
