package com.example.smartcommunity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReportDetailsActivity extends AppCompatActivity {

    ImageView imgDetailReport;
    TextView tvDetailTitle, tvDetailReporter, tvDetailDateTime,
            tvDetailCategory, tvDetailLocation, tvDetailDescription, tvDetailStatus;

    Button btnPending, btnUnderReview, btnResolved;

    DatabaseReference reportsRef, notificationsRef;
    String reportId = "";
    ReportModel currentReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report_details);

        imgDetailReport = findViewById(R.id.imgDetailReport);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailReporter = findViewById(R.id.tvDetailReporter);
        tvDetailDateTime = findViewById(R.id.tvDetailDateTime);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);

        btnPending = findViewById(R.id.btnPending);
        btnUnderReview = findViewById(R.id.btnUnderReview);
        btnResolved = findViewById(R.id.btnResolved);

        reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        reportId = getIntent().getStringExtra("reportId");

        if (reportId == null || reportId.isEmpty()) {
            Toast.makeText(this, "Invalid report ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadReportDetails();

        btnPending.setOnClickListener(v -> updateReportStatus("Pending"));
        btnUnderReview.setOnClickListener(v -> updateReportStatus("Under Review"));
        btnResolved.setOnClickListener(v -> updateReportStatus("Resolved"));
    }

    private void loadReportDetails() {
        reportsRef.child(reportId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentReport = snapshot.getValue(ReportModel.class);

                if (currentReport == null) {
                    Toast.makeText(AdminReportDetailsActivity.this, "Report not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String reporterName = currentReport.getReporterName();
                if (reporterName == null || reporterName.trim().isEmpty()) {
                    reporterName = "Anonymous";
                }

                tvDetailTitle.setText(currentReport.getTitle());
                tvDetailReporter.setText("Reported by: " + reporterName);
                tvDetailCategory.setText("Category: " + currentReport.getCategory());
                tvDetailLocation.setText("Location: " + currentReport.getLocation());
                tvDetailDescription.setText(currentReport.getDescription());
                tvDetailStatus.setText("Current Status: " + currentReport.getStatus());

                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault());
                tvDetailDateTime.setText(sdf.format(new Date(currentReport.getTimestamp())));

                if (currentReport.getImageUrl() != null && !currentReport.getImageUrl().isEmpty()) {
                    Glide.with(AdminReportDetailsActivity.this)
                            .load(currentReport.getImageUrl())
                            .centerCrop()
                            .into(imgDetailReport);
                } else {
                    imgDetailReport.setImageResource(R.mipmap.ic_launcher);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminReportDetailsActivity.this, "Failed to load report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReportStatus(String newStatus) {
        reportsRef.child(reportId).child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    tvDetailStatus.setText("Current Status: " + newStatus);

                    if (currentReport != null && currentReport.getUserId() != null) {
                        createNotification(
                                currentReport.getUserId(),
                                "Report Status Updated",
                                "Your report \"" + currentReport.getTitle() + "\" under " + currentReport.getCategory() +
                                        " is now marked as " + newStatus + "."
                        );
                    }

                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void createNotification(String userId, String title, String message) {
        String notificationId = notificationsRef.push().getKey();

        if (notificationId == null) return;

        NotificationModel notification = new NotificationModel(
                notificationId,
                userId,
                title,
                message,
                System.currentTimeMillis(),
                false
        );

        notificationsRef.child(notificationId).setValue(notification);
    }
}