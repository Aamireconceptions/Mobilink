package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author  Babar
 * @since 18-Jun-15.
 */
public class BitmapDownloadTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    private ProgressBar progressBar;

    public String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor(this);

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private URL url;

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

        if(bitmap != null)
        {
            Logger.print("Downloaded: "+imgUrl);
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

            if(isCancelled())
            {
                Logger.print("isCancelled: true");

                return null;
            }

            Logger.print("Bitmap Url: " + imgUrl);
            url = new URL(imgUrl);

            InputStream inputStream = openStream();

            int width = (int) Converter.convertDpToPixels(Integer.parseInt(reqWidth));
            int height = (int) Converter.convertDpToPixels(Integer.parseInt(reqHeight));

            return bitmapProcessor.decodeSampledBitmapFromStream(inputStream, width, height);
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

    private void showProgress(int visible)
    {
        if(progressBar != null)
        {
            progressBar.setVisibility(visible);
        }
    }
}