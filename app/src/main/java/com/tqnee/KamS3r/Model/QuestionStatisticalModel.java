package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 9/24/2017.
 */

public class QuestionStatisticalModel {
    String statistical_id;
    String statistical_title;
    String statistical_percentage;
    String statistical_answer_count;
    String statistical_currency;

    public String getStatistical_currency() {
        return statistical_currency;
    }

    public void setStatistical_currency(String statistical_currency) {
        this.statistical_currency = statistical_currency;
    }

    public String getStatistical_id() {
        return statistical_id;
    }

    public void setStatistical_id(String statistical_id) {
        this.statistical_id = statistical_id;
    }

    public String getStatistical_title() {
        return statistical_title;
    }

    public void setStatistical_title(String statistical_title) {
        this.statistical_title = statistical_title;
    }

    public String getStatistical_percentage() {
        return statistical_percentage;
    }

    public void setStatistical_percentage(String statistical_percentage) {
        this.statistical_percentage = statistical_percentage;
    }

    public String getStatistical_answer_count() {
        return statistical_answer_count;
    }

    public void setStatistical_answer_count(String statistical_answer_count) {
        this.statistical_answer_count = statistical_answer_count;
    }
}
