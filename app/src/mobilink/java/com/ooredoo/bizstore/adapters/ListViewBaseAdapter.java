package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.transition.Explode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.CalculateDistanceTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.views.NonScrollableGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

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

    ImageView markerImageView;
    TextView tvBrandText;
    FrameLayout linearLayout;
    //public boolean isDealOfDay;

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
            //notifyDataSetChanged();
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

        if(this.brands != null)
        {
            if(brands.size() > 0 && brands.get(0).isHeader)
            {
                filterHeaderBrand = brands.get(0);
            }

            brands.clear();
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
            if(brands.size() > 0 && filterHeaderBrand != null)
            {
                return 2;
            }
            else
            if(brands.size() > 0 || listingType.equals("map"))
            {
                return 1;
            }
            else
            {
                return 0;
            }
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

    int lastPosition = -1;

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
                   // tvFilter.setText("Filter: "+ filter);
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

                holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
                FontUtils.setFontWithStyle(context,  holder.tvDiscount, Typeface.BOLD);
                holder.ivDiscountTag = (ImageView) row.findViewById(R.id.discount_tag);
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


                if(BuildConfig.FLAVOR.equals("mobilink"))
                {
                    holder.tvValidity = (TextView) row.findViewById(R.id.validity);
                    //holder.tvTitle.setVisibility(View.GONE);
                    holder.tvDiscount.setVisibility(View.GONE);
                    holder.ivDiscountTag.setVisibility(View.GONE);

                    //holder.tvDetail.setTextSize(16);
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    holder.rlHeader.setBackgroundResource(R.drawable.list_header);
                    holder.llFooter.setBackgroundResource(R.drawable.list_footer);
                }

                row.setTag(holder);
            } else {
                holder = (Holder) row.getTag();
            }

            if(BuildConfig.FLAVOR.equals("mobilink"))
            {
                if(deal.endDate != null && !deal.endDate.isEmpty())
                {
                    //holder.tvValidity.setText("Valid till: " + deal.endDate);
                    holder.tvValidity.setText(" "+deal.endDate);
                    holder.tvValidity.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.tvValidity.setVisibility(View.GONE);
                }

               deal.mDistance = deal.distance * 1000;
            }


            if(!isFilterShowing&& position == 0)
            {
                //row.setPadding(0, -24, 0, 0);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                params.topMargin = -24;
               // holder.rlHeader.setLayoutParams(params);
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


            if(!BuildConfig.FLAVOR.equals("mobilink")) {
                if (deal.discount == 0) {
                    holder.tvDiscount.setVisibility(View.GONE);
                    holder.ivDiscountTag.setVisibility(View.GONE);
                } else {
                    holder.tvDiscount.setText(valueOf(deal.discount) + "%\n" + context.getString(R.string.off));

                    holder.tvDiscount.setVisibility(View.VISIBLE);
                    holder.ivDiscountTag.setVisibility(View.VISIBLE);
                }
            }

            if(BizStore.getLanguage().equals("en"))
            {
                holder.tvDiscount.setRotation(-40);
            }
            else
            {
                holder.tvDiscount.setRotation(40);
            }


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
                        if(isSearchEnabled()) {
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

                    holder.ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));

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
                    genericDeal = deal;
                    CommonHelper.startDirections(context, genericDeal);
                }
            });

            if((deal.latitude != 0 && deal.longitude != 0)
                    && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
            {

                /*float results[] = new float[3];
                Location.distanceBetween(HomeActivity.lat, HomeActivity.lng, deal.latitude, deal.longitude,
                        results);*/

                if(deal.mDistance != 0)
                {
                    holder.tvDirections.setVisibility(View.VISIBLE);

                    holder.tvDirections.setText(String.format("%.1f",(deal.mDistance / 1000))
                            + " " + context.getString(R.string.km));
                }
                else
                {
                    holder.tvDirections.setVisibility(View.GONE);

                    boolean isZero = false;

                    if(deal.distanceStatus != null && ((deal.distanceStatus.equals("ZERO_RESULTS"))))
                    {
                        isZero = true;
                    }

                    String origin = HomeActivity.lat + "," + HomeActivity.lng;
                    String destination = deal.latitude + "," + deal.longitude;

                    if(!isZero) {
                        if (CalculateDistanceTask.distancePool.get(deal.id) == null && deal.mDistance == 0) {

                            if(calculateDistanceTask.getStatus() != AsyncTask.Status.RUNNING)
                            {
                                calculateDistanceTask = new CalculateDistanceTask(deal, this);
                                calculateDistanceTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                        origin, destination);
                            }
                        }
                    }
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

            /*if(doAnimate && position > 0)
            {
                //AnimUtils.slideView(activity, row, prevItem < position);
            }
            else
            {
                doAnimate = true;
            }

            prevItem = position;*/

            return row;
        }
        else
            if(listingType.equals("brands"))
            {
                Brand brand = brands.get(position);

                if(brand.isHeader)
                {
                    final HomeActivity homeActivity = (HomeActivity) activity;
                    String filter = "";

                    if(homeActivity.doApplyDiscount)
                    {
                        filter = context.getString(R.string.sort_by) + ": "
                                + context.getString(R.string.sort_discount) + ", ";
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
                        // tvFilter.setText("Filter: "+ filter);
                    }

                    ImageView ivCloseFilerTag = (ImageView) filterHeader.findViewById(R.id.close);
                    ivCloseFilerTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            tvFilter.setText("");

                            brands.remove(0);
                            filterHeaderBrand = null;

                            filterHeaderDeal = null;

                            notifyDataSetChanged();

                            homeActivity.resetFilters();
                                    ((OnFilterChangeListener) fragment).onFilterChange();
                        }
                    });

                    return filterHeader;
                }

                List<Brand> mBrands = new ArrayList<>();
                mBrands.addAll(brands);

                if(filterHeaderBrand != null)
                {
                    mBrands.remove(0);

                }

                SimilarBrandsAdapter adapter = new SimilarBrandsAdapter(context, R.layout.grid_brand, mBrands);

                NonScrollableGridView gridView = new NonScrollableGridView(context, null);
               // gridView.setHorizontalSpacing(-30);
                //gridView.setVerticalSpacing(-30);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    gridView.setDrawSelectorOnTop(true);
                }
                else
                {
                    gridView.setSelector(new ColorDrawable());
                }

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Brand brand1 = (Brand) parent.getItemAtPosition(position);

                        if(brand1.isHeader)
                        {
                            return;
                        }

                        brand1.views += 1;

                        Business business = new Business(brand1);

                        Intent intent = new Intent(context, BusinessDetailActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // intent.setClass(context, BusinessDetailActivity.class);
                        intent.putExtra("business", business);
                        intent.putExtra(CATEGORY, "N/A");
                        context.startActivity(intent);
                    }
                });
                gridView.setNumColumns(3);
               // gridView.setHorizontalSpacing(-(int) resources.getDimension(R.dimen._4sdp));
                gridView.setVerticalSpacing((int) resources.getDimension(R.dimen._10sdp));

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                {
                    if(BizStore.getLanguage().equals("ar"))
                        gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus1sdp));
   /*             else
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._8sdp));*/
                }
               // gridView.setHorizontalSpacing(-(int) resources.getDimension(R.dimen._5sdp));
                //gridView.setVerticalSpacing(-(int) resources.getDimension(R.dimen._5sdp));

                gridView.setGravity(Gravity.CENTER_HORIZONTAL);

                gridView.setAdapter(adapter);

                return gridView;
            }

        return null;

    }

    CalculateDistanceTask calculateDistanceTask = new CalculateDistanceTask(null,null);

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



    private boolean isSearchEnabled() {
        return activity instanceof HomeActivity && ((HomeActivity) activity).isSearchEnabled;
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

        ImageView  ivPromotional, ivBrand, ivDiscountTag;

        TextView tvTitle, tvDetail, tvDiscount, tvBrandName, tvBrandAddress,
                 tvDirections, tvBrandText, tvPrice, tvValidity;

        ProgressBar progressBar;

        LinearLayout llFooter;

        RelativeLayout rlPromotionalLayout, rlHeader;
    }


}