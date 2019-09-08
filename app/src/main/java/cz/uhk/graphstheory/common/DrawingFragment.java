package cz.uhk.graphstheory.common;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.TabActivity;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
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
public class DrawingFragment extends Fragment implements TabActivity.OnFragmentInteractionListener, DrawingFragmentListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private PaintView paintView;
    private DrawMapViewModel drawMapViewModel;
    private DisplayMetrics metrics;
    CommunicationInterface mListener;
    private int width, height;

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
        if (drawMapViewModel.getMap() != null) {
            paintView.setMap(drawMapViewModel.getMap());
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mListener != null){
                width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    mListener.sentMetrics(width, height);
                }
            }
        });
    }

        @Override
        public void onAttach (Context context){
            super.onAttach(context);
            if (context instanceof CommunicationInterface) {
                mListener = (CommunicationInterface) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement GraphListener");
            }
        }

        @Override
        public void onDetach () {
            super.onDetach();
            Map map = paintView.getMap();
            drawMapViewModel.setMap(map);
        }

        @Override
        public void changeDrawingMethod (String method){
            switch (method) {
                case "line":
                    paintView.line();
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

            }
        }

        @Override
        public Map getUserGraph () {
            return paintView.getMap();
        }

        public void setUserGraph (Map map){
            paintView.setMap(map);
        }

        public DisplayMetrics getMetrics () {
            return metrics;
        }

        public interface CommunicationInterface {
            public void sentMetrics(int width, int height);
        }
    }
