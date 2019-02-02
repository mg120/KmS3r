package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.AdsDetailsActivity;
import com.tqnee.KamS3r.Activites.AnswerDetailsActivity;
import com.tqnee.KamS3r.Activites.QuestionDetailsActivity;
import com.tqnee.KamS3r.Interfaces.INotificationListener;
import com.tqnee.KamS3r.Model.NotificationModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 2/21/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context mContext;
    ArrayList<NotificationModel> notificationsList = new ArrayList<>();
    INotificationListener iNotificationItemListener;

    public NotificationAdapter(Context mContext, INotificationListener iNotificationItemListener) {
        this.mContext = mContext;
        this.iNotificationItemListener = iNotificationItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_question_title.setText(notificationsList.get(position).getNotification_title());
        holder.tv_notification_content.setText(notificationsList.get(position).getNotification_comment());
        holder.tv_user_name.setText(notificationsList.get(position).getNotification_username());
        holder.tv_time.setText(notificationsList.get(position).getNotification_time());
        Glide.with(mContext).load(Constants.imagesBaseUrl + notificationsList.get(position).getNotification_user_image())
                .error(R.mipmap.person_place_holder)
                .placeholder(R.mipmap.person_place_holder)
                .into(holder.iv_user_image);
        final String type= String.valueOf(notificationsList.get(position).getNotification_type());
        final String id=notificationsList.get(position).getNotify_id();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals(Constants.QUESTION_TYPE))
                    mContext.startActivity(new Intent(mContext, QuestionDetailsActivity.class).putExtra("question_id",id));
                else if(type.equals(Constants.OFFER_TYPE))
                    mContext.startActivity(new Intent(mContext, AdsDetailsActivity.class).putExtra("adId",id));
                else if(type.equals(Constants.ANSWER_TYPE))
                    mContext.startActivity(new Intent(mContext, AnswerDetailsActivity.class).putExtra("answer_id",id));
            }
        });
    }


    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public void addNotificationList(ArrayList<NotificationModel> notificationsList) {
        this.notificationsList = notificationsList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_question_title)
        TextView tv_question_title;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_notification_content)
        TextView tv_notification_content;
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.tv_time)
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iNotificationItemListener.onItemClicked(getAdapterPosition());
                }
            });
            iv_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iNotificationItemListener.onUserClicked(getAdapterPosition());
                }
            });
            tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iNotificationItemListener.onUserClicked(getAdapterPosition());
                }
            });


        }

    }
}
