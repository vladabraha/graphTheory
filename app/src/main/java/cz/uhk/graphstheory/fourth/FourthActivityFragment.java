package cz.uhk.graphstheory.fourth;

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

public class FourthActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;


    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

    public FourthActivityFragment() {
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
                        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
                            amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        Map mapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);

                        //myšlenka - mam graf - projdu všechny body a podívám se jestli jsou propojený se všema bodama
                        //pokud s nějakým nejsou přidám je do druhého seznamu (red line listu)
                        ArrayList<Coordinate> nodes = mapToSet.getCircles();
                        ArrayList<CustomLine> lines = mapToSet.getCustomLines();
                        ArrayList<CustomLine> redLines = new ArrayList<>();

                        for (Coordinate coordinate : nodes) {
                            ArrayList<Coordinate> alreadyFoundConnection = new ArrayList<>();
                            for (CustomLine customLine : lines) {
                                if (customLine.getFrom().equal(coordinate)) {
                                    //projde vsechny body v alreadyFoundConnection a mrkne, jestli nejakej bod n se rovna custom line.getto
                                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getTo()))) {
                                        alreadyFoundConnection.add(customLine.getTo());
                                    }
                                } else if (customLine.getTo().equal(coordinate)) {
                                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(customLine.getFrom()))) {
                                        alreadyFoundConnection.add(customLine.getFrom());
                                    }
                                }
                            }
                            //pocet nalezenych uzlu může být max. o jedna menší než všechny uzly (sám sebe tam nepřidá)
                            if (alreadyFoundConnection.size() != (nodes.size() - 1)) {
                                for (Coordinate allNodes : nodes) {
                                    if (alreadyFoundConnection.stream().noneMatch(n -> n.equal(allNodes))) {
                                        redLines.add(new CustomLine(allNodes, coordinate));
                                    }
                                }
                            }
                        }

                        mapToSet.setRedLineList(redLines);
                        getGraphGeneratedView().setMap(mapToSet);
                    }
                    disableListener = true;
                }
            }
        });
    }
}
