package cz.uhk.graphstheory.model;

import java.util.HashMap;

public class User implements Comparable{

    private String email;
    private String team;
    private double score;
    private int unlockTopics;
    private String uuID;
    private HashMap<String, Double> remainingPointsFromActivity;
    private String nickName;

    public User(String email, String team, double score, int unlockTopics, String uuID, HashMap<String, Double> remainingPointsFromActivity, String nickName) {
        this.email = email;
        this.team = team;
        this.score = score;
        this.unlockTopics = unlockTopics;
        this.uuID = uuID;
        this.remainingPointsFromActivity = remainingPointsFromActivity;
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public HashMap<String, Double> getRemainingPointsFromActivity() {
        return remainingPointsFromActivity;
    }

    public void setRemainingPointsFromActivity(HashMap<String, Double> remainingPointsFromActivity) {
        this.remainingPointsFromActivity = remainingPointsFromActivity;
    }

    public String getUuID() {
        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getUnlockTopics() {
        return unlockTopics;
    }

    public void setUnlockTopics(int unlockTopics) {
        this.unlockTopics = unlockTopics;
    }

    public User() {
    }

    @Override
    public int compareTo(Object o) {
        double comparedUserScore = ((User) o).getScore();
        double difference = comparedUserScore - this.score;
        return (int) difference;
    }
}
