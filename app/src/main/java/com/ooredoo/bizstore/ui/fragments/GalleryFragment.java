package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.Gallery;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FragmentUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class GalleryFragment extends Fragment implements View.OnClickListener {

    private DealDetailActivity activity;

    private int id;

    private Bitmap bitmap;

    private Gallery gallery;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    public static GalleryFragment newInstance(Gallery gallery, int position, boolean clickable)
    {
        Bundle bundle = new Bundle();
        /*bundle.putInt("id", id);
        bundle.putString("image_url", imgUrl);*/
        bundle.putSerializable("gallery", gallery);
        bundle.putInt("pos", position);
        bundle.putBoolean("clickable", clickable);

        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        v.setOnClickListener(this);

        initAndLoadTopBrand(v);

        return v;
    }

    ImageView imageView;
    ProgressBar progressBar;
    int pos;
    private void initAndLoadTopBrand(View v)
    {
        pos = getArguments().getInt("pos");

        activity = (DealDetailActivity) getActivity();

        Bundle bundle = getArguments();

        gallery = (Gallery) bundle.getSerializable("gallery");

        String imgUrl = gallery.image;

       // id = brand.id;

        imageView = (ImageView) v.findViewById(R.id.image_view);

        if(!getArguments().getBoolean("clickable"))
        {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rlImage = (RelativeLayout) v.findViewById(R.id.image_layout);
            rlImage.setLayoutParams(params);

            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setBackground(null);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        if(imgUrl != null)
        {
            Logger.print("Top Brand imgUrl was NOT null");

            String url = BaseAsyncTask.IMAGE_BASE_URL + imgUrl;

            Logger.logE("FRAGMENT URL:", url);

            bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap == null)
            {
                fallBackToDiskCache(url);
            }
            else
            {
                imageView.setImageBitmap(bitmap);
            }
        }
        else
        {
            Logger.print("Top Brand imgUrl was null");
        }
    }

    private void fallBackToDiskCache(final String url)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                bitmap = diskCache.getBitmapFromDiskCache(url);

                Logger.print("dCache getting bitmap from cache");

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.print("Setting bitmap: "+bitmap);
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag("loaded");
                        }
                    });
                }
                else
                {
                    //Logger.print("Root Width:" + v.getWidth());

                    Resources resources = activity.getResources();

                    final int reqWidth = resources.getDisplayMetrics().widthPixels ;

                    final int reqHeight = resources.getDisplayMetrics().heightPixels;

                    Logger.print("req Width Pixels:" + reqWidth);
                    Logger.print("req Height Pixels:" + reqHeight);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapForceDownloadTask bitmapDownloadTask =
                                    new BitmapForceDownloadTask(imageView, progressBar, null);
                            bitmapDownloadTask.executeOnExecutor
                                    (AsyncTask.THREAD_POOL_EXECUTOR, url,
                                            String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });

                }
            }
        });

        thread.start();

      /*  try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onClick(View v)
    {
        if(getArguments().getBoolean("clickable") && ((imageView.getTag() != null
                && imageView.getTag().equals("loaded")) || bitmap != null)) {

            Logger.print("CLICKED");
            FragmentUtils.addFragmentWithBackStack(activity, android.R.id.content,
                    ImageViewerFragment.newInstance(activity.galleryList, pos), "gallery");

        }
    }

}
