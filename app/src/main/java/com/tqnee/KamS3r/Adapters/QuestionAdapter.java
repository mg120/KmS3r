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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Activites.EditQuestion;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Interfaces.IQuestionOperationsListener;
import com.tqnee.KamS3r.Model.QuestionModel;
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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    Context mContext;
    IQuestionOperationsListener mQuestionOperationsListener;
    ArrayList<QuestionModel> questionsList;
    SharedPrefManager mSharedPrefManager;

    public QuestionAdapter(Context mContext, SharedPrefManager mSharedPrefManager, IQuestionOperationsListener mQuestionOperationsListener) {
        this.mContext = mContext;
        this.mQuestionOperationsListener = mQuestionOperationsListener;
        questionsList = new ArrayList<>();
        this.mSharedPrefManager = mSharedPrefManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_custom_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        QuestionModel question = questionsList.get(position);
        holder.tv_time.setText(question.getQuestion_time());
        holder.tv_offers.setText(mContext.getString(R.string.offers) + " (" + question.getQuestion_offers_count() + ")");
        holder.tv_user_name.setText(question.getQuestion_username());
        holder.tv_country.setText(question.getQuestion_user_state());
        holder.tv_question_title.setText(question.getQuestion_title());
//        holder.iv_option_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        final PopupMenu popup;
        popup = new PopupMenu(mContext, holder.iv_option_menu);
        popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
        final MenuItem edit_ad = popup.getMenu().findItem(R.id.edit_ad);
        final MenuItem delete_ad = popup.getMenu().findItem(R.id.delete_ad);
        final MenuItem report = popup.getMenu().findItem(R.id.report);
        holder.iv_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPrefManager.getUserDate().getId().equals(questionsList.get(position).getQuestion_user_id())) {
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
                    mQuestionOperationsListener.onOptionMenuClicked(position);
                } else if (item.getItemId() == R.id.edit_ad) {
                    Intent intent = new Intent(mContext, EditQuestion.class);
                    intent.putExtra("ques_id", questionsList.get(position).getQuestion_id());
                    mContext.startActivity(intent);
                } else if (item.getItemId() == R.id.delete_ad) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setIcon(R.mipmap.splash_logo)
                            .setMessage("تأكيد حذف السؤال ؟")
                            .setCancelable(false)
                            .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    delete_Ques(questionsList.get(position).getQuestion_id(), position);
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


        if (question.getFavorites_count() != null)
            if (!question.getFavorites_count().equals("0"))
                holder.likesCounterTextView.setText(question.getFavorites_count());
            else
                holder.likesCounterTextView.setText("");
        if (question.getQuestion_answers_count() != null)
            if (!question.getQuestion_answers_count().equals("0"))
                holder.answersCounterTextView.setText(question.getQuestion_answers_count());
            else
                holder.answersCounterTextView.setText("");
        if (question.getQuestion_image().isEmpty()) {
            holder.iv_question_image.setVisibility(View.GONE);
        } else {
            holder.iv_question_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Constants.imagesBaseUrl + question.getQuestion_image())
                    .error(R.mipmap.question_image_placeholder)
                    .placeholder(R.mipmap.question_image_placeholder)
                    .into(holder.iv_question_image);
        }
        Glide.with(mContext).load(question.getQuestion_user_image())
                .error(R.mipmap.person_place_holder)
                .placeholder(R.mipmap.person_place_holder)
                .into(holder.iv_user_image);
        if (question.isFavourite()) {
            holder.iv_favorite.setImageResource(R.mipmap.like_filled_icon);
        } else {
            holder.iv_favorite.setImageResource(R.mipmap.like_empty_icon);
        }
        if (mSharedPrefManager.getUserDate().getId().equals(question.getQuestion_user_id()))
            holder.iv_message.setVisibility(View.GONE);
        else
            holder.iv_message.setVisibility(View.VISIBLE);

    }

    public void addQuestionsList(ArrayList<QuestionModel> questionsList) {
        this.questionsList = questionsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_country)
        TextView tv_country;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_offers)
        TextView tv_offers;
        @BindView(R.id.tv_question_title)
        TextView tv_question_title;
        @BindView(R.id.iv_question_image)
        ImageView iv_question_image;
        @BindView(R.id.iv_answer)
        ImageView iv_answer;
        @BindView(R.id.iv_share)
        ImageView iv_share;
        @BindView(R.id.iv_option_menu)
        ImageView iv_option_menu;
        @BindView(R.id.iv_statistical)
        ImageView iv_statistical;
        @BindView(R.id.iv_favorite)
        ImageView iv_favorite;
        @BindView(R.id.iv_message)
        ImageView iv_message;
        @BindView(R.id.iv_user_image)
        CircleImageView iv_user_image;
        @BindView(R.id.likes_counter_text_view)
        TextView likesCounterTextView;
        @BindView(R.id.answers_counter_text_view)
        TextView answersCounterTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            iv_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onUserClicked(getAdapterPosition());
                }
            });
            tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onUserClicked(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onQuestionClicked(getAdapterPosition());
                }
            });
            iv_question_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onImageClicked(getAdapterPosition());
                }
            });
