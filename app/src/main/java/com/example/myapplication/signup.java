package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;
import java.util.Random;

public class signup extends AppCompatActivity {
    DatabaseReference  fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView Error1 = findViewById(R.id.error1);
        TextView Error2 = findViewById(R.id.error2);
        TextView Error3 = findViewById(R.id.error3);
        TextView Error4 = findViewById(R.id.error4);
        Error1.setVisibility(View.INVISIBLE);
        Error2.setVisibility(View.INVISIBLE);
        Error3.setVisibility(View.INVISIBLE);
        Error4.setVisibility(View.INVISIBLE);
        gobackmainpage();
        accountcreater();
    }

    @SuppressLint("SetTextI18n")
    private void accountcreater() {
        //errors
        String error2 = "Please enter More than 3, less than 21 characters.";
        String error = "*Required";
        TextView Error1 = findViewById(R.id.error1);
        TextView Error2 = findViewById(R.id.error2);
        TextView Error3 = findViewById(R.id.error3);
        TextView Error4 = findViewById(R.id.error4);
        //button
        Button AccountCreaterButton = findViewById(R.id.Createaccountbutton);
        AccountCreaterButton.setOnClickListener(view -> {
            EditText Textname = findViewById(R.id.Nametext);
            EditText Textsurname = findViewById(R.id.Surnametext);
            EditText Textnickname = findViewById(R.id.Nicknametext);
            EditText Textpassword = findViewById(R.id.Passtext);
            String Textn = String.valueOf(Textname.getText());
            String Textsurn = String.valueOf(Textsurname.getText());
            String Textnick = String.valueOf(Textnickname.getText());
            String Textpass = String.valueOf(Textpassword.getText());
            //checkers
            boolean a;
            boolean b;
            boolean c;
            boolean d;
            //start checking
            if (checkconnect()) {
                //if1
                if (Textn.isEmpty()) {
                    Error1.setText(error);
                    Error1.setVisibility(View.VISIBLE);
                    a = false;
                }
                else {
                    if (Textn.length() >= 3 && Textn.length() <= 21) {
                        Error1.setVisibility(View.INVISIBLE);
                        a = true;
                    } else {
                        Error1.setText(error2);
                        Error1.setVisibility(View.VISIBLE);
                        a = false;
                    }
                }
                //if1

                //if2
                if (Textsurn.isEmpty()) {
                    Error2.setText(error);
                    Error2.setVisibility(View.VISIBLE);
                    b = false;
                } else {
                    if (Textsurn.length() >= 3 && Textsurn.length() <= 21) {
                        Error2.setVisibility(View.INVISIBLE);
                        b = true;
                    } else {
                        Error2.setText(error2);
                        Error2.setVisibility(View.VISIBLE);
                        b = false;
                    }
                }
                //if2

                //if3
                if (Textnick.isEmpty()) {
                    Error3.setText(error);
                    Error3.setVisibility(View.VISIBLE);
                    c = false;
                } else {
                    if (Textnick.length() >= 3 && Textnick.length() <= 21) {
                        Error3.setVisibility(View.INVISIBLE);
                        c = true;
                    } else {
                        Error3.setText(error2);
                        Error3.setVisibility(View.VISIBLE);
                        c = false;
                    }
                }
                //if3

                //if4
                if (Textpass.isEmpty()) {
                    Error4.setText(error);
                    Error4.setVisibility(View.VISIBLE);
                    d = false;
                } else {
                    if (Textpass.length() >= 3 && Textpass.length() <= 21) {
                        Error4.setVisibility(View.INVISIBLE);
                        d = true;
                    } else {
                        Error4.setText(error2);
                        Error4.setVisibility(View.VISIBLE);
                        d = false;
                    }
                }
                //if4
                if (a && b && c && d) {
                    if (Textn.equals(Textnick) || Textsurn.equals(Textnick) || Textpass.equals(Textnick)) {
                        Error3.setText("*Username cant be same with name, surname or password!");
                        Error3.setVisibility(View.VISIBLE);
                    } else {
                        accountcreater2();
                    }
                }
            }
            else{
                Toast.makeText(signup.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void accountcreater2() {
        TextView Error3 = findViewById(R.id.error3);
        Error3.setVisibility(View.INVISIBLE);
        EditText Textnickname = findViewById(R.id.Nicknametext);
        String Textnick = String.valueOf(Textnickname.getText());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("users");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    accountcreater3();
                }
                else if (Objects.requireNonNull(snapshot.getValue()).toString().contains(Textnick)) {
                    Error3.setText("*This account already exists");
                    Error3.setVisibility(View.VISIBLE);
                }
                else{
                    accountcreater3();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void accountcreater3(){
        Random random = new Random();
        EditText Textname = findViewById(R.id.Nametext);
        EditText Textsurname = findViewById(R.id.Surnametext);
        EditText Textnickname = findViewById(R.id.Nicknametext);
        EditText Textpassword = findViewById(R.id.Passtext);
        String Textn = String.valueOf(Textname.getText());
        String Textsurn = String.valueOf(Textsurname.getText());
        String Textnick = String.valueOf(Textnickname.getText());
        String Textpass = String.valueOf(Textpassword.getText());
        int sayi = random.nextInt(50000) + 50000;
        User user = new User(
                Textn,
                Textsurn,
                Textnick,
                Textpass,
                "x",
                sayi


        );
        fDatabase.child("users").child(user.getNickname()).setValue(user);
        Toast.makeText(signup.this, "ACCOUNT CREATED", Toast.LENGTH_SHORT).show();
    }
    private void gobackmainpage() {
        ImageButton GoBackButton = findViewById(R.id.Gobackbutton);
        GoBackButton.setOnClickListener(view -> finish());
    }

    boolean checkconnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null){
            return networkInfo.isConnected();
        }
        else{
            return false;
        }
    }

}