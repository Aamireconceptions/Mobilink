package com.ooredoo.bizstore.ui.fragments;

import android.view.View;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SubscriptionPlansFragment extends BaseFragment {

    public SubscriptionPlansFragment() {
        super();
        layoutResId = R.layout.fragment_subscription_plans;
    }

    public void init(View parent) {
        parent.findViewById(R.id.ll_next).setOnClickListener(this);
        ((TextView) parent.findViewById(R.id.tv_daily_plan_details)).setText(getString(R.string.daily_plan_details));
        ((TextView) parent.findViewById(R.id.tv_weekly_plan_details)).setText(getString(R.string.weekly_plan_details));
    }

    @Override
    public void onClick(View v) {
        SignUpActivity activity = (SignUpActivity) mActivity;
        activity.startActivity(HomeActivity.class);
    }
}
