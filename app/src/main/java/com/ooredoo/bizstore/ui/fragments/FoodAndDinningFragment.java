package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;

public class FoodAndDinningFragment extends Fragment
{
    public static FoodAndDinningFragment newInstance()
    {
        FoodAndDinningFragment fragment = new FoodAndDinningFragment();

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
        View v = inflater.inflate(R.layout.fragment_food_dinning, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {

    }


}