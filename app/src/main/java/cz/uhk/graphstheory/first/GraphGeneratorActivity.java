package cz.uhk.graphstheory.first;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractAppCompactActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.model.User;
import cz.uhk.graphstheory.util.GraphChecker;
import cz.uhk.graphstheory.util.GraphGenerator;

public class GraphGeneratorActivity extends AbstractAppCompactActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, NavigationView.OnNavigationItemSelectedListener, DrawingFragment.CommunicationInterface, SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;
    private FloatingActionButton floatingActionButton;
    private TabLayoutFragment tabLayoutFragment;
    private SecondaryTabLayoutFragment secondaryLayoutFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DrawingFragmentListener drawingFragmentListener;

    private DatabaseConnector databaseConnector;
    TextView navigationDrawerName, navigationDrawerEmail;
    int height, width, length;
    boolean isViewCreated = false;


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
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Cesta");
        tabNames.add("Tah");
        tabNames.add("Kužnice");
        tabNames.add("Sled");
        secondaryLayoutFragment = new SecondaryTabLayoutFragment(tabNames);
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

        textFragment.setEducationText(R.string.first_activity_text);

        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity", 0);
            boolean isValid = false;
            switch (displayedActivity) {
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsCesta(drawingFragment.getUserGraph());
                    break;
                case 1:
                    isValid = GraphChecker.checkIfGraphContainsTah(drawingFragment.getUserGraph());
                    break;
                case 2:
                    isValid = GraphChecker.checkIfGraphContainsCycle(drawingFragment.getUserGraph());
                    break;
                case 3:
                    isValid = GraphChecker.checkIfGraphContainsSled(drawingFragment.getUserGraph(), length);
                    break;
            }
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints;
                switch (displayedActivity) {
                    case 0:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-first");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-second");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-third");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-fourth");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + "bodů", Toast.LENGTH_LONG).show();
                        break;
                }
                drawingFragment.changeDrawingMethod("clear");
                bottomNavigationView.setSelectedItemId(R.id.circle);
                drawingFragment.changeDrawingMethod("circle");
                createDialog();
            } else {
                Toast.makeText(GraphGeneratorActivity.this, "Bohužel, něco je špatně, oprav graf a zkus to znovu", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_first); //tady treba hodit, co se ma zvyraznit

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

        //set current user to navigation drawer
        navigationDrawerName = findViewById(R.id.navigation_header_name);
        navigationDrawerEmail = findViewById(R.id.navigation_header_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigationDrawerEmail.setText(currentUser.getEmail());
            User user = databaseConnector.findUser(Objects.requireNonNull(currentUser.getEmail()));
            if (user != null) {
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
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);
        switch (number) {
            case 0:
                changeToTextFragment();
                break;
            case 1:
                changeToEducationFragment();
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu cestu", Toast.LENGTH_LONG).show();
                break;
            case 2:
                changeToDrawingFragment();
                showProperToastMessage(displayedActivity);
                break;
        }
    }

    private void showProperToastMessage(int displayedActivity) {
        switch (displayedActivity) {
            case 0:
                Toast.makeText(this, "Nakresli cestu v grafu, případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this, "Nakresli tah v grafu, případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "Nakresli kružnici v grafu, případně si graf uprav", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Random ran = new Random();
                length = ran.nextInt(7);
                Toast.makeText(this, "Nakresli sled délky " + length + " , případně si graf uprav", Toast.LENGTH_LONG).show();
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
            fragmentTransaction.remove(secondaryLayoutFragment);
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
            fragmentTransaction.remove(secondaryLayoutFragment);
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
            fragmentTransaction.remove(secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.remove(secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }
        textFragment.setEducationText(R.string.first_activity_text);
    }

    private void changeToEducationFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //kontrola, ze se to nezavola 2x a nehodi to chybu
        if (fragmentManager.getFragments().contains(textFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(textFragment);
            fragmentTransaction.add(R.id.generator_activity_group, secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, generateGraphFragment);
            fragmentTransaction.commit();
            bottomNavigationView.setVisibility(View.GONE);
            floatingActionButton.hide();
        }
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);
        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        switch (displayedActivity) {
            case 0:
                menu.getItem(2).setTitle("cesta");
                break;
            case 1:
                menu.getItem(2).setTitle("tah");
                break;
            case 2:
                menu.getItem(2).setTitle("kružnice");
                break;
        }
        if (isViewCreated) setMapToDrawingFragment(width, height);
    }


    private void changeActivity() {
        //do shared preferences si ukladame posledni otevrenou aktivitu, abychom se mohli tocit do kolecka a neotevirali pripadne porad stejnou aktivitu
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);

        if (displayedActivity < 3) {
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

        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
        showProperToastMessage(displayedActivity);

    }

    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("");
        dialog.setMessage("Máš to správně! Chceš si to zkusit ještě jednou, nebo jít na další?");
        dialog.setPositiveButton("Další", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                changeActivity();
            }
        })
                .setNegativeButton("Znovu ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
                        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
                        drawingFragment.setUserGraph(map);
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;
        setMapToDrawingFragment(width, height);
        isViewCreated = true;
    }

    private void setMapToDrawingFragment(int width, int height) {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
        Menu menu = bottomNavigationView.getMenu();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);

        switch (displayedActivity) {
            case 0:
                menu.getItem(2).setTitle("cesta");
                break;
            case 1:
                menu.getItem(2).setTitle("tah");
                break;
            case 2:
                menu.getItem(2).setTitle("kužnice");
                break;
            case 3:
                menu.getItem(2).setTitle("sled");
                break;
        }
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {
        int length;
        switch (number) {
            case 0:
                generateGraphFragment.changeEducationGraph("cesta");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu cestu", Toast.LENGTH_LONG).show();
                break;
            case 1:
                generateGraphFragment.changeEducationGraph("tah");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu tah", Toast.LENGTH_LONG).show();
                break;
            case 2:
                length = generateGraphFragment.changeEducationGraph("kruznice");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu kružnici delky " + length, Toast.LENGTH_LONG).show();
                break;
            case 3:
                length = generateGraphFragment.changeEducationGraph("kruznice");
                Toast.makeText(this, "Nyní si ukážeme v zadaném grafu sled délky " + length, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
