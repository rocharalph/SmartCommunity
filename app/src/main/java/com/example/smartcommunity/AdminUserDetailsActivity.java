package com.example.smartcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminUserDetailsActivity extends AppCompatActivity {

    TextView tvDetailFullName, tvDetailEmail, tvDetailStudentId, tvDetailContact,
            tvDetailAddress, tvDetailRole, tvDetailStatus;
    Button btnViewLogs;

    DatabaseReference userRef, reportsRef, notificationsRef;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        tvDetailFullName = findViewById(R.id.tvDetailFullName);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        tvDetailStudentId = findViewById(R.id.tvDetailStudentId);
        tvDetailContact = findViewById(R.id.tvDetailContact);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailRole = findViewById(R.id.tvDetailRole);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);

        btnViewLogs = findViewById(R.id.btnViewLogs);


        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        loadUserDetails();

        btnViewLogs.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminUserLogsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    private void loadUserDetails() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(AdminUserDetailsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String fullName = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String studentId = snapshot.child("studentId").getValue(String.class);
                String contact = snapshot.child("contactNumber").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                tvDetailFullName.setText("Full Name: " + (fullName != null ? fullName : "N/A"));
                tvDetailEmail.setText("Email: " + (email != null ? email : "N/A"));
                tvDetailStudentId.setText("Student ID: " + (studentId != null ? studentId : "N/A"));
                tvDetailContact.setText("Contact: " + (contact != null ? contact : "N/A"));
                tvDetailAddress.setText("Address: " + (address != null ? address : "N/A"));
                tvDetailRole.setText("Role: " + (role != null ? role : "Student"));
                tvDetailStatus.setText("Status: " + (status != null ? status : "active"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUserDetailsActivity.this, "Failed to load user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}