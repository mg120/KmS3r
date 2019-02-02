package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Adapters.OffersAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IOffersOperationsListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.OfferModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.ImagePreviewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class OffersActivity extends ParentActivity implements IOffersOperationsListener, ISendingMessagesListener {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    OffersAdapter mOffersAdapter;
    LinearLayoutManager mLinearLayoutManager;
    Intent mIntent;


    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.tv_add)
    TextView addAdTextView;

    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;

    String question_id;
    ArrayList<OfferModel> offersList;
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    private ImagePreviewDialog mImagePreviewDialog;


    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.offers));
        mContext = this;
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        mSharedPrefManager = new SharedPrefManager(mContext);
        if (getIntent().hasExtra("question_id")) {
            question_id = getIntent().getStringExtra("question_id");
        }
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mOffersAdapter = new OffersAdapter(mContext, mSharedPrefManager, this);
        mRecycler.setAdapter(mOffersAdapter);

        loadOffers(Constants.asksOffersUrl);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_offers;
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

    @OnClick(R.id.tv_add)
    void addNewOffer() {
        if (mSharedPrefManager.getLoginStatus())
            startActivity(new Intent(this, AddAdActivity.class).putExtra("question_id", question_id));
        else
            Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);

    }

    @Override
    public void onFavouriteClicked(int position) {
        offersList.get(position).setFavourite(!offersList.get(position).isFavourite());
        mOffersAdapter.notifyDataSetChanged();
        addToFavourite(Constants.addToFavouriteUrl, offersList.get(position).getOffer_id());
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
        Utils.Share(mContext, getString(R.string.offer_share), Constants.shareAdUrl + offersList.get(position).getOffer_id());

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
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        offersList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(offers)", response);
                        parseOffersResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
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
                params.put("ask_id", question_id);
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
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
                        offersList.add(offersModel);
                    }
                }
            }
            if (offersList.isEmpty()) {
                noDataLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText("لا يوجد عروض");
            } else {
                mOffersAdapter.addOffersList(offersList);
                noDataLayout.setVisibility(View.GONE);
            }
            mOffersAdapter.addOffersList(offersList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendingMessage(String serviceUrl, final String user_id, final String message) {
//        mCustomLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(sendMessage)", response);
                        Utils.showSnackBar(mContext, layout_content, getString(R.string.message_sent_successfully), R.color.colorCorrect);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
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
