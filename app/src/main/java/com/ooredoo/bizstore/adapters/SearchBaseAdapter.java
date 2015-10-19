package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class SearchBaseAdapter extends BaseAdapter {

    private Activity activity;

    private Context context;

    private int layoutResId;

    public List<SearchResult> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private String category;

    private int reqWidth, reqHeight;

    private int prevItem = -1;

    public boolean doAnimate = true;



    public SearchBaseAdapter(Context context, int layoutResId, List<SearchResult> deals) {
        this.context = context;

        this.activity = (Activity) context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();

        Resources resources = context.getResources();

        reqWidth = resources.getDisplayMetrics().widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._105sdp)
                    / resources.getDisplayMetrics().density);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setData(List<SearchResult> deals) {
        this.deals = deals;
    }

    public void clearData()
    {
        if(this.deals != null)
        {
            deals.clear();
        }
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public SearchResult getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deals.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SearchResult deal = getItem(position);

        View row = convertView;

        if(row == null) {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.layout = row.findViewById(R.id.layout_deal_detail);
            holder.tvCategory = (TextView) row.findViewById(R.id.category_icon);
            holder.ivFav = (ImageView) row.findViewById(R.id.fav);
            holder.ivShare = (ImageView) row.findViewById(R.id.share);
            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            holder.tvViews = (TextView) row.findViewById(R.id.views);
            holder.rbRatings = (RatingBar) row.findViewById(R.id.ratings);
            holder.ivPromotional = (ImageView) row.findViewById(R.id.promotional_banner);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
            holder.rlPromotionalLayout = (RelativeLayout) row.findViewById(R.id.promotion_layout);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if(holder.tvCategory != null)
        {
            String category = deal.category;
            holder.tvCategory.setText(category);

            int categoryDrawable = ResourceUtils.getDrawableResId(this.category);
            if(categoryDrawable > 0) {
                holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, 0, 0, 0);
            }
        }

        deal.isFav = Favorite.isFavorite(deal.id);

        holder.ivFav.setSelected(deal.isFav);
        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        holder.tvTitle.setText(deal.title);

        holder.tvDetail.setText(deal.description);

        if(deal.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDiscount.setVisibility(View.VISIBLE);
        }
        holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

        holder.layout.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(isSearchEnabled()) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.showHideSearchBar(false);
                } else {
                    showDetail(deal);
                }*/

                showDetail(deal);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(isSearchEnabled()) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.showHideSearchBar(false);
                } else {
                    DealDetailActivity.shareDeal((Activity) context, deal.id);
                }*/

                DealDetailActivity.shareDeal((Activity) context, deal.id);
            }
        });

        holder.rbRatings.setRating(deal.rating);

        holder.tvViews.setText(valueOf(deal.views));

        String promotionalBanner = deal.image != null ? deal.image.bannerUrl : null;

        Logger.print("promotionalBanner: " + promotionalBanner);

        if(promotionalBanner != null && holder.ivPromotional != null)
        {
            holder.rlPromotionalLayout.setVisibility(View.VISIBLE);

            String url = BaseAsyncTask.IMAGE_BASE_URL + promotionalBanner;

            Bitmap bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap != null)
            {
                holder.ivPromotional.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                fallBackToDiskCache(url);
            }

            holder.ivPromotional.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   /* if(isSearchEnabled()) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        homeActivity.showHideSearchBar(false);
                    } else {
                        showDetail(deal);
                    }*/

                    showDetail(deal);
                }
            });
        }
        else
        {
            if(holder.ivPromotional != null)
            {
                holder.rlPromotionalLayout.setVisibility(View.GONE);

               /* holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                holder.progressBar.setVisibility(View.GONE);
                holder.ivPromotional.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetail(deal);
                    }
                });*/
            }
        }

        if(doAnimate && position > 0)
        {
            //AnimUtils.slideView(activity, row, prevItem < position);
        }
        else
        {
            doAnimate = true;
        }

        prevItem = position;

        return row;
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

                    activity.runOnUiThread(new Runnable()
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                            holder.progressBar.setVisibility(View.VISIBLE);

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(SearchBaseAdapter.this);

                            bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                            String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });

                   // bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));
                }
            }
        });

        thread.start();
    }

    private boolean isSearchEnabled() {
        return activity instanceof HomeActivity && ((HomeActivity) activity).isSearchEnabled;
    }

    private void showDetail(SearchResult result)
    {
        /*this.genericDeal = deal;

        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;


        showDealDetailActivity(category, deal);*/

        RecentViewedActivity.addToRecentViewed(result);
        Log.i("ITEM", String.valueOf(result.type));
        result.views += 1;

        final boolean isBusiness = StringUtils.isNotNullOrEmpty(result.type) && result.type.equalsIgnoreCase("business");

        if(isBusiness) {

            BusinessDetailActivity.selectedBusiness = new Business(result);

            //mActivity.showDetailActivity(BUSINESS, DEAL_CATEGORIES[2], result.id);
            //showBusinessDetailActivity("", new Business(result));
            ((HomeActivity) context).showBusinessDetailActivity(DEAL_CATEGORIES[2], new Business(result));
        } else {

            //mActivity.showDetailActivity(DEAL, DEAL_CATEGORIES[0], result.id);
            //showDealDetailActivity("", new GenericDeal(result));
            ((HomeActivity) context).showDealDetailActivity(DEAL_CATEGORIES[0], new GenericDeal(result));
        }

        notifyDataSetChanged();
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            SearchResult searchResult = getItem(position);

            Logger.logI("FAV_DEAL: " + searchResult.id, String.valueOf(searchResult.isFav));

            searchResult.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favDeal = new Favorite(searchResult);
            Favorite.updateFavorite(favDeal);
        }
    }

    private static class Holder {

        View layout;

        ImageView ivFav, ivShare, ivPromotional;

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvViews;

        RatingBar rbRatings;

        ProgressBar progressBar;

        RelativeLayout rlPromotionalLayout;
    }
}