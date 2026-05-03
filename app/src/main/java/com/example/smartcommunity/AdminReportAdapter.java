package com.example.smartcommunity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminReportAdapter extends RecyclerView.Adapter<AdminReportAdapter.AdminReportViewHolder> {

    Context context;
    ArrayList<ReportModel> reportList;

    public AdminReportAdapter(Context context, ArrayList<ReportModel> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public AdminReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_report_item, parent, false);
        return new AdminReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportViewHolder holder, int position) {
        ReportModel report = reportList.get(position);

        String reporterName = report.getReporterName();
        if (reporterName == null || reporterName.trim().isEmpty()) {
            reporterName = "Anonymous";
        }

        holder.tvAdminReportTitle.setText(report.getTitle());
        holder.tvAdminReporterName.setText("Reported by: " + reporterName);
        holder.tvAdminCategory.setText("Category: " + report.getCategory());
        holder.tvAdminLocation.setText("Location: " + report.getLocation());
        holder.tvAdminDescription.setText(report.getDescription());
        holder.tvAdminStatus.setText("Status: " + report.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault());
        holder.tvAdminDateTime.setText(sdf.format(new Date(report.getTimestamp())));

        if (report.getImageUrl() != null && !report.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(report.getImageUrl())
                    .centerCrop()
                    .into(holder.imgAdminReport);
        } else {
            holder.imgAdminReport.setImageResource(R.mipmap.ic_launcher);
        }

        holder.btnOpenReport.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminReportDetailsActivity.class);
            intent.putExtra("reportId", report.getReportId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class AdminReportViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAdminReport;
        TextView tvAdminReportTitle, tvAdminReporterName, tvAdminDateTime,
                tvAdminCategory, tvAdminLocation, tvAdminDescription, tvAdminStatus, tvAdminPriority;
        Button btnOpenReport;

        public AdminReportViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAdminReport = itemView.findViewById(R.id.imgAdminReport);
            tvAdminReportTitle = itemView.findViewById(R.id.tvAdminReportTitle);
            tvAdminReporterName = itemView.findViewById(R.id.tvAdminReporterName);
            tvAdminDateTime = itemView.findViewById(R.id.tvAdminDateTime);
            tvAdminCategory = itemView.findViewById(R.id.tvAdminCategory);
            tvAdminLocation = itemView.findViewById(R.id.tvAdminLocation);
            tvAdminDescription = itemView.findViewById(R.id.tvAdminDescription);
            tvAdminStatus = itemView.findViewById(R.id.tvAdminStatus);
            btnOpenReport = itemView.findViewById(R.id.btnOpenReport);
            tvAdminPriority = itemView.findViewById(R.id.tvAdminPriority);
        }
    }
}