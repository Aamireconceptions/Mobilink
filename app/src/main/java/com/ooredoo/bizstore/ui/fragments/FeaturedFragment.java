package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class FeaturedFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_featured, container, false);
        v.findViewById(R.id.iv_featured_banner).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_featured_banner) {
            startActivity(new Intent(getActivity(), DealDetailActivity.class));
        }
    }

    public static FeaturedFragment newInstance() {
        FeaturedFragment fragment = new FeaturedFragment();
        return fragment;
    }
}