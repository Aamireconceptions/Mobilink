package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author  Babar
 * @since 18-Jun-15.
 */
public class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    private ProgressBar progressBar;

    private String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor();

    private MemoryCache memoryCache = MemoryCache.getInstance();

    public BitmapDownloadTask(ImageView imageView, ProgressBar progressBar)
    {
        this.imageView = imageView;

        this.progressBar = progressBar;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        imgUrl = params[0];

        return downloadBitmap(imgUrl, params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap downloadBitmap(String imgUrl, String reqWidth, String reqHeight)
    {
        try
        {
            URL url = new URL(imgUrl);

            InputStream inputStream = url.openStream();

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
}