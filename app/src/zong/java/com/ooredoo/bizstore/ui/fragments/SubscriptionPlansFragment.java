package com.ooredoo.bizstore.ui.fragments;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.FragmentUtils;

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
        MainActivity mainActivity = (MainActivity) mActivity;
        mainActivity.toolbar.setVisibility(View.GONE);
        MainActivity.hideToolbar = true;
        btnSubscribe = (Button) parent.findViewById(R.id.btn_subscribe);
        btnSubscribe.setOnClickListener(this);

        FontUtils.setFontWithStyle(mainActivity, btnSubscribe, Typeface.BOLD);

        TextView tvFreeTrialNote = (TextView) parent.findViewById(R.id.free_trial_note);
        FontUtils.setFontWithStyle(mainActivity, tvFreeTrialNote, Typeface.BOLD);

        TextView tvPostPaid = (TextView) parent.findViewById(R.id.post_paid);
        FontUtils.setFontWithStyle(mainActivity, tvPostPaid, Typeface.BOLD);

        TextView tvPrePaid = (TextView) parent.findViewById(R.id.pre_paid);
        FontUtils.setFontWithStyle(mainActivity, tvPrePaid, Typeface.BOLD);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_subscribe) {

            AppCompatActivity compatActivity = (AppCompatActivity) mActivity;
            FragmentUtils.replaceFragmentWithBackStack(compatActivity, R.id.fragment_container,
                    new SignUpFragment(), "SignUp");
        }
    }
}