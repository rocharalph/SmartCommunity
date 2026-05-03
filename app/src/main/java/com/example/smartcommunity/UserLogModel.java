package com.example.smartcommunity;

public class UserLogModel {
    private String logId;
    private String userId;
    private String action;
    private String details;
    private long timestamp;

    public UserLogModel() {
    }

    public UserLogModel(String logId, String userId, String action, String details, long timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getLogId() {
        return logId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public long getTimestamp() {
        return timestamp;
    }
}