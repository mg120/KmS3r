package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.tqnee.KamS3r.Activites.ProfilePreviewActivity;
import com.tqnee.KamS3r.Adapters.OffersAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.Interfaces.IOffersOperationsListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.OfferModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.CategoriesCustomDialog;
import com.tqnee.KamS3r.Widgets.ImagePreviewDialog;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Kamal Marcus on 31/10/2017.
 * kamalmarcus94@gmail.com
 * +201015793659
 */

public class HashtagOffersFragment extends BaseFragment implements IOffersOperationsListener, ISendingMessagesListener {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    OffersAdapter mOffersAdapter;
    LinearLayoutManager mLinearLayoutManager;
    Intent mIntent;


    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.loading_progress)
    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;

    ArrayList<OfferModel> offersList=new ArrayList<OfferModel>();
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    ImagePreviewDialog mImagePreviewDialog;
    @BindView(R.id.iv_category_icon)
    ImageView iv_category_icon;
    @BindView(R.id.layout_choose_category)
    RelativeLayout layout_choose_category;
    @BindView(R.id.tv_category_name)
    TextView tv_category_name;
    private CategoriesCustomDialog mCategoriesCustomDialog;
    private int current;
    private boolean isRefreshing=false;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_questions;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        mSharedPrefManager = new SharedPrefManager(mContext);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mOffersAdapter = new OffersAdapter(mContext, mSharedPrefManager, this);
        mRecycler.setAdapter(mOffersAdapter);
        loadOffers(Constants.hashtagOffers+"?country_code="+mSharedPrefManager.getCountryCode());

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing=true;
                loadOffers(Constants.hashtagOffers+"?country_code="+mSharedPrefManager.getCountryCode());            }
        });
        layout_choose_category.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFavouriteClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            offersList.get(position).setFavourite(!offersList.get(position).isFavourite());
            mOffersAdapter.notifyDataSetChanged();
            addToFavourite(Constants.addToFavouriteUrl, offersList.get(position).getOffer_id());
        } else {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @Override
    public void onMessageClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddingMessageDialog = new AddingMessageDialog(mContext, offersList.get(position).getOffer_user_id(), this);
            mAddingMessageDialog.show();
        } else {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @Override
    public void onShareClicked(int position) {
        Utils.Share(mContext, getString(R.string.offer_share), Constants.shareAdUrl+offersList.get(position).getOffer_id());

    }


    @Override
    public void onImageClicked(int position) {
        mImagePreviewDialog = new ImagePreviewDialog(mContext, offersList.get(position).getOffer_image());
        mImagePreviewDialog.show();
    }

    @Override
    public void onUserClicked(int position) {
        mIntent = new Intent(mContext, ProfilePreviewActivity.class);
        mIntent.putExtra("user_id", offersList.get(position).getOffer_user_id());
        startActivity(mIntent);
    }

    @Override
    public void onOptionMenuClicked(int position) {

    }

    @Override
    public void onSendMessage(String user_id, String message) {
        mAddingMessageDialog.dismiss();
        sendingMessage(Constants.sendingMessageUrl, user_id, message);
    }

    public void loadOffers(String serviceUrl) {
        loading_progress.show();
        offersList.clear();
        mOffersAdapter.notifyDataSetChanged();
        final StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading_progress.hide();
                        Utils.printLog(Constants.LOG_TAG + "(hashtag offers)", response);
                        parseOffersResponse(response);

                        if(isRefreshing){
                            swipe_layout.setRefreshing(false);
                            isRefreshing=false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading_progress.hide();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                System.out.println("Parameters : "+params);
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

    private void parseOffersResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optBoolean("response")) {
                if (jsonObject.has("offers")) {
                    JSONObject offersObject = jsonObject.optJSONObject("offers");
                    JSONArray offersArray = offersObject.optJSONArray("data");
                    for (int i = 0; i < offersArray.length(); i++) {
                        JSONObject offerObject = offersArray.optJSONObject(i);
                        OfferModel offersModel = new OfferModel();
                        offersModel.setOffer_id(String.valueOf(offerObject.optInt("id")));
                        offersModel.setOffer_title(offerObject.optString("offer_title"));
                        offersModel.setOffer_image(offerObject.optString("image"));
                        offersModel.setOffer_time(offerObject.optString("time"));
                        offersModel.setOffer_user_id(String.valueOf(offerObject.optJSONObject("get_user").optInt("id")));
                        offersModel.setOffer_username(offerObject.optJSONObject("get_user").optString("fullname"));
                        offersModel.setOffer_user_image(offerObject.optJSONObject("get_user").optString("photo"));
                        if(!offerObject.isNull("get_state"))
                            offersModel.setOffer_state(offerObject.getJSONObject("get_state").getString("ar_name"));
                        offersModel.setOffer_price(offerObject.getString("price")+" "+offerObject.getString("currecny_txt"));
                        offersList.add(offersModel);
                    }
                }
            }
            if(offersList.isEmpty()){
                noDataLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText("لا يوجد اعلانات عن "+tv_category_name.getText());
            }
            else {
                mOffersAdapter.addOffersList(offersList);
                noDataLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendingMessage(String serviceUrl, final String user_id, final String message) {
//        mCustomLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mCustomLoadingDialog.dismiss();
                        Utils.printLog(Constants.LOG_TAG + "(sendMessage)", response);
                        Utils.showSnackBar(mContext, layout_content, getString(R.string.message_sent_successfully), R.color.colorCorrect);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);

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

    private void addToFavourite(String serviceUrl, final String offer_id) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(favorites)", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("item_id", offer_id);
                params.put("type", Constants.OFFER_TYPE);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
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
