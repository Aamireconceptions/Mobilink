package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.utils.NotificationUtils;

/**
 * Created by Babar on 31-Jul-15.
 */
public class BitmapNotificationTask extends BitmapDownloadTask
{
    private Context context;

    public BitmapNotificationTask(Context context)
    {
        super(null, null);

        this.context = context;
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
            NotificationUtils.showNotification(context, "Title", "Text", bitmap);
        }
    }
}
