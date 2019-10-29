package cz.uhk.graphstheory.first;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

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
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
                            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
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
        if (!type.isEmpty()) {
            switch (type) {
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
     * @param type of generated graph
     * @return size of line if needed
     */

    public int changeEducationGraph(String type) {
        this.type = type;
        ArrayList<Coordinate> redLines = null;
        Map map = null;
        if (!type.isEmpty()) {
            map = getGraphGeneratedView().getMap();
            switch (type) {
                case "cesta":
                    redLines = PathGenerator.generatePath(getGraphGeneratedView().getMap());
                    getGraphGeneratedView().setRedLineList(redLines);
                    getGraphGeneratedView().invalidate();
                    break;
                case "tah":
                    redLines = PathGenerator.generateTrail(getGraphGeneratedView().getMap());
                    getGraphGeneratedView().setRedLineList(redLines);
                    getGraphGeneratedView().invalidate();
                    break;


                case "kruznice":
                    ArrayList<Coordinate> coordinates = PathGenerator.generateCycle(getGraphGeneratedView().getMap());
                    int length = Math.round(coordinates.size() / 2);
                    getGraphGeneratedView().setRedLineList(coordinates);
                    getGraphGeneratedView().invalidate();
                    return length;
            }
            if (redLines != null) {
                ArrayList<String> chars = new ArrayList<>();
                ArrayList<Coordinate> nodes = map.getCircles();
                for (Coordinate redCoordinate : redLines) {
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).equal(redCoordinate)) {
                            String value = "B";
                            int charValue = value.charAt(0);
                            for (int j = 0; j < i; j++) {
                                charValue++;
                            }
                            String letter = String.valueOf((char) charValue);
                            //todo seradit uzly tak, aby sly za sebou
                            chars.add(letter);
                        }
                    }
                }
                Log.d("chars", chars.toString());
                arrangeChars(chars);
                Log.d("chars", chars.toString());
            }
        }
        return 0;
    }

    //projdu pole, na každém lichém prvku se podívám jestli lichý a lichý minus jedna prvek není obsažen v následujícíh 2 indexech
    //pokud ano, porovnám a mrknu, jestli stejná písmena jsou vedle sebe, pokuc ne, prohodím
    private ArrayList<String> arrangeChars(ArrayList<String> chars) {
        for (int i = 0; i < chars.size() - 1; i++) {
            if (i > 0 && i % 2 != 0) {
                //pokud sedi n-1 index s nasledujícím - prohodím n - 1 se současným
                if (chars.get(i - 1).equals(chars.get(i + 1))) {
                    Collections.swap(chars, i - 1, i);
                    //pokud sedí současný s n + 2 prohodím následující 2 indexy
                } else if (chars.get(i).equals(chars.get(i + 2))) {
                    Collections.swap(chars, i + 1, i + 2);
                    //pokud sedí n - 1 s n + 2 prohodím jak n - 1 s n tak n + 1 s n + 2
                } else if (chars.get(i - 1).equals(chars.get(i + 2))) {
                    Collections.swap(chars, i - 1, i);
                    Collections.swap(chars, i + 1, i + 2);
                }
            }
        }
        return chars;
    }
}
