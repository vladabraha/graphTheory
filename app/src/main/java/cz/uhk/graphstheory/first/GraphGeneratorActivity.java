package cz.uhk.graphstheory.first;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cz.uhk.graphstheory.DrawingFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.TabLayoutFragment;
import cz.uhk.graphstheory.second.SecondActivity;
import cz.uhk.graphstheory.util.GraphChecker;

public class GraphGeneratorActivity extends AppCompatActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, NavigationView.OnNavigationItemSelectedListener {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DrawingFragmentListener drawingFragmentListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_generator);

        floatingActionButton = findViewById(R.id.floating_action_button_generate_graph);

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        drawingFragment = new DrawingFragment();
        textFragment = new TextFragment();
        generateGraphFragment = new GenerateGraphFragment();
        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

        floatingActionButton.setOnClickListener(v -> {
            //todo tady osetrit co dal
            boolean isValid = GraphChecker.checkIfGraphContainsCesta(drawingFragment.getUserGraph());
            Toast.makeText(GraphGeneratorActivity.this, String.valueOf(isValid), Toast.LENGTH_LONG).show();
 //                DatabaseConnector databaseConnector = new DatabaseConnector();
//                databaseConnector.writeFirstActivityValue("test");
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.circle:
                drawingFragment.changeDrawingMethod("circle");
                return true;
            case R.id.line:
                drawingFragment.changeDrawingMethod("line");
                return true;
            case R.id.delete:
                drawingFragment.changeDrawingMethod("remove");
                return true;
            case R.id.clear:
                drawingFragment.changeDrawingMethod("clear");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void tableLayoutSelectedChange(int number) {
        switch (number) {
            case 0:
                changeToTextFragment();
                break;
            case 1:
                changeToEducationFragment();
                break;
            case 2:
                changeToDrawingFragment();
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

    private void changeToDrawingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, drawingFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    floatingActionButton.show();
                }
            }, 5000);

        }
        else if (fragmentManager.getFragments().contains(generateGraphFragment)){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(generateGraphFragment);
            fragmentTransaction.add(R.id.generator_activity_group, drawingFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    floatingActionButton.show();
                }
            }, 5000);

        }
    }
    private void changeToTextFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        //zkontroluje, že už tam neni drawing fragment a kdyžtak tam hodi text fragment
        if (fragmentManager.getFragments().contains(generateGraphFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(generateGraphFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }
        else if (fragmentManager.getFragments().contains(drawingFragment)){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }
    }

    private void changeToEducationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }
        else if (fragmentManager.getFragments().contains(drawingFragment)){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (id == R.id.paths) {
            // Handle the camera action
            Intent notificationIntent = new Intent(this, GraphGeneratorActivity.class);
            startActivity(notificationIntent);
            navigationView.setCheckedItem(R.id.paths);

        } else if (id == R.id.dalsi) {
            Intent notificationIntent = new Intent(this, SecondActivity.class);
            startActivity(notificationIntent);
            navigationView.setCheckedItem(R.id.dalsi);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
