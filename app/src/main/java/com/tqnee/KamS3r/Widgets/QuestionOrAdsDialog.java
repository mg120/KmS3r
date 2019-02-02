package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.tqnee.KamS3r.Activites.AddAdActivity;
import com.tqnee.KamS3r.Activites.CreateQuestionActivity;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

/**
 * Created by Kamal Marcus on 09/10/2017.
 * kamalmarcus94@gmail.com
 * +201015793659
 */

public class QuestionOrAdsDialog extends Dialog {
    Context mContext;
    private SharedPrefManager mSharedPrefManager;
    View view;

    public QuestionOrAdsDialog(Context context,View view) {
        super(context);
        mContext = context;
        this.view=view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.questions_or_ads_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);

        mSharedPrefManager = new SharedPrefManager(mContext);

        FloatingActionButton adsButton = (FloatingActionButton) findViewById(R.id.ads_button);
        FloatingActionButton questionsButton = (FloatingActionButton) findViewById(R.id.questions_button);

        questionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSharedPrefManager.getLoginStatus()) {
                    Intent mIntent = new Intent(mContext, CreateQuestionActivity.class);
                    mContext.startActivity(mIntent);
                } else {
                    Utils.showSnackBar(mContext, view, mContext.getString(R.string.you_should_login), R.color.colorError);
                }
            }
        });

        adsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, AddAdActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

}
