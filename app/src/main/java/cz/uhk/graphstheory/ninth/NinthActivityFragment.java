package cz.uhk.graphstheory.ninth;

import android.content.Context;
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

public class NinthActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;
    private SeventhFragmentActivityCommunicationInterface seventhFragmentActivityCommunicationInterface;

    public NinthActivityFragment() {
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

                        //myšlenka - pro každý vrchol spočítám všechny čáry, který obsahuji daný uzel a mám jednu hodnotu skore grafu
                        Map mapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);
                        ArrayList<Coordinate> nodes = mapToSet.getCircles();
                        ArrayList<CustomLine> lines = mapToSet.getCustomLines();

                        ArrayList<Integer> graphScore = new ArrayList<>();
                        for (Coordinate coordinate : nodes){
                            int score = 0;
                            for (CustomLine customLine : lines){
                                if (customLine.isPointInStartOrEndOfLine(coordinate)) score++;
                            }
                            graphScore.add(score);
                        }

                        graphScore.sort((o1, o2) -> o2-o1);
                        seventhFragmentActivityCommunicationInterface.onScoreComputed(graphScore);
                        getGraphGeneratedView().setMap(mapToSet);

                    }
                    disableListener = true;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            seventhFragmentActivityCommunicationInterface = (SeventhFragmentActivityCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SeventhFragmentActivityCommunicationInterface");
        }
    }

    public interface SeventhFragmentActivityCommunicationInterface {
        public void onScoreComputed(ArrayList<Integer> graphScore);
    }

}
