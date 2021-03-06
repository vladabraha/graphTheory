package cz.uhk.graphtheory.common;

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

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.DrawMapViewModel;
import cz.uhk.graphtheory.model.Graph;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link DrawingFragment.GraphListener} interface
// * to handle interaction events.
// * Use the {@link DrawingFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class DrawingFragment extends Fragment implements DrawingFragmentListener, PaintView.CommunicationInterface {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private PaintView paintView;
    private DrawMapViewModel drawMapViewModel;
    private DisplayMetrics metrics;
    private CommunicationInterface mListener;
    private int width, height;
    private static int BRUSH_SIZE;

    private boolean disableListener = false;
    private Graph sentGraph;
    private boolean shouldBeSentGraphSet, shouldBeNodeColorSwitched;

//    private GraphListener mListener;

    public DrawingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        drawMapViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DrawMapViewModel.class);
        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        paintView = Objects.requireNonNull(getView()).findViewById(R.id.paintView);
        metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        if (shouldBeSentGraphSet){
            shouldBeSentGraphSet = false;
            paintView.setGraph(sentGraph);
            drawMapViewModel.setGraph(sentGraph);

        }else if (drawMapViewModel.getGraph() != null) {
            paintView.setGraph(drawMapViewModel.getGraph());
        }
        paintView.setmListener(this);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mListener != null && !disableListener) {
                    disableListener = true;
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    mListener.sentMetrics(width, height);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CommunicationInterface) {
            mListener = (CommunicationInterface) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement GraphListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Graph graph = paintView.getGraph();
        drawMapViewModel.setGraph(graph);
    }

    public void changeDrawingMethod(String method) {
        switch (method) {
            case "line":
                paintView.line();
                break;
            case "circle_move":
                paintView.circleMove();
                break;
            case "path":
                paintView.path();
                break;
            case "circle":
                paintView.circle();
                break;
            case "remove":
                paintView.remove();
                break;
            case "clear":
                paintView.clear();
                break;
            case "prevent_all":
                paintView.disableAllActions();
                break;

        }
    }

    @Override
    public Graph getUserGraph() {
        return paintView.getGraph();
    }

    public void setUserGraph(Graph graph) {
        paintView.setGraph(graph);
    }

    public void setShouldBeNodeColorSwitched(boolean shouldBeNodeColorSwitched) {
        this.shouldBeNodeColorSwitched = shouldBeNodeColorSwitched;
        //tohle zabrání všem aktivitám v provádění
        if (shouldBeNodeColorSwitched){
            paintView.disableAllActions();
        }
    }

    public void setMapAfterViewIsCreated(Graph graph){
        shouldBeSentGraphSet = true;
        sentGraph = graph;
    }

    public DisplayMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void sentTouchUpCoordinates(Coordinate coordinate) {
        if (shouldBeNodeColorSwitched){
            Graph graph = paintView.getGraph();
            boolean found = false, nodeToSwitchIsRed = false;
            Coordinate nodeToSwitch = null;
            ArrayList<Coordinate> redNodes = graph.getRedNodes();
            ArrayList<Coordinate> nodes = graph.getNodes();

            for (Coordinate nodeCoordinate : nodes){
                if (checkIsInCircle(nodeCoordinate.x, nodeCoordinate.y, coordinate.x, coordinate.y)){
                    found = true;
                    nodeToSwitchIsRed = false;
                    nodeToSwitch = nodeCoordinate;
                    break;
                }
            }
            if (!found && redNodes != null){
                for (Coordinate redNodeCoordinate : redNodes){
                    if (checkIsInCircle(redNodeCoordinate.x, redNodeCoordinate.y, coordinate.x, coordinate.y)){
                        found = true;
                        nodeToSwitchIsRed = true;
                        nodeToSwitch = redNodeCoordinate;
                        break;
                    }
                }
            }
            //pokud si kliknul na uzel, tak mu prohod barvu
            if (found){
                if (nodeToSwitchIsRed){
                    nodes.add(nodeToSwitch);
                    redNodes.remove(Objects.requireNonNull(nodeToSwitch));
                }else {
                    //přehoď všechny červeny uzly na normální
                    nodes.addAll(redNodes);
                    redNodes.clear(); //to avoid ConcurrentModificationException
                    //a teď samotné prohození
                    redNodes.add(nodeToSwitch);
                    nodes.remove(Objects.requireNonNull(nodeToSwitch));
                }
            }
            paintView.setGraph(new Graph(graph.getEdges(), nodes, graph.getRedEdgesList(), redNodes));
        }
    }

    private boolean checkIsInCircle(float circle_x, float circle_y, float point_x, float point_y) {
        if (BRUSH_SIZE == 0) BRUSH_SIZE = PaintView.getBrushSize();
        double D = Math.pow(point_x - circle_x, 2) + Math.pow(point_y - circle_y, 2);
        return D <= Math.pow(BRUSH_SIZE + 30, 2);
    }

    public interface CommunicationInterface {
        void sentMetrics(int width, int height);
    }
}
