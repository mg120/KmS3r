package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.tqnee.KamS3r.Interfaces.IForgotPassword;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ramzy on 3/8/2017.
 */

public class ForgotPasswordCustomDialog extends Dialog {

    Context mContext;
    IForgotPassword mForgotPasswordListener;

    @BindView(R.id.et_user_email)
    EditText et_user_email;

    public ForgotPasswordCustomDialog(Context mContext, IForgotPassword mForgotPasswordListener) {
        super(mContext);
        this.mContext = mContext;
        this.mForgotPasswordListener = mForgotPasswordListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_forgot_password);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
    }

    @OnClick(R.id.btn_send_password)
    void onResetPasswordClicked() {
        if (submitForm()) {
            mForgotPasswordListener.onResetPasswordListener(et_user_email.getText().toString().trim());
        }
    }

    private boolean submitForm() {

        if (!validateEmail()) {
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

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}