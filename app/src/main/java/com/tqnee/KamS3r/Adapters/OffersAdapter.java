package com.tqnee.KamS3r.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.AdsDetailsActivity;
import com.tqnee.KamS3r.Activites.EditAdActivity;
import com.tqnee.KamS3r.Interfaces.IOffersOperationsListener;
import com.tqnee.KamS3r.Model.OfferModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * Created by ramzy on 2/21/2017.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    Context mContext;
    IOffersOperationsListener mOffersOperationsListener;
    ArrayList<OfferModel> offersList;
    SharedPrefManager mSharedPrefManager;
    PopupMenu popup;
    MenuItem edit_ad, delete_ad, report;

    public OffersAdapter(Context mContext, SharedPrefManager mSharedPrefManager, IOffersOperationsListener mOffersOperationsListener) {
        this.mContext = mContext;
        this.mOffersOperationsListener = mOffersOperationsListener;
        offersList = new ArrayList<>();
        this.mSharedPrefManager = mSharedPrefManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offers_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_offer_title.setText(offersList.get(position).getOffer_title());
        holder.tv_user_name.setText(offersList.get(position).getOffer_username());
        holder.tv_time.setText(offersList.get(position).getOffer_time());
        holder.tv_country.setText(offersList.get(position).getOffer_state());
        holder.priceTextView.setText(offersList.get(position).getOffer_price());
        if (offersList.get(position).getOffer_image().isEmpty()) {
            holder.iv_offer_image.setVisibility(View.GONE);
        } else {
            holder.iv_offer_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Constants.imagesBaseUrl + offersList.get(position).getOffer_image())
                    .error(R.mipmap.question_image_placeholder)
                    .placeholder(R.mipmap.question_image_placeholder)
                    .into(holder.iv_offer_image);
        }
        Glide.with(mContext).load(offersList.get(position).getOffer_user_image())
                .error(R.mipmap.person_place_holder)
                .placeholder(R.mipmap.person_place_holder)
                .into(holder.iv_user_image);

        final PopupMenu popup;
        popup = new PopupMenu(mContext, holder.iv_option_menu);
        popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
        final MenuItem edit_ad = popup.getMenu().findItem(R.id.edit_ad);
        final MenuItem delete_ad = popup.getMenu().findItem(R.id.delete_ad);
        final MenuItem report = popup.getMenu().findItem(R.id.report);
        holder.iv_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPrefManager.getUserDate().getId().equals(offersList.get(position).getOffer_user_id())) {
                    edit_ad.setVisible(true);
                    delete_ad.setVisible(true);
                    report.setVisible(false);
                } else {
                    edit_ad.setVisible(false);
                    delete_ad.setVisible(false);
                    report.setVisible(true);
                }
                popup.show();
            }
        });
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.report) {
                    popup.dismiss();
                    mOffersOperationsListener.onOptionMenuClicked(position);
                } else if (item.getItemId() == R.id.edit_ad) {
                    Intent intent = new Intent(mContext, EditAdActivity.class);
                    intent.putExtra("ad_id", offersList.get(position).getOffer_id());
                    mContext.startActivity(intent);
                } else if (item.getItemId() == R.id.delete_ad) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setIcon(R.mipmap.splash_logo)
                            .setMessage("تأكيد حذف الاعلان ؟")
                            .setCancelable(false)
                            .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    delete_Ad(offersList.get(position).getOffer_id());
                                }
                            }).setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                return false;
            }
        });

        if (offersList.get(position).isFavourite()) {
            holder.iv_favorite.setImageResource(R.mipmap.like_filled_icon);
        } else {
            holder.iv_favorite.setImageResource(R.mipmap.like_empty_icon);
        }

        if (mSharedPrefManager.getUserDate().getId().equals(offersList.get(position).getOffer_user_id())) {
            holder.iv_message.setVisibility(View.GONE);
            report.setVisible(false);
            edit_ad.setVisible(true);
            delete_ad.setVisible(true);

        } else {
            holder.iv_message.setVisibility(View.VISIBLE);
            report.setVisible(true);
            edit_ad.setVisible(false);
            delete_ad.setVisible(false);
        }

        holder.offerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Ad ID : " + offersList.get(position).getOffer_id());
                Intent mIntent = new Intent(mContext, AdsDetailsActivity.class);
                mIntent.putExtra("adId", offersList.get(position).getOffer_id());
                mIntent.putExtra("userId", offersList.get(position).getOffer_user_id());
                mContext.startActivity(mIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return offersList.size();
    }

    public void addOffersList(ArrayList<OfferModel> offersList) {
        this.offersList = offersList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_offer_title)
        TextView tv_offer_title;
        @BindView(R.id.iv_offer_image)
        ImageView iv_offer_image;
        @BindView(R.id.iv_option_menu)
        ImageView iv_option_menu;
        @BindView(R.id.iv_share)
        ImageView iv_share;
        @BindView(R.id.iv_favorite)
        ImageView iv_favorite;
        @BindView(R.id.iv_message)
        ImageView iv_message;
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.offer_layout)
        LinearLayout offerLayout;
        @BindView(R.id.tv_country)
        TextView tv_country;
        @BindView(R.id.tv_ads_cost)
        TextView priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            iv_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onUserClicked(getAdapterPosition());
                }
            });
            tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onUserClicked(getAdapterPosition());
                }
            });

            iv_offer_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onImageClicked(getAdapterPosition());
                }
            });

            iv_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onMessageClicked(getAdapterPosition());
                }
            });
            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onShareClicked(getAdapterPosition());
                }
            });
            iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffersOperationsListener.onFavouriteClicked(getAdapterPosition());
                }
            });
        }
    }

    private void delete_Ad(final String ad_Id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.delete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("response").equals("true")) {
                        Toasty.success(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        OffersAdapter.this.notifyDataSetChanged();
                    } else {
                        Toasty.error(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        OffersAdapter.this.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                params.put("offer_id", ad_Id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }
}
