package cz.uhk.graphtheory.model;

import androidx.annotation.NonNull;

public class Team implements Comparable{

    private String name;
    private double score;

    public Team(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        double comparedUserScore = ((Team) o).getScore();
        double difference = comparedUserScore - this.score;
        return (int) difference;
    }
}
