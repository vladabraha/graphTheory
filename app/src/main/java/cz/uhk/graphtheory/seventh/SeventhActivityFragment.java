package cz.uhk.graphtheory.seventh;

import android.content.Context;
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
import cz.uhk.graphtheory.util.GraphGenerator;

public class SeventhActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;
    private SeventhFragmentActivityCommunicationInterface seventhFragmentActivityCommunicationInterface;

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

                        //myšlenka - pro každý vrchol spočítám všechny čáry, který obsahuji daný uzel a mám jednu hodnotu skore grafu
                        Graph graphToSet = GraphGenerator.generateGraph(height, width, BRUSH_SIZE, amountOfEdges);
                        ArrayList<Coordinate> nodes = graphToSet.getNodes();
                        ArrayList<Edge> lines = graphToSet.getEdges();

                        ArrayList<Integer> graphScore = computeGraphScore(nodes, lines);

                        seventhFragmentActivityCommunicationInterface.onScoreComputed(graphScore);
                        getGraphGeneratedView().setGraph(graphToSet);

                    }
                    disableListener = true;
                }
            }
        });
    }

    @NonNull
    public static ArrayList<Integer> computeGraphScore(ArrayList<Coordinate> nodes, ArrayList<Edge> lines) {
        ArrayList<Integer> graphScore = new ArrayList<>();
        for (Coordinate coordinate : nodes){
            int score = 0;
            for (Edge edge : lines){
                if (edge.isPointInStartOrEndOfLine(coordinate)) score++;
            }
            graphScore.add(score);
        }

        graphScore.sort((o1, o2) -> o2-o1);
        return graphScore;
    }



    @Override
    public void onAttach(@NonNull Context context) {
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
