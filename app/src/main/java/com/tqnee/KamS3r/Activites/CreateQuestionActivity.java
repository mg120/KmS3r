package com.tqnee.KamS3r.Activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.karan.churi.PermissionManager.PermissionManager;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Interfaces.ICountrySelected;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.Model.CountryModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.CountriesCustomDialog;
import com.tqnee.KamS3r.Widgets.SelectCategoriesCustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateQuestionActivity extends ParentActivity implements ICategoryItemClicked,ICountrySelected {

    final int ATTACHMENT_REQUEST_CODE = 222;
    Context mContext;
    @BindView(R.id.btn_attachment)
    Button btn_attachment;
    @BindView(R.id.iv_attachment)
    ImageView iv_attachment;
    Intent intent;
    String category_id = "";
    String country_id = "187";
    Activity activity;
    SharedPrefManager sharedPrefManager;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.tv_login_status)
    TextView tv_login_status;
    String imageBase64 = "";
    @BindView(R.id.et_quetion_title)
    EditText et_quetion_title;
    @BindView(R.id.tv_country)
    TextView tv_country;
    @BindView(R.id.tv_department)
    TextView tv_department;
    @BindView(R.id.lay_department)
    RelativeLayout lay_department;
    @BindView(R.id.create_ques_progress_id)
    ProgressBar progressBar;
//    CustomLoadingDialog customLoadingDialog;

    @BindView(R.id.btn_add_question)
    Button btn_add_question;
    SelectCategoriesCustomDialog mSelectCategoriesCustomDialog;
    private CountriesCustomDialog mCountriesCustomDialog;
    private String stateId = "";
    PermissionManager permissionManager;


    @Override
    protected void initializeComponents() {
        mContext = this;
        activity = this;
        setToolbarTitle(getString(R.string.create_ask));
//        customLoadingDialog = new CustomLoadingDialog(mContext);

        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        sharedPrefManager = new SharedPrefManager(mContext);
        if (sharedPrefManager.getLoginStatus()) {
            layout_content.setVisibility(View.VISIBLE);
            tv_login_status.setVisibility(View.GONE);
        } else {
            layout_content.setVisibility(View.GONE);
            tv_login_status.setVisibility(View.VISIBLE);
        }

        RelativeLayout stateLayout = (RelativeLayout) findViewById(R.id.lay_country);
        stateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.isStatesLoading = true;
                mCountriesCustomDialog = new CountriesCustomDialog(mContext, CreateQuestionActivity.this);
                mCountriesCustomDialog.show();
            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_create_question;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //To get Granted Permission and Denied Permission
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;
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
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = decodeImageIntoBitmap(data);
                iv_attachment.setVisibility(View.VISIBLE);
                iv_attachment.setImageBitmap(bitmap);
                imageBase64 = Utils.getEncoded64ImageStringFromBitmap(bitmap);
                //   UploadImage(Constants.Upload, Constants.BASE_64 + imageBase64, PROFILE_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            Log.e("SIZE", e.toString());
            Utils.showSnackBar(mContext, iv_attachment, getString(R.string.error_in_activity_result), R.color.colorError);

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

    @OnClick(R.id.btn_add_question)
    void addQuestion() {
        if (category_id.equals("")) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_choose_category), R.color.colorError);
        } else if (et_quetion_title.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_enter_title), R.color.colorError);
        } else if (stateId.equals("")) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_choose_state), R.color.colorError);
        } else {
            createQuestion(Constants.addQuestionUrl);
        }
    }

    @OnClick(R.id.btn_attachment)
    void addAttachment() {
        if (checkPermission()) {
            requestPermission(ATTACHMENT_REQUEST_CODE);
        } else {
            startCameraIntent(ATTACHMENT_REQUEST_CODE);
        }
    }

    @OnClick(R.id.lay_department)
    void addCategory() {
        mSelectCategoriesCustomDialog = new SelectCategoriesCustomDialog(mContext, this);
        mSelectCategoriesCustomDialog.show();
    }


    private void createQuestion(String serviceURL) {
//        customLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressBar.setVisibility(View.GONE);
                            if (jsonObject.has("response")) {
                                if (jsonObject.optString("response").equals("true")) {
                                    Toast.makeText(mContext, getString(R.string.added_successfully), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        customLoadingDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        customLoadingDialog.hide();
                        progressBar.setVisibility(View.GONE);
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, tv_country, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, tv_country, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, tv_country, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, tv_country, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                params.put("ask_title", et_quetion_title.getText().toString().trim());
                params.put("country", mSharedPrefManager.getCountryCode());
                params.put("ask_category", category_id);
                params.put("photo", imageBase64);
                params.put("state_id", stateId);

                return params;
            }
        };


        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }


    @Override
    public void onCategoryItemClicked(CategoriesModel categoriesModel, Boolean isCategorySelected) {
        mSelectCategoriesCustomDialog.dismiss();
        tv_department.setText(categoriesModel.getCategory_name());
        category_id = categoriesModel.getCategory_id();
    }

    @Override
    public void onCountrySelected(CountryModel mCountryModel) {
        mCountriesCustomDialog.dismiss();
        stateId = mCountryModel.getCountry_id();
        tv_country.setText(mCountryModel.getCountry_name());
        Constants.isStatesLoading = false;
    }
}
