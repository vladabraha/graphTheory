package cz.uhk.graphstheory.second;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.util.GraphChecker;

public class SecondActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TabLayoutFragment tabLayoutFragment;
    private SecondActivityFragment secondActivityFragment;

    private DrawingFragmentListener drawingFragmentListener;
    String type;

    DatabaseConnector databaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseConnector = new DatabaseConnector();
        type = getDisplayedActivity();

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        //get instance of abstraction object
        textFragment = getTextFragment();
        drawingFragment = getDrawingFragment();
        educationGraphFragment = getGenerateGraphFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();
        tabLayoutFragment = getTabLayoutFragment();

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);
            String isValid;
            switch (displayedActivity){
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsArticulation(drawingFragment.getUserGraph());
                    switch (isValid) {
                        case "true":
                            String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            assert userName != null;
                            Double receivedPoints;
                            Toast.makeText(SecondActivity.this, "Správně!", Toast.LENGTH_LONG).show();
                            receivedPoints = databaseConnector.recordUserPoints(userName, "fifth-first");
                            Toast.makeText(SecondActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                            tabLayoutFragment.switchSelectedTab(1);
                            drawingFragment.changeDrawingMethod("clear"); //toto vymaže, co uživatel nakreslil, aby nebouchal jenom check, check...

                            changeActivity();
                            break;
                        case "false":
                            Toast.makeText(SecondActivity.this, "Jejda, špatně, mkrni na to ještě jednou", Toast.LENGTH_LONG).show();
                            break;
                        case "chybi ohraniceni cervenou carou":
                            Toast.makeText(SecondActivity.this, "Zapomněl jsi označit artiákulaci červenou čarou", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case 1:
                    //todo zmenit kontrolu grafu
                    isValid = GraphChecker.checkIfGraphContainsArticulation(drawingFragment.getUserGraph());
                    switch (isValid) {
                        case "true":
                            String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            assert userName != null;
                            Double receivedPoints;
                            Toast.makeText(SecondActivity.this, "Správně!", Toast.LENGTH_LONG).show();
                            receivedPoints = databaseConnector.recordUserPoints(userName, "fifth-second");
                            Toast.makeText(SecondActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                            tabLayoutFragment.switchSelectedTab(1);
                            drawingFragment.changeDrawingMethod("clear"); //toto vymaže, co uživatel nakreslil, aby nebouchal jenom check, check...

                            changeActivity();
                            break;
                        case "false":
                            Toast.makeText(SecondActivity.this, "Jejda, špatně, mkrni na to ještě jednou", Toast.LENGTH_LONG).show();
                            break;
                        case "chybi ohraniceni cervenou carou":
                            Toast.makeText(SecondActivity.this, "Zapomněl jsi označit artiákulaci červenou čarou", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }

        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.dalsi); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);
        bottomNavigationView.setSelectedItemId(R.id.circle);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.circle:
                        drawingFragment.changeDrawingMethod("circle");
                        return true;
                    case R.id.line:
                        drawingFragment.changeDrawingMethod("line");
                        return true;
                    case R.id.path:
                        drawingFragment.changeDrawingMethod("path");
                        return true;
                    case R.id.delete:
                        drawingFragment.changeDrawingMethod("remove");
                        return true;
                    case R.id.clear:
                        drawingFragment.changeDrawingMethod("clear");
                        bottomNavigationView.setSelectedItemId(R.id.circle);
                        drawingFragment.changeDrawingMethod("circle");
                        return false; // return true if you want the item to be displayed as the selected item
                    default:
                        return false;
                }
            }
        });
    }

    private String getDisplayedActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);

        switch (displayedActivity) {
            case 0:
                return "most";
            case 1:
                return "most";
        }
        return "artikulace";
    }

    @Override
    protected Fragment getGraphFragment() {
        secondActivityFragment = new SecondActivityFragment();
        return secondActivityFragment;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        //todo dodelat predani spravneho stringu
        textFragment.setEducationText("tada");
    }

    @Override
    protected void showBottomNavigationView() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideBottomNavigationView() {
        bottomNavigationView.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void changeActivity() {

        //do shared preferences si ukladame posledni otevrenou aktivitu, abychom se mohli tocit do kolecka a neotevirali pripadne porad stejnou aktivitu
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);

        if (displayedActivity < 1) {
            displayedActivity++;
        } else {
            displayedActivity = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("displayedActivity-second", displayedActivity);
        editor.apply();


        switch (displayedActivity) {
            case 0:
                Toast.makeText(this, "Teď si ukážeme artikulaci v grafu", Toast.LENGTH_LONG).show();
                secondActivityFragment.changeGraph("most");
                break;
            case 1:
                Toast.makeText(this, "Teď si ukážeme most v grafu", Toast.LENGTH_LONG).show();
                secondActivityFragment.changeGraph("artikulace");
                break;
        }
    }
}
