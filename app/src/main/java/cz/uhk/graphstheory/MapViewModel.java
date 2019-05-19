package cz.uhk.graphstheory;

import android.arch.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {
    public Map map;


    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
