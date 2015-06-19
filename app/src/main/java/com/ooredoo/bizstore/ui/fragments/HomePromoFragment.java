package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;

/**
 * Created by Babar on 19-Jun-15.
 */
public class HomePromoFragment extends Fragment
{
    public static HomePromoFragment newInstance()
    {
        HomePromoFragment fragment = new HomePromoFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home_promo, container, false);

        return v;
    }
}