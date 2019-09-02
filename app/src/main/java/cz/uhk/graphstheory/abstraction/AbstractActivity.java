package cz.uhk.graphstheory.abstraction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import cz.uhk.graphstheory.common.DrawingFragment;
import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.common.TabLayoutFragment;
import cz.uhk.graphstheory.database.DatabaseConnector;
import cz.uhk.graphstheory.first.GraphGeneratorActivity;
import cz.uhk.graphstheory.common.TextFragment;
import cz.uhk.graphstheory.model.User;
import cz.uhk.graphstheory.second.SecondActivity;
import cz.uhk.graphstheory.statistics.StatisticsActivity;
import cz.uhk.graphstheory.third.ThirdActivity;

public abstract class AbstractActivity extends AbstractAppCompactActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationFragment;
    private FloatingActionButton floatingActionButton;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private DatabaseConnector databaseConnector;
    private TabLayoutFragment tabLayoutFragment;
    TextView navigationDrawerName, navigationDrawerEmail;

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
        educationFragment = getGraphFragment();

        tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }


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


//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.circle:
//                drawingFragment.changeDrawingMethod("circle");
//                return true;
//            case R.id.line:
//                drawingFragment.changeDrawingMethod("line");
//                return true;
//            case R.id.delete:
//                drawingFragment.changeDrawingMethod("remove");
//                return true;
//            case R.id.clear:
//                drawingFragment.changeDrawingMethod("clear");
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


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

        } else if (fragmentManager.getFragments().contains(educationFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(educationFragment);
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
        if (fragmentManager.getFragments().contains(educationFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(educationFragment);
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
            fragmentTransaction.add(R.id.generator_activity_group, educationFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, educationFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        }

    }


//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        // Handle navigation view item clicks here.
//        int id = menuItem.getItemId();
//        //zabrani znovu spusteni pustene aktivity
//        int sessionId = getIntent().getIntExtra("SESSION_ID", 0);
//
//        if (id == R.id.paths) {
//            if (sessionId != 1) {
//                Intent newActivityIntent = new Intent(this, GraphGeneratorActivity.class);
//                newActivityIntent.putExtra("SESSION_ID", 1);
//                finish();
//                startActivity(newActivityIntent);
//            }
//        } else if (id == R.id.dalsi) {
//            if (sessionId != 2) {
//                Intent newActivityIntent = new Intent(this, SecondActivity.class);
//                newActivityIntent.putExtra("SESSION_ID", 2);
//                finish();
//                startActivity(newActivityIntent);
//            }
//        } else if (id == R.id.nav_third) {
//            if (sessionId != 3) {
//                Intent newActivityIntent = new Intent(this, ThirdActivity.class);
//                newActivityIntent.putExtra("SESSION_ID", 3);
//                finish();
//                startActivity(newActivityIntent);
//            }
//        }
//        else if (id == R.id.nav_statistic) {
//            if (sessionId != 99) {
//                Intent newActivityIntent = new Intent(this, StatisticsActivity.class);
//                newActivityIntent.putExtra("SESSION_ID", 99);
//                finish();
//                startActivity(newActivityIntent);
//            }
//        }
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


    public DrawingFragment getDrawingFragment() {
        return drawingFragment;
    }

    public TextFragment getTextFragment() {
        return textFragment;
    }

    public Fragment getGenerateGraphFragment() {
        return educationFragment;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public TabLayoutFragment getTabLayoutFragment(){return tabLayoutFragment;}

    protected abstract void hideBottomNavigationView();

    protected abstract void showBottomNavigationView();

    /**
     * abstract method needed for inflating fragment which provides generated graph
     * generated graph is the one which is provided as example for user
     *
     * @return Fragment
     */
    protected abstract Fragment getGraphFragment();
}

