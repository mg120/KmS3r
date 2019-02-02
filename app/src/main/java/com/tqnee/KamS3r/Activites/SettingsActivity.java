package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Widgets.UpdatePasswordDialog;

import butterknife.OnClick;

public class SettingsActivity extends ParentActivity {
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    UpdatePasswordDialog mUpdatePasswordDialog;


    @Override
    protected void initializeComponents() {
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        setToolbarTitle(getString(R.string.settings));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
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

    @OnClick(R.id.btn_update_password)
    void onUpdatePasswordClicked() {
        mUpdatePasswordDialog = new UpdatePasswordDialog(mContext);
        mUpdatePasswordDialog.show();
    }

    @OnClick (R.id.btn_update_profile)
    void onUpdateProfileClicked(){
        startActivity(new Intent(SettingsActivity.this,UpdateProfileActivity.class));
    }
}