//            iv_answer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mQuestionOperationsListener.onAnswerClicked(getAdapterPosition());
//                }
//            });
            iv_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onMessageClicked(getAdapterPosition());
                }
            });
            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onShareClicked(getAdapterPosition());
                }
            });
            iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onFavouriteClicked(getAdapterPosition());
                }
            });
            iv_statistical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onStatisticalClicked(getAdapterPosition());
                }
            });
            tv_offers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQuestionOperationsListener.onOffersClicked(getAdapterPosition());
                }
            });
//            final PopupMenu popup;
//            popup = new PopupMenu(mContext, iv_option_menu);
//            popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
//            final MenuItem edit_ad = popup.getMenu().findItem(R.id.edit_ad);
//            final MenuItem delete_ad = popup.getMenu().findItem(R.id.delete_ad);
//            final MenuItem report = popup.getMenu().findItem(R.id.report);
//            iv_option_menu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mContext, "" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
//                    if (mSharedPrefManager.getUserDate().getId().equals(questionsList.get(getAdapterPosition()).getQuestion_user_id())) {
//                        edit_ad.setVisible(true);
//                        delete_ad.setVisible(true);
//                        report.setVisible(false);
//                    } else {
//                        edit_ad.setVisible(false);
//                        delete_ad.setVisible(false);
//                        report.setVisible(true);
//                    }
//                    popup.show();
//                }
//            });
//            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    if (item.getItemId() == R.id.report) {
//                        popup.dismiss();
//                        mQuestionOperationsListener.onOptionMenuClicked(getAdapterPosition());
//                    } else if (item.getItemId() == R.id.edit_ad) {
//                        Toast.makeText(mContext, "edit", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(mContext, "id: " + questionsList.get(getAdapterPosition()).getQuestion_id(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(mContext, EditQuestion.class);
//                        intent.putExtra("ques_id", questionsList.get(getAdapterPosition()).getQuestion_id());
//                        mContext.startActivity(intent);
//                    } else if (item.getItemId() == R.id.delete_ad) {
//                        Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setIcon(R.mipmap.splash_logo)
//                                .setMessage("تأكيد حذف السؤال ؟")
//                                .setCancelable(false)
//                                .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        delete_Ques(questionsList.get(getAdapterPosition()).getQuestion_id());
//                                    }
//                                }).setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
//                    }
//                    return false;
//                }
//            });
        }
    }

    private void delete_Ques(final String question_id, final int pos) {
        StringRequest delete_Request = new StringRequest(Request.Method.POST, Constants.askDelete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                System.out.println("Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("response").equals("true")) {
                        Toasty.success(mContext, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                        questionsList.remove(pos);
                        QuestionAdapter.this.notifyDataSetChanged();
                    } else {
                        Toasty.success(mContext, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                        QuestionAdapter.this.notifyDataSetChanged();
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
                params.put("ask_id", question_id);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                return params;
            }
        };

        System.out.println("URL : " + delete_Request.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        delete_Request.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(delete_Request);
    }
}
