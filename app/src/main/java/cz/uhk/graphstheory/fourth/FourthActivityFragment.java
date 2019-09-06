package cz.uhk.graphstheory.fourth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphstheory.abstraction.AbstractFragment;
import cz.uhk.graphstheory.common.GraphGeneratedView;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;

public class FourthActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;


    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    private Map mapToSet;
    private boolean setFirst;
    private Map secondMapToSet;
    private boolean shouldStop = false;
    private GraphGeneratedView graphGeneratedView;

    public FourthActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);

        setFirst = true;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {

                        //set init bipartitní graf educational fragment
                        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
                            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        mapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);

                        secondMapToSet = new Map(mapToSet);

                        //myšlenka - mám graf, změním tam jenom souřadnice a znovu vykreslím
                        int randomNumber = (int) Math.round(Math.random() * mapToSet.getCircles().size());
                        for (int i = 0; i < randomNumber; i++) {
                            //vytvořím si náhodnou souřadnici
                            float newXCoordinate = (float) (Math.random() * width);
                            float newYCoordinate = (float) (Math.random() * height);

                            //vezmu náhodný uzel a tomu změním souřadnice + všem elementům se stejnou souřadnicí

                            int randomIndex = (int) Math.round(Math.random() * (secondMapToSet.getCircles().size() - 1));
                            Coordinate oldCoordinate = secondMapToSet.getCircles().get(randomIndex);
                            Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);

                            secondMapToSet.getCircles().set(randomIndex, newCoordinate);
                            ArrayList<CustomLine> customLines = secondMapToSet.getCustomLines();
                            for (int j = 0; j < customLines.size(); j++) {
                                CustomLine customLine = customLines.get(j);
                                if (customLine.getTo().equal(oldCoordinate)) {
                                    CustomLine newCustomLine = new CustomLine(customLine.getFrom(), newCoordinate);
                                    customLines.set(j, newCustomLine);
                                } else if (customLine.getFrom().equal(oldCoordinate)) {
                                    CustomLine newCustomLine = new CustomLine(newCoordinate, customLine.getTo());
                                    customLines.set(j, newCustomLine);
                                }
                            }
                        }
                        graphGeneratedView = getGraphGeneratedView();
                        graphGeneratedView.setMap(mapToSet);
                    }
                    disableListener = true;

                    new Handler().postDelayed(() -> changeGraph(), 5000);
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        shouldStop = true;
    }

    private void changeGraph() {
        if (!shouldStop){
            if (setFirst) {
                setFirst = false;
                graphGeneratedView.setMap(mapToSet);
                new Handler().postDelayed(this::changeGraph, 5000);
            } else {
                setFirst = true;
                graphGeneratedView.setMap(secondMapToSet);
                new Handler().postDelayed(this::changeGraph, 5000);
            }
        }
    }
}
