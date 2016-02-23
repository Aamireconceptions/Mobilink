package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

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

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
//            if(adapter instanceof ListViewBaseAdapter)
//            {
//                ((ListViewBaseAdapter) adapter).doAnimate = false;
//            }

            if(adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
            else
            {
                baseExpandableListAdapter.notifyDataSetChanged();
            }
        }
    }
}