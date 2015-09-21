package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.RecentItem;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

/*
public class RecentItemsAdapter extends ArrayAdapter<RecentItem> {

    Activity mActivity;
    int layoutResID;
    List<RecentItem> recentItems;
    private int prevItem = -1;

    public RecentItemsAdapter(Activity activity, int layoutResourceID, List<RecentItem> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.recentItems = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final RecentItem recentItem = this.recentItems.get(position);

        final Holder holder;
        View view = convertView;

        final LayoutInflater inflater = mActivity.getLayoutInflater();

        if(view == null) {
            holder = new Holder();
        } else {
            holder = (Holder) view.getTag();
        }

        if(view == null) {
            view = inflater.inflate(layoutResID, parent, false);

            holder.detail = view.findViewById(R.id.rl_search_result);

            holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.tvViews = (TextView) view.findViewById(R.id.tv_views);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.ivTypeIcon = (ImageView) view.findViewById(R.id.iv);
            holder.ivFav = (ImageView) view.findViewById(R.id.iv_fav);
            holder.ivShare = (ImageView) view.findViewById(R.id.iv_share);

            holder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

            view.setTag(holder);
        }

        holder.tvDesc.setText(recentItem.description);
        holder.tvTitle.setText(recentItem.title);
        holder.tvViews.setText(valueOf(recentItem.views));

        holder.tvDiscount.setVisibility(recentItem.isBusiness ? View.GONE : View.VISIBLE);
        holder.ivTypeIcon.setImageResource(recentItem.isBusiness ? R.drawable.ic_business : R.drawable.ic_deal_tag);

        if(recentItem.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvDiscount.setText(String.valueOf(recentItem.discount) + getContext().getString(R.string.percentage_off));

        holder.ratingBar.setRating(recentItem.rating);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.selectedDeal = null;
                BusinessDetailActivity.selectedBusiness = null;
                showDetailActivity(recentItem);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recentItem.isBusiness) {
                    BusinessDetailActivity.shareBusiness(mActivity, recentItem.id);
                } else {
                    DealDetailActivity.shareDeal(mActivity, recentItem.id);
                }
            }
        });

        recentItem.isFavorite = Favorite.isFavorite(recentItem.id);

        holder.ivFav.setSelected(recentItem.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentItem.isFavorite = !recentItem.isFavorite;
                v.setSelected(recentItem.isFavorite);
                Favorite favorite = new Favorite(recentItem);
                Favorite.updateFavorite(favorite);
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void showDetailActivity(RecentItem recentItem) {
        Intent intent = new Intent();
        if(recentItem.isBusiness) {
            Business business = new Business(recentItem);
            BusinessDetailActivity.selectedBusiness = business;
            intent.setClass(mActivity, BusinessDetailActivity.class);
            intent.putExtra("business", business);
        } else {
            intent.putExtra("generic_deal", new GenericDeal(recentItem));
            intent.setClass(mActivity, DealDetailActivity.class);
        }
        //intent.putExtra(AppConstant.ID, recentItem.id);
        mActivity.startActivity(intent);
    }

    public void showDealDetailActivity(String dealCategory, RecentItem recentItem)
    {
        Intent intent = new Intent();

        if(recentItem.isBusiness)
        {
            intent.setClass(mActivity, BusinessDetailActivity.class);
            intent.putExtra("business", new Business(recentItem));
        }
        else
        {
            intent.setClass(mActivity, DealDetailActivity.class);
            intent.putExtra("generic_deal", new GenericDeal(recentItem));
        }

        intent.putExtra(CATEGORY, dealCategory);
        mActivity.startActivityForResult(intent, 1);
    }

    private static class Holder {
        View detail;
        RatingBar ratingBar;
        ImageView ivTypeIcon, ivShare, ivFav;
        TextView tvTitle, tvDesc, tvDiscount, tvViews;
    }
*/

public class RecentItemsAdapter extends ArrayAdapter<RecentItem> {

    private Context context;

    private int layoutResId;

    public List<RecentItem> recentItems;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private String category;

    private int reqWidth, reqHeight;

    public boolean doAnimate = true;

    public GenericDeal genericDeal;

    public RecentItemsAdapter(RecentViewedActivity context, int layoutResId, List<RecentItem> recentItems) {

        super(context, layoutResId, recentItems);
        this.context = context;

        this.layoutResId = layoutResId;

        this.recentItems = recentItems;

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

    @Override
    public int getCount() {
        return recentItems.size();
    }

    @Override
    public RecentItem getItem(int position) {
        return recentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recentItems.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final RecentItem recentItem = getItem(position);

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
            String category = recentItem.category;
            holder.tvCategory.setText(category);

            int categoryDrawable = ResourceUtils.getDrawableResId(this.category);
            if(categoryDrawable > 0) {
                holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, 0, 0, 0);
            }
        }

        recentItem.isFavorite = Favorite.isFavorite(recentItem.id);

        holder.ivFav.setSelected(recentItem.isFavorite);
        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        holder.tvTitle.setText(recentItem.title);

        holder.tvDetail.setText(recentItem.description);

        if(recentItem.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDiscount.setVisibility(View.VISIBLE);
        }
        holder.tvDiscount.setText(valueOf(recentItem.discount) + context.getString(R.string.percentage_off));

        holder.layout.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailActivity(recentItem);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.shareDeal((Activity) context, recentItem.id);
            }
        });

        holder.rbRatings.setRating(recentItem.rating);

        holder.tvViews.setText(valueOf(recentItem.views));

        String promotionalBanner = recentItem.banner;

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
                    //showDetail(new GenericDeal(fav));

                    showDetailActivity(recentItem);
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

                    ((RecentViewedActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.print(" dCache fallback notifyDataSetChanged");
                            notifyDataSetChanged();
                        }
                    });
                }
                else
                {
                    ((MyFavoritesActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                            holder.progressBar.setVisibility(View.VISIBLE);

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(RecentItemsAdapter.this);

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

   /* private void showDetail(GenericDeal deal)
    {
        this.genericDeal = deal;

        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;

        showDealDetailActivity(category, deal);
    }*/

    public void showDetailActivity(RecentItem recentItem) {
        Intent intent = new Intent();
        if(recentItem.isBusiness) {
            intent.setClass(context, BusinessDetailActivity.class);
            intent.putExtra("business", new Business(recentItem));
        } else {
            intent.setClass(context, DealDetailActivity.class);
            intent.putExtra("generic_deal", new GenericDeal(recentItem));
        }
        intent.putExtra(AppConstant.ID, recentItem.id);
        context.startActivity(intent);
        //RecentViewedActivity.addToRecentViewed(recentItem);
    }

    public void showDealDetailActivity(String dealCategory, GenericDeal genericDeal)
    {
        Intent intent = new Intent();
        intent.setClass(context, DealDetailActivity.class);
        intent.putExtra("generic_deal", genericDeal);
        intent.putExtra(CATEGORY, dealCategory);
        context.startActivity(intent);
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            /*Favorite genericDeal = getItem(position);

            Logger.logI("FAV_DEAL: " + genericDeal.id, String.valueOf(genericDeal.isFav));

            genericDeal.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favDeal = new Favorite(genericDeal);
            Favorite.updateFavorite(favDeal);*/

            RecentItem recentItem = getItem(position);

            recentItem.isFavorite = false;
            recentItem.save();
            recentItems.remove(recentItem);
            notifyDataSetChanged();
            ((MyFavoritesActivity) context).toggleEmptyView(getCount());
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