package com.tqnee.KamS3r.App;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.tqnee.KamS3r.R;
//import com.tsengvn.typekit.Typekit;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ramzy on 4/18/2017.
 */


/**
 * this class is responsible for single tone operation
 * in app
 * like initializing typeKit for change fonts
 * creating single tone Object for volley networking library
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;


    public AppController() {
    }

    private AppController(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AppController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppController(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
                AppEventsLogger.activateApp(this);
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(
                            "com.tqnee.kamsa3r",
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                    }
                } catch (PackageManager.NameNotFoundException e) {

                } catch (NoSuchAlgorithmException e) {

                }
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.secret_key)))
                .build();
        Twitter.initialize(config);
//        Typekit.getInstance()
//                .addNormal(Typekit.createFromAsset(mContext, "fonts/" + Fontss.regularFont))
//                .addBold(Typekit.createFromAsset(mContext, "fonts/" + Fontss.semiBoldFont));

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);/*, new HurlStack(null, newSslSocketFactory()));*/
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
