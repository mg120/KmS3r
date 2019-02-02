package com.tqnee.KamS3r.Activites;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Adapters.AdsImagesSliderAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IReportItemListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.WebServices;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.AddingReportDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.relex.circleindicator.CircleIndicator;

public class AdsDetailsActivity extends ParentActivity implements IReportItemListener, ISendingMessagesListener {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    @BindView(R.id.view_pager_slider)
    ViewPager view_pager_slider;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.iv_option_menu)
    ImageView iv_option_menu;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.iv_user_image)
    CircleImageView iv_user_image;
    @BindView(R.id.tv_is_discussable)
    TextView isDiscussableTextView;
    @BindView(R.id.tv_new_or_used)
    TextView newOrUsedTextView;
    @BindView(R.id.ad_details_progress_id)
    ProgressBar progressBar;

    AdsImagesSliderAdapter imagesSliderAdapter;
    boolean isFabPressed = false;
    private LinearLayout callLayout;
    private LinearLayout saveContactLayout;
    private LinearLayout copyLayout;
    private View mainLayout;
    //    private CustomLoadingDialog customLoadingDialog;
    private TextView userNameTextView;
    private TextView timeTextView;
    private TextView costTextView;
    private TextView countryTextView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String phoneNumber = "";
    private TextView phoneTextView;
    ArrayList<String> imagesUrls = new ArrayList<String>();
    private ImageView shareImageView;
    private ImageView messageImageView;
    private ImageView favouriteImageView;
    private ImageView answerImageView;
    private String userName = "";
    private String offerTitle;
    boolean isFav = false;
    private String adId = "";
    private TextView favoriteCounterTextView;
    private AdsDetailsActivity mIReportItemListener;
    private PopupMenu popup;
    private AddingReportDialog mAddingReportDialog;
    //    private CustomLoadingDialog mCustomLoadingDialog;
    private AddingMessageDialog mAddingMessageDialog;
    String userId;
    MenuItem edit_ad, delete_ad, report;

    @Override
    protected void initializeComponents() {
        mContext = this;
        setToolbarTitle(getResources().getString(R.string.ads_details));
        mIReportItemListener = this;
        popup = new PopupMenu(mContext, iv_option_menu);
        popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
        edit_ad = popup.getMenu().findItem(R.id.edit_ad);
        delete_ad = popup.getMenu().findItem(R.id.delete_ad);
        report = popup.getMenu().findItem(R.id.report);

//        customLoadingDialog = new CustomLoadingDialog(this);
        mSharedPrefManager = new SharedPrefManager(mContext);
        imagesSliderAdapter = new AdsImagesSliderAdapter(mContext, imagesUrls);
        view_pager_slider.setAdapter(imagesSliderAdapter);
        indicator.setViewPager(view_pager_slider);
        imagesSliderAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        mainLayout = findViewById(R.id.main_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ad_details_swipeRefreshLayout);
        userNameTextView = (TextView) findViewById(R.id.tv_user_name);
        timeTextView = (TextView) findViewById(R.id.tv_time);
        costTextView = (TextView) findViewById(R.id.tv_ads_cost);
        countryTextView = (TextView) findViewById(R.id.tv_country);

        titleTextView = (TextView) findViewById(R.id.tv_ads_title);
        detailsTextView = (TextView) findViewById(R.id.tv_ads_description);
        phoneTextView = (TextView) findViewById(R.id.phone_text_view);

        shareImageView = (ImageView) findViewById(R.id.iv_share);
        messageImageView = (ImageView) findViewById(R.id.iv_message);
        favouriteImageView = (ImageView) findViewById(R.id.iv_favorite);
        favoriteCounterTextView = (TextView) findViewById(R.id.likes_counter_text_view);
        answerImageView = (ImageView) findViewById(R.id.iv_answer);

        callLayout = (LinearLayout) findViewById(R.id.call_layout);
        saveContactLayout = (LinearLayout) findViewById(R.id.save_contact_layout);
        copyLayout = (LinearLayout) findViewById(R.id.copy_layout);

        if (getIntent().getExtras() != null) {
            adId = getIntent().getStringExtra("adId");
            getAds(adId);
            userId = getIntent().getStringExtra("userId");
        }

        final FloatingActionButton contactFab = (FloatingActionButton) findViewById(R.id.contact_fab);
        contactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabPressed) {
                    rotateFab(60);
                    isFabPressed = true;
                    mainLayout.setVisibility(View.VISIBLE);
                    setFABsVisibility(View.VISIBLE);
                } else {
                    rotateFab(0);
                    isFabPressed = false;
                    mainLayout.setVisibility(View.GONE);
                    setFABsVisibility(View.GONE);
                }
            }

            private void rotateFab(float rotationAngle) {
                final OvershootInterpolator interpolator = new OvershootInterpolator();
                ViewCompat.animate(contactFab).
                        rotation(rotationAngle).
                        withLayer().
                        setDuration(300).
                        setInterpolator(interpolator).
                        start();
            }
        });

        if (!mSharedPrefManager.getLoginStatus())
            contactFab.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getAds(adId);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (userId.equals(mSharedPrefManager.getUserDate().getId())) {
            edit_ad.setVisible(true);
            delete_ad.setVisible(true);
            report.setVisible(false);
        } else {
            edit_ad.setVisible(false);
            delete_ad.setVisible(false);
            report.setVisible(true);
        }

    }

    private void getAds(final String id) {
        progressBar.setVisibility(View.VISIBLE);
        imagesUrls.clear();
        final StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.getAd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("Response : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("response")) {
                                JSONObject offerObject = jsonObject.getJSONObject("offer");
                                userName = offerObject.getJSONObject("get_user").getString("fullname");
                                userNameTextView.setText(userName);
                                Glide.with(mContext).load(offerObject.getJSONObject("get_user").getString("photo"))
                                        .error(R.mipmap.person_place_holder)
                                        .placeholder(R.mipmap.person_place_holder)
                                        .into(iv_user_image);
                                imagesUrls.add(offerObject.getString("image"));
                                JSONArray imagesArray = offerObject.getJSONArray("get_ad_photos");
                                for (int i = 0; i < imagesArray.length(); i++) {
                                    imagesUrls.add(imagesArray.getJSONObject(i).getString("photo"));
                                }
                                timeTextView.setText(offerObject.getString("time"));

                                if (!offerObject.isNull("get_state"))
                                    countryTextView.setText(offerObject.getJSONObject("get_state").getString("ar_name"));
                                costTextView.setText(offerObject.getString("price") + " " + offerObject.getJSONObject("get_currency").getString("currency"));
                                if (offerObject.getInt("can_discuss") == Constants.OFFER_DISCUSSABLE) {
                                    isDiscussableTextView.setText("(قابل للنقاش)");
                                } else
                                    isDiscussableTextView.setText("(غير قابل للنقاش)");
                                if (offerObject.getInt("status") == Constants.OFFER_STATUS_NEW)
                                    newOrUsedTextView.setText("جديد");
                                else
                                    newOrUsedTextView.setText("مستعمل");
//                                if (offerObject.getString("country_code").equals("EG")) {
//                                    costTextView.setText(offerObject.getString("price") + " جنيه مصرى");
//                                    countryTextView.setText("مصر");
//                                } else {
//                                    costTextView.setText(offerObject.getString("price") + " ريال");
//                                    countryTextView.setText("السعودية");
//                                }

                                offerTitle = offerObject.getString("offer_title");
                                titleTextView.setText(offerTitle);
                                detailsTextView.setText(offerObject.getString("offer_details"));

                                phoneNumber = offerObject.getString("phone");
                                phoneTextView.setText("اتصال " + phoneNumber);

                                if (offerObject.getInt("isFav") == 1)
                                    favouriteImageView.setImageResource(R.mipmap.like_filled_icon);

                                favoriteCounterTextView.setText(offerObject.getString("fav_count"));
                                setButtonsClickListeners();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imagesSliderAdapter.notifyDataSetChanged();
//                        customLoadingDialog.hide();
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        customLoadingDialog.hide();
                        progressBar.setVisibility(View.GONE);
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, callLayout, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, callLayout, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, callLayout, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, callLayout, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("offer_id", id);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                System.out.println("parameters : " + params);
                return params;
            }
        };

        System.out.println("URL : " + verifyReq.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private void setButtonsClickListeners() {
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.Share(mContext, getString(R.string.ad_share), Constants.shareAdUrl + adId);
            }
        });

        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(AdsDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        saveContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, userName);
                startActivity(intent);
            }
        });
        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", phoneNumber);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "تم نسخ الرقم", Toast.LENGTH_LONG).show();
            }
        });

        favouriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebServices.addToFavourite(mContext, favouriteImageView, Constants.addToFavouriteUrl, adId, Constants.favorite_ad_item_type, mSharedPrefManager.getUserDate().getApiToken());
                int likesCounter = Integer.parseInt(favoriteCounterTextView.getText().toString());
                if (isFav) {
                    favouriteImageView.setImageResource(R.mipmap.like_empty_icon);
                    isFav = false;
                    favoriteCounterTextView.setText("" + (likesCounter - 1));
                } else {
                    favouriteImageView.setImageResource(R.mipmap.like_filled_icon);
                    isFav = true;
                    favoriteCounterTextView.setText("" + (likesCounter + 1));
                }
            }
        });
    }

    private void setFABsVisibility(int visibility) {
        if (!phoneNumber.equals("")) {
            callLayout.setVisibility(visibility);
            copyLayout.setVisibility(visibility);
        }
        saveContactLayout.setVisibility(visibility);
        mainLayout.setVisibility(visibility);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_ads_details;
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

    @OnClick(R.id.iv_user_image)
    void onUserClicked() {
        Intent mIntent = new Intent(mContext, ProfilePreviewActivity.class);
        mIntent.putExtra("user_id", userId);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_user_name)
    void onNameClicked() {
        onUserClicked();
    }

    @OnClick(R.id.iv_message)
    void onMessageClicked() {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddingMessageDialog = new AddingMessageDialog(mContext, userId, this);
            mAddingMessageDialog.show();
        } else {
            Utils.showSnackBar(mContext, iv_option_menu, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @OnClick(R.id.iv_option_menu)
    void onOptionMenuClicked() {
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.report) {
                    popup.dismiss();
                    if (mSharedPrefManager.getLoginStatus()) {
                        mAddingReportDialog = new AddingReportDialog(mContext, adId, Constants.QUESTION_TYPE, mIReportItemListener);
                        mAddingReportDialog.show();
                    } else {
                        Utils.showSnackBarWithLong(mContext, iv_option_menu, getString(R.string.you_should_login), R.color.colorError);
                    }
                } else if (item.getItemId() == R.id.edit_ad) {
                    Intent intent = new Intent(AdsDetailsActivity.this, EditAdActivity.class);
                    intent.putExtra("ad_id", adId);
                    startActivity(intent);

                } else if (item.getItemId() == R.id.delete_ad) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdsDetailsActivity.this);
                    builder.setIcon(R.mipmap.splash_logo)
                            .setMessage("تأكيد حذف الاعلان ؟")
                            .setCancelable(false)
                            .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    delete_Ad(adId);
                                }
                            }).setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onReportItem(String item_id, String item_type, String message) {
        mAddingReportDialog.dismiss();
        reportingItem(Constants.reportItemUrl, item_id, item_type, message);
    }

    private void reportingItem(String serviceUrl, final String item_id, final String item_type, final String message) {
//        mCustomLoadingDialog.show();

        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mCustomLoadingDialog.dismiss();
                        Utils.printLog(Constants.LOG_TAG + "(report)", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("type", item_type);
                params.put("message", message);
                params.put("item_id", item_id);
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
    public void onSendMessage(String user_id, String message) {
        mAddingMessageDialog.dismiss();
        sendingMessage(Constants.sendingMessageUrl, user_id, message);
    }

    private void sendingMessage(String serviceUrl, final String user_id, final String message) {
//        mCustomLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mCustomLoadingDialog.dismiss();
                        Utils.printLog(Constants.LOG_TAG + "(sendMessage)", response);
                        Utils.showSnackBar(mContext, timeTextView, getString(R.string.message_sent_successfully), R.color.colorCorrect);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, iv_option_menu, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("receiver_id", user_id);
                params.put("message", message);
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

    private void delete_Ad(final String ad_Id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.delete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("response").equals("true")) {
                        Toasty.success(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toasty.error(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
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
                //   params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("offer_id", ad_Id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AdsDetailsActivity.this);
        queue.add(stringRequest);
    }

}
