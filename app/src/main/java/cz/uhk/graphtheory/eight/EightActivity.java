package cz.uhk.graphtheory.eight;

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
import java.util.Random;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractActivity;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphtheory.model.Map;
import cz.uhk.graphtheory.ninth.NinthActivity;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;
import cz.uhk.graphtheory.util.Util;

public class EightActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;
    private TabLayoutFragment tabLayoutFragment;

    private ArrayList<Integer> graphScore = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EightActivityFragment eightActivityFragment;

    int height, width, amountOfNodes, amountOfComponent;

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

        textFragment.setEducationText(R.string.eighth_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            boolean isValid = GraphChecker.checkIfGraphHasCertainAmountOfComponent(drawingFragment.getUserGraph(), amountOfComponent);
//            boolean isValid = GraphChecker.checkIfGraphDoesNotContainsCycle(drawingFragment.getUserGraph());
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints = databaseConnector.recordUserPoints(userName, "eight");
                Toast.makeText(EightActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
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
        eightActivityFragment = new EightActivityFragment();
        return eightActivityFragment;
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Strom");
        tabNames.add("Les");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.eighth_activity_text);
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        showSnackBar( "Teď si ukážeme strom");
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
        Intent newActivityIntent = new Intent(this, NinthActivity.class);
        newActivityIntent.putExtra("SESSION_ID", 9);
        finish();
        startActivity(newActivityIntent);
    }

    @Override
    public void onNegativeButtonClick() {
        amountOfNodes = getAmountOfNodesAndGenerateGraphScore();
        Map map = new Map(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(map);
        bottomNavigationView.setSelectedItemId(R.id.line);
        generateNewMessageWithNewAmountOfComponent();
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        if (amountOfComponent == 0) {
            generateNewMessageWithNewAmountOfComponent();
        }else {
            if (amountOfComponent == 1 ){
                showSnackBar( "Nakresli les s jednou komponentou");
            }else {
                showSnackBar( "Nakresli les s " + amountOfComponent + " komponentami");
            }
        }
    }

    private void generateNewMessageWithNewAmountOfComponent() {
        Random ran = new Random();
        amountOfComponent = ran.nextInt(3);
        if (amountOfComponent == 0) amountOfComponent++;
        if (amountOfComponent == 1 ){
            showSnackBar( "Nakresli les s jednou komponentou");
        }else {
            showSnackBar( "Nakresli les s " + amountOfComponent + " komponentami");
        }
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;

        amountOfNodes = getAmountOfNodesAndGenerateGraphScore();
        Map map = new Map(new ArrayList<>(), GraphGenerator.generateNodes(height, width, 15, amountOfNodes));
        drawingFragment.setUserGraph(map);
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
    public void secondaryTableLayoutSelectedChange(int number) {
        switch (number) {
            case 0:
                showSnackBar( "Teď si ukážeme strom");
                eightActivityFragment.changeGraph("tree");
                break;
            case 1:
                showSnackBar( "Teď si ukážeme les");
                eightActivityFragment.changeGraph("forrest");
                break;
        }
    }

}
