package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NotificationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Babar on 31-Jul-15.
 */
public class BitmapNotificationTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private Context context;

    private int id;

    private String title, desc;

    private URL url;

    private BitmapProcessor bitmapProcessor;

    public BitmapNotificationTask(Context context, int id, String title, String desc)
    {
        this.context = context;

        this.id = id;

        this.title = title;

        this.desc = desc;

        bitmapProcessor = new BitmapProcessor();
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        return downloadBitmap(params[0], params[1], params[2]);
    }

    public Bitmap downloadBitmap(String imgUrl, String reqWidth, String reqHeight)
    {
        try
        {
            Logger.print("notification Bitmap Url: " + imgUrl);

            url = new URL(imgUrl);

            InputStream inputStream = openStream();

            int width = (int) Converter.convertDpToPixels(Integer.parseInt(reqWidth));
            int height = (int) Converter.convertDpToPixels(Integer.parseInt(reqHeight));

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

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

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
            NotificationUtils.showNotification(context, title, desc, id, bitmap);
        }
        else
        {
            NotificationUtils.showNotification(context, title, desc, id, null);

            Logger.print("Failed to download notification img");
        }
    }
}
