package cz.uhk.graphstheory.eight;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;
import cz.uhk.graphstheory.util.Util;

public class EightActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;
    private TabLayoutFragment tabLayoutFragment;

    private ArrayList<Integer> graphScore = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int height, width, amountOfNodes;

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

        textFragment.setEducationText(R.string.sixth_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            boolean isValid = GraphChecker.checkIfGraphIsCorrect(drawingFragment.getUserGraph(), graphScore);
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints = databaseConnector.recordUserPoints(userName, "eight");
                Toast.makeText(EightActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                createDialog();
            } else {
                Toast.makeText(EightActivity.this, "bohužel, to není správně, oprav se a zkus to znovu", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_eighth); //tady treba hodit, co se ma zvyraznit

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

    @Override
    protected Fragment getGraphFragment() {
        return new EightActivityFragment();
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.eighth_activity_text);
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
        Intent newActivityIntent = new Intent(this, EightActivity.class);
        newActivityIntent.putExtra("SESSION_ID", 8);
        finish();
        startActivity(newActivityIntent);
    }

    @Override
    public void onNegativeButtonClick() {
        amountOfNodes = getAmountOfNodesAndGenerateGraphScore();
        Map map = new Map(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(map);
        bottomNavigationView.setSelectedItemId(R.id.line);
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();

        if (amountOfNodes == 0) getAmountOfNodesAndGenerateGraphScore();

        StringBuilder text = new StringBuilder();
        for (Integer score : graphScore) {
            text.append(score.toString()).append(", ");
        }
        Toast.makeText(this, "Nakresli graf s tímto skórem " + text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        amountOfNodes = getAmountOfNodesAndGenerateGraphScore();
        Map map = new Map(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(map);
        bottomNavigationView.setSelectedItemId(R.id.line);


    }

    private int getAmountOfNodesAndGenerateGraphScore() {
        amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        graphScore = Util.generateGraphScore(amountOfNodes);
        return amountOfNodes;
    }
}
