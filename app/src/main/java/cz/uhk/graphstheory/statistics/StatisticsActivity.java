package cz.uhk.graphstheory.statistics;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractAppCompactActivity;
import cz.uhk.graphstheory.database.DatabaseConnector;

public class StatisticsActivity extends AbstractAppCompactActivity implements StatisticsTab.TableLayoutCommunicationInterface, NavigationView.OnNavigationItemSelectedListener {

    private StatisticsTab statisticsTab;
    private StatisticFragment statisticFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private DatabaseConnector databaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

//        databaseConnector = new DatabaseConnector();

        //for navigation drawer and top drawer
        Toolbar toolbar = findViewById(R.id.statistic_toolbar);
        setSupportActionBar(toolbar);

        statisticFragment = new StatisticFragment();
        statisticsTab = new StatisticsTab();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.statisticLayout, statisticsTab);
        fragmentTransaction.add(R.id.statisticLayout, statisticFragment);
        fragmentTransaction.commit();

        //drawer menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout_statistic);
        NavigationView navigationView = findViewById(R.id.nav_view_statistic);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void tableLayoutSelectedChange(int number) {

    }


}
