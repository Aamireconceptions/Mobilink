package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NotificationUtils;

/**
 * Created by Babar on 31-Jul-15.
 */
public class BitmapNotificationTask extends BitmapDownloadTask
{
    private Context context;

    private int id;

    private String title, desc;
    public BitmapNotificationTask(Context context, int id, String title, String desc)
    {
        super(null, null);

        this.context = context;

        this.id = id;

        this.title = title;

        this.desc = desc;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        return super.doInBackground(params);
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
