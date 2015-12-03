package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 18-Jun-15
 */
public class GridViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    public List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;
    public String listingType;

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

        //brands.clear();
    }

    @Override
    public int getCount()
    {
        if(listingType.equals("deals"))
        {
            return deals.size();
        }
        else
        {
            return brands.size();
        }
    }

    @Override
    public Object getItem(int position)
    {
        if(listingType.equals("deals"))
        {
            return deals.get(position);
        }
        else
        {
            return brands.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return deals == null || deals.size() == 0 ? 0 : deals.get(position).id;
    }

    public void setListingType(String type)
    {
        this.listingType = type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(listingType.equals("deals")) {
            if (grid == null) {
                grid = inflater.inflate(layoutResId, parent, false);

                holder = new Holder();

                holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
                holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
                holder.tvDesc = (TextView) grid.findViewById(R.id.desc);
                holder.tvTitle = (TextView) grid.findViewById(R.id.title);
                holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);
                holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);
                holder.ivDiscountTag = (ImageView) grid.findViewById(R.id.discount_tag);

                grid.setTag(holder);
            } else {
                holder = (Holder) grid.getTag();
            }

            final GenericDeal deal = (GenericDeal) getItem(position);

            if(deal.color == 0)
            {
                deal.color = Color.parseColor(getColorCode());
            }

            if(deal.discount == 0) {
                holder.tvDiscount.setVisibility(View.GONE);
                holder.ivDiscountTag.setVisibility(View.GONE);
            }
            else
            {
                holder.tvDiscount.setVisibility(View.VISIBLE);
                holder.ivDiscountTag.setVisibility(View.VISIBLE);
            }

            holder.tvDiscount.setText(valueOf(deal.discount) + "%\n"+context.getString(R.string.off));

            if(BizStore.getLanguage().equals("en"))
            {
                holder.tvDiscount.setRotation(-40);
            }
            else
            {
                holder.tvDiscount.setRotation(40);
            }

            deal.isFav = Favorite.isFavorite(deal.id);

            holder.ivFav.setSelected(deal.isFav);

            holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

            Image image = deal.image;
            if (image != null && image.gridBannerUrl != null) {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                Logger.print("Pos: " + position + "imgUrl: " + imgUrl + ", bitmap: " + bitmap);
                if (bitmap != null) {
                    holder.ivThumbnail.setImageBitmap(bitmap);
                    holder.progressBar.setVisibility(View.GONE);
                } else {
                /*holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                holder.progressBar.setVisibility(View.VISIBLE);

                Logger.print("Download started for position: " + position);

                BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                        new BaseAdapterBitmapDownloadTask(this);

                *//*bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl,
                                         String.valueOf(reqWidth), String.valueOf(reqHeight));*//*
                bitmapDownloadTask.execute(imgUrl, String.valueOf(reqWidth), String.valueOf(reqHeight));*/

                    holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    fallBackToDiskCache(imgUrl);
                }
            } else {
                holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                holder.progressBar.setVisibility(View.GONE);
            }

           /* if (deal.discount == 0) {
                holder.tvDiscount.setVisibility(View.GONE);
            } else {
                holder.tvDiscount.setVisibility(View.VISIBLE);
            }*/

            holder.tvTitle.setText(deal.businessName);
            holder.tvDesc.setText(deal.title);
           // holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

            return grid;
        }
        else
            if(listingType.equals("brands"))
            {
                if (grid == null) {
                    grid = inflater.inflate(layoutResId, parent, false);

                    holder = new Holder();

                    holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
                    holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
                    holder.tvDesc = (TextView) grid.findViewById(R.id.desc);
                    holder.tvTitle = (TextView) grid.findViewById(R.id.title);
                    holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);
                    holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);

                    grid.setTag(holder);
                } else {
                    holder = (Holder) grid.getTag();
                }

                final Brand brand = (Brand) getItem(position);

                if(brand.color == 0)
                {
                    brand.color = Color.parseColor(getColorCode());
                }

                brand.isFavorite = Favorite.isFavorite(brand.id);

                holder.ivFav.setSelected(brand.isFavorite);

                holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

                Image image = brand.image;
                if (image != null && image.gridBannerUrl != null) {
                    String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                    Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                    Logger.print("Pos: " + position + "imgUrl: " + imgUrl + ", bitmap: " + bitmap);
                    if (bitmap != null) {
                        holder.ivThumbnail.setImageBitmap(bitmap);
                        holder.progressBar.setVisibility(View.GONE);
                    } else {
                /*holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                holder.progressBar.setVisibility(View.VISIBLE);

                Logger.print("Download started for position: " + position);

                BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                        new BaseAdapterBitmapDownloadTask(this);

                *//*bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl,
                                         String.valueOf(reqWidth), String.valueOf(reqHeight));*//*
                bitmapDownloadTask.execute(imgUrl, String.valueOf(reqWidth), String.valueOf(reqHeight));*/

                        holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        fallBackToDiskCache(imgUrl);
                    }
                } else {
                    holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                    holder.progressBar.setVisibility(View.GONE);
                }

                /*if (brand.discount == 0) {
                    holder.tvDiscount.setVisibility(View.GONE);
                } else {
                    holder.tvDiscount.setVisibility(View.VISIBLE);
                }*/

               // holder.tvDiscount.setVisibility(View.GONE);

                holder.tvTitle.setText(brand.title);
                holder.tvDesc.setText(brand.description);
                //holder.tvDiscount.setText(valueOf(brand.discount) + context.getString(R.string.percentage_off));

                return grid;
            }

        return null;
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
                           // holder.ivThumbnail.setImageResource(R.drawable.deal_bg);
                            //holder.progressBar.setVisibility(View.VISIBLE);

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

    List<Brand> brands = new ArrayList<>();

    public void setBrandsList(List<Brand> brands) {
        this.brands = brands;
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            GenericDeal genericDeal = (GenericDeal) getItem(position);

            Logger.logI("FAV_DEAL: " + genericDeal.id, String.valueOf(genericDeal.isFav));

            genericDeal.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favorite = new Favorite(genericDeal);
            Favorite.updateFavorite(favorite);
        }
    }

    private static class Holder
    {
        ImageView ivThumbnail, ivFav, ivDiscountTag;

        TextView tvTitle, tvDiscount, tvDesc;

        ProgressBar progressBar;

    }

    public String getColorCode()
    {
        int min = 1;
        int max = 8;

        Random random = new Random();

        int i = random.nextInt(max - min) + min;

        Logger.print("random: "+i);

        String color = null;
        switch (i)
        {
            case 1:
                color = "#90a4ae";
                break;
            case 2:
                color = "#ff8a65";
                break;
            case 3:
                color = "#ba68c8";
                break;
            case 4:
                color = "#da4336";
                break;
            case 5:
                color = "#4fc3f7";
                break;
            case 6:
                color = "#ffa726";
                break;
            case 7:
                color = "#aed581";
                break;
            case 8:
                color = "#b39ddb";
                break;
        }

        return color;
    }
}