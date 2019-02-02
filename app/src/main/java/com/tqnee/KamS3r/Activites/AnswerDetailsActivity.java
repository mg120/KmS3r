package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Adapters.AnswerCommentsAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Model.AnswerCommentModel;
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

public class AnswerDetailsActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    AnswerCommentsAdapter mAnswerCommentsAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;

    @BindView(R.id.et_message_content)
    EditText et_message_content;

    @BindView(R.id.btn_send)
    Button btn_send;

    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;

    String answer_id;
    ArrayList<AnswerCommentModel> commentsList;
    int last_position = 0;
    String comment;

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.answer_comments));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        if (getIntent().hasExtra("answer_id")) {
            answer_id = getIntent().getStringExtra("answer_id");
        }

        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mAnswerCommentsAdapter = new AnswerCommentsAdapter(mContext);
        mRecycler.setAdapter(mAnswerCommentsAdapter);
        loadComments(Constants.answerCommentsUrl);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_answer_details;
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

    @OnClick(R.id.btn_send)
    void addComment() {
        if (mSharedPrefManager.getLoginStatus()) {
            if (et_message_content.getText().toString().trim().isEmpty()) {
                Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_enter_your_comment), R.color.colorError);
            } else {
                AnswerCommentModel commentModel = new AnswerCommentModel();
                commentModel.setComment_user_id(mSharedPrefManager.getUserDate().getId());
                commentModel.setComment_content(et_message_content.getText().toString());
                commentModel.setComment_user_name(mSharedPrefManager.getUserDate().getName());
                commentModel.setComment_user_image(mSharedPrefManager.getUserDate().getPhoto());
                mAnswerCommentsAdapter.insertItem(commentModel, mAnswerCommentsAdapter.getItemCount());
                last_position = mAnswerCommentsAdapter.getItemCount() - 1;
                mAnswerCommentsAdapter.setSelectedItem(last_position);
                comment = et_message_content.getText().toString().trim();
                et_message_content.setText("");
                mRecycler.scrollToPosition(last_position);
                btn_send.setEnabled(false);
                createComment(Constants.addCommentUrl);
            }
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    private void createComment(String serviceUrl) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(addComment)", response);
                        btn_send.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                params.put("answer_id", answer_id);
                params.put("comment", comment);

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

    public void loadComments(String serviceUrl) {
//        loading_progress.show();
        commentsList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loading_progress.hide();
                        Utils.printLog(Constants.LOG_TAG + "(answers)", response);
                        parseCommentsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loading_progress.hide();
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
                params.put("answer_id", answer_id);
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

    private void parseCommentsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                if (jsonObject.has("comments")) {
                    JSONObject commentsObject = jsonObject.optJSONObject("comments");
                    JSONArray commentsArray = commentsObject.optJSONArray("data");
                    for (int i = 0; i < commentsArray.length(); i++) {
                        AnswerCommentModel answerCommentModel = new AnswerCommentModel();
                        JSONObject jsonObject1 = commentsArray.optJSONObject(i);
                        answerCommentModel.setComment_id(String.valueOf(jsonObject1.optInt("id")));
                        answerCommentModel.setComment_content(jsonObject1.optString("comment"));
                        answerCommentModel.setComment_user_id(jsonObject1.optJSONObject("get_user").optString("id"));
                        answerCommentModel.setComment_user_image(jsonObject1.optJSONObject("get_user").optString("photo"));
                        answerCommentModel.setComment_user_name(jsonObject1.optJSONObject("get_user").optString("fullname"));
                        commentsList.add(answerCommentModel);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (commentsList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            noDataTextView.setText("لا يوجد تعليقات");
        } else {
            mAnswerCommentsAdapter.addCommentList(commentsList);
            noDataLayout.setVisibility(View.GONE);
        }
    }
}
