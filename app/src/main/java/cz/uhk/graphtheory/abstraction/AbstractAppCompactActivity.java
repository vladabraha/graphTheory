package cz.uhk.graphtheory.abstraction;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.eight.EightActivity;
import cz.uhk.graphtheory.fifth.FifthActivity;
import cz.uhk.graphtheory.first.FirstActivity;
import cz.uhk.graphtheory.fourth.FourthActivity;
import cz.uhk.graphtheory.ninth.NinthActivity;
import cz.uhk.graphtheory.second.SecondActivity;
import cz.uhk.graphtheory.seventh.SeventhActivity;
import cz.uhk.graphtheory.sixth.SixthActivity;
import cz.uhk.graphtheory.statistics.StatisticsActivity;
import cz.uhk.graphtheory.third.ThirdActivity;

public abstract class AbstractAppCompactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //tohle slouží ke znovu otevření současné aktivity při otočení displaye, tím se předejde chybám, ke kterým dochází při otočení displaye
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("tag", "Portrait");
            finish();
            startActivity(getIntent());
        }

        else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d("tag", "Landscape");
            finish();
            startActivity(getIntent());
        }
        else
            Log.w("tag", "other: " + orientation);
    }

    //this code implements all activities except the first one
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        NavigationView navigationView = findViewById(R.id.nav_view);

        //zabrani znovu spusteni pustene aktivity
        int sessionId = getIntent().getIntExtra("SESSION_ID", 0);

        if (id == R.id.nav_first) {
            if (sessionId != 1) {
                Intent newActivityIntent = new Intent(this, FirstActivity.class);
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
        } else if (id == R.id.nav_seventh) {
            if (sessionId != 7) {
                Intent newActivityIntent = new Intent(this, SeventhActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 7);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_eighth) {
            if (sessionId != 8) {
                Intent newActivityIntent = new Intent(this, EightActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 8);
                finish();
                startActivity(newActivityIntent);
            }
        } else if (id == R.id.nav_ninth) {
            if (sessionId != 9) {
                Intent newActivityIntent = new Intent(this, NinthActivity.class);
                newActivityIntent.putExtra("SESSION_ID", 9);
                finish();
                startActivity(newActivityIntent);
            }
        }else if (id == R.id.nav_statistic) {
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
