package com.tqnee.KamS3r.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;

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
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectCountryActivity extends ParentActivity {

    private SelectCountryActivity mContext;
    SharedPrefManager mSharedPrefManager;
    //    private CustomLoadingDialog customLoadingDialog;
    private Button continueButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("اختيار الدولة");
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
//        customLoadingDialog = new CustomLoadingDialog(mContext);
    }

    @Override
    protected void initializeComponents() {
        final RadioButton egyptRadioButton = (RadioButton) findViewById(R.id.egypt_radio);
        continueButton = (Button) findViewById(R.id.continue_button);
        progressBar = (ProgressBar) findViewById(R.id.select_country_progress_id);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (egyptRadioButton.isChecked())
                    getCountry(Constants.EGYPT_ID);
                else
                    getCountry(Constants.KSA_ID);
            }
        });
    }

    void getCountry(final String countryId) {
//        customLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, Constants.getCountry,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        System.out.println("Response : " + response);
                        try {
                            JSONObject countryObject = new JSONObject(response).getJSONArray("country").getJSONObject(0);
                            mSharedPrefManager.setCountry(countryObject.getString("iso"), countryObject.getString("id"), countryObject.getString("ar_name"), countryObject.getString("phonecode"), countryObject.getString("currency_id"), countryObject.getJSONObject("get_currency").getString("currency"), countryObject.getJSONObject("get_currency").getString("sign"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (mContext != null) {
                            if (error instanceof TimeoutError) {
                                Utils.showSnackBar(mContext, continueButton, getString(R.string.time_out), R.color.colorError);
                            } else if (error instanceof NoConnectionError)
                                Utils.showSnackBar(mContext, continueButton, getString(R.string.no_connection), R.color.colorError);
                            else if (error instanceof ServerError)
                                Utils.showSnackBar(mContext, continueButton, getString(R.string.server_error), R.color.colorError);
                            else if (error instanceof NetworkError)
                                Utils.showSnackBar(mContext, continueButton, getString(R.string.no_connection), R.color.colorError);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_id", countryId);

                System.out.println("parameters : " + params);
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
    protected int getLayoutResource() {
        return R.layout.activity_select_country;
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
        return false;
    }

    @Override
    protected boolean hideInputType() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}
