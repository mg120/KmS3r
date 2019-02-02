package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
import com.tqnee.KamS3r.Adapters.InternalMessageAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Model.MessageModel;
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
import butterknife.OnClick;

public class InternalMessageActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    InternalMessageAdapter mInternalMessageAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.et_message_content)
    EditText et_message_content;
    @BindView(R.id.btn_send)
    Button btn_send;

    String message_id;
    String receiver_id;

    int last_position = 0;
    String message;
    ArrayList<MessageModel> conversationList;


    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.messages_title));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        message_id = getIntent().getStringExtra("message_id");
        receiver_id = getIntent().getStringExtra("receiver_id");
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mInternalMessageAdapter = new InternalMessageAdapter(mContext, mSharedPrefManager);
        mRecycler.setAdapter(mInternalMessageAdapter);
        loadMessages(Constants.internalMessageUrl);

    }

    @OnClick(R.id.btn_send)
    void sendMessage() {
        if (et_message_content.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_enter_your_message), R.color.colorError);
        } else {
            MessageModel messageModel = new MessageModel();
            messageModel.setMessage_content(et_message_content.getText().toString().trim());
            messageModel.setSender_user_id(mSharedPrefManager.getUserDate().getId());
            mInternalMessageAdapter.insertItem(messageModel, mInternalMessageAdapter.getItemCount());
            last_position = mInternalMessageAdapter.getItemCount() - 1;
            mInternalMessageAdapter.setSelectedItem(last_position);
            message = et_message_content.getText().toString().trim();
            et_message_content.setText("");
            mRecycler.scrollToPosition(last_position);
            btn_send.setEnabled(false);
            sendMessageToConversation(Constants.sendingMessageUrl);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_internal_message;
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

    private void loadMessages(String serviceURL) {
        conversationList = new ArrayList<>();
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(messages)", response);
                        parseInternalMessageResponse(response);

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
                params.put("message_id", message_id);
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

    private void parseInternalMessageResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("response")) {
                if (jsonObject.optString("response").equals("true")) {
                    JSONObject jsonObject1 = jsonObject.optJSONObject("messages");
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject messageObject = jsonArray.optJSONObject(i);
                        MessageModel messageModel = new MessageModel();
                        messageModel.setMessage_id(messageObject.optString("id"));
                        messageModel.setSender_user_id(messageObject.optString("sender_id"));
                        messageModel.setMessage_content(messageObject.optString("message"));
                        messageModel.setSender_user_name(messageObject.optString("sender"));
                        messageModel.setSender_user_image(messageObject.optString("photo"));
                        messageModel.setSender_time(messageObject.optString("time"));
                        conversationList.add(messageModel);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mInternalMessageAdapter.addMessageList(conversationList);
        last_position = mInternalMessageAdapter.getItemCount() - 1;
        mInternalMessageAdapter.setSelectedItem(last_position);
        mRecycler.scrollToPosition(last_position);
        progressBar.setVisibility(View.GONE);
    }

    private void sendMessageToConversation(String serviceUrl) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(sendMessage)", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response")) {
                                if (jsonObject.optString("response").equals("true")) {
                                    btn_send.setEnabled(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btn_send.setEnabled(true);
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
                params.put("receiver_id", receiver_id);
                params.put("message", message);
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
