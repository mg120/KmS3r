package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tqnee.KamS3r.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ramzy on 3/8/2017.
 */

public class UpdatePasswordDialog extends Dialog {

    Context mContext;
    @BindView(R.id.btn_update_password)
    Button btn_update_password;
    @BindView(R.id.et_user_old_password)
    EditText et_user_old_password;
    @BindView(R.id.et_user_new_password)
    EditText et_user_new_password;
    @BindView(R.id.et_confirm_password)
    EditText et_confirm_password;


    public UpdatePasswordDialog(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_update_password);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);

        et_user_old_password.setTransformationMethod(new PasswordTransformationMethod());
        et_user_new_password.setTransformationMethod(new PasswordTransformationMethod());
        et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());
    }

    @OnClick(R.id.btn_update_password)
    void onUpdatePasswordClicked() {
        dismiss();
    }
}