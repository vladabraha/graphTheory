package cz.uhk.graphstheory.first;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.model.GeneratedMapViewModel;
import cz.uhk.graphstheory.model.Map;


public class GenerateGraphFragment extends Fragment {

    private GraphGeneratedView graphGeneratedView;
    private GeneratedMapViewModel generatedMapViewModel;

    boolean disableListener = false;

//    private GraphListener mListener;

    public GenerateGraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenerateGraphFragment.
     */
    public static GenerateGraphFragment newInstance(String param1, String param2) {
        //        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
        return new GenerateGraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        generatedMapViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GeneratedMapViewModel.class);
        return inflater.inflate(R.layout.fragment_generate_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        graphGeneratedView = Objects.requireNonNull(getView()).findViewById(R.id.graphGeneratedView);
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        graphGeneratedView.init(metrics);
        if (generatedMapViewModel.getMap() != null) {
            graphGeneratedView.setMap(generatedMapViewModel.getMap());
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    int width = view.getMeasuredWidth();
                    int height = view.getMeasuredHeight();
                    if (width != 0) graphGeneratedView.setDimensionsForMapGeneratorAndGenerateRandomMap(height, width);
                    disableListener = true;
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof GraphListener) {
//            mListener = (GraphListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement GraphListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Map map = graphGeneratedView.getMap();
        generatedMapViewModel.setMap(map);
//        mListener = null;
    }


}
