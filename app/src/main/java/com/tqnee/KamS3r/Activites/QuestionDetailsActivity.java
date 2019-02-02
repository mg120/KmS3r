package com.tqnee.KamS3r.Activites;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Adapters.AnswersAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IAddAnswer;
import com.tqnee.KamS3r.Interfaces.IAnswerItemClicked;
import com.tqnee.KamS3r.Interfaces.IReportItemListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.AnswersModel;
import com.tqnee.KamS3r.Model.QuestionModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.AddAnswerDialog;
import com.tqnee.KamS3r.Widgets.AddingMessageDialog;
import com.tqnee.KamS3r.Widgets.AddingReportDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class QuestionDetailsActivity extends ParentActivity implements IAnswerItemClicked, ISendingMessagesListener, IReportItemListener, IAddAnswer {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    AnswersAdapter mAnswersAdapter;
    LinearLayoutManager mLinearLayoutManager;
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    AddingReportDialog mAddingReportDialog;
    AddAnswerDialog mAddAnswerDialog;
    Intent mIntent;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.ques_details_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_country)
    TextView tv_country;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_offers)
    TextView tv_offers;
    @BindView(R.id.tv_question_title)
    TextView tv_question_title;
    @BindView(R.id.iv_question_image)
    ImageView iv_question_image;
    @BindView(R.id.iv_user_image)
    CircleImageView iv_user_image;
    @BindView(R.id.iv_favorite)
    ImageView iv_favorite;
    @BindView(R.id.iv_option_menu)
    ImageView iv_option_menu;
    @BindView(R.id.iv_message)
    ImageView iv_message;
    @BindView(R.id.likes_counter_text_view)
    TextView favoriteCounterTextView;
    @BindView(R.id.answers_counter_text_view)
    TextView answersCounterTextView;
    @BindView(R.id.add_answer_button)
    Button addAmswerButton;
    @BindView(R.id.ques_details_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    QuestionModel questionsModel;

    String question_id;
    boolean isFav = false;
    ArrayList<AnswersModel> answersList;
    PopupMenu popup;
    IReportItemListener mIReportItemListener;

    MenuItem edit_ad, delete_ad, report;

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.question_details));
        mContext = this;

        mIReportItemListener = this;
        popup = new PopupMenu(mContext, iv_option_menu);
        popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
        edit_ad = popup.getMenu().findItem(R.id.edit_ad);
        delete_ad = popup.getMenu().findItem(R.id.delete_ad);
        report = popup.getMenu().findItem(R.id.report);

        mSharedPrefManager = new SharedPrefManager(mContext);
        if (getIntent().hasExtra("question_id")) {
            question_id = getIntent().getStringExtra("question_id");
        }
        loadQuestionDetails(Constants.askDetailsUrl);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mAnswersAdapter = new AnswersAdapter(mContext, this);
        mRecycler.setAdapter(mAnswersAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadQuestionDetails(Constants.askDetailsUrl);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_question_details;
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
        if (answersList != null) {
            mIntent = new Intent(mContext, AnswerDetailsActivity.class);
            mIntent.putExtra("answer_id", answersList.get(position).getAnswer_id());
            startActivity(mIntent);
        }
    }

    @Override
    public void onAnswerFavouriteClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            answersList.get(position).setFav(!answersList.get(position).isFav());
            mAnswersAdapter.notifyDataSetChanged();
            addToFavourite(Constants.addToFavouriteUrl, answersList.get(position).getAnswer_id(), Constants.ANSWER_TYPE);
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @OnClick(R.id.iv_favorite)
    void onFavouriteClicked() {
        if (mSharedPrefManager.getLoginStatus()) {
            isFav = !isFav;
            int likesCounter = Integer.parseInt(questionsModel.getFavorites_count());
            if (isFav) {
                iv_favorite.setImageResource(R.mipmap.like_filled_icon);
                questionsModel.setFavorites_count("" + (likesCounter + 1));
            } else {
                iv_favorite.setImageResource(R.mipmap.like_empty_icon);
                questionsModel.setFavorites_count("" + (likesCounter - 1));
            }
            if (questionsModel.getFavorites_count().equals("0"))
                favoriteCounterTextView.setText("");
            else
                favoriteCounterTextView.setText(questionsModel.getFavorites_count());
            addToFavourite(Constants.addToFavouriteUrl, question_id, Constants.QUESTION_TYPE);
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @OnClick(R.id.iv_statistical)
    void onStatisticalClicked() {
        mIntent = new Intent(mContext, QuestionStatisticalActivity.class);
        mIntent.putExtra("question_id", question_id);
        mContext.startActivity(mIntent);
    }

    @OnClick(R.id.iv_message)
    void onMessageClicked() {
        if (mSharedPrefManager.getLoginStatus()) {
            if (questionsModel != null) {
                mAddingMessageDialog = new AddingMessageDialog(mContext, questionsModel.getQuestion_user_id(), this);
                mAddingMessageDialog.show();
            }
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @OnClick(R.id.iv_share)
    void onShareClicked() {
        if (questionsModel != null) {
            Utils.Share(mContext, getString(R.string.question_share), Constants.shareAskUrl + question_id);
        }
    }

    @OnClick(R.id.iv_answer)
    void onAnswerClicked() {
        if (mSharedPrefManager.getLoginStatus()) {
            mAddAnswerDialog = new AddAnswerDialog(mContext, question_id, this);
            mAddAnswerDialog.show();
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @OnClick(R.id.add_answer_button)
    void onAnswerButtonClicked() {
        onAnswerClicked();
    }

    @OnClick(R.id.iv_option_menu)
    void onOptionMenuClicked() {
        if (questionsModel.getQuestion_user_id().equals(mSharedPrefManager.getUserDate().getId())) {
            edit_ad.setVisible(true);
            delete_ad.setVisible(true);
            report.setVisible(false);
        } else {
            edit_ad.setVisible(false);
            delete_ad.setVisible(false);
            report.setVisible(true);
        }
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.report) {
                    popup.dismiss();
                    if (mSharedPrefManager.getLoginStatus()) {
                        mAddingReportDialog = new AddingReportDialog(mContext, question_id, Constants.QUESTION_TYPE, mIReportItemListener);
                        mAddingReportDialog.show();
                    } else {
                        Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
                    }
                } else if (item.getItemId() == R.id.edit_ad) {
                    Intent intent = new Intent(QuestionDetailsActivity.this, EditQuestion.class);
                    intent.putExtra("ques_id", questionsModel.getQuestion_id());
//                    intent.putExtra("ques_title", questionsModel.getQuestion_title());
//                    intent.putExtra("ques_img", questionsModel.getQuestion_image());
//                    intent.putExtra("ques_time", questionsModel.getQuestion_time());
//                    intent.putExtra("ques_ans_count", questionsModel.getQuestion_answers_count());
//                    intent.putExtra("ques_fav_count", questionsModel.getFavorites_count());
                    startActivity(intent);
                } else if (item.getItemId() == R.id.delete_ad) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetailsActivity.this);
                    builder.setIcon(R.mipmap.splash_logo)
                            .setMessage("تأكيد حذف السؤال ؟")
                            .setCancelable(false)
                            .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    delete_Ques(question_id);
                                }
                            }).setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                return false;
            }
        });

    }

    private void delete_Ques(final String question_id) {
        StringRequest delete_Request = new StringRequest(Request.Method.POST, Constants.askDelete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("response").equals("true")) {
                        Toasty.success(mContext, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toasty.error(mContext, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ask_id", question_id);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                return params;
            }
        };

        System.out.println("URL : " + delete_Request.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        delete_Request.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(delete_Request);
    }

    @OnClick(R.id.tv_offers)
    void onOffersClicked() {
        mIntent = new Intent(mContext, OffersActivity.class);
        mIntent.putExtra("question_id", question_id);
        mContext.startActivity(mIntent);
    }

    @OnClick(R.id.iv_question_image)
    void onImageClicked() {

    }

    @OnClick(R.id.iv_user_image)
    void onUserClicked() {
        if (questionsModel != null) {
            mIntent = new Intent(mContext, ProfilePreviewActivity.class);
            mIntent.putExtra("user_id", questionsModel.getQuestion_user_id());
            startActivity(mIntent);
        }
    }

    @OnClick(R.id.tv_user_name)
    void onNameClicked() {
        if (questionsModel != null) {
            mIntent = new Intent(mContext, ProfilePreviewActivity.class);
            mIntent.putExtra("user_id", questionsModel.getQuestion_user_id());
            startActivity(mIntent);
        }
    }

    @OnClick(R.id.tv_all_answer)
    void showAllAnswers() {
        mIntent = new Intent(mContext, AnswersActivity.class);
        mIntent.putExtra("question_id", question_id);
        startActivity(mIntent);
    }


    @Override
    public void onReportItem(String item_id, String item_type, String message) {
        mAddingReportDialog.dismiss();
        reportingItem(Constants.reportItemUrl, item_id, item_type, message);
    }

    @Override
    public void onSendMessage(String user_id, String message) {
        mAddingMessageDialog.dismiss();
        sendingMessage(Constants.sendingMessageUrl, user_id, message);
    }

    private void loadQuestionDetails(String serviceUrl) {
//        mCustomLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(questions)", response);
                        parseQuestionsResponse(response);
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
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                params.put("ask_id", question_id);
                return params;
            }
        };
        System.out.println("URL : " + verifyReq.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private void parseQuestionsResponse(String response) {
        answersList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                JSONObject questionObject = jsonObject.optJSONObject("ask");
                questionsModel = new QuestionModel();
                questionsModel.setQuestion_id(String.valueOf(questionObject.optInt("id")));
                questionsModel.setQuestion_title(questionObject.optString("ask"));
                questionsModel.setQuestion_image(questionObject.optString("ask_photo"));
                questionsModel.setQuestion_answers_count(questionObject.optString("answer_count"));
                questionsModel.setQuestion_time(questionObject.optString("time"));

                questionsModel.setQuestion_offers_count(questionObject.optString("offer_count"));
                questionsModel.setQuestion_user_id(String.valueOf(questionObject.optJSONObject("get_user").optInt("id")));
                questionsModel.setQuestion_user_image(questionObject.optJSONObject("get_user").optString("photo"));
                questionsModel.setQuestion_username(questionObject.optJSONObject("get_user").optString("fullname"));
                questionsModel.setFavorites_count(questionObject.optString("fav_count"));
                if (questionObject.has("isFav")) {
                    if (questionObject.optString("isFav").equals("1")) {
                        questionsModel.setFavourite(true);
                    } else {
                        questionsModel.setFavourite(false);
                    }
                }
                if (questionObject.has("get_answers")) {
                    JSONArray answersArray = questionObject.optJSONArray("get_answers");
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
                tv_user_name.setText(questionsModel.getQuestion_username());
                tv_country.setText(getIntent().getStringExtra("state"));
                tv_time.setText(questionsModel.getQuestion_time());
                tv_offers.setText(mContext.getString(R.string.offers) + " (" + questionsModel.getQuestion_offers_count() + ")");
                if (!questionsModel.getFavorites_count().equals("0"))
                    favoriteCounterTextView.setText(questionsModel.getFavorites_count());
                else
                    favoriteCounterTextView.setText("");
                if (!questionsModel.getQuestion_answers_count().equals("0"))
                    answersCounterTextView.setText(questionsModel.getQuestion_answers_count());

                tv_question_title.setText(questionsModel.getQuestion_title());
                tv_question_title.setMaxLines(5);
                tv_time.setText(questionsModel.getQuestion_time());
                isFav = questionsModel.isFavourite();
                if (isFav) {
                    iv_favorite.setImageResource(R.mipmap.like_filled_icon);
                } else {
                    iv_favorite.setImageResource(R.mipmap.like_empty_icon);
                }
                Glide.with(mContext)
                        .load(Constants.imagesBaseUrl + questionsModel.getQuestion_image())
                        .placeholder(R.mipmap.question_image_placeholder) // can also be a drawable
                        .error(R.mipmap.question_image_placeholder) // will be displayed if the image cannot be loaded
                        .into(iv_question_image);
                Glide.with(mContext)
                        .load(questionsModel.getQuestion_user_image())
                        .placeholder(R.mipmap.person_place_holder) // can also be a drawable
                        .error(R.mipmap.person_place_holder) // will be displayed if the image cannot be loaded
                        .into(iv_user_image);
                if (mSharedPrefManager.getUserDate().getId().equals(questionsModel.getQuestion_user_id())) {
                    iv_message.setVisibility(View.GONE);
                } else {
                    iv_message.setVisibility(View.VISIBLE);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAnswersAdapter.addAnswersList(answersList);
        mAnswersAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
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

    private void addToFavourite(String serviceUrl, final String item_id, final String item_type) {
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
                params.put("type", item_type);
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

    @Override
    public void onAnswerAdd() {
        loadQuestionDetails(Constants.askDetailsUrl);

    }
}
