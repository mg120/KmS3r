package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.tqnee.KamS3r.Adapters.AnswersAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IAnswerItemClicked;
import com.tqnee.KamS3r.Model.AnswersModel;
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

public class AnswersActivity extends ParentActivity implements IAnswerItemClicked {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    AnswersAdapter mAnswerAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;

    String question_id;

    ArrayList<AnswersModel> answersList;


    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.answers));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        if (getIntent().hasExtra("question_id")) {
            question_id = getIntent().getStringExtra("question_id");
        }

        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mAnswerAdapter = new AnswersAdapter(mContext, this);
        mRecycler.setAdapter(mAnswerAdapter);
        loadAnswers(Constants.askAnswersUrl);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_answers;
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
    public void onCommentIconClicked(int position) {
        Intent intent = new Intent(mContext, AnswerDetailsActivity.class);
        intent.putExtra("answer_id", answersList.get(position).getAnswer_id());
        startActivity(intent);
    }

    @Override
    public void onAnswerFavouriteClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            answersList.get(position).setFav(!answersList.get(position).isFav());
            mAnswerAdapter.notifyDataSetChanged();
            addToFavourite(Constants.addToFavouriteUrl, answersList.get(position).getAnswer_id());
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    public void loadAnswers(String serviceUrl) {
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        answersList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(answers)", response);
                        parseAnswersResponse(response);
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

    private void parseAnswersResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                if (jsonObject.has("answers")) {
                    JSONObject answerObject = jsonObject.optJSONObject("answers");
                    JSONArray answersArray = answerObject.optJSONArray("data");
                    for (int i = 0; i < answersArray.length(); i++) {
                        AnswersModel answersModel = new AnswersModel();
                        JSONObject jsonObject1 = answersArray.optJSONObject(i);
                        answersModel.setAnswer_id(String.valueOf(jsonObject1.optInt("id")));
                        answersModel.setAnswer_price(jsonObject1.optString("price"));
                        answersModel.setAnswer_content(jsonObject1.optString("about"));
                        answersModel.setAnswer_currency(jsonObject1.optString("currency_sign"));
                        answersModel.setComments_number(jsonObject1.optString("comment_count"));
                        answersModel.setAnswer_user_id(String.valueOf(jsonObject1.optJSONObject("get_user").optInt("id")));
                        answersModel.setAnswer_user_name(jsonObject1.optJSONObject("get_user").optString("fullname"));
                        answersModel.setAnswer_user_image(jsonObject1.optJSONObject("get_user").optString("photo"));
                        if (jsonObject1.has("isFav")) {
                            if (jsonObject1.optString("isFav").equals("1")) {
                                answersModel.setFav(true);
                            } else {
                                answersModel.setFav(false);
                            }
                        }
                        answersList.add(answersModel);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAnswerAdapter.addAnswersList(answersList);
    }

    private void addToFavourite(String serviceUrl, final String item_id) {
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
                params.put("item_id", item_id);
                params.put("type", Constants.ANSWER_TYPE);
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
