package cz.uhk.graphstheory.second;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import cz.uhk.graphstheory.DrawingFragment;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.TabLayoutFragment;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.first.GenerateGraphFragment;
import cz.uhk.graphstheory.first.TextFragment;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.util.GraphChecker;

public class SecondActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        //get instance of abstraction object
        textFragment = getTextFragment();
        drawingFragment = getDrawingFragment();
        generateGraphFragment = getGenerateGraphFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo tady osetrit co dal
                boolean isValid = GraphChecker.checkIfGraphContainsCesta(drawingFragment.getUserGraph());
                Toast.makeText(SecondActivity.this, String.valueOf(isValid), Toast.LENGTH_LONG).show();
//                DatabaseConnector databaseConnector = new DatabaseConnector();
//                databaseConnector.writeFirstActivityValue("test");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.dalsi); //tady treba hodit, co se ma zvyraznit

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
    protected GenerateGraphFragment getGraphFragment() {
        return new GenerateGraphFragment();
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


}
