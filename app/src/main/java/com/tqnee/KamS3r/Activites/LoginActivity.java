package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IForgotPassword;
import com.tqnee.KamS3r.Model.UserModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.ForgotPasswordCustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends ParentActivity implements IForgotPassword {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    //    CustomLoadingDialog mCustomLoadingDialog;
    @BindView(R.id.et_user_email)
    EditText et_user_email;
    @BindView(R.id.et_user_password)
    EditText et_user_password;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.login_progress_id)
    ProgressBar progressBar;

    ForgotPasswordCustomDialog mPasswordCustomDialog;


    Intent intent;

    @Override
    protected void initializeComponents() {
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        et_user_password.setTransformationMethod(new PasswordTransformationMethod());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isEnableToolbar() {
        return false;
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

    @OnClick(R.id.btn_login)
    void onLoginButtonClicked() {
        if (submitForm()) {
            userLogin(Constants.userLoginUrl);
        }
    }

    @OnClick(R.id.tv_register)
    void onCreateAccountButtonClicked() {
        intent = new Intent(mContext, CreateAccountActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    @OnClick(R.id.tv_forgot_password)
    void onForgotPasswordButtonClicked() {
        mPasswordCustomDialog = new ForgotPasswordCustomDialog(mContext, this);
        mPasswordCustomDialog.show();
    }

    @Override
    public void onResetPasswordListener(String Email) {
        mPasswordCustomDialog.dismiss();
        forgotPassword(Constants.forgotPasswordUrl, Email);
    }

    private boolean submitForm() {

        if (!validateEmail()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        if (et_user_email.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, et_user_email, mContext.getString(R.string.error_empty_email), R.color.colorError);
            Utils.requestFocus(et_user_email, getWindow());
            return false;
        } else if (!isValidEmail(et_user_email.getText().toString().trim())) {
            Utils.showSnackBar(mContext, et_user_email, mContext.getString(R.string.error_email_format), R.color.colorError);
            Utils.requestFocus(et_user_email, getWindow());
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_user_password.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, et_user_password, mContext.getString(R.string.error_empty_password), R.color.colorError);
            Utils.requestFocus(et_user_password, getWindow());
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void userLogin(String serviceUrl) {
        final String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("token: " + token);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response: " + response);
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(login)", response);
                        parseLoginResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", et_user_email.getText().toString());
                params.put("password", et_user_password.getText().toString().trim());
                params.put("device_token", token);
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

    private void parseLoginResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Boolean responseMessage = jsonObject.optBoolean("response");
            if (responseMessage) {
                if (jsonObject.has("user")) {
                    JSONObject userObject = jsonObject.optJSONObject("user");
                    UserModel userModel = new UserModel();
                    userModel.setName(userObject.optString(UserModel.KEY_USERNAME));
                    userModel.setEmail(userObject.optString(UserModel.KEY_EMAIL));
                    userModel.setId(userObject.optString(UserModel.KEY_USER_ID));
                    userModel.setPhone(userObject.optString(UserModel.KEY_PHONE));
                    userModel.setPhoto(userObject.optString(UserModel.KEY_USRE_IMAGE));
                    userModel.setCover_image(userObject.optString(UserModel.KEY_USER_COVER));
                    userModel.setApiToken(jsonObject.optString(UserModel.KEY_API_TOKEN));
                    userModel.setBio(userObject.optString(UserModel.KEY_USER_BIO));
                    if (!userObject.isNull("get_mobile_country"))
                        userModel.setCountry(userObject.getJSONObject("get_mobile_country").getString("ar_name"));
                    userModel.setCountryCode(userObject.getString("country_code"));
                    mSharedPrefManager.setLoginStatus(true);
                    mSharedPrefManager.setUserDate(userModel);
                    Intent intent = new Intent(mContext, SelectLoginType.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

                }
            } else
                Utils.showSnackBar(mContext, layout_content, jsonObject.getString("errors"), R.color.colorError);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void forgotPassword(String serviceUrl, final String email) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(resetPassword)", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optBoolean("response")) {
                                Utils.showSnackBarWithLong(mContext, layout_content, jsonObject.optString("action"), R.color.colorCorrect);
                            } else {
                                Utils.showSnackBarWithLong(mContext, layout_content, jsonObject.optString("action"), R.color.colorError);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                params.put("email", email);
                System.out.println("Parameters : " + params);
                return params;
            }
        };


        System.out.println("Url : " + verifyReq.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if ((mCustomLoadingDialog != null) && mCustomLoadingDialog.isShowing())
//            mCustomLoadingDialog.dismiss();
//        mCustomLoadingDialog = null;
        progressBar.setVisibility(View.GONE);
    }
}
