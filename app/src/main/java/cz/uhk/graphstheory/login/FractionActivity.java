package cz.uhk.graphstheory.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import cz.uhk.graphstheory.R;

public class FractionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fraction);

        EditText teamName = findViewById(R.id.editTeamName);
        Button btnEnterTeam = findViewById(R.id.btnEnterTeam);

        btnEnterTeam.setOnClickListener(v -> {
            String nameOfTeam = teamName.getText().toString().toLowerCase().trim();
            String firstCharacter = nameOfTeam.substring(0, 1).toUpperCase();
            nameOfTeam = firstCharacter + nameOfTeam.substring(1);
            setResult(nameOfTeam);
        });

    }

    private void setResult(String team) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("team", team);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
