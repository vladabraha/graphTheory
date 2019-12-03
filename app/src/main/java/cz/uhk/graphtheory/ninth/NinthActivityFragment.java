package cz.uhk.graphtheory.ninth;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.CustomLine;
import cz.uhk.graphtheory.model.Map;
import cz.uhk.graphtheory.util.GraphGenerator;

public class NinthActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private static final int MAXIMUM_AMOUNT_OF_NODES = 7;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;

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
                        int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                        getGraphGeneratedView().setMap(generateSpanningTree(BRUSH_SIZE, height, width));

                    }
                    disableListener = true;
                }
            }
        });
    }

    //VZHLEDE K TOMU, ŽE JE KOSTRA TO SAME CO STROM, AKORÁT TAM CHYBÍ NĚJAKÝ ČÁRY NAVÍC Z PŮVODNÍHO GRAFU, VYGENERUJU STROM JAKO ČERVENÝ ČÁRY A PŘIDÁM NĚJAKÝ NORMÁLNÍ HRANY
    //myšlenka - projdu uzly, vezmu ten co má nejnižší y souřadnici a postupně ho budu propojovat dál s nodama, který leží níž
    //nejnižsí souřadnici si zajistím tak, že budu mít array list serazenej pomoci comparatoru
    //plus tam 3 od konce trochu náhodně pospojuju, aby to nějak vypadalo
    public static Map generateSpanningTree(int BRUSH_SIZE, int height, int width ) {
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);
//        Collections.sort(nodesToSet);

        ArrayList<CustomLine> redLinesList = new ArrayList<>();
        for (int i = 0; i < nodesToSet.size(); i++) {
            if (i == nodesToSet.size() - 3 && i > 0) {
                redLinesList.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i - 1)));
                redLinesList.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 1)));
                redLinesList.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 2)));
            } else if (i < nodesToSet.size() - 3 && i > 0) {
                redLinesList.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i - 1)));
            }
        }
        //k vytvořené kostře přidám náhodně najaké hrany
        ArrayList<CustomLine> edges = GraphGenerator.generateRandomEdges(nodesToSet);
        for (CustomLine redLine : redLinesList){
            if (edges.stream().noneMatch(m -> m.isLineSame(redLine))){
                edges.add(redLine);
            }
        }
        return new Map(edges, nodesToSet, redLinesList);
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            seventhFragmentActivityCommunicationInterface = (SeventhFragmentActivityCommunicationInterface) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement SeventhFragmentActivityCommunicationInterface");
//        }
//    }
//
//    public interface SeventhFragmentActivityCommunicationInterface {
//        public void onScoreComputed(ArrayList<Integer> graphScore);
//    }

}
