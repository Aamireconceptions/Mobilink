package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Pehlaj Rai
 * @since 19-Jun-15.
 */
public class DealDetailFragment extends Fragment implements View.OnClickListener {
    public boolean showBanner = false;

    public int bannerResId = R.drawable.tmp_banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deal_details, container, false);
        ((ImageView) v.findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);
        v.findViewById(R.id.iv_back).setOnClickListener(this);
        v.findViewById(R.id.tv_hdr_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.iv_deal_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        return v;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_back) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.showTabs();
            if(!showBanner) {
                homeActivity.showHideSearchBar(true);
            }
        }
    }
}