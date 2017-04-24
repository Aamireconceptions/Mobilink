package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

/**
 * Created by Babar on 10-Feb-17.
 */
@EBean
public class CommonHelper
{
    public static void fallBackToDiskCache(final String url, final DiskCache diskCache,
                                           final MemoryCache memoryCache, final BaseAdapter adapter,
                                           final int reqWidth, final int reqHeight)
    {
        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap bitmap = diskCache.getBitmapFromDiskCache(url);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);

                    adapter.notifyDataSetChanged();

                }
                else
                {
                    if(BitmapDownloadTask.downloadingPool.get(url) == null)
                    {
                        Logger.print("Adapter executing:"+url);

                        BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                new BaseAdapterBitmapDownloadTask(adapter);
                        bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                String.valueOf(reqWidth), String.valueOf(reqHeight));
                    }
                    else
                    {
                        Logger.print("Adapter returning");
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }

    public static void fallBackToDiskCache(final String url, final DiskCache diskCache,
                                           final MemoryCache memoryCache, final BaseExpandableListAdapter adapter,
                                           final int reqWidth, final int reqHeight)
    {
        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap bitmap = diskCache.getBitmapFromDiskCache(url);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);

                    adapter.notifyDataSetChanged();

                }
                else
                {
                    if(BitmapDownloadTask.downloadingPool.get(url) == null)
                    {
                        Logger.print("Adapter executing:"+url);

                        BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                new BaseAdapterBitmapDownloadTask(adapter);
                        bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                String.valueOf(reqWidth), String.valueOf(reqHeight));
                    }
                    else
                    {
                        Logger.print("Adapter returning");
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    Bitmap bitmap;
    @Background
    public void fallBackToDiskCache(final Activity activity, final String url, final DiskCache diskCache,
                                     final MemoryCache memoryCache, final ImageView imageView,
                                     final ProgressBar progressBar, final int reqWidth, final int reqHeight)
    {
        bitmap = diskCache.getBitmapFromDiskCache(url);

        Logger.print("dCache getting bitmap from cache");

        if(bitmap != null)
        {
            Logger.print("dCache found!");

            memoryCache.addBitmapToCache(url, bitmap);
            setBitmap(bitmap, imageView);
        }
        else
        {
            downloadBitmap(imageView, progressBar, url, reqWidth, reqHeight);
        }
    }

    @UiThread
    void downloadBitmap(ImageView imageView, ProgressBar progressBar, String url, int reqWidth, int reqHeight)
    {
        BitmapForceDownloadTask bitmapDownloadTask = new BitmapForceDownloadTask
                (imageView, progressBar);

        bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                url, String.valueOf(reqWidth),
                String.valueOf(reqHeight));
    }

    @UiThread
    void setBitmap(Bitmap bitmap, ImageView imageView)
    {
        imageView.setImageBitmap(bitmap);
        imageView.setTag("loaded");
    }


    public static void startDirections(Context context, GenericDeal genericDeal)
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        if(mLat != 0 && mLong != 0)
        {
            src = "saddr=" + mLat + "," + mLong + "&";
        }

        if(genericDeal.latitude != 0 && genericDeal.longitude != 0)
        {
            dest = "daddr="+genericDeal.latitude + "," + genericDeal.longitude;
        }

        String uri = "http://maps.google.com/maps?";

        if(src != null)
        {
            uri += src;
        }

        if(dest != null)
        {
            uri += dest;
        }

        System.out.println("Directions URI:"+uri);

        if(src == null)
        {
            Toast.makeText(context, "Location not available. Please enable location services!", Toast.LENGTH_SHORT).show();

            return;
        }

        if(dest == null)
        {
            Toast.makeText(context, "Deal location is not available!", Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
