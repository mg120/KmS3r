package com.tqnee.KamS3r.Activites.Parent;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tqnee.KamS3r.R;
import com.tqnee.KamS3r.Utils.SharedPrefManager;
//import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.ButterKnife;


/**
 * Created by ramzy on 4/19/2017.
 */

/**
 * this is the parent class which contains
 * the shared data between other activities
 * like toolbar  etc...
 */

public abstract class ParentActivity extends AppCompatActivity implements View.OnClickListener {

    protected AppCompatActivity activity;
    protected SharedPrefManager mSharedPrefManager;


    Toolbar toolbar;
    TextView tv_toolbar_title;
    Context mContext;
    private int menuId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (hideInputType()) {
            hideInputTyping();
        }
        setContentView(getLayoutResource());
        activity = this;
        mSharedPrefManager = new SharedPrefManager(activity);
        ButterKnife.bind(this);
        if (isEnableToolbar()) {
            configureToolbar();
        }
        initializeComponents();
    }

    public void setToolbarTitle(String titleId) {
        if (toolbar != null) {
            tv_toolbar_title.setText(titleId);
        }
    }

    protected abstract void initializeComponents();

    /**
     * this method is responsible for configure toolbar
     * it is called when I enable toolbar in my activity
     */
    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        // check if enable back
        if (isEnableBack()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.mipmap.back_icon);

        }
    }

    /**
     * @return the layout resource id
     */
    protected abstract int getLayoutResource();

    /**
     * it is a boolean method which tell my if toolbar
     * is enabled or not
     *
     * @return
     */
    protected abstract boolean isEnableToolbar();

    /**
     * it is a boolean method which tell if full screen mode
     * is enabled or not
     *
     * @return
     */
    protected abstract boolean isFullScreen();

    /**
     * it is a boolean method which tell if back button
     * is enabled or not
     *
     * @return
     */
    protected abstract boolean isEnableBack();

    /**
     * it is a boolean method which tell if input is
     * is appeared  or not
     *
     * @return
     */
    protected abstract boolean hideInputType();

    /**
     * this method allowed me to create option menu
     */
    public void createOptionsMenu(int menuId) {
        this.menuId = menuId;
        invalidateOptionsMenu();
    }

    /**
     * this method allowed me to remove option menu
     */
    public void removeOptionsMenu() {
        menuId = 0;
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menuId != 0) {
            getMenuInflater().inflate(menuId, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    public void hideInputTyping() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * this method allowed me to initialize the typeKit for font change
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}