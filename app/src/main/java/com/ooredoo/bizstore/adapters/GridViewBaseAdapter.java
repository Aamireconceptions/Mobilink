package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;

import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 18-Jun-15
 */
public class GridViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;

    public GridViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();

        reqWidth = Resources.getSystem().getDisplayMetrics().widthPixels / 2;

        reqHeight = reqWidth;

        Logger.print("GridView thumbnail reqWidth:"+reqWidth+", reqHeight: "+reqHeight);
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
    }

    public void clearData()
    {
        deals.clear();
    }

    @Override
    public int getCount()
    {
        return deals.size();
    }

    @Override
    public GenericDeal getItem(int position)
    {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deals == null || deals.size() == 0 ? 0 : deals.get(position).id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(grid == null)
        {
            grid = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
            holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
            holder.tvDesc = (TextView) grid.findViewById(R.id.desc);
            holder.tvTitle = (TextView) grid.findViewById(R.id.title);
            holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);
            holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);

            grid.setTag(holder);
        }
        else
        {
            holder = (Holder) grid.getTag();
        }

        final GenericDeal deal = getItem(position);

        deal.isFav = Favorite.isFavorite(deal.id);

        holder.ivFav.setSelected(deal.isFav);

        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        Image image = deal.image;
        if(image != null && image.gridBannerUrl != null)
        {
            String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

            Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

            Logger.print("Pos: " + position + "imgUrl: "+imgUrl+ ", bitmap: "+bitmap);
            if(bitmap != null)
            {
                holder.ivThumbnail.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                /*holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                holder.progressBar.setVisibility(View.VISIBLE);

                Logger.print("Download started for position: " + position);

                BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                        new BaseAdapterBitmapDownloadTask(this);

                *//*bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl,
                                         String.valueOf(reqWidth), String.valueOf(reqHeight));*//*
                bitmapDownloadTask.execute(imgUrl, String.valueOf(reqWidth), String.valueOf(reqHeight));*/

                fallBackToDiskCache(imgUrl);
            }
        }
        else
        {
            holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
            holder.progressBar.setVisibility(View.GONE);
        }

        if(deal.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvTitle.setText(deal.title);
        holder.tvDesc.setText(deal.description);
        holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

        return grid;
    }

    private void fallBackToDiskCache(final String url)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Bitmap bitmap = diskCache.getBitmapFromDiskCache(url);

                Logger.print("dCache getting bitmap from cache");

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);
                    ((Activity) context).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            Logger.print(" dCache fallback notifyDataSetChanged");
                            notifyDataSetChanged();
                        }
                    });
                }
                else
                {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                            holder.progressBar.setVisibility(View.VISIBLE);

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(GridViewBaseAdapter.this);

                            bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                    String.valueOf(reqWidth), String.valueOf(reqHeight));

                            // bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            GenericDeal genericDeal = getItem(position);

            Logger.logI("FAV_DEAL: " + genericDeal.id, String.valueOf(genericDeal.isFav));

            genericDeal.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favorite = new Favorite(genericDeal);
            Favorite.updateFavorite(favorite);
        }
    }

    private static class Holder
    {
        ImageView ivThumbnail, ivFav;

        TextView tvTitle, tvDiscount, tvDesc;

        ProgressBar progressBar;
    }
}