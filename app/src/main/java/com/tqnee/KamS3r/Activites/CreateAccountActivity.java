package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Model.UserModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateAccountActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    //    CustomLoadingDialog mCustomLoadingDialog;
    Intent intent;

    @BindView(R.id.et_user_email)
    EditText et_user_email;
    @BindView(R.id.et_user_password)
    EditText et_user_password;
    @BindView(R.id.et_confirm_password)
    EditText et_confirm_password;
    @BindView(R.id.et_user_name)
    EditText et_user_name;
    @BindView(R.id.btn_register)
    Button btn_register;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.create_account_progress_id)
    ProgressBar progressBar;


    @Override
    protected void initializeComponents() {
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());
        et_user_password.setTransformationMethod(new PasswordTransformationMethod());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_create_account;
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

    @OnClick(R.id.tv_login)
    void onLoginButtonClicked() {
        intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    @OnClick(R.id.btn_register)
    void onCreateAccountButtonClicked() {
        if (submitForm()) {
            createAccount(Constants.createAccountUrl);
        }
    }

    private boolean submitForm() {
        if (!validateEmail()) {
            return false;
        }
        if (!validateName()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        if (!validateConfirmPassword()) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        if (et_user_email.getText().toString().trim().isEmpty()) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.error_empty_email), R.color.colorError);
            Utils.requestFocus(et_user_email, getWindow());
            return false;
        } else if (!isValidEmail(et_user_email.getText().toString().trim())) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.error_email_format), R.color.colorError);
            Utils.requestFocus(et_user_email, getWindow());
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        if (et_user_password.getText().toString().trim().isEmpty()) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.error_empty_password), R.color.colorError);
            Utils.requestFocus(et_user_password, getWindow());
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (et_confirm_password.getText().toString().trim().isEmpty()) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.error_empty_confirm_password), R.color.colorError);
            Utils.requestFocus(et_confirm_password, getWindow());
            return false;
        } else if (!et_confirm_password.getText().toString().equals(et_user_password.getText().toString().trim())) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.password_not_matched), R.color.colorError);
            Utils.requestFocus(et_confirm_password, getWindow());
            return false;
        }
        return true;
    }

    private boolean validateName() {
        if (et_user_name.getText().toString().trim().isEmpty()) {
            Utils.showSnackBarWithLong(mContext, btn_register, getString(R.string.error_user_name), R.color.colorError);
            Utils.requestFocus(et_user_name, getWindow());
            return false;
        }
        return true;
    }

    public void createAccount(String serviceUrl) {
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
//        mCustomLoadingDialog.show();

//        final String device_token = FirebaseInstanceId.getInstance().getToken();

        progressBar.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Utils.printLog(Constants.LOG_TAG + "(register)", response);
                        parseRegisterResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, btn_register, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, btn_register, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, btn_register, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, btn_register, getString(R.string.no_connection), R.color.colorError);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("register_type", Constants.NORMAL_REGISTER);
                params.put("fullname", et_user_name.getText().toString().trim());
                params.put("email", et_user_email.getText().toString().trim());
                params.put("password", et_user_password.getText().toString());
//                params.put("device_token", device_token);
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

    private void parseRegisterResponse(String response) {
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
                    mSharedPrefManager.setLoginStatus(true);
                    mSharedPrefManager.setUserDate(userModel);
                    Intent intent = new Intent(mContext, CompleteRegister.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if ((mCustomLoadingDialog != null) && mCustomLoadingDialog.isShowing())
//            mCustomLoadingDialog.dismiss();
//        mCustomLoadingDialog = null;
    }
}
