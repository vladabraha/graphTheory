package com.example.graphstheory;


import android.graphics.Path;

public class FingerPath {
    public int color;
    public int strokeWidth;
    public Path path;

    public int getColor() {
        return color;
    }

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;


    }
}
