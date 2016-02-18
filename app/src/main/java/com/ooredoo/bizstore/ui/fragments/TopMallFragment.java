package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MallDetailActivity;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.Random;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class TopMallFragment extends Fragment implements View.OnClickListener {

    private HomeActivity activity;

    private int id;

    private Bitmap bitmap;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private Mall mall;

    public static TopMallFragment newInstance(Mall mall)
    {
        Bundle bundle = new Bundle();
        /*bundle.putInt("id", id);
        bundle.putString("name", name);
        bundle.putString("image_url", imgUrl);*/
        bundle.putSerializable("mall", mall);

        TopMallFragment fragment = new TopMallFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_top_mall, container, false);
        v.setOnClickListener(this);

        initAndLoadTopMalls(v);

        return v;
    }

    ImageView imageView;
    ProgressBar progressBar;
    private void initAndLoadTopMalls(View v)
    {
        activity = (HomeActivity) getActivity();

        Bundle bundle = getArguments();

        mall = (Mall) bundle.getSerializable("mall");

        if(mall.color == 0)
        {

            mall.color = Color.parseColor(ColorUtils.getColorCode());
        }

        /*id = bundle.getInt("id");

        String name = bundle.getString("name");

        String imgUrl = bundle.getString("image_url");*/

        id = mall.id;

        String name = mall.title;

        String imgUrl = mall.image.logoUrl;

       imageView = (ImageView) v.findViewById(R.id.image_view);

        TextView textView = (TextView) v.findViewById(R.id.text_view);
        textView.setText(name);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        if(imgUrl != null)
        {
            Logger.print("imgUrl was NOT null");

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
            Logger.print("imgUrl was null");
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
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag("loaded");
                        }
                    });
                }
                else
                {
                   // Logger.print("Root Width:" + v.getWidth());

                    Resources resources = activity.getResources();

                    final int reqWidth = resources.getDisplayMetrics().widthPixels / 3;

                    final int reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._90sdp)
                            /
                            resources.getDisplayMetrics().density);

                    Logger.print("req Width Pixels:" + reqWidth);
                    Logger.print("req Height Pixels:" + reqHeight);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(imageView, progressBar);
                            bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });
                }
            }
        });

        thread.start();

  /*      try
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
        //activity.showDetailActivity(AppConstant.BUSINESS, AppConstant.DEAL_CATEGORIES[6], id);

        mall.views += 1;

        Intent intent = new Intent(activity, MallDetailActivity.class);
        intent.putExtra("business", new Business(mall));
        intent.putExtra(AppConstant.CATEGORY, AppConstant.DEAL_CATEGORIES[6]);
        startActivity(intent);

       // activity.showBusinessDetailActivity(AppConstant.DEAL_CATEGORIES[6], new Business(mall));
    }
}
