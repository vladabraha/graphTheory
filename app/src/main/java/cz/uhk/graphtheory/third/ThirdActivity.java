package cz.uhk.graphtheory.third;

import android.content.Intent;
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
import cz.uhk.graphtheory.fourth.FourthActivity;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphConverter;
import cz.uhk.graphtheory.util.GraphGenerator;
import cz.uhk.graphtheory.util.PathGenerator;

public class ThirdActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    DatabaseConnector databaseConnector;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ThirdActivityFragment thirdActivityFragment;
    Graph graphToCheck;
    boolean isAttached;

    int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseConnector = new DatabaseConnector();

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        //get instance of abstraction object
        textFragment = getTextFragment();
        drawingFragment = getDrawingFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();

        textFragment.setEducationText(R.string.third_activity_text);

        floatingActionButton.setOnClickListener(v -> {
            if (GraphChecker.checkIfGraphIsComplementGraph(drawingFragment.getUserGraph(), graphToCheck)) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints = databaseConnector.recordUserPoints(userName, "third");
                Toast.makeText(ThirdActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                drawingFragment.changeDrawingMethod("clear");
                createDialog();
            } else {
                Toast.makeText(ThirdActivity.this, "To není správně, změň graf a zkus to znovu", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_third); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);
        bottomNavigationView.setSelectedItemId(R.id.path);
    }

    @Override
    protected Fragment getGraphFragment() {
        thirdActivityFragment = new ThirdActivityFragment();
        return thirdActivityFragment;
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Doplněk grafu");
        return tabNames;
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        if (isAttached) drawingFragment.changeDrawingMethod("path");
        showSnackBar( "Nakresli doplněk grafu");

        //hack - wait 0.5 sec if drawing fragment is already set and if not wait another 0.5
        waitForDrawingFragment("path");

        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setTitle("doplněk");
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.third_activity_text);
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        showSnackBar( "Červenou čarou je vidět ukázka doplňku grafu");
    }

    @Override
    protected void showBottomNavigationView() {
        bottomNavigationView.setVisibility(View.INVISIBLE);
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
    public void sentMetrics(int width, int height) {
        drawingFragment.changeDrawingMethod("path");
        isAttached = true;
        this.width = width;
        this.height = height;
        createComplementGraph();
    }

    private void createComplementGraph() {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Graph firstGraph = GraphGenerator.generateGraph(height, width, 15, amountOfNodes);

        ArrayList<Graph> graphs = GraphConverter.convertGraphsToSplitScreenArray(firstGraph, height);
        Graph splittedGraph = graphs.get(0);
        Graph splittedGraph2 = graphs.get(1);

        //tohle je pro ulozeni si celýho grafu bez vymazanejch čar, abych na tom mohl zavolat jenom dogenerovani do uplnyho grafu a pak to mohl porovnat
        Graph secondGraph = new Graph(splittedGraph2);
        graphToCheck = PathGenerator.createComplementToGraph(secondGraph); //v tomhle bude mapa, vuci ktere budeme kreslit to, co vyvtvoril uzivatel

        splittedGraph2.setEdges(new ArrayList<>());

        splittedGraph.getNodes().addAll(splittedGraph2.getNodes());
        splittedGraph.getEdges().addAll(splittedGraph2.getEdges());
        splittedGraph.getRedEdgesList().addAll(splittedGraph2.getRedEdgesList());

        drawingFragment.setUserGraph(splittedGraph);
    }

    @Override
    public void onPositiveButtonClick() {
        Intent newActivityIntent = new Intent(ThirdActivity.this, FourthActivity.class);
        newActivityIntent.putExtra("SESSION_ID", 4);
        finish();
        startActivity(newActivityIntent);
    }

    @Override
    public void onNegativeButtonClick() {
        createComplementGraph();
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }
}
