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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.tqnee.KamS3r.Widgets.CustomLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class CompleteRegister extends ParentActivity implements ICountrySelected {
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
    @BindView(R.id.et_hashtags)
    EditText hashtagsEditText;
    @BindView(R.id.complete_register_id)
    ProgressBar progressBar;


    String country_id;
    String country_iso;
    CountriesCustomDialog mCountriesCustomDialog;
    final int PROFILE_PHOTO_REQUEST_CODE = 222;
    private String profileImageBase64 = "";
    String coverImageBase64 = "";
    CustomLoadingDialog mCustomLoadingDialog;
    private int COVER_PHOTO_REQUEST_CODE = 3;
    private ImageView coverImageView;
    private Dialog dialog;


    @Override
    protected void initializeComponents() {
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);

        phone_code.setText(mSharedPrefManager.getCountryPhoneCode());
        tv_choose_country.setText(mSharedPrefManager.getCountryName());

        coverImageView = (ImageView) findViewById(R.id.iv_user_cover);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_complete_register;
    }

    @Override
    protected boolean isEnableToolbar() {
        return false;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean isEnableBack() {
        return false;
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
        completeUserRegister(Constants.completeRegisterUrl);
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
                } else { //Profile photo selected
                    iv_user_image.setImageDrawable(null);
                    iv_user_image.setBackgroundColor(getResources().getColor(R.color.colorDivider));
                    profileImageBase64 = "";
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
                dialog.dismiss();
            } else if (requestCode == COVER_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Bitmap coverBitmap = decodeImageIntoBitmap(data);
                coverImageBase64 = Utils.getEncoded64ImageStringFromBitmap(coverBitmap);
                coverImageView.setImageBitmap(coverBitmap);
                dialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("SIZE", e.toString());
            Utils.showSnackBar(mContext, layout_content, getString(R.string.error_in_activity_result), R.color.colorError);

        }

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
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(complete)", response);
                        progressBar.setVisibility(View.GONE);
                        parseCompleteRegisterResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
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
                params.put("user_id", mSharedPrefManager.getUserDate().getId());
                params.put("photo", profileImageBase64);
                params.put("about", et_bio.getText().toString());
                params.put("mobile", et_user_phone.getText().toString());
                params.put("cover", coverImageBase64);
                params.put("phone_code", phone_code.getText().toString());
                String userHashtags = hashtagsEditText.getText().toString().trim().replaceAll("[^a-zA-Z ]", ",");
                mSharedPrefManager.setUserHashtags(userHashtags);
                params.put("hashtags", userHashtags);
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

    private void parseCompleteRegisterResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Boolean responseMessage = jsonObject.optBoolean("response");
            if (responseMessage) {
                if (jsonObject.has("user")) {
                    JSONObject userObject = jsonObject.optJSONObject("user");
                    UserModel userModel = new UserModel();
                    userModel.setName(mSharedPrefManager.getUserDate().getName());
                    userModel.setEmail(mSharedPrefManager.getUserDate().getEmail());
                    userModel.setId(mSharedPrefManager.getUserDate().getId());
                    userModel.setPhone(et_user_phone.getText().toString());
                    userModel.setPhoto("");
                    userModel.setCover_image("");
                    userModel.setApiToken(mSharedPrefManager.getUserDate().getApiToken());
                    userModel.setBio(et_bio.getText().toString());
                    mSharedPrefManager.setLoginStatus(true);
                    mSharedPrefManager.setUserDate(userModel);
                    Intent intent = new Intent(mContext, SelectLoginType.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
