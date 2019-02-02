package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IAddAnswer;
import com.tqnee.KamS3r.Model.CurrenciesModel;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by ramzy on 3/8/2017.
 */

public class AddAnswerDialog extends Dialog {
    Context mContext;
    @BindView(R.id.btn_answer)
    Button btn_answer;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.et_value)
    EditText et_value;
    @BindView(R.id.et_comment)
    EditText et_comment;
    @BindView(R.id.spinner_currencies)
    Spinner spinner_currencies;
    @BindView(R.id.tv_currency_type)
    TextView currencyTextView;
    @BindView(R.id.add_answer_progress_id)
    ProgressBar progressBar;
    SharedPrefManager sharedPrefManager;
    //    CustomLoadingDialog customLoadingDialog;
    String ask_id;
    ArrayList<CurrenciesModel> currenciesList;
    ArrayList<String> currenciesListString;
    String currency_id = "";
    IAddAnswer addAnswerListener;

    public AddAnswerDialog(Context mContext, String ask_id, IAddAnswer addAnswerListener) {
        super(mContext);
        this.mContext = mContext;
        this.ask_id = ask_id;
        sharedPrefManager = new SharedPrefManager(mContext);
//        customLoadingDialog = new CustomLoadingDialog(mContext);
        this.addAnswerListener = addAnswerListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.add_answer_custom_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
        currenciesList = new ArrayList<>();
        currenciesListString = new ArrayList<>();
        spinner_currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currency_id = currenciesList.get(adapterView.getSelectedItemPosition()).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadCode(Constants.getAllCurrencies);

        currencyTextView.setText(sharedPrefManager.getCurrencyText());
    }

    @OnClick(R.id.iv_close)
    void onCloseIconClicked() {
        dismiss();
    }

    @OnClick(R.id.btn_answer)
    void answer() {
        addAnswer(Constants.addAnswer, ask_id);

    }

    private void addAnswer(String serviceURL, final String ask_id) {
//        customLoadingDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response")) {
                                if (jsonObject.optString("response").equals("true")) {
                                    Toasty.success(mContext, mContext.getString(R.string.answer_add_successfully), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                    addAnswerListener.onAnswerAdd();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", sharedPrefManager.getUserDate().getApiToken());
                params.put("ask_currency", sharedPrefManager.getCurrencyId());
                params.put("answer_value", et_value.getText().toString().trim());
                params.put("ask_id", ask_id);
                params.put("about", et_comment.getText().toString().trim());
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

    private void loadCode(String serviceURL) {
        StringRequest verifyReq = new StringRequest(Request.Method.GET, serviceURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("response").equals("true")) {
                                if (jsonObject.has("currencies")) {
                                    JSONArray currenciesArray = jsonObject.optJSONArray("currencies");
                                    for (int i = 0; i < currenciesArray.length(); i++) {
                                        CurrenciesModel currenciesModel = new CurrenciesModel();
                                        JSONObject jsonObject1 = currenciesArray.optJSONObject(i);

                                        currenciesModel.setCode(jsonObject1.optInt("id") + "");
                                        currenciesModel.setCurrancy(jsonObject1.optString("currency"));
                                        currenciesListString.add(currenciesModel.getCurrancy());
                                        currenciesList.add(currenciesModel);
                                    }
                                    spinner_currencies.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, currenciesListString));
                                    spinner_currencies.setSelection(0);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, btn_answer, mContext.getString(R.string.no_connection), R.color.colorError);

                    }
                });


        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }
}