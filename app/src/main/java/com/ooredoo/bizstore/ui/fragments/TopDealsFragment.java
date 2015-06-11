package com.ooredoo.bizstore.ui.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;

public class TopDealsFragment extends Fragment
{
    public static TopDealsFragment newInstance()
    {
        TopDealsFragment fragment = new TopDealsFragment();

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
        View v = inflater.inflate(R.layout.fragment_top_deals, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {

    }


}