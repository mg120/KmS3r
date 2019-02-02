package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
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
import com.tqnee.KamS3r.Adapters.CountriesAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.ICountrySelected;
import com.tqnee.KamS3r.Model.CountryModel;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tqnee.KamS3r.Utils.Constants.isStatesLoading;

/**
 * Created by ramzy on 3/8/2017.
 */

public class CountriesCustomDialog extends Dialog {

    Context mContext;
    ICountrySelected mICountrySelected;
    LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_toolbar_title)
    TextView tv_toolbar_title;

    @BindView(R.id.layout_content)
    LinearLayout layout_content;

    @BindView(R.id.loading_progress)
    AVLoadingIndicatorView loading_progress;
    CountriesAdapter mCountriesAdapter;
    ArrayList<CountryModel> countriesList;


    public CountriesCustomDialog(Context mContext, ICountrySelected mICountrySelected) {
        super(mContext);
        this.mContext = mContext;
        this.mICountrySelected = mICountrySelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_country);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
        tv_toolbar_title.setText(mContext.getString(R.string.choose_your_country));
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(mLinearLayoutManager);
        mCountriesAdapter = new CountriesAdapter(mContext, mICountrySelected);
        recycler.setAdapter(mCountriesAdapter);
        SharedPrefManager mSharedPrefManager=new SharedPrefManager(mContext);
        if (isStatesLoading){
            loadStates(mSharedPrefManager.getCountryId());
            tv_toolbar_title.setText(mContext.getString(R.string.choose_state));
        }
        else
            loadCountries(Constants.countriesUrl);


    }

    public void loadCountries(String serviceUrl) {
        loading_progress.show();
        countriesList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.GET, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading_progress.hide();
                        Utils.printLog(Constants.LOG_TAG + "(countries)", response);
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

    public void loadStates(final String countryId) {
        loading_progress.show();
        countriesList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.getStates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading_progress.hide();
                        System.out.println("response : " + response);
                        try {
                            JSONArray statesArrays = new JSONObject(response).getJSONArray("states").getJSONObject(0).getJSONArray("get_states");
                            for (int i = 0; i < statesArrays.length(); i++) {
                                JSONObject stateObject = statesArrays.getJSONObject(i);

                                CountryModel countryModel = new CountryModel();
                                countryModel.setCountry_id(String.valueOf(stateObject.optInt("id")));
                                countryModel.setCountry_name(stateObject.optString("ar_name"));
                                countriesList.add(countryModel);
                            }
                            mCountriesAdapter.addCategoriesList(countriesList);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_id", countryId);
                System.out.println("params : " + params);
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

    private void parseCategoriesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryObject = jsonArray.optJSONObject(i);
                CountryModel countryModel = new CountryModel();
                countryModel.setCountry_id(String.valueOf(countryObject.optInt("id")));
                countryModel.setCountry_name(countryObject.optString("ar_name"));
                countryModel.setCountry_code(countryObject.optString("phonecode"));
                countryModel.setCountry_iso(countryObject.optString("iso"));
                countriesList.add(countryModel);
            }
            mCountriesAdapter.addCategoriesList(countriesList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_notification)
    void onCloseClicked() {
        dismiss();
    }

}