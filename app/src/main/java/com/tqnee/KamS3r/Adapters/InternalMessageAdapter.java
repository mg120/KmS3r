package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tqnee.KamS3r.Model.MessageModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramzy on 2/21/2017.
 */

public class InternalMessageAdapter extends RecyclerView.Adapter<InternalMessageAdapter.ViewHolder> {
    Context mContext;
    ArrayList<MessageModel> conversationList = new ArrayList<>();
    SharedPrefManager mSharedPrefManager;
    int selectedItem = -1;


    public InternalMessageAdapter(Context mContext, SharedPrefManager mSharedPrefManager) {
        this.mContext = mContext;
        this.mSharedPrefManager = mSharedPrefManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.internal_message_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mSharedPrefManager.getUserDate().getId().equals(conversationList.get(position).getSender_user_id())) {
            holder.layout_my_message.setVisibility(View.VISIBLE);
            holder.tv_my_message.setText(conversationList.get(position).getMessage_content());
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_time.setText(conversationList.get(position).getSender_time());

            holder.layout_other_user.setVisibility(View.GONE);
            holder.tv_time_other.setVisibility(View.GONE);
        } else {
            holder.layout_other_user.setVisibility(View.VISIBLE);
            holder.layout_my_message.setVisibility(View.GONE);
            holder.tv_other_user_message.setText(conversationList.get(position).getMessage_content());

            holder.tv_time_other.setVisibility(View.VISIBLE);
            holder.tv_time_other.setText(conversationList.get(position).getSender_time());
            holder.tv_time.setVisibility(View.GONE);
        }
    }


    public void addMessageList(ArrayList<MessageModel> conversationList) {
        this.conversationList = conversationList;
        notifyDataSetChanged();
    }

    public void insertItem(MessageModel messageModel, int position) {
        conversationList.add(position, messageModel);
        notifyItemInserted(getItemCount() - 1);
    }


    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_my_message)
        RelativeLayout layout_my_message;
        @BindView(R.id.layout_other_user)
        RelativeLayout layout_other_user;
        @BindView(R.id.tv_other_user_message)
        TextView tv_other_user_message;
        @BindView(R.id.tv_my_message)
        TextView tv_my_message;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_time_other)
        TextView tv_time_other;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}