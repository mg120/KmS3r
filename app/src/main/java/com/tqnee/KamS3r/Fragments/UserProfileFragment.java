package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramzy on 9/13/2017.
 */

public class UserProfileFragment extends BaseFragment {
    public static String USER_ID = "user_id";
    Context mContext;
    @BindView(R.id.profile_tab_layout)
    TabLayout profile_tab_layout;
    @BindView(R.id.profile_view_pager)
    ViewPager profile_view_pager;
    TabsViewPagerAdapter adapter;
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
    @BindView(R.id.my_profile_info_layout_id)
    LinearLayout user_profile_info_layout;
    @BindView(R.id.my_ads_num_txt_id)
    TextView ads_txt;
    @BindView(R.id.my_asks_num_txt_id)
    TextView asks_txt;
    @BindView(R.id.my_answers_num_txt_id)
    TextView answers_txt;


    //    CustomLoadingDialog mCustomLoadingDialog;
    String currentUserID;

    public static UserProfileFragment newInstance(String user_id) {
        Bundle args = new Bundle();
        args.putString(USER_ID, user_id);
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_profile;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        mSharedPrefManager = new SharedPrefManager(mContext);
        currentUserID = getArguments().getString(USER_ID);
        loadUserData(Constants.userProfileDataUrl);

//        user_profile_info_layout.setVisibility(View.GONE);
    }

    private void setupViewPager() {
        adapter = new TabsViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(AdsFragment.newInstance(currentUserID));
        adapter.addFragment(ProfileOffersFragment.newInstance(currentUserID));
        adapter.addFragment(ProfileQuestionsFragment.newInstance(currentUserID));
        profile_view_pager.setAdapter(adapter);
        profile_tab_layout.setupWithViewPager(profile_view_pager);
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        profile_tab_layout.getTabAt(0).setIcon(R.drawable.offers_blue_icon).setText("الاعلانات");
        profile_tab_layout.getTabAt(1).setIcon(R.drawable.offers).setText("العروض");
        profile_tab_layout.getTabAt(2).setIcon(R.drawable.add).setText("الاسئلة");
        Utils.TabCustomFontSize(mContext, profile_tab_layout, Fontss.regularFont, R.color.colorPrimary);
        profile_tab_layout.getTabAt(2).select();
    }

    @Override
    public void onClick(View view) {

    }

    private void loadUserData(String serviceUrl) {
//        mCustomLoadingDialog = new CustomLoadingDialog(mContext);
//        mCustomLoadingDialog.show();
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res: " + response);
                        Log.e("ress: ", response);
                        setupViewPager();
//                        mCustomLoadingDialog.dismiss();
                        Utils.printLog(Constants.LOG_TAG + "(UserData)", response);
                        parseProfileData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        mCustomLoadingDialog.dismiss();
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, layout_content, getString(R.string.no_connection), R.color.colorError);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", currentUserID);
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());

                System.out.println("params : " + params);
                return params;
            }
        };

        System.out.println("URL : " + verifyReq.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    private void parseProfileData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optBoolean("response")) {
                JSONObject userObject = jsonObject.optJSONObject("users");
                String userName = userObject.optString("fullname");
                String country = "";
                if (!userObject.isNull("get_mobile_country"))
                    country = userObject.getJSONObject("get_mobile_country").getString("ar_name");
                String userPhoto = userObject.optString("photo");
                String userCover = userObject.optString("cover");
                String asks_count = userObject.getString("asks_count");
                String answer_count = userObject.getString("answer_count");
                String offers_count = userObject.getString("offer_count");

                tv_user_name.setText(userName);
                tv_country.setText(country);
                asks_txt.setText(asks_count);
                ads_txt.setText(offers_count);
                answers_txt.setText(answer_count);


                Glide.with(mContext).load(userPhoto)
//                        .placeholder(R.mipmap.person_place_holder)
//                        .error(R.mipmap.person_place_holder)
                        .into(iv_user_image);
                Glide.with(mContext).load(Constants.imagesBaseUrl + userCover)
                        .placeholder(R.color.colorPrimary)
                        .error(R.color.colorPrimary)
                        .into(iv_user_cover);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
