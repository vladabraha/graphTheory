package cz.uhk.graphtheory.fourth;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.common.GraphGeneratedView;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Map;
import cz.uhk.graphtheory.util.GraphConverter;
import cz.uhk.graphtheory.util.GraphGenerator;

public class FourthActivityFragment extends AbstractFragment {

    private int width;
    private int height;

    public static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    public static final int MINIMUM_AMOUNT_OF_NODES = 5;

    private Map firstMapToSet;
    private boolean setFirst;
    private Map secondMapToSet;
    private boolean shouldStop, disableListener = false;
    private GraphGeneratedView graphGeneratedView;
    public int BRUSH_SIZE;

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
                        BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        firstMapToSet = GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges);

                        secondMapToSet = new Map(firstMapToSet);

                        //myšlenka - mám graf, změním tam jenom souřadnice a znovu vykreslím
                        int randomNumber = (int) Math.round(Math.random() * firstMapToSet.getNodes().size());
                        for (int i = 0; i < randomNumber; i++) {
                            //vytvořím si náhodnou souřadnici
                            float newXCoordinate = (float) (Math.random() * width);
                            float newYCoordinate = (float) (Math.random() * height);

                            //vezmu náhodný uzel a tomu změním souřadnice + všem elementům se stejnou souřadnicí

                            int randomIndex = (int) Math.round(Math.random() * (secondMapToSet.getNodes().size() - 1));
                            Coordinate oldCoordinate = secondMapToSet.getNodes().get(randomIndex);
                            Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);

                            secondMapToSet.getNodes().set(randomIndex, newCoordinate);
                            ArrayList<Edge> edges = secondMapToSet.getEdges();
                            for (int j = 0; j < edges.size(); j++) {
                                Edge edge = edges.get(j);
                                if (edge.getTo().equal(oldCoordinate)) {
                                    Edge newEdge = new Edge(edge.getFrom(), newCoordinate);
                                    edges.set(j, newEdge);
                                } else if (edge.getFrom().equal(oldCoordinate)) {
                                    Edge newEdge = new Edge(newCoordinate, edge.getTo());
                                    edges.set(j, newEdge);
                                }
                            }
                        }
                        graphGeneratedView = getGraphGeneratedView();

                        //rozdelim si mapu na 2 poloviny (metoda bohužel vrací ten samej graf rozdelenej na polovinu, takze si to musi zavolat 2x a pak si to z toho vytahnu
                        //nez to poslu do view, tak to musim ještě slepit do jednoho grafu
                        ArrayList<Map> firstMapTwice = GraphConverter.convertMapsToSplitScreenArray(firstMapToSet, height);
                        firstMapToSet = firstMapTwice.get(0);
                        ArrayList<Map> secondMapTwice = GraphConverter.convertMapsToSplitScreenArray(secondMapToSet, height);
                        secondMapToSet = secondMapTwice.get(1);

                        firstMapToSet.getEdges().addAll(secondMapToSet.getEdges());
                        firstMapToSet.getNodes().addAll(secondMapToSet.getNodes());
                        firstMapToSet.getRedEdgesList().addAll(secondMapToSet.getRedEdgesList());

                        graphGeneratedView.setMap(firstMapToSet);

                    }
                    disableListener = true;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouldStop = true;
    }

}
