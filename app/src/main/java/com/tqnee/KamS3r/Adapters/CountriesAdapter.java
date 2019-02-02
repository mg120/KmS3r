package com.tqnee.KamS3r.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tqnee.KamS3r.Interfaces.ICountrySelected;
import com.tqnee.KamS3r.Model.CountryModel;
import com.tqnee.KamS3r.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ramzy on 2/21/2017.
 */

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {
    Context mContext;
    ICountrySelected mICountrySelected;
    ArrayList<CountryModel> countriesList;

    public CountriesAdapter(Context mContext, ICountrySelected mICountrySelected) {
        this.mContext = mContext;
        this.mICountrySelected = mICountrySelected;
        countriesList = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_country_name.setText(countriesList.get(position).getCountry_name());

    }


    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public void addCategoriesList(ArrayList<CountryModel> countriesList) {
        this.countriesList = countriesList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_country_name)
        TextView tv_country_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mICountrySelected.onCountrySelected(countriesList.get(getAdapterPosition()));
                }
            });

        }
    }

}
