package cz.uhk.graphstheory.model;

import androidx.lifecycle.ViewModel;

public class DrawMapViewModel extends ViewModel {
    public Map map;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
