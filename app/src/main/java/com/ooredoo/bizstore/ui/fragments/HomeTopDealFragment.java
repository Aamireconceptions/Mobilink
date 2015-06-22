package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * Created by Babar on 19-Jun-15.
 */
public class HomeTopDealFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_top_deal, container, false);
        v.findViewById(R.id.iv_featured_banner).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_featured_banner) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            DealDetailFragment fragment = new DealDetailFragment();
            fragment.showBanner = true;
            homeActivity.showFragment(fragment);
        }
    }

    public static HomeTopDealFragment newInstance() {
        HomeTopDealFragment fragment = new HomeTopDealFragment();
        return fragment;
    }
}