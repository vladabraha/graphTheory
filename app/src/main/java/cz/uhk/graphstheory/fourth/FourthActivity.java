package cz.uhk.graphstheory.fourth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.abstraction.AbstractActivity;
import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.interfaces.DrawingFragmentListener;
import cz.uhk.graphstheory.util.GraphValidator;

public class FourthActivity extends AbstractActivity implements TabLayoutFragment.TableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationGraphFragment;
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
        educationGraphFragment = getGenerateGraphFragment();
        bottomNavigationView = getBottomNavigationView();
        floatingActionButton = getFloatingActionButton();

        drawingFragmentListener = drawingFragment; //potřeba předat, kdo poslouchá daný listener
        floatingActionButton.setOnClickListener(v -> {
            //todo tady osetrit co dal
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("Rozhodněte");
            dialog.setMessage("Jedná se v tomto případě o izomorfismus" );
            dialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Delete".
                    Log.d("action", "ano");
                }
            })
                    .setNegativeButton("Ne ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                            Log.d("action", "ne");
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
        navigationView.setCheckedItem(R.id.nav_third); //tady treba hodit, co se ma zvyraznit

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
        return new FourthActivityFragment();
    }

    @Override
    protected void changeToTextFragment() {
        super.changeToTextFragment();
        //todo dodelat predani spravneho stringu
        textFragment.setEducationText("tada");
    }

    @Override
    protected void changeToEducationFragment() {
        super.changeToEducationFragment();
        Toast.makeText(this, "Ukázka izomorfních grafů", Toast.LENGTH_LONG).show();
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
