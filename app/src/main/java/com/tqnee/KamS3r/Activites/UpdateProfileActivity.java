package com.tqnee.KamS3r.Activites;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.ICountrySelected;
import com.tqnee.KamS3r.Model.CountryModel;
import com.tqnee.KamS3r.Model.UserModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.CountriesCustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateProfileActivity extends ParentActivity implements ICountrySelected {
    Context mContext;
    SharedPrefManager mSharedPrefManager;

    @BindView(R.id.tv_choose_country)
    TextView tv_choose_country;
    @BindView(R.id.et_bio)
    EditText et_bio;
    @BindView(R.id.phone_code)
    TextView phone_code;
    @BindView(R.id.et_user_phone)
    EditText et_user_phone;
    @BindView(R.id.iv_user_image)
    ImageView iv_user_image;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.et_full_name)
    TextView fullNameEditText;
    @BindView(R.id.et_hashtags)
    TextView hashtagsEditText;
    @BindView(R.id.contact_us_text_view)
    TextView contact_us_text_view;

    @BindView(R.id.password_layout)
    LinearLayout passwordLayout;
    @BindView(R.id.btn_update_password)
    Button btn_update_password;
    @BindView(R.id.et_user_old_password)
    EditText et_user_old_password;
    @BindView(R.id.et_user_new_password)
    EditText et_user_new_password;
    @BindView(R.id.et_confirm_password)
    EditText et_confirm_password;
    @BindView(R.id.spinner_countries)
    Spinner countriesSpinner;
    @BindView(R.id.log_out_text_view)
    TextView logOutTextView;

    ArrayList<String> countriesList = new ArrayList<String>();

    String country_id;
    String country_iso;
    CountriesCustomDialog mCountriesCustomDialog;
    final int PROFILE_PHOTO_REQUEST_CODE = 222;
    private String profileImageBase64 = "";
    String coverImageBase64 = "";
    //    CustomLoadingDialog mCustomLoadingDialog;
    private int COVER_PHOTO_REQUEST_CODE = 3;
    private ImageView coverImageView;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.update_profile));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);


        phone_code.setText(mSharedPrefManager.getCountryPhoneCode());
        tv_choose_country.setText(mSharedPrefManager.getCountryName());

        fullNameEditText.setText(mSharedPrefManager.getUserDate().getName());
        et_user_phone.setText(mSharedPrefManager.getUserDate().getPhone());
        et_bio.setText(mSharedPrefManager.getUserDate().getBio());

        hashtagsEditText.setText(mSharedPrefManager.getUserHashtags());

        coverImageView = (ImageView) findViewById(R.id.iv_user_cover);

        Glide.with(mContext).load(Constants.imagesBaseUrl + mSharedPrefManager.getUserDate().getCover_image())
                .error(R.color.colorPrimary)
                .placeholder(R.color.colorPrimary)
                .into(coverImageView);

        Glide.with(mContext).load(Constants.imagesBaseUrl + mSharedPrefManager.getUserDate().getPhoto())
