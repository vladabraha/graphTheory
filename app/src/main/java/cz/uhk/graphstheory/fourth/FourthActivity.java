package cz.uhk.graphstheory.fourth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphConverter;
import cz.uhk.graphstheory.util.GraphGenerator;

public class FourthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
    private FloatingActionButton floatingActionButton;

    private FourthActivityFragment fourthActivityFragment;

    private DrawingFragmentListener drawingFragmentListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int height;
    int width;
    boolean isGraphSame, firstGraph;
    Map firstMap, secondMap;

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

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("Rozhodněte");
            dialog.setMessage("Jedná se v tomto případě o izomorfismus?");
            dialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Delete".
                    Log.d("action", "ano");
                    if (isGraphSame) {
                        showMessage("ano, máš pravdu!");
                        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                        assert userName != null;
                        databaseConnector.recordUserPoints(userName, "fourth");
                    } else {
                        showMessage("bohužel, právě naopak");
                    }
                }
            })
                    .setNegativeButton("Ne ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                            Log.d("action", "ne");
                            if (!isGraphSame) {
                                showMessage("ano, máš pravdu!");
                                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                assert userName != null;
                                databaseConnector.recordUserPoints(userName, "fourth");
                            } else {
                                showMessage("bohužel, právě naopak");
                            }
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();
//                DatabaseConnector databaseConnector = new DatabaseConnector();
//                databaseConnector.writeFirstActivityValue("test");
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_fourth); //tady treba hodit, co se ma zvyraznit

        bottomNavigationView = findViewById(R.id.graph_generator_navigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);

        textFragment.setEducationText(R.string.fifth_activity_text);
    }


    @Override
    protected Fragment getGraphFragment() {
        fourthActivityFragment = new FourthActivityFragment();
        return fourthActivityFragment;
    }

    @Override
    protected ArrayList<String> getTabNames() {
        ArrayList<String> tabNames = new ArrayList<>();
        tabNames.add("Izomorfismus");
        return tabNames;
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        textFragment.setEducationText(R.string.fifth_activity_text);
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        Toast.makeText(this, "Ukázka izomorfních grafů", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        Toast.makeText(this, "Rozhodni, zdali se jedna o izomorfni grafy", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void showBottomNavigationView() {
        bottomNavigationView.setVisibility(View.INVISIBLE);
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

    private Map createMap() {
        //budeme chtit vygenerovat mapu, ktera ma alespon 3 uzly
        Map generatedMap;
        do {
            int amountOfEdges = (int) (Math.random() * FourthActivityFragment.MAXIMUM_AMOUNT_OF_NODES);
            if (amountOfEdges < FourthActivityFragment.MINIMUM_AMOUNT_OF_NODES) {
                amountOfEdges = FourthActivityFragment.MINIMUM_AMOUNT_OF_NODES;
            }

            generatedMap = GraphGenerator.generateMap(height, width, fourthActivityFragment.BRUSH_SIZE, amountOfEdges);
        } while (generatedMap.getCircles().size() < 3);
        return generatedMap;
    }

    private Map createSameGraph(Map firstMap) {
        Map secondMap = new Map(firstMap);
        int randomNumber = (int) Math.round(Math.random() * secondMap.getCircles().size());

        for (int i = 0; i < randomNumber; i++) {

            //vytvořím si náhodnou souřadnici
            float newXCoordinate = (float) (Math.random() * width);
            float newYCoordinate = (float) (Math.random() * height);

            //vezmu náhodný uzel a tomu změním souřadnice + všem elementům se stejnou souřadnicí

            int randomIndex = (int) Math.round(Math.random() * (secondMap.getCircles().size() - 1));
            Coordinate oldCoordinate = secondMap.getCircles().get(randomIndex);
            Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);

            secondMap.getCircles().set(randomIndex, newCoordinate);
            ArrayList<CustomLine> customLines = secondMap.getCustomLines();
            for (int j = 0; j < customLines.size(); j++) {
                CustomLine customLine = customLines.get(j);
                if (customLine.getTo().equal(oldCoordinate)) {
                    CustomLine newCustomLine = new CustomLine(customLine.getFrom(), newCoordinate);
                    customLines.set(j, newCustomLine);
                } else if (customLine.getFrom().equal(oldCoordinate)) {
                    CustomLine newCustomLine = new CustomLine(newCoordinate, customLine.getTo());
                    customLines.set(j, newCustomLine);
                }
            }
        }
        return secondMap;
    }

    private Map createDifferentGraph(Map firstMap) {

        //jina mapa by mela mit take alespon 3 uzly, aby bylo co poznat
        Map secondMap;
        do {
            secondMap = new Map(firstMap);
            ArrayList<CustomLine> customLines = secondMap.getCustomLines();
            ArrayList<Coordinate> circles = secondMap.getCircles();

            //myšlenka, projdu nekolikrat graf, smazu z neho bod a vsechny cary, ktere jsou k nemu propojene
            // nahradim ho novym uzlem a nahodnym poctem novych car (nemusim ani hledat jestli uz nejsou s nim propojeny, protoze jsou novy)
            int randomNumber = (int) Math.round(Math.random() * secondMap.getCustomLines().size());
            for (int i = 0; i < randomNumber; i++) {

                //vezmu nahodny bod a ten smazu
                int randomIndex = (int) Math.round(Math.random() * (secondMap.getCircles().size() - 1));
                Coordinate oldCoordinate = secondMap.getCircles().get(randomIndex);
                secondMap.getCircles().remove(randomIndex);

                //projdu vsechny primky a mrknu jestli neprochazely tim bodem
                Iterator<CustomLine> iterator = customLines.iterator();
                while (iterator.hasNext()) {
                    CustomLine customLine = iterator.next();
                    if (customLine.getTo().equal(oldCoordinate)) {
                        iterator.remove();
                    } else if (customLine.getFrom().equal(oldCoordinate)) {
                        iterator.remove();
                    }
                }

                //vytvořím si náhodnou souřadnici a tu propojim s nahodnymi uzly
                float newXCoordinate = (float) (Math.random() * width);
                float newYCoordinate = (float) (Math.random() * height);

                Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);
                circles.add(newCoordinate);

                int randomNumber2 = (int) Math.round(Math.random() * (secondMap.getCircles().size() - 1));
                for (int k = 0; k < randomNumber2; k++) {
                    //nalezeni nahodneho bodu se kterym novy bod propojime
                    boolean found = false;
                    int randomIndex2;
                    do {
                        randomIndex2 = (int) Math.round(Math.random() * (secondMap.getCircles().size() - 1));
                        if (randomIndex2 != secondMap.getCircles().size() - 1) found = true;
                    } while (!found);
                    CustomLine newCustomLine = new CustomLine(circles.get(randomIndex2), newCoordinate);
                    customLines.add(newCustomLine);
                }
            }
        } while (secondMap.getCircles().size() < 3);
        return secondMap;
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.height = height;
        this.width = width;

        firstMap = createMap();
        if (Math.random() > 0.5) {
            secondMap = createSameGraph(firstMap);
            isGraphSame = true;
        } else {
            secondMap = createDifferentGraph(firstMap);
            isGraphSame = false;
        }

        //rozdelim si mapu na 2 poloviny (metoda bohužel vrací ten samej graf rozdelenej na polovinu, takze si to musi zavolat 2x a pak si to z toho vytahnu
        //nez to poslu do view, tak to musim ještě slepit do jednoho grafu
        ArrayList<Map> firstMapTwice = GraphConverter.convertMapsToSplitScreenArray(firstMap, height);
        firstMap = firstMapTwice.get(0);

        ArrayList<Map> secondMapTwice = GraphConverter.convertMapsToSplitScreenArray(secondMap, height);
        secondMap = secondMapTwice.get(1);


        firstMap.getCustomLines().addAll(secondMap.getCustomLines());
        firstMap.getCircles().addAll(secondMap.getCircles());
        firstMap.getRedLineList().addAll(secondMap.getRedLineList());
        drawingFragment.setUserGraph(firstMap);
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }
}
