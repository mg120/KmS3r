package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 6/7/2017.
 */

public class MessageModel {
    String message_id;
    String sender_user_image;
    String sender_user_name;
    String message_content;
    String sender_user_id;
    private String sender_time;
    private String receiver_time;

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender_user_image() {
        return sender_user_image;
    }

    public void setSender_user_image(String sender_user_image) {
        this.sender_user_image = sender_user_image;
    }

    public String getSender_user_name() {
        return sender_user_name;
    }

    public void setSender_user_name(String sender_user_name) {
        this.sender_user_name = sender_user_name;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getSender_time() {
        return sender_time;
    }

    public void setSender_time(String sender_time) {
        this.sender_time = sender_time;
    }

    public String getReceiver_time() {
        return receiver_time;
    }

    public void setReceiver_time(String receiver_time) {
        this.receiver_time = receiver_time;
    }
}
