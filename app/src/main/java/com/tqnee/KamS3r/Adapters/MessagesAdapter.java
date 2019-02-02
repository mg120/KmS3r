package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Interfaces.IRecyclerItemClicked;
import com.tqnee.KamS3r.Model.MessagesModel;
import com.tqnee.KamS3r.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 2/21/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context mContext;
    IRecyclerItemClicked mRecyclerItemClickListener;
    ArrayList<MessagesModel> messagesList = new ArrayList<>();

    public MessagesAdapter(Context mContext, IRecyclerItemClicked mRecyclerItemClickListener) {
        this.mContext = mContext;
        this.mRecyclerItemClickListener = mRecyclerItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_user_name.setText(messagesList.get(position).getOther_person_name());
        holder.tv_message_content.setText(messagesList.get(position).getMessage());
        holder.tv_time.setText(messagesList.get(position).getTime());
        Glide.with(mContext)
                .load( messagesList.get(position).getOther_person_image())
                .placeholder(R.mipmap.person_place_holder) // can also be a drawable
                .error(R.mipmap.person_place_holder) // will be displayed if the image cannot be loaded
                .into(holder.iv_user_image);
        if (messagesList.get(position).isMessage_seen()) {
            holder.tv_message_content.setTextColor(ContextCompat.getColor(mContext, R.color.colorQuestionBarText));
        } else {
            holder.tv_message_content.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        }
    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addMessagesList(ArrayList<MessagesModel> messagesList) {
        this.messagesList = messagesList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_message_content)
        TextView tv_message_content;
        @BindView(R.id.tv_time)
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRecyclerItemClickListener.onRecyclerItemClickListener(getAdapterPosition());
                }
            });
        }
    }

}
