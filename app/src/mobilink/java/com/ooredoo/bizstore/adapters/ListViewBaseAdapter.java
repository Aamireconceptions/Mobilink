package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class ListViewBaseAdapter extends BaseAdapter {

    private Activity activity;

    private Context context;

    private int layoutResId;

    public List<GenericDeal> deals;

    private Fragment fragment;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private String category;

    private int reqWidth, reqHeight;

    private int prevItem = -1;

    public boolean doAnimate = true;

    public GenericDeal genericDeal;

    public String listingType = "";

    BitmapProcessor bitmapProcessor;

    Resources resources;

    public ListViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals,
                               Fragment fragment) {
        this.context = context;

        this.activity = (Activity) context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        this.fragment = fragment;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();

        resources = context.getResources();

        reqWidth = resources.getDisplayMetrics().widthPixels;

        reqHeight = resources.getDisplayMetrics().heightPixels / 2;

        bitmapProcessor = new BitmapProcessor();
    }

   public boolean isFilterShowing = false;

    public void setListingType(String type)
    {
        this.listingType = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setData(List<GenericDeal> deals) {
        this.deals = deals;

        if(filterHeaderDeal != null)
        {
            deals.add(0, filterHeaderDeal);
        }
    }

    public GenericDeal filterHeaderDeal;

    public Brand filterHeaderBrand;

    public void clearData()
    {
        if(this.deals != null)
        {
            if(deals.size() >0 && deals.get(0).isHeader)
            {
                filterHeaderDeal = deals.get(0);
            }

            deals.clear();
        }
    }


    @Override
    public int getCount() {

        if(listingType.equals("deals") || listingType.equals("list"))
        {
            return deals.size();
        }
        else
        {
           return 0;
        }
    }

    @Override
    public GenericDeal getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int subcategoryParent;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(listingType.equals("deals"))
        {
            Logger.print("getView");
            final GenericDeal deal = getItem(position);

            if(deal.isHeader)
            {
                final HomeActivity homeActivity = (HomeActivity) activity;
                String filter = "";

                if(homeActivity.doApplyDiscount)
                {
                    filter = context.getString(R.string.sort_by) + ": "
                            + context.getString(R.string.sort_discount) + ", ";
                }

                if(homeActivity.doApplyDistance)
                {
                    filter +=  "Distance: Nearest First, ";
                }

                if(homeActivity.doApplyRating)
                {
                    filter += context.getString(R.string.rating) + ": " + homeActivity.ratingFilter + ", ";
                }

                if(homeActivity.distanceFilter != null && subcategoryParent == CategoryUtils.CT_NEARBY)
                {
                    filter += context.getString(R.string.distance)
                            + ": " + homeActivity.distanceFilter
                            + " " + context.getString(R.string.km)+", ";
                }

                String categories = CategoryUtils.getSelectedSubCategoriesForTag(subcategoryParent);

                if(!categories.isEmpty())
                {
                    filter +=  context.getString(R.string.sub_categories)+": "+categories ;
                }

                if(! filter.isEmpty() && filter.charAt(filter.length() - 2) == ',')
                {
                    filter = filter.substring(0, filter.length() - 2);
                }

                View filterHeader = inflater.inflate(R.layout.layout_filter_tags, parent, false);

                final TextView tvFilter = (TextView) filterHeader.findViewById(R.id.filter);
                FontUtils.setFont(context, tvFilter);

                if(!filter.isEmpty())
                {
                    FontUtils.changeColorAndMakeBold(tvFilter,
                            context.getString(R.string.filter) + " : " + filter,
                            context.getString(R.string.filter) + " : ",
                            context.getResources().getColor(R.color.black));
                }

                ImageView ivCloseFilerTag = (ImageView) filterHeader.findViewById(R.id.close);
                ivCloseFilerTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvFilter.setText("");

                       deals.remove(0);
                        filterHeaderDeal = null;

                        filterHeaderBrand = null;

                        notifyDataSetChanged();

                        homeActivity.resetFilters();
                        ((OnFilterChangeListener) fragment).onFilterChange();
                    }
                });

                return filterHeader;
            }

            View row = null;
            if((convertView instanceof LinearLayout))
            {
                 row = convertView;
            }

            if(row == null) {
                row = inflater.inflate(layoutResId, parent, false);

                holder = new Holder();

                holder.tvTitle = (TextView) row.findViewById(R.id.title);
                FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);

                holder.tvDetail = (TextView) row.findViewById(R.id.detail);

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
                holder.rlHeader = (RelativeLayout) row.findViewById(R.id.header);
                holder.llFooter = (LinearLayout) row.findViewById(R.id.footer);
                holder.tvPrice = (TextView) row.findViewById(R.id.prices);


                holder.tvValidity = (TextView) row.findViewById(R.id.validity);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    holder.rlHeader.setBackgroundResource(R.drawable.list_header);
                    holder.llFooter.setBackgroundResource(R.drawable.list_footer);
                }

                row.setTag(holder);
            } else {
                holder = (Holder) row.getTag();
            }

            if(deal.endDate != null && !deal.endDate.isEmpty())
            {
                holder.tvValidity.setText(" "+deal.endDate);
                holder.tvValidity.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.tvValidity.setVisibility(View.GONE);
            }

            deal.mDistance = deal.distance * 1000;

            if(!isFilterShowing&& position == 0)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

               // params.topMargin = -24;
                params.topMargin = (int) context.getResources().getDimension(R.dimen._12sdp);
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

            holder.tvBrandName.setText(deal.businessName);

            holder.tvBrandAddress.setText(deal.location);

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
                }
                else
                {
                    holder.ivBrand.setImageBitmap(null);

                    CommonHelper.fallBackToDiskCache(url, diskCache, memoryCache, this,
                            reqWidth, reqHeight);
                }
            }
            else
            {
                holder.ivBrand.setImageBitmap(null);
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
                else
                {
                    holder.tvBrandText.setVisibility(View.GONE);
                }
            }

            holder.tvTitle.setText(deal.title);

            holder.tvDetail.setText(deal.description);

            String promotionalBanner = deal.image != null ? deal.image.detailBannerUrl : null;

            Logger.print("promotionalBanner: " + promotionalBanner);

            if(promotionalBanner != null && holder.ivPromotional != null)
            {
                holder.rlPromotionalLayout.setVisibility(View.VISIBLE);

                String url = BaseAsyncTask.IMAGE_BASE_URL + promotionalBanner;

               final Bitmap bitmap = memoryCache.getBitmapFromCache(url);

                if(bitmap != null)
                {
                    holder.progressBar.setVisibility(View.GONE);

                    holder.ivPromotional.setImageBitmap(bitmap);

                    if(!deal.isBannerDisplayed)
                    {
                        AnimatorUtils.fadeIn(holder.ivPromotional);
                        deal.isBannerDisplayed = true;
                    }
                }
                else
                {
                    holder.ivPromotional.setImageBitmap(null);
                    holder.ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));
                    holder.progressBar.setVisibility(View.VISIBLE);

                    CommonHelper.fallBackToDiskCache(url, diskCache, memoryCache, this,
                                                     reqWidth, reqHeight);
                }

                holder.ivPromotional.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        showDetail(deal);
                    }
                });
            }
            else
            {
                if(holder.ivPromotional != null)
                {
                    holder.rlPromotionalLayout.setVisibility(View.GONE);

                    holder.ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));
                }
            }

            holder.tvDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    genericDeal = deal;
                    CommonHelper.startDirections(context, genericDeal);
                }
            });

            if((deal.latitude != 0 && deal.longitude != 0)
                    && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
            {
                if(deal.mDistance != 0)
                {
                    holder.tvDirections.setVisibility(View.VISIBLE);

                    holder.tvDirections.setText(String.format("%.1f",(deal.mDistance / 1000))
                            + " " + context.getString(R.string.km));
                }
                else
                {
                    holder.tvDirections.setVisibility(View.GONE);
                }
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

            return row;
        }

        return null;
    }

    private HashMap<String, GenericDeal> genericDealHashMap;

    public void setGenericDealHashMap(HashMap<String, GenericDeal> genericDealHashMap)
    {
        this.genericDealHashMap = genericDealHashMap;
    }

    public List<Brand> brands = new ArrayList<>();

    public void setBrandsList(List<Brand> brands)
    {
        this.brands = brands;

        if(filterHeaderBrand != null)
        {
            this.brands.add(0, filterHeaderBrand);
        }
    }

    private void showDetail(GenericDeal deal)
    {
        this.genericDeal = deal;

        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;

        showDealDetailActivity(category, deal);
    }

    public void showDealDetailActivity(String dealCategory, GenericDeal genericDeal)
    {
        genericDeal.views += 1;
        Intent intent = new Intent();
        intent.setClass(activity, DealDetailActivity.class);
        intent.putExtra("generic_deal", genericDeal);
        intent.putExtra(CATEGORY, dealCategory);

        activity.startActivityForResult(intent, 1);
    }

     static class Holder {

        ImageView  ivPromotional, ivBrand;

        TextView tvTitle, tvDetail, tvBrandName, tvBrandAddress,
                 tvDirections, tvBrandText, tvPrice, tvValidity;

        ProgressBar progressBar;

        LinearLayout llFooter;

        RelativeLayout rlPromotionalLayout, rlHeader;
    }
}