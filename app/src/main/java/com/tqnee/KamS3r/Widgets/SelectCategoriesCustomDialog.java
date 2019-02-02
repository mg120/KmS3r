package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.tqnee.KamS3r.Adapters.CategoriesAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ramzy on 3/8/2017.
 */

public class SelectCategoriesCustomDialog extends Dialog {

    Context mContext;
    ICategoryItemClicked mCategoryItemClickListener;
    GridLayoutManager mGridLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.layout_all_categories)
    RelativeLayout layout_all_categories;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.iv_notification)
    ImageView iv_back_button;
    @BindView(R.id.loading_progress)
    AVLoadingIndicatorView loading_progress;
    CategoriesAdapter mCategoriesAdapter;
    ArrayList<CategoriesModel> categoriesList;

    public SelectCategoriesCustomDialog(Context mContext, ICategoryItemClicked mCategoryItemClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.mCategoryItemClickListener = mCategoryItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_categories);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        recycler.setLayoutManager(mGridLayoutManager);
        mCategoriesAdapter = new CategoriesAdapter(mContext, mCategoryItemClickListener);
        recycler.setAdapter(mCategoriesAdapter);
        loadCategories(Constants.categoriesUrl);
        layout_all_categories.setVisibility(View.GONE);


    }

    public void loadCategories(String serviceUrl) {
        loading_progress.show();
        categoriesList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.GET, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading_progress.hide();
                        Utils.printLog(Constants.LOG_TAG + "(categories)", response);
                        parseCategoriesResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading_progress.hide();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                });


        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private void parseCategoriesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject categoryObject = jsonArray.optJSONObject(i);
                CategoriesModel categoryModel = new CategoriesModel();
                categoryModel.setCategory_id(String.valueOf(categoryObject.optInt("id")));
                categoryModel.setCategory_name(categoryObject.optString("name"));
                categoryModel.setCategory_image(categoryObject.optString("app_icon"));
                categoriesList.add(categoryModel);
            }
            mCategoriesAdapter.addCategoriesList(categoriesList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_notification)
    void onCloseClicked() {
        dismiss();
    }

    @OnClick(R.id.layout_all_categories)
    void onAllCategoryClicked() {
        CategoriesModel categoriesModel = new CategoriesModel();
        categoriesModel.setCategory_name(mContext.getString(R.string.all_categories));
        mCategoryItemClickListener.onCategoryItemClicked(categoriesModel, false);
    }
}