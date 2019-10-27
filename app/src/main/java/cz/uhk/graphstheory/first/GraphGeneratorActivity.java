package cz.uhk.graphstheory.first;

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
import java.util.Random;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;

public class GraphGeneratorActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int height, width, length;
    boolean isViewCreated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseConnector databaseConnector = new DatabaseConnector();

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        //get instance of abstraction object
        textFragment = getTextFragment();
        drawingFragment = getDrawingFragment();
        bottomNavigationView = getBottomNavigationView();
        FloatingActionButton floatingActionButton = getFloatingActionButton();

        textFragment.setEducationText(R.string.first_activity_text);

        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity", 0);
            boolean isValid = false;
            switch (displayedActivity) {
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsCesta(drawingFragment.getUserGraph());
                    break;
                case 1:
                    isValid = GraphChecker.checkIfGraphContainsTah(drawingFragment.getUserGraph());
                    break;
                case 2:
                    isValid = GraphChecker.checkIfGraphContainsCycle(drawingFragment.getUserGraph());
                    break;
                case 3:
                    isValid = GraphChecker.checkIfGraphContainsSled(drawingFragment.getUserGraph(), length);
                    break;
            }
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints;
                switch (displayedActivity) {
                    case 0:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-first");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-second");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-third");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-fourth");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                }
                drawingFragment.changeDrawingMethod("clear");
                bottomNavigationView.setSelectedItemId(R.id.circle);
                drawingFragment.changeDrawingMethod("circle");
                createDialog();
            } else {
                Toast.makeText(GraphGeneratorActivity.this, "Bohužel, něco je špatně, oprav graf a zkus to znovu", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_first); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);
        bottomNavigationView.setSelectedItemId(R.id.path);
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

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Cesta");
        tabNames.add("Tah");
        tabNames.add("Kužnice");
        tabNames.add("Sled");
        return tabNames;
    }

    private void showProperToastMessage(int displayedActivity) {
        switch (displayedActivity) {
            case 0:
                Toast.makeText(this, "Nakresli cestu v grafu, případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this, "Nakresli tah v grafu, případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "Nakresli kružnici v grafu, případně si graf uprav. Nezapoměň, že kružnice musí končit v bodě, ze kterého vychází", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Random ran = new Random();
                length = ran.nextInt(7);
                Toast.makeText(this, "Nakresli sled délky " + length + " , případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finishAffinity(); //exit the app
        }
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);
        showProperToastMessage(displayedActivity);

        if (isViewCreated) {
            setProperTitleToBottomNavigationMenu(displayedActivity);
            drawingFragment.changeDrawingMethod("path");
            bottomNavigationView.setSelectedItemId(R.id.path);
        }
    }

    private void setProperTitleToBottomNavigationMenu(int displayedActivity) {
        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        switch (displayedActivity) {
            case 0:
                menu.getItem(3).setTitle("cesta");
                break;
            case 1:
                menu.getItem(3).setTitle("tah");
                break;
            case 2:
                menu.getItem(3).setTitle("kružnice");
                break;
        }
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        if (isViewCreated) setMapToDrawingFragment(width, height);
        Toast.makeText(this, "Nyní si ukážeme v zadaném grafu cestu", Toast.LENGTH_LONG).show();
    }

    private void changeActivity() {
        //do shared preferences si ukladame posledni otevrenou aktivitu, abychom se mohli tocit do kolecka a neotevirali pripadne porad stejnou aktivitu
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);

        if (displayedActivity < 3) {
            displayedActivity++;
        } else {
            displayedActivity = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("displayedActivity", displayedActivity);
        editor.apply();

        //tady tocime jednotlivy aktivity za zaznamenavame skore (tahle metoda se vola jenom po uspesnym vyplneni)
        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert userName != null;

        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
        showProperToastMessage(displayedActivity);
        setProperTitleToBottomNavigationMenu(displayedActivity);
    }

    @Override
    public void onPositiveButtonClick() {
        changeActivity();
    }

    @Override
    public void onNegativeButtonClick() {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
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
    protected Fragment getGraphFragment() {
        generateGraphFragment = new GenerateGraphFragment();
        return generateGraphFragment;
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;
        setMapToDrawingFragment(width, height);
        isViewCreated = true;
    }

    private void setMapToDrawingFragment(int width, int height) {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
        Menu menu = bottomNavigationView.getMenu();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);

        switch (displayedActivity) {
            case 0:
                menu.getItem(3).setTitle("cesta");
                break;
            case 1:
                menu.getItem(3).setTitle("tah");
                break;
            case 2:
                menu.getItem(3).setTitle("kužnice");
                break;
            case 3:
                menu.getItem(3).setTitle("sled");
                break;
        }
        bottomNavigationView.setSelectedItemId(R.id.path);
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {
        int length;
        switch (number) {
            case 0:
                generateGraphFragment.changeEducationGraph("cesta");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu cestu", Toast.LENGTH_LONG).show();
                break;
            case 1:
                generateGraphFragment.changeEducationGraph("tah");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu tah", Toast.LENGTH_LONG).show();
                break;
            case 2:
                length = generateGraphFragment.changeEducationGraph("kruznice");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu kružnici delky " + length, Toast.LENGTH_LONG).show();
                break;
            case 3:
                length = generateGraphFragment.changeEducationGraph("kruznice");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu sled délky " + length, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
