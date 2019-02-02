package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.karan.churi.PermissionManager.PermissionManager;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Interfaces.ICountrySelected;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.Model.CountryModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.CategoriesCustomDialog;
import com.tqnee.KamS3r.Widgets.CountriesCustomDialog;
import com.tqnee.KamS3r.Widgets.CustomLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class EditAdActivity extends AppCompatActivity implements ICategoryItemClicked, ICountrySelected {
    private Context mContext;
    private CategoriesCustomDialog mCategoriesCustomDialog;
    private TextView categoryNameTextView;
    private ImageView categoryIconImageView;
    private EditText adNameEditText;
    private EditText adDetailsEditText;
    private EditText priceEditText;
    private CheckBox isDiscussableCheckbox;
    private RadioGroup usedOrNewRadioGroup;
    private Spinner statesSpinner;
    private EditText mobileEditText;
    private EditText emailEditText;
    private ImageView firstPhotoButton;
    private ImageView secondPhotoButton;
    private ImageView thirdPhotoButton;
    private ImageView fourthPhotoButton;
    private ImageView fifthPhotoButton;
    private ProgressBar progressBar;
    private LinearLayout edit_ad_layot;
    String firstPhotoName = "";
    String secondPhotoName = "";
    String thirdPhotoName = "";
    String fourthPhotoName = "";
    String fifthPhotoName = "";
    private int PICK_IMAGE = 10;
    int clickedPhoto = 1;
    private String imageBase64 = "";
    private ImageButton deleteFirstPhoto;
    private ImageButton deleteSecondPhoto;
    private ImageButton deleteThirdPhoto;
    private ImageButton deleteFourthPhoto;
    private ImageButton deleteFifthPhoto;
    CustomLoadingDialog customLoadingDialog;
    private Button editAdButton;
    private RadioButton newAdRadioButton;
    private SharedPrefManager sharedPrefManager;
    private String selectedCategoryId = "";
    private ArrayList<String> statesList;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> statesIDs;
    private CountriesCustomDialog mCountriesCustomDialog;
    private String stateId = "";
    private TextView statesTextView, edit_back, currency_text_view;
    String adId = "";
    int discuss, ad_state;
    ArrayList<String> images_Urls = new ArrayList<>();
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
        mContext = this;
        customLoadingDialog = new CustomLoadingDialog(mContext);
        sharedPrefManager = new SharedPrefManager(mContext);
        RelativeLayout chooseCategoryButton = (RelativeLayout) findViewById(R.id.layout_choose_category);
        categoryNameTextView = (TextView) findViewById(R.id.tv_category_name);
        categoryIconImageView = (ImageView) findViewById(R.id.iv_category_icon);
        edit_ad_layot = (LinearLayout) findViewById(R.id.edit_ad_layot_id);

        if (getIntent().hasExtra("ad_id")) {
            adId = getIntent().getStringExtra("ad_id");
            getAdData(adId);
        }
        //Category

        if (adId.equals("")) { //User is adding offer to specific question so no need for category
            chooseCategoryButton.setVisibility(View.GONE);
        } else {
            chooseCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCategoriesCustomDialog = new CategoriesCustomDialog(mContext, EditAdActivity.this);
                    mCategoriesCustomDialog.show();
                }
            });
        }

        adNameEditText = (EditText) findViewById(R.id.edit_ad_name_ed_id);
        adDetailsEditText = (EditText) findViewById(R.id.edit_ad_details_ed_id);
        currency_text_view = (TextView) findViewById(R.id.currency_text_view);
        priceEditText = (EditText) findViewById(R.id.edit_price_ed_id);
        isDiscussableCheckbox = (CheckBox) findViewById(R.id.edit_is_discussable_checkbox_id);
        usedOrNewRadioGroup = (RadioGroup) findViewById(R.id.edit_new_or_used_radio_group_id);
        newAdRadioButton = (RadioButton) findViewById(R.id.edit_new_ad_radio_button_id);
        statesSpinner = (Spinner) findViewById(R.id.country_spinner);
        edit_back = (TextView) findViewById(R.id.edit_back);
        progressBar = (ProgressBar) findViewById(R.id.edit_ad_progress_id);

        edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        statesList = new ArrayList<String>();
        statesList.add("اختر مدينة");
        statesIDs = new ArrayList<String>();
        spinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, statesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statesSpinner.setAdapter(spinnerAdapter);

        statesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    getCountryStates(mSharedPrefManager.getCountryId());
                }
                return true;
            }
        });
        statesTextView = (TextView) findViewById(R.id.edit_state_text_view_id);
        statesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.isStatesLoading = true;
                mCountriesCustomDialog = new CountriesCustomDialog(mContext, EditAdActivity.this);
                mCountriesCustomDialog.show();
            }
        });

        mobileEditText = (EditText) findViewById(R.id.edit_mobile_edit_text_id);
        emailEditText = (EditText) findViewById(R.id.edit_email_edit_text_id);

        firstPhotoButton = (ImageView) findViewById(R.id.first_photo_button);
        secondPhotoButton = (ImageView) findViewById(R.id.second_photo_button);
        thirdPhotoButton = (ImageView) findViewById(R.id.third_photo_button);
        fourthPhotoButton = (ImageView) findViewById(R.id.fourth_photo_button);
        fifthPhotoButton = (ImageView) findViewById(R.id.fifth_photo_button);

        editAdButton = (Button) findViewById(R.id.edit_ad_button_id);

        firstPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPhoto = 1;
                getImageFromGalery();
            }
        });
        secondPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPhoto = 2;
                getImageFromGalery();
            }
        });
        thirdPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPhoto = 3;
                getImageFromGalery();
            }
        });
        fourthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPhoto = 4;
                getImageFromGalery();
            }
        });
        fifthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPhoto = 5;
                getImageFromGalery();
            }
        });

        deleteFirstPhoto = (ImageButton) findViewById(R.id.delete_first_photo_image_button);
        deleteSecondPhoto = (ImageButton) findViewById(R.id.delete_second_photo_image_button);
        deleteThirdPhoto = (ImageButton) findViewById(R.id.delete_third_photo_image_button);
        deleteFourthPhoto = (ImageButton) findViewById(R.id.delete_fourth_photo_image_button);
        deleteFifthPhoto = (ImageButton) findViewById(R.id.delete_fifth_photo_image_button);

        deleteFirstPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!firstPhotoName.equals(""))
                deleteImage(firstPhotoButton, deleteFirstPhoto, firstPhotoName);
            }
        });
        deleteSecondPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!secondPhotoName.equals(""))
                deleteImage(secondPhotoButton, deleteSecondPhoto, secondPhotoName);
            }
        });
        deleteThirdPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!thirdPhotoButton.equals(""))
                deleteImage(thirdPhotoButton, deleteThirdPhoto, thirdPhotoName);
            }
        });
        deleteFourthPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!fourthPhotoName.equals(""))
                deleteImage(fourthPhotoButton, deleteFourthPhoto, fourthPhotoName);
            }
        });
        deleteFifthPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!fifthPhotoName.equals(""))
                deleteImage(fifthPhotoButton, deleteFifthPhoto, fifthPhotoName);
            }
        });

        editAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> photosList = new ArrayList<String>();
                photosList.clear();
                if (!firstPhotoName.equals(""))
                    photosList.add(firstPhotoName);
                if (!secondPhotoName.equals(""))
                    photosList.add(secondPhotoName);
                if (!thirdPhotoButton.equals(""))
                    photosList.add(thirdPhotoName);
                if (!fourthPhotoName.equals(""))
                    photosList.add(fourthPhotoName);
                String adPhotos = ("" + photosList).replace("[", "").replace("]", "").replace(" ", "");
                if (adPhotos.startsWith(",")) {
                    adPhotos = adPhotos.substring(1);
                }
                if (adPhotos.endsWith(",")) {
                    adPhotos = adPhotos.substring(0, adPhotos.length() - 1);
                }
                System.out.println("photos : " + adPhotos);

                if (categoryNameTextView.getText().toString().equals("القسم")) {
                    Utils.showSnackBar(mContext, categoryNameTextView, getString(R.string.you_should_choose_category), R.color.colorError);
                } else if (TextUtils.isEmpty(adNameEditText.getText().toString().trim())) {
                    Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_enter_ad_title), R.color.colorError);
                } else if (TextUtils.isEmpty(adDetailsEditText.getText().toString().trim())) {
                    Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_enter_ad_description), R.color.colorError);
                } else if (stateId.equals("")) {
                    Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_choose_state), R.color.colorError);
                } else if (photosList.size() == 0 && ("".equals(photosList.get(0)))) {
                    Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_add_main_photo), R.color.colorError);
                } else {
                    editAd(adId);
                }
            }
        });
    }

    private void editAd(final String adId) {
        progressBar.setVisibility(View.VISIBLE);
        editAdButton.setEnabled(false);
        StringRequest edit_request = new StringRequest(Request.Method.POST, Constants.editAd_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                editAdButton.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("offer_id", adId);
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                params.put("offer_category", selectedCategoryId);
                params.put("offer_price", priceEditText.getText().toString().trim());
                params.put("offer_currency", currency_text_view.getText().toString().trim());
                params.put("offer_email", emailEditText.getText().toString().trim());
                params.put("offer_phone", mobileEditText.getText().toString().trim());
                params.put("offer_status", ad_state + "");
                params.put("discuss", discuss + "");
                params.put("country", statesTextView.getText().toString());
                params.put("state_id", stateId);
                params.put("offer_title", adNameEditText.getText().toString().trim());
                params.put("offer_details", adDetailsEditText.getText().toString().trim());
                System.out.println("parameters : " + params);
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        edit_request.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(edit_request);
    }

    private void getAdData(final String adId) {
        images_Urls.clear();
        StringRequest adDataRequest = new StringRequest(Request.Method.POST, Constants.getAd, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
                progressBar.setVisibility(View.GONE);
                edit_ad_layot.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("response")) {
                        JSONObject offerObject = jsonObject.getJSONObject("offer");
                        emailEditText.setText(offerObject.getString("email"));
                        mobileEditText.setText(offerObject.getString("phone"));
                        selectedCategoryId = String.valueOf(offerObject.getInt("category"));
                        adNameEditText.setText(offerObject.getString("offer_title"));
                        adDetailsEditText.setText(offerObject.getString("offer_details"));

                        Glide.with(mContext).load(Constants.imagesBaseUrl + offerObject.getString("image")).into(fifthPhotoButton);
                        deleteFifthPhoto.setVisibility(View.VISIBLE);

                        for (int i = 0; i < HomeActivity.categoriesList.size(); i++) {
                            if (selectedCategoryId.equals(HomeActivity.categoriesList.get(i).getCategory_id()))
                                categoryNameTextView.setText(HomeActivity.categoriesList.get(i).getCategory_name());
                        }

                        JSONArray imagesArray = offerObject.getJSONArray("get_ad_photos");
                        for (int i = 0; i < imagesArray.length(); i++) {
                            images_Urls.add(imagesArray.getJSONObject(i).getString("photo"));
                        }
                        if (images_Urls.size() == 1) {
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(0)).into(fourthPhotoButton);
                            deleteFourthPhoto.setVisibility(View.VISIBLE);
                        } else if (images_Urls.size() == 2) {
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(0)).into(fourthPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(1)).into(thirdPhotoButton);
                            deleteFourthPhoto.setVisibility(View.VISIBLE);
                            deleteThirdPhoto.setVisibility(View.VISIBLE);
                        } else if (images_Urls.size() == 3) {
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(0)).into(fourthPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(1)).into(thirdPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(2)).into(secondPhotoButton);
                            deleteFourthPhoto.setVisibility(View.VISIBLE);
                            deleteThirdPhoto.setVisibility(View.VISIBLE);
                            deleteSecondPhoto.setVisibility(View.VISIBLE);
                        } else if (images_Urls.size() == 4) {
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(0)).into(fourthPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(1)).into(thirdPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(2)).into(secondPhotoButton);
                            Glide.with(mContext).load(Constants.imagesBaseUrl + images_Urls.get(3)).into(firstPhotoButton);
                            deleteFourthPhoto.setVisibility(View.VISIBLE);
                            deleteThirdPhoto.setVisibility(View.VISIBLE);
                            deleteSecondPhoto.setVisibility(View.VISIBLE);
                            deleteFirstPhoto.setVisibility(View.VISIBLE);
                        }

