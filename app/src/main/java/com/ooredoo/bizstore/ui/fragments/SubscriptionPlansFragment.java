package com.ooredoo.bizstore.ui.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;

import static android.text.Html.fromHtml;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SubscriptionPlansFragment extends BaseFragment {

    Button btnNext;
    ImageButton btnDailyPlan, btnWeeklyPlan;

    public SubscriptionPlansFragment() {
        super();
        layoutResId = R.layout.fragment_subscription_plans;
    }

    public void init(View parent) {
        btnNext = (Button) parent.findViewById(R.id.btn_next);
        btnDailyPlan = (ImageButton) parent.findViewById(R.id.btn_daily_plan);

        TextView tvDailyPlanBox = ((TextView) parent.findViewById(R.id.tv_daily_plan_details));

        btnNext.setOnClickListener(this);
        btnDailyPlan.setOnClickListener(this);
        btnWeeklyPlan.setOnClickListener(this);

        tvDailyPlanBox.setOnClickListener(this);
        tvDailyPlanBox.setText(fromHtml(getString(R.string.daily_plan_details)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_next) {
            updateVal(mActivity, LOGIN_STATUS, true);
            SignUpActivity activity = (SignUpActivity) mActivity;
            activity.startActivity(HomeActivity.class);
        }
    }
}
