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
public class BusinessDetailFragment extends Fragment implements View.OnClickListener {
    public boolean isFavorite = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_details, container, false);
        v.findViewById(R.id.iv_back).setOnClickListener(this);
        v.findViewById(R.id.iv_favorite).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_back) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.showTabs();
            homeActivity.showHideSearchBar(true);
        } else if(viewId == R.id.iv_favorite) {
            isFavorite = !isFavorite;
            int favDrawable = isFavorite ? R.drawable.ic_like_big_active : R.drawable.ic_like_big_inactive;
            ((ImageView) getView().findViewById(R.id.iv_favorite)).setImageResource(favDrawable);
        }
    }
}