package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author  Babar
 * @since 18-Jun-15.
 */
public class BitmapForceDownloadTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    private ProgressBar progressBar;

    public String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor();

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private URL url;

    //public static List<String> downloadingPool = new ArrayList<>();

    public static ConcurrentHashMap<String, String> downloadingPool = new ConcurrentHashMap<>();

    public BitmapForceDownloadTask(ImageView imageView, ProgressBar progressBar)
    {
        this.imageView = imageView;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        showProgress(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        imgUrl = params[0];

        Logger.print("doInBg: ->imgUrl: " + imgUrl);

        return downloadBitmap(imgUrl, params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        showProgress(View.GONE);

        if(bitmap != null)
        {
            Logger.print("x3 Downloaded: " + imgUrl);

            memoryCache.addBitmapToCache(imgUrl, bitmap);

            if(imageView != null)
            {
                /*Palette palette = Palette.from(bitmap).generate();

                Palette.Swatch swatch = palette.getVibrantSwatch();
                if(swatch != null)
                {
                    imageView.setImageBitmap(null);
                    imageView.setBackgroundColor(swatch.getRgb());
                }*/

                imageView.setImageBitmap(bitmap);
                imageView.setTag("loaded");

                AnimatorUtils.expandAndFadeIn(imageView);
            }
        }
    }


    boolean writing = false;
    public Bitmap downloadBitmap(String imgUrl, String reqWidth, String reqHeight)
    {
        try
        {
            if(memoryCache.getBitmapFromCache(imgUrl) != null)
            {
                Logger.print("Already downloaded. Cancelling task");

                return memoryCache.getBitmapFromCache(imgUrl);
            }


            Logger.print("Force Bitmap Url: " + imgUrl);
            url = new URL(imgUrl);

            InputStream inputStream = openStream();

           /* int width = (int) Converter.convertDpToPixels(Integer.parseInt(reqWidth));
            int height = (int) Converter.convertDpToPixels(Integer.parseInt(reqHeight));*/

            int width = Integer.parseInt(reqWidth);

            int height = Integer.parseInt(reqHeight);

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

            if(bitmap != null)
            {
                diskCache.addBitmapToDiskCache(imgUrl, bitmap);
            }

            return bitmap;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream openStream() throws IOException
    {
        return url.openStream();
    }

    public void showProgress(int visible)
    {
        if(progressBar != null)
        {
            progressBar.setVisibility(visible);
        }
    }
}