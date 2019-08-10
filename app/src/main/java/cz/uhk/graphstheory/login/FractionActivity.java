package cz.uhk.graphstheory.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import cz.uhk.graphstheory.R;

public class FractionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fraction);

        Spinner fractionSpinner = findViewById(R.id.fractionSpinner);
        Button btnEnterTeam = findViewById(R.id.btnEnterTeam);

        btnEnterTeam.setOnClickListener(v -> {
                String teamName = fractionSpinner.getSelectedItem().toString();
                if (teamName.equals( getResources().getString(R.string.blue_team))){
                    setResult(getResources().getString(R.string.blue_team));
                }else if (teamName.equals( getResources().getString(R.string.redTeam))){
                    setResult(getResources().getString(R.string.redTeam));
                } else if (teamName.equals( getResources().getString(R.string.blackTeam))){
                    setResult(getResources().getString(R.string.blackTeam));
                }
        });


    }

    private void setResult(String team) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("team", team);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
