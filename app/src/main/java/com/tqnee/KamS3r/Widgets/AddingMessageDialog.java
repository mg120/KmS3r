package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;

import com.tqnee.KamS3r.Interfaces.ISendingMessagesListener;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ramzy on 3/8/2017.
 */

public class AddingMessageDialog extends Dialog {

    Context mContext;
    @BindView(R.id.et_user_message)
    EditText et_user_message;
    String user_id;
    ISendingMessagesListener mSendingMessagesListener;


    public AddingMessageDialog(Context mContext, String user_id, ISendingMessagesListener mSendingMessagesListener) {
        super(mContext);
        this.mContext = mContext;
        this.user_id = user_id;
        this.mSendingMessagesListener = mSendingMessagesListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_send_message);
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
    }

    @OnClick(R.id.btn_send)
    void onSendMessageClick() {
        if (submitForm()) {
            mSendingMessagesListener.onSendMessage(user_id, et_user_message.getText().toString());
        }
    }

    private boolean submitForm() {

        if (!validateMessage()) {
            return false;
        }
        return true;
    }

    private boolean validateMessage() {
        if (et_user_message.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(mContext, et_user_message, mContext.getString(R.string.error_empty_message), R.color.colorError);
            Utils.requestFocus(et_user_message, getWindow());
            return false;
        }
        return true;
    }
}