package cz.uhk.graphstheory.second;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphstheory.abstraction.AbstractFragment;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.GeneratedMapViewModel;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;


public class SecondActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private String type = "";
    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;
    private GeneratedMapViewModel generatedMapViewModel;

    public SecondActivityFragment() {
        // Required empty public constructor
    }

    public SecondActivityFragment(String type) {
        this.type = type;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        generatedMapViewModel = getGeneratedMapViewModel();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {
                        switch (type) {
                            case "artikulace":
                                createArticulation();
                                break;

                            case "most":
                                createBridge();
                                break;
                            default:
                                createArticulation();
                                break;
                        }
                    }
                    disableListener = true;
                }
            }
        });
    }

    private void createArticulation() {
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);

        //myšlenka - mam body, vezmu polovinu a nějak je spojim
        //vezmu druhou polovinu a nejak je spojim. Mezi prvni a druhou půlkou neni žádný propoj - bipartitni graf
        ArrayList<Coordinate> firstPartOfNodes = new ArrayList<>();
        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i < (nodesToSet.size() / 2)) {
                firstPartOfNodes.add(nodesToSet.get(i));
            } else {
                secondPartOfNodes.add(nodesToSet.get(i));
            }
        }

        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám novej bod, mám 2 samostný grafy, pridám mezi ne bod a ten propojím s kažodou polovinou - tadá artikulace
        Coordinate newNode = new Coordinate((float) Math.random() * width, (float) Math.random() * height);
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        CustomLine newCustomLine = new CustomLine(newNode, oneNode);
        CustomLine newCustomLine2 = new CustomLine(newNode, secondNode);

        //tohle jenom proto aby to bylo videt
        ArrayList<CustomLine> redLines = new ArrayList<>();
        redLines.add(newCustomLine);
        redLines.add(newCustomLine2);
        nodesToSet.add(newNode);


        firstPartOfBipartite.addAll(secondPartOfBipartite);
        Map mapToSet = new Map(firstPartOfBipartite, nodesToSet, redLines);
        getGraphGeneratedView().setMap(mapToSet);
        generatedMapViewModel.setMap(mapToSet); //pro viemodel
    }

    private void createBridge() {
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);

        //myšlenka - mam body, vezmu polovinu a nějak je spojim
        //vezmu druhou polovinu a nejak je spojim. Mezi prvni a druhou půlkou neni žádný propoj - bipartitni graf
        ArrayList<Coordinate> firstPartOfNodes = new ArrayList<>();
        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i < (nodesToSet.size() / 2)) {
                firstPartOfNodes.add(nodesToSet.get(i));
            } else {
                secondPartOfNodes.add(nodesToSet.get(i));
            }
        }

        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);

        //myšlenka - mám 2 samostatný grafy, spojím je jednou čarou - tadá artikulace
        Coordinate oneNode = firstPartOfNodes.get(0);
        Coordinate secondNode = secondPartOfNodes.get(0);
        CustomLine newCustomLine = new CustomLine(oneNode, secondNode);


        //tohle jenom proto aby to bylo videt
        ArrayList<CustomLine> redLines = new ArrayList<>();
        redLines.add(newCustomLine);


        firstPartOfBipartite.addAll(secondPartOfBipartite);
        Map mapToSet = new Map(firstPartOfBipartite, nodesToSet, redLines);
        getGraphGeneratedView().setMap(mapToSet);
        generatedMapViewModel.setMap(mapToSet);
    }

    public void changeGraph(String type) {
        this.type = type;
        switch (type){
            case "artikulace":
                createArticulation();
                break;
            case "most":
                createBridge();
                break;
        }
    }
}

