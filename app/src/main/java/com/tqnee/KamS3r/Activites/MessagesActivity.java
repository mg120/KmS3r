package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
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
import com.tqnee.KamS3r.Adapters.MessagesAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IRecyclerItemClicked;
import com.tqnee.KamS3r.Model.MessagesModel;
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

public class MessagesActivity extends ParentActivity implements IRecyclerItemClicked {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    MessagesAdapter mMessagesAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;


    Intent mIntent;
    ArrayList<MessagesModel> messagesList;


    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.messages_title));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);

        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mMessagesAdapter = new MessagesAdapter(mContext, this);
        mRecycler.setAdapter(mMessagesAdapter);
        loadMessages(Constants.allMessagesUrl);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_messages;
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
    public void onRecyclerItemClickListener(int position) {
        mIntent = new Intent(mContext, InternalMessageActivity.class);
        mIntent.putExtra("message_id", messagesList.get(position).getThread_id());
        mIntent.putExtra("receiver_id", messagesList.get(position).getOther_person_id());
        startActivity(mIntent);
    }

    private void loadMessages(String serviceURL) {
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        messagesList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + ("messages"), response);
                        parseMessagesResponse(response);
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

    private void parseMessagesResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                if (jsonObject.has("user")) {
                    JSONObject user_object = jsonObject.optJSONObject("user");
                    if (user_object.has("data")) {
                        JSONArray messagesArray = user_object.optJSONArray("data");
                        for (int i = 0; i < messagesArray.length(); i++) {
                            MessagesModel allMessagesModel = new MessagesModel();
                            JSONObject messageObject = messagesArray.optJSONObject(i);
                            allMessagesModel.setThread_id(messageObject.optInt("id") + "");
                            if (messageObject.optString("view").equals("1")) {
                                allMessagesModel.setMessage_seen(true);
                            } else {
                                allMessagesModel.setMessage_seen(false);
                            }
                            allMessagesModel.setMessage(messageObject.optString("message"));
                            allMessagesModel.setSender_id(messageObject.optInt("sender_id") + "");
                            allMessagesModel.setReceiver_id(messageObject.optInt("receiver_id") + "");
                            allMessagesModel.setTime(messageObject.getString("time"));
                            JSONObject otherPersonObject = null;
                            if (allMessagesModel.getSender_id().equals(mSharedPrefManager.getUserDate().getId())) {
                                otherPersonObject = messageObject.optJSONObject("get_receiver_user");
                            } else if (allMessagesModel.getReceiver_id().equals(mSharedPrefManager.getUserDate().getId())) {
                                otherPersonObject = messageObject.optJSONObject("get_sender_user");
                            }
                            if (otherPersonObject != null) {
                                allMessagesModel.setOther_person_id(otherPersonObject.optInt("id") + "");
                                allMessagesModel.setOther_person_image(otherPersonObject.optString("photo"));
                                allMessagesModel.setOther_person_name(otherPersonObject.optString("fullname"));
                            }
                            messagesList.add(allMessagesModel);
                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (messagesList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            noDataTextView.setText("لا يوجد رسائل");
        } else {
            mMessagesAdapter.addMessagesList(messagesList);
            noDataLayout.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }
}