package cz.uhk.graphstheory;


import org.junit.Test;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.util.GraphGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class GraphGenerationTest {

    @Test
    public void testMethodReturnNodes(){

        ArrayList<Coordinate> nodes = GraphGenerator.generateNodes(1000, 1000, 15, 15);

        assertThat(nodes.size(), lessThan(15));
        assertThat(nodes.size(), greaterThan(0));
    }
}
