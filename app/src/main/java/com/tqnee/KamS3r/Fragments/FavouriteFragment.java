package com.tqnee.KamS3r.Fragments;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tqnee.KamS3r.Adapters.TabsViewPagerAdapter;
import com.tqnee.KamS3r.Fragments.Parent.BaseFragment;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.Fontss;
import com.tqnee.KamS3r.Utils.Utils;

import butterknife.BindView;

/**
 * Created by ramzy on 9/27/2017.
 */

public class FavouriteFragment extends BaseFragment {
    Context mContext;
    @BindView(R.id.tab_layout)
    TabLayout tab_layout;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    TabsViewPagerAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_favourite;
    }

    @Override
    protected void initializeComponents() {
        mContext = getActivity();
        setupViewPager();
        Utils.TabCustomFontSize(mContext, tab_layout, Fontss.regularFont, R.color.colorPrimary);
    }

    @Override
    public void onClick(View view) {

    }

    private void setupViewPager() {
        adapter = new TabsViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MyFavouriteOffers());
        adapter.addFragment(new FavouriteAnswerFragment());
        adapter.addFragment(new MyFavouriteQuestions());
        view_pager.setAdapter(adapter);
        tab_layout.setupWithViewPager(view_pager);
        tab_layout.getTabAt(0).setText(R.string.offers);
        tab_layout.getTabAt(1).setText(R.string.fav_answers);
        tab_layout.getTabAt(2).setText(R.string.fav_questions);
        tab_layout.getTabAt(2).select();
    }
}
