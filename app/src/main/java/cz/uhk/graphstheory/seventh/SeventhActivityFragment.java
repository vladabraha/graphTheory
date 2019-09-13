package cz.uhk.graphstheory.seventh;

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
import cz.uhk.graphstheory.util.GraphGenerator;

public class SeventhActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;


    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public SeventhActivityFragment() {
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

                        //set init bipartitní graf educational fragment
                        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);

                        //myšlenka - mam body, rozdělím je na polovinu a každý bod z jedné poloviny spojím s každým bodem z druhé poloviny
                        ArrayList<Coordinate> firstPartOfNodes  = new ArrayList<>();
                        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
                        ArrayList<CustomLine> bipartite = new ArrayList<>();

                        for (int i = 0; i < nodesToSet.size(); i++){
                            if (i < (nodesToSet.size() / 2)){
                                firstPartOfNodes.add(nodesToSet.get(i));
                            }else {
                                secondPartOfNodes.add(nodesToSet.get(i));
                            }
                        }

                        for (Coordinate coordinateFirstPart: firstPartOfNodes){
                            for (Coordinate coordinateSecondPart: secondPartOfNodes){
                                CustomLine customLine = new CustomLine(coordinateFirstPart, coordinateSecondPart);
                                bipartite.add(customLine);
                            }
                        }

                        Map mapToSet = new Map(bipartite, nodesToSet);
                        getGraphGeneratedView().setMap(mapToSet);
//                        graphGeneratedView.setRedLineList(PathGenerator.generateCesta(graphGeneratedView.getMap()));
                    }
                    disableListener = true;
                }
            }
        });
    }
}
