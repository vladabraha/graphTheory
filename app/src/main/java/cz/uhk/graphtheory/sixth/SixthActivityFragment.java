package cz.uhk.graphtheory.sixth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.common.GraphGeneratedView;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.GeneratedGraphViewModel;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphGenerator;


public class SixthActivityFragment extends AbstractFragment implements GraphGeneratedView.CommunicationInterface {

    private boolean disableListener = false;

    private int width;
    private int height;

    private String type = "";
    private static final int MAXIMUM_AMOUNT_OF_NODES = 12;
    private static final int MINIMUM_AMOUNT_OF_NODES = 5;
    private GeneratedGraphViewModel generatedGraphViewModel;

    private Graph hamiltonGraph, eulerGraph, previousGraphToUpdate, graphAnimated;

    private boolean hamilton, euler;

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
        generatedGraphViewModel = getGeneratedGraphViewModel();
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

                    eulerGraph = createEulerMap(nodesToSet);
                    hamiltonGraph = createHamiltonMap(nodesToSet);
                    if (width != 0) {
                        switch (type) {
                            case "euleruv":
                                euler = true;
                                hamilton = false;
                                getGraphGeneratedView().setGraph(eulerGraph);
                                generatedGraphViewModel.setGraph(eulerGraph);
                                graphAnimated = new Graph(eulerGraph);
                                createMapAnimation();
                                break;
                            case "hamiltonovsky":
                                euler = false;
                                hamilton = true;
                                getGraphGeneratedView().setGraph(hamiltonGraph);
                                generatedGraphViewModel.setGraph(hamiltonGraph);
                                graphAnimated = new Graph(hamiltonGraph);
                                createMapAnimation();
                                break;

                            default:
                                generatedGraphViewModel.setGraph(eulerGraph);
                                graphAnimated = new Graph(eulerGraph);
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
        sentUpdatedGraph(getGraphGeneratedView().getGraph()); //před každým updatem animace si stáhnu aktuální rozmístění prvků ve view
        int amountOfredEdges;
        int amountOfShowedLines;
        switch (type) {
            case "euleruv":
                amountOfredEdges = eulerGraph.getRedEdgesList().size();
                amountOfShowedLines = graphAnimated.getRedEdgesList().size();
                if (amountOfredEdges == amountOfShowedLines){
                    graphAnimated = new Graph(eulerGraph);
                    graphAnimated.setRedEdgesList(new ArrayList<>());
                    ArrayList<Edge> redEdges = graphAnimated.getRedEdgesList();
                    redEdges.add(eulerGraph.getRedEdgesList().get(0));
                } else {
                    graphAnimated = new Graph(eulerGraph);
                    graphAnimated.setRedEdgesList(new ArrayList<>());
                    ArrayList<Edge> redEdges = graphAnimated.getRedEdgesList();
                    for (int i = 0; i < amountOfShowedLines + 1; i++){
                        redEdges.add(eulerGraph.getRedEdgesList().get(i));
                    }
                }
                break;

            case "hamiltonovsky":

                amountOfredEdges = hamiltonGraph.getRedEdgesList().size();
                amountOfShowedLines = graphAnimated.getRedEdgesList().size();
                if (amountOfredEdges == amountOfShowedLines){
                    graphAnimated = new Graph(hamiltonGraph);
                    graphAnimated.setRedEdgesList(new ArrayList<>());
                    ArrayList<Edge> redEdges = graphAnimated.getRedEdgesList();
                    redEdges.add(hamiltonGraph.getRedEdgesList().get(0));
                } else {
                    graphAnimated = new Graph(hamiltonGraph);
                    graphAnimated.setRedEdgesList(new ArrayList<>());
                    ArrayList<Edge> redEdges = graphAnimated.getRedEdgesList();
                    for (int i = 0; i < amountOfShowedLines + 1; i++){
                        redEdges.add(hamiltonGraph.getRedEdgesList().get(i));
                    }
                }
                break;

            default:

                break;
        }

        getGraphGeneratedView().setGraph(graphAnimated);
        generatedGraphViewModel.setGraph(graphAnimated);

        new Handler().postDelayed(this::createMapAnimation, 3000);
    }

    public static Graph createEulerMap(ArrayList<Coordinate> nodesToSet) {
        ArrayList<Edge> lines = new ArrayList<>();
        ArrayList<Edge> redEdges = new ArrayList<>();

        for (int i = 0; i < nodesToSet.size(); i++){
            if (i < nodesToSet.size() - 1){
                lines.add(new Edge(nodesToSet.get(i), nodesToSet.get(i+1)));
                redEdges.add(new Edge(nodesToSet.get(i), nodesToSet.get(i+1)));
            }else {
                lines.add(new Edge(nodesToSet.get(i), nodesToSet.get(2)));
                redEdges.add(new Edge(nodesToSet.get(i), nodesToSet.get(2)));
            }
        }
        return new Graph(lines, nodesToSet, redEdges);
    }

    //myšlenka, projedu postupne vrcholy a propojim je jak jdou za sebou a kdyz tam ještě nemaj normální caru z generatoru, tak ho tam taky pridam
    public static Graph createHamiltonMap(ArrayList<Coordinate> nodesToSet) {
        ArrayList<Edge> lines = new ArrayList<>();
        ArrayList<Edge> redEdges = new ArrayList<>();
        ArrayList<Edge> preGeneratedLines = GraphGenerator.generateRandomEdges(nodesToSet);

        for (int i = 0; i < nodesToSet.size(); i++) {
            Edge line;
            Edge redline;
            if (i < nodesToSet.size() - 1) {
                line = new Edge(nodesToSet.get(i), nodesToSet.get(i + 1));
                redline = new Edge(nodesToSet.get(i), nodesToSet.get(i + 1));
            } else {
                line = new Edge(nodesToSet.get(i), nodesToSet.get(0));
                redline = new Edge(nodesToSet.get(i), nodesToSet.get(0));
            }
            lines.add(line);
            redEdges.add(redline);
        }

        for (int j = 0; j < redEdges.size(); j++) {
            int finalJ = j;
            if (preGeneratedLines.stream().noneMatch(line -> line.isEdgeSame(redEdges.get(finalJ)))) {
                preGeneratedLines.add(lines.get(j));
            }
        }

        return new Graph(preGeneratedLines, nodesToSet, redEdges);
    }

    public void changeGraph(String type) {
        this.type = type;
        switch (type) {
            case "euleruv":
                euler = true;
                hamilton = false;
                previousGraphToUpdate = null;
                break;
            case "hamiltonovsky":
                euler = false;
                hamilton = true;
                previousGraphToUpdate = null;
                break;
        }
    }

    //sem se posle mapa pred stistknutim
    @Override
    public void sentPreviousGraph(Graph graph) {
        previousGraphToUpdate = new Graph(graph);
    }

    //sem prijde mapa po dokončení tahu uživatele
    //myšlenka - projdu celou mapu a jeden jedinej uzel musi byt jinej
    //tim ze to prochazim po jednom a mam predchozi tak se mrknu, kterej index ma jinde bod, mrknu do mapy jak vypadal předtím a najdu si všechny prvky s danou souřadnici a změnim je na nove
    @Override
    public void sentUpdatedGraph(Graph graph) {
        if (previousGraphToUpdate == null) return;

        ArrayList<Edge> lines = graph.getEdges();
        ArrayList<Coordinate> nodes = graph.getNodes();

        ArrayList<Edge> previousLines = previousGraphToUpdate.getEdges();
        ArrayList<Coordinate> previousNodes = previousGraphToUpdate.getNodes();


        for (int i = 0; i < previousLines.size(); i++) {
            int finalI = i;
            if (lines.stream().noneMatch(line -> line.isEdgeSame(previousLines.get(finalI)))) {
                if (hamilton) {
                    Edge edgeToUpdate = hamiltonGraph.getEdges().get(i);
                    ArrayList<Edge> hamiltonredEdges = hamiltonGraph.getRedEdgesList();
                    for (Edge hamiltonRedLine : hamiltonredEdges){
                        if (hamiltonRedLine.isEdgeSame(edgeToUpdate)){
                            hamiltonRedLine.setFrom(lines.get(i).getFrom());
                            hamiltonRedLine.setTo(lines.get(i).getTo());
                        }
                    }
                    edgeToUpdate.setFrom(lines.get(i).getFrom());
                    edgeToUpdate.setTo(lines.get(i).getTo());

                    //ještě aktualizuje nody
                    for (int k = 0; k < previousNodes.size(); k++){
                        int finalK = k;
                        if (nodes.stream().noneMatch(circle -> circle.equal(previousNodes.get(finalK)))){
                            Coordinate coordinate = hamiltonGraph.getNodes().get(k);
                            coordinate.x = nodes.get(k).x;
                            coordinate.y = nodes.get(k).y;
                        }
                    }

                } else if (euler) {
                    Edge edgeToUpdate = eulerGraph.getEdges().get(i);
                    ArrayList<Edge> eulerredEdges = eulerGraph.getRedEdgesList();
                    for (Edge eulerRedLine : eulerredEdges){
                        if (eulerRedLine.isEdgeSame(edgeToUpdate)){
                            eulerRedLine.setFrom(lines.get(i).getFrom());
                            eulerRedLine.setTo(lines.get(i).getTo());
                        }
                    }
                    edgeToUpdate.setFrom(lines.get(i).getFrom());
                    edgeToUpdate.setTo(lines.get(i).getTo());

                    //ještě aktualizuje nody
                    for (int k = 0; k < previousNodes.size(); k++){
                        int finalK = k;
                        if (nodes.stream().noneMatch(circle -> circle.equal(previousNodes.get(finalK)))){
                            Coordinate coordinate = eulerGraph.getNodes().get(k);
                            coordinate.x = nodes.get(k).x;
                            coordinate.y = nodes.get(k).y;
                        }
                    }
                }
            }
        }
    }
}

