package cz.uhk.graphtheory.eight;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphGenerator;

public class EightActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    private static final int MINIMUM_AMOUNT_OF_NODES = 4;

    private String mode = "";

    public EightActivityFragment() {
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
                        changeGraph(mode);
                    }
                    disableListener = true;
                }
            }
        });
    }

    //myšlenka - projdu uzly, vezmu ten co má nejnižší y souřadnici a postupně ho budu propojovat dál s nodama, který leží níž
    //nejnižsí souřadnici si zajistím tak, že budu mít array list serazenej pomoci comparatoru
    //plus tam 3 od konce trochu náhodně pospojuju, aby to nějak vypadalo
    @SuppressWarnings("unchecked")
    public static Graph generateTree(int BRUSH_SIZE, int height, int width) {
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);
        Collections.sort(nodesToSet);

        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i == nodesToSet.size() - 3 && i > 0) {
                edges.add(new Edge(nodesToSet.get(i), nodesToSet.get(i - 1)));
                edges.add(new Edge(nodesToSet.get(i), nodesToSet.get(i + 1)));
                edges.add(new Edge(nodesToSet.get(i), nodesToSet.get(i + 2)));
            } else if (i < nodesToSet.size() - 3 && i > 0) {
                edges.add(new Edge(nodesToSet.get(i), nodesToSet.get(i - 1)));
            }
        }
        return new Graph(edges, nodesToSet);
    }

    //myšlenka - vezmu alg. na generovani stromu, a použiju ho 2 - a mám 2 samostatny stromy -> les
    //ještě tomu dám poloviční šířku viewportu, takže k tý druhý půlce přičtu polovinu viewportu a zobrazí se mi to hezky na půlku
    @SuppressWarnings("unchecked")
    public static Graph generateForrest(int BRUSH_SIZE, int height, int width) {

        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> firstNodesToSet = GraphGenerator.generateNodes(height, (width / 2), BRUSH_SIZE, amountOfNodes);
        Collections.sort(firstNodesToSet);

        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < firstNodesToSet.size(); i++) {
            if (i == firstNodesToSet.size() - 3 && i > 0) {
                edges.add(new Edge(firstNodesToSet.get(i), firstNodesToSet.get(i - 1)));
                edges.add(new Edge(firstNodesToSet.get(i), firstNodesToSet.get(i + 1)));
                edges.add(new Edge(firstNodesToSet.get(i), firstNodesToSet.get(i + 2)));
            } else if (i < firstNodesToSet.size() - 3 && i > 0) {
                edges.add(new Edge(firstNodesToSet.get(i), firstNodesToSet.get(i - 1)));
            }
        }

        ArrayList<Coordinate> secondNodesToSet = GraphGenerator.generateNodes(height, (width / 2), BRUSH_SIZE, amountOfNodes);
        Collections.sort(firstNodesToSet);
        Collections.sort(secondNodesToSet);

        ArrayList<Edge> secondEdges = new ArrayList<>();
        for (int i = 0; i < secondNodesToSet.size(); i++) {
            if (i == secondNodesToSet.size() - 3 && i > 0) {
                secondEdges.add(new Edge(secondNodesToSet.get(i), secondNodesToSet.get(i - 1)));
                secondEdges.add(new Edge(secondNodesToSet.get(i), secondNodesToSet.get(i + 1)));
                secondEdges.add(new Edge(secondNodesToSet.get(i - 1), secondNodesToSet.get(i + 2)));
            } else if (i < secondNodesToSet.size() - 3 && i > 0) {
                secondEdges.add(new Edge(secondNodesToSet.get(i), secondNodesToSet.get(i - 1)));
            }
        }

        //posunuti na druhou půlku obrazovky
        for (Coordinate coordinate : secondNodesToSet) {
            coordinate.x = coordinate.x + (((float) width) / 2);
        }


        edges.addAll(secondEdges);
        firstNodesToSet.addAll(secondNodesToSet);

        return new Graph(edges, firstNodesToSet);
    }

    public void changeGraph(String mode) {
        switch (mode) {
            case "tree":
                getGraphGeneratedView().setGraph(generateTree(getGraphGeneratedView().getBrushSize(), height, width));
                break;
            case "forrest":
                getGraphGeneratedView().setGraph(generateForrest(getGraphGeneratedView().getBrushSize(), height, width));
                break;
            default:
                getGraphGeneratedView().setGraph(generateTree(getGraphGeneratedView().getBrushSize(), height, width));
                break;
        }
    }
}
