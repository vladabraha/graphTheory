package cz.uhk.graphtheory.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import cz.uhk.graphtheory.R;
import cz.uhk.graphtheory.database.DatabaseConnector;
import cz.uhk.graphtheory.first.FirstActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText emailEditText, passwordEditText, nickNameEditText;
    private DatabaseConnector databaseConnector;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.sign_up);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        nickNameEditText = findViewById(R.id.nickname);

        databaseConnector = new DatabaseConnector();

        loginButton.setOnClickListener((View v) -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");

                            Intent mainIntent = new Intent(LoginActivity.this, FirstActivity.class);
                            startActivity(mainIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Nesprávné uživatelské jméno nebo heslo", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signUpButton.setOnClickListener((View v) -> {
            String password = passwordEditText.getText().toString();
            String nickName = nickNameEditText.getText().toString();
            String email = emailEditText.getText().toString();

            if (isAccountValid(password, nickName, email)) {
                Intent fractionIntent = new Intent(this, FractionActivity.class);
                startActivityForResult(fractionIntent, 1);
            }
        });
    }

    private boolean isAccountValid(String password, String nickName, String email) {
        if (password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Heslo musí mít alespoň 6 znaků", Toast.LENGTH_SHORT).show();
        } else if (!databaseConnector.emailAvailable(nickName)) {
            Toast.makeText(LoginActivity.this, "Tato přezdívka je již zabraná", Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(LoginActivity.this, "Zadaný email není ve validním formátu", Toast.LENGTH_SHORT).show();
        } else if (email.length() > 25 && nickName.length() > 25) {
            Toast.makeText(LoginActivity.this, "Zadaný email, nebo přezdívka má moc znaků", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void registerUser(String email, String password, String selectedTeam) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //this method is called asynchronously -> never called as classic method
                        Log.d("TAG", "createUserWithEmail:success");
                        createUserAndStartActivity(selectedTeam);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserAndStartActivity(String selectedTeam) {
        FirebaseUser user = mAuth.getCurrentUser();

        databaseConnector.createUserAccount(Objects.requireNonNull(user).getUid(), nickNameEditText.getText().toString(), Objects.requireNonNull(user.getEmail()), selectedTeam);

        Intent mainIntent = new Intent(this, FirstActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            Intent notificationIntent = new Intent(this, FirstActivity.class);
            startActivity(notificationIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String selectedTeam = data.getStringExtra("team");
                registerUser(email, password, selectedTeam);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(LoginActivity.this, "Registrace byla zrušena", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
    }
}
