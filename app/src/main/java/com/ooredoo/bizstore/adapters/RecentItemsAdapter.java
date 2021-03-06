package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.RecentItem;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;
import java.util.Random;

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

            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            FontUtils.setFontWithStyle(context,  holder.tvDiscount, Typeface.BOLD);
            holder.ivPromotional = (ImageView) row.findViewById(R.id.promotional_banner);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
            holder.rlPromotionalLayout = (RelativeLayout) row.findViewById(R.id.promotion_layout);
            holder.ivBrand = (ImageView) row.findViewById(R.id.brand_logo);
            holder.tvBrandName = (TextView) row.findViewById(R.id.brand_name);
            FontUtils.setFontWithStyle(context,  holder.tvBrandName, Typeface.BOLD);
            holder.tvBrandAddress = (TextView) row.findViewById(R.id.brand_address);
            holder.tvDirections = (TextView) row.findViewById(R.id.directions);
            FontUtils.setFontWithStyle(context, holder.tvDirections, Typeface.BOLD);
            holder.tvBrandText = (TextView) row.findViewById(R.id.brand_txt);
            FontUtils.setFontWithStyle(context, holder.tvBrandText, Typeface.BOLD);
            holder.ivDiscountTag = (ImageView) row.findViewById(R.id.discount_tag);
            holder.rlHeader = (RelativeLayout) row.findViewById(R.id.header);
            holder.llFooter = (LinearLayout) row.findViewById(R.id.footer);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                holder.rlHeader.setBackgroundResource(R.drawable.list_header);
                holder.llFooter.setBackgroundResource(R.drawable.list_footer);
            }

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.tvBrandName.setText(recentItem.businessName);

        holder.tvBrandAddress.setText(recentItem.location);

        if((recentItem.latitude != 0 && recentItem.longitude != 0)
                && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
        {
            holder.tvDirections.setVisibility(View.VISIBLE);
            float results[] = new float[3];
            Location.distanceBetween(HomeActivity.lat, HomeActivity.lng, recentItem.latitude, recentItem.longitude,
                    results);

            holder.tvDirections.setText(String.format("%.1f",(results[0] / 1000)) + "km");
        }
        else
        {
            holder.tvDirections.setVisibility(View.GONE);
        }

        String brandLogoUrl = recentItem.businessLogo != null ? recentItem.businessLogo : null;

        Logger.print("BrandLogo: " + brandLogoUrl);

        if(brandLogoUrl != null )
        {
            holder.tvBrandText.setVisibility(View.GONE);
            String url = BaseAsyncTask.IMAGE_BASE_URL + brandLogoUrl;

            Bitmap bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap != null)
            {
                holder.ivBrand.setImageBitmap(bitmap);
                //holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                holder.ivBrand.setImageBitmap(null);

                fallBackToDiskCache(url);
            }
        }
        else
        {
            holder.tvBrandText.setVisibility(View.VISIBLE);

            if(recentItem.color == 0)
            {
                recentItem.color = Color.parseColor(ColorUtils.getColorCode());
            }
            if(recentItem.businessName != null)
            {
                holder.tvBrandText.setText(String.valueOf(recentItem.businessName.charAt(0)));
                holder.tvBrandText.setBackgroundColor(recentItem.color);
            }

            holder.ivBrand.setImageBitmap(null);
        }


        holder.tvTitle.setText(recentItem.title);

        holder.tvDetail.setText(recentItem.description);

        if(recentItem.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.ivDiscountTag.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.ivDiscountTag.setVisibility(View.VISIBLE);
        }
        holder.tvDiscount.setText(valueOf(recentItem.discount) + "%\n" + context.getString(R.string.off));

        if(BizStore.getLanguage().equals("en"))
        {
            holder.tvDiscount.setRotation(-40);
        }
        else
        {
            holder.tvDiscount.setRotation(40);
        }

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
                holder.ivPromotional.setImageBitmap(null);
                holder.ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));
                holder.progressBar.setVisibility(View.VISIBLE);

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

        holder.rlHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailActivity(recentItem);
            }
        });

        holder.llFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailActivity(recentItem);
            }
        });

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
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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

    private static class Holder {

        View layout;

        ImageView  ivPromotional, ivBrand, ivDiscountTag;

        TextView tvTitle, tvDetail, tvDiscount, tvBrandName, tvBrandAddress,
                tvDirections, tvBrandText;

        ProgressBar progressBar;

        LinearLayout llFooter;

        RelativeLayout rlPromotionalLayout, rlHeader;
    }
}