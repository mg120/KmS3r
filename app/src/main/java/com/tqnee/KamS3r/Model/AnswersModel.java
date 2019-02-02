package com.tqnee.KamS3r.Model;

import java.io.Serializable;

/**
 * Created by ramzy on 5/16/2017.
 */

public class AnswersModel implements Serializable {
    String answer_id;
    String answer_price;
    String answer_currency;
    String answer_content;
    String comments_number;
    String answer_user_name;
    String answer_user_id;
    String answer_user_image;
    String question_title;
    boolean fav;

    public String getComments_number() {
        return comments_number;
    }

    public void setComments_number(String comments_number) {
        this.comments_number = comments_number;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }


    public String getAnswer_content() {
        return answer_content;
    }

    public void setAnswer_content(String answer_content) {
        this.answer_content = answer_content;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getAnswer_user_name() {
        return answer_user_name;
    }

    public void setAnswer_user_name(String answer_user_name) {
        this.answer_user_name = answer_user_name;
    }

    public String getAnswer_user_id() {
        return answer_user_id;
    }

    public void setAnswer_user_id(String answer_user_id) {
        this.answer_user_id = answer_user_id;
    }

    public String getAnswer_user_image() {
        return answer_user_image;
    }

    public void setAnswer_user_image(String answer_user_image) {
        this.answer_user_image = answer_user_image;
    }

    public String getAnswer_price() {
        return answer_price;
    }

    public void setAnswer_price(String answer_price) {
        this.answer_price = answer_price;
    }

    public String getAnswer_currency() {
        return answer_currency;
    }

    public void setAnswer_currency(String answer_currency) {
        this.answer_currency = answer_currency;
    }
}
