package cz.uhk.graphtheory.ninth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractActivity;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.statistics.StatisticsActivity;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;

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

    private Graph generatedGraph;


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
            String isValid = GraphChecker.checkIfGraphIsSpanningTree(drawingFragment.getUserGraph(), generatedGraph);
            switch (isValid) {
                case "true":
                    String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    assert userName != null;
                    Double receivedPoints = databaseConnector.recordUserPoints(userName, "ninth");
                    Toast.makeText(NinthActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                    createDialog();
                    break;
                case "false":
                    Toast.makeText(NinthActivity.this, "bohužel, to není správně, oprav se a zkus to znovu", Toast.LENGTH_LONG).show();
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
        showSnackBar("Kostra grafu je v grafu zvýrazněna červenou čarou");
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
    public void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Gratulujeme");
        dialog.setMessage("Jste na konci, prošli jste všemi výukovými materiály, které tato aplikace zatím nabízí. Můžete se kdykoliv vrátit ");
        dialog.setNegativeButton("Znovu procvičit", (dialog12, which) -> onNegativeButtonClick());
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent newActivityIntent = new Intent(NinthActivity.this, StatisticsActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 99);
                finish();
                startActivity(newActivityIntent);
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void onNegativeButtonClick() {
        generateNewSpanningTree(height, width);
        showSnackBar("Nakresli v zadaném grafu kostru, případně si graf uprav ");
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        showSnackBar("Nakresli v zadaném grafu kostru, případně si graf uprav ");
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        generateNewSpanningTree(height, width);

        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setTitle("kostra");
    }

    private void generateNewSpanningTree(int height, int width) {
        final int MAXIMUM_AMOUNT_OF_NODES = 9;
        final int MINIMUM_AMOUNT_OF_NODES = 4;
        int amountOfNodes = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfNodes < MINIMUM_AMOUNT_OF_NODES) amountOfNodes = MINIMUM_AMOUNT_OF_NODES;

        Graph graphToSet = GraphGenerator.generateGraph(height, width, 15, amountOfNodes);

        generatedGraph = new Graph(graphToSet);
        drawingFragment.setUserGraph(graphToSet);
        bottomNavigationView.setSelectedItemId(R.id.path);
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }

}
