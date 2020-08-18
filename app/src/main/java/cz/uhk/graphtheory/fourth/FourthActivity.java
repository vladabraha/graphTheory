package cz.uhk.graphtheory.fourth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.abstraction.AbstractActivity;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;
import cz.uhk.graphtheory.util.GraphConverter;
import cz.uhk.graphtheory.util.GraphGenerator;

public class FourthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface, DrawingFragment.CommunicationInterface, SecondaryTabLayoutFragment.SecondaryTableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    private FourthActivityFragment fourthActivityFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    int height;
    int width;
    boolean isGraphSame = false;
    Graph firstGraph, secondGraph;

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
        floatingActionButton = getFloatingActionButton();
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
                        showMessage("Ano, máš pravdu!");
                        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                        assert userName != null;
                        databaseConnector.recordUserPoints(userName, "fourth");
                        showGraphRandomlyIsomorfic(height);
                    } else {
                        showMessage("Bohužel, právě naopak");
                        showGraphRandomlyIsomorfic(height);
                    }
                }
            })
                    .setNegativeButton("Ne ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                            Log.d("action", "ne");
                            if (!isGraphSame) {
                                showMessage("Ano, máš pravdu!");
                                String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                                assert userName != null;
                                databaseConnector.recordUserPoints(userName, "fourth");
                                showGraphRandomlyIsomorfic(height);
                            } else {
                                showMessage("Bohužel, právě naopak");
                                showGraphRandomlyIsomorfic(height);
                            }
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();
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

        textFragment.setEducationText(R.string.fourth_activity_text);
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
        showSnackBar( "Ukázka izomorfních grafů");
    }

    @Override
    protected void changeToDrawingFragment() {
        super.changeToDrawingFragment();
        showSnackBar( "Rozhodni, zdali se jedná o izomorfní grafy. Až si budeš jistý, klikni na fajfku");

        //hack - wait 0.5 sec if drawing fragment is already set and if not wait another 0.5
        waitForDrawingFragment("circle_move");
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

    private Graph createGraph() {
        //budeme chtit vygenerovat mapu, ktera ma alespon 3 uzly
        Graph generatedGraph;
        do {
            int amountOfEdges = (int) (Math.random() * FourthActivityFragment.MAXIMUM_AMOUNT_OF_NODES);
            if (amountOfEdges < FourthActivityFragment.MINIMUM_AMOUNT_OF_NODES) {
                amountOfEdges = FourthActivityFragment.MINIMUM_AMOUNT_OF_NODES;
            }

            generatedGraph = GraphGenerator.generateGraph(height, width, fourthActivityFragment.BRUSH_SIZE, amountOfEdges);
        } while (generatedGraph.getNodes().size() < 3);
        return generatedGraph;
    }

    private Graph createSameGraph(Graph firstGraph) {
        Graph secondGraph = new Graph(firstGraph);
        int randomNumber = (int) Math.round(Math.random() * secondGraph.getNodes().size());

        for (int i = 0; i < randomNumber; i++) {

            //vytvořím si náhodnou souřadnici
            float newXCoordinate = (float) (Math.random() * width);
            float newYCoordinate = (float) (Math.random() * height);

            //vezmu náhodný uzel a tomu změním souřadnice + všem elementům se stejnou souřadnicí

            int randomIndex = (int) Math.round(Math.random() * (secondGraph.getNodes().size() - 1));
            Coordinate oldCoordinate = secondGraph.getNodes().get(randomIndex);
            Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);

            secondGraph.getNodes().set(randomIndex, newCoordinate);
            ArrayList<Edge> edges = secondGraph.getEdges();
            for (int j = 0; j < edges.size(); j++) {
                Edge edge = edges.get(j);
                if (edge.getTo().equal(oldCoordinate)) {
                    Edge newEdge = new Edge(edge.getFrom(), newCoordinate);
                    edges.set(j, newEdge);
                } else if (edge.getFrom().equal(oldCoordinate)) {
                    Edge newEdge = new Edge(newCoordinate, edge.getTo());
                    edges.set(j, newEdge);
                }
            }
        }
        return secondGraph;
    }

    private Graph createDifferentGraph(Graph firstGraph) {

        //jina mapa by mela mit take alespon 3 uzly, aby bylo co poznat
        Graph secondGraph;
        do {
            secondGraph = new Graph(firstGraph);
            ArrayList<Edge> edges = secondGraph.getEdges();
            ArrayList<Coordinate> nodes = secondGraph.getNodes();

            //myšlenka, projdu nekolikrat graf, smazu z neho bod a vsechny cary, ktere jsou k nemu propojene
            // nahradim ho novym uzlem a nahodnym poctem novych car (nemusim ani hledat jestli uz nejsou s nim propojeny, protoze jsou novy)
            int randomNumber = (int) Math.round(Math.random() * secondGraph.getEdges().size());
            for (int i = 0; i < randomNumber; i++) {

                //vezmu nahodny bod a ten smazu
                int randomIndex = (int) Math.round(Math.random() * (secondGraph.getNodes().size() - 1));
                Coordinate oldCoordinate = secondGraph.getNodes().get(randomIndex);
                secondGraph.getNodes().remove(randomIndex);

                //projdu vsechny primky a mrknu jestli neprochazely tim bodem
                Iterator<Edge> iterator = edges.iterator();
                while (iterator.hasNext()) {
                    Edge edge = iterator.next();
                    if (edge.getTo().equal(oldCoordinate)) {
                        iterator.remove();
                    } else if (edge.getFrom().equal(oldCoordinate)) {
                        iterator.remove();
                    }
                }

                //vytvořím si náhodnou souřadnici a tu propojim s nahodnymi uzly
                float newXCoordinate = (float) (Math.random() * width);
                float newYCoordinate = (float) (Math.random() * height);

                Coordinate newCoordinate = new Coordinate(newXCoordinate, newYCoordinate);
                nodes.add(newCoordinate);

                int randomNumber2 = (int) Math.round(Math.random() * (secondGraph.getNodes().size() - 1));
                for (int k = 0; k < randomNumber2; k++) {
                    //nalezeni nahodneho bodu se kterym novy bod propojime
                    boolean found = false;
                    int randomIndex2;
                    do {
                        randomIndex2 = (int) Math.round(Math.random() * (secondGraph.getNodes().size() - 1));
                        if (randomIndex2 != secondGraph.getNodes().size() - 1) found = true;
                    } while (!found);
                    Edge newEdge = new Edge(nodes.get(randomIndex2), newCoordinate);
                    edges.add(newEdge);
                }
            }
        } while (secondGraph.getNodes().size() < 3);
        return secondGraph;
    }

    @Override
    public void sentMetrics(int width, int height) {
        this.height = height;
        this.width = width;
        showGraphRandomlyIsomorfic(height);
        drawingFragment.changeDrawingMethod("circle_move");
    }

    private void showGraphRandomlyIsomorfic(int height) {
        firstGraph = createGraph();
        if (Math.random() > 0.5) {
            secondGraph = createSameGraph(firstGraph);
            isGraphSame = true;
        } else {
            secondGraph = createDifferentGraph(firstGraph);
            isGraphSame = false;
        }

        //rozdelim si mapu na 2 poloviny (metoda bohužel vrací ten samej graf rozdelenej na polovinu, takze si to musi zavolat 2x a pak si to z toho vytahnu
        //nez to poslu do view, tak to musim ještě slepit do jednoho grafu
        ArrayList<Graph> firstGraphTwice = GraphConverter.convertGraphsToSplitScreenArray(firstGraph, height);
        firstGraph = firstGraphTwice.get(0);

        ArrayList<Graph> secondGraphTwice = GraphConverter.convertGraphsToSplitScreenArray(secondGraph, height);
        secondGraph = secondGraphTwice.get(1);

        firstGraph.getEdges().addAll(secondGraph.getEdges());
        firstGraph.getNodes().addAll(secondGraph.getNodes());
        firstGraph.getRedEdgesList().addAll(secondGraph.getRedEdgesList());
        drawingFragment.setUserGraph(firstGraph);
        drawingFragment.changeDrawingMethod("circle_move"); //this will enable moving nodes
    }

    private void showMessage(String text) {
        showSnackBar(text);
    }

    @Override
    public void secondaryTableLayoutSelectedChange(int number) {

    }
}
