package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Interfaces.IAnswerItemClicked;
import com.tqnee.KamS3r.Model.AnswersModel;
import com.tqnee.KamS3r.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 2/21/2017.
 */

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {
    Context mContext;
    IAnswerItemClicked mAnswerItemListener;
    ArrayList<AnswersModel> answersList = new ArrayList<>();

    public AnswersAdapter(Context mContext, IAnswerItemClicked mAnswerItemListener) {
        this.mContext = mContext;
        this.mAnswerItemListener = mAnswerItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_user_name.setText(answersList.get(position).getAnswer_user_name());
        holder.tv_answer_content.setText(answersList.get(position).getAnswer_content());
        holder.tv_price.setText(answersList.get(position).getAnswer_price() + " " + answersList.get(position).getAnswer_currency());
        holder.questionTitleTextView.setText(answersList.get(position).getQuestion_title());
        Glide.with(mContext)
                .load( answersList.get(position).getAnswer_user_image())
                .placeholder(R.mipmap.person_place_holder) // can also be a drawable
                .error(R.mipmap.person_place_holder) // will be displayed if the image cannot be loaded
                .into(holder.iv_user_image);
        if (answersList.get(position).isFav()) {
            holder.iv_favorite.setImageResource(R.mipmap.like_filled_icon);
        } else {
            holder.iv_favorite.setImageResource(R.mipmap.like_empty_icon);
        }
        holder.tv_comment.setText(answersList.get(position).getComments_number());
    }

    public void addAnswersList(ArrayList<AnswersModel> answersList) {
        this.answersList = answersList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_answer_content)
        TextView tv_answer_content;
        @BindView(R.id.iv_favorite)
        ImageView iv_favorite;
        @BindView(R.id.tv_comment)
        TextView tv_comment;
        @BindView(R.id.question_title_text_view)
        TextView questionTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAnswerItemListener.onCommentIconClicked(getAdapterPosition());
                }
            });
            iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAnswerItemListener.onAnswerFavouriteClicked(getAdapterPosition());
                }
            });
        }
    }
}
