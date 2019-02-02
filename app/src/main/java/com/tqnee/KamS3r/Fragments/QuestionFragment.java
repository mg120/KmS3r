package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.OffersActivity;
import com.tqnee.KamS3r.Activites.ProfilePreviewActivity;
import com.tqnee.KamS3r.Activites.QuestionDetailsActivity;
import com.tqnee.KamS3r.Activites.QuestionStatisticalActivity;
import com.tqnee.KamS3r.Adapters.EndlessRecyclerOnScrollListener;
import com.tqnee.KamS3r.Adapters.QuestionAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Interfaces.IQuestionOperationsListener;
import com.tqnee.KamS3r.Interfaces.IReportItemListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.Model.QuestionModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.AddAnswerDialog;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.AddingReportDialog;
import com.tqnee.KamS3r.Widgets.CategoriesCustomDialog;
import com.tqnee.KamS3r.Widgets.ImagePreviewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ramzy on 9/13/2017.
 */

public class QuestionFragment extends BaseFragment implements IQuestionOperationsListener, ICategoryItemClicked, ISendingMessagesListener, IReportItemListener {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.layout_choose_category)
    RelativeLayout layout_choose_category;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    //    @BindView(R.id.loading_progress)
//    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.questions_frag_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.iv_category_icon)
    ImageView iv_category_icon;
    @BindView(R.id.tv_category_name)
    TextView tv_category_name;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;
    @BindView(R.id.ques_auto_complete_txtV_id)
    AutoCompleteTextView search_box;
    QuestionAdapter mQuestionAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SharedPrefManager mSharedPrefManager;

    Context mContext;
    Intent mIntent;

    AddAnswerDialog mAddAnswerDialog;
    CategoriesCustomDialog mCategoriesCustomDialog;
    ArrayList<QuestionModel> questionsList;
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    AddingReportDialog mAddingReportDialog;
    ImagePreviewDialog mImagePreviewDialog;
    int last_page = 0;
    private int current = 1;
    boolean isRefreshing = false;
    private CategoriesModel categoryModel = null;
    boolean isCategoryClicked = false;
    ArrayList<String> suggest_list = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_questions;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        mSharedPrefManager = new SharedPrefManager(mContext);
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        questionsList = new ArrayList<>();
        current = 1;
        last_page = 0;
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mQuestionAdapter = new QuestionAdapter(mContext, mSharedPrefManager, this);
        mRecycler.setAdapter(mQuestionAdapter);

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search_box.setText("");
                current = 1;
                last_page = 0;
                isRefreshing = true;
                if (categoryModel != null)
                    loadQuestions(Constants.asksUrl, true, categoryModel);
                else
                    loadQuestions(Constants.asksUrl, false, null);
                setupPagination();
            }
        });
        setupPagination();
    }

    private void setupPagination() {
        mRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                current += 1;
                if (last_page >= current_page) {
                    if (!isCategoryClicked)
                        loadMoreQuestion(Constants.asksUrl, "1");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isCategoryClicked) {
            loadQuestions(Constants.asksUrl, false, null);
        }
    }

    private void loadMoreQuestion(String serviceURL, final String category_id) {
//        mCustomLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            isCategoryClicked = false;
                            System.out.println("Response : " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("response").equals("true")) {
                                JSONObject dateObject = jsonObject.optJSONObject("data");
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

                                        questionsModel.setQuestion_image(questionObject.optString("ask_photo"));
                                        questionsModel.setQuestion_answers_count(questionObject.optInt("answer_count") + "");
                                        questionsModel.setQuestion_user_id(String.valueOf(questionObject.optJSONObject("get_user").optInt("id")));
                                        questionsModel.setQuestion_user_image(questionObject.optJSONObject("get_user").optString("photo"));
                                        questionsModel.setQuestion_username(questionObject.optJSONObject("get_user").optString("fullname"));

                                        questionsList.add(questionsModel);
                                    }
                                }
                            }
                            for (int i = suggest_list.size(); i < questionsList.size(); i++) {
                                suggest_list.add(questionsList.get(i).getQuestion_title());
                            }
                            if (getActivity() != null) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest_list);
                                search_box.setAdapter(adapter);
                            }
                            search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = (TextView) view;
                                    searchForQuestion(Constants.searchUrl, textView.getText().toString());
                                }
                            });

                            search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                        searchClicked();
                                        return true;
                                    }
                                    return false;
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        mQuestionAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isCategoryClicked = false;
//                        mCustomLoadingDialog.hide();
                        progressBar.setVisibility(View.GONE);
                        if (mContext != null && isAdded()) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, tv_category_name, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, tv_category_name, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, tv_category_name, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (!category_id.equals("1")) {
                    params.put("id", category_id);
                }
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                params.put("page", current + "");
                params.put("country_code", mSharedPrefManager.getCountryCode());
                System.out.println("Parameters : " + params);
                return params;
            }
        };


        System.out.println(verifyReq.getUrl());
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

        mIntent = new Intent(mContext, QuestionDetailsActivity.class);
        mIntent.putExtra("question_id", questionsList.get(position).getQuestion_id());
        mIntent.putExtra("state", questionsList.get(position).getQuestion_user_state());
        mContext.startActivity(mIntent);

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
        mIntent.putExtra("state", questionsList.get(position).getQuestion_user_state());
        mContext.startActivity(mIntent);
    }

    @Override
    public void onImageClicked(int position) {
        mImagePreviewDialog = new ImagePreviewDialog(mContext, questionsList.get(position).getQuestion_image());
        mImagePreviewDialog.setCancelable(true);
        mImagePreviewDialog.show();
    }

    @Override
    public void onUserClicked(int position) {
        mIntent = new Intent(mContext, ProfilePreviewActivity.class);
        mIntent.putExtra("user_id", questionsList.get(position).getQuestion_user_id());
        startActivity(mIntent);
    }

    @OnClick(R.id.search_btn_id)
    void searchClicked() {
        if (search_box.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.enter_search_word), R.color.colorError);
        } else {
            searchForQuestion(Constants.searchUrl, search_box.getText().toString().trim());
        }
    }

    private void searchForQuestion(String searchUrl, final String txt) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, searchUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response_search:: " + response);
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(searchResult)", response);
                        parseSearchResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("errorsLog", error.toString());
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
                params.put("txt", txt);
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                System.out.println("Parameters :: " + params);
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

    private void parseSearchResult(String response) {
        try {
            questionsList.clear();
            Log.e("result:: ", response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optBoolean("response")) {
                JSONObject dateObject = jsonObject.optJSONObject("results");
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

                        questionsModel.setQuestion_image(questionObject.optString("ask_photo"));
                        questionsModel.setQuestion_answers_count(questionObject.optInt("answer_count") + "");
                        questionsModel.setQuestion_user_id(String.valueOf(questionObject.optJSONObject("get_user").optInt("id")));
                        questionsModel.setQuestion_user_image(questionObject.optJSONObject("get_user").optString("photo"));
                        questionsModel.setQuestion_username(questionObject.optJSONObject("get_user").optString("fullname"));
                        questionsList.add(questionsModel);
                    }
                }
            }
            if (questionsList.isEmpty()) {
                noDataLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText("لا توجد نتائج للبحث ");
            } else {
                mQuestionAdapter.addQuestionsList(questionsList);
                noDataLayout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.layout_choose_category)
    void chooseCategory() {
        mCategoriesCustomDialog = new CategoriesCustomDialog(mContext, this);
        mCategoriesCustomDialog.show();
    }

    @Override
    public void onCategoryItemClicked(CategoriesModel categoriesModel, Boolean isCategorySelected) {
        isCategoryClicked = true;
        questionsList.clear();
        mQuestionAdapter.notifyDataSetChanged();
        mCategoriesCustomDialog.dismiss();
        current = 1;
        if (isCategorySelected) {
            tv_category_name.setText(categoriesModel.getCategory_name());
            Glide.with(mContext)
                    .load(Constants.imagesBaseUrl + categoriesModel.getCategory_image())
                    .placeholder(R.drawable.category_placeholder) // can also be a drawable
                    .error(R.drawable.category_placeholder) // will be displayed if the image cannot be loaded
                    .into(iv_category_icon);
            this.categoryModel = categoriesModel;
        } else {
            tv_category_name.setText(categoriesModel.getCategory_name());
            iv_category_icon.setImageResource(R.drawable.category_placeholder);
            this.categoryModel = null;
        }
        loadQuestions(Constants.asksUrl, isCategorySelected, categoriesModel);
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

    public void loadQuestions(String serviceUrl, final boolean isCategoryChosen, final CategoriesModel categoriesModel) {
//        loading_progress.show();
        progressBar.setVisibility(View.VISIBLE);
        questionsList.clear();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isCategoryClicked = false;
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(questions)", response);
                        parseQuestionsResponse(response);

                        if (isRefreshing) {
                            swipe_layout.setRefreshing(false);
                            isRefreshing = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isCategoryClicked = false;
                        progressBar.setVisibility(View.GONE);
                        Log.e("errorsLog", error.toString());
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
                if (isCategoryChosen) {
                    params.put("id", categoriesModel.getCategory_id());//
                }
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
//                params.put("page", String.valueOf(current));
                params.put("country_code", mSharedPrefManager.getCountryCode());

                System.out.println("Parameters : " + params);
                return params;
            }
        };


        System.out.println(verifyReq.getUrl());
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
            if (jsonObject.optBoolean("response")) {
                JSONObject dateObject = jsonObject.optJSONObject("data");
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

                        questionsModel.setQuestion_image(questionObject.optString("ask_photo"));
                        questionsModel.setQuestion_answers_count(questionObject.optInt("answer_count") + "");
                        questionsModel.setQuestion_user_id(String.valueOf(questionObject.optJSONObject("get_user").optInt("id")));
                        questionsModel.setQuestion_user_image(questionObject.optJSONObject("get_user").optString("photo"));
                        questionsModel.setQuestion_username(questionObject.optJSONObject("get_user").optString("fullname"));
                        questionsModel.setFavorites_count(questionObject.optString("fav_count"));
                        questionsModel.setQuestion_answers_count(questionObject.optString("answer_count"));
                        if (!questionObject.isNull("get_state"))
                            questionsModel.setQuestion_user_state(questionObject.getJSONObject("get_state").getString("ar_name"));
                        questionsList.add(questionsModel);
                    }
                }
            }
            if (questionsList.isEmpty()) {
                noDataLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText("لا يوجد أسئلة عن " + tv_category_name.getText());
            } else {
                mQuestionAdapter.addQuestionsList(questionsList);
                noDataLayout.setVisibility(View.GONE);
            }
            for (int i = suggest_list.size(); i < questionsList.size(); i++) {
                suggest_list.add(questionsList.get(i).getQuestion_title());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest_list);
            search_box.setAdapter(adapter);
            search_box.setThreshold(1);
            search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    searchForQuestion(Constants.searchUrl, textView.getText().toString());
                }
            });

            search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchClicked();
                        return true;
                    }
                    return false;
                }
            });
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
//                        mCustomLoadingDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(sendMessage)", response);
                        Utils.showSnackBar(mContext, layout_content, getString(R.string.message_sent_successfully), R.color.colorCorrect);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
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
//        mCustomLoadingDialog.show();
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

}

