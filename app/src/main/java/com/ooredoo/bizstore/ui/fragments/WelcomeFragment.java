package com.ooredoo.bizstore.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class WelcomeFragment extends BaseFragment {

    private AppCompatActivity activity;
    BizStore bizStore;
    public WelcomeFragment() {
        super();
        layoutResId = R.layout.fragment_welcome;
    }

    Tracker tracker, ooredooTracker;

    public void init(View parent) {
        Button btNext = (Button) parent.findViewById(R.id.btn_next);
        btNext.setOnClickListener(this);

        activity = (AppCompatActivity) mActivity;

        FontUtils.setFontWithStyle(activity, btNext, Typeface.BOLD);
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.toolbar.setVisibility(View.GONE);
        MainActivity.hideToolbar = true;

        TextView tvCongrats = (TextView) parent.findViewById(R.id.tv_congratz);
        FontUtils.setFontWithStyle(activity, tvCongrats, Typeface.BOLD);

        bizStore = (BizStore) activity.getApplication();

        tracker = bizStore.getDefaultTracker();

    }

    @Override
    public void onClick(View v) {

        /*FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container,
                                        DemoFragment.newInstance(), "demo_fragment");*/

        SharedPrefUtils.updateVal(activity, SharedPrefUtils.LOGIN_STATUS, true);

        //activity.finish();

        MainActivity activity = (MainActivity) mActivity;
       activity.startActivity(HomeActivity.class);

        /*getActivity().finish();
        getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));*/

        Map<String, String> loginEvent = new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Log in")
                .build();

        tracker.send(loginEvent);

        if(BuildConfig.FLAVOR.equals("ooredoo"))
        {
            ooredooTracker = bizStore.getOoredooTracker();
            ooredooTracker.send(loginEvent);
        }
    }
}