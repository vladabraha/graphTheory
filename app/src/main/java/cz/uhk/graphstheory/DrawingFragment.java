package cz.uhk.graphstheory;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.Objects;

import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.model.MapViewModel;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link DrawingFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link DrawingFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class DrawingFragment extends Fragment implements TabActivity.OnFragmentInteractionListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private PaintView paintView;
    private MapViewModel mapViewModel;

//    private OnFragmentInteractionListener mListener;

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

        mapViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MapViewModel.class);
        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        paintView =  Objects.requireNonNull(getView()).findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        if (mapViewModel.getMap() != null){
            paintView.setMap(mapViewModel.getMap());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Map map = paintView.getMap();
        mapViewModel.setMap(map);
    }

    @Override
    public void changeDrawingMethod(String method) {
        switch (method){
            case "line":
                paintView.line();
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

}
