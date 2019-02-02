package com.tqnee.KamS3r.Model;

import java.io.Serializable;

/**
 * Created by ramzy on 9/26/2017.
 */

public class MessagesModel implements Serializable {
    String thread_id;
    String sender_id;
    String receiver_id;
    String other_person_id;
    String other_person_image;
    String message;
    boolean message_seen;
    String other_person_name;
    private String time;


    public String getOther_person_name() {
        return other_person_name;
    }

    public void setOther_person_name(String other_person_name) {
        this.other_person_name = other_person_name;
    }

    public boolean isMessage_seen() {
        return message_seen;
    }

    public void setMessage_seen(boolean message_seen) {
        this.message_seen = message_seen;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getOther_person_id() {
        return other_person_id;
    }

    public void setOther_person_id(String other_person_id) {
        this.other_person_id = other_person_id;
    }

    public String getOther_person_image() {
        return other_person_image;
    }

    public void setOther_person_image(String other_person_image) {
        this.other_person_image = other_person_image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
