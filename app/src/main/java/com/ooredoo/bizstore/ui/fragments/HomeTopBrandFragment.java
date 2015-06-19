package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class HomeTopBrandFragment extends Fragment
{
    public static HomeTopBrandFragment newInstance()
    {
        HomeTopBrandFragment fragment = new HomeTopBrandFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home_top_brand, container, false);

        return v;
    }

}
