package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.model.DOD;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;

/**
 * Created by Babar on 12-Jan-16.
 */
public class DealOfDayAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    public List<DOD> dods;

    private LayoutInflater layoutInflater;

    private Resources resources;

    private Holder holder;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;

    public DealOfDayAdapter(Context context, int layoutResId, List<DOD> dods)
    {
        this.context  = context;

        this.layoutResId = layoutResId;

        this.dods = dods;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        resources = context.getResources();

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        reqWidth = displayMetrics.widthPixels / 2;
        reqHeight = reqWidth;
    }

    public void clear()
    {
        this.dods.clear();
    }

    public void setData(List<DOD> dods)
    {
        this.dods = dods;
    }

    @Override
    public int getCount() {
        return dods.size();
    }

    @Override
    public Object getItem(int position) {
        return dods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(grid == null)
        {
            grid = layoutInflater.inflate(layoutResId, parent, false);

            holder = new Holder();
            holder.tvCategory = (TextView) grid.findViewById(R.id.category);

            holder.gridLayout = (GridLayout) grid.findViewById(R.id.grid);
            holder.gridLayout.setColumnCount(2);

            grid.setTag(holder);
        }
        else
        {
            holder = (Holder ) grid.getTag();
        }

        DOD dod = (DOD) getItem(position);

        holder.tvCategory.setText(dod.category);

        for(GenericDeal genericDeal : dod.deals)
        {
            RelativeLayout rlCell = (RelativeLayout)
                    layoutInflater.inflate(R.layout.grid_deal_of_day, parent, false);

            holder.tvTitle = (TextView) rlCell.findViewById(R.id.title);
            holder.tvTitle.setText(genericDeal.title);

            holder.tvDescription = (TextView) rlCell.findViewById(R.id.description);
            holder.tvDescription.setText(genericDeal.description);

            Image image = genericDeal.image;

            if(image != null && !image.gridBannerUrl.isEmpty())
            {
                String imageUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imageUrl);

                if(bitmap != null)
                {
                    rlCell.setBackground(new BitmapDrawable(resources, bitmap));
                }
                else
                {
                    rlCell.setBackground(null);

                    fallBackToDiskCache(imageUrl);
                }
            }
            else
            {
                rlCell.setBackground(null);

            }

            holder.gridLayout.addView(rlCell);
        }

        return grid;
    }

    private void fallBackToDiskCache(final String imageUrl)
    {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = diskCache.getBitmapFromDiskCache(imageUrl);

                if(bitmap != null)
                {
                    memoryCache.addBitmapToCache(imageUrl, bitmap);

                    notifyDataSetChanged();
                }
                else
                {
                    BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                            new BaseAdapterBitmapDownloadTask(DealOfDayAdapter.this);
                    bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrl,
                            String.valueOf(reqWidth), String.valueOf(reqHeight));
                }
            }
        });
    }

    public static class Holder
    {
        TextView tvCategory, tvTitle, tvDescription;

        GridLayout gridLayout;
    }
}