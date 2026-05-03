package com.example.smartcommunity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Collections;

public class all_reports extends AppCompatActivity {

    RecyclerView recyclerReports;
    ArrayList<ReportModel> allReportList;
    ArrayList<ReportModel> filteredReportList;
    ReportAdapter reportAdapter;
    DatabaseReference reportsRef;

    Button btnFilterDate, btnClearFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        recyclerReports = findViewById(R.id.recyclerReports);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        recyclerReports.setHasFixedSize(true);

        allReportList = new ArrayList<>();
        filteredReportList = new ArrayList<>();

        reportAdapter = new ReportAdapter(filteredReportList);
        recyclerReports.setAdapter(reportAdapter);

        reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        loadReports();

        btnFilterDate.setOnClickListener(v -> showDatePicker());
        btnClearFilter.setOnClickListener(v -> clearFilter());
    }

    private void loadReports() {
        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allReportList.clear();
                filteredReportList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReportModel report = dataSnapshot.getValue(ReportModel.class);
                    if (report != null) {
                        allReportList.add(report);
                    }
                }

                Collections.reverse(allReportList);
                filteredReportList.addAll(allReportList);
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(all_reports.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> filterReportsByDate(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void filterReportsByDate(int year, int month, int dayOfMonth) {
        filteredReportList.clear();

        Calendar selectedStart = Calendar.getInstance();
        selectedStart.set(year, month, dayOfMonth, 0, 0, 0);
        selectedStart.set(Calendar.MILLISECOND, 0);

        Calendar selectedEnd = Calendar.getInstance();
        selectedEnd.set(year, month, dayOfMonth, 23, 59, 59);
        selectedEnd.set(Calendar.MILLISECOND, 999);

        long startTime = selectedStart.getTimeInMillis();
        long endTime = selectedEnd.getTimeInMillis();

        for (ReportModel report : allReportList) {
            long reportTime = report.getTimestamp();

            if (reportTime >= startTime && reportTime <= endTime) {
                filteredReportList.add(report);
            }
        }

        reportAdapter.notifyDataSetChanged();

        if (filteredReportList.isEmpty()) {
            Toast.makeText(this, "No reports found on selected date", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Filtered reports: " + filteredReportList.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFilter() {
        filteredReportList.clear();
        filteredReportList.addAll(allReportList);
        reportAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Filter cleared", Toast.LENGTH_SHORT).show();
    }
}