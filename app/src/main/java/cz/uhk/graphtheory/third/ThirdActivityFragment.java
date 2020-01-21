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
import cz.uhk.graphtheory.model.Map;
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
                        ArrayList<Map> complementGraphs = createComplementGraphs(BRUSH_SIZE, height, width);

                        Map splittedMap = complementGraphs.get(0);
                        Map splittedMap2 = complementGraphs.get(1);

                        splittedMap.getNodes().addAll(splittedMap2.getNodes());
                        splittedMap.getEdges().addAll(splittedMap2.getEdges());
                        splittedMap.getRedEdgesList().addAll(splittedMap2.getRedEdgesList());

                        getGraphGeneratedView().setMap(splittedMap);
                    }
                    disableListener = true;
                }
            }
        });

    }

    public static ArrayList<Map> createComplementGraphs(int BRUSH_SIZE, int height, int width){
        //set init bipartitní graf educational fragment
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;

        Map firstMap = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);
        Map secondMap = new Map(firstMap);

        //myšlenka - mam graf - projdu všechny body a podívám se jestli jsou propojený se všema bodama
        //pokud s nějakým nejsou přidám je do druhého seznamu (red line listu)
        ArrayList<Coordinate> nodes = firstMap.getNodes();
        ArrayList<Edge> lines = firstMap.getEdges();
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
        firstMap.setRedEdgesList(redEdges);

        ArrayList<Map> maps = GraphConverter.convertMapsToSplitScreenArray(firstMap, height);
        Map splittedMap = maps.get(0);
        Map splittedMap2 = maps.get(1);

        splittedMap2.setEdges(new ArrayList<>());
        splittedMap.setRedEdgesList(new ArrayList<>());

        ArrayList<Map> complementMaps = new ArrayList<>();
        complementMaps.add(splittedMap);
        complementMaps.add(splittedMap2);

        return complementMaps;
    }
}
