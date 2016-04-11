package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.NonScrollableGridView;

import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

/**
 * Created by Babar on 30-Oct-15.
 */
public class MallDetailAdapter extends BaseExpandableListAdapter
{
    private Context context;

    private List<String> groupList;

    private List<List<?>> childList;

    private LayoutInflater layoutInflater;

    private Resources resources;

    MemoryCache memoryCache = MemoryCache.getInstance();
    DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight, padding;

    public HashMap<String, Boolean> expandStateMap = new HashMap<>();

    private String type;

    public MallDetailAdapter(Context context, List<String> groupList, List<List<?>> childList)
    {
        this.context = context;

        this.groupList = groupList;

        this.childList = childList;

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        resources = context.getResources();

        reqWidth = resources.getDisplayMetrics().widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._105sdp)
                / resources.getDisplayMetrics().density);

        padding = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._16sdp) /
        resources.getDisplayMetrics().density);
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public int getGroupCount() {

        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if(childList.size() > 0 && childList.get(groupPosition).get(0) instanceof Brand)
        {
            return 1;
        }

        return childList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition)
    {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.mall_group_view, parent, false);
           convertView.setPaddingRelative(padding, 0, padding, 0);
            convertView.setBackground(null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.name);
        FontUtils.setFont(context, textView);

        ImageView ivIndicator = (ImageView) convertView.findViewById(R.id.indicator);

        String category = Converter.convertCategoryText(context, getGroup(groupPosition)).name.toUpperCase();

        //textView.setText(category);

        String part = category;

        if(category.contains(" "))
        {
            part = category.substring(0, category.indexOf(" "));
        }

        FontUtils.changeColorAndMakeBold(textView, category, part.toUpperCase(),
                context.getResources().getColor(R.color.red));

        String key = getGroup(groupPosition);
        Boolean isGroupExpanded = expandStateMap.get(key);

        if(isGroupExpanded != null && isGroupExpanded)
        {
            ivIndicator.setImageResource(R.drawable.ic_group_expand);
        }
        else
        {
            ivIndicator.setImageResource(R.drawable.ic_group_fwd);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        View childView = convertView;

        Logger.print("group:position:" + groupPosition + ", child:position:" + childPosition);
        if(getChild(groupPosition, childPosition) instanceof GenericDeal)
        {
            if(childView == null || ! (childView instanceof LinearLayout)) {
                childView = layoutInflater.inflate(R.layout.list_deal_promotional, parent, false);
                childView.setPaddingRelative((int) ( padding -
                                Converter.convertDpToPixels(context.getResources().getDimension(R.dimen._8sdp))/
                                        context.getResources().getDisplayMetrics().density),
                        0,
                        (int) (padding -
                                 Converter.convertDpToPixels(context.getResources().getDimension(R.dimen._8sdp))/
                                context.getResources().getDisplayMetrics().density)
                        , 0);
           }

            final GenericDeal deal = (GenericDeal) getChild(groupPosition, childPosition);

            TextView tvTitle = (TextView) childView.findViewById(R.id.title);
            FontUtils.setFontWithStyle(context, tvTitle, Typeface.BOLD);
            TextView tvDetail = (TextView) childView.findViewById(R.id.detail);
            TextView tvDiscount = (TextView) childView.findViewById(R.id.discount);
            ImageView ivPromotional = (ImageView) childView.findViewById(R.id.promotional_banner);
            ProgressBar progressBar = (ProgressBar) childView.findViewById(R.id.progress_bar);
            RelativeLayout rlPromotionalLayout = (RelativeLayout) childView.findViewById(R.id.promotion_layout);
            ImageView ivBrand = (ImageView) childView.findViewById(R.id.brand_logo);
            TextView tvBrandName = (TextView) childView.findViewById(R.id.brand_name);
            FontUtils.setFontWithStyle(context, tvBrandName, Typeface.BOLD);
            TextView tvBrandAddress = (TextView) childView.findViewById(R.id.brand_address);
            TextView tvBrandText = (TextView) childView.findViewById(R.id.brand_txt);
            FontUtils.setFontWithStyle(context, tvBrandText, Typeface.BOLD);
            TextView tvDirections = (TextView) childView.findViewById(R.id.directions);
            FontUtils.setFontWithStyle(context, tvDirections, Typeface.BOLD);
            ImageView ivDiscountTag = (ImageView) childView.findViewById(R.id.discount_tag);

            RelativeLayout rlHeader = (RelativeLayout) childView.findViewById(R.id.header);
            LinearLayout llFooter = (LinearLayout) childView.findViewById(R.id.footer);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
               rlHeader.setBackgroundResource(R.drawable.list_header);
               llFooter.setBackgroundResource(R.drawable.list_footer);
            }

            if((deal.latitude != 0 && deal.longitude != 0)
                    && (HomeActivity.lat != 0 && HomeActivity.lng != 0 ))
            {
                tvDirections.setVisibility(View.VISIBLE);
                float results[] = new float[3];
                Location.distanceBetween(HomeActivity.lat, HomeActivity.lng, deal.latitude, deal.longitude,
                        results);

                tvDirections.setText(String.format("%.1f",(results[0] / 1000)) + " " + context.getString(R.string.km));
            }
            else
            {
                tvDirections.setVisibility(View.GONE);
            }

           tvBrandName.setText(deal.businessName);

            tvBrandAddress.setText(deal.location);

            String brandLogoUrl = deal.image != null ? deal.image.logoUrl : null;

            Logger.print("BrandLogo: " + brandLogoUrl);

            if(brandLogoUrl != null )
            {
                String url = BaseAsyncTask.IMAGE_BASE_URL + brandLogoUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(url);

                if(bitmap != null)
                {
                    ivBrand.setImageBitmap(bitmap);
                }
                else
                {
                    ivBrand.setBackgroundColor(context.getResources().getColor(R.color.banner));

                    fallBackToDiskCache(url);
                }

                ivPromotional.setOnClickListener(new View.OnClickListener()
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
                tvBrandText.setVisibility(View.VISIBLE);
                if(deal.businessName != null && !deal.businessName.isEmpty())
                {
                    if(deal.color == 0)
                    {
                        deal.color = Color.parseColor(ColorUtils.getColorCode());
                    }

                    tvBrandText.setText(String.valueOf(deal.businessName.charAt(0)));
                    tvBrandText.setBackgroundColor(deal.color);
                }

                ivBrand.setImageBitmap(null);
            }

            deal.isFav = Favorite.isFavorite(deal.id);

            tvTitle.setText(deal.title);

            tvDetail.setText(deal.description);

            if(deal.discount == 0) {
                tvDiscount.setVisibility(View.GONE);
                ivDiscountTag.setVisibility(View.GONE);
            }
            else
            {
                tvDiscount.setVisibility(View.VISIBLE);
                ivDiscountTag.setVisibility(View.VISIBLE);
            }

            tvDiscount.setText(valueOf(deal.discount) + "%\n"+context.getString(R.string.off));

            rlHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(deal);
                }
            });
            llFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(deal);
                }
            });

            String promotionalBanner = deal.image != null ? deal.image.bannerUrl : null;

            Logger.print("promotionalBanner: " + promotionalBanner);

            if(promotionalBanner != null && ivPromotional != null)
            {
                rlPromotionalLayout.setVisibility(View.VISIBLE);

                String url = BaseAsyncTask.IMAGE_BASE_URL + promotionalBanner;

                Bitmap bitmap = memoryCache.getBitmapFromCache(url);

                if(bitmap != null)
                {
                    ivPromotional.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));
                    progressBar.setVisibility(View.VISIBLE);

                    fallBackToDiskCache(url);
                }

                ivPromotional.setOnClickListener(new View.OnClickListener()
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
                if(ivPromotional != null)
                {
                   rlPromotionalLayout.setVisibility(View.GONE);
                }
            }

            return childView;
        }
        else
        {
            List<Brand> testbrands = (List<Brand>) childList.get(groupPosition);

            SimilarBrandsAdapter adapter = new SimilarBrandsAdapter(context, R.layout.grid_brand,
                    (List<Brand>) childList.get(groupPosition));

            NonScrollableGridView gridView = new NonScrollableGridView(context, null);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Brand brand1 = (Brand) parent.getItemAtPosition(position);

                    Business business = new Business(brand1);

                    Intent intent = new Intent(context, BusinessDetailActivity.class);
                    intent.putExtra("business", business);
                    intent.putExtra(CATEGORY, "N/A");
                    context.startActivity(intent);
                }
            });
            gridView.setNumColumns(3);
            /*gridView.setPadding((int) resources.getDimension(R.dimen._9sdp), 0,
                   (int) resources.getDimension(R.dimen._9sdp), 0);*/

            gridView.setPaddingRelative(padding, 0, padding, 0);

            gridView.setVerticalSpacing((int) resources.getDimension(R.dimen._12sdp));

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                if(BizStore.getLanguage().equals("ar"))
                    gridView.setHorizontalSpacing((int) context.getResources().getDimension(R.dimen._minus1sdp));
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                gridView.setDrawSelectorOnTop(true);
            }
            else
            {
                gridView.setSelector(new ColorDrawable());
            }

          gridView.setGravity(Gravity.CENTER_HORIZONTAL);

           gridView.setAdapter(adapter);

           return gridView;
        }
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

        expandStateMap.put(getGroup(groupPosition), true);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);

        expandStateMap.put(getGroup(groupPosition), false);
    }

    private void showDetail(GenericDeal deal)
    {
        //this.genericDeal = deal;

        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;

        showDealDetailActivity(null, deal);
    }

    public void showDealDetailActivity(String dealCategory, GenericDeal genericDeal)
    {
        genericDeal.views += 1;
        Intent intent = new Intent();
        intent.setClass(context, DealDetailActivity.class);
        intent.putExtra("generic_deal", genericDeal);
        intent.putExtra(CATEGORY, dealCategory);
      //  context.startActivityForResult(intent, 1);
        context.startActivity(intent);
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

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Logger.print(" dCache fallback notifyDataSetChanged");
                            notifyDataSetChanged();
                        }
                    });
                }
                else
                {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                            // holder.progressBar.setVisibility(View.VISIBLE);

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(MallDetailAdapter.this);

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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}