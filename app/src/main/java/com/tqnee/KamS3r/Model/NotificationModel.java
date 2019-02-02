package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 9/25/2017.
 */

public class NotificationModel {
    String notification_id;
    int notification_type;
    String notification_title;
    String notification_username;
    String notification_user_image;
    String notify_id;
    String notification_user_id;
    String notification_comment;
    private String notification_time;

    public String getNotification_comment() {
        return notification_comment;
    }

    public void setNotification_comment(String notification_comment) {
        this.notification_comment = notification_comment;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public int getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(int notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }

    public String getNotification_username() {
        return notification_username;
    }

    public void setNotification_username(String notification_username) {
        this.notification_username = notification_username;
    }

    public String getNotification_user_image() {
        return notification_user_image;
    }

    public void setNotification_user_image(String notification_user_image) {
        this.notification_user_image = notification_user_image;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }

    public String getNotification_user_id() {
        return notification_user_id;
    }

    public void setNotification_user_id(String notification_user_id) {
        this.notification_user_id = notification_user_id;
    }


    public String getNotification_time() {
        return notification_time;
    }

    public void setNotification_time(String notification_time) {
        this.notification_time = notification_time;
    }
}
