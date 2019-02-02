package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.ProfilePreviewActivity;
import com.tqnee.KamS3r.Adapters.EndlessRecyclerOnScrollListener;
import com.tqnee.KamS3r.Adapters.OffersAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Interfaces.IOffersOperationsListener;
import com.tqnee.KamS3r.Interfaces.IReportItemListener;
import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.Model.OfferModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
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
 * Created by ramzy on 9/14/2017.
 */

public class OffersFragment extends BaseFragment implements IOffersOperationsListener, ISendingMessagesListener, ICategoryItemClicked, IReportItemListener {
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
    @BindView(R.id.questions_frag_progress_id)
    ProgressBar progressBar;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;
    @BindView(R.id.ques_auto_complete_txtV_id)
    AutoCompleteTextView search_box;

    ArrayList<OfferModel> offersList = new ArrayList<OfferModel>();
    //    CustomLoadingDialog mCustomLoadingDialog;
    AddingMessageDialog mAddingMessageDialog;
    ImagePreviewDialog mImagePreviewDialog;
    @BindView(R.id.iv_category_icon)
    ImageView iv_category_icon;
    @BindView(R.id.tv_category_name)
    TextView tv_category_name;
    private CategoriesCustomDialog mCategoriesCustomDialog;
    int last_page = 0;
    private int current = 1;
    private boolean isRefreshing = false;
    private AddingReportDialog mAddingReportDialog;
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
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mOffersAdapter = new OffersAdapter(mContext, mSharedPrefManager, this);
        mRecycler.setAdapter(mOffersAdapter);
//        loadOffers(Constants.loadAllOffers+"?country_code="+mSharedPrefManager.getCountryCode(),false,null);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search_box.setText("");
                current = 1;
                last_page = 0;
                isRefreshing = true;
                if (categoryModel != null)
                    loadOffers(Constants.loadAllOffers + "?country_code=" + mSharedPrefManager.getCountryCode(), true, categoryModel);
                else
                    loadOffers(Constants.loadAllOffers + "?country_code=" + mSharedPrefManager.getCountryCode(), false, null);
                setupPagination();
            }

        });
        setupPagination();
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onFavouriteClicked(int position) {
        if (mSharedPrefManager.getLoginStatus()) {
            offersList.get(position).setFavourite(!offersList.get(position).isFavourite());
            mOffersAdapter.notifyDataSetChanged();
            addToFavourite(Constants.addToFavouriteUrl, offersList.get(position).getOffer_id());
        } else {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
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
        if (mSharedPrefManager.getLoginStatus()) {
            mAddingReportDialog = new AddingReportDialog(mContext, offersList.get(position).getOffer_id(), Constants.QUESTION_TYPE, this);
            mAddingReportDialog.show();
        } else {
            Utils.showSnackBarWithLong(mContext, layout_content, getString(R.string.you_should_login), R.color.colorError);
        }
    }

    @Override
    public void onReportItem(String item_id, String item_type, String message) {
        mAddingReportDialog.dismiss();
        reportingItem(Constants.reportItemUrl, item_id, item_type, message);
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

    @OnClick(R.id.search_btn_id)
    void searchClicked() {
        if (search_box.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, layout_content, getString(R.string.enter_search_word), R.color.colorError);
        } else {
            searchForOffer(Constants.offers_searchUrl, search_box.getText().toString().trim());
        }
    }

    @Override
    public void onSendMessage(String user_id, String message) {
        mAddingMessageDialog.dismiss();
        sendingMessage(Constants.sendingMessageUrl, user_id, message);
    }

    public void loadOffers(String serviceUrl, final boolean isCategoryChosen, final CategoriesModel categoriesModel) {
        progressBar.setVisibility(View.VISIBLE);
        offersList.clear();
        mOffersAdapter.notifyDataSetChanged();
        final StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isCategoryClicked = false;
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(offers)", response);
                        parseOffersResponse(response);
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
                    params.put("id", categoriesModel.getCategory_id());
                }
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                System.out.println("Parameters : " + params);
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

    private void parseOffersResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optBoolean("response")) {
                if (jsonObject.has("offers")) {
                    JSONObject offersObject = jsonObject.optJSONObject("offers");
                    last_page = offersObject.optInt("last_page");
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
                        if (!offerObject.isNull("get_state"))
                            offersModel.setOffer_state(offerObject.getJSONObject("get_state").getString("ar_name"));
                        offersModel.setOffer_price(offerObject.getString("price") + " " + offerObject.getString("currecny_txt"));
                        offersList.add(offersModel);
                    }
                }
            }
            if (offersList.isEmpty()) {
                noDataLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText("لا يوجد اعلانات عن " + tv_category_name.getText());
            } else {
                mOffersAdapter.addOffersList(offersList);
                noDataLayout.setVisibility(View.GONE);
            }
            for (int i = suggest_list.size(); i < offersList.size(); i++) {
                suggest_list.add(offersList.get(i).getOffer_title());
            }
            if (getActivity() != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest_list);
                search_box.setAdapter(adapter);
            }
            search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    searchForOffer(Constants.offers_searchUrl, textView.getText().toString());
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

    @OnClick(R.id.layout_choose_category)
    void chooseCategory() {
        mCategoriesCustomDialog = new CategoriesCustomDialog(mContext, this);
        mCategoriesCustomDialog.show();
    }

    @Override
    public void onCategoryItemClicked(CategoriesModel categoriesModel, Boolean isCategorySelected) {
        isCategoryClicked = true;
        offersList.clear();
        mOffersAdapter.notifyDataSetChanged();
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
        loadOffers(Constants.loadAllOffers + "?country_code=" + mSharedPrefManager.getCountryCode(), isCategorySelected, categoriesModel);

    }

    private void setupPagination() {
        mRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                current += 1;
                if (last_page >= current_page) {
                    if (!isCategoryClicked)
                        loadMoreOffers(Constants.loadAllOffers, "1");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isCategoryClicked)
            loadOffers(Constants.loadAllOffers + "?country_code=" + mSharedPrefManager.getCountryCode(), false, null);
    }

    private void loadMoreOffers(String serviceURL, final String category_id) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(offers)", response);
                        parseOffersResponse(response);
                        if (isRefreshing) {
                            swipe_layout.setRefreshing(false);
                            isRefreshing = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void searchForOffer(String searchUrl, final String s) {
        progressBar.setVisibility(View.VISIBLE);
        offersList.clear();
        final StringRequest verifyReq = new StringRequest(Request.Method.POST, searchUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isCategoryClicked = false;
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(offers)", response);
                        parseOffersResponse(response);

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
//                        loading_progress.hide();
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
                params.put("keyword", s);
                if (mSharedPrefManager.getLoginStatus()) {
                    params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                }
                System.out.println("search_Parameters : " + params);
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
}
