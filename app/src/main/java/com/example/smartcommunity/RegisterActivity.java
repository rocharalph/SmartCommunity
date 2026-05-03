package com.example.smartcommunity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText username, email, password, confirmPassword, name;
    Spinner roles;
    Button registerBtn;
    TextView SignText;

    DatabaseReference usersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        SignText = findViewById(R.id.SignText);
        name = findViewById(R.id.fullname);
        roles = findViewById(R.id.spinner);



        usersRef = FirebaseDatabase.getInstance().getReference("users");


        mAuth = FirebaseAuth.getInstance();

        //register function
        registerBtn.setOnClickListener(view -> {

            String fullNameText = name.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confirmPasswordText = confirmPassword.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String role = roles.getSelectedItem().toString().trim();


            if (fullNameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordText.equals(confirmPasswordText)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            String userId = mAuth.getCurrentUser().getUid();

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", fullNameText);
                            userMap.put("email", emailText);
                            userMap.put("role",role);
                            userMap.put("status", "active");
                            userMap.put("studentId", "");
                            userMap.put("contactNumber", "");
                            userMap.put("address", "");
                            userMap.put("profileImage", "");
                            userMap.put("createdAt", System.currentTimeMillis());

                            usersRef.child(userId).setValue(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        //back to log in module
        SignText.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}