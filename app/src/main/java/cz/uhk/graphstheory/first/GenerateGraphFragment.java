package cz.uhk.graphstheory.first;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphstheory.abstraction.AbstractFragment;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.PathGenerator;


public class GenerateGraphFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private String type = "";

    private static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public GenerateGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {
                        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        Map mapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);
                        getGraphGeneratedView().setMap(mapToSet);
                        getGraphGeneratedView().setRedLineList(PathGenerator.generatePath(getGraphGeneratedView().getMap()));
                    }
                    disableListener = true;
                }
            }
        });

        //typ "cervene cary", ktera se nad grafem vykresli
        if (!type.isEmpty()){
            switch (type){
                case "cesta":
                    getGraphGeneratedView().setRedLineList(PathGenerator.generatePath(getGraphGeneratedView().getMap()));
                    getGraphGeneratedView().invalidate();
                    break;
                case "tah":
                    getGraphGeneratedView().setRedLineList(PathGenerator.generateTrail(getGraphGeneratedView().getMap()));
                    getGraphGeneratedView().invalidate();
                    break;
                case "kruznice":
                    getGraphGeneratedView().setRedLineList(PathGenerator.generateCycle(getGraphGeneratedView().getMap()));
                    getGraphGeneratedView().invalidate();
                    break;
            }
        }
    }


    //zmeni typ vykresleni, predava se jako parametr, protoze nejde volat metodu na view, ktere neni jeste vytvoreno

    /**
     *
     * @param type of generated graph
     * @return size of line if needed
     */

    public int changeEducationGraph(String type) {
       this.type = type;
        switch (type){
            case "cesta":
                getGraphGeneratedView().setRedLineList(PathGenerator.generatePath(getGraphGeneratedView().getMap()));
                getGraphGeneratedView().invalidate();
                return 0;
            case "tah":
                getGraphGeneratedView().setRedLineList(PathGenerator.generateTrail(getGraphGeneratedView().getMap()));
                getGraphGeneratedView().invalidate();
                return 0;
            case "kruznice":
                ArrayList<Coordinate> coordinates = PathGenerator.generateCycle(getGraphGeneratedView().getMap());
                int length = Math.round(coordinates.size() / 2);
                getGraphGeneratedView().setRedLineList(coordinates);
                getGraphGeneratedView().invalidate();
                return length;
        }
        return 0;
    }

}
