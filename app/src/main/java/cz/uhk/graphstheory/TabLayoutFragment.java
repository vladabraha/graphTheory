package cz.uhk.graphstheory;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabLayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabLayoutFragment extends Fragment {

    TableLayoutCommunicationInterface tableLayoutCommunicationInterface;

    public TabLayoutFragment() {
        // Required empty public constructor
    }


    public static TabLayoutFragment newInstance(String param1, String param2) {

        return new TabLayoutFragment();
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
