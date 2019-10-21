package cz.uhk.graphstheory.common;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphstheory.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondaryTabLayoutFragment extends Fragment {

    private SecondaryTableLayoutCommunicationInterface secondaryTableLayoutCommunicationInterface;
    private TabLayout tabLayout;
    private ArrayList<String> tabName;

    public SecondaryTabLayoutFragment() {
        // Required empty public constructor
    }

    public SecondaryTabLayoutFragment(ArrayList<String> tabName) {
        this.tabName = tabName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_secondary_tab_layout, container, false);
        tabLayout =  view.findViewById(R.id.secondaryTabLayout);

        if (tabName != null && (!tabName.isEmpty())){
            for (String text : tabName){
                tabLayout.addTab(tabLayout.newTab().setText(text));
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                Objects.requireNonNull(tab).select();
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                secondaryTableLayoutCommunicationInterface.secondaryTableLayoutSelectedChange(tab.getPosition());
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
            secondaryTableLayoutCommunicationInterface = (SecondaryTableLayoutCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SecondaryTableLayoutCommunicationInterface");
        }
    }

    public void switchSelectedTab(int tabId){
        TabLayout.Tab tab = tabLayout.getTabAt(tabId);
        Objects.requireNonNull(tab).select();

    }

    public interface SecondaryTableLayoutCommunicationInterface {
        public void secondaryTableLayoutSelectedChange(int number);
    }

}
