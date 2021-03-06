package cz.uhk.graphtheory.seventh;

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
import cz.uhk.graphtheory.eight.EightActivity;
import cz.uhk.graphtheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;
import cz.uhk.graphtheory.util.Util;

public class SeventhActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface,
        DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface, SeventhActivityFragment.SeventhFragmentActivityCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;
    private TabLayoutFragment tabLayoutFragment;

    private ArrayList<Integer> graphScore = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StringBuilder scoreText;

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

        textFragment.setEducationText(R.string.seventh_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            boolean isValid = GraphChecker.checkIfGraphHasCorrectScore(drawingFragment.getUserGraph(), graphScore);
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints = databaseConnector.recordUserPoints(userName, "seventh");
                Toast.makeText(SeventhActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                createDialog();
            } else {
                Toast.makeText(SeventhActivity.this, "bohužel, to není správně, oprav se a zkus to znovu", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_seventh); //tady treba hodit, co se ma zvyraznit

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
        return new SeventhActivityFragment();
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Skóre grafu");
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
        if (scoreText != null && scoreText.length() != 0) {
            showSnackBar(scoreText.toString());
        }
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
        Graph graph = new Graph(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(graph);
        bottomNavigationView.setSelectedItemId(R.id.line);

        StringBuilder text = new StringBuilder();
        for (Integer score : graphScore) {
            text.append(score.toString()).append(", ");
        }
        text = text.deleteCharAt(text.length() - 1);
        showSnackBar( "Nakresli graf s tímto skórem " + text);
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();

        if (amountOfNodes == 0) getAmountOfNodesAndGenerateGraphScore();

        StringBuilder text = new StringBuilder();
        for (Integer score : graphScore) {
            text.append(score.toString()).append(", ");
        }
        text = text.deleteCharAt(text.length() - 1);
        showSnackBar( "Nakresli graf s tímto skórem " + text);
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        if (amountOfNodes == 0) getAmountOfNodesAndGenerateGraphScore();
        Graph graph = new Graph(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(graph);
        bottomNavigationView.setSelectedItemId(R.id.line);

        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setTitle("");

    }

    private int getAmountOfNodesAndGenerateGraphScore() {
        amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        graphScore = Util.generateGraphScore(amountOfNodes);
        return amountOfNodes;
    }

    @Override
    public void onScoreComputed(ArrayList<Integer> graphScore) {
        scoreText = new StringBuilder("Skóre grafu je ");
        for (Integer integer : graphScore) {
            scoreText.append(integer).append(", ");
        }
        scoreText = scoreText.deleteCharAt(scoreText.length() - 1);
        showSnackBar(scoreText.toString());
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }

}
