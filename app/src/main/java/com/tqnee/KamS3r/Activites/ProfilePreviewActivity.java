package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.Fragments.ProfileFragment;
import com.tqnee.KamS3r.Fragments.UserProfileFragment;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;

public class ProfilePreviewActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    FragmentManager mFragmentManager;

    @Override
    protected void initializeComponents() {
        setToolbarTitle(getString(R.string.profile_title));
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        mFragmentManager = getSupportFragmentManager();
        if (getIntent().hasExtra("user_id")) {
            if (getIntent().getStringExtra("user_id").equals(mSharedPrefManager.getUserDate().getId())) {
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, new ProfileFragment())
                        .commit();
            } else {
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, UserProfileFragment.newInstance(getIntent().getStringExtra("user_id")))
                        .commit();
            }
        }


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile_preview;
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
        return true;
    }

    @Override
    protected boolean hideInputType() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}
