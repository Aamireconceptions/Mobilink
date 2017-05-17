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
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
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
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.NonScrollableGridView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static java.lang.String.valueOf;

/**
 * Created by Babar on 30-Oct-15.
 */
public class BusinessAdapter extends BaseExpandableListAdapter
{
    private Context context;

    private List<String> groupList;

    private List<List<?>> childList;

    private LayoutInflater layoutInflater;

    private Resources resources;

    MemoryCache memoryCache = MemoryCache.getInstance();
    DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;

    public BusinessAdapter(Context context, List<String> groupList, List<List<?>> childList)
    {
        this.context = context;

        this.groupList = groupList;

        this.childList = childList;

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        resources = context.getResources();

        reqWidth = resources.getDisplayMetrics().widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._105sdp)
                / resources.getDisplayMetrics().density);
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

            convertView = inflater.inflate(R.layout.business_group_view, parent, false);
        }

        TextView textView = (TextView) convertView;
        textView.setAllCaps(true);

        textView.setText(getGroup(groupPosition));


        if(groupPosition == 0)
        {
            textView.setBackgroundColor(resources.getColor(R.color.red));
        }
        else
        {
            textView.setBackgroundColor(resources.getColor(R.color.orange));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        View childView = null;

        childView = convertView;

        Logger.print("group:position:" + groupPosition + ", child:position:" + childPosition);
        if(getChild(groupPosition, childPosition) instanceof GenericDeal)
        {
            if(childView == null || !(childView instanceof LinearLayout))
            {
                childView = layoutInflater.inflate(R.layout.list_deal_promotional_similar_deals, parent, false);
            }

            childView.setPadding(0, (int) resources.getDimension(R.dimen._12sdp), 0,
                    (int) resources.getDimension(R.dimen._12sdp));

            if(childPosition == 0 && getChildrenCount(groupPosition) > 1)
            {
                childView.setPadding(0, (int) resources.getDimension(R.dimen._12sdp), 0,
                        (int) resources.getDimension(R.dimen._6sdp));
            }
            else
            {
                if(getChildrenCount(groupPosition) > 1)
                {
                    childView.setPadding(0, (int) resources.getDimension(R.dimen._6sdp), 0,
                            (int) resources.getDimension(R.dimen._6sdp));
                }
                else
                {
                    childView.setPadding(0, (int) resources.getDimension(R.dimen._12sdp), 0,
                            (int) resources.getDimension(R.dimen._12sdp));
                }
            }

            if(childPosition != 0 && childPosition == getChildrenCount(groupPosition) - 1)
            {
                childView.setPadding(0, (int) resources.getDimension(R.dimen._6sdp), 0,
                                     (int) resources.getDimension(R.dimen._12sdp));
            }

            final GenericDeal deal = (GenericDeal) getChild(groupPosition, childPosition);

            TextView tvTitle = (TextView) childView.findViewById(R.id.title);
            FontUtils.setFontWithStyle(context, tvTitle, Typeface.BOLD);

            TextView tvDetail = (TextView) childView.findViewById(R.id.detail);
            TextView tvDiscount = (TextView) childView.findViewById(R.id.discount);
            FontUtils.setFontWithStyle(context, tvDiscount, Typeface.BOLD);

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

            LinearLayout llFooter = (LinearLayout) childView.findViewById(R.id.layout_deal_detail);

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

            String brandLogoUrl = deal.businessLogo;

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
                    ivBrand.setImageBitmap(null);

                    CommonHelper.fallBackToDiskCache(url, diskCache, memoryCache, this, reqWidth, reqHeight);
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

            llFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(deal);
                }
            });

            rlHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(deal);
                }
            });

            String promotionalBanner = deal.image != null ? deal.image.detailBannerUrl : null;

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

                    if(!deal.isBannerDisplayed)
                    {
                        deal.isBannerDisplayed = true;

                        AnimatorUtils.fadeIn(ivPromotional);
                    }
                }
                else
                {
                    ivPromotional.setImageBitmap(null);
                    ivPromotional.setBackgroundColor(context.getResources().getColor(R.color.banner));
                    progressBar.setVisibility(View.VISIBLE);

                    CommonHelper.fallBackToDiskCache(url, diskCache, memoryCache, this, reqWidth, reqHeight);
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

            SimilarBrandsAdapter adapter = new SimilarBrandsAdapter(context, R.layout.grid_brand,
                    (List<Brand>) childList.get(groupPosition));

            NonScrollableGridView gridView = new NonScrollableGridView(context, null);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Brand brand1 = (Brand) parent.getItemAtPosition(position);

                    brand1.views += 1;

                    Business business = new Business(brand1);

                    Intent intent = new Intent(context, BusinessDetailActivity.class);
                    intent.putExtra("business", business);
                    intent.putExtra(CATEGORY, "N/A");
                    context.startActivity(intent);
                }
            });
            gridView.setNumColumns(3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                gridView.setPaddingRelative((int) resources.getDimension(R.dimen._6sdp), (int) resources.getDimension(R.dimen._12sdp),
                        (int) resources.getDimension(R.dimen._6sdp), (int) resources.getDimension(R.dimen._12sdp));
            }
            else
            {
                gridView.setPadding((int) resources.getDimension(R.dimen._6sdp), (int) resources.getDimension(R.dimen._12sdp),
                        (int) resources.getDimension(R.dimen._6sdp), (int) resources.getDimension(R.dimen._12sdp));
            }

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

    private void showDetail(GenericDeal deal)
    {
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
        context.startActivity(intent);
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}