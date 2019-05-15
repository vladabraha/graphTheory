package com.example.graphstheory;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TabActivity extends AppCompatActivity {

    private TextFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new TextFragment();
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
//                paintView.circle();
                fragment.changeDrawingMethod("circle");
                return true;
            case R.id.line:
                fragment.changeDrawingMethod("line");
//                paintView.line();
                return true;

            case R.id.clear:
                fragment.changeDrawingMethod("clear");
//                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void changeDrawingMethod(String method);
    }
}
