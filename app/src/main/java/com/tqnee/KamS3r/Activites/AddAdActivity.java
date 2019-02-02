package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.tqnee.KamS3r.Widgets.CategoriesCustomDialog;
import com.tqnee.KamS3r.Widgets.CountriesCustomDialog;
import com.tqnee.KamS3r.Widgets.CustomLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddAdActivity extends ParentActivity implements ICategoryItemClicked, ICountrySelected {

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
    String firstPhotoName = "";
    String secondPhotoName = "";
    String thirdPhotoName = "";
    String fourthPhotoName = "";
    String fifthPhotoName = "";
    private int PICK_IMAGE = 3;
    int clickedPhoto = 1;
    private String imageBase64 = "";
    private ImageButton deleteFirstPhoto;
    private ImageButton deleteSecondPhoto;
    private ImageButton deleteThirdPhoto;
    private ImageButton deleteFourthPhoto;
    private ImageButton deleteFifthPhoto;
    CustomLoadingDialog customLoadingDialog;
    private Button postAdButton;
    private RadioButton newAdRadioButton;
    private SharedPrefManager sharedPrefManager;
    private String selectedCategoryId = "";
    private ArrayList<String> statesList;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> statesIDs;
    private CountriesCustomDialog mCountriesCustomDialog;
    private String stateId = "";
    private TextView statesTextView;
    String questionId = "";
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.add_ad));
        mContext = this;
