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
import java.util.Collections;

public class AdminUserLogsActivity extends AppCompatActivity {

    RecyclerView recyclerUserLogs;
    ArrayList<UserLogModel> logList;
    UserLogsAdapter userLogsAdapter;
    DatabaseReference logsRef;

    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_logs);

        recyclerUserLogs = findViewById(R.id.recyclerUserLogs);
        recyclerUserLogs.setLayoutManager(new LinearLayoutManager(this));

        logList = new ArrayList<>();
        userLogsAdapter = new UserLogsAdapter(logList);
        recyclerUserLogs.setAdapter(userLogsAdapter);

        userId = getIntent().getStringExtra("userId");
        logsRef = FirebaseDatabase.getInstance().getReference("user_logs");

        loadLogs();
    }

    private void loadLogs() {
        logsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserLogModel log = dataSnapshot.getValue(UserLogModel.class);
                    if (log != null && log.getUserId() != null && log.getUserId().equals(userId)) {
                        logList.add(log);
                    }
                }

                Collections.reverse(logList);
                userLogsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUserLogsActivity.this, "Failed to load logs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}