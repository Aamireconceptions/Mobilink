package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GalleryStatePagerAdapter;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.FragmentUtils;

import java.io.Serializable;

/**
 * Created by Babar on 04-Aug-16.
 */
public class ImageViewerFragment extends Fragment
{
    public static ImageViewerFragment newInstance(GalleryStatePagerAdapter adapter, int pos)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("adapter", (Serializable) adapter);
        bundle.putInt("pos", pos);

        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_gallery_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivClose = (ImageView) view.findViewById(R.id.close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.removeFragment(getActivity(), ImageViewerFragment.this);
            }
        });

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter((PagerAdapter) getArguments().getSerializable("adapter"));

        viewPager.setCurrentItem(getArguments().getInt("pos"));
    }
}
