package cz.uhk.graphtheory.util;

import java.util.ArrayList;
import java.util.HashMap;

import cz.uhk.graphtheory.model.Team;
import cz.uhk.graphtheory.model.User;

public class FilterData {

    public static ArrayList<User> filterUsersInTeam(ArrayList<User> allUsers, String team){
        ArrayList<User> usersInTeam = new ArrayList<>();

        for (User user: allUsers){
            if (user.getTeam().equals(team)){
                usersInTeam.add(user);
            }
        }
        return usersInTeam;
    }

    public static User findUser(String email, ArrayList<User> users){
        for (User user: users){
            if (user.getEmail().equals(email)) return user;
        }
        return null;
    }

    public static ArrayList<Team> getTeams(ArrayList<User> allUsers){
        ArrayList<Team> teams = new ArrayList<>();
        HashMap<String, Double> scoreForTeams = new HashMap<>();

        for (User user: allUsers){
           if (scoreForTeams.containsKey(user.getTeam())){
               Double teamScore = scoreForTeams.get(user.getTeam());
               teamScore += user.getScore();
               scoreForTeams.put(user.getTeam(), teamScore);
           }else {
               scoreForTeams.put(user.getTeam(), user.getScore());
           }
        }

        scoreForTeams.forEach((teamName, score) -> {
            Team team = new Team(teamName, score);
            teams.add(team);
        });

        return teams;
    }
}
