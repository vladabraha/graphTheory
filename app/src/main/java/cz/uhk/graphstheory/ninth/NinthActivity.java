package cz.uhk.graphstheory.ninth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;

public class NinthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface,
        DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;
    private DrawingFragmentListener drawingFragmentListener;
    private TabLayoutFragment tabLayoutFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Map generatedMap;


    int height, width;

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
        educationGraphFragment = getGenerateGraphFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();
        tabLayoutFragment = getTabLayoutFragment();

        textFragment.setEducationText(R.string.ninth_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            String isValid = GraphChecker.checkIfGraphIsSpanningTree(drawingFragment.getUserGraph(), generatedMap);
            switch (isValid) {
                case "true":
                    String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    assert userName != null;
                    Double receivedPoints = databaseConnector.recordUserPoints(userName, "ninth");
                    Toast.makeText(NinthActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                    createDialog();
                    break;
                case "false":
                    Toast.makeText(NinthActivity.this, "bohužel, to není správně, oprav se a zkus to znovu", Toast.LENGTH_LONG).show();
                    break;
                case "graf":
                    Toast.makeText(NinthActivity.this, "graf je jiný, než byl vygenerován, doplň graf do původní podoby, nebo si nech vygenerovat nový", Toast.LENGTH_LONG).show();
                    break;
                case "cesta":
                    Toast.makeText(NinthActivity.this, "kostra je vedená minimálně v jednom úseku přes neexistující hranu v původním grafu", Toast.LENGTH_LONG).show();
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
        navigationView.setCheckedItem(R.id.nav_ninth); //tady treba hodit, co se ma zvyraznit

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
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected Fragment getGraphFragment() {
        return new NinthActivityFragment();
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Kostra grafu");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.seventh_activity_text);
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        Toast.makeText(this, "Kostra grafu je v grafu zvýrazněna červenou čarou", Toast.LENGTH_LONG).show();

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

    @Override
    public void onPositiveButtonClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Gratulujeme");
        dialog.setMessage("Jste na konci, prošli jste všemi výukovými materiály, které tato aplikace zatím nabízí. Můžete se kdykoliv vrátit ");
        dialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                generateNewSpanningTree(height, width);
            }
        });
    }

    @Override
    public void onNegativeButtonClick() {
        generateNewSpanningTree(height, width);
        Toast.makeText(this, "Nakresli v zadaném grafu kostru ", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        Toast.makeText(this, "Nakresli v zadaném grafu kostru ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        generateNewSpanningTree(height, width);

        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(2).setTitle("kostra");
        menu.getItem(4).setTitle("");
    }

    private void generateNewSpanningTree(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;

        Map mapToSet = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        generatedMap = new Map(mapToSet);
        drawingFragment.setUserGraph(mapToSet);
        bottomNavigationView.setSelectedItemId(R.id.path);
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }

}
