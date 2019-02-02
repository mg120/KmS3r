package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Model.AnswerCommentModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 2/21/2017.
 */

public class AnswerCommentsAdapter extends RecyclerView.Adapter<AnswerCommentsAdapter.ViewHolder> {
    Context mContext;
    ArrayList<AnswerCommentModel> commentsList = new ArrayList<>();
    int selectedItem = -1;

    public AnswerCommentsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_comment_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_answer_content.setText(commentsList.get(position).getComment_content());
        holder.tv_user_name.setText(commentsList.get(position).getComment_user_name());
        Glide.with(mContext).load(Constants.imagesBaseUrl + commentsList.get(position).getComment_user_image())
                .error(R.mipmap.person_place_holder)
                .placeholder(R.mipmap.person_place_holder)
                .into(holder.iv_user_image);
    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public void addCommentList(ArrayList<AnswerCommentModel> commentsList) {
        this.commentsList = commentsList;
        notifyDataSetChanged();
    }

    public void insertItem(AnswerCommentModel comment, int position) {
        commentsList.add(position, comment);
        notifyItemInserted(getItemCount() - 1);
    }


    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_answer_content)
        TextView tv_answer_content;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
