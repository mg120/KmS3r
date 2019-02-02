package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 9/20/2017.
 */

public class OfferModel {
    String offer_id;
    String offer_title;
    String offer_image;
    String offer_username;
    String offer_user_image;
    boolean isFavourite;
    String offer_time;
    private String offer_user_id;
    private String offer_state;
    private String offer_price;

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_title() {
        return offer_title;
    }

    public void setOffer_title(String offer_title) {
        this.offer_title = offer_title;
    }

    public String getOffer_image() {
        return offer_image;
    }

    public void setOffer_image(String offer_image) {
        this.offer_image = offer_image;
    }

    public String getOffer_username() {
        return offer_username;
    }

    public void setOffer_username(String offer_username) {
        this.offer_username = offer_username;
    }

    public String getOffer_user_image() {
        return offer_user_image;
    }

    public void setOffer_user_image(String offer_user_image) {
        this.offer_user_image = offer_user_image;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getOffer_time() {
        return offer_time;
    }

    public void setOffer_time(String offer_time) {
        this.offer_time = offer_time;
    }

    public String getOffer_user_id() {
        return offer_user_id;
    }

    public void setOffer_user_id(String offer_user_id) {
        this.offer_user_id = offer_user_id;
    }

    public String getOffer_state() {
        return offer_state;
    }

    public void setOffer_state(String offer_state) {
        this.offer_state = offer_state;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }
}
