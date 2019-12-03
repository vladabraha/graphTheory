package cz.uhk.graphtheory.first;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
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
import java.util.Random;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractActivity;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.model.Map;
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;

public class GraphGeneratorActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface,
        DrawingFragment.CommunicationInterface, SecondaryTableLayoutCommunicationInterface, GenerateGraphFragment.FirstFragmentCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private GenerateGraphFragment generateGraphFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int height, width, length, sizeOfMap;
    boolean isPathGenerated, isViewCreated = false;
    String textToShow;


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
        bottomNavigationView = getBottomNavigationView();
        FloatingActionButton floatingActionButton = getFloatingActionButton();

        textFragment.setEducationText(R.string.first_activity_text);

        floatingActionButton.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int displayedActivity = sharedPref.getInt("displayedActivity", 0);
            boolean isValid = false;
            switch (displayedActivity) {
                case 0:
                    isValid = GraphChecker.checkIfGraphContainsPath(drawingFragment.getUserGraph());
                    break;
                case 1:
                    isValid = GraphChecker.checkIfGraphContainsTah(drawingFragment.getUserGraph());
                    break;
                case 2:
                    isValid = GraphChecker.checkIfGraphContainsCycle(drawingFragment.getUserGraph());
                    break;
                case 3:
                    isValid = GraphChecker.checkIfGraphContainsWalk(drawingFragment.getUserGraph(), length);
                    break;
            }
            if (isValid) {
                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert userName != null;
                Double receivedPoints;
                switch (displayedActivity) {
                    case 0:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-first");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-second");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-third");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        receivedPoints = databaseConnector.recordUserPoints(userName, "first-fourth");
                        Toast.makeText(GraphGeneratorActivity.this, "Získáno " + receivedPoints + " bodů", Toast.LENGTH_LONG).show();
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
        bottomNavigationView.setSelectedItemId(R.id.path);
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
                case R.id.path:
                    drawingFragment.changeDrawingMethod("path");
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
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Cesta");
        tabNames.add("Tah");
        tabNames.add("Kužnice");
        tabNames.add("Sled");
        return tabNames;
    }

    private void showProperToastMessage() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);
        switch (displayedActivity) {
            case 0:
                showSnackBar("Nakresli cestu v grafu, případně si graf uprav");
                break;
            case 1:
                showSnackBar("Nakresli tah v grafu, případně si graf uprav");
                break;
            case 2:
                showSnackBar("Zakresli do grafu kružnici (graf si případně uprav). Nezapoměň, že kružnice musí končit v bodě, ze kterého vychází.");
                break;
            case 3:
                Random ran = new Random();
                length = ran.nextInt(sizeOfMap);
                if (length < 2) length = 2;
                showSnackBar( "Nakresli sled délky " + length + " , případně si graf uprav");
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

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity", 0);
        if (isViewCreated) showProperToastMessage();

        if (isViewCreated) {
            setProperTitleToBottomNavigationMenu(displayedActivity);
            int size = bottomNavigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                bottomNavigationView.getMenu().getItem(i).setCheckable(false);
            }
        }
        isPathGenerated = false;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        isPathGenerated = false;
    }

    private void setProperTitleToBottomNavigationMenu(int displayedActivity) {
        //zmeni text bottomNavigationView
        Menu menu = bottomNavigationView.getMenu();
        switch (displayedActivity) {
            case 0:
                menu.getItem(3).setTitle("cesta");
                break;
            case 1:
                menu.getItem(3).setTitle("tah");
                break;
            case 2:
                menu.getItem(3).setTitle("kružnice");
                break;
            case 3:
                menu.getItem(3).setTitle("sled");
                break;
        }
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        if (isPathGenerated)
            showSnackBar("Nyní si ukážeme v zadaném grafu cestu přes vrcholy ");
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
        sizeOfMap = map.getCircles().size() - 1;
        showProperToastMessage();
        setProperTitleToBottomNavigationMenu(displayedActivity);
    }

    @Override
    public void onPositiveButtonClick() {
        changeActivity();
    }

    @Override
    public void onNegativeButtonClick() {
        int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
        Map map = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
        drawingFragment.setUserGraph(map);
        sizeOfMap = map.getCircles().size() - 1;
        showProperToastMessage();
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
    protected Fragment getGraphFragment() {
        generateGraphFragment = new GenerateGraphFragment();
        return generateGraphFragment;
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.width = width;
        this.height = height;
        setMapToDrawingFragment(width, height);
        if (!isViewCreated) showProperToastMessage();
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
                menu.getItem(3).setTitle("cesta");
                break;
            case 1:
                menu.getItem(3).setTitle("tah");
                break;
            case 2:
                menu.getItem(3).setTitle("kužnice");
                break;
            case 3:
                menu.getItem(3).setTitle("sled");
                break;
        }
        bottomNavigationView.setSelectedItemId(R.id.path);
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {
        String text;
        switch (number) {
            case 0:
                text = generateGraphFragment.changeEducationGraph("cesta");
                text = convertNameOfLinesToNodes(text);
                showSnackBar("Nyní si ukážeme v zadaném grafu cestu přes vrcholy " + text);
                break;
            case 1:
                text = generateGraphFragment.changeEducationGraph("tah");
                text = convertNameOfLinesToNodes(text);
                showSnackBar("Nyní si ukážeme v zadaném grafu tah přes vrcholy " + text);
                break;
            case 2:
                text = generateGraphFragment.changeEducationGraph("kruznice");
                showSnackBar("Nyní si ukážeme v zadaném grafu kružnici delky " + text);
                break;
            case 3:
                text = generateGraphFragment.changeEducationGraph("kruznice");
                showSnackBar("Nyní si ukážeme v zadaném grafu sled délky " + text);
                break;
        }
    }

    @Override
    public void passArrayOfNodes(String text) {
        textToShow = convertNameOfLinesToNodes(text);

        if (!isPathGenerated)
            showSnackBar("Nyní si ukážeme v zadaném grafu cestu přes vrcholy " + textToShow);
        isPathGenerated = true;
    }

    @NonNull
    private String convertNameOfLinesToNodes(String text) {
        //odstraní zbytečný znaky
        text = text.replaceAll(", ", "");
        text = text.substring(1, text.length() - 1);
        //vymažeme každý druhý písmeno, který tam je zbytečně
        StringBuilder str = new StringBuilder(text);
        for (int i = text.length() - 2; i != 0; i--) {
            if (i > 0 && i % 2 != 0) {
                str.deleteCharAt(i);
            }
        }

        //přidání čárek mezi písmena
        int length = str.length();
        for (int i = length - 1; i != 0; i--) {
            str.insert(i, "-");
        }
        return str.toString();
    }
}
