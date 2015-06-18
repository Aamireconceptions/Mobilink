package com.ooredoo.bizstore.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author pehlaj.rai
 * @since 16-Jun-15
 */
public class TopDealFragment extends Fragment {

    HomeActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_deal, container, false);

        mActivity = (HomeActivity) getActivity();
        init(v);

        return v;
    }

    private void init(View v) {
    }

    public static TopDealFragment newInstance() {
        TopDealFragment fragment = new TopDealFragment();
        return fragment;
    }
}