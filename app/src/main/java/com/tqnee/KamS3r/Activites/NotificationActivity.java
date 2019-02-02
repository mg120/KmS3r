package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Adapters.NotificationAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.INotificationListener;
import com.tqnee.KamS3r.Model.NotificationModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class NotificationActivity extends ParentActivity implements INotificationListener {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    NotificationAdapter mNotificationAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    ArrayList<NotificationModel> notificationsList;

    int current = 1;
    int last_page = 0;


    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.notification_title));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);

        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mNotificationAdapter = new NotificationAdapter(mContext, this);
        mRecycler.setAdapter(mNotificationAdapter);
        loadNotification(Constants.notificationsUrl);

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_notification;
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
    public void onUserClicked(int position) {

    }

    @Override
    public void onItemClicked(int position) {

    }


    private void loadNotification(String serviceUrl) {
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        notificationsList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(notification)", response);
                        parseNotificationResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("page", current + "");

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

    private void parseNotificationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                JSONObject notificationObject = jsonObject.optJSONObject("notifications");
                last_page = notificationObject.optInt("last_page");
                if (notificationObject.has("data")) {
                    JSONArray dataArray = notificationObject.optJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        NotificationModel notificationModel = new NotificationModel();
                        JSONObject jsonObject1 = dataArray.optJSONObject(i);
                        notificationModel.setNotification_type(jsonObject1.optInt("type"));
                        notificationModel.setNotification_title(jsonObject1.getString("title"));
                        notificationModel.setNotify_id(jsonObject1.optString("notify_id"));
                        notificationModel.setNotification_id(jsonObject1.optString("id"));
                        notificationModel.setNotification_comment(jsonObject1.optString("content"));
                        notificationModel.setNotification_time(jsonObject1.optString("time"));
                        if (jsonObject1.has("get_sender_user") && jsonObject1.optJSONObject("get_sender_user") != null) {
                            notificationModel.setNotification_username(jsonObject1.optJSONObject("get_sender_user").optString("fullname"));
                            notificationModel.setNotification_user_id(jsonObject1.optJSONObject("get_sender_user").optString("id"));
                            notificationModel.setNotification_user_image(jsonObject1.optJSONObject("get_sender_user").optString("photo"));
                        }
                        notificationsList.add(notificationModel);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (notificationsList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            noDataTextView.setText("لا يوجد إشعارات");
        } else {
            mNotificationAdapter.addNotificationList(notificationsList);
            noDataLayout.setVisibility(View.GONE);
        }

    }
}
