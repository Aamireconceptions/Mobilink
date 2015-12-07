package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
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

        Logger.print("group:position:" + groupPosition + ", child:position:" + childPosition);
        if(getChild(groupPosition, childPosition) instanceof GenericDeal)
        {
            childView = layoutInflater.inflate(R.layout.list_deal_promotional_similar_deals, parent, false);
            childView.setPadding(0, (int) resources.getDimension(R.dimen._5sdp), 0,
                    (int) resources.getDimension(R.dimen._5sdp));

            if(childPosition == 0)
            {
                childView.setPadding(0, (int) resources.getDimension(R.dimen._10sdp), 0,
                        (int) resources.getDimension(R.dimen._5sdp));
            }

            if(childPosition == getChildrenCount(groupPosition) - 1)
            {
                childView.setPadding(0, (int) resources.getDimension(R.dimen._5sdp), 0,
                                     (int) resources.getDimension(R.dimen._10sdp));
            }

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
            TextView tvBrandText = (TextView) childView.findViewById(R.id.brand_txt);
            TextView tvDirections = (TextView) childView.findViewById(R.id.directions);
            ImageView ivDiscountTag = (ImageView) childView.findViewById(R.id.discount_tag);

            //holder.tvBrandName.setText(genericDeal.brandName);
            //holder.tvBrandAddress.setText(genericDeal.brandAddress);

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
                tvBrandText.setVisibility(View.VISIBLE);
                if(deal.businessName != null)
                {
                    if(deal.color == 0)
                    {
                        deal.color = Color.parseColor(getColorCode());
                    }

                    tvBrandText.setText(String.valueOf(deal.businessName.charAt(0)));
                    tvBrandText.setBackgroundColor(deal.color);
                }

                ivBrand.setImageBitmap(null);
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
                ivDiscountTag.setVisibility(View.GONE);
            }
            else
            {
                tvDiscount.setVisibility(View.VISIBLE);
                ivDiscountTag.setVisibility(View.VISIBLE);
            }

            tvDiscount.setText(valueOf(deal.discount) + "%\n"+context.getString(R.string.off));

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

            return childView;
        }
        else
        {
           // Brand brand = (Brand) getChild(groupPosition, childPosition);

            SimilarBrandsAdapter adapter = new SimilarBrandsAdapter(context, R.layout.grid_brand, (List<Brand>) childList.get(groupPosition));

           // GridView gridView = (GridView) layoutInflater.inflate(R.layout.grid_view, parent, false);
            NonScrollableGridView gridView = new NonScrollableGridView(context, null);


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Brand brand1 = (Brand) parent.getItemAtPosition(position);

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
            gridView.setPadding((int) resources.getDimension(R.dimen._10sdp), (int) resources.getDimension(R.dimen._10sdp),
                    (int) resources.getDimension(R.dimen._10sdp), (int) resources.getDimension(R.dimen._10sdp));
            gridView.setHorizontalSpacing((int) resources.getDimension(R.dimen._8sdp));
            gridView.setVerticalSpacing((int) resources.getDimension(R.dimen._10sdp));

            gridView.setGravity(Gravity.CENTER_HORIZONTAL);

            gridView.setAdapter(adapter);

            //GridLayout gridLayout = new GridLayout(context);

           /* View brandView = layoutInflater.inflate(R.layout.brand, parent, false);

            brandView.setTag(brand);
            brandView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            TextView tvTitle = (TextView) brandView.findViewById(R.id.text_view);
            tvTitle.setText(brand.title);*/

           /* if(brand.businessLogo != null && !brand.businessLogo.isEmpty())
            {
                String logoUrl = BaseAsyncTask.IMAGE_BASE_URL + brand.businessLogo;

                Bitmap bitmap = memoryCache.getBitmapFromCache(logoUrl);

                ImageView ivImageView = (ImageView) brandView.findViewById(R.id.image_view);


                ProgressBar progressBar = (ProgressBar) brandView.findViewById(R.id.progressBar);

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
            }*/



            return gridView;
        }
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

    public String getColorCode()
    {
        int min = 1;
        int max = 8;

        Random random = new Random();

        int i = random.nextInt(max - min) + min;

        Logger.print("random: "+i);

        String color = null;
        switch (i)
        {
            case 1:
                color = "#90a4ae";
                break;
            case 2:
                color = "#ff8a65";
                break;
            case 3:
                color = "#ba68c8";
                break;
            case 4:
                color = "#da4336";
                break;
            case 5:
                color = "#4fc3f7";
                break;
            case 6:
                color = "#ffa726";
                break;
            case 7:
                color = "#aed581";
                break;
            case 8:
                color = "#b39ddb";
                break;
        }

        return color;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}