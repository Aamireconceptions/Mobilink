package com.ooredoo.bizstore.ui.fragments;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FragmentUtils;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SubscriptionPlansFragment extends BaseFragment {

    Button btnSubscribe;

    public SubscriptionPlansFragment() {
        super();
        layoutResId = R.layout.fragment_subscription_plans;
    }

    public void init(View parent) {
        TextView tvPlanDetails = (TextView) parent.findViewById(R.id.tv_plan_details);
        tvPlanDetails.setText(Html.fromHtml(getString(R.string.weekly_plan_details)));

        btnSubscribe = (Button) parent.findViewById(R.id.btn_subscribe);
        btnSubscribe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_subscribe) {
            updateVal(mActivity, LOGIN_STATUS, true);
            SignUpActivity activity = (SignUpActivity) mActivity;
            activity.startActivity(HomeActivity.class);

            FragmentUtils.replaceFragment(mActivity, R.id.fragment_container,
                                          DemoFragment.newInstance(), "demo_fragment");
        }
    }
}
