package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Interfaces.IAdsOperationsListener;
import com.tqnee.KamS3r.Model.AdModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.WebServices;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 2/21/2017.
 */

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {
    Context mContext;
    IAdsOperationsListener mAdsOperationsListener;
    ArrayList<AdModel> adsList = new ArrayList<AdModel>();
    SharedPrefManager mSharedPrefManager;

    public AdsAdapter(Context mContext, ArrayList<AdModel> adsList, IAdsOperationsListener mAdsOperationsListener) {
        this.mContext = mContext;
        this.mAdsOperationsListener = mAdsOperationsListener;
        this.adsList = adsList;
        mSharedPrefManager = new SharedPrefManager(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ads_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AdModel adModel = adsList.get(position);
        holder.tv_user_name.setText(adModel.getUserName());
        holder.tv_ads_title.setText(adModel.getTitle());
        holder.tv_ads_cost.setText(adModel.getPrice());
        holder.tv_country.setText(adModel.getCity());
        holder.tv_time.setText(adModel.getTime());
        if (!adModel.getFavoritesCount().equals("0"))
            holder.likesCounterTextView.setText(adModel.getFavoritesCount());
        else
            holder.likesCounterTextView.setText("");
        holder.likesCounterTextView.setText(adModel.getFavoritesCount());
        if (adModel.isFavorite())
            holder.iv_favorite.setImageResource(R.mipmap.like_filled_icon);
        holder.iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebServices.addToFavourite(mContext, holder.iv_favorite, Constants.addToFavouriteUrl, adModel.getId(), Constants.favorite_ad_item_type, mSharedPrefManager.getUserDate().getApiToken());
                int likesCounter = Integer.parseInt(holder.likesCounterTextView.getText().toString());
                if (adModel.isFavorite()) {
                    holder.iv_favorite.setImageResource(R.mipmap.like_empty_icon);
                    adModel.setFavorite(false);
                    holder.likesCounterTextView.setText("" + (likesCounter - 1));
                } else {
                    holder.iv_favorite.setImageResource(R.mipmap.like_filled_icon);
                    adModel.setFavorite(true);
                    holder.likesCounterTextView.setText("" + (likesCounter + 1));
                }
            }
        });
        if (adModel.getImage().equals(""))
            holder.iv_ads_image.setVisibility(View.GONE);
        else {
            Glide.with(mContext).load(Constants.imagesBaseUrl + adModel.getImage())
                    .into(holder.iv_ads_image);
        }
        holder.iv_ads_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdsOperationsListener.onImageClicked(position);
            }
        });
        Glide.with(mContext).load(adModel.getUserPhoto())
                .error(R.mipmap.person_place_holder)
                .placeholder(R.mipmap.person_place_holder)
                .into(holder.iv_user_image);

        holder.adLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdsOperationsListener.onItemClicked(position);
            }
        });

        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdsOperationsListener.onShareClicked(position);
            }
        });
    }


    @Override
    public int getItemCount() {

        return adsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;

        @BindView(R.id.tv_time)
        TextView tv_time;

        @BindView(R.id.tv_ads_title)
        TextView tv_ads_title;
        @BindView(R.id.tv_ads_cost)
        TextView tv_ads_cost;

        @BindView(R.id.tv_country)
        TextView tv_country;
        @BindView(R.id.iv_ads_image)
        ImageView iv_ads_image;
        @BindView(R.id.iv_answer)
        ImageView iv_answer;
        @BindView(R.id.iv_share)
        ImageView iv_share;
        @BindView(R.id.iv_option_menu)
        ImageView iv_option_menu;
        @BindView(R.id.iv_favorite)
        ImageView iv_favorite;
        @BindView(R.id.iv_message)
        ImageView iv_message;
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.ad_layout)
        LinearLayout adLayout;
        @BindView(R.id.likes_counter_text_view)
        TextView likesCounterTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            final int position = getAdapterPosition();


            iv_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsOperationsListener.onUserClicked(position);
                }
            });
            tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsOperationsListener.onUserClicked(position);
                }
            });
            iv_ads_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsOperationsListener.onImageClicked(position);
                }
            });
            iv_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsOperationsListener.onAnswerClicked(position);
                }
            });
            iv_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsOperationsListener.onMessageClicked(position);
                }
            });
//            iv_share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mAdsOperationsListener.onShareClicked(position);
//                }
//            });


            final PopupMenu popup;
            popup = new PopupMenu(mContext, iv_option_menu);

            popup.getMenuInflater()
                    .inflate(R.menu.report_menu, popup.getMenu());
            iv_option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.report) {
                        popup.dismiss();
                        mAdsOperationsListener.onOptionMenuClicked(position);
                    }
                    return false;
                }
            });

        }
    }
}
