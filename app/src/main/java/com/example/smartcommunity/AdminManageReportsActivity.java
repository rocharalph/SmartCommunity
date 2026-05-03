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

public class AdminManageReportsActivity extends AppCompatActivity {

    RecyclerView recyclerAdminReports;
    ArrayList<ReportModel> reportList;
    AdminReportAdapter adminReportAdapter;
    DatabaseReference reportsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_reports);

        recyclerAdminReports = findViewById(R.id.recyclerAdminReports);
        recyclerAdminReports.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdminReports.setHasFixedSize(true);

        reportList = new ArrayList<>();
        adminReportAdapter = new AdminReportAdapter(this, reportList);
        recyclerAdminReports.setAdapter(adminReportAdapter);

        reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        loadReports();
    }

    private void loadReports() {
        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReportModel report = dataSnapshot.getValue(ReportModel.class);
                    if (report != null) {
                        reportList.add(report);
                    }
                }

                Collections.reverse(reportList);
                adminReportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminManageReportsActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }
}