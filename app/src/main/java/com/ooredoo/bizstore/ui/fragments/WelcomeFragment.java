package com.ooredoo.bizstore.ui.fragments;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

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
        parent.findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        updateVal(mActivity, LOGIN_STATUS, true);
        SignUpActivity activity = (SignUpActivity) mActivity;
        activity.startActivity(HomeActivity.class);
        /*AppCompatActivity activity = (AppCompatActivity) mActivity;
        FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container, new SubscriptionPlansFragment(), "Welcome");*/
    }
}
