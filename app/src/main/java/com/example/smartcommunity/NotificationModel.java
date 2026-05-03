package com.example.smartcommunity;

public class NotificationModel {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private long timestamp;
    private boolean read;

    public NotificationModel() {
    }

    public NotificationModel(String notificationId, String userId, String title, String message, long timestamp, boolean read) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}