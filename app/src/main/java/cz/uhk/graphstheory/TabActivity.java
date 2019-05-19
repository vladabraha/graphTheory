package cz.uhk.graphstheory;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TabActivity extends AppCompatActivity implements TabLayoutFragment.TableLayoutCommunicationInterface {

    private DrawingFragment drawingFragment;
    private TextFragment textFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        drawingFragment = new DrawingFragment();
        textFragment = new TextFragment();
        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        fragmentTransaction.add(R.id.activity_group, tabLayoutFragment);
        fragmentTransaction.add(R.id.activity_group, drawingFragment);
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
        switch (item.getItemId()) {
            case R.id.circle:
                drawingFragment.changeDrawingMethod("circle");
                return true;
            case R.id.line:
                drawingFragment.changeDrawingMethod("line");
                return true;
            case R.id.clear:
                drawingFragment.changeDrawingMethod("clear");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void tableLayoutSelectedChange(int number) {
        switch (number) {
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

//        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();

        //zkontroluje, že už tam neni drawing fragment a kdyžtak tam hodi text fragment
        if (fragmentManager.getFragments().contains(drawingFragment)) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(drawingFragment);
            fragmentTransaction.add(R.id.activity_group, textFragment);
            fragmentTransaction.commit();
        }
    }

    private void addDrawingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        drawingFragment = new DrawingFragment();
//        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();

        fragmentTransaction.remove(textFragment);
        fragmentTransaction.add(R.id.activity_group, drawingFragment);
        fragmentTransaction.commit();
    }

    public interface OnFragmentInteractionListener {
        void changeDrawingMethod(String method);
    }
}
