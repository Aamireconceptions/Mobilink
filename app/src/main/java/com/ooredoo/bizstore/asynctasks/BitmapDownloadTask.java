package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  Babar
 * @since 18-Jun-15.
 */
public class BitmapDownloadTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    private ProgressBar progressBar;

    public String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor();

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private URL url;

    public static List<String> downloadingPool = new ArrayList<>();

    public BitmapDownloadTask(ImageView imageView, ProgressBar progressBar)
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

        Iterator<String> iterator = downloadingPool.iterator();

        while (iterator.hasNext())
        {
            String str = iterator.next();

            if(str.equals(imgUrl));
            {
               // downloadingPool.remove(imgUrl)
                iterator.remove();

                Logger.print("x3 " + imgUrl + " removed from pool");
            }
        }


        if(bitmap != null)
        {
            Logger.print("x3 Downloaded: " + imgUrl);

            memoryCache.addBitmapToCache(imgUrl, bitmap);

            if(imageView != null)
            {
                imageView.setImageBitmap(bitmap);
                imageView.setTag("loaded");
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        Logger.print("onCacelled");
    }

    public Bitmap downloadBitmap(String imgUrl, String reqWidth, String reqHeight)
    {
        try
        {
            if(memoryCache.getBitmapFromCache(imgUrl) != null)
            {
                Logger.print("Already downloaded. Cancelling task");

                cancel(true);
            }

            if(BizStore.forceStopTasks)
            {
                Logger.print("Force stopped bitmap download task");

                return null;
            }

            synchronized(this)
            {
                String pool[] = new String[downloadingPool.size()];

                downloadingPool.toArray(pool);

                for(String url : pool)
                {
                    if(url.equals(imgUrl))
                    {
                        Logger.print("x3 Image Download alreay in progress");
                        cancel(true);
                    }
                }
            }

            if(isCancelled())
            {
                Logger.print("isCancelled: true");

                return null;
            }

            Logger.print("x3 " + imgUrl + " added to pool");

            downloadingPool.add(imgUrl);

            Logger.print("Bitmap Url: " + imgUrl);
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