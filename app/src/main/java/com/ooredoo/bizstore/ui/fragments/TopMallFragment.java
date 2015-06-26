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
public class TopMallFragment extends Fragment
{
    public static TopMallFragment newInstance(int id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        TopMallFragment fragment = new TopMallFragment();
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
