package com.tqnee.KamS3r.Activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.karan.churi.PermissionManager.PermissionManager;
import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.App.AppController;
import com.tqnee.KamS3r.Fragments.HashtagFragment;
import com.tqnee.KamS3r.Fragments.OffersFragment;
import com.tqnee.KamS3r.Fragments.ProfileFragment;
import com.tqnee.KamS3r.Fragments.QuestionFragment;
import com.tqnee.KamS3r.Fragments.TrendFragment;
import com.tqnee.KamS3r.Model.CategoriesModel;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.Fontss;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;
import com.tqnee.KamS3r.Widgets.QuestionOrAdsDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends ParentActivity {
    @BindView(R.id.activity_home_tab)
    TabLayout home_tab_layout;
    @BindView(R.id.iv_notification)
    ImageView iv_notification;
    @BindView(R.id.iv_messages_icon)
    ImageView iv_messages_icon;
    @BindView(R.id.tv_notification_count)
    TextView tv_notification_count;
    @BindView(R.id.tv_messages_count)
    TextView tv_messages_count;
    @BindView(R.id.add_fab)
    FloatingActionButton addFab;

    Context mContext;
    Activity mainActivity;

    Intent mIntent;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SharedPrefManager mSharedPrefManager;
    int selectedTab = 0;
    public static ArrayList<CategoriesModel> categoriesList;
    PermissionManager permissionManager;

    int selected;

    @Override
    protected void initializeComponents() {
        mContext = this;
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        mSharedPrefManager = new SharedPrefManager(mContext);
        fragmentManager = getSupportFragmentManager();
        mainActivity = this;
        SetupTabLayout();
        if (getIntent().getExtras() != null) {
            selected = getIntent().getExtras().getInt("selected");
        }
        if (selected == 1) {
            fragment = new QuestionFragment();
            home_tab_layout.getTabAt(0).select();
            home_tab_layout.getTabAt(0).setText(R.string.home);
            Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
            DrawProfileView(fragment, 0);
        } else if (selected == 2) {
            fragment = new OffersFragment();
            home_tab_layout.getTabAt(3).select();
            home_tab_layout.getTabAt(3).setText(R.string.ads_tab);
            Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
            DrawProfileView(fragment, 3);
        } else {
            fragment = new QuestionFragment();
            home_tab_layout.getTabAt(0).select();
            home_tab_layout.getTabAt(0).setText(R.string.home);
            Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
            DrawProfileView(fragment, 0);
        }
//        fragment = new QuestionFragment();
//        SetupTabLayout();
//        home_tab_layout.getTabAt(0).select();
//        home_tab_layout.getTabAt(0).setText(R.string.home);
//        Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
//        DrawProfileView(fragment, 0);
        if (mSharedPrefManager.getLoginStatus()) {
            iv_notification.setVisibility(View.VISIBLE);
            iv_messages_icon.setVisibility(View.VISIBLE);
            loadCount(Constants.loadCountUrl);
        } else {
            iv_notification.setVisibility(View.GONE);
            iv_messages_icon.setVisibility(View.GONE);
        }

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSharedPrefManager.getLoginStatus()) {
                    QuestionOrAdsDialog questionOrAdsDialog = new QuestionOrAdsDialog(HomeActivity.this, tv_messages_count);
                    questionOrAdsDialog.show();
                } else
                    Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.you_should_login), R.color.colorError);
            }
        });

        getCategories();
    }

    private void getCategories() {
        categoriesList = new ArrayList<>();
        StringRequest verifyReq = new StringRequest(Request.Method.GET, Constants.categoriesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(categories)", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject categoryObject = jsonArray.optJSONObject(i);
                                CategoriesModel categoryModel = new CategoriesModel();
                                categoryModel.setCategory_id(String.valueOf(categoryObject.optInt("id")));
                                categoryModel.setCategory_name(categoryObject.optString("name"));
                                categoryModel.setCategory_image(categoryObject.optString("app_icon"));
                                categoriesList.add(categoryModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Toast.makeText(mContext, mContext.getString(R.string.time_out), Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError)
                            Toast.makeText(mContext, mContext.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        else if (error instanceof ServerError)
                            Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                });


        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected boolean isEnableToolbar() {
        return true;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean isEnableBack() {
        return false;
    }

    @Override
    protected boolean hideInputType() {
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //To Setup Tab Layout with out viewPager
    private void SetupTabLayout() {
        home_tab_layout.addTab(home_tab_layout.newTab().setIcon(R.drawable.tab_icon_home));
        home_tab_layout.addTab(home_tab_layout.newTab().setIcon(R.drawable.tab_icon_most_active));
        home_tab_layout.addTab(home_tab_layout.newTab().setIcon(R.drawable.tab_icon_hashtag));
        home_tab_layout.addTab(home_tab_layout.newTab().setIcon(R.drawable.tab_icon_statistical));
        home_tab_layout.addTab(home_tab_layout.newTab().setIcon(R.drawable.tab_icon_profile));

        home_tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectTab(tab.getPosition());
            }
        });
    }

    private void selectTab(int position) {
        switch (position) {
            case 0:
//                if (selectedTab != 0) {
                selectedTab = position;
                home_tab_layout.getTabAt(0).setText(R.string.home);
                home_tab_layout.getTabAt(1).setText("");
                home_tab_layout.getTabAt(2).setText("");
                home_tab_layout.getTabAt(3).setText("");
                home_tab_layout.getTabAt(4).setText("");
                fragment = new QuestionFragment();
                DrawProfileView(fragment, 0);
                Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
//                }
                break;
            case 1:
                if (selectedTab != 1) {
                    selectedTab = position;
                    home_tab_layout.getTabAt(1).setText(R.string.most_active);
                    home_tab_layout.getTabAt(0).setText("");
                    home_tab_layout.getTabAt(2).setText("");
                    home_tab_layout.getTabAt(3).setText("");
                    home_tab_layout.getTabAt(4).setText("");
                    fragment = new TrendFragment();
                    DrawProfileView(fragment, 1);
                    Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
                }
                break;
            case 2:
                if (selectedTab != 2) {
                    selectedTab = position;
                    if (mSharedPrefManager.getLoginStatus()) {
                        home_tab_layout.getTabAt(4).setText("");
                        home_tab_layout.getTabAt(1).setText("");
                        home_tab_layout.getTabAt(2).setText("اهتماماتى");
                        home_tab_layout.getTabAt(3).setText("");
                        home_tab_layout.getTabAt(0).setText("");
                        fragment = new HashtagFragment();
                        DrawProfileView(fragment, 2);
                    } else
                        Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.you_should_login), R.color.colorError);
                }
                break;
            case 3:
                if (selectedTab != 3) {
                    selectedTab = position;
                    home_tab_layout.getTabAt(3).setText(R.string.ads_tab);
                    home_tab_layout.getTabAt(1).setText("");
                    home_tab_layout.getTabAt(2).setText("");
                    home_tab_layout.getTabAt(0).setText("");
                    home_tab_layout.getTabAt(4).setText("");
                    fragment = new OffersFragment();
                    DrawProfileView(fragment, 3);
                    Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
                }
                break;
            case 4:
                if (selectedTab != 4) {
                    if (mSharedPrefManager.getLoginStatus()) {
                        selectedTab = position;
                        home_tab_layout.getTabAt(4).setText(R.string.profile_tab);
                        home_tab_layout.getTabAt(1).setText("");
                        home_tab_layout.getTabAt(2).setText("");
                        home_tab_layout.getTabAt(3).setText("");
                        home_tab_layout.getTabAt(0).setText("");
                        fragment = new ProfileFragment();
                        DrawProfileView(fragment, 4);
                        Utils.TabCustomFontSize(mContext, home_tab_layout, Fontss.regularFont, R.color.colorPrimary);
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }
                break;
        }
    }

    private void DrawProfileView(Fragment fragment, int position) {
        ReplaceFragment(fragment);
        if (position == 0) {
            setToolbarTitle(getString(R.string.home));
            createOptionsMenu(R.menu.search_menu);
        } else if (position == 1) {
            createOptionsMenu(R.menu.search_menu);
            setToolbarTitle(getString(R.string.most_active));
        } else if (position == 2) {
            createOptionsMenu(R.menu.search_menu);
            setToolbarTitle(getString(R.string.hashtag));
        } else if (position == 3) {
            createOptionsMenu(R.menu.search_menu);
            setToolbarTitle(getString(R.string.ads_tab));
        } else if (position == 4) {
            if (mSharedPrefManager.getLoginStatus()) {
                createOptionsMenu(R.menu.logout);
            } else {
                removeOptionsMenu();
            }
            setToolbarTitle(getString(R.string.profile_tab));
        }
        if (mSharedPrefManager.getLoginStatus()) {
            loadCount(Constants.loadCountUrl);
        }

    }

    @SuppressLint("CommitTransaction")
    private void ReplaceFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            mIntent = new Intent(mContext, SearchActivity.class);
            startActivity(mIntent);
        } else if (item.getItemId() == R.id.app_bar_edit) {
            mIntent = new Intent(mContext, UpdateProfileActivity.class);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_notification)
    void onNotificationClicked() {
        mIntent = new Intent(mContext, NotificationActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.iv_messages_icon)
    void onMessagesClicked() {
        mIntent = new Intent(mContext, MessagesActivity.class);
        startActivity(mIntent);
    }

    private void loadCount(String serviceUrl) {
        StringRequest verifyReq = new StringRequest(Request.Method.POST, serviceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.printLog(Constants.LOG_TAG + "(notification count)", response);
                        parseNotificationCountResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.time_out), R.color.colorError);
                        } else if (error instanceof NoConnectionError)
                            Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.no_connection), R.color.colorError);
                        else if (error instanceof ServerError)
                            Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.server_error), R.color.colorError);
                        else if (error instanceof NetworkError)
                            Utils.showSnackBar(mContext, tv_messages_count, getString(R.string.no_connection), R.color.colorError);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", mSharedPrefManager.getUserDate().getApiToken());
                return params;
            }
        };
        System.out.println("Count url : " + verifyReq.getUrl());
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance(mContext).addToRequestQueue(verifyReq);
    }

    void parseNotificationCountResponse(String response) {
        try {
            Utils.printLog(Constants.LOG_TAG + "(notification count)", response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("response").equals("true")) {
                int notifiyCount = jsonObject.optInt("notifyCount");
                int messageCount = jsonObject.optInt("messageCount");
                if (notifiyCount == 0) {
                    tv_notification_count.setVisibility(View.GONE);
                } else {
                    tv_notification_count.setVisibility(View.VISIBLE);
                    tv_notification_count.setText(notifiyCount + "");
                }
                if (messageCount == 0) {
                    tv_messages_count.setVisibility(View.GONE);
                } else {
                    tv_messages_count.setVisibility(View.VISIBLE);
                    tv_messages_count.setText(messageCount + "");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSharedPrefManager.getLoginStatus()) {
            loadCount(Constants.loadCountUrl);
        }

    }
}
