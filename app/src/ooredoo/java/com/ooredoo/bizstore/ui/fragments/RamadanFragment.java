package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.RamadanTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.model.Category;
import com.ooredoo.bizstore.model.DOD;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Babar on 31-May-16.
 */
public class RamadanFragment extends Fragment implements OnDealsTaskFinishedListener, OnFilterChangeListener
{
    LayoutInflater inflater;

    DisplayMetrics displayMetrics;

    int reqWidth, reqHeight;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();

    ImageView ivBanner;

    TextView tvEmptyView;

    ProgressBar progressBar;

    LinearLayout llContainer;

    public static RamadanFragment newInstance()
    {
        RamadanFragment ramadanFragment = new RamadanFragment();

        return ramadanFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;
        View v = inflater.inflate(R.layout.fragment_ramadan, container, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            v.setNestedScrollingEnabled(true);
        }

        initAndLoad(v);
        return v;
    }

    private void initAndLoad(View v)
    {
        ivBanner = (ImageView) v.findViewById(R.id.banner);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        llContainer = (LinearLayout) v.findViewById(R.id.container);

        displayMetrics = getActivity().getResources().getDisplayMetrics();

        reqWidth = displayMetrics.widthPixels / 2;
        reqHeight = reqWidth;

        RamadanTask ramadanTask = new RamadanTask(getActivity(), this, progressBar);

        String cache = ramadanTask.getCache("dealofday");

        if(cache != null)
        {
            ramadanTask.setData(cache);
        }
        else {
            ramadanTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "dealofday");
        }
    }

    public void setData(List<DOD> dods)
    {
        int position = 0;

        for(DOD dod : dods)
        {
            position += 1;

            View grid = inflater.inflate(R.layout.layout_deal_of_day, null);

            TextView tvCategory = (TextView) grid.findViewById(R.id.category);
            tvCategory.setText(dod.category);

            TextView tvDiscount = (TextView) grid.findViewById(R.id.discount);

            GridLayout gridLayout = (GridLayout) grid.findViewById(R.id.grid);

            LinearLayout llCat = (LinearLayout) grid.findViewById(R.id.cat_layout);

            Category category  = Converter.convertCategoryText(getActivity(), dod.category);

            tvCategory.setText(category.name.toUpperCase());

            if(BizStore.getLanguage().equals("en")) {
                tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(category.drawableResId, 0,
                        0, 0);
            }
            else
            {
                tvDiscount.setCompoundDrawablesRelativeWithIntrinsicBounds(category.drawableResId, 0,
                        0, 0);
            }

            FontUtils.setFontWithStyle(getActivity(), tvCategory, Typeface.BOLD);

            for(int i = 0, r = 0, c = 0; i < dod.deals.size(); i++, c++)
            {
                GenericDeal genericDeal = dod.deals.get(i);

                CellClickListener clickListener = new CellClickListener(genericDeal);

                RelativeLayout rlCell = (RelativeLayout)
                        inflater.inflate(R.layout.grid_deal_of_day, (ViewGroup) grid, false);
                rlCell.setOnClickListener(clickListener);

                ImageView ivThumbnail = (ImageView) rlCell.findViewById(R.id.thumbnail);

                ProgressBar progressBar = (ProgressBar) rlCell.findViewById(R.id.progressBar);

                TextView tvTitle = (TextView) rlCell.findViewById(R.id.title);
                tvTitle.setText(genericDeal.businessName.toUpperCase());
                FontUtils.setFontWithStyle(getActivity(), tvTitle, Typeface.BOLD);

                TextView tvDescription = (TextView) rlCell.findViewById(R.id.description);
                tvDescription.setText(genericDeal.title.toUpperCase());

                Image image = genericDeal.image;

                if(image != null && image.gridBannerUrl != null && !image.gridBannerUrl.isEmpty())
                {
                    String imageUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                    Bitmap bitmap = memoryCache.getBitmapFromCache(imageUrl);

                    if(bitmap != null)
                    {
                        progressBar.setVisibility(View.GONE);

                        ivThumbnail.setImageBitmap(bitmap);
                        // rlCell.setBackground(new BitmapDrawable(resources, bitmap));
                    }
                    else
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        // rlCell.setBackground(null);

                        fallBackToDiskCache(imageUrl, ivThumbnail, progressBar);
                    }
                }
                else
                {
                    // rlCell.setBackground(null);

                    progressBar.setVisibility(View.GONE);
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
                    if(BizStore.getLanguage().equals("en"))
                    {
                        params.leftMargin = (int) Converter.convertDpToPixels(12);
                    }
                    else
                    {
                        params.rightMargin = (int) Converter.convertDpToPixels(12);
                    }

                    //  params.setMarginStart((int) Converter.convertDpToPixels(12));
                }
                // params.rowSpec = GridLayout.spec(r);
                //params.columnSpec = GridLayout.spec(c);

                params.setGravity(Gravity.CENTER_HORIZONTAL);
                Logger.print("row:" + r + ", column:" + c);
                gridLayout.addView(rlCell, params);

                Logger.print("Specs: "+params.width+", "+params.height);
            }

            if(position > 0)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                params.topMargin = (int) Converter.convertDpToPixels(12);

                llCat.setLayoutParams(params);
            }

            llContainer.addView(grid);
        }
    }

    @Override
    public void onFilterChange() {

    }

    @Override
    public void filterTagUpdate() {

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
            Intent intent = new Intent(getActivity(), DealDetailActivity.class);
            intent.putExtra("generic_deal", genericDeal);

            startActivity(intent);
        }
    }

    private void fallBackToDiskCache(final String imageUrl, final ImageView ivThumbnail,
                                     final ProgressBar progressBar)
    {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = diskCache.getBitmapFromDiskCache(imageUrl);

                if(bitmap != null)
                {
                    memoryCache.addBitmapToCache(imageUrl, bitmap);

                    ivThumbnail.setImageBitmap(bitmap);

                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                            new BaseAdapterBitmapDownloadTask();
                    bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrl,
                            String.valueOf(reqWidth), String.valueOf(reqHeight));
                }
            }
        });
    }
    @Override
    public void onRefreshCompleted() {

    }

    @Override
    public void onHaveDeals() {
        tvEmptyView.setVisibility(View.GONE);
        ivBanner.setImageResource(R.drawable.automotive_banner);
    }

    @Override
    public void onNoDeals(int stringResId) {

        tvEmptyView.setVisibility(View.VISIBLE);
        ivBanner.setImageBitmap(null);

    }
}