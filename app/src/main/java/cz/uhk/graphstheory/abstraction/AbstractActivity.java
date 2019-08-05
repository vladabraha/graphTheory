package cz.uhk.graphstheory.abstraction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import cz.uhk.graphstheory.DrawingFragment;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.TabLayoutFragment;
import cz.uhk.graphstheory.first.GenerateGraphFragment;
import cz.uhk.graphstheory.first.GraphGeneratorActivity;
import cz.uhk.graphstheory.first.TextFragment;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.second.SecondActivity;
import cz.uhk.graphstheory.util.GraphChecker;

public abstract class AbstractActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;
    private FloatingActionButton floatingActionButton;
    private NavigationView navigationView;

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
        generateGraphFragment = getGraphFragment();

        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_options, menu);
        return super.onCreateOptionsMenu(menu);
    }


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


    protected void changeToDrawingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, drawingFragment);
            fragmentTransaction.commit();
            showBottomNavigationView();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    floatingActionButton.show();
                }
            }, 5000);

        } else if (fragmentManager.getFragments().contains(generateGraphFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(generateGraphFragment);
            fragmentTransaction.add(R.id.generator_activity_group, drawingFragment);
            fragmentTransaction.commit();
            showBottomNavigationView();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    floatingActionButton.show();
                }
            }, 5000);

        }
    }


    protected void changeToTextFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        //zkontroluje, že už tam neni drawing fragment a kdyžtak tam hodi text fragment
        if (fragmentManager.getFragments().contains(generateGraphFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(generateGraphFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        }
    }


    protected void changeToEducationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        }

    }


    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.paths) {
            // Handle the camera action
            Intent notificationIntent = new Intent(this, GraphGeneratorActivity.class);
            startActivity(notificationIntent);

        } else if (id == R.id.dalsi) {
            Intent notificationIntent = new Intent(this, SecondActivity.class);
            startActivity(notificationIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public DrawingFragment getDrawingFragment() {
        return drawingFragment;
    }

    public TextFragment getTextFragment() {
        return textFragment;
    }

    public GenerateGraphFragment getGenerateGraphFragment() {
        return generateGraphFragment;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    protected abstract void hideBottomNavigationView();

    protected abstract void showBottomNavigationView();

    /**
     * abstract method needed for inflating fragment which provides generated graph
     * generated graph is the one which is provided as example for user
     *
     * @return Fragment
     */
    protected abstract GenerateGraphFragment getGraphFragment();
}

