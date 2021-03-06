package cz.uhk.graphtheory.abstraction;

import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.common.DrawingFragment;
import cz.uhk.graphtheory.common.SecondaryTabLayoutFragment;
import cz.uhk.graphtheory.common.TabLayoutFragment;
import cz.uhk.graphtheory.common.TextFragment;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.model.User;
import cz.uhk.graphtheory.util.Util;

public abstract class AbstractActivity extends AbstractAppCompactActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment educationFragment;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private DatabaseConnector databaseConnector;
    private TabLayoutFragment tabLayoutFragment;
    TextView navigationDrawerName, navigationDrawerEmail;
    private SecondaryTabLayoutFragment secondaryLayoutFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_generator);
        floatingActionButton = findViewById(R.id.floating_action_button_generate_graph);
        bottomNavigationView = findViewById(R.id.graph_generator_navigation);

        databaseConnector = new DatabaseConnector();

        //for navigation drawer
        Toolbar toolbar = findViewById(R.id.graph_generator_toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        drawingFragment = new DrawingFragment();
        textFragment = new TextFragment();
        educationFragment = getGraphFragment();
        ArrayList<String> tabName = getTabNames();
        secondaryLayoutFragment = new SecondaryTabLayoutFragment(tabName);

        tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.generator_activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.generator_activity_group, textFragment);
        fragmentTransaction.commit();

        if (!Util.isNetworkConnected(this)){
            showSnackBar("Ajaj, nejste připojeni k internetu, bez toho to nebude bohužel fungovat");
        }

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
            fragmentTransaction.remove(secondaryLayoutFragment);
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
            fragmentTransaction.remove(secondaryLayoutFragment);
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
            fragmentTransaction.remove(secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, textFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.remove(secondaryLayoutFragment);
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
            fragmentTransaction.add(R.id.generator_activity_group, secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, educationFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        } else if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.generator_activity_group, secondaryLayoutFragment);
            fragmentTransaction.add(R.id.generator_activity_group, educationFragment);
            fragmentTransaction.commit();
            hideBottomNavigationView();
            floatingActionButton.hide();
        }

    }

    public void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("");
        dialog.setMessage("Máš to správně! Chceš si to zkusit ještě jednou, nebo jít na další?");
        dialog.setPositiveButton("Další aktivita", (dialog1, id) -> onPositiveButtonClick())
                .setNegativeButton("Znovu procvičit", (dialog12, which) -> onNegativeButtonClick());

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    protected void showSnackBar(String snackTitle) {
        View view = findViewById(R.id.frame_layout);
        Snackbar snackbar = Snackbar.make(view, snackTitle, Snackbar.LENGTH_INDEFINITE);

        //další monožsti stylování snackBaru
//        snackbarActionTextView.setTextSize( 20 );
//        snackbarActionTextView.setTypeface(snackbarActionTextView.getTypeface(), Typeface.BOLD);

        //tohle přepíše defaultní počet řádků (2) na 3
        //android X má nově com.google.android.material.R namísto android.support.design.R
        TextView snackBarTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackBarTextView.setMaxLines( 3 );

        //this will set text size of snackbar
        final View snackView = snackbar.getView();
        final TextView snackTextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.snackBar_textSize)); //řekne, že číslo bude v SP a hodnotu vezme z dimens

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    /**
     * this method is kinda hack - due to unsychronous fragment transactions this will check every 0.5sec if transaction is done and then it will set proper parameter
     */
    protected void waitForDrawingFragment(String value) {
        new Handler().postDelayed(() -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getFragments().contains(drawingFragment)) {
                drawingFragment.changeDrawingMethod(value); //this will enable moving nodes
            }else{
                waitForDrawingFragment(value);
            }
        }, 500);
    }

    public void onPositiveButtonClick(){

    }

    public void onNegativeButtonClick(){

    }


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

    protected abstract ArrayList<String> getTabNames();


}