//                .error(R.color.colorDivider)
//                .placeholder(R.color.colorDivider)
                .into(iv_user_image);


        et_user_old_password.setTransformationMethod(new PasswordTransformationMethod());
        et_user_new_password.setTransformationMethod(new PasswordTransformationMethod());
        et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());

        if (mSharedPrefManager.isSocialRegister()) {
            passwordLayout.setVisibility(View.GONE);
        }

        countriesList.add("مصر");
        countriesList.add("السعودية");
        countriesSpinner.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, countriesList));
        if (mSharedPrefManager.getCountryCode().equals("EG"))
            countriesSpinner.setSelection(0);
        else
            countriesSpinner.setSelection(1);
        countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    mSharedPrefManager.setCountryCode("EG");
                else
                    mSharedPrefManager.setCountryCode("SA");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_update_profile;
    }

    @Override
    protected boolean isEnableToolbar() {
        return true;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean isEnableBack() {
        return true;
    }

    @Override
    protected boolean hideInputType() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }


    @OnClick(R.id.tv_choose_country)
    void onCountryClicked() {
        mCountriesCustomDialog = new CountriesCustomDialog(mContext, this);
        mCountriesCustomDialog.show();
    }

    @OnClick(R.id.btn_complete_register)
    void completeRegister() {
        completeUserRegister(Constants.editProfileUrl);
    }

    @OnClick(R.id.btn_update_password)
    void updatePassword() {
        updateUserPassword();
    }

    @OnClick(R.id.log_out_text_view)
    void logOut() {
        mSharedPrefManager.Logout();
        Intent mIntent = new Intent(mContext, SelectCountryActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(mIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    @OnClick(R.id.contact_us_text_view)
    void onContactUsClicked() {
        Utils.feedbackButtonListener(mContext);
    }

    @Override
    public void onCountrySelected(CountryModel mCountryModel) {
        mCountriesCustomDialog.dismiss();
        country_id = mCountryModel.getCountry_id();
        country_iso = mCountryModel.getCountry_iso();
        phone_code.setText(mCountryModel.getCountry_code());
        tv_choose_country.setText(mCountryModel.getCountry_name());
    }

    @OnClick(R.id.layout_add_images)
    void addImage() {
        showPhotoDialog(PROFILE_PHOTO_REQUEST_CODE);
    }

    @OnClick(R.id.cover_layout)
    void updateCover() {
        showPhotoDialog(COVER_PHOTO_REQUEST_CODE);
    }

    private void showPhotoDialog(final int photoRequestCode) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_or_delete_photo);
        dialog.findViewById(R.id.update_photo_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    requestPermission(photoRequestCode);
                } else {
                    startCameraIntent(photoRequestCode);
                }
            }
        });
        dialog.findViewById(R.id.delete_photo_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoRequestCode == COVER_PHOTO_REQUEST_CODE) { //Cover photo selected
                    coverImageView.setImageDrawable(null);
                    coverImageBase64 = "";
                    deleteImage(Constants.userImageCoverType);
                } else { //Profile photo selected
                    iv_user_image.setImageDrawable(null);
                    iv_user_image.setBackgroundColor(getResources().getColor(R.color.colorDivider));
                    profileImageBase64 = "";
                    deleteImage(Constants.userImageProfileType);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private void requestPermission(int REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    private void startCameraIntent(int REQUEST_CODE) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PROFILE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = decodeImageIntoBitmap(data);
                iv_user_image.setVisibility(View.VISIBLE);
                iv_user_image.setImageBitmap(bitmap);
                profileImageBase64 = Utils.getEncoded64ImageStringFromBitmap(bitmap);
                uploadImage(Constants.userImageProfileType, profileImageBase64);
                dialog.dismiss();
            } else if (requestCode == COVER_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Bitmap coverBitmap = decodeImageIntoBitmap(data);
                coverImageBase64 = Utils.getEncoded64ImageStringFromBitmap(coverBitmap);
                coverImageView.setVisibility(View.VISIBLE);
                coverImageView.setImageBitmap(coverBitmap);
                uploadImage(Constants.userImageCoverType, coverImageBase64);
                dialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("SIZE", e.toString());
            Utils.showSnackBar(mContext, layout_content, getString(R.string.error_in_activity_result), R.color.colorError);

        }

    }

    private void uploadImage(final String type, final String imageBase64) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.uploadProfileOrCoverPhoto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "Upload : ", response);
                        try {
                            JSONObject photoObject = new JSONObject(response);
                            if (photoObject.getString("response").equals("true")) {
                                Utils.showSnackBar(mContext, layout_content, "تم تحديث الصورة بنجاح", R.color.colorCorrect);
                                if (type.equals(Constants.userImageProfileType)) {
                                    mSharedPrefManager.setUserProfilePhoto(photoObject.getString("image"));
                                } else
                                    mSharedPrefManager.setUserCoverPhoto(photoObject.getString("image"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("photo", imageBase64);
                params.put("type", type);

                System.out.println("Parameters : " + params);
                return params;
            }
        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private void deleteImage(final String type) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.deleteUserPhoto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "Delete photo : ", response);
                        try {
                            JSONObject photoObject = new JSONObject(response);
                            if (photoObject.getString("response").equals("true")) {
                                if (type.equals(Constants.userImageProfileType)) {
                                    mSharedPrefManager.setUserProfilePhoto("");
                                } else
                                    mSharedPrefManager.setUserCoverPhoto("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("type", type);

                System.out.println("Parameters : " + params);
                return params;
            }
        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private Bitmap decodeImageIntoBitmap(Intent data) {
        Uri pickedImage = data.getData();
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(filePath[0]));
        cursor.close();
        File file = new File(path);
        Bitmap bitmap = Utils.decodeAndResizeFile(file);
        return bitmap;
    }

    private void completeUserRegister(String serviceUrl) {
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
//        mCustomLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Edit Profile response : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean responseMessage = jsonObject.optBoolean("response");
                            if (responseMessage) {
                                if (jsonObject.has("user")) {
                                    Utils.printLog(Constants.LOG_TAG + "(Edit Profile)", response);
                                    JSONObject userObject = jsonObject.optJSONObject("user");
                                    UserModel userModel = new UserModel();
                                    userModel.setName(userObject.optString(UserModel.KEY_USERNAME));
                                    userModel.setEmail(userObject.optString(UserModel.KEY_EMAIL));
                                    userModel.setId(userObject.optString(UserModel.KEY_USER_ID));
                                    userModel.setPhone(userObject.optString(UserModel.KEY_PHONE));
                                    userModel.setPhoto(userObject.optString(UserModel.KEY_USRE_IMAGE));
                                    userModel.setCover_image(userObject.optString(UserModel.KEY_USER_COVER));
                                    userModel.setApiToken(userObject.optString(UserModel.KEY_API_TOKEN));
                                    userModel.setBio(userObject.optString(UserModel.KEY_USER_BIO));
                                    if (!userObject.isNull("get_mobile_country"))
                                        userModel.setCountry(userObject.getJSONObject("get_mobile_country").getString("ar_name"));
                                    userModel.setCountryCode(userObject.getString("country_code"));
                                    mSharedPrefManager.setLoginStatus(true);
                                    mSharedPrefManager.setUserDate(userModel);
                                    Utils.showSnackBar(mContext, layout_content, getString(R.string.settings_saved_successfully), R.color.colorCorrect);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        mCustomLoadingDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("fullname", fullNameEditText.getText().toString());
                params.put("about", et_bio.getText().toString());
                params.put("mobile", et_user_phone.getText().toString());
                params.put("phone_code", phone_code.getText().toString());
                String userHashtags = hashtagsEditText.getText().toString().trim().replaceAll("\\p{Punct}|\\d", ",");
                params.put("hashtags", userHashtags);
                mSharedPrefManager.setUserHashtags(userHashtags);
                System.out.println("Parameters : " + params);
                return params;
            }
        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    public void updateUserPassword() {
        if (!et_user_new_password.getText().toString().equals(et_confirm_password.getText().toString())) {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.password_not_matched), R.color.colorError);
            return;
        }
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
//        mCustomLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.resetPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(Edit Password)", response);
//                        mCustomLoadingDialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            if (responseObject.getString("response").equals("false"))
                                Utils.showSnackBar(mContext, layout_content, getString(R.string.user_old_password_incorrect), R.color.colorError);
                            else
                                Utils.showSnackBar(mContext, layout_content, getString(R.string.password_changed_correctly), R.color.colorCorrect);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("old_password", et_user_old_password.getText().toString());
                params.put("password", et_user_new_password.getText().toString());

                System.out.println("Parameters : " + params);
                return params;
            }
        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

}
