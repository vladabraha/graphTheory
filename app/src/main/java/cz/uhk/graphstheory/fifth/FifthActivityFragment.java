package cz.uhk.graphstheory.fifth;

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

public class FifthActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;


    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public FifthActivityFragment() {
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

                        //myšlenka - mam body, vezmu polovinu a nějak je spojim
                        //vezmu druhou polovinu a nejak je spojim. Mezi prvni a druhou půlkou neni žádný propoj - bipartitni graf
                        ArrayList<Coordinate> firstPartOfNodes  = new ArrayList<>();
                        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
                        for (int i = 0; i < nodesToSet.size(); i++){
                            if (i < (nodesToSet.size() / 2)){
                                firstPartOfNodes.add(nodesToSet.get(i));
                            }else {
                                secondPartOfNodes.add(nodesToSet.get(i));
                            }
                        }

                        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
                        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

                        firstPartOfBipartite.addAll(secondPartOfBipartite);
                        Map mapToSet = new Map(firstPartOfBipartite, nodesToSet);
                        getGraphGeneratedView().setMap(mapToSet);
//                        graphGeneratedView.setRedLineList(PathGenerator.generateCesta(graphGeneratedView.getMap()));
                    }
                    disableListener = true;
                }
            }
        });
    }
}
