package cz.uhk.graphstheory.sixth;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphstheory.abstraction.AbstractFragment;
import cz.uhk.graphstheory.common.GraphGeneratedView;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.GeneratedMapViewModel;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;


public class SixthActivityFragment extends AbstractFragment implements GraphGeneratedView.CommunicationInterface {

    private boolean disableListener = false;

    private int width;
    private int height;

    private String type = "";
    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;
    private GeneratedMapViewModel generatedMapViewModel;

    private Map hamiltonMap, eulerMap, previousMapToUpdate;

    boolean hamilton, euler;

    public SixthActivityFragment() {
        // Required empty public constructor
    }

    public SixthActivityFragment(String type) {
        this.type = type;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        generatedMapViewModel = getGeneratedMapViewModel();
        getGraphGeneratedView().setListener(this);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
                    if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES)
                        amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
                    int BRUSH_SIZE = getGraphGeneratedView().getBrushSize();
                    ArrayList<Coordinate> nodesToSet = GraphGenerator.generateNodes(height, width, BRUSH_SIZE, amountOfEdges);

                    createEulerMap(nodesToSet);
                    createHamiltonMap(nodesToSet);
                    if (width != 0) {
                        switch (type) {
                            case "euleruv":
                                euler = true;
                                hamilton = false;
                                getGraphGeneratedView().setMap(eulerMap);
                                generatedMapViewModel.setMap(eulerMap);
                                break;

                            case "hamiltonovsky":
                                euler = false;
                                hamilton = true;
                                getGraphGeneratedView().setMap(hamiltonMap);
                                generatedMapViewModel.setMap(hamiltonMap);
                                break;

                            default:
                                generatedMapViewModel.setMap(eulerMap);
                                break;
                        }
                    }
                    disableListener = true;
                }
            }
        });
    }

    private void createEulerMap(ArrayList<Coordinate> nodesToSet) {
//
//        //myšlenka - mam body, vezmu polovinu a nějak je spojim
//        //vezmu druhou polovinu a nejak je spojim. Mezi prvni a druhou půlkou neni žádný propoj - bipartitni graf
//        ArrayList<Coordinate> firstPartOfNodes = new ArrayList<>();
//        ArrayList<Coordinate> secondPartOfNodes = new ArrayList<>();
//        for (int i = 0; i < nodesToSet.size(); i++) {
//            if (i < (nodesToSet.size() / 2)) {
//                firstPartOfNodes.add(nodesToSet.get(i));
//            } else {
//                secondPartOfNodes.add(nodesToSet.get(i));
//            }
//        }
//
//        ArrayList<CustomLine> firstPartOfBipartite = GraphGenerator.generateRandomEdges(firstPartOfNodes);
//        ArrayList<CustomLine> secondPartOfBipartite = GraphGenerator.generateRandomEdges(secondPartOfNodes);
//
//        //myšlenka - mám novej bod, mám 2 samostný grafy, pridám mezi ne bod a ten propojím s kažodou polovinou - tadá artikulace
//        Coordinate newNode = new Coordinate((float) Math.random() * width, (float) Math.random() * height);
//        Coordinate oneNode = firstPartOfNodes.get(0);
//        Coordinate secondNode = secondPartOfNodes.get(0);
//        CustomLine newCustomLine = new CustomLine(newNode, oneNode);
//        CustomLine newCustomLine2 = new CustomLine(newNode, secondNode);
//
//        //tohle jenom proto aby to bylo videt
//        ArrayList<CustomLine> redLines = new ArrayList<>();
//        redLines.add(newCustomLine);
//        redLines.add(newCustomLine2);
//        nodesToSet.add(newNode);
//
//
//        firstPartOfBipartite.addAll(secondPartOfBipartite);
//        Map mapToSet = new Map(firstPartOfBipartite, nodesToSet, redLines);
//        getGraphGeneratedView().setMap(mapToSet);
//        generatedMapViewModel.setMap(mapToSet);
    }

    //todo pridat timer na postupny pridavani do grafu
    //myšlenka, projedu postupne vrcholy a propojim je jak jdou za sebou a kdyz tam ještě nemaj normální caru z generatoru, tak ho tam taky pridam
    private void createHamiltonMap(ArrayList<Coordinate> nodesToSet) {
        ArrayList<CustomLine> lines = new ArrayList<>();
        ArrayList<CustomLine> redLines = new ArrayList<>();
        ArrayList<CustomLine> preGeneratedLines = GraphGenerator.generateRandomEdges(nodesToSet);

        for (int i = 0; i < nodesToSet.size(); i++) {
            CustomLine line;
            CustomLine redline;
            if (i < nodesToSet.size() - 1) {
                line = new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 1));
                redline = new CustomLine(nodesToSet.get(i), nodesToSet.get(i + 1));
            } else {
                line = new CustomLine(nodesToSet.get(i), nodesToSet.get(0));
                redline = new CustomLine(nodesToSet.get(i), nodesToSet.get(0));
            }
            lines.add(line);
            redLines.add(redline);
        }

        for (int j = 0; j < redLines.size(); j++) {
            int finalJ = j;
            if (preGeneratedLines.stream().noneMatch(line -> line.isLineSame(redLines.get(finalJ)))) {
                preGeneratedLines.add(lines.get(j));
            }
        }

        hamiltonMap = new Map(preGeneratedLines, nodesToSet, redLines);
    }

    public void changeGraph(String type) {
        this.type = type;
        switch (type) {
            case "euleruv":
                euler = true;
                hamilton = false;
                previousMapToUpdate = null;
                generatedMapViewModel.setMap(eulerMap);
                break;
            case "hamiltonovsky":
                euler = false;
                hamilton = true;
                previousMapToUpdate = null;
                generatedMapViewModel.setMap(hamiltonMap);
                break;
        }
    }

    //sem se posle mapa pred stistknutim
    @Override
    public void sentPreviousMap(Map map) {
        previousMapToUpdate = map;
    }

    //sem prijde mapa po dokončení tahu uživatele
    //myšlenka - projdu celou mapu a jeden jedinej uzel musi byt jinej
    //tim ze to prochazim po jednom a mam predchozi tak se mrknu, kterej index ma jinde bod, mrknu do mapy jak vypadal předtím a najdu si všechny prvky s danou souřadnici a změnim je na nove
    @Override
    public void sentUpdatedMap(Map map) {
        if (previousMapToUpdate == null) return;

        ArrayList<CustomLine> lines = map.getCustomLines();
//        ArrayList<CustomLine> redLines = map.getRedLineList();
        ArrayList<Coordinate> circles = map.getCircles();

        ArrayList<CustomLine> previousLines = previousMapToUpdate.getCustomLines();
//        ArrayList<CustomLine> previousRedLines = previousMapToUpdate.getRedLineList();
        ArrayList<Coordinate> previousCircles = previousMapToUpdate.getCircles();


        for (int i = 0; i < previousLines.size(); i++) {
            int finalI = i;
            if (lines.stream().noneMatch(line -> line.isLineSame(previousLines.get(finalI)))) {
                if (hamilton) {
                    CustomLine customLineToUpdate = hamiltonMap.getCustomLines().get(i);
                    ArrayList<CustomLine> hamiltonRedLines = hamiltonMap.getRedLineList();
                    for (CustomLine hamiltonRedLine : hamiltonRedLines){
                        if (hamiltonRedLine.isLineSame(customLineToUpdate)){
                            hamiltonRedLine.setFrom(lines.get(i).getFrom());
                            hamiltonRedLine.setTo(lines.get(i).getTo());
                        }
                    }
                    customLineToUpdate.setFrom(lines.get(i).getFrom());
                    customLineToUpdate.setTo(lines.get(i).getTo());

                } else if (euler) {
                    CustomLine customLineToUpdate = eulerMap.getCustomLines().get(i);
                    ArrayList<CustomLine> eulerRedLines = eulerMap.getRedLineList();
                    for (CustomLine eulerRedLine : eulerRedLines){
                        if (eulerRedLine.isLineSame(customLineToUpdate)){
                            eulerRedLine.setFrom(lines.get(i).getFrom());
                            eulerRedLine.setTo(lines.get(i).getTo());
                        }
                    }
                    customLineToUpdate.setFrom(lines.get(i).getFrom());
                    customLineToUpdate.setTo(lines.get(i).getTo());
                }
            }
        }

        for (int k = 0; k < previousCircles.size(); k++){
            int finalK = k;
            if (circles.stream().noneMatch(circle -> circle.equal(previousCircles.get(finalK)))){
                Coordinate coordinate = previousCircles.get(k);
                coordinate.x = circles.get(k).x;
                coordinate.y = circles.get(k).y;
            }
        }


    }
}

