package cz.uhk.graphtheory.abstraction;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.common.GraphGeneratedView;
import cz.uhk.graphtheory.model.GeneratedMapViewModel;
import cz.uhk.graphtheory.model.Map;

public abstract class AbstractFragment extends Fragment {

    private GraphGeneratedView graphGeneratedView;
    private GeneratedMapViewModel generatedMapViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getView modal for current activity
        generatedMapViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity(), "Activity for ViewModel musn't be null")).get(GeneratedMapViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        graphGeneratedView = Objects.requireNonNull(getView()).findViewById(R.id.eductionGraphView);
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        graphGeneratedView.init(metrics);
        if (generatedMapViewModel.getMap() != null) {
            graphGeneratedView.setMap(generatedMapViewModel.getMap());
        }

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //vhodi chybu pokud neni implementovany listener
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
        //set current map to view modal when fragment detached
        Map map = graphGeneratedView.getMap();
        generatedMapViewModel.setMap(map);
    }

    public GraphGeneratedView getGraphGeneratedView() {
        return graphGeneratedView;
    }

    public GeneratedMapViewModel getGeneratedMapViewModel() {
        return generatedMapViewModel;
    }


}
