package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class HomePromoFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_promo, container, false);
        v.findViewById(R.id.iv_promo_banner).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_promo_banner) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            DealDetailFragment fragment = new DealDetailFragment();
            fragment.showBanner = true;
            fragment.bannerResId = R.drawable.tmp_listing;
            homeActivity.showFragment(fragment);
        }
    }

    public static HomePromoFragment newInstance() {
        HomePromoFragment fragment = new HomePromoFragment();
        return fragment;
    }
}