//                        timeTextView.setText(offerObject.getString("time"));

                        stateId = offerObject.getString("state_id");
                        if (!offerObject.isNull("get_state"))
                            statesTextView.setText(offerObject.getJSONObject("get_state").getString("ar_name"));
                        priceEditText.setText(offerObject.getString("price"));
                        currency_text_view.setText(offerObject.getJSONObject("get_currency").getString("currency"));
                        if (offerObject.getInt("can_discuss") == Constants.OFFER_DISCUSSABLE) {
                            isDiscussableCheckbox.setChecked(true);
                            discuss = 1;
                        } else {
                            discuss = 0;
                        }
//                            .setText("(غير قابل للنقاش)");
                        if (offerObject.getInt("status") == Constants.OFFER_STATUS_NEW) {
                            usedOrNewRadioGroup.check(R.id.edit_new_ad_radio_button_id);
                            ad_state = 0;
                        } else {
                            usedOrNewRadioGroup.check(R.id.edit_used_ad_radio_button_id);
                            ad_state = 1;
                        }

                        adNameEditText.setText(offerObject.getString("offer_title"));
                        adDetailsEditText.setText(offerObject.getString("offer_details"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("offer_id", adId);
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                System.out.println("parameters : " + params);
                return params;
            }
        };
        System.out.println("URL : " + adDataRequest.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        adDataRequest.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(adDataRequest);
    }

    private void deleteImage(final ImageView imageView, final ImageView deleteImageView, final String photoName) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.remove_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        imageView.setImageDrawable(null);
                        if (clickedPhoto == 1) {
                            firstPhotoName = "";
                            deleteFirstPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 2) {
                            secondPhotoName = "";
                            deleteSecondPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 3) {
                            thirdPhotoName = "";
                            deleteThirdPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 4) {
                            fourthPhotoName = "";
                            deleteFourthPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 5) {
                            fifthPhotoName = "";
                            deleteFifthPhoto.setVisibility(View.GONE);
                        }
                        deleteImageView.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, imageView, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("photoName", photoName);
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

    private void getImageFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void getCountryStates(final String countryId) {
//        customLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.getStates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("response : " + response);
                        try {
                            statesList.clear();
                            JSONArray statesArrays = new JSONObject(response).getJSONArray("states").getJSONObject(0).getJSONArray("get_states");
                            for (int i = 0; i < statesArrays.length(); i++) {
                                JSONObject stateObject = statesArrays.getJSONObject(i);
                                statesList.add(stateObject.getString("ar_name"));
                                statesIDs.add(stateObject.getString("id"));
                            }
//                            customLoadingDialog.dismiss();
                            spinnerAdapter.notifyDataSetChanged();
                            statesSpinner.performClick();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, adNameEditText, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, adNameEditText, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, adNameEditText, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, adNameEditText, getString(R.string.no_connection), R.color.colorError);
                        }
