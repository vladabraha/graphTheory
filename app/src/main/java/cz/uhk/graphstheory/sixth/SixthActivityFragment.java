package cz.uhk.graphstheory.sixth;

import android.os.Bundle;
import android.os.Handler;
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

    private Map hamiltonMap, eulerMap, previousMapToUpdate, mapAnimated;

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
                                mapAnimated = new Map(eulerMap);
                                createMapAnimation();
                                break;
                            case "hamiltonovsky":
                                euler = false;
                                hamilton = true;
                                getGraphGeneratedView().setMap(hamiltonMap);
                                generatedMapViewModel.setMap(hamiltonMap);
                                mapAnimated = new Map(hamiltonMap);
                                createMapAnimation();
                                break;

                            default:
                                generatedMapViewModel.setMap(eulerMap);
                                mapAnimated = new Map(eulerMap);
                                createMapAnimation();
                                break;
                        }
                    }
                    disableListener = true;
                }
            }
        });
    }

    //vezmu vždycky aktualizovaný graf o uživatelovi pohyby
    //z minulého grafu spočítám počet červených čar a z nového grafu nakopíruju stejný počet o jeden zvětšený (pokud předtím neměli stejný počet, to pak vezmu jenom první)
    private void createMapAnimation() {
        int amountOfRedLines;
        int amountOfShowedLines;
        switch (type) {
            case "euleruv":
                amountOfRedLines = eulerMap.getRedLineList().size();
                amountOfShowedLines = mapAnimated.getRedLineList().size();
                if (amountOfRedLines == amountOfShowedLines){
                    mapAnimated = new Map(eulerMap);
                    mapAnimated.setRedLineList(new ArrayList<>());
                    ArrayList<CustomLine> redLines = mapAnimated.getRedLineList();
                    redLines.add(eulerMap.getRedLineList().get(0));
                } else {
                    mapAnimated = new Map(eulerMap);
                    mapAnimated.setRedLineList(new ArrayList<>());
                    ArrayList<CustomLine> redLines = mapAnimated.getRedLineList();
                    for (int i = 0; i < amountOfShowedLines + 1; i++){
                        redLines.add(eulerMap.getRedLineList().get(i));
                    }
                }
                break;

            case "hamiltonovsky":

                amountOfRedLines = hamiltonMap.getRedLineList().size();
                amountOfShowedLines = mapAnimated.getRedLineList().size();
                if (amountOfRedLines == amountOfShowedLines){
                    mapAnimated = new Map(hamiltonMap);
                    mapAnimated.setRedLineList(new ArrayList<>());
                    ArrayList<CustomLine> redLines = mapAnimated.getRedLineList();
                    redLines.add(hamiltonMap.getRedLineList().get(0));
                } else {
                    mapAnimated = new Map(hamiltonMap);
                    mapAnimated.setRedLineList(new ArrayList<>());
                    ArrayList<CustomLine> redLines = mapAnimated.getRedLineList();
                    for (int i = 0; i < amountOfShowedLines + 1; i++){
                        redLines.add(hamiltonMap.getRedLineList().get(i));
                    }
                }
                break;

            default:

                break;
        }

        getGraphGeneratedView().setMap(mapAnimated);
        generatedMapViewModel.setMap(mapAnimated);

        new Handler().postDelayed(this::createMapAnimation, 3000);
    }

    private void createEulerMap(ArrayList<Coordinate> nodesToSet) {
        ArrayList<CustomLine> lines = new ArrayList<>();
        ArrayList<CustomLine> redLines = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++){
            if (i < nodesToSet.size() - 1){
                lines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i+1)));
                redLines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(i+1)));
            }else {
                lines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(2)));
                redLines.add(new CustomLine(nodesToSet.get(i), nodesToSet.get(2)));
            }
        }
        eulerMap = new Map(lines, nodesToSet, redLines);
    }

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
        previousMapToUpdate = new Map(map);
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

                    //ještě aktualizuje nody
                    for (int k = 0; k < previousCircles.size(); k++){
                        int finalK = k;
                        if (circles.stream().noneMatch(circle -> circle.equal(previousCircles.get(finalK)))){
                            Coordinate coordinate = hamiltonMap.getCircles().get(k);
                            coordinate.x = circles.get(k).x;
                            coordinate.y = circles.get(k).y;
                        }
                    }

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

                    //ještě aktualizuje nody
                    for (int k = 0; k < previousCircles.size(); k++){
                        int finalK = k;
                        if (circles.stream().noneMatch(circle -> circle.equal(previousCircles.get(finalK)))){
                            Coordinate coordinate = eulerMap.getCircles().get(k);
                            coordinate.x = circles.get(k).x;
                            coordinate.y = circles.get(k).y;
                        }
                    }
                }
            }
        }




    }
}

