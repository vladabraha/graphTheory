package cz.uhk.graphtheory.statistics;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractAppCompactActivity;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.model.User;

public class StatisticsActivity extends AbstractAppCompactActivity implements StatisticsTab.TableLayoutCommunicationInterface, NavigationView.OnNavigationItemSelectedListener {

    private StatisticsTab statisticsTab;
    private StatisticFragment statisticFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseConnector databaseConnector;
    private String loggedUserEmail;
    TextView navigationDrawerName, navigationDrawerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        databaseConnector = new DatabaseConnector();

        loggedUserEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_statistic);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_statistic); //tady treba hodit, co se ma zvyraznit
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void tableLayoutSelectedChange(int number) {
        if (number == 0 ) statisticFragment.tabLayoutChange(number);
        if (number == 1 ) statisticFragment.tabLayoutChange(number, loggedUserEmail);
        if (number == 2 ) statisticFragment.tabLayoutChange(number, loggedUserEmail);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //set current user to navigation drawer
        navigationDrawerName = findViewById(R.id.navigation_header_name);
        navigationDrawerEmail = findViewById(R.id.navigation_header_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigationDrawerEmail.setText(currentUser.getEmail());
            User user = databaseConnector.findUser(Objects.requireNonNull(currentUser.getEmail()));
            if (user != null) {
                navigationDrawerName.setText(user.getNickName());
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

}
