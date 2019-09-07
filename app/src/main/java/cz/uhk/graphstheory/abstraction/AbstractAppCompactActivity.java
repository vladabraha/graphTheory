package cz.uhk.graphstheory.abstraction;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.fifth.FifthActivity;
import cz.uhk.graphstheory.first.GraphGeneratorActivity;
import cz.uhk.graphstheory.fourth.FourthActivity;
import cz.uhk.graphstheory.second.SecondActivity;
import cz.uhk.graphstheory.sixth.SixthActivity;
import cz.uhk.graphstheory.statistics.StatisticsActivity;
import cz.uhk.graphstheory.third.ThirdActivity;

public abstract class AbstractAppCompactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    //this code implements all activities except the first one
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        NavigationView navigationView = findViewById(R.id.nav_view);

        //zabrani znovu spusteni pustene aktivity
        int sessionId = getIntent().getIntExtra("SESSION_ID", 0);

        if (id == R.id.paths) {
            if (sessionId != 1) {
                Intent newActivityIntent = new Intent(this, GraphGeneratorActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 1);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_second) {
            if (sessionId != 2) {
                Intent newActivityIntent = new Intent(this, SecondActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 2);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_third) {
            if (sessionId != 3) {
                Intent newActivityIntent = new Intent(this, ThirdActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 3);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_fourth) {
            if (sessionId != 4) {
                Intent newActivityIntent = new Intent(this, FourthActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 4);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_fifth) {
            if (sessionId != 5) {
                Intent newActivityIntent = new Intent(this, FifthActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 5);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_sixth) {
            if (sessionId != 6) {
                Intent newActivityIntent = new Intent(this, SixthActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 6);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_statistic) {
            if (sessionId != 99) {
                Intent newActivityIntent = new Intent(this, StatisticsActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 99);
                finish();
                startActivity(newActivityIntent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
