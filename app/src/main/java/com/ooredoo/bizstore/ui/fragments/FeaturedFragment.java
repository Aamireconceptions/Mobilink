package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class FeaturedFragment extends Fragment implements View.OnClickListener
{
    public static FeaturedFragment newInstance(int id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        FeaturedFragment fragment = new FeaturedFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_featured, container, false);

        initAndLoadBanner(v);

        return v;
    }

    private void initAndLoadBanner(View v)
    {
        v.setOnClickListener(this);

        ImageView imageView = (ImageView) v.findViewById(R.id.iv_featured_banner);

        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(imageView, progressBar);
    }

    @Override
    public void onClick(View v)
    {

    }

}