package cz.uhk.graphtheory.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.model.Team;
import cz.uhk.graphtheory.model.User;
import cz.uhk.graphtheory.util.FilterData;

public class StatisticFragment extends Fragment implements DatabaseConnector.ValuesUpdate {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseConnector databaseConnector;

    private UserAdapter userAdapter;
    private ArrayList<User> users;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseConnector = new DatabaseConnector(StatisticFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // view se musi nejdrive nainicializovat, abychom si z neho mohli brat prvky
        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_statistic);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        userAdapter = new UserAdapter(databaseConnector.getUsers());
        mAdapter = userAdapter;
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void usersUpdated(@NotNull ArrayList<User> users) {
        this.users = users;
        Collections.sort(users);
        userAdapter.updateData(users);
        mAdapter.notifyDataSetChanged();
    }

    public void tabLayoutChange(int idTab) {
        if (idTab == 0) {
            mAdapter = userAdapter;
            recyclerView.setAdapter(mAdapter);
            userAdapter.updateData(users);
            mAdapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("unchecked")
    public void tabLayoutChange(int idTab, String currentUserEmail) {
        User currentUser = FilterData.findUser(currentUserEmail, users);
        if (idTab == 1) {
            mAdapter = userAdapter;
            recyclerView.setAdapter(mAdapter);
            ArrayList<User> usersInSameTeam = FilterData.filterUsersInTeam(users, Objects.requireNonNull(currentUser).getTeam());
            Collections.sort(usersInSameTeam);
            userAdapter.updateData(usersInSameTeam);
            mAdapter.notifyDataSetChanged();
        }
        if (idTab == 2){
            ArrayList<Team> teams = FilterData.getTeams(databaseConnector.getUsers());
            Collections.sort(teams);
            mAdapter = new TeamAdapter(teams);
            recyclerView.setAdapter(mAdapter);
        }
    }
}
