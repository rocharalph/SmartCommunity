package com.example.smartcommunity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    RecyclerView recyclerUsers;
    ArrayList<UserItemModel> userList;
    AdminUsersAdapter adminUsersAdapter;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        recyclerUsers = findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setHasFixedSize(true);

        userList = new ArrayList<>();
        adminUsersAdapter = new AdminUsersAdapter(this, userList);
        recyclerUsers.setAdapter(adminUsersAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadUsers();
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String role = dataSnapshot.child("role").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    if (fullName == null || fullName.isEmpty()) fullName = "Unnamed User";
                    if (email == null) email = "No Email";
                    if (role == null || role.isEmpty()) role = "Student";
                    if (status == null || status.isEmpty()) status = "active";

                    userList.add(new UserItemModel(userId, fullName, email, role, status));
                }

                adminUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}