package com.ooredoo.bizstore.ui.fragments;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FragmentUtils;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class WelcomeFragment extends BaseFragment {

    private AppCompatActivity activity;

    public WelcomeFragment() {
        super();
        layoutResId = R.layout.fragment_welcome;
    }

    public void init(View parent) {
        parent.findViewById(R.id.btn_next).setOnClickListener(this);

        activity = (AppCompatActivity) mActivity;

        SignUpActivity signUpActivity = (SignUpActivity) activity;
        signUpActivity.toolbar.setVisibility(View.GONE);
        SignUpActivity.isWelcomeScreen = true;
    }

    @Override
    public void onClick(View v) {

        FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container,
                                        DemoFragment.newInstance(), "demo_fragment");
    }
}
