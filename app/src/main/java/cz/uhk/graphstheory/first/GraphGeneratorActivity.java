package cz.uhk.graphstheory.first;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.model.User;
import cz.uhk.graphstheory.second.SecondActivity;
import cz.uhk.graphstheory.statistics.StatisticsActivity;
import cz.uhk.graphstheory.third.ThirdActivity;
import cz.uhk.graphstheory.util.GraphChecker;

public class GraphGeneratorActivity extends AppCompatActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, NavigationView.OnNavigationItemSelectedListener {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;
    private FloatingActionButton floatingActionButton;
    private TabLayoutFragment tabLayoutFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DrawingFragmentListener drawingFragmentListener;

    private DatabaseConnector databaseConnector;
    TextView navigationDrawerName;
    TextView navigationDrawerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_generator);

        floatingActionButton = findViewById(R.id.floating_action_button_generate_graph);
        databaseConnector = new DatabaseConnector();

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        drawingFragment = new DrawingFragment();
        textFragment = new TextFragment();
        generateGraphFragment = new GenerateGraphFragment();
        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity", 0);
            boolean isValid = false;
            switch (displayedActivity){
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsCesta(drawingFragment.getUserGraph());
                    Toast.makeText(GraphGeneratorActivity.this, "cesta je " + (isValid), Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    isValid = GraphChecker.checkIfGraphContainsTah(drawingFragment.getUserGraph());
                    Toast.makeText(GraphGeneratorActivity.this, "tah je" + isValid, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    isValid = GraphChecker.checkIfGraphContainsCycle(drawingFragment.getUserGraph());
                    Toast.makeText(GraphGeneratorActivity.this, "kruznice je" + isValid, Toast.LENGTH_LONG).show();
                    break;
            }
            if (isValid) {
                changeActivity();
            }
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
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_options, menu);

        //set current user to navigation drawer
        navigationDrawerName = findViewById(R.id.navigation_header_name);
        navigationDrawerEmail = findViewById(R.id.navigation_header_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            navigationDrawerEmail.setText(currentUser.getEmail());
            User user = databaseConnector.findUser(Objects.requireNonNull(currentUser.getEmail()));
            if (user != null){
                navigationDrawerName.setText(user.getNickName());
            }
        }

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
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                int displayedActivity = sharedPref.getInt("displayedActivity", 0);
                Log.d("displayedActivity", String.valueOf(displayedActivity));

                switch (displayedActivity){
                    case 0:
                        Toast.makeText(this, "Nakresli cestu", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(this, "Nakresli tah", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(this, "Nakresli kružnici", Toast.LENGTH_LONG).show();
                        break;
                }
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

        } else if (fragmentManager.getFragments().contains(generateGraphFragment)) {
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
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
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

        //kontrola, ze se to nezavola 2x a nehodi to chybu
        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
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

        //zabrani znovu spusteni pustene aktivity
        int sessionId = getIntent().getIntExtra("SESSION_ID", 0);

        if (id == R.id.paths) {
            if (sessionId != 1) {
                Intent newActivityIntent = new Intent(this, GraphGeneratorActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 1);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.dalsi) {
            if (sessionId != 2) {
                Intent newActivityIntent = new Intent(this, SecondActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 2);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_third) {
            if (sessionId != 3) {
                Intent newActivityIntent = new Intent(this, ThirdActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 3);
                finish();
                startActivity(newActivityIntent);
            }
        }
        else if (id == R.id.nav_statistic) {
            if (sessionId != 99) {
                Intent newActivityIntent = new Intent(this, StatisticsActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 99);
                finish();
                startActivity(newActivityIntent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeActivity() {

        //do shared preferences si ukladame posledni otevrenou aktivitu, abychom se mohli tocit do kolecka a neotevirali pripadne porad stejnou aktivitu
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);

        if (displayedActivity < 2) {
            displayedActivity++;
        } else {
            displayedActivity = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("displayedActivity", displayedActivity);
        editor.apply();

        //tady tocime jednotlivy aktivity za zaznamenavame skore (tahle metoda se vola jenom po uspesnym vyplneni)
        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert userName != null;
        switch (displayedActivity) {
            case 0:
                databaseConnector.recordUserPoints(userName, "first-first");
                tabLayoutFragment.switchSelectedTab(1);
                Toast.makeText(this, "Teď si ukážeme cestu v grafu", Toast.LENGTH_LONG).show();
                generateGraphFragment.changeEducationGraph("cesta");

                break;
            case 1:
                databaseConnector.recordUserPoints(userName, "first-second");
                tabLayoutFragment.switchSelectedTab(1);
                Toast.makeText(this, "Teď si ukážeme tah v grafu", Toast.LENGTH_LONG).show();
                generateGraphFragment.changeEducationGraph("tah");
                break;

            case 2:
                databaseConnector.recordUserPoints(userName, "first-third");
                tabLayoutFragment.switchSelectedTab(1);
                Toast.makeText(this, "Teď si ukážeme kružnici v grafu", Toast.LENGTH_LONG).show();
                generateGraphFragment.changeEducationGraph("kruznice");
                break;
        }
    }

    public interface ChangeGraphListener {
        public void changeEducationGraph(String type);
    }

}
