package cz.uhk.graphtheory.fifth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import cz.uhk.graphtheory.util.GraphChecker;
import cz.uhk.graphtheory.util.GraphGenerator;
import cz.uhk.graphtheory.util.SpecificGraphGenerator;

public class FifthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private DrawingFragmentListener drawingFragmentListener;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int height, width;
    boolean isGraphBipartite, isDrawingViewCreated = false;


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

        textFragment.setEducationText(R.string.fifth_activity_text);

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {

            switch (getCurrentActivity()) {
                //rozhodování o tom jestli je graf bipartitní
                case 0:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Rozhodněte");
                    dialog.setMessage("Jedná se v tomto případě o bipartitní graf?");
                    dialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Delete".
                            if (isGraphBipartite) {
                                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                assert userName != null;
                                showMessage("Získáno " + databaseConnector.recordUserPoints(userName, "fifth-first") + " bodů");
                                createDialog();
                            } else {
                                showMessage("Bohužel, právě naopak");
                                setContentAccordingCurrentActivity();
                            }
                        }
                    })
                            .setNegativeButton("Ne ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Action for "Cancel".
                                    if (!isGraphBipartite) {
                                        showMessage("Ano, máš pravdu!");
                                        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                        assert userName != null;
                                        showMessage("Získáno " + databaseConnector.recordUserPoints(userName, "fifth-first") + " bodů");
                                        createDialog();
                                    } else {
                                        showMessage("Bohužel, právě naopak");
                                        setContentAccordingCurrentActivity();
                                    }
                                }
                            });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                    break;

                //nakresli bipartitní graf
                case 1:
                    if (drawingFragment.getUserGraph().getEdges().size() < 2 || drawingFragment.getUserGraph().getNodes().size() < 2){
                        Toast.makeText(FifthActivity.this, "Nakresli alespoň 2 uzly a 2 hrany", Toast.LENGTH_LONG).show();
                        break;
                    }
                    boolean isValid = GraphChecker.checkIfGraphIsBipartite(drawingFragment.getUserGraph());
                    if (isValid) {
                        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                        assert userName != null;
                        showMessage("Získáno " + databaseConnector.recordUserPoints(userName, "fifth-second") + " bodů");
                        createDialog();
                    } else {
                        Toast.makeText(FifthActivity.this, "bohužel, to není správně", Toast.LENGTH_LONG).show();
                    }
                    break;
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
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.circle:
                    drawingFragment.changeDrawingMethod("circle");
                    return true;
                case R.id.circle_move:

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

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Fragment getGraphFragment() {
        return new FifthActivityFragment();
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Bipartitní graf");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.fifth_activity_text);
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
        showSnackBar( "Poskládej si graf, abys viděl bipartitní graf");
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        showTextAccordingCurrentActivity();

        //hack - wait 0.5 sec if drawing fragment is already set and if not wait another 0.5
        waitForDrawingFragment();
    }

    /**
     * this method is kinda hack - due to unsychronous fragment transactions this will check every 0.5sec if transaction is done and then it will set proper parameter
     */
    private void waitForDrawingFragment() {
        new Handler().postDelayed(() -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getFragments().contains(drawingFragment)) {
                if (isDrawingViewCreated){
                    switch (getCurrentActivity()) {
                        case 0:
                            drawingFragment.changeDrawingMethod("circle_move");
                            break;
                        case 1:
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }else{
                waitForDrawingFragment();
            }
        }, 500);
    }

    private void showTextAccordingCurrentActivity() {
        switch (getCurrentActivity()){
            case 0:
                showSnackBar("Rozhodni, zda se se jedná o bipartitní graf");
                break;
            case 1 :
                showSnackBar("Nakresli bipartitní graf");
                break;
        }
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
        this.width = width;
        this.height = height;
        setContentAccordingCurrentActivity();
        isDrawingViewCreated = true;
        switch (getCurrentActivity()) {
            case 0:
                bottomNavigationView.setVisibility(View.INVISIBLE);
                drawingFragment.changeDrawingMethod("circle_move");
                break;
            case 1:
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }

    private void setContentAccordingCurrentActivity(){
        showTextAccordingCurrentActivity();
        switch (getCurrentActivity()){
            case 0:
                Random random = new Random();
                isGraphBipartite = random.nextBoolean();
                if (isGraphBipartite){
                    Map mapToSet = SpecificGraphGenerator.generateBipartiteGraph(height, width, 15);
                    drawingFragment.setUserGraph(mapToSet);
                }else {
                    int amountOfNodes = (int) Math.round(Math.random() * 2) + 4;
                    Map mapToSet = GraphGenerator.generateMap(height, width, 15, amountOfNodes);
                    drawingFragment.setUserGraph(mapToSet);
                }
                bottomNavigationView.setVisibility(View.INVISIBLE);
                drawingFragment.changeDrawingMethod("circle_move");
                break;
            case 1:
                //vymaže graf z view a nechá ho prázdný
                drawingFragment.changeDrawingMethod("clear");
                drawingFragment.changeDrawingMethod("circle");
                bottomNavigationView.setSelectedItemId(R.id.circle);
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changeActivity() {
        //do shared preferences si ukladame posledni otevrenou aktivitu, abychom se mohli tocit do kolecka a neotevirali pripadne porad stejnou aktivitu
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int displayedActivity = sharedPref.getInt("displayedActivity-fifth", 0);

        if (displayedActivity < 1) {
            displayedActivity++;
        } else {
            displayedActivity = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("displayedActivity-fifth", displayedActivity);
        editor.apply();

       setContentAccordingCurrentActivity();
    }

    /**
     * 0 - rozpoznej jestli je to bipartitni graf
     * 1 - nakresli bipartitni graf
     * @return 0 nebo 1
     */
    private int getCurrentActivity() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("displayedActivity-fifth", 0);
    }

    @Override
    public void onPositiveButtonClick() {
        changeActivity();
    }

    @Override
    public void onNegativeButtonClick() {
        setContentAccordingCurrentActivity();
        drawingFragment.changeDrawingMethod("circle_move");
    }
}
