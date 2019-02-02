package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Interfaces.ICategoryItemClicked;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramzy on 2/21/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    Context mContext;
    ICategoryItemClicked mCategoryItemClickListener;
    ArrayList<CategoriesModel> categoriesList;

    public CategoriesAdapter(Context mContext, ICategoryItemClicked mCategoryItemClickListener) {
        this.mContext = mContext;
        this.mCategoryItemClickListener = mCategoryItemClickListener;
        categoriesList = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categories_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_item_title.setText(categoriesList.get(position).getCategory_name());
        Glide.with(mContext)
                .load(Constants.imagesBaseUrl + categoriesList.get(position).getCategory_image())
                .placeholder(R.drawable.category_placeholder) // can also be a drawable
                .error(R.drawable.category_placeholder) // will be displayed if the image cannot be loaded
                .into(holder.iv_category_icon);


    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public void addCategoriesList(ArrayList<CategoriesModel> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_category_icon)
        ImageView iv_category_icon;
        @BindView(R.id.tv_item_title)
        TextView tv_item_title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCategoryItemClickListener.onCategoryItemClicked(categoriesList.get(getAdapterPosition()), true);
                }
            });

        }
    }

}
