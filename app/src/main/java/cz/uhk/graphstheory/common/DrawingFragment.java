package cz.uhk.graphstheory.common;

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

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.DrawMapViewModel;
import cz.uhk.graphstheory.model.Map;


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
    private Map sentMap;
    private boolean shouldBeSentMapSet, shouldBeNodeColorSwitched;

//    private GraphListener mListener;

    public DrawingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrawingFragment.
     */

    public static DrawingFragment newInstance(String param1, String param2) {
        DrawingFragment fragment = new DrawingFragment();
        fragment.setRetainInstance(true); //při otočení displaye by se to nemělo změnit
        return fragment;
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
        if (shouldBeSentMapSet){
            shouldBeSentMapSet = false;
            paintView.setMap(sentMap);
            drawMapViewModel.setMap(sentMap);

        }else if (drawMapViewModel.getMap() != null) {
            paintView.setMap(drawMapViewModel.getMap());
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
        Map map = paintView.getMap();
        drawMapViewModel.setMap(map);
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
    public Map getUserGraph() {
        return paintView.getMap();
    }

    public void setUserGraph(Map map) {
        paintView.setMap(map);
    }

    public void setShouldBeNodeColorSwitched(boolean shouldBeNodeColorSwitched) {
        this.shouldBeNodeColorSwitched = shouldBeNodeColorSwitched;
        //tohle zabrání všem aktivitám v provádění
        if (shouldBeNodeColorSwitched){
            paintView.disableAllActions();
        }
    }

    public void setMapAfterViewIsCreated(Map map){
        shouldBeSentMapSet = true;
        sentMap = map;
    }

    public DisplayMetrics getMetrics() {
        return metrics;
    }

    @Override
    public void sentTouchUpCoordinates(Coordinate coordinate) {
        if (shouldBeNodeColorSwitched){
            Map map = paintView.getMap();
            boolean found = false, nodeToSwitchIsRed = false;
            Coordinate nodeToSwitch = null;
            ArrayList<Coordinate> redCircles = map.getRedCircles();
            ArrayList<Coordinate> circles = map.getCircles();

            for (Coordinate nodeCoordinate : circles){
                if (checkIsInCircle(nodeCoordinate.x, nodeCoordinate.y, coordinate.x, coordinate.y)){
                    found = true;
                    nodeToSwitchIsRed = false;
                    nodeToSwitch = nodeCoordinate;
                    break;
                }
            }
            if (!found && redCircles != null){
                for (Coordinate redNodeCoordinate : redCircles){
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
                    circles.add(nodeToSwitch);
                    redCircles.remove(Objects.requireNonNull(nodeToSwitch));
                }else {
                    //přehoď všechny červeny uzly na normální
                    circles.addAll(redCircles);
                    redCircles.clear(); //to avoid ConcurrentModificationException
                    //a teď samotné prohození
                    redCircles.add(nodeToSwitch);
                    circles.remove(Objects.requireNonNull(nodeToSwitch));
                }
            }
            paintView.setMap(new Map(map.getCustomLines(), circles, map.getRedLineList(), redCircles));
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
