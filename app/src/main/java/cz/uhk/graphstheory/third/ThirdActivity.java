package cz.uhk.graphstheory.third;

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

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.fourth.FourthActivity;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphConverter;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.PathGenerator;

public class ThirdActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private TabLayoutFragment tabLayoutFragment;
    private DrawingFragmentListener drawingFragmentListener;
    DatabaseConnector databaseConnector;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ThirdActivityFragment thirdActivityFragment;
    Map mapToCheck;
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
        educationGraphFragment = getGenerateGraphFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();
        tabLayoutFragment = getTabLayoutFragment();

        textFragment.setEducationText(R.string.third_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            if (checkIfGraphIsComplementGraph(drawingFragment.getUserGraph())) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints = databaseConnector.recordUserPoints(userName, "third");
                Toast.makeText(ThirdActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Nakresli doplněk grafu", Toast.LENGTH_LONG).show();

        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(2).setTitle("doplněk");
        menu.getItem(4).setTitle("");
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.third_activity_text);
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        Toast.makeText(this, "Červenou čarou je vidět ukázka doplňku grafu", Toast.LENGTH_LONG).show();
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
    public void sentMetrics(int width, int height) {
        drawingFragment.changeDrawingMethod("path");
        isAttached = true;
        this.width = width;
        this.height = height;
        createComplementGraph();
    }

    private void createComplementGraph() {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map firstMap = GraphGenerator.generateMap(height, width, 15, amountOfNodes);

        ArrayList<Map> maps = GraphConverter.convertMapsToSplitScreenArray(firstMap, height);
        Map splittedMap = maps.get(0);
        Map splittedMap2 = maps.get(1);

        //tohle je pro ulozeni si celýho grafu bez vymazanejch čar, abych na tom mohl zavolat jenom dogenerovani do uplnyho grafu a pak to mohl porovnat
        Map secondMap = new Map(splittedMap2);
        mapToCheck = PathGenerator.createComplementToGraph(secondMap); //v tomhle bude mapa, vuci ktere budeme kreslit to, co vyvtvoril uzivatel

        splittedMap2.setCustomLines(new ArrayList<>());

        splittedMap.getCircles().addAll(splittedMap2.getCircles());
        splittedMap.getCustomLines().addAll(splittedMap2.getCustomLines());
        splittedMap.getRedLineList().addAll(splittedMap2.getRedLineList());

        drawingFragment.setUserGraph(splittedMap);
    }

    //myšlenka - pokud vezmu ten prvni graf a připočítám k němu stejnej rozdíl, dostanu stejnej graf
    //na něm porovnám, zdali má červené čáry (doplněk) stejný souřadnice jako když by to generoval algoritmus
    //podmínkou je lock na posunování uzlů
    private boolean checkIfGraphIsComplementGraph(Map userGraph) {
        ArrayList<CustomLine> redLines = userGraph.getRedLineList();
        ArrayList<CustomLine> redLinesToCheck = mapToCheck.getRedLineList();

        if (redLinesToCheck.size() == 0 && redLines.size() > 0) return false;

        for (CustomLine customLine : redLinesToCheck) {
            boolean found = false;
            for (CustomLine redLine : redLines) {
                if (redLine.isLineSame(customLine)){
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
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
