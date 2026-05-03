package com.example.smartcommunity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private ArrayList<ReportModel> reportList;

    public ReportAdapter(ArrayList<ReportModel> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportModel report = reportList.get(position);

        holder.tvReportTitle.setText(report.getTitle());
        holder.tvReporterName.setText("Reported by: " + report.getReporterName());
        holder.tvCategory.setText("Category: " + report.getCategory());
        holder.tvLocation.setText("Location: " + report.getLocation());
        holder.tvDescription.setText(report.getDescription());
        holder.tvStatus.setText("Status: " + report.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault());
        String formattedDateTime = sdf.format(new Date(report.getTimestamp()));
        holder.tvDateTime.setText(formattedDateTime);

        if (report.getImageUrl() != null && !report.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(report.getImageUrl())
                    .centerCrop()
                    .into(holder.imgReport);
        } else {
            holder.imgReport.setImageResource(R.mipmap.ic_launcher);
        }


        //risk detector
        String priority = report.getPriority();

        if (priority == null || priority.isEmpty()) {
            priority = "LOW";
        }

        holder.tvPriority.setText("Priority: " + priority);

        if (priority.equalsIgnoreCase("HIGH")) {
            holder.tvPriority.setTextColor(0xFFD32F2F);
        } else if (priority.equalsIgnoreCase("MEDIUM")) {
            holder.tvPriority.setTextColor(0xFFF9A825);
        } else {
            holder.tvPriority.setTextColor(0xFF2E7D32);
        }
    }
    @Override
    public int getItemCount() {
        return reportList.size();
    }
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        // assigning variables
        ImageView imgReport;
        TextView tvReportTitle, tvReporterName, tvDateTime, tvCategory, tvLocation, tvDescription, tvStatus, tvPriority;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            //initializing variables
            imgReport = itemView.findViewById(R.id.imgReport);
            tvReportTitle = itemView.findViewById(R.id.tvReportTitle);
            tvReporterName = itemView.findViewById(R.id.tvReporterName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPriority = itemView.findViewById(R.id.tvPriority);
        }
    }
}