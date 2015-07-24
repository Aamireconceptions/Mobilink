package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.DemoPagerAdapter;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

/**
 * Created by Babar on 24-Jul-15.
 */
public class DemoFragment extends Fragment implements View.OnClickListener {
    private SignUpActivity signUpActivity;

    private Activity activity;

    public static DemoFragment newInstance()
    {
        return new DemoFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_demo, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {
        activity = getActivity();

        signUpActivity = (SignUpActivity) activity;
        signUpActivity.toolbar.setVisibility(View.GONE);

        DemoPagerAdapter adapter = new DemoPagerAdapter(getFragmentManager());

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.pager_indicator);
        circlePageIndicator.setViewPager(viewPager);
        circlePageIndicator.setFillColor(activity.getResources().getColor(R.color.black));

        TextView tvSkip = (TextView) v.findViewById(R.id.skip);
        tvSkip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        SharedPrefUtils.updateVal(activity, SharedPrefUtils.LOGIN_STATUS, true);

        signUpActivity.startActivity(HomeActivity.class);
    }
}