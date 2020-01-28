package cz.uhk.graphtheory.third;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphConverter;
import cz.uhk.graphtheory.util.GraphGenerator;

public class ThirdActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;


    private static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public ThirdActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        ArrayList<Graph> complementGraphs = createComplementGraphs(BRUSH_SIZE, height, width);

                        Graph splittedGraph = complementGraphs.get(0);
                        Graph splittedGraph2 = complementGraphs.get(1);

                        splittedGraph.getNodes().addAll(splittedGraph2.getNodes());
                        splittedGraph.getEdges().addAll(splittedGraph2.getEdges());
                        splittedGraph.getRedEdgesList().addAll(splittedGraph2.getRedEdgesList());

                        getGraphGeneratedView().setGraph(splittedGraph);
                    }
                    disableListener = true;
                }
            }
        });

    }

    public static ArrayList<Graph> createComplementGraphs(int BRUSH_SIZE, int height, int width){
        //set init bipartitní graf educational fragment
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;

        Graph firstGraph = GraphGenerator.generateGraph(height, width, BRUSH_SIZE, amountOfEdges);
        Graph secondGraph = new Graph(firstGraph);

        //myšlenka - mam graf - projdu všechny body a podívám se jestli jsou propojený se všema bodama
        //pokud s nějakým nejsou přidám je do druhého seznamu (red line listu)
        ArrayList<Coordinate> nodes = firstGraph.getNodes();
        ArrayList<Edge> lines = firstGraph.getEdges();
        ArrayList<Edge> redEdges = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
            for (Edge edge : lines) {
                if (edge.getFrom().equal(coordinate)) {
                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(edge.getTo()))) {
                        alreadyFoundConnection.add(edge.getTo());
                    }
                } else if (edge.getTo().equal(coordinate)) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(edge.getFrom()))) {
                        alreadyFoundConnection.add(edge.getFrom());
                    }
                }
            }
            //pocet nalezenych uzlu může být max. o jedna menší než všechny uzly (sám sebe tam nepřidá)
            if (alreadyFoundConnection.size() != (nodes.size() - 1)) {
                for (Coordinate allNodes : nodes) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes)) && !allNodes.equal(coordinate)) {
                        redEdges.add(new Edge(allNodes, coordinate));
                    }
                }
            }
        }
        firstGraph.setRedEdgesList(redEdges);

        ArrayList<Graph> graphs = GraphConverter.convertGraphsToSplitScreenArray(firstGraph, height);
        Graph splittedGraph = graphs.get(0);
        Graph splittedGraph2 = graphs.get(1);

        splittedGraph2.setEdges(new ArrayList<>());
        splittedGraph.setRedEdgesList(new ArrayList<>());

        ArrayList<Graph> complementGraphs = new ArrayList<>();
        complementGraphs.add(splittedGraph);
        complementGraphs.add(splittedGraph2);

        return complementGraphs;
    }
}
