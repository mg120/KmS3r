package com.tqnee.KamS3r.Utils;

/**
 * Created by ramzy on 4/19/2017.
 */

/**
 * this class contains all APIs Url
 */

public class Constants {
    public static String QUESTION_TYPE = "1";
    public static String ANSWER_TYPE = "2";
    public static String OFFER_TYPE = "3";
    public static String SOCIAL_REGISTER = "1";
    public static String NORMAL_REGISTER = "2";
    public static final String EGYPT_COUNTRY_CODE = "EG";
    public static final String KSA_COUNTRY_CODE = "SA";
    public static final String EGYPT_ID = "63";
    public static final String KSA_ID = "187";
    public static final int OFFER_STATUS_USED = 1;
    public static final int OFFER_STATUS_NEW = 0;
    public static final int OFFER_DISCUSSABLE = 1;
    public static final int OFFER_NOT_DISCUSSABLE = 0;

    public static boolean isStatesLoading = false;
    public static boolean isHashtag = false;


    public static String LOG_TAG = "keyResponse";
    public static String baseUrl = "https://kms3r.com/api/";
    public static String imagesBaseUrl = "https://kms3r.com/uploads/";
    public static String shareAskUrl = "https://kms3r.com/ask/";
    public static String shareAdUrl = "https://kms3r.com/ads_content/";
    public static String categoriesUrl = baseUrl + "categories";
    public static String asksUrl = baseUrl + "categorySite";
    public static String asksOffersUrl = baseUrl + "ask/getOffers";
    public static String trendAsksUrl = baseUrl + "ask/popular";
    public static String sendingMessageUrl = baseUrl + "addReply";
    public static String questionStatisticalUrl = baseUrl + "ask/getRates";
    public static String addToFavouriteUrl = baseUrl + "ask/addFavourite";
    public static String reportItemUrl = baseUrl + "ask/addReport";
    public static String notificationsUrl = baseUrl + "notifications";
    public static String askDetailsUrl = baseUrl + "ask/askDetails";
    public static String answerCommentsUrl = baseUrl + "ask/AnswerComments";
    public static String askAnswersUrl = baseUrl + "ask/getAnswers";
    public static String addCommentUrl = baseUrl + "ask/addComment";
    public static String allMessagesUrl = baseUrl + "profile/allMessages";
    public static String forgotPasswordUrl = baseUrl + "password/email";
    public static String internalMessageUrl = baseUrl + "message/thread";
    public static String userLoginUrl = baseUrl + "login";
    public static String loadCountUrl = baseUrl + "unseen";
    public static String createAccountUrl = baseUrl + "register";
    public static String completeRegisterUrl = baseUrl + "regComplete";
    public static String loadAllOffers = baseUrl + "ads/getAskOffers";
    public static String searchUrl = baseUrl + "ask/getResult";
    public static String profileQuestionUrl = baseUrl + "profile/asks";
    public static String profileOffersUrl = baseUrl + "profile/offers";
    public static String userProfileDataUrl = baseUrl + "profile/getUser";
    public static String favQuestionUrl = baseUrl + "profile/favouriteAsks";
    public static String favOffersUrl = baseUrl + "profile/favouriteOffers";
    public static String favAnswersUrl = baseUrl + "profile/favouriteAnswers";
    public static String addQuestionUrl = baseUrl + "addAsk";
    public static String getAllCurrencies = baseUrl + "currencies";
    public static String addAnswer = baseUrl + "ask/addAnswer";
    public static String countriesUrl = baseUrl + "countries";
    public static String uploadImageToAd = baseUrl + "files/uploadImage";
    public static String deleteImageFromAd = baseUrl + "files/removeFile";
    public static String postAd = baseUrl + "ad/addAd";
    public static String getAd = baseUrl + "ad/getAd";
    public static String getCountry = baseUrl + "getCountry";
    public static String getStates = baseUrl + "states";
    public static String userAds = baseUrl + "profile/ads";
    public static String favorite_ad_item_type = "3";
    public static String uploadProfileOrCoverPhoto = baseUrl + "profile/editImage";
    public static String userImageCoverType = "2";
    public static String userImageProfileType = "1";
    public static String editProfileUrl = baseUrl + "profile/edit";
    public static String deleteUserPhoto = baseUrl + "profile/removeUserImage";
    public static String hashtagQuestions = baseUrl + "profile/hashAsks";
    public static String hashtagOffers = baseUrl + "profile/hashAds";
    public static String resetPassword = baseUrl + "profile/password";

    public static String getQuestionDataUrl = baseUrl + "ask/askForEdit";
    public static String editAd_url = baseUrl + "ad/edit";
    public static String delete_url = baseUrl + "deleteOffer";

    public static String askDelete_url = baseUrl + "ask/hide-ask";

    public static String offers_searchUrl = baseUrl + "ad/getResult";
    public static String remove_image = baseUrl + "ad/removeFile";
    public static String upload_image = baseUrl + "ad/uploadFile";
    public static String unseen_Data = baseUrl + "unseen";

    public static String ContactUsEmail = "support@kmsr.com";
}
