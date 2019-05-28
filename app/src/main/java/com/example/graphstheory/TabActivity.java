package com.example.graphstheory;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

public class TabActivity extends AppCompatActivity implements TableLayoutFragment.TableLayoutCommunicationInterface {

    private TextFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new TextFragment();
        TableLayoutFragment tableLayoutFragment = new TableLayoutFragment();
        fragmentTransaction.add(R.id.activity_group, tableLayoutFragment);
        fragmentTransaction.add(R.id.activity_group, fragment);
        fragmentTransaction.commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.circle:
                fragment.changeDrawingMethod("circle");
                return true;
            case R.id.line:
                fragment.changeDrawingMethod("line");
                return true;
            case R.id.clear:
                fragment.changeDrawingMethod("clear");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void tableLayoutSelectedChange(int number) {
        switch (number){
            case 0:
                removeDrawingFragment();
                break;
            case 1:
                removeDrawingFragment();
                break;
            case 2:
                addDrawingFragment();
                break;
        }
    }

    private void removeDrawingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        TableLayoutFragment tableLayoutFragment = new TableLayoutFragment();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    private void addDrawingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new TextFragment();
        TableLayoutFragment tableLayoutFragment = new TableLayoutFragment();

        fragmentTransaction.add(R.id.activity_group, fragment);
        fragmentTransaction.commit();
    }

    public interface OnFragmentInteractionListener {
        void changeDrawingMethod(String method);
    }
}
