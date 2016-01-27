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

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        String brandLogo = brand.businessLogo;

        if(brandLogo != null && !brandLogo.isEmpty())
        {
            brandLogo = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

            Bitmap bitmap = memoryCache.getBitmapFromCache(brandLogo);

            if(bitmap != null)
            {
                holder.tvBrandText.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);

                holder.ivThumbnail.setImageBitmap(bitmap);

                if(!brand.isLogoDisplayed)
                {
                    brand.isLogoDisplayed = true;
                    AnimatorUtils.fadeIn(holder.ivThumbnail);
                }
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
                if(brand.color == 0)
                {
                    brand.color = Color.parseColor(ColorUtils.getColorCode());
                }
                holder.tvBrandText.setVisibility(View.VISIBLE);
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

                            Iterator it = BitmapDownloadTask.downloadingPool.entrySet().iterator();

                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                System.out.println(pair.getKey() + " = " + pair.getValue());

                                if(pair.getKey().equals(url))
                                {
                                    Logger.print("Adapter not downloading the bitmap");

                                    return;
                                }
                            }

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(SimilarBrandsAdapter.this);

                            bitmapDownloadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url,
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
}