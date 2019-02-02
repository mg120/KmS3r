package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 9/20/2017.
 */

public class QuestionModel {
    String question_id;
    String question_title;
    String question_image;
    String question_username;
    String question_user_image;
    String question_offers_count;
    String question_answers_count;
    boolean isFavourite;
    String question_time;
    private String question_user_id;
    private String favorites_count;
    private String question_user_state;

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }

    public String getQuestion_image() {
        return question_image;
    }

    public void setQuestion_image(String question_image) {
        this.question_image = question_image;
    }

    public String getQuestion_username() {
        return question_username;
    }

    public void setQuestion_username(String question_username) {
        this.question_username = question_username;
    }

    public String getQuestion_user_image() {
        return question_user_image;
    }

    public void setQuestion_user_image(String question_user_image) {
        this.question_user_image = question_user_image;
    }

    public String getQuestion_offers_count() {
        return question_offers_count;
    }

    public void setQuestion_offers_count(String question_offers_count) {
        this.question_offers_count = question_offers_count;
    }

    public String getQuestion_answers_count() {
        return question_answers_count;
    }

    public void setQuestion_answers_count(String question_answers_count) {
        this.question_answers_count = question_answers_count;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getQuestion_time() {
        return question_time;
    }

    public void setQuestion_time(String question_time) {
        this.question_time = question_time;
    }

    public String getQuestion_user_id() {
        return question_user_id;
    }

    public void setQuestion_user_id(String question_user_id) {
        this.question_user_id = question_user_id;
    }

    public String getFavorites_count() {
        return favorites_count;
    }

    public void setFavorites_count(String favorites_count) {
        this.favorites_count = favorites_count;
    }

    public String getQuestion_user_state() {
        return question_user_state;
    }

    public void setQuestion_user_state(String question_user_state) {
        this.question_user_state = question_user_state;
    }
}
