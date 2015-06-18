package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.views.HeaderGridView;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment
{
    private Activity activity;

    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {
        activity = getActivity();

        HeaderGridView headerGridView = (HeaderGridView) v.findViewById(R.id.home_grid_view);

        LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_gridview_header, null);

        headerGridView.addHeaderView(header);
        headerGridView.setAdapter(null);
    }


}