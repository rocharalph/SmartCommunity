package com.example.smartcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView tvTotalReports, tvPendingReports, tvResolvedReports, tvTotalUsers;
    Button btnManageReports, btnLogoutAdmin, btnManageUsers;


    Switch themeSwitch;
    SharedPreferences sharedPreferences;

    FirebaseAuth mAuth;
    DatabaseReference reportsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalReports = findViewById(R.id.tvTotalReports);
        tvPendingReports = findViewById(R.id.tvPendingReports);
        tvResolvedReports = findViewById(R.id.tvResolvedReports);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);

        //buttons
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageReports = findViewById(R.id.btnManageReports);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        mAuth = FirebaseAuth.getInstance();
        reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadDashboardCounts();

        btnManageReports.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, AdminManageReportsActivity.class))
        );

        btnManageUsers.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class))
        );

        btnLogoutAdmin.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });


        //theme switch
        themeSwitch = findViewById(R.id.adthemeSwitch);
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);

        themeSwitch.setChecked(isDarkMode);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void loadDashboardCounts() {
        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalReports = 0;
                int pendingReports = 0;
                int resolvedReports = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    totalReports++;

                    String status = dataSnapshot.child("status").getValue(String.class);

                    if (status != null) {
                        if (status.equalsIgnoreCase("Pending")) {
                            pendingReports++;
                        } else if (status.equalsIgnoreCase("Resolved")) {
                            resolvedReports++;
                        }
                    }
                }

                tvTotalReports.setText(String.valueOf(totalReports));
                tvPendingReports.setText(String.valueOf(pendingReports));
                tvResolvedReports.setText(String.valueOf(resolvedReports));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load report counts", Toast.LENGTH_SHORT).show();
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvTotalUsers.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load users count", Toast.LENGTH_SHORT).show();
            }
        });
    }

}