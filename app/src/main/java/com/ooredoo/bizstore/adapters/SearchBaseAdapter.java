package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import java.util.List;
import java.util.Random;

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

    private View doItBusinessWay(final SearchResult result, int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        if(row == null) {
            row = inflater.inflate(R.layout.list_search_business, parent, false);

            holder = new Holder();

            holder.ivBrand = (ImageView) row.findViewById(R.id.brand_logo);
            holder.tvBrandName = (TextView) row.findViewById(R.id.brand_name);
            FontUtils.setFontWithStyle(context, holder.tvBrandName, Typeface.BOLD);
            holder.tvBrandAddress = (TextView) row.findViewById(R.id.brand_address);
            holder.tvDirections = (TextView) row.findViewById(R.id.directions);
            FontUtils.setFontWithStyle(context, holder.tvDirections, Typeface.BOLD);
            holder.tvBrandText = (TextView) row.findViewById(R.id.brand_txt);
            FontUtils.setFontWithStyle(context, holder.tvBrandText, Typeface.BOLD);
            holder.rlHeader = (RelativeLayout) row.findViewById(R.id.header);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                row.setBackgroundResource(R.drawable.masked_ripple_light);
            }



            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.tvBrandName.setText(result.businessName);

        holder.tvBrandAddress.setText(result.location);

        //deal.isFav = Favorite.isFavorite(deal.id);

        // holder.ivFav.setSelected(deal.isFav);
        //holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        String brandLogoUrl = result.businessLogo != null ? result.businessLogo : null;

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
                //holder.progressBar.setVisibility(View.VISIBLE);

                fallBackToDiskCache(url);
            }
        }
        else
        {
            holder.tvBrandText.setVisibility(View.VISIBLE);
            if(result.businessName != null && !result.businessName.isEmpty())
            {
                if(result.color == 0)
                {
                    result.color = Color.parseColor(ColorUtils.getColorCode());
                }

                holder.tvBrandText.setText(String.valueOf(result.businessName.charAt(0)));
                holder.tvBrandText.setBackgroundColor(result.color);
            }

            holder.ivBrand.setImageBitmap(null);
        }

        holder.tvDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDirections(result);
            }
        });

        if((result.latitude != 0 && result.longitude != 0)
                && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
        {
            holder.tvDirections.setVisibility(View.VISIBLE);
            float results[] = new float[3];
            Location.distanceBetween(HomeActivity.lat, HomeActivity.lng, result.latitude, result.longitude,
                    results);

            holder.tvDirections.setText(String.format("%.1f", (results[0] / 1000)) + " " + context.getString(R.string.km));
        }
        else
        {
            holder.tvDirections.setVisibility(View.GONE);
        }

        holder.rlHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(result);
            }
        });

        return row;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SearchResult deal = getItem(position);

        if(HomeActivity.searchType.equals("business"))
        {
            return doItBusinessWay(deal, position, convertView, parent);
        }

        View row = convertView;

        if(row == null) {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            FontUtils.setFontWithStyle(context,  holder.tvDiscount, Typeface.BOLD);
            holder.ivDiscountTag = (ImageView) row.findViewById(R.id.discount_tag);

            holder.ivPromotional = (ImageView) row.findViewById(R.id.promotional_banner);
            holder.ivDiscountTag = (ImageView) row.findViewById(R.id.discount_tag);
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
            holder.rlHeader = (RelativeLayout) row.findViewById(R.id.header);
            holder.llFooter = (LinearLayout) row.findViewById(R.id.footer);
            holder.tvPrice = (TextView) row.findViewById(R.id.prices);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                holder.rlHeader.setBackgroundResource(R.drawable.list_header);
                holder.llFooter.setBackgroundResource(R.drawable.list_footer);
            }

            if(BuildConfig.FLAVOR.equals("mobilink"))
            {
                holder.tvValidity = (TextView) row.findViewById(R.id.validity);
            }

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if(BuildConfig.FLAVOR.equals("mobilink"))
        {
            if(deal.endDate != null && !deal.endDate.isEmpty())
            {
                holder.tvValidity.setText("Valid Till: " + deal.endDate);
                holder.tvValidity.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.tvValidity.setVisibility(View.GONE);
            }
        }

        if(deal.actualPrice > 0 && deal.discountedPrice > 0)
        {
            holder.tvDetail.setVisibility(View.GONE);

            holder.tvPrice.setVisibility(View.VISIBLE);

            String qar = context.getString(R.string.qar);

            String discountedPrice = qar + " " + deal.discountedPrice;

            String actualPrice = qar + " " + deal.actualPrice;

            FontUtils.strikeThrough(holder.tvPrice, discountedPrice + "  -  " + actualPrice,
                    actualPrice, context.getResources().getColor(R.color.slight_grey));

        }
        else
        {
            holder.tvPrice.setVisibility(View.GONE);

            holder.tvDetail.setVisibility(View.VISIBLE);
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

        holder.tvBrandName.setText(deal.businessName);

        holder.tvBrandAddress.setText(deal.location);

        holder.tvTitle.setText(deal.title);

        holder.tvDetail.setText(deal.description);

        String brandLogoUrl = deal.businessLogo != null ? deal.businessLogo : null;

        Logger.print("BrandLogo: " + brandLogoUrl);

        if(brandLogoUrl != null )
        {
            holder.tvBrandText.setVisibility(View.GONE);
            String url = BaseAsyncTask.IMAGE_BASE_URL + brandLogoUrl;

            Bitmap bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap != null)
            {
                holder.ivBrand.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                holder.ivBrand.setImageBitmap(null);
                holder.progressBar.setVisibility(View.VISIBLE);

                fallBackToDiskCache(url);
            }
        }
        else
        {
            holder.tvBrandText.setVisibility(View.VISIBLE);
            if(deal.businessName != null && !deal.businessName.isEmpty())
            {
                if(deal.color == 0)
                {
                    deal.color = Color.parseColor(ColorUtils.getColorCode());
                }

                holder.tvBrandText.setText(String.valueOf(deal.businessName.charAt(0)));
                holder.tvBrandText.setBackgroundColor(deal.color);
            }

            holder.ivBrand.setImageBitmap(null);
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

        holder.tvDiscount.setText(valueOf(deal.discount) + "%\n" + context.getString(R.string.off));

        if(BizStore.getLanguage().equals("en"))
        {
            holder.tvDiscount.setRotation(-40);
        }
        else
        {
            holder.tvDiscount.setRotation(40);
        }

        if(BizStore.getLanguage().equals("en")) {
            holder.tvDiscount.setRotation(-40);
        } else {
            holder.tvDiscount.setRotation(40);
        }

        String promotionalBanner = deal.image != null ? deal.image.bannerUrl : null;

        Logger.print("promotionalBanner: " + promotionalBanner);

        if(promotionalBanner != null && holder.ivPromotional != null)
        {
            holder.rlPromotionalLayout.setVisibility(View.VISIBLE);

            String url = BaseAsyncTask.IMAGE_BASE_URL + promotionalBanner;

            Bitmap bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap != null)
            {
                holder.progressBar.setVisibility(View.GONE);

                holder.ivPromotional.setImageBitmap(bitmap);

                if(!deal.isBannerDisplayed)
                {
                    deal.isBannerDisplayed = true;

                    AnimatorUtils.fadeIn(holder.ivPromotional);
                }
            }
            else
            {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.ivPromotional.setImageBitmap(null);
                holder.ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));

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

        holder.tvDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDirections(deal);
            }
        });

        if((deal.latitude != 0 && deal.longitude != 0)
                && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
        {
            holder.tvDirections.setVisibility(View.VISIBLE);
            float results[] = new float[3];
            Location.distanceBetween(HomeActivity.lat, HomeActivity.lng, deal.latitude, deal.longitude,
                    results);

            holder.tvDirections.setText(String.format("%.1f", (results[0] / 1000)) + " " + context.getString(R.string.km));
        }
        else
        {
            holder.tvDirections.setVisibility(View.GONE);
        }

        holder.rlHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(deal);
            }
        });

        holder.llFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(deal);
            }
        });

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
                            //holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                            //holder.progressBar.setVisibility(View.VISIBLE);

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


    private static class Holder {

        View layout;

        ImageView ivFav, ivShare, ivPromotional, ivBrand, ivDiscountTag;

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvViews, tvBrandName, tvBrandAddress,
                tvDirections, tvBrandText, tvPrice, tvValidity;

        RatingBar rbRatings;

        ProgressBar progressBar;

        LinearLayout llFooter;

        RelativeLayout rlPromotionalLayout, rlHeader;
    }

    private void startDirections(SearchResult genericDeal)
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        if(mLat != 0 && mLong != 0)
        {
            src = "saddr=" + mLat + "," + mLong + "&";
        }

        if(genericDeal.latitude != 0 && genericDeal.longitude != 0)
        {
            dest = "daddr="+genericDeal.latitude + "," + genericDeal.longitude;
        }

        String uri = "http://maps.google.com/maps?";

        if(src != null)
        {
            uri += src;
        }

        if(dest != null)
        {
            uri += dest;
        }

        System.out.println("Directions URI:"+uri);

        if(src == null)
        {
            Toast.makeText(context, "Location not available. Please enable location services!", Toast.LENGTH_SHORT).show();

            return;
        }

        if(dest == null)
        {
            Toast.makeText(context, "Deal location is not available!", Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));

        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}