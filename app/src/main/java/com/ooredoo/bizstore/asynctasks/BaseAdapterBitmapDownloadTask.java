package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

/**
 * Created by Babar on 04-Aug-15.
 */
public class BaseAdapterBitmapDownloadTask extends BitmapDownloadTask
{
    private BaseAdapter adapter;

    private BaseExpandableListAdapter baseExpandableListAdapter;


    public BaseAdapterBitmapDownloadTask(BaseAdapter adapter)
    {
        super(null, null);

        this.adapter = adapter;
    }

    public BaseAdapterBitmapDownloadTask(BaseExpandableListAdapter adapter)
    {
        super(null, null);

        this.baseExpandableListAdapter = adapter;
    }

    public BaseAdapterBitmapDownloadTask() {
        super(null, null);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
            if(adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
            else
            {
                if(baseExpandableListAdapter != null) {
                    baseExpandableListAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}