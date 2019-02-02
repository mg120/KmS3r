package com.tqnee.KamS3r.Activites;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;

public class SplashScreen extends ParentActivity {
    Runnable run;
    Handler handler = new Handler();
    //    Context mContext;
    SharedPrefManager mSharedPrefManager;
    Intent intent;

    @Override
    protected void initializeComponents() {
//        mContext = SplashScreen.this;
        mSharedPrefManager = new SharedPrefManager(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        run = new Runnable() {
            @Override
            public void run() {
                if (mSharedPrefManager.isFirstTime()) {
                    intent = new Intent(SplashScreen.this, IntroScreensActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                } else {
                    if (mSharedPrefManager.getLoginStatus()) {
                        intent = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    } else {
                        intent = new Intent(SplashScreen.this, SelectCountryActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    }
                }

            }
        };
        handler.postDelayed(run, 3000);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash_screen;
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        handler.removeCallbacks(run);
        super.onDestroy();
    }
}
