package com.tqnee.KamS3r.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;
import com.tqnee.KamS3r.Model.UserModel;


/**
 * Created by ramzy on 5/11/2017.
 */

/**
 * this class contains shared Preferences operation
 * like caching user data
 * caching login status
 */

public class SharedPrefManager {
    final static String SHARED_PREF_NAME = "kams3r_shared";
    final static String LOGIN_STATUS = "kams3r_shared_login_status";
    final static String FIRST_TIME = "kams3r_shared_first_time";

    final static String COUNTRY_CODE="kams3r_shared_country_code";
    final static String COUNTRY_ID="kams3r_shared_country_id";
    final static String COUNTRY_NAME="kams3r_shared_country_name";
    final static String COUNTRY_PHONE_CODE="kams3r_shared_country_phone_code";
    final static String CURRENCY_ID="kams3r_shared_country_currency_id";
    final static String CURRENCY_TEXT="kams3r_shared_currency_text";
    final static String CURRENCY_SIGN="kams3r_shared_currency_sign";
    final static String IS_SOCIAL_REGISTER="kams3r_is_social_register";

    final static String USER_HASHTAGS="kams3r_shared_user_hashtags";
    Context mContext;


    public SharedPrefManager(Context mContext) {
        this.mContext = mContext;
    }

    public Boolean getLoginStatus() {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        Boolean value = sharedPreferences.getBoolean(LOGIN_STATUS, false);
        return value;
    }

    public void setLoginStatus(Boolean status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATUS, status);
        editor.apply();
    }

    public void setSocialRegister(Boolean isSocialRegister) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SOCIAL_REGISTER, isSocialRegister);
        editor.apply();
    }
    public boolean isSocialRegister(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getBoolean(IS_SOCIAL_REGISTER, false);
    }

    public void setCountry(String countryCode, String countryId,String countryName,String phoneCode,String currencyId,String currencyText,String currencySign){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(SHARED_PREF_NAME,0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(COUNTRY_CODE,countryCode);
        editor.putString(COUNTRY_ID,countryId);
        editor.putString(COUNTRY_NAME,countryName);
        editor.putString(COUNTRY_PHONE_CODE,phoneCode);
        editor.putString(CURRENCY_ID,currencyId);
        editor.putString(CURRENCY_TEXT,currencyText);
        editor.putString(CURRENCY_SIGN,currencySign);
        editor.apply();
    }

    public void setCountryCode(String countryCode){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(SHARED_PREF_NAME,0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(COUNTRY_CODE,countryCode);
        editor.apply();
    }
    public String getCountryCode(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(COUNTRY_CODE, "");
    }
    public String getCountryId(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(COUNTRY_ID, "");
    }
    public String getCountryName(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(COUNTRY_NAME, "");
    }
    public String getCountryPhoneCode(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(COUNTRY_PHONE_CODE, "");
    }
    public String getCurrencyId(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(CURRENCY_ID, "");
    }
    public String getCurrencyText(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(CURRENCY_TEXT, "");
    }
    public String getCurrencySign(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(CURRENCY_SIGN, "");
    }





    public Boolean isFirstTime() {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        Boolean value = sharedPreferences.getBoolean(FIRST_TIME, true);
        return value;
    }

    public void setFirstTime(Boolean status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_TIME, status);
        editor.apply();
    }

    public void setUserHashtags(String hashtags) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_HASHTAGS, hashtags);
        editor.apply();
    }
    public String getUserHashtags(){
        return mContext.getSharedPreferences(SHARED_PREF_NAME, 0).getString(USER_HASHTAGS, "");
    }



    /**
     * return userModel which hold all user data
     *
     * @return user model
     */
    public UserModel getUserDate() {
        UserModel userModel = new UserModel();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
        userModel.setName(sharedPreferences.getString(UserModel.KEY_USERNAME, ""));
        userModel.setPhone(sharedPreferences.getString(UserModel.KEY_PHONE, ""));
        userModel.setEmail(sharedPreferences.getString(UserModel.KEY_EMAIL, ""));
        userModel.setId(sharedPreferences.getString(UserModel.KEY_USER_ID, ""));
        userModel.setCover_image(sharedPreferences.getString(UserModel.KEY_USER_COVER, ""));
        userModel.setPhoto(sharedPreferences.getString(UserModel.KEY_USRE_IMAGE, ""));
        userModel.setApiToken(sharedPreferences.getString(UserModel.KEY_API_TOKEN, ""));
        userModel.setBio(sharedPreferences.getString(UserModel.KEY_USER_BIO, ""));
        userModel.setCountry(sharedPreferences.getString(UserModel.KEY_USER_COUNTRY,""));
        userModel.setCountryCode(sharedPreferences.getString(UserModel.KEY_USER_COUNTRY_CODE,""));
        return userModel;
    }

    /**
     * saving user data to be used in profile
     *
     * @param userModel is the model which hold all user data
     */

    public void setUserDate(UserModel userModel) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserModel.KEY_USER_ID, userModel.getId());
        editor.putString(UserModel.KEY_USERNAME, userModel.getName());
        editor.putString(UserModel.KEY_PHONE, userModel.getPhone());
        editor.putString(UserModel.KEY_EMAIL, userModel.getEmail());
        editor.putString(UserModel.KEY_USRE_IMAGE, userModel.getPhoto());
        editor.putString(UserModel.KEY_USER_COVER, userModel.getCover_image());
        editor.putString(UserModel.KEY_API_TOKEN, userModel.getApiToken());
        editor.putString(UserModel.KEY_USER_BIO, userModel.getBio());
        editor.putString(UserModel.KEY_USER_COUNTRY,userModel.getCountry());
        editor.putString(UserModel.KEY_USER_COUNTRY_CODE,userModel.getCountryCode());
        editor.apply();
    }

    public void setUserProfilePhoto(String profilePhoto){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserModel.KEY_USRE_IMAGE, profilePhoto);
        editor.apply();
    }
    public void setUserCoverPhoto(String coverPhoto){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserModel.KEY_USER_COVER, coverPhoto);
        editor.apply();
    }

    /**
     * this method is responsible for user logout and clearing cache
     */
    public void Logout() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        setFirstTime(false);
        LoginManager.getInstance().logOut();
    }
}
