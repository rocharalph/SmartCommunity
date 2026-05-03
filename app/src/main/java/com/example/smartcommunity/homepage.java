package com.example.smartcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//themes switch
import android.content.SharedPreferences;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class homepage extends AppCompatActivity {

    LinearLayout reportBtn, viewReportsBtn, notificationBtn, profileBtn, logoutBtn;
    TextView tvNotifBadge;

    FirebaseAuth mAuth;
    DatabaseReference notifRef;
    ValueEventListener notifListener;

    Switch themeSwitch;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mAuth = FirebaseAuth.getInstance();
        notifRef = FirebaseDatabase.getInstance().getReference("notifications");

        tvNotifBadge = findViewById(R.id.tvNotifBadge);
        reportBtn = findViewById(R.id.reportBtn);
        viewReportsBtn = findViewById(R.id.viewReportsBtn);
        notificationBtn = findViewById(R.id.notificationBtn);
        profileBtn = findViewById(R.id.profileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        attachNotificationCountListener();

        profileBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        reportBtn.setOnClickListener(v ->
                startActivity(new Intent(this, report.class))
        );

        notificationBtn.setOnClickListener(v ->
                startActivity(new Intent(homepage.this, NotificationsActivity.class))
        );

        viewReportsBtn.setOnClickListener(v ->
                startActivity(new Intent(homepage.this, all_reports.class))
        );

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        //theme switch
        themeSwitch = findViewById(R.id.themeSwitch);
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

    //notif
    private void attachNotificationCountListener() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        notifListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int count = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);

                    if (notification != null
                            && notification.getUserId() != null
                            && notification.getUserId().equals(userId)
                            && !notification.isRead()) {
                        count++;
                    }
                }

                if (count > 0) {
                    tvNotifBadge.setVisibility(View.VISIBLE);
                    tvNotifBadge.setText(String.valueOf(count));
                } else {
                    tvNotifBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        notifRef.addValueEventListener(notifListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notifListener != null) {
            notifRef.removeEventListener(notifListener);
        }
    }
}