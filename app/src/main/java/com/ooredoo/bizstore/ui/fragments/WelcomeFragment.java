package com.ooredoo.bizstore.ui.fragments;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.TimeUtils;

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

    Tracker tracker;

    public void init(View parent) {
        parent.findViewById(R.id.btn_next).setOnClickListener(this);

        activity = (AppCompatActivity) mActivity;

        SignUpActivity signUpActivity = (SignUpActivity) activity;
        signUpActivity.toolbar.setVisibility(View.GONE);
        SignUpActivity.hideToolbar = true;

        BizStore bizStore = (BizStore) activity.getApplication();

        tracker = bizStore.getDefaultTracker();

        if(TimeUtils.isDateOver(6, 07, 2016))
        {
            TextView tvSpecialMsg = (TextView) parent.findViewById(R.id.special_msg);
            tvSpecialMsg.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        /*FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container,
                                        DemoFragment.newInstance(), "demo_fragment");*/

        SharedPrefUtils.updateVal(activity, SharedPrefUtils.LOGIN_STATUS, true);

        //activity.finish();

        SignUpActivity activity = (SignUpActivity) mActivity;
        activity.startActivity(HomeActivity.class);

        tracker.send(new HitBuilders.EventBuilder()
               .setCategory("Action")
               .setAction("Log in")
               .build());
    }

}
