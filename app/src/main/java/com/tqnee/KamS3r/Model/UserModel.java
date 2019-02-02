package com.tqnee.KamS3r.Model;

/**
 * Created by ramzy on 4/19/2017.
 */

public class UserModel {
    public static String KEY_USERNAME = "fullname";
    public static String KEY_PHONE = "phone";
    public static String KEY_EMAIL = "email";
    public static String KEY_USER_ID = "id";
    public static String KEY_USER_BIO = "about";
    public static String KEY_USRE_IMAGE = "photo";
    public static String KEY_USER_COVER = "cover";
    public static String KEY_API_TOKEN = "api_token";
    public static String KEY_USER_IMAGE = "image";
    public static String KEY_USER_COUNTRY="user_country";
    public static String KEY_USER_COUNTRY_CODE="user_country_code";


    private String name;
    private String email;
    private String phone;
    private String id;
    private String photo;
    private String cover_image;
    private String apiToken;
    private String bio;
    private String user_image;
    private String country;
    private String countryCode;

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
