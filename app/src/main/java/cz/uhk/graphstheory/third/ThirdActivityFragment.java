package cz.uhk.graphstheory.third;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphstheory.abstraction.AbstractFragment;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphConverter;
import cz.uhk.graphstheory.util.GraphGenerator;

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

                        splittedMap.getCircles().addAll(splittedMap2.getCircles());
                        splittedMap.getCustomLines().addAll(splittedMap2.getCustomLines());
                        splittedMap.getRedLineList().addAll(splittedMap2.getRedLineList());

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
        ArrayList<Coordinate> nodes = firstMap.getCircles();
        ArrayList<CustomLine> lines = firstMap.getCustomLines();
        ArrayList<CustomLine> redLines = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
            for (CustomLine customLine : lines) {
                if (customLine.getFrom().equal(coordinate)) {
                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getTo()))) {
                        alreadyFoundConnection.add(customLine.getTo());
                    }
                } else if (customLine.getTo().equal(coordinate)) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getFrom()))) {
                        alreadyFoundConnection.add(customLine.getFrom());
                    }
                }
            }
            //pocet nalezenych uzlu může být max. o jedna menší než všechny uzly (sám sebe tam nepřidá)
            if (alreadyFoundConnection.size() != (nodes.size() - 1)) {
                for (Coordinate allNodes : nodes) {
                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes)) && !allNodes.equal(coordinate)) {
                        redLines.add(new CustomLine(allNodes, coordinate));
                    }
                }
            }
        }
        firstMap.setRedLineList(redLines);

        ArrayList<Map> maps = GraphConverter.convertMapsToSplitScreenArray(firstMap, height);
        Map splittedMap = maps.get(0);
        Map splittedMap2 = maps.get(1);

        splittedMap2.setCustomLines(new ArrayList<>());
        splittedMap.setRedLineList(new ArrayList<>());

        ArrayList<Map> complementMaps = new ArrayList<>();
        complementMaps.add(splittedMap);
        complementMaps.add(splittedMap2);

        return complementMaps;
    }
}
