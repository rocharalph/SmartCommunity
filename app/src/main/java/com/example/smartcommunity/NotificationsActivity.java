package com.example.smartcommunity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView recyclerNotifications;
    ArrayList<NotificationModel> notificationList;
    NotificationAdapter notificationAdapter;

    FirebaseAuth mAuth;
    DatabaseReference notificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerNotifications.setAdapter(notificationAdapter);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        loadNotifications(userId);
    }

    private void loadNotifications(String userId) {
        notificationsRef.get().addOnSuccessListener(snapshot -> {
            notificationList.clear();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);

                if (notification != null
                        && notification.getUserId() != null
                        && notification.getUserId().equals(userId)) {

                    notificationList.add(notification);

                    // mark as read
                    dataSnapshot.getRef().child("read").setValue(true);
                }
            }

            notificationAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show()
        );
    }
}