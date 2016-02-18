package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.model.Category;
import com.ooredoo.bizstore.model.DOD;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
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

    private DisplayMetrics displayMetrics;

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

        displayMetrics = resources.getDisplayMetrics();

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

            holder.llCat = (LinearLayout) grid.findViewById(R.id.cat_layout);

            grid.setTag(holder);
        }
        else
        {
            holder = (Holder ) grid.getTag();
        }

        DOD dod = (DOD) getItem(position);

        holder.gridLayout.removeAllViews();

        Category category  = Converter.convertCategoryText(context, dod.category);

        holder.tvCategory.setText(category.name.toUpperCase());
        holder.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(category.drawableResId, 0,
                0, 0);

        FontUtils.setFontWithStyle(context, BizStore.DEFAULT_FONT, holder.tvCategory, Typeface.BOLD);

        for(int i = 0, r = 0, c = 0; i < dod.deals.size(); i++, c++)
        {
            GenericDeal genericDeal = dod.deals.get(i);

            CellClickListener clickListener = new CellClickListener(genericDeal);

            RelativeLayout rlCell = (RelativeLayout)
                    layoutInflater.inflate(R.layout.grid_deal_of_day, parent, false);
            rlCell.setOnClickListener(clickListener);

            holder.progressBar = (ProgressBar) rlCell.findViewById(R.id.progressBar);

            holder.tvTitle = (TextView) rlCell.findViewById(R.id.title);
            holder.tvTitle.setText(genericDeal.title.toUpperCase());
            FontUtils.setFontWithStyle(context, BizStore.DEFAULT_FONT, holder.tvTitle, Typeface.BOLD);

            holder.tvDescription = (TextView) rlCell.findViewById(R.id.description);
            holder.tvDescription.setText(genericDeal.description.toUpperCase());

            Image image = genericDeal.image;

            if(image != null && image.gridBannerUrl != null && !image.gridBannerUrl.isEmpty())
            {
                String imageUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imageUrl);

                if(bitmap != null)
                {
                    holder.progressBar.setVisibility(View.GONE);

                    rlCell.setBackground(new BitmapDrawable(resources, bitmap));
                }
                else
                {
                    holder.progressBar.setVisibility(View.VISIBLE);

                    rlCell.setBackground(null);

                    fallBackToDiskCache(imageUrl);
                }
            }
            else
            {
                rlCell.setBackground(null);

                holder.progressBar.setVisibility(View.GONE);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (displayMetrics.widthPixels - (int) Converter.convertDpToPixels(36)) / 2;
            params.height = (int) ((displayMetrics.widthPixels - (int) Converter.convertDpToPixels(36)) / 2.2);

            if(c == 2)
            {
                //params.topMargin = (int) Converter.convertDpToPixels(8);
                c = 0;

                r++;
            }

            if(r > 0)
            {
                params.topMargin = (int) Converter.convertDpToPixels(12);
            }

            if(c == 1)
            {
                params.leftMargin = (int) Converter.convertDpToPixels(12);
            }
           // params.rowSpec = GridLayout.spec(r);
            //params.columnSpec = GridLayout.spec(c);

            params.setGravity(Gravity.CENTER_HORIZONTAL);
            Logger.print("row:"+r+", column:"+c);
            holder.gridLayout.addView(rlCell, params);

            Logger.print("Specs: "+params.width+", "+params.height);

            /*if(first)
            {
                first = false;

                holder.gridLayout.addView(rlCell, new GridLayout.LayoutParams(
                        GridLayout.spec(0, GridLayout.CENTER),
                        GridLayout.spec(0, GridLayout.CENTER)));
            }
            else
            {
                holder.gridLayout.addView(rlCell, new GridLayout.LayoutParams(
                        GridLayout.spec(0, GridLayout.CENTER),
                        GridLayout.spec(1, GridLayout.CENTER)));
            }*/
        }

        if(position > 0)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.topMargin = (int) Converter.convertDpToPixels(12);

            holder.llCat.setLayoutParams(params);
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

    class CellClickListener implements View.OnClickListener
    {
        GenericDeal genericDeal;

        CellClickListener(GenericDeal genericDeal)
        {
            this.genericDeal = genericDeal;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DealDetailActivity.class);
            intent.putExtra("generic_deal", genericDeal);

            context.startActivity(intent);
        }
    }

    public static class Holder
    {
        TextView tvCategory, tvTitle, tvDescription;

        ProgressBar progressBar;

        LinearLayout llCat;

        GridLayout gridLayout;
    }
}