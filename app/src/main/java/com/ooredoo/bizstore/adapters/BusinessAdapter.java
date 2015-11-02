package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

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

    MemoryCache memoryCache = MemoryCache.getInstance();
    DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;

    public BusinessAdapter(Context context, List<String> groupList, List<List<?>> childList)
    {
        this.context = context;

        this.groupList = groupList;

        this.childList = childList;

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        Resources resources = context.getResources();

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

        ((TextView) convertView).setText(getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        View childView = null;

        Logger.print("group:position:"+groupPosition+", child:position:"+childPosition);
        if(groupPosition == 0)
        {
            childView = layoutInflater.inflate(R.layout.list_deal_promotional, parent, false);

            final GenericDeal deal = (GenericDeal) getChild(groupPosition, childPosition);

                View layout = childView.findViewById(R.id.layout_deal_detail);
                TextView tvCategory = (TextView) childView.findViewById(R.id.category_icon);
                TextView tvTitle = (TextView) childView.findViewById(R.id.title);
                TextView tvDetail = (TextView) childView.findViewById(R.id.detail);
                TextView tvDiscount = (TextView) childView.findViewById(R.id.discount);
                ImageView ivPromotional = (ImageView) childView.findViewById(R.id.promotional_banner);
                ProgressBar progressBar = (ProgressBar) childView.findViewById(R.id.progress_bar);
                RelativeLayout rlPromotionalLayout = (RelativeLayout) childView.findViewById(R.id.promotion_layout);
                ImageView ivBrand = (ImageView) childView.findViewById(R.id.brand_logo);
                TextView tvBrandName = (TextView) childView.findViewById(R.id.brand_name);
                TextView tvBrandAddress = (TextView) childView.findViewById(R.id.brand_address);

            //holder.tvBrandName.setText(genericDeal.brandName);
            //holder.tvBrandAddress.setText(genericDeal.brandAddress);

            String brandLogoUrl = deal.image != null ? deal.image.logoUrl : null;

            Logger.print("BrandLogo: " + brandLogoUrl);

            if(brandLogoUrl != null )
            {
                String url = BaseAsyncTask.IMAGE_BASE_URL + brandLogoUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(url);

                if(bitmap != null)
                {
                    ivBrand.setImageBitmap(bitmap);
                    //holder.progressBar.setVisibility(View.GONE);
                }
                else
                {
                    ivBrand.setImageResource(R.drawable.deal_banner);

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

            if(tvCategory != null)
            {
                String category = deal.category;
                tvCategory.setText(category);

                int categoryDrawable = ResourceUtils.getDrawableResId(category);
                if(categoryDrawable > 0) {
                    tvCategory.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, 0, 0, 0);
                }
            }

            deal.isFav = Favorite.isFavorite(deal.id);

            // holder.ivFav.setSelected(deal.isFav);
            //  holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

            tvTitle.setText(deal.title);

            tvDetail.setText(deal.description);

            if(deal.discount == 0) {
                tvDiscount.setVisibility(View.GONE);
            }
            else
            {
                tvDiscount.setVisibility(View.VISIBLE);
            }
            tvDiscount.setText(valueOf(deal.discount) +"%\nOFF");

            layout.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail(deal);
                }
            });

       /* holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearchEnabled()) {
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.showHideSearchBar(false);
                } else {
                    DealDetailActivity.shareDeal((Activity) context, deal.id);
                }
            }
        });*/

//        holder.rbRatings.setRating(deal.rating);

            //   holder.tvViews.setText(valueOf(deal.views));

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
                    ivPromotional.setImageResource(R.drawable.deal_banner);
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
        }
        else
        {
            Brand brand = (Brand) getChild(groupPosition, childPosition);

            childView = layoutInflater.inflate(R.layout.grid_layout, parent, false);

            GridLayout gridLayout = (GridLayout) childView;
            gridLayout.addView(childView);

            View brandView = layoutInflater.inflate(R.layout.brand, parent, false);
            brandView.setTag(brand);
            brandView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            TextView tvTitle = (TextView) brandView.findViewById(R.id.text_view);
            tvTitle.setText(brand.title);

            String logoUrl = brand.image.logoUrl;

            Bitmap bitmap = memoryCache.getBitmapFromCache(logoUrl);

            ImageView ivImageView = (ImageView) brandView.findViewById(R.id.image_view);

            ProgressBar progressBar = (ProgressBar) brandView.findViewById(R.id.progress_bar);

            if(bitmap != null)
            {
                ivImageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }
            else
            {
                ivImageView.setImageBitmap(null);
                progressBar.setVisibility(View.VISIBLE);

                fallBackToDiskCache(logoUrl);
            }
        }

        return childView;
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

                    Handler handler = new Handler();

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
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // holder.ivPromotional.setImageResource(R.drawable.deal_banner);
                            // holder.progressBar.setVisibility(View.VISIBLE);

                            BaseAdapterBitmapDownloadTask bitmapDownloadTask =
                                    new BaseAdapterBitmapDownloadTask(BusinessAdapter.this);

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