//                        customLoadingDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_id", countryId);
                System.out.println("params : " + params);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) { //Upload a new Image
            Uri file_path = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_path);
                imageBase64 = Utils.getEncoded64ImageStringFromBitmap(bitmap);
                if (clickedPhoto == 1) {
                    uploadImage(imageBase64, firstPhotoButton, bitmap, clickedPhoto);
                } else if (clickedPhoto == 2) {
                    uploadImage(imageBase64, secondPhotoButton, bitmap, clickedPhoto);
                } else if (clickedPhoto == 3) {
                    uploadImage(imageBase64, thirdPhotoButton, bitmap, clickedPhoto);
                } else if (clickedPhoto == 4) {
                    uploadImage(imageBase64, fourthPhotoButton, bitmap, clickedPhoto);
                } else if (clickedPhoto == 5) {
                    uploadImage(imageBase64, fifthPhotoButton, bitmap, clickedPhoto);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage(final String imageBase64, final ImageView imageView, final Bitmap bitmap, final int clickedPhoto) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.upload_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("ress: " + response);
                        imageView.setImageBitmap(bitmap);
                        try {
                            if (clickedPhoto == 1) {
                                firstPhotoName = new JSONObject(response).getString("fileName");
                                deleteFirstPhoto.setVisibility(View.VISIBLE);
                            } else if (clickedPhoto == 2) {
                                secondPhotoName = new JSONObject(response).getString("fileName");
                                deleteSecondPhoto.setVisibility(View.VISIBLE);
                            } else if (clickedPhoto == 3) {
                                thirdPhotoName = new JSONObject(response).getString("fileName");
                                deleteThirdPhoto.setVisibility(View.VISIBLE);
                            } else if (clickedPhoto == 4) {
                                fourthPhotoName = new JSONObject(response).getString("fileName");
                                deleteFourthPhoto.setVisibility(View.VISIBLE);
                            } else if (clickedPhoto == 5) {
                                fifthPhotoName = new JSONObject(response).getString("fileName");
                                deleteFifthPhoto.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, imageView, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, imageView, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("photo", imageBase64);
                params.put("offer_id", adId);
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                if (clickedPhoto == 1) {
                    params.put("type", "main");
                } else {
                    params.put("type", "relate");
                }

                System.out.println("parameters:: " + params);
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
        mCategoriesCustomDialog.dismiss();
        if (isCategorySelected) {
            System.out.println("Selected Category : " + categoriesModel.getCategory_id());
            selectedCategoryId = categoriesModel.getCategory_id();
            categoryNameTextView.setText(categoriesModel.getCategory_name());
            Glide.with(mContext)
                    .load(Constants.imagesBaseUrl + categoriesModel.getCategory_image())
                    .placeholder(R.drawable.ic_menu_black_24dp) // can also be a drawable
                    .error(R.drawable.category_placeholder) // will be displayed if the image cannot be loaded
                    .into(categoryIconImageView);
        } else {
            categoryNameTextView.setText(categoriesModel.getCategory_name());
            categoryIconImageView.setImageResource(R.drawable.category_placeholder);
        }
    }

    @Override
    public void onCountrySelected(CountryModel mCountryModel) {
        mCountriesCustomDialog.dismiss();
        stateId = mCountryModel.getCountry_id();
        statesTextView.setText(mCountryModel.getCountry_name());
        Constants.isStatesLoading = false;
    }
}
