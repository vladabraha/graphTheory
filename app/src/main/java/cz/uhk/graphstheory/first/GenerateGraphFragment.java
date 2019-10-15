package cz.uhk.graphstheory.first;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.common.GraphGeneratedView;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.GeneratedMapViewModel;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.PathGenerator;


public class GenerateGraphFragment extends Fragment {

    private GraphGeneratedView graphGeneratedView;
    private GeneratedMapViewModel generatedMapViewModel;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        generatedMapViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GeneratedMapViewModel.class);
        return inflater.inflate(R.layout.fragment_generate_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        graphGeneratedView = Objects.requireNonNull(getView()).findViewById(R.id.eductionGraphView);
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        graphGeneratedView.init(metrics);
        if (generatedMapViewModel.getMap() != null) {
            graphGeneratedView.setMap(generatedMapViewModel.getMap());
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {
                        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                        int BRUSH_SIZE = graphGeneratedView.getBrushSize();
                        Map mapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);
                        graphGeneratedView.setMap(mapToSet);
                        graphGeneratedView.setRedLineList(PathGenerator.generatePath(graphGeneratedView.getMap()));
                    }
                    disableListener = true;
                }
            }
        });

        //typ "cervene cary", ktera se nad grafem vykresli
        if (!type.isEmpty()){
            switch (type){
                case "cesta":
                    graphGeneratedView.setRedLineList(PathGenerator.generatePath(graphGeneratedView.getMap()));
                    graphGeneratedView.invalidate();
                    break;
                case "tah":
                    graphGeneratedView.setRedLineList(PathGenerator.generateTrail(graphGeneratedView.getMap()));
                    graphGeneratedView.invalidate();
                    break;
                case "kruznice":
                    graphGeneratedView.setRedLineList(PathGenerator.generateCycle(graphGeneratedView.getMap()));
                    graphGeneratedView.invalidate();
                    break;
            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Map map = graphGeneratedView.getMap();
        generatedMapViewModel.setMap(map);
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
                graphGeneratedView.setRedLineList(PathGenerator.generatePath(graphGeneratedView.getMap()));
                graphGeneratedView.invalidate();
                return 0;
            case "tah":
                graphGeneratedView.setRedLineList(PathGenerator.generateTrail(graphGeneratedView.getMap()));
                graphGeneratedView.invalidate();
                return 0;
            case "kruznice":
                ArrayList<Coordinate> coordinates = PathGenerator.generateCycle(graphGeneratedView.getMap());
                int lenght = Math.round(coordinates.size() / 2);
                graphGeneratedView.setRedLineList(coordinates);
                graphGeneratedView.invalidate();
                return lenght;
        }
        return 0;
    }

}
