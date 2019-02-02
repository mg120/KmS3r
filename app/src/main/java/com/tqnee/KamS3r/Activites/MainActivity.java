package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Model.UserModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MainActivity extends ParentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    Intent intent;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.login_button)
    TwitterLoginButton mTwitterLoginButton;
    @BindView(R.id.face_book_login_button)
    LoginButton face_book_login_button;
    @BindView(R.id.main_progress_id)
    AVLoadingIndicatorView loadingIndicatorView;
    //    CustomLoadingDialog mCustomLoadingDialog;
    CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void initializeComponents() {
        mContext = this;
        mCallbackManager = CallbackManager.Factory.create();
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
        mSharedPrefManager = new SharedPrefManager(mContext);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        setupTwitterButton();
        setupFacebookButton();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
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

    @OnClick(R.id.btn_facebook)
    void onFacebookButtonClicked() {
        face_book_login_button.performClick();
    }

    @OnClick(R.id.btn_google_plus)
    void onGoogleButtonClicked() {
        //sign_in_button.performClick();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.btn_twitter)
    void onTwitterButtonClicked() {
        mTwitterLoginButton.performClick();
    }

    @OnClick(R.id.btn_create_account)
    void onCreateAccountButtonClicked() {
        intent = new Intent(mContext, CreateAccountActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClicked() {
        intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    @OnClick(R.id.tv_visitor_login)
    void onVisitorButtonClicked() {
        intent = new Intent(mContext, SelectLoginType.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    private void setupTwitterButton() {
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final String username = result.data.getUserName();
                final String user_token = String.valueOf(result.data.getUserId());
                final String email = user_token + "@twitter.com";
                Call<User> userCall = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                userCall.enqueue(new retrofit2.Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                        String social_email = "";
                        if (response.body().email != null) {
                            social_email = response.body().email;
                        }
                        String avatar = response.body().profileImageUrl.replace("_normal", "");
                        loginWithSocialMedia(Constants.createAccountUrl, username, email, social_email, user_token, avatar);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e(Constants.LOG_TAG, exception.getMessage().toString());
            }
        });
    }

    private void setupFacebookButton() {
        List<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        face_book_login_button.setReadPermissions(permissions);
        face_book_login_button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String username = object.optString("first_name") + object.optString("last_name");
                                    String user_token = object.optString("id");
                                    String email = object.optString("id") + "@facebook.com";
                                    String avatar = "https://graph.facebook.com/" + user_token + "/picture?type=large";
                                    String social_email = object.optString("email");
                                    loginWithSocialMedia(Constants.createAccountUrl, username, email, social_email, user_token, avatar);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loginWithSocialMedia(String serviceUrl, final String username, final String email, final String social_email, final String token, final String avatar) {
        mSharedPrefManager.setSocialRegister(true);
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
//        mCustomLoadingDialog.show();
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("token: " + device_token);
        loadingIndicatorView.setVisibility(View.VISIBLE);
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(register)", response);
                        loadingIndicatorView.setVisibility(View.GONE);
                        parseRegisterResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        loadingIndicatorView.setVisibility(View.GONE);
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
                params.put("register_type", Constants.SOCIAL_REGISTER);
                params.put("username", username);
                params.put("email", email);
                params.put("token", token);
                params.put("avatar", avatar);
                params.put("social_email", social_email);
                params.put("device_token", device_token);
                System.out.println("Parameters : " + params);
                return params;
            }
        };


        System.out.println("Register Url : " + verifyReq.getUrl());
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
                    userModel.setApiToken(userObject.optString(UserModel.KEY_API_TOKEN));
                    userModel.setBio(userObject.optString(UserModel.KEY_USER_BIO));
                    mSharedPrefManager.setLoginStatus(true);
                    mSharedPrefManager.setUserDate(userModel);
                    mSharedPrefManager.setUserHashtags(userObject.optString("hashtags"));
                    Intent intent = new Intent();
                    if (userObject.getInt("profile_compleate") == 0)
                        intent = new Intent(mContext, CompleteRegister.class);
                    else
                        intent = new Intent(mContext, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    System.out.println("User Token : " + userModel.getApiToken());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Utils.printLog(Constants.LOG_TAG, result.isSuccess() + "");
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String username = acct.getDisplayName();
            String token = acct.getId();
            String email = token + "@google.com";
            String social_email = acct.getEmail();
            String avatar = acct.getPhotoUrl().toString();
            loginWithSocialMedia(Constants.createAccountUrl, username, email, social_email, token, avatar);

        } else {
            // Signed out, show unauthenticated UI.
            Log.e("Error", result.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if ((mCustomLoadingDialog != null) && mCustomLoadingDialog.isShowing())
//            mCustomLoadingDialog.dismiss();
//        mCustomLoadingDialog = null;

        loadingIndicatorView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
