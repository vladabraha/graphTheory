package cz.uhk.graphtheory.sixth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractActivity;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphtheory.model.Map;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.PathGenerator;

public class SixthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TabLayoutFragment tabLayoutFragment;
    private SixthActivityFragment sixthActivityFragment;

    private DrawingFragmentListener drawingFragmentListener;
    String type;
    int height, width;
    private boolean isViewCreated;

    Map eulerMap, hamiltonMap;

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

        textFragment.setEducationText(R.string.sixth_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity-sixth", 0);
            String isValid;
            switch (displayedActivity) {
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsHamiltonCircle(drawingFragment.getUserGraph());
                    switch (isValid) {
                        case "true":
                            String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            assert userName != null;
                            Double receivedPoints = databaseConnector.recordUserPoints(userName, "sixth-first");
                            Toast.makeText(SixthActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                            createDialog();
                            break;
                        case "false":
                            Toast.makeText(SixthActivity.this, "Jejda, špatně, mkrni na to ještě jednou", Toast.LENGTH_LONG).show();
                            drawingFragment.setUserGraph(hamiltonMap);
                            break;
                        case "chybi ohraniceni cervenou carou":
                            Toast.makeText(SixthActivity.this, "Zapomněl jsi označit kružnici červenou čarou", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case 1:
                    isValid = GraphChecker.checkIfGraphHasEulerPath(drawingFragment.getUserGraph());
                    switch (isValid) {
                        case "true":
                            String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            assert userName != null;
                            Double receivedPoints;
                            Toast.makeText(SixthActivity.this, "Správně!", Toast.LENGTH_LONG).show();
                            receivedPoints = databaseConnector.recordUserPoints(userName, "sixth-second");
                            Toast.makeText(SixthActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                            createDialog();
                            break;
                        case "false":
                            Toast.makeText(SixthActivity.this, "Jejda, špatně, mkrni na to ještě jednou", Toast.LENGTH_LONG).show();
                            drawingFragment.setUserGraph(eulerMap);
                            break;
                        case "chybi ohraniceni cervenou carou":
                            Toast.makeText(SixthActivity.this, "Zapomněl jsi označit kružnici červenou čarou", Toast.LENGTH_LONG).show();
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
        navigationView.setCheckedItem(R.id.nav_sixth); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);
        bottomNavigationView.setSelectedItemId(R.id.circle);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.circle:
                    drawingFragment.changeDrawingMethod("circle");
                    return true;
                case R.id.circle_move:
                    drawingFragment.changeDrawingMethod("circle_move");
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
                default:
                    return false;// return true if you want the item to be displayed as the selected item
            }
        });
    }

    private String getDisplayedActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-sixth", 0);

        switch (displayedActivity) {
            case 0:
                return "hamiltonovsky";
            case 1:
                return "euleruv";
            default:
                return "hamiltonovsky";
        }
    }

    @Override
    protected Fragment getGraphFragment() {
        sixthActivityFragment = new SixthActivityFragment(getDisplayedActivity());
        return sixthActivityFragment;
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Hamiltonovská kružnice");
        tabNames.add("Eulerův tah");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.sixth_activity_text);
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
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        switch (getDisplayedActivity()) {
            case "euleruv":
                showSnackBar("Teď si ukážeme euleruv tah v grafu");
                break;
            case "hamiltonovsky":
                showSnackBar("Teď si ukážeme hamiltonovskou kruznici v grafu");
                break;
        }
        if (isViewCreated) sixthActivityFragment.changeGraph("hamiltonovsky");
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        showToastMessageAccordingCurrentActivity();
        //hack - wait 0.5 sec if drawing fragment is already set and if not wait another 0.5
        waitForDrawingFragment("path");
    }

    private void showToastMessageAccordingCurrentActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-sixth", 0);
        if (displayedActivity == 0) {
            showSnackBar("Vyznač v grafu hamiltonovskou kružnici");
        } else {
            showSnackBar("Vyznač v grafu eulerův tah, postupně jak jde za sebou");
        }
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
        int displayedActivity = sharedPref.getInt("displayedActivity-sixth", 0);

        if (displayedActivity < 1) {
            displayedActivity++;
        } else {
            displayedActivity = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("displayedActivity-sixth", displayedActivity);
        editor.apply();

        setGraphAccordingCurrentActivity(width, height);
        showToastMessageAccordingCurrentActivity();
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;
        setGraphAccordingCurrentActivity(width, height);
        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setTitle("označit");
        drawingFragment.changeDrawingMethod("path");
        isViewCreated = true;
    }

    private void setGraphAccordingCurrentActivity(int width, int height) {
        switch (getDisplayedActivity()) {
            case "euleruv":
                eulerMap = PathGenerator.createEulerMapWithoutRedLines(height, width);
                drawingFragment.setUserGraph(eulerMap);
                bottomNavigationView.setSelectedItemId(R.id.path);
                break;
            case "hamiltonovsky":
                hamiltonMap = PathGenerator.createHamiltonMapWithoutRedLines(height, width);
                drawingFragment.setUserGraph(hamiltonMap);
                bottomNavigationView.setSelectedItemId(R.id.path);
                break;
        }
    }

    @Override
    public void onPositiveButtonClick() {
        drawingFragment.changeDrawingMethod("clear"); //toto vymaže, co uživatel nakreslil, aby nebouchal jenom check, check...
        tabLayoutFragment.switchSelectedTab(1);
        changeActivity();
    }

    @Override
    public void onNegativeButtonClick() {
        showToastMessageAccordingCurrentActivity();
        switch (getDisplayedActivity()) {
            case "euleruv":
                drawingFragment.setUserGraph(PathGenerator.createEulerMapWithoutRedLines(height, width));
                break;
            case "hamiltonovsky":
                drawingFragment.setUserGraph(PathGenerator.createHamiltonMapWithoutRedLines(height, width));
                break;
        }

    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {
        switch (number) {
            case 0:
                showSnackBar("Teď si ukážeme hamiltonovskou kružnici v grafu");
                sixthActivityFragment.changeGraph("hamiltonovsky");
                break;
            case 1:
                showSnackBar("Teď si ukážeme eulerův tah v grafu");
                sixthActivityFragment.changeGraph("euleruv");
                break;
        }
    }
}
