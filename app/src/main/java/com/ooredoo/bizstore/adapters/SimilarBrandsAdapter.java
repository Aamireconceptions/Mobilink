package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;
import java.util.Random;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 18-Jun-15
 */
public class SimilarBrandsAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    public List<Brand> brands;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;

    public SimilarBrandsAdapter(Context context, int layoutResId, List<Brand> brands)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.brands = brands;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();

        reqWidth = Resources.getSystem().getDisplayMetrics().widthPixels / 3;

        reqHeight = reqWidth;

        Logger.print("GridView thumbnail reqWidth:"+reqWidth+", reqHeight: "+reqHeight);
    }

    public void setData(List<GenericDeal> deals)
    {
        this.brands = brands;
    }

    public void clearData()
    {
        brands.clear();
    }

    @Override
    public int getCount()
    {
        return brands.size();
    }

    @Override
    public Brand getItem(int position)
    {
        return brands.get(position);
    }

    @Override
    public long getItemId(int position) {
        return brands == null || brands.size() == 0 ? 0 : brands.get(position).id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(grid == null)
        {
            grid = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.ivThumbnail = (ImageView) grid.findViewById(R.id.image_view);
            holder.tvTitle = (TextView) grid.findViewById(R.id.text_view);
            holder.tvBrandText = (TextView) grid.findViewById(R.id.brand_txt);
            holder.progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);

            grid.setTag(holder);
        }
        else
        {
            holder = (Holder) grid.getTag();
        }

        final Brand brand = getItem(position);

        if(brand.color == 0)
        {
            brand.color = Color.parseColor(getColorCode());
        }

        String brandLogo = brand.businessLogo;
        if(brandLogo != null && !brandLogo.isEmpty())
        {
            brandLogo = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

            Bitmap bitmap = memoryCache.getBitmapFromCache(brandLogo);

            if(bitmap != null)
            {
                holder.ivThumbnail.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                holder.ivThumbnail.setImageBitmap(null);
                holder.progressBar.setVisibility(View.VISIBLE);
                fallBackToDiskCache(brandLogo);
            }
        }
        else
        {
            holder.ivThumbnail.setImageBitmap(null);
            if(brand.title != null && !brand.title.isEmpty())
            {
                holder.tvBrandText.setBackgroundColor(brand.color);
                holder.tvBrandText.setText(String.valueOf(brand.title.charAt(0)));
            }

            holder.progressBar.setVisibility(View.GONE);
        }

       /* holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Business business = new Business(brand);

                Intent intent = new Intent();
                intent.setClass(context, BusinessDetailActivity.class);
                intent.putExtra("business", business);
                intent.putExtra(CATEGORY, "N/A");
                context.startActivity(intent);
            }
        });*/

        holder.tvTitle.setText(brand.title);

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

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(SimilarBrandsAdapter.this);

                            bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                    String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private static class Holder
    {
        ImageView ivThumbnail;

        TextView tvTitle, tvBrandText;

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