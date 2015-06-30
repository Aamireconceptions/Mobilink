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
public class TopBrandFragment extends Fragment
{
    public static TopBrandFragment newInstance(int id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        TopBrandFragment fragment = new TopBrandFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_top_brand, container, false);

        return v;
    }

}