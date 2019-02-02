package com.tqnee.KamS3r.Fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tqnee.KamS3r.Adapters.TabsViewPagerAdapter;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Constants;
import com.tqnee.KamS3r.Utils.Fontss;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
import com.tqnee.KamS3r.Utils.Utils;

import butterknife.BindView;

/**
 * Created by Kamal Marcus on 31/10/2017.
 * kamalmarcus94@gmail.com
 * +201015793659
 */

public class HashtagFragment extends BaseFragment {
    @BindView(R.id.hashtag_tab_layout)
    TabLayout hashtagTabLayout;
    @BindView(R.id.hashtag_view_pager)
    ViewPager hashtagViewPager;
    private FragmentActivity mContext;
    private SharedPrefManager mSharedPrefManager;
    private TabsViewPagerAdapter adapter;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_hashtag;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        mSharedPrefManager = new SharedPrefManager(mContext);
        Constants.isHashtag=true;
        setupViewPager();
    }

    private void setupViewPager() {
        adapter = new TabsViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new HashtagQuestionsFragment());
        adapter.addFragment(new HashtagOffersFragment());
        hashtagViewPager.setAdapter(adapter);
        hashtagTabLayout.setupWithViewPager(hashtagViewPager);
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        hashtagTabLayout.getTabAt(0).setIcon(R.drawable.add).setText(getString(R.string.fav_questions));
        hashtagTabLayout.getTabAt(1).setIcon(R.drawable.offers).setText(getString(R.string.ads_tab));
        Utils.TabCustomFontSize(mContext, hashtagTabLayout, Fontss.regularFont, R.color.colorPrimary);
        hashtagTabLayout.getTabAt(1).select();
    }


    @Override
    public void onClick(View view) {

    }
}
