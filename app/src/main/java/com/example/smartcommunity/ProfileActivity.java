package com.example.smartcommunity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgProfile;
    EditText etFullName, etEmail, etStudentId, etContactNumber, etAddress, etRole;
    Button btnSaveProfile;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference usersRef;

    String profileImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etStudentId = findViewById(R.id.etStudentId);
        etContactNumber = findViewById(R.id.etContactNumber);
        etAddress = findViewById(R.id.etAddress);
        etRole = findViewById(R.id.etRole);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        loadUserProfile();

        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etEmail.setText(currentUser.getEmail());

                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String studentId = snapshot.child("studentId").getValue(String.class);
                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);
                    profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                    if (fullName != null) etFullName.setText(fullName);
                    if (studentId != null) etStudentId.setText(studentId);
                    if (contactNumber != null) etContactNumber.setText(contactNumber);
                    if (address != null) etAddress.setText(address);
                    if (role != null) etRole.setText(role);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(profileImageUrl)
                                .centerCrop()
                                .into(imgProfile);
                    }
                } else {
                    etRole.setText("Student");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        String fullName = etFullName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String contactNumber = etContactNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String email = currentUser.getEmail();
        String role = etRole.getText().toString().trim();

        if (fullName.isEmpty() || studentId.isEmpty() || contactNumber.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("studentId", studentId);
        userMap.put("contactNumber", contactNumber);
        userMap.put("address", address);
        userMap.put("email", email);
        userMap.put("role", role.isEmpty() ? "Student" : role);
        userMap.put("profileImage", profileImageUrl);

        usersRef.setValue(userMap)
                .addOnSuccessListener(unused ->
                        Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}