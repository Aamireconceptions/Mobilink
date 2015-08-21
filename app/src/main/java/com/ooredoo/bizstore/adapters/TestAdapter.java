package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static java.lang.String.valueOf;

/**
 * Created by Babar on 11-Aug-15.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>
{
    private Activity activity;

    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private MemoryCache memoryCache;

    private String category;

    private int reqWidth, reqHeight;

    private int prevItem = -1;

    public boolean doAnimate = true;

    public TestAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v  = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final GenericDeal deal = deals.get(position);

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
        holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

        holder.layout.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearchEnabled()) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.showHideSearchBar(false);
                } else {
                    showDetail(deal);
                }
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearchEnabled()) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.showHideSearchBar(false);
                } else {
                    DealDetailActivity.shareDeal((Activity) context, deal.id);
                }
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
                holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                holder.progressBar.setVisibility(View.VISIBLE);

                /*BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                        new BaseAdapterBitmapDownloadTask(this);

                *//*bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url,
                                        String.valueOf(reqWidth), String.valueOf(reqHeight));*//*

                bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));*/
            }

            holder.ivPromotional.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSearchEnabled()) {
                        HomeActivity homeActivity = (HomeActivity) activity;
                        homeActivity.showHideSearchBar(false);
                    } else {
                        showDetail(deal);
                    }
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

        /*if(doAnimate)
        {
            AnimUtils.slideView(activity, holder.v, prevItem < position);
        }
        else
        {
            doAnimate = true;
        }*/

        prevItem = position;


    }

    private boolean isSearchEnabled() {
        return activity instanceof HomeActivity && ((HomeActivity) activity).isSearchEnabled;
    }

    private void showDetail(GenericDeal deal) {
        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;
        ((HomeActivity) context).showDealDetailActivity(category, deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            GenericDeal genericDeal = deals.get(position);

            Logger.logI("FAV_DEAL: " + genericDeal.id, String.valueOf(genericDeal.isFav));

            genericDeal.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favDeal = new Favorite(genericDeal);
            Favorite.updateFavorite(favDeal);
        }
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
    }

    public void clearData()
    {
        this.deals.clear();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
      //  public TextView mTextView;

        View layout;

        ImageView ivFav, ivShare, ivPromotional;

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvViews;

        RatingBar rbRatings;

        ProgressBar progressBar;

        RelativeLayout rlPromotionalLayout;

        public ViewHolder(View v)
        {
            super(v);

            layout = v.findViewById(R.id.layout_deal_detail);
            tvCategory = (TextView) v.findViewById(R.id.category_icon);
            ivFav = (ImageView) v.findViewById(R.id.fav);
            ivShare = (ImageView) v.findViewById(R.id.share);
            tvTitle = (TextView) v.findViewById(R.id.title);
            tvDetail = (TextView) v.findViewById(R.id.detail);
            tvDiscount = (TextView) v.findViewById(R.id.discount);
            tvViews = (TextView) v.findViewById(R.id.views);
            rbRatings = (RatingBar) v.findViewById(R.id.ratings);
            ivPromotional = (ImageView) v.findViewById(R.id.promotional_banner);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            rlPromotionalLayout = (RelativeLayout) v.findViewById(R.id.promotion_layout);
        }
    }


}
