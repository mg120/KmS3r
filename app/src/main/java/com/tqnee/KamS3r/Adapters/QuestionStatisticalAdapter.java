package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tqnee.KamS3r.Model.QuestionStatisticalModel;
import com.tqnee.KamS3r.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramzy on 2/21/2017.
 */

public class QuestionStatisticalAdapter extends RecyclerView.Adapter<QuestionStatisticalAdapter.ViewHolder> {
    Context mContext;
    ArrayList<QuestionStatisticalModel> statisticalList = new ArrayList<>();


    public QuestionStatisticalAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_statistical_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_price.setText(statisticalList.get(position).getStatistical_title() + " " + statisticalList.get(position).getStatistical_currency());
        holder.tv_percentage.setText(statisticalList.get(position).getStatistical_percentage());
        holder.tv_number_of_answer.setText(statisticalList.get(position).getStatistical_answer_count() + " " + mContext.getString(R.string.prices_title));

    }

    public void addStatisticalList(ArrayList<QuestionStatisticalModel> statisticalList) {
        this.statisticalList = statisticalList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return statisticalList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_percentage)
        TextView tv_percentage;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_number_of_answer)
        TextView tv_number_of_answer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
