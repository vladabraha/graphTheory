package com.example.graphstheory;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableLayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableLayoutFragment extends Fragment {

    TableLayoutCommunicationInterface tableLayoutCommunicationInterface;

    public TableLayoutFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TableLayoutFragment newInstance(String param1, String param2) {

        return new TableLayoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_layout, container, false);

        TabLayout tabLayout =  view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Text"));
        tabLayout.addTab(tabLayout.newTab().setText("Ukázka"));
        tabLayout.addTab(tabLayout.newTab().setText("Procvičování"));

        TabLayout.Tab tab = tabLayout.getTabAt(2);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            tableLayoutCommunicationInterface = (TableLayoutCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface TableLayoutCommunicationInterface{
        public void tableLayoutSelectedChange(int number);
    }

}
