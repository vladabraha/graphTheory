package cz.uhk.graphtheory;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import cz.uhk.graphtheory.eight.EightActivityFragment;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.ninth.NinthActivityFragment;
import cz.uhk.graphtheory.seventh.SeventhActivityFragment;
import cz.uhk.graphtheory.sixth.SixthActivityFragment;
import cz.uhk.graphtheory.third.ThirdActivityFragment;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;
import cz.uhk.graphtheory.util.SpecificGraphGenerator;

public class SpecificGraphGeneratorTest {

    private final int HEIGHT = 1200;
    private final int WIDTH = 1200;
    private final int BRUSH_SIZE = 15;

    @Test
    public void firstActivityTest(){
        //todo
    }

    @Test
    public void secondActivityTest(){
        Graph generatedArticulation = SpecificGraphGenerator.createGraphWithArticulation(HEIGHT, WIDTH, BRUSH_SIZE);
        Graph generatedBridge = SpecificGraphGenerator.createGraphWithABridge(HEIGHT, WIDTH, BRUSH_SIZE);

        String isBridgeValid = GraphChecker.checkIfGraphContainsBridge(generatedBridge);
        String isArticulationValid = GraphChecker.checkIfGraphContainsArticulation(generatedArticulation);

        Assert.assertEquals("Mapa neobsahuje most", "true", isBridgeValid);
        Assert.assertEquals("Mapa neobsahuje artikulaci", "true", isArticulationValid);
    }

    @Test
    public void thirdActivityTest(){

        ArrayList<Graph> complementGraphs = ThirdActivityFragment.createComplementGraphs(BRUSH_SIZE, HEIGHT, WIDTH);
        Graph graphGenerated = complementGraphs.get(0);
        graphGenerated.setRedEdgesList(graphGenerated.getEdges());

        Graph userGraph = complementGraphs.get(1);
        userGraph.setEdges(userGraph.getRedEdgesList());

        boolean isValid = GraphChecker.checkIfGraphIsComplementGraph(complementGraphs.get(0), graphGenerated);
        Assert.assertTrue("Doplněk do grafu není správný", isValid);

    }

    @Test
    public void fourthActivityTest(){
        //neni co testovat
    }

    @Test
    public void fifthActivityTest(){
        Graph graph = SpecificGraphGenerator.generateBipartiteGraph(HEIGHT, WIDTH, BRUSH_SIZE);
        boolean isValid = GraphChecker.checkIfGraphIsBipartite(graph);
        Assert.assertTrue("Bipartitní graf se negeneruje správně", isValid);
    }

    @Test
    public void sixthActivityTest(){

        ArrayList<Coordinate> nodes = GraphGenerator.generateNodes(HEIGHT ,WIDTH,BRUSH_SIZE,12);

        Graph hamiltonGraph = SixthActivityFragment.createHamiltonMap(nodes);
        Graph eulerGraph = SixthActivityFragment.createEulerMap(nodes);

        String isEulerMapValid = GraphChecker.checkIfGraphHasEulerPath(eulerGraph);
        String isHamiltonMapValid = GraphChecker.checkIfGraphContainsHamiltonCircle(hamiltonGraph);

        Assert.assertEquals("eulerova mapa neni vygenerována špatně", isEulerMapValid,"true");
        Assert.assertEquals("hamiltonova mapa neni vygenerována špatně", isHamiltonMapValid,"true");

    }

    @Test
    public void seventhActivityTest(){

        Graph graph = GraphGenerator.generateGraph(HEIGHT ,WIDTH,BRUSH_SIZE,12);
        ArrayList<Integer> graphScore = SeventhActivityFragment.computeGraphScore(graph.getNodes(), graph.getEdges());

        boolean isValid = GraphChecker.checkIfGraphHasCorrectScore(graph, graphScore);
        Assert.assertTrue("Skore grafu se generuje špatně", isValid);

    }

    @Test
    public void eightActivityTest(){

        Graph graphWithForrest = EightActivityFragment.generateForrest(BRUSH_SIZE,HEIGHT,WIDTH);
        Graph graphWithTree = EightActivityFragment.generateTree(BRUSH_SIZE,HEIGHT,WIDTH);

        boolean isForrestValid = GraphChecker.checkIfGraphHasCertainAmountOfComponent(graphWithForrest, 2);
        boolean isTreeValid = GraphChecker.checkIfGraphHasCertainAmountOfComponent(graphWithTree, 1);

        Assert.assertTrue("Les se nevygeneroval se 2 komponentami", isForrestValid);
        Assert.assertTrue("Strom se nevygeneroval s 1 komponentou", isTreeValid);
    }

    @Test
    public void ninthActivityTest(){

        Graph userGraph = NinthActivityFragment.generateSpanningTree(BRUSH_SIZE, HEIGHT, WIDTH);
        Graph generatedGraph = new Graph(userGraph);
        generatedGraph.setRedEdgesList(new ArrayList<>());
        String isValid = GraphChecker.checkIfGraphIsSpanningTree(userGraph, generatedGraph);

        Assert.assertEquals("Kostra grafu se vygenerovala špatně", "true", isValid);

    }
}
