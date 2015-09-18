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
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

/*public class FavoritesAdapter extends ArrayAdapter<Favorite> {

    Activity mActivity;
    int layoutResID;
    List<Favorite> favorites;
    private int prevItem = -1;

    public FavoritesAdapter(Activity activity, int layoutResourceID, List<Favorite> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.favorites = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Favorite favorite = this.favorites.get(position);

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

        *//*String category = favorite.category;
        holder.tvCategory.setText(category);
        int categoryIcon = ResourceUtils.getDrawableResId(favorite.category);
        if(categoryIcon > 0) {
            holder.ivCategory.setImageResource(categoryIcon);
        }*//*

        holder.tvDesc.setText(favorite.description);
        holder.tvTitle.setText(favorite.title);
        holder.tvViews.setText(valueOf(favorite.views));

        holder.tvDiscount.setVisibility(favorite.isBusiness ? View.GONE : View.VISIBLE);
        holder.ivTypeIcon.setImageResource(favorite.isBusiness ? R.drawable.ic_business : R.drawable.ic_deal_tag);

        if(favorite.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvDiscount.setText(String.valueOf(favorite.discount) + getContext().getString(R.string.percentage_off));

        holder.ratingBar.setRating(favorite.rating);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.selectedDeal = null;
                BusinessDetailActivity.selectedBusiness = null;


                showDetailActivity(favorite);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite.isBusiness) {
                    BusinessDetailActivity.shareBusiness(mActivity, favorite.id);
                } else {
                    DealDetailActivity.shareDeal(mActivity, favorite.id);
                }
            }
        });

        favorite.isFavorite = true; //Deal.isFavorite(favorite.id);

        holder.ivFav.setSelected(favorite.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite.isFavorite = false;
                favorite.save();
                favorites.remove(favorite);
                notifyDataSetChanged();
                ((MyFavoritesActivity) mActivity).toggleEmptyView(getCount());
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void showDetailActivity(Favorite favorite) {
        Intent intent = new Intent();
        if(favorite.isBusiness) {
            intent.setClass(mActivity, BusinessDetailActivity.class);
            intent.putExtra("business", new Business(favorite));
        } else {
            intent.setClass(mActivity, DealDetailActivity.class);
            intent.putExtra("generic_deal", new GenericDeal(favorite));
        }
        intent.putExtra(AppConstant.ID, favorite.id);
        mActivity.startActivity(intent);
        RecentViewedActivity.addToRecentViewed(favorite);
    }

    private static class Holder {
        View detail;
        RatingBar ratingBar;
        ImageView ivTypeIcon, ivShare, ivFav;
        TextView tvTitle, tvDesc, tvDiscount, tvViews;
    }*/

public class FavoritesAdapter extends BaseAdapter {

    private Context context;

    private int layoutResId;

    public List<Favorite> favorites;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private String category;

    private int reqWidth, reqHeight;

    public boolean doAnimate = true;

    public GenericDeal genericDeal;

    public FavoritesAdapter(MyFavoritesActivity context, int layoutResId, List<Favorite> favorites) {
        this.context = context;

        this.layoutResId = layoutResId;

        this.favorites = favorites;

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
        return favorites.size();
    }

    @Override
    public Favorite getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return favorites.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Favorite fav = getItem(position);

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
            String category = fav.category;
            holder.tvCategory.setText(category);

            int categoryDrawable = ResourceUtils.getDrawableResId(this.category);
            if(categoryDrawable > 0) {
                holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, 0, 0, 0);
            }
        }

        fav.isFav = Favorite.isFavorite(fav.id);

        holder.ivFav.setSelected(fav.isFav);
        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        holder.tvTitle.setText(fav.title);

        holder.tvDetail.setText(fav.description);

        if(fav.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDiscount.setVisibility(View.VISIBLE);
        }
        holder.tvDiscount.setText(valueOf(fav.discount) + context.getString(R.string.percentage_off));

        holder.layout.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDetailActivity(fav);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.shareDeal((Activity) context, fav.id);
            }
        });

        holder.rbRatings.setRating(fav.rating);

        holder.tvViews.setText(valueOf(fav.views));

        String promotionalBanner = fav.banner;

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

                    showDetailActivity(fav);
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

                    ((MyFavoritesActivity) context).runOnUiThread(new Runnable() {
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
                                    new BaseAdapterBitmapDownloadTask(FavoritesAdapter.this);

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

    public void showDetailActivity(Favorite favorite) {
        Intent intent = new Intent();
        if(favorite.isBusiness) {
            intent.setClass(context, BusinessDetailActivity.class);
            intent.putExtra("business", new Business(favorite));
        } else {
            intent.setClass(context, DealDetailActivity.class);
            intent.putExtra("generic_deal", new GenericDeal(favorite));
        }
        intent.putExtra(AppConstant.ID, favorite.id);
        context.startActivity(intent);
        RecentViewedActivity.addToRecentViewed(favorite);
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

            Favorite favorite = getItem(position);

            favorite.isFavorite = false;
            favorite.save();
            favorites.remove(favorite);
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