package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.widget.BaseAdapter;

import com.ooredoo.bizstore.utils.MemoryCache;

/**
 * Created by Babar on 04-Aug-15.
 */
public class BaseAdapterBitmapDownloadTask extends BitmapDownloadTask
{
    private BaseAdapter adapter;

    public BaseAdapterBitmapDownloadTask(BaseAdapter adapter)
    {
        super(null, null);

        this.adapter = adapter;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
            adapter.notifyDataSetChanged();
        }
    }
}