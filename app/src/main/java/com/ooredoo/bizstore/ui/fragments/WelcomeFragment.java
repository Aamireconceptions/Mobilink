package com.ooredoo.bizstore.ui.fragments;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.FragmentUtils;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class WelcomeFragment extends BaseFragment {

    public WelcomeFragment() {
        super();
        layoutResId = R.layout.fragment_welcome;
    }

    public void init(View parent) {
        parent.findViewById(R.id.ll_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AppCompatActivity activity = (AppCompatActivity) mActivity;
        FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container, new SubscriptionPlansFragment(), "Welcome");
    }
}
