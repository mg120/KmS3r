package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.gson.Gson;
import com.tqnee.KamS3r.Activites.AdsDetailsActivity;
import com.tqnee.KamS3r.Adapters.AdsAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.Interfaces.IAdsOperationsListener;
import com.tqnee.KamS3r.Model.AdModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.ImagePreviewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by ramzy on 9/13/2017.
 */

public class AdsFragment extends BaseFragment implements IAdsOperationsListener {

    public static String USER_ID = "user_id";
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    AdsAdapter mAdsAdapter;
    LinearLayoutManager mLinearLayoutManager;
    Context mContext;
    Intent mIntent;
    String currentUserID;
    private SharedPrefManager mSharedPrefManager;
    //    private CustomLoadingDialog customLoadingDialog;
    ArrayList<AdModel> adsList = new ArrayList<AdModel>();
    static String otherUserId = "";
    private ImagePreviewDialog mImagePreviewDialog;

    public static AdsFragment newInstance(String user_id) {
        Bundle args = new Bundle();
        args.putString(USER_ID, user_id);
        AdsFragment fragment = new AdsFragment();
        fragment.setArguments(args);
        otherUserId = user_id;
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        mSharedPrefManager = new SharedPrefManager(mContext);
//        customLoadingDialog = new CustomLoadingDialog(mContext);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mAdsAdapter = new AdsAdapter(mContext, adsList, this);
        mRecycler.setAdapter(mAdsAdapter);
        currentUserID = mSharedPrefManager.getUserDate().getId();

        getAds(otherUserId);
    }

    private void getAds(final String userID) {
//        customLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.userAds,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("Response : " + response);
                        adsList.clear();
                        try {
                            JSONArray adsArray = new JSONObject(response).getJSONObject("offers").getJSONArray("data");
                            for (int i = 0; i < adsArray.length(); i++) {
                                JSONObject adObject = adsArray.getJSONObject(i);
                                AdModel ad = new AdModel();
                                ad.setId(adObject.getString("id"));
                                ad.setTitle(adObject.getString("offer_title"));
                                if (adObject.getJSONObject("get_state") != null)
                                    ad.setCity(adObject.getJSONObject("get_state").getString("ar_name"));
                                ad.setCurrency(adObject.getJSONObject("get_currency").getString("currency"));
                                ad.setDetails(adObject.getString("offer_details"));
                                ad.setImage(adObject.getString("image"));
                                ad.setTime(adObject.getString("time"));
                                ad.setUserId(adObject.getJSONObject("get_user").getString("id"));
                                ad.setUserName(adObject.getJSONObject("get_user").getString("fullname"));
                                ad.setUserPhoto(adObject.getJSONObject("get_user").getString("photo"));
                                ad.setFavorite(adObject.getInt("isFav") == 1 ? true : false);
                                ad.setFavoritesCount(adObject.getString("fav_count"));
                                ad.setPrice(adObject.getString("price") + " " + adObject.getString("currecny_txt"));

                                adsList.add(ad);
                            }
                            // Convert list to Json object to check it ...
                            Gson gson = new Gson();
                            String list_obj = gson.toJson(adsList);
                            Log.e("ads_list: ", list_obj);
                            ///////////////////////////////////////////////
                            if (adsList.isEmpty()) {
                                mRecycler.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                noDataTextView.setText("لا يوجد اعلانات");
                            } else {
                                noDataLayout.setVisibility(View.GONE);
                                mAdsAdapter.notifyDataSetChanged();
                                noDataLayout.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                                Utils.showSnackBar(mContext, mRecycler, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, mRecycler, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, mRecycler, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, mRecycler, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userID);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                System.out.println("parameters ads : " + params);
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


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFavouriteClicked(int position) {

    }

    @Override
    public void onMessageClicked(int position) {

    }

    @Override
    public void onShareClicked(int position) {
        Utils.Share(mContext, getString(R.string.question_title), Constants.shareAdUrl + adsList.get(position).getId());

    }

    @Override
    public void onItemClicked(int position) {
//        Toast.makeText(mContext, adsList.get(position).getId() + "\n" + adsList.get(position).getUserId(), Toast.LENGTH_SHORT).show();
        Intent mIntent = new Intent(mContext, AdsDetailsActivity.class);
        mIntent.putExtra("adId", adsList.get(position).getId());
        mIntent.putExtra("userId", adsList.get(position).getUserId());
        startActivity(mIntent);
    }

    @Override
    public void onAnswerClicked(int position) {

    }

    @Override
    public void onOptionMenuClicked(int position) {
        Utils.printLog("option_menu", "Clicked");
    }


    @Override
    public void onImageClicked(int position) {
        mImagePreviewDialog = new ImagePreviewDialog(mContext, adsList.get(position).getImage());
        mImagePreviewDialog.show();
    }

    @Override
    public void onUserClicked(int position) {

    }
}

