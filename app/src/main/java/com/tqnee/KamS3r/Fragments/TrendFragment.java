package com.tqnee.KamS3r.Fragments;

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
import com.tqnee.KamS3r.Activites.OffersActivity;
import com.tqnee.KamS3r.Activites.ProfilePreviewActivity;
import com.tqnee.KamS3r.Activites.QuestionDetailsActivity;
import com.tqnee.KamS3r.Activites.QuestionStatisticalActivity;
import com.tqnee.KamS3r.Adapters.QuestionAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.Interfaces.IAddAnswer;
import com.tqnee.KamS3r.Interfaces.IQuestionOperationsListener;
import com.tqnee.KamS3r.Interfaces.IReportItemListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.QuestionModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.AddAnswerDialog;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.AddingReportDialog;
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

public class TrendFragment extends BaseFragment implements IQuestionOperationsListener, ISendingMessagesListener, IReportItemListener, IAddAnswer {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    RelativeLayout layout_content;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.recycler_progress_id)
    ProgressBar progressBar;
    QuestionAdapter mQuestionAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SharedPrefManager mSharedPrefManager;
    Context mContext;
    Intent mIntent;

    AddAnswerDialog mAddAnswerDialog;
    ArrayList<QuestionModel> questionsList;
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    AddingReportDialog mAddingReportDialog;
    ImagePreviewDialog mImagePreviewDialog;
    int last_page = 0;
    private int current = 1;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        mSharedPrefManager = new SharedPrefManager(mContext);
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        current = 1;
        last_page = 0;
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mQuestionAdapter = new QuestionAdapter(mContext, mSharedPrefManager, this);
        mRecycler.setAdapter(mQuestionAdapter);
        loadQuestions(Constants.trendAsksUrl);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFavouriteClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            int likesCounter = Integer.parseInt(questionsList.get(position).getFavorites_count());
            if (questionsList.get(position).isFavourite())
                questionsList.get(position).setFavorites_count("" + (likesCounter - 1));
            else
                questionsList.get(position).setFavorites_count("" + (likesCounter + 1));
            questionsList.get(position).setFavourite(!questionsList.get(position).isFavourite());
            mQuestionAdapter.notifyDataSetChanged();
            addToFavourite(Constants.addToFavouriteUrl, questionsList.get(position).getQuestion_id());
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }

    }

    @Override
    public void onStatisticalClicked(int position) {
        mIntent = new Intent(mContext, QuestionStatisticalActivity.class);
        mIntent.putExtra("question_id", questionsList.get(position).getQuestion_id());
        mContext.startActivity(mIntent);
    }

    @Override
    public void onMessageClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddingMessageDialog = new AddingMessageDialog(mContext, questionsList.get(position).getQuestion_user_id(), this);
            mAddingMessageDialog.show();
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }

    }

    @Override
    public void onShareClicked(int position) {
        Utils.Share(mContext, getString(R.string.question_share), Constants.shareAskUrl + questionsList.get(position).getQuestion_id());
    }

    @Override
    public void onAnswerClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddAnswerDialog = new AddAnswerDialog(mContext, questionsList.get(position).getQuestion_id(), this);
            mAddAnswerDialog.show();
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }

    }

    @Override
    public void onOptionMenuClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddingReportDialog = new AddingReportDialog(mContext, questionsList.get(position).getQuestion_id(), Constants.QUESTION_TYPE, this);
            mAddingReportDialog.show();
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }

    }

    @Override
    public void onOffersClicked(int position) {
        mIntent = new Intent(mContext, OffersActivity.class);
        mIntent.putExtra("question_id", questionsList.get(position).getQuestion_id());
        mContext.startActivity(mIntent);

    }

    @Override
    public void onQuestionClicked(int position) {
        mIntent = new Intent(mContext, QuestionDetailsActivity.class);
        mIntent.putExtra("question_id", questionsList.get(position).getQuestion_id());
        mContext.startActivity(mIntent);
    }

    @Override
    public void onImageClicked(int position) {
        mImagePreviewDialog = new ImagePreviewDialog(mContext, questionsList.get(position).getQuestion_image());
        mImagePreviewDialog.show();
    }

    @Override
    public void onUserClicked(int position) {
        mIntent = new Intent(mContext, ProfilePreviewActivity.class);
        mIntent.putExtra("user_id", questionsList.get(position).getQuestion_user_id());
        startActivity(mIntent);
    }

    @Override
    public void onSendMessage(String user_id, String message) {
        mAddingMessageDialog.dismiss();
        sendingMessage(Constants.sendingMessageUrl, user_id, message);
    }

    @Override
    public void onReportItem(String item_id, String item_type, String message) {
        mAddingReportDialog.dismiss();
        reportingItem(Constants.reportItemUrl, item_id, item_type, message);
    }

    public void loadQuestions(String serviceUrl) {
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        questionsList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(trend)", response);
                        parseQuestionsResponse(response);
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

                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                params.put("page", String.valueOf(current));
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

    private void parseQuestionsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                JSONObject dateObject = jsonObject.optJSONObject("asks");
                last_page = dateObject.optInt("last_page");
                if (dateObject.has("data")) {
                    JSONArray dataArray = dateObject.optJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject questionObject = dataArray.optJSONObject(i);
                        QuestionModel questionsModel = new QuestionModel();
                        if (questionObject.has("isFav")) {
                            if (questionObject.optInt("isFav") == 1) {
                                questionsModel.setFavourite(true);
                            } else {
                                questionsModel.setFavourite(false);
                            }
                        }
                        questionsModel.setQuestion_id(String.valueOf(questionObject.optInt("id")));
                        questionsModel.setQuestion_title(questionObject.optString("ask"));
                        questionsModel.setQuestion_time(questionObject.optString("time"));
                        questionsModel.setQuestion_offers_count(questionObject.optString("offer_count"));
                        questionsModel.setFavorites_count(questionObject.optString("fav_count"));

                        questionsModel.setQuestion_image(questionObject.optString("ask_photo"));
                        questionsModel.setQuestion_answers_count(questionObject.optInt("answer_count") + "");
                        questionsModel.setQuestion_user_id(String.valueOf(questionObject.optJSONObject("get_user").optInt("id")));
                        questionsModel.setQuestion_user_image(questionObject.optJSONObject("get_user").optString("photo"));
                        questionsModel.setQuestion_username(questionObject.optJSONObject("get_user").optString("fullname"));
                        questionsList.add(questionsModel);
                    }
                }
            }
            mQuestionAdapter.addQuestionsList(questionsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendingMessage(String serviceUrl, final String user_id, final String message) {
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

    private void addToFavourite(String serviceUrl, final String question_id) {
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

                params.put("item_id", question_id);
                params.put("type", Constants.QUESTION_TYPE);
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

    private void reportingItem(String serviceUrl, final String item_id, final String item_type, final String message) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(report)", response);
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
    public void onAnswerAdd() {

    }
}
