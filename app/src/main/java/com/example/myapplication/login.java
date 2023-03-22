package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.R)
public class login extends AppCompatActivity {
    DatabaseReference fDatabase;

    private static final int REQUEST_CODE = 1;
    String imagepath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        fDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();


        TextView error = findViewById(R.id.errormoney);
        error.setVisibility(View.INVISIBLE);

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ArrayList<String> users = new ArrayList<>();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String nickname = userSnapshot.child("nickname").getValue(String.class);
                    users.add(nickname);
                }

                String currentUserNickname = ((TextView) findViewById(R.id.usernickname)).getText().toString();
                users.remove(currentUserNickname);

                Spinner spinner = findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(login.this, android.R.layout.simple_spinner_item, users);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String selectedUserNickname = adapterView.getItemAtPosition(position).toString();
                        sendmoney(selectedUserNickname);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        String selected = null;
        sendmoney(selected);
        gobackmainpage();
        Bundle extras = getIntent().getExtras();
        String value = extras.getString("key");
        StorageReference storageRef = storage.getReference().child("images/" + value + ".jpg");
        getUserInfo(value);
        initializeUI();
        ImageButton chooseImageButton = findViewById(R.id.fileopen);

        // Initialize the ActivityResultLauncher for the file picker
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    // Get the selected image and set it as the source of the ImageView
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView.setImageBitmap(bitmap);
                        UploadTask uploadTask = storageRef.putFile(uri);
                        fDatabase.child("users").child(value).child("imageloc").setValue("a");

                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                });

        // Request the permission if it hasn't been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else {
            // Set OnClickListener to the button if the permission has already been granted
            chooseImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Set OnClickListener to the button if the permission has been granted
                ImageButton chooseImageButton = findViewById(R.id.fileopen);
                chooseImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
            } else {
                // Permission has not been granted, handle it here
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        ImageView imageView = findViewById(R.id.imageView2);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imagepath = uri.getPath();
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


    // Define UI elements
    private TextView name, surname, nickname, password, balance;

    private void initializeUI() {
        name = findViewById(R.id.username);
        surname = findViewById(R.id.usersurname);
        nickname = findViewById(R.id.usernickname);
        password = findViewById(R.id.userpassword);
        balance = findViewById(R.id.userbalance);
    }

    private void getUserInfo(String userID) {
        // Get a reference to the Firebase database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Define the database query for the user
        DatabaseReference userRef = rootRef.child("users").child(userID);

        // Attach a listener for the "value" event to the user query
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Extract the user information from the snapshot
                String userName = snapshot.child("name").getValue(String.class);
                String userSurname = snapshot.child("surname").getValue(String.class);
                String userNickname = snapshot.child("nickname").getValue(String.class);
                String userPassword = snapshot.child("password").getValue(String.class);
                Long userBalance = snapshot.child("wallet").getValue(Long.class);
                String userImage = snapshot.child("imageloc").getValue(String.class);
                if(Objects.equals(userImage, "x")){
                    ImageView imageView = findViewById(R.id.imageView2);
                    imageView.setImageResource(R.drawable.x);
                }
                else{
                    StorageReference imageRef = storageRef.child("images/" + userNickname + ".jpg");
                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final File finalLocalFile = localFile;
                    imageRef.getFile(Objects.requireNonNull(localFile))
                            .addOnSuccessListener(taskSnapshot -> {
                                ImageView imageView = findViewById(R.id.imageView2);
                                // Image downloaded successfully, set it to the ImageView
                                Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                                imageView.setImageBitmap(bitmap);
                            });
                }

                // Update the UI elements with the user information
                name.setText(userName);
                surname.setText(userSurname);
                nickname.setText(userNickname);
                password.setText(userPassword);
                balance.setText(userBalance + " $");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors in case the database query is canceled
            }
        });
    }
    private void sendmoney(String selected) {
        TextView error = findViewById(R.id.errormoney);
        error.setVisibility(View.INVISIBLE);
        TextView nickname = findViewById(R.id.usernickname);
        TextView wallet = findViewById(R.id.userbalance);
        EditText money = findViewById(R.id.moneytext);
        Button sbutton = findViewById(R.id.button2);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        sbutton.setOnClickListener(view -> {
            if (selected == null) {
                return;
            }

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String senderNickname = nickname.getText().toString();
                    Long senderMoney = snapshot.child(senderNickname).child("wallet").getValue(Long.class);
                    Long takerMoney = snapshot.child(selected).child("wallet").getValue(Long.class);

                    if (senderMoney == null || takerMoney == null) {
                        return;
                    }

                    try {
                        int senderMoneyInt = senderMoney.intValue();
                        int takerMoneyInt = takerMoney.intValue();
                        int amount = Integer.parseInt(money.getText().toString());

                        if (amount <= 0) {
                            error.setText("Please enter a valid amount.");
                            error.setVisibility(View.VISIBLE);
                            return;
                        }

                        if (senderMoneyInt < amount) {
                            error.setText("You don't have enough money.");
                            error.setVisibility(View.VISIBLE);
                            return;
                        }

                        int senderMoneyNew = senderMoneyInt - amount;
                        int takerMoneyNew = takerMoneyInt + amount;

                        fDatabase.child("users").child(senderNickname).child("wallet").setValue(senderMoneyNew);
                        fDatabase.child("users").child(selected).child("wallet").setValue(takerMoneyNew);

                        wallet.setVisibility(View.INVISIBLE);
                        wallet.setText(senderMoneyNew + " $");
                        wallet.setVisibility(View.VISIBLE);
                    } catch (NumberFormatException e) {
                        error.setText("Please enter a valid amount.");
                        error.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }
    private void gobackmainpage() {
        ImageButton bbutton = findViewById(R.id.button);
        bbutton.setOnClickListener(view -> finish());
    }
}