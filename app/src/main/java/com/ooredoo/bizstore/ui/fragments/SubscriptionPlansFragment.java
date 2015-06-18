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

    boolean isPlanSelected = false;

    public SubscriptionPlansFragment() {
        super();
        layoutResId = R.layout.fragment_subscription_plans;
    }

    public void init(View parent) {
        btnNext = (Button) parent.findViewById(R.id.btn_next);
        btnDailyPlan = (ImageButton) parent.findViewById(R.id.btn_daily_plan);
        btnWeeklyPlan = (ImageButton) parent.findViewById(R.id.btn_weekly_plan);

        TextView tvDailyPlanBox = ((TextView) parent.findViewById(R.id.tv_daily_plan_details));
        TextView tvWeeklyPlanBox = ((TextView) parent.findViewById(R.id.tv_weekly_plan_details));

        btnNext.setOnClickListener(this);
        btnDailyPlan.setOnClickListener(this);
        btnWeeklyPlan.setOnClickListener(this);

        tvDailyPlanBox.setOnClickListener(this);
        tvWeeklyPlanBox.setOnClickListener(this);
        tvDailyPlanBox.setText(fromHtml(getString(R.string.daily_plan_details)));
        tvWeeklyPlanBox.setText(fromHtml(getString(R.string.weekly_plan_details)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_next) {
            if(isPlanSelected) {
                updateVal(mActivity, LOGIN_STATUS, true);
                SignUpActivity activity = (SignUpActivity) mActivity;
                activity.startActivity(HomeActivity.class);
            }
        } else if(id == R.id.btn_daily_plan || id == R.id.btn_weekly_plan || id == R.id.tv_daily_plan_details || id == R.id.tv_weekly_plan_details) {
            isPlanSelected = true;
            btnNext.setAlpha(1);
            boolean isDailyPlan = id == R.id.btn_daily_plan || id == R.id.tv_daily_plan_details;
            btnDailyPlan.setImageResource(isDailyPlan ? R.drawable.circle_check : R.drawable.circle_unchecked);
            btnWeeklyPlan.setImageResource(isDailyPlan ? R.drawable.circle_unchecked : R.drawable.circle_check);
        }
    }
}
