package com.tqnee.KamS3r.Model;

import java.io.Serializable;

/**
 * Created by ramzy on 5/28/2017.
 */

public class AnswerCommentModel implements Serializable {
    String comment_id;
    String comment_content;
    String comment_user_id;
    String comment_user_name;
    String comment_user_image;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getComment_user_name() {
        return comment_user_name;
    }

    public void setComment_user_name(String comment_user_name) {
        this.comment_user_name = comment_user_name;
    }

    public String getComment_user_image() {
        return comment_user_image;
    }

    public void setComment_user_image(String comment_user_image) {
        this.comment_user_image = comment_user_image;
    }
}
