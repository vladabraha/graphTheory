package cz.uhk.graphstheory;

import org.junit.Assert;
import org.junit.Test;

import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.SpecificGraphGenerator;

public class TestToMerge {

    private final int HEIGHT = 1200;
    private final int WIDTH = 1200;
    private final int BRUSH_SIZE = 15;

    @Test
    public void isGraphSame(){

        Map generatedArticulation = SpecificGraphGenerator.createMapWithArticulation(HEIGHT, WIDTH, BRUSH_SIZE);
        Map generatedBridge = SpecificGraphGenerator.createMapWithABridge(HEIGHT, WIDTH, BRUSH_SIZE);

        String isBridgeValid = GraphChecker.checkIfGraphContainsBridge(generatedBridge);
        String isArticulationValid = GraphChecker.checkIfGraphContainsArticulation(generatedArticulation);

        Assert.assertEquals("Mapa neobsahuje most", "true", isBridgeValid);
        Assert.assertEquals("Mapa neobsahuje artikulaci", "true", isArticulationValid);
    }
}
