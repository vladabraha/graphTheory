package cz.uhk.graphtheory.statistics;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.common.TabLayoutFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsTab extends Fragment {

    private StatisticsTab.TableLayoutCommunicationInterface tableLayoutCommunicationInterface;
    private TabLayout tabLayout;


    public StatisticsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics_tab, container, false);
        tabLayout =  view.findViewById(R.id.statisticsTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Pořadí hráčů"));
        tabLayout.addTab(tabLayout.newTab().setText("Pořádí v týmu"));
        tabLayout.addTab(tabLayout.newTab().setText("Pořadí týmů"));

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        Objects.requireNonNull(tab).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tableLayoutCommunicationInterface.tableLayoutSelectedChange(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public static TabLayoutFragment newInstance(String param1, String param2) {

        return new TabLayoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            tableLayoutCommunicationInterface = (StatisticsTab.TableLayoutCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TableLayoutCommunicationInterface");
        }
    }

    public void switchSelectedTab(int tabId){
        TabLayout.Tab tab = tabLayout.getTabAt(tabId);
        Objects.requireNonNull(tab).select();

    }

    public interface TableLayoutCommunicationInterface{
        public void tableLayoutSelectedChange(int number);
    }


}
