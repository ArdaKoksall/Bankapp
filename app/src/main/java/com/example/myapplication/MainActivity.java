package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView errorTextView = findViewById(R.id.textView3);
        errorTextView.setVisibility(View.INVISIBLE);

        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(v -> startLogin());

        Button signUpButton = findViewById(R.id.signupbutton);
        signUpButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, signup.class)));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void startLogin() {
        if (!isConnected()) {
            Toast.makeText(MainActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView errorTextView = findViewById(R.id.textView3);
        EditText nicknameEditText = findViewById(R.id.nicknametext2);
        EditText passwordEditText = findViewById(R.id.passtext2);
        String nickname = nicknameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        databaseReference.child(nickname).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String passwordFromDatabase = String.valueOf(task.getResult().child("password").getValue());
                if (password.equals(passwordFromDatabase)) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    Intent i = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        i = new Intent(MainActivity.this, login.class);
                    }
                    Objects.requireNonNull(i).putExtra("key", nickname);
                    nicknameEditText.setText("");
                    passwordEditText.setText("");
                    startActivity(i);
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            } else {
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isConnected() {
        // Check internet connectivity
        return getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) instanceof android.net.ConnectivityManager &&
                ((android.net.ConnectivityManager) getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
