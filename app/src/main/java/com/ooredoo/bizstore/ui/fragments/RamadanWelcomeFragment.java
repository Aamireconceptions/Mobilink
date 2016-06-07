package com.ooredoo.bizstore.ui.fragments;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.TimeUtils;

import java.lang.reflect.Type;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class RamadanWelcomeFragment extends BaseFragment {

    private AppCompatActivity activity;

    public RamadanWelcomeFragment() {
        super();
        layoutResId = R.layout.fragment_welcome_ramadan;
    }

    Tracker tracker;

    public void init(View parent) {
        Button btNext = (Button) parent.findViewById(R.id.btn_next);
        btNext.setOnClickListener(this);

        activity = (AppCompatActivity) mActivity;

        FontUtils.setFontWithStyle(activity, btNext, Typeface.BOLD);

        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.toolbar.setVisibility(View.GONE);
        MainActivity.hideToolbar = true;

        BizStore bizStore = (BizStore) activity.getApplication();

        TextView tvCongrats = (TextView) parent.findViewById(R.id.tv_congratz);
        FontUtils.setFontWithStyle(activity, tvCongrats, Typeface.BOLD);

        TextView tvMsg = (TextView) parent.findViewById(R.id.special_msg);
        FontUtils.setFontWithStyle(activity, tvMsg, Typeface.BOLD);

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

        MainActivity activity = (MainActivity) mActivity;
        activity.startActivity(HomeActivity.class);

        tracker.send(new HitBuilders.EventBuilder()
               .setCategory("Action")
               .setAction("Log in")
               .build());
    }

}
