package com.example.smartcommunity;

public class ReportModel {

    private String reportId;
    private String userId;
    private String reporterName;
    private String title;
    private String description;
    private String location;
    private String category;
    private String imageUrl;
    private String status;
    private long timestamp;

    private String priority;

    public ReportModel() {
    }

    public ReportModel(String reportId, String userId, String reporterName, String title, String description,
                       String location, String category, String imageUrl,
                       String status, String priority , long timestamp) {
        this.reportId = reportId;
        this.userId = userId;
        this.reporterName = reporterName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.category = category;
        this.imageUrl = imageUrl;
        this.status = status;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public String getReportId() {
        return reportId;
    }

    public String getUserId() {
        return userId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }
    public long getTimestamp() {
        return timestamp;
    }
}