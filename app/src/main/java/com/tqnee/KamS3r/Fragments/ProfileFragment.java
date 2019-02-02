package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.tqnee.KamS3r.Adapters.TabsViewPagerAdapter;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.Fontss;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 9/13/2017.
 */

public class ProfileFragment extends BaseFragment {
    Context mContext;
    @BindView(R.id.profile_tab_layout)
    TabLayout profile_tab_layout;
    @BindView(R.id.profile_view_pager)
    ViewPager profile_view_pager;
    TabsViewPagerAdapter adapter;
    ArrayList<Integer> tabsIconsList;
    SharedPrefManager mSharedPrefManager;
    @BindView(R.id.layout_content)
    LinearLayout layout_content;
    @BindView(R.id.iv_user_cover)
    ImageView iv_user_cover;
    @BindView(R.id.iv_user_image)
    CircleImageView iv_user_image;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_country)
    TextView tv_country;
    @BindView(R.id.tv_login_status)
    TextView tv_login_status;
    @BindView(R.id.my_asks_num_txt_id)
    TextView my_asks_num;
    @BindView(R.id.my_answers_num_txt_id)
    TextView my_answers_num;
    @BindView(R.id.my_ads_num_txt_id)
    TextView my_ads_num;


    public static String notifyCount, messageCount, asksCount, answersCount, adsCount;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_profile;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        tabsIconsList = new ArrayList<>();
        mSharedPrefManager = new SharedPrefManager(mContext);
        if (mSharedPrefManager.getLoginStatus()) {
            fillData();
            getUnseenData(mSharedPrefManager.getUserDate().getApiToken());
            setupViewPager();
        } else {
            tv_login_status.setVisibility(View.VISIBLE);
        }
    }

    private void fillData() {
        tv_user_name.setText(mSharedPrefManager.getUserDate().getName());
        tv_country.setText(mSharedPrefManager.getUserDate().getCountry());
        Glide.with(mContext).load(Constants.imagesBaseUrl + mSharedPrefManager.getUserDate().getPhoto())
//                .error(R.mipmap.person_place_holder)
//                .placeholder(R.mipmap.person_place_holder)
                .into(iv_user_image);
        Glide.with(mContext).load(Constants.imagesBaseUrl + mSharedPrefManager.getUserDate().getCover_image())
                .error(R.color.colorPrimary)
                .placeholder(R.color.colorPrimary)
                .into(iv_user_cover);
    }

    private void getUnseenData(final String apiToken) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.unseen_Data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    notifyCount = jsonObject.getString("notifyCount");
                    messageCount = jsonObject.getString("messageCount");
                    asksCount = jsonObject.getString("asksCount");
                    answersCount = jsonObject.getString("answersCount");
                    adsCount = jsonObject.getString("adsCount");

                    my_asks_num.setText(asksCount);
                    my_answers_num.setText(answersCount);
                    my_ads_num.setText(adsCount);
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
                params.put("api_token", apiToken);
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    private void setupViewPager() {
        adapter = new TabsViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FavouriteFragment());
        adapter.addFragment(AdsFragment.newInstance(mSharedPrefManager.getUserDate().getId()));
        adapter.addFragment(ProfileOffersFragment.newInstance(mSharedPrefManager.getUserDate().getId()));
        adapter.addFragment(ProfileQuestionsFragment.newInstance(mSharedPrefManager.getUserDate().getId()));
        profile_view_pager.setAdapter(adapter);
        profile_tab_layout.setupWithViewPager(profile_view_pager);
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        profile_tab_layout.getTabAt(0).setIcon(R.drawable.like_filled_icon).setText(getString(R.string.favourite));
        profile_tab_layout.getTabAt(1).setIcon(R.drawable.offers_blue_icon).setText(getString(R.string.my_ads));
        profile_tab_layout.getTabAt(2).setIcon(R.drawable.offers).setText(getString(R.string.my_offers));
        profile_tab_layout.getTabAt(3).setIcon(R.drawable.add).setText(getString(R.string.my_questions));
        Utils.TabCustomFontSize(mContext, profile_tab_layout, Fontss.regularFont, R.color.colorPrimary);
        profile_tab_layout.getTabAt(3).select();
    }

    @Override
    public void onClick(View view) {

    }
}
