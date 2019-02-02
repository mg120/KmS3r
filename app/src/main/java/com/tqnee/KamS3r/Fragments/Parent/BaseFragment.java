package com.tqnee.KamS3r.Fragments.Parent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


/**
 * Created by Ramzy on 6/23/16.
 */

/**
 * this is the Parent fragment class which contains
 * shared data in other fragments
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (view != null && savedInstanceState != null) {
        } else {
            view = inflater.inflate(getLayoutResource(), container, false);
        }
        ButterKnife.bind(this, view);
        initializeComponents();
        return view;

    }

    protected abstract int getLayoutResource();

    protected abstract void initializeComponents();


}
