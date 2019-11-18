package cz.uhk.graphstheory;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import cz.uhk.graphstheory.eight.EightActivityFragment;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.seventh.SeventhActivityFragment;
import cz.uhk.graphstheory.sixth.SixthActivityFragment;
import cz.uhk.graphstheory.third.ThirdActivityFragment;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.SpecificGraphGenerator;

public class SpecificGraphGeneratorTest {

    private final int HEIGHT = 1200;
    private final int WIDTH = 1200;
    private final int BRUSH_SIZE = 15;

    @Test
    public void secondActivityTest(){
        Map generatedArticulation = SpecificGraphGenerator.createMapWithArticulation(HEIGHT, WIDTH, BRUSH_SIZE);
        Map generatedBridge = SpecificGraphGenerator.createMapWithABridge(HEIGHT, WIDTH, BRUSH_SIZE);

        String isBridgeValid = GraphChecker.checkIfGraphContainsBridge(generatedBridge);
        String isArticulationValid = GraphChecker.checkIfGraphContainsArticulation(generatedArticulation);

        Assert.assertEquals("Mapa neobsahuje most", "true", isBridgeValid);
        Assert.assertEquals("Mapa neobsahuje artikulaci", "true", isArticulationValid);
    }

    @Test
    public void thirdActivityTest(){

        ArrayList<Map> complementMaps = ThirdActivityFragment.createComplementGraphs(BRUSH_SIZE, HEIGHT, WIDTH);
        Map mapGenerated = complementMaps.get(0);
        mapGenerated.setRedLineList(mapGenerated.getCustomLines());

        Map userGraph = complementMaps.get(1);
        userGraph.setCustomLines(userGraph.getRedLineList());

        boolean isValid = GraphChecker.checkIfGraphIsComplementGraph(complementMaps.get(0), mapGenerated);
        Assert.assertTrue("Doplněk do grafu není správný", isValid);

    }

    @Test
    public void fourthActivityTest(){
        //neni co testovat
    }

    @Test
    public void fifthActivityTest(){
        Map map = SpecificGraphGenerator.generateBipartiteGraph(HEIGHT, WIDTH, BRUSH_SIZE);
        boolean isValid = GraphChecker.checkIfGraphIsBipartite(map);
        Assert.assertTrue("Bipartitní graf se negeneruje správně", isValid);
    }

    @Test
    public void sixthActivityTest(){

        ArrayList<Coordinate> nodes = GraphGenerator.generateNodes(HEIGHT ,WIDTH,BRUSH_SIZE,12);

        Map hamiltonMap = SixthActivityFragment.createHamiltonMap(nodes);
        Map eulerMap = SixthActivityFragment.createEulerMap(nodes);

        String isEulerMapValid = GraphChecker.checkIfGraphHasEulerPath(eulerMap);
        String isHamiltonMapValid = GraphChecker.checkIfGraphContainsHamiltonCircle(hamiltonMap);

        Assert.assertEquals("eulerova mapa neni vygenerována špatně", isEulerMapValid,"true");
        Assert.assertEquals("hamiltonova mapa neni vygenerována špatně", isHamiltonMapValid,"true");

    }

    @Test
    public void seventhActivityTest(){

        Map map = GraphGenerator.generateMap(HEIGHT ,WIDTH,BRUSH_SIZE,12);
        ArrayList<Integer> graphScore = SeventhActivityFragment.computeGraphScore(map.getCircles(), map.getCustomLines());

        boolean isValid = GraphChecker.checkIfGraphHasCorrectScore(map, graphScore);
        Assert.assertTrue("Skore grafu se generuje špatně", isValid);

    }

    @Test
    public void eightActivityTest(){

        Map mapWithForrest = EightActivityFragment.generateForrest(BRUSH_SIZE,HEIGHT,WIDTH);
        Map mapWithTree = EightActivityFragment.generateTree(BRUSH_SIZE,HEIGHT,WIDTH);

        boolean isForrestValid = GraphChecker.checkIfGraphHasCertainAmountOfComponent(mapWithForrest, 2);
        boolean isTreeValid = GraphChecker.checkIfGraphHasCertainAmountOfComponent(mapWithTree, 1);

        Assert.assertTrue("Les se nevygeneroval se 2 komponentami", isForrestValid);
        Assert.assertTrue("Strom se nevygeneroval s 1 komponentou", isTreeValid);
    }
}
