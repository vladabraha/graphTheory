package cz.uhk.graphstheory.fifth;

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
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphConverter;
import cz.uhk.graphstheory.util.GraphGenerator;

public class FifthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        textFragment.setEducationText(getString(R.string.fifth_activity_text));

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            boolean isValid = GraphChecker.checkIfGraphIsBipartite(drawingFragment.getUserGraph());
            if (isValid) {
                Toast.makeText(FifthActivity.this, "výborně, můžeš zkusit další, nebo jít dál", Toast.LENGTH_LONG).show();
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                databaseConnector.recordUserPoints(userName, "fifth");
                drawingFragment.changeDrawingMethod("clear"); //toto vymaže, co uživatel nakreslil, aby nebouchal jenom check, check...
            }else {
                Toast.makeText(FifthActivity.this, "bohužel, to není správně", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_fifth); //tady treba hodit, co se ma zvyraznit

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
        return new FifthActivityFragment();
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(getString(R.string.fifth_activity_text));
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
        Toast.makeText(this, "Právě se zobrazuje bipartitní graf", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        Toast.makeText(this, "Nakresli bipartitní graf", Toast.LENGTH_LONG).show();
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
//        this.height = height;
//        this.width = width;

        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        ArrayList<Coordinate> nodes = GraphGenerator.generateNodes(height, width, 15, amountOfNodes );
        Map mapToSet = new Map(new ArrayList<>(), nodes);
        drawingFragment.setUserGraph(mapToSet);
    }

}
