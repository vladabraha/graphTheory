package cz.uhk.graphstheory.second;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.PaintView;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.SpecificGraphGenerator;

public class SecondActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface,
        DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface, PaintView.CommunicationInterface {

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
    int height, width;

    boolean userFinishedPreviousTask, viewCreated = false;

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

        textFragment.setEducationText(R.string.second_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isValid;
                final int[] displayedActivity = new int[1];
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                displayedActivity[0] = sharedPref.getInt("displayedActivity-second", 0);
                switch (displayedActivity[0]) {
                    case 0:
                        isValid = GraphChecker.checkIfGraphContainsBridge(drawingFragment.getUserGraph());
                        switch (isValid) {
                            case "true":
                                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                assert userName != null;
                                Double receivedPoints = databaseConnector.recordUserPoints(userName, "second-first");
                                Toast.makeText(SecondActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                                createDialog();
                                break;
                            case "false":
                                Toast.makeText(SecondActivity.this, "Jejda, špatně, mkrni na to ještě jednou. Ujisti se také, že je graf spojitý", Toast.LENGTH_LONG).show();
                                break;
                            case "chybi ohraniceni cervenou carou":
                                Toast.makeText(SecondActivity.this, "Zapomněl jsi označit most červenou čarou", Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 1:
                        isValid = GraphChecker.checkIfGraphContainsArticulation(drawingFragment.getUserGraph());
                        switch (isValid) {
                            case "true":
                                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                assert userName != null;
                                Double receivedPoints;
                                Toast.makeText(SecondActivity.this, "Správně!", Toast.LENGTH_LONG).show();
                                receivedPoints = databaseConnector.recordUserPoints(userName, "second-second");
                                Toast.makeText(SecondActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                                createDialog();
                                break;
                            case "false":
                                Toast.makeText(SecondActivity.this, "Jejda, špatně, mkrni na to ještě jednou. Ujisti se také, že je graf spojitý", Toast.LENGTH_LONG).show();
                                break;
                            case "chyba v poctu cervenych bodu":
                                Toast.makeText(SecondActivity.this, "Špatně jsi označil artikulaci, má být označena 1 červeným bodem", Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_second); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);

        bottomNavigationView.setSelectedItemId(R.id.circle);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.circle:
                        drawingFragment.changeDrawingMethod("circle");
                        drawingFragment.setShouldBeNodeColorSwitched(false);
                        return true;
                    case R.id.line:
                        drawingFragment.changeDrawingMethod("line");
                        drawingFragment.setShouldBeNodeColorSwitched(false);
                        return true;
                    case R.id.path:
                        drawingFragment.changeDrawingMethod("path");
                        switchColoringNodeAccordingActivity();
                        return true;
                    case R.id.delete:
                        drawingFragment.changeDrawingMethod("remove"); // nechat pro prekliknuti na cernou tecku a pote smazani
                        return true;
                    default:
                        return false; // return true if you want the item to be displayed as the selected item
                }
            }
        });
    }

    private void switchColoringNodeAccordingActivity() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int currentActivity = sharedPref.getInt("displayedActivity-second", 0);
        if (currentActivity == 0) {
            drawingFragment.setShouldBeNodeColorSwitched(false);
        } else {
            drawingFragment.setShouldBeNodeColorSwitched(true);
        }
    }

    private String getDisplayedActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);

        switch (displayedActivity) {
            case 0:
                return "most";
            case 1:
                return "artikulace";
        }
        return "most";
    }

    @Override
    protected Fragment getGraphFragment() {
        secondActivityFragment = new SecondActivityFragment();
        return secondActivityFragment;
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Most");
        tabNames.add("Artikulace");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.second_activity_text);
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
        if (viewCreated) secondActivityFragment.changeGraph("most");
        Toast.makeText(this, "Teď si ukážeme most v grafu", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();

        int displayedActivity = showToastMessageAccordingCurrentActivity();
        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setTitle("označ");

        //pokud uzivatel splnil predchozi ukol, tak se mu nasetuje novej graf, do kteryho muze kreslit (generuje se podle ukazky, ale bez cerveny cary
        if (userFinishedPreviousTask) {
            userFinishedPreviousTask = false;
            setGraphToDrawingFragment(displayedActivity);
        }
    }

    private int showToastMessageAccordingCurrentActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);
        if (displayedActivity == 0) {
            Toast.makeText(this, "Vyznač v grafu most (červenou čarou)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Vyznač v grafu artikulaci (klepnutím na uzel)", Toast.LENGTH_LONG).show();
        }
        return displayedActivity;
    }

    //vygeneruje graf jak do education fragmentu, ale odstraní z toho červený čáry
    private void setGraphToDrawingFragment(int displayedActivity) {
        //set new map to create user graph
        Map map;
        if (displayedActivity == 0) {
            map = SpecificGraphGenerator.createMapWithABridge(height, width, 15);
            map.setRedCircles(new ArrayList<>());
            map.setRedLineList(new ArrayList<>());
        } else {
            map = SpecificGraphGenerator.createMapWithArticulation(height, width, 15);
            map.setRedLineList(new ArrayList<>());
            map.setRedCircles(new ArrayList<>());
        }
        drawingFragment.setUserGraph(map);
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

        setGraphToDrawingFragment(displayedActivity);
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        //nasetovani spravneho grafu po nacteni view (ale metrics se poslou jenom jednou)
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);
        Map map;
        if (displayedActivity == 0) {
            map = SpecificGraphGenerator.createMapWithABridge(height, width, 15);
            map.setRedLineList(new ArrayList<>());
        } else {
            map = SpecificGraphGenerator.createMapWithArticulation(height, width, 15);
            map.setRedCircles(new ArrayList<>());
        }
        drawingFragment.setUserGraph(map);

    }

    @Override
    public void onPositiveButtonClick() {
        drawingFragment.changeDrawingMethod("clear"); //toto vymaže, co uživatel nakreslil, aby nebouchal jenom check, check...
        changeActivity();
        userFinishedPreviousTask = true;
        showToastMessageAccordingCurrentActivity();
        bottomNavigationView.setSelectedItemId(R.id.circle);
        switchColoringNodeAccordingActivity();
    }

    @Override
    public void onNegativeButtonClick() {
        //nasetovani spravneho grafu po nacteni view (ale metrics se poslou jenom jednou)
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-second", 0);
        Map map;
        if (displayedActivity == 0) {
            map = SpecificGraphGenerator.createMapWithABridge(height, width, 15);
        } else {
            map = SpecificGraphGenerator.createMapWithArticulation(height, width, 15);
        }
        map.setRedLineList(new ArrayList<>());
        drawingFragment.setUserGraph(map);
        bottomNavigationView.setSelectedItemId(R.id.circle);
        switchColoringNodeAccordingActivity();
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {
        switch (number) {
            case 0:
                Toast.makeText(this, "Teď si ukážeme most v grafu", Toast.LENGTH_LONG).show();
                secondActivityFragment.changeGraph("most");
                break;
            case 1:
                Toast.makeText(this, "Teď si ukážeme artikulaci v grafu", Toast.LENGTH_LONG).show();
                secondActivityFragment.changeGraph("artikulace");
                break;
        }
    }

    @Override
    public void sentTouchUpCoordinates(Coordinate coordinate) {

    }
}
