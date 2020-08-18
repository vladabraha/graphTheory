package cz.uhk.graphtheory.second;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cz.uhk.graphtheory.abstraction.AbstractFragment;
import cz.uhk.graphtheory.model.GeneratedGraphViewModel;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.SpecificGraphGenerator;


public class SecondActivityFragment extends AbstractFragment {

    private boolean disableListener = false;

    private int width;
    private int height;

    private String type = "";

    private GeneratedGraphViewModel generatedGraphViewModel;

    public SecondActivityFragment() {
        // Required empty public constructor
    }

    public SecondActivityFragment(String type) {
        this.type = type;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(view, savedInstanceState);
        generatedGraphViewModel = getGeneratedGraphViewModel();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!disableListener) {
                    width = view.getMeasuredWidth();
                    height = view.getMeasuredHeight();
                    if (width != 0) {
                        switch (type) {
                            case "artikulace":
                                createArticulation();
                                break;

                            case "most":
                                createBridge();
                                break;
                            default:
                                createBridge();
                                break;
                        }
                    }
                    disableListener = true;
                }
            }
        });
    }

    private void createArticulation() {
        Graph graphToSet = SpecificGraphGenerator.createGraphWithArticulation(height, width, getGraphGeneratedView().getBrushSize());
        getGraphGeneratedView().setGraph(graphToSet);
        generatedGraphViewModel.setGraph(graphToSet); //pro viemodel
    }

    private void createBridge() {
        Graph graphToSet = SpecificGraphGenerator.createGraphWithABridge(height, width, getGraphGeneratedView().getBrushSize());
        getGraphGeneratedView().setGraph(graphToSet);
        generatedGraphViewModel.setGraph(graphToSet);
    }

    public void changeGraph(String type) {
        this.type = type; //toto se prohodi pri nacteni
        switch (type) {
            case "artikulace":
                createArticulation();
                break;

            case "most":
                createBridge();
                break;
        }
    }
}

