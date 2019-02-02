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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class EditQuestion extends AppCompatActivity implements ICategoryItemClicked, ICountrySelected {

    final int ATTACHMENT_REQUEST_CODE = 333;
    Context mContext;
    Activity activity;

    @BindView(R.id.btn_edit_image)
    Button btn_attachment;
    @BindView(R.id.edit_iv_attachment)
    ImageView iv_attachment;

    @BindView(R.id.edit_et_quetion_title)
    EditText et_quetion_title;
    @BindView(R.id.edit_tv_country)
    TextView tv_country;
    @BindView(R.id.edit_tv_department)
    TextView tv_department;
    @BindView(R.id.edit_lay_department)
    RelativeLayout lay_department;
    @BindView(R.id.btn_edit_question)
    Button btn_edit_question;
    @BindView(R.id.edit_ques_progress_id)
    ProgressBar progressBar;

    SharedPrefManager sharedPrefManager;
    String ques_id;
    SelectCategoriesCustomDialog mSelectCategoriesCustomDialog;
    private CountriesCustomDialog mCountriesCustomDialog;
    Intent intent;
    String category_id, countryCode = "";
    private String stateId = "";
    String imageBase64 = "";
    ArrayList<CountryModel> countries_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        ButterKnife.bind(this);
        mContext = this;
        activity = this;
        sharedPrefManager = new SharedPrefManager(mContext);
        mCountriesCustomDialog = new CountriesCustomDialog(mContext, EditQuestion.this);
        mSelectCategoriesCustomDialog = new SelectCategoriesCustomDialog(mContext, EditQuestion.this);
        if (getIntent().getExtras() != null) {
            ques_id = getIntent().getExtras().getString("ques_id");
            getQuesData(ques_id);
        }
        RelativeLayout stateLayout = (RelativeLayout) findViewById(R.id.edit_lay_country);
        stateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.isStatesLoading = true;
                mCountriesCustomDialog.show();
            }
        });
        lay_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectCategoriesCustomDialog.show();
            }
        });

    }

    private void getQuesData(final String ques_id) {
        StringRequest ques_result = new StringRequest(Request.Method.POST, Constants.getQuestionDataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("response");
                    if (success.equals("true")) {
                        JSONObject ask_obj = jsonObject.getJSONObject("ask");
                        category_id = ask_obj.getString("ask_category");
                        countryCode = ask_obj.getString("country_code");
                        stateId = ask_obj.getString("state_id");

                        et_quetion_title.setText(ask_obj.getString("ask"));
                        Glide.with(mContext).load(Constants.imagesBaseUrl + ask_obj.getString("ask_photo")).into(iv_attachment);

                        for (int i = 0; i < HomeActivity.categoriesList.size(); i++) {
                            if (category_id.equals(HomeActivity.categoriesList.get(i).getCategory_id()))
                                tv_department.setText(HomeActivity.categoriesList.get(i).getCategory_name());
                        }

                        loadCuntries(countryCode, stateId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ask_id", ques_id);
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        ques_result.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(ques_result);
    }


    private void loadCuntries(final String countryCode, final String state_id) {
        countries_List = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.getStates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response : " + response);
                        try {
                            JSONArray statesArrays = new JSONObject(response).getJSONArray("states").getJSONObject(0).getJSONArray("get_states");
                            for (int i = 0; i < statesArrays.length(); i++) {
                                JSONObject stateObject = statesArrays.getJSONObject(i);

                                CountryModel countryModel = new CountryModel();
                                countryModel.setCountry_id(String.valueOf(stateObject.optInt("id")));
                                countryModel.setCountry_name(stateObject.optString("ar_name"));
                                countries_List.add(countryModel);
                            }

                            for (int i = 0; i < countries_List.size(); i++) {
                                if (state_id.equals(countries_List.get(i).getCountry_id()))
                                    tv_country.setText(countries_List.get(i).getCountry_name());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_code", countryCode);
                System.out.println("params : " + params);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @OnClick(R.id.edit_ques_back)
    void back() {
        finish();
    }

    @OnClick(R.id.btn_edit_image)
    void addAttachment() {
        if (checkPermission()) {
            requestPermission(ATTACHMENT_REQUEST_CODE);
        } else {
            startCameraIntent(ATTACHMENT_REQUEST_CODE);
        }
    }

    @OnClick(R.id.edit_lay_attachment)
    void addCategory() {
        mSelectCategoriesCustomDialog = new SelectCategoriesCustomDialog(mContext, this);
        mSelectCategoriesCustomDialog.show();
    }

    @OnClick(R.id.btn_edit_question)
    void addQuestion() {
        if (category_id.equals("")) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_choose_category), R.color.colorError);
        } else if (et_quetion_title.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_enter_title), R.color.colorError);
        } else if (stateId.equals("")) {
            Utils.showSnackBar(mContext, tv_country, getString(R.string.you_should_choose_state), R.color.colorError);
        } else {
            editQuestion(Constants.addQuestionUrl);
        }
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
    public void onCategoryItemClicked(CategoriesModel categoriesModel, Boolean isCategorySelected) {
        mSelectCategoriesCustomDialog.dismiss();
        tv_department.setText(categoriesModel.getCategory_name());
        category_id = categoriesModel.getCategory_id();
    }

    @Override
    public void onCountrySelected(CountryModel mCountryModel) {
        mCountriesCustomDialog.dismiss();
        stateId = mCountryModel.getCountry_id();
        mCountryModel.getCountry_code();
        tv_country.setText(mCountryModel.getCountry_name());
        Constants.isStatesLoading = false;
    }

    private void editQuestion(String serviceURL) {
        progressBar.setVisibility(View.VISIBLE);
        btn_edit_question.setEnabled(false);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response")) {
                                if (jsonObject.optString("response").equals("true")) {
                                    Toasty.success(mContext, getString(R.string.edited_successfully), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                        progressBar.setVisibility(View.GONE);
                        btn_edit_question.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ask_id", ques_id);
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                params.put("ask_title", et_quetion_title.getText().toString().trim());
                params.put("country", tv_country.getText().toString().trim());
                params.put("ask_category", category_id);
                params.put("photo", imageBase64);
                params.put("state_id", stateId);
                System.out.println("params: " + params);
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
}
