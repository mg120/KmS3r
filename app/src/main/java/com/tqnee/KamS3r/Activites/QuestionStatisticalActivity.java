package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

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
import com.tqnee.KamS3r.Adapters.QuestionStatisticalAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Model.QuestionStatisticalModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class QuestionStatisticalActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    QuestionStatisticalAdapter mQuestionStatisticalAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.loading_progress)
    AVLoadingIndicatorView loading_progress;

    String question_id;
    ArrayList<QuestionStatisticalModel> statisticalList;

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.statistical));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        if (getIntent().hasExtra("question_id")) {
            question_id = getIntent().getStringExtra("question_id");
        }
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mQuestionStatisticalAdapter = new QuestionStatisticalAdapter(mContext);
        mRecycler.setAdapter(mQuestionStatisticalAdapter);
        loadQuestionStatistical(Constants.questionStatisticalUrl);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_question_statistical;
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

    private void loadQuestionStatistical(String serviceUrl) {
//        loading_progress.show();
        statisticalList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loading_progress.hide();
                        Utils.printLog(Constants.LOG_TAG + "(statistical)", response);
                        parseQuestionsStatisticalResponse(response);
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
                params.put("ask_id", question_id);
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

    private void parseQuestionsStatisticalResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                if (jsonObject.has("rates")) {
                    JSONArray dataArray = jsonObject.optJSONArray("rates");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject statisticalObject = dataArray.optJSONObject(i);
                        QuestionStatisticalModel statisticalModel = new QuestionStatisticalModel();

                        statisticalModel.setStatistical_currency(statisticalObject.optString("CURRENCY_SIGN"));
                        statisticalModel.setStatistical_answer_count(statisticalObject.optString("PRICE_COUNT_PER_ASK"));
                        statisticalModel.setStatistical_title(statisticalObject.optString("PRICE"));
                        statisticalModel.setStatistical_percentage(statisticalObject.optString("PRICE_PERCRNTAGE_PER_ASK"));
                        statisticalList.add(statisticalModel);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQuestionStatisticalAdapter.addStatisticalList(statisticalList);

    }
}
