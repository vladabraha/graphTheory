package cz.uhk.graphstheory.model;

import android.arch.lifecycle.ViewModel;

import cz.uhk.graphstheory.model.Map;

public class MapViewModel extends ViewModel {
    public Map map;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
