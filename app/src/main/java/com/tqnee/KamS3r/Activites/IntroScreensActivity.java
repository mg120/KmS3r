package com.tqnee.KamS3r.Activites;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tqnee.KamS3r.Activites.Parent.ParentActivity;
import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;

import butterknife.BindView;
import me.relex.circleindicator.CircleIndicator;

public class IntroScreensActivity extends ParentActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    Context mContext;
    SharedPrefManager mSharedPrefManager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {


            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));

            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void initializeComponents() {
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        mSharedPrefManager.setFirstTime(false);

        layouts = new int[]{
                R.layout.intro_slider_one,
                R.layout.intro_slider_two,
                R.layout.intro_slider_three,
                R.layout.intro_slider_four,
                R.layout.intro_slider_five,
                R.layout.intro_slider_six};


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        indicator.setViewPager(viewPager);
        myViewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_intro_screens;
    }

    @Override
    protected boolean isEnableToolbar() {
        return false;
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

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(mContext, SelectCountryActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