//        customLoadingDialog = new CustomLoadingDialog(mContext);
        sharedPrefManager = new SharedPrefManager(mContext);
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        if (getIntent().hasExtra("question_id")) {
            questionId = getIntent().getStringExtra("question_id");
        }
        //Category
        RelativeLayout chooseCategoryButton = (RelativeLayout) findViewById(R.id.layout_choose_category);
        if (!questionId.equals("")) { //User is adding offer to specific question so no need for category
            chooseCategoryButton.setVisibility(View.GONE);
        } else {
            categoryNameTextView = (TextView) findViewById(R.id.tv_category_name);
            categoryIconImageView = (ImageView) findViewById(R.id.iv_category_icon);
            chooseCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCategoriesCustomDialog = new CategoriesCustomDialog(mContext, AddAdActivity.this);
                    mCategoriesCustomDialog.show();
                }
            });
        }

        progressBar = (ProgressBar) findViewById(R.id.add_ad_progress_id);
        adNameEditText = (EditText) findViewById(R.id.ad_name_edit_text);
        adDetailsEditText = (EditText) findViewById(R.id.ad_details_edit_text);

        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        isDiscussableCheckbox = (CheckBox) findViewById(R.id.is_discussable_checkbox);
        usedOrNewRadioGroup = (RadioGroup) findViewById(R.id.new_or_used_radio_group);
        newAdRadioButton = (RadioButton) findViewById(R.id.new_ad_radio_button);
        statesSpinner = (Spinner) findViewById(R.id.country_spinner);
        TextView currencyTextView = (TextView) findViewById(R.id.currency_text_view);
        currencyTextView.setText(mSharedPrefManager.getCurrencyText());

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
                    getCountryStates(mSharedPrefManager.getCountryId());
                }
                return true;
            }
        });
        statesTextView = (TextView) findViewById(R.id.state_text_view);
        statesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.isStatesLoading = true;
                mCountriesCustomDialog = new CountriesCustomDialog(mContext, AddAdActivity.this);
                mCountriesCustomDialog.show();
            }
        });

        mobileEditText = (EditText) findViewById(R.id.mobile_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        firstPhotoButton = (ImageView) findViewById(R.id.first_photo_button);
        secondPhotoButton = (ImageView) findViewById(R.id.second_photo_button);
        thirdPhotoButton = (ImageView) findViewById(R.id.third_photo_button);
        fourthPhotoButton = (ImageView) findViewById(R.id.fourth_photo_button);
        fifthPhotoButton = (ImageView) findViewById(R.id.fifth_photo_button);

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


        postAdButton = (Button) findViewById(R.id.post_ad_button);
        postAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adTitle = adNameEditText.getText().toString();
                String adDetails = adDetailsEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String isDiscussable = (isDiscussableCheckbox.isChecked() ? "1" : "0");
                String adStatus = (newAdRadioButton.isChecked() ? "0" : "1");
                String country = mSharedPrefManager.getCountryCode();
                String mobile = mobileEditText.getText().toString();
                String email = emailEditText.getText().toString();
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

                if (!questionId.equals(""))
                    postAd(sharedPrefManager.getUserDate().getApiToken(), adTitle, adDetails, price, mSharedPrefManager.getCurrencyId(), email, mobile, adStatus, fifthPhotoName, country, adPhotos, isDiscussable, "", stateId);
                else {
                    if (categoryNameTextView.getText().toString().equals("القسم")) {
                        Utils.showSnackBar(mContext, categoryNameTextView, getString(R.string.you_should_choose_category), R.color.colorError);
                    } else if (adTitle.isEmpty()) {
                        Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_enter_ad_title), R.color.colorError);
                    } else if (adDetails.trim().isEmpty()) {
                        Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_enter_ad_description), R.color.colorError);
                    } else if (stateId.equals("")) {
                        Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_choose_state), R.color.colorError);
                    } else if (photosList.size() >= 1 && (!("".equals(photosList.get(0)))) && fifthPhotoName.equals("")) {
                        Utils.showSnackBar(mContext, adNameEditText, getString(R.string.you_should_add_main_photo), R.color.colorError);
                    } else {
                        postAd(sharedPrefManager.getUserDate().getApiToken(), adTitle, adDetails, price, mSharedPrefManager.getCurrencyId(), email, mobile, adStatus, fifthPhotoName, country, adPhotos, isDiscussable, selectedCategoryId, stateId);
                    }
                }
            }
        });
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

    private void getImageFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_ad;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) { //Upload a new Image
                Bitmap bitmap = decodeImageIntoBitmap(data);
                imageBase64 = Utils.getEncoded64ImageStringFromBitmap(bitmap);
                if (clickedPhoto == 1) {
                    uploadImage(imageBase64, firstPhotoButton, bitmap, firstPhotoName);
                } else if (clickedPhoto == 2) {
                    uploadImage(imageBase64, secondPhotoButton, bitmap, secondPhotoName);
                } else if (clickedPhoto == 3) {
                    uploadImage(imageBase64, thirdPhotoButton, bitmap, thirdPhotoName);
                } else if (clickedPhoto == 4) {
                    uploadImage(imageBase64, fourthPhotoButton, bitmap, fourthPhotoName);
                } else if (clickedPhoto == 5) {
                    uploadImage(imageBase64, fifthPhotoButton, bitmap, fifthPhotoName);
                }

            }
        } catch (Exception e) {
            Log.e("SIZE", e.toString());
            Utils.showSnackBar(mContext, fifthPhotoButton, getString(R.string.error_in_activity_result), R.color.colorError);
            e.printStackTrace();
        }
    }

    private void uploadImage(final String imageBase64, final ImageView imageView, final Bitmap bitmap, final String photoName) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.uploadImageToAd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
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
                if (!photoName.equals(""))
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

    private void deleteImage(final ImageView imageView, final ImageView deleteImageView, final String photoName) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.deleteImageFromAd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        imageView.setImageDrawable(null);
                        if (clickedPhoto == 1) {
                            firstPhotoName = "";
//                            deleteFirstPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 2) {
                            secondPhotoName = "";
//                            deleteSecondPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 3) {
                            thirdPhotoName = "";
//                            deleteThirdPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 4) {
                            fourthPhotoName = "";
//                            deleteFourthPhoto.setVisibility(View.GONE);
                        } else if (clickedPhoto == 5) {
                            fifthPhotoName = "";
//                            deleteFifthPhoto.setVisibility(View.GONE);
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

    private void postAd(final String apiToken, final String title, final String details, final String price, final String currency, final String email, final String phone, final String status, final String image, final String country, final String offerPhotos, final String discussable, final String category, final String stateId) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.postAd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("Response : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response")) {
                                if (jsonObject.optString("response").equals("true")) {
                                    Toast.makeText(mContext, getString(R.string.added_successfully), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
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
                                Utils.showSnackBar(mContext, firstPhotoButton, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, firstPhotoButton, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, firstPhotoButton, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, firstPhotoButton, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", apiToken);
                params.put("offer_title", title);
                params.put("offer_details", details);
                params.put("offer_price", price);
                params.put("offer_currency", currency);
                params.put("offer_email", email);
                params.put("offer_phone", phone);
                params.put("offer_status", status);
                params.put("discuss", discussable);
                params.put("country", country);
                if (!category.equals(""))
                    params.put("offer_category", category);
                params.put("state_id", stateId);
                if (!image.equals(""))
                    params.put("image", image);
                if (!offerPhotos.equals(""))
                    params.put("offer_photos", offerPhotos);
                if (!questionId.equals(""))
                    params.put("ask_id", questionId);

                System.out.println("parameters : " + params);
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

    @Override
    public void onCountrySelected(CountryModel mCountryModel) {
        mCountriesCustomDialog.dismiss();
        stateId = mCountryModel.getCountry_id();
        statesTextView.setText(mCountryModel.getCountry_name());
        Constants.isStatesLoading = false;
    }
}
