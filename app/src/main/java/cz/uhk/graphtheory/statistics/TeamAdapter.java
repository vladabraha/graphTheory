package cz.uhk.graphtheory.statistics;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.model.Team;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private ArrayList<Team> teams;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class TeamViewHolder extends RecyclerView.ViewHolder {

        //tady si vytahneme veci z xml, ktery budeme chtit pro kazdej zaznam zmenit
        TextView teamName, teamScore;

        public TeamViewHolder(View view) {
            super(view);
            teamName = view.findViewById(R.id.txt_teamName);
            teamScore = view.findViewById(R.id.txt_teamScore);
        }
    }

    // Konstruktor pro předání dat, která budeme zobrazovat
    public TeamAdapter(ArrayList<Team> teams) {
        this.teams = teams;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TeamAdapter.TeamViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // ukaž na view, který se bude opakovat (jeden radek RecyclerView)
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_team_row, parent, false);

        return new TeamAdapter.TeamViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TeamAdapter.TeamViewHolder holder, int position) {
        // tady nasetuj data pro jeden radek z listu dat, ktera mame k dispozici
        Team team = teams.get(position);
        holder.teamName.setText(team.getName());
        holder.teamScore.setText(String.valueOf(team.getScore()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void updateData(ArrayList<Team> teams) {
        this.teams = teams;
    }


}
