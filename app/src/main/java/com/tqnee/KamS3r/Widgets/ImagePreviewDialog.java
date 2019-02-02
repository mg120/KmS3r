package com.tqnee.KamS3r.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramzy on 3/8/2017.
 */

public class ImagePreviewDialog extends Dialog {
    Context mContext;
    String imageUrl;
    @BindView(R.id.iv_image_preview)
    ImageView iv_image_preview;
    @BindView(R.id.loading_progress)
    AVLoadingIndicatorView loading_progress;
    @BindView(R.id.close_image_id)
    ImageView iv_image_close;

    @BindView(R.id.tv_error)
    TextView tv_error;

    public ImagePreviewDialog(Context mContext, String imageUrl) {
        super(mContext);
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.custom_loading_background));
        setContentView(R.layout.dialog_image_preview);
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        ButterKnife.bind(this);

        Glide.with(mContext).load(Constants.imagesBaseUrl + imageUrl)
                .animate(R.anim.fade_in)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        loading_progress.hide();
                        tv_error.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        loading_progress.hide();
                        tv_error.setVisibility(View.GONE);
                        return false;
                    }
                }).into(iv_image_preview);

//        Picasso.with(mContext)
//                .load(Constants.imagesBaseUrl + imageUrl)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder)
//                .into(iv_image_preview);


        iv_image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}