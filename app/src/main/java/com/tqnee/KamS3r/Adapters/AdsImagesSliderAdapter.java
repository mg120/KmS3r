package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;

import java.util.ArrayList;


/**
 * Created by ramzy on 5/21/2017.
 */

public class AdsImagesSliderAdapter extends PagerAdapter {

    private Context mContext;
    ArrayList<String>imagesUrls;

    public AdsImagesSliderAdapter(Context mContext,ArrayList<String>imagesUrls) {
        this.mContext = mContext;
        this.imagesUrls=imagesUrls;
    }

    @Override
    public int getCount() {
        return imagesUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.ads_slider_custom_row, container, false);
        init(itemView);
        Glide.with(mContext).load(Constants.imagesBaseUrl + imagesUrls.get(position))
                .error(R.mipmap.question_image_placeholder)
                .placeholder(R.mipmap.question_image_placeholder)
                .into((ImageView)itemView.findViewById(R.id.iv_ads_image));

        container.addView(itemView);
        return itemView;
    }

    private void init(View view) {
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);

    }

    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}