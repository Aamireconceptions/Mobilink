package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 18-Jun-15
 */
public class GridViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    public List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private DiskCache diskCache = DiskCache.getInstance();

    private int reqWidth, reqHeight;
    public String listingType;

    public GridViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();

        reqWidth = Resources.getSystem().getDisplayMetrics().widthPixels / 2;

        reqHeight = reqWidth;

        Logger.print("GridView thumbnail reqWidth:"+reqWidth+", reqHeight: "+reqHeight);
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
    }

    public void clearData()
    {
        deals.clear();

        //brands.clear();
    }

    @Override
    public int getCount()
    {
        if(listingType.equals("deals"))
        {
            return deals.size();
        }
        else
        {
            return brands.size();
        }
    }

    @Override
    public Object getItem(int position)
    {
        if(listingType.equals("deals"))
        {
            return deals.get(position);
        }
        else
        {
            return brands.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return deals == null || deals.size() == 0 ? 0 : deals.get(position).id;
    }

    public void setListingType(String type)
    {
        this.listingType = type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(listingType.equals("deals")) {
            if (grid == null) {
                grid = inflater.inflate(layoutResId, parent, false);

                holder = new Holder();

                holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
                holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
                holder.tvDesc = (TextView) grid.findViewById(R.id.desc);
                holder.tvTitle = (TextView) grid.findViewById(R.id.title);
                FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);
                holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);
                FontUtils.setFontWithStyle(context, holder.tvDiscount, Typeface.BOLD);
                holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);
                holder.ivDiscountTag = (ImageView) grid.findViewById(R.id.discount_tag);
                holder.tvPrice = (TextView) grid.findViewById(R.id.prices);

                if(BuildConfig.FLAVOR.equals("mobilink"))
                {
                    holder.tvValidity = (TextView) grid.findViewById(R.id.validity);
                }

                grid.setTag(holder);
            } else {
                holder = (Holder) grid.getTag();
            }

            final GenericDeal deal = (GenericDeal) getItem(position);

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
                holder.tvDesc.setVisibility(View.GONE);

                holder.tvPrice.setVisibility(View.VISIBLE);

                String qar = context.getString(R.string.qar);

                String discountedPrice = qar + " " + deal.discountedPrice;

                String actualPrice = qar + " " + deal.actualPrice;

                FontUtils.strikeThrough(holder.tvPrice, discountedPrice + " - " + actualPrice,
                        actualPrice, context.getResources().getColor(R.color.slight_grey));
            }
            else
            {
                holder.tvPrice.setVisibility(View.GONE);

                holder.tvDesc.setVisibility(View.VISIBLE);
            }

            if(deal.color == 0)
            {
                deal.color = Color.parseColor(ColorUtils.getColorCode());
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

            holder.tvDiscount.setText(valueOf(deal.discount) + "%\n"+context.getString(R.string.off));

            if(BizStore.getLanguage().equals("en"))
            {
                holder.tvDiscount.setRotation(-40);
            }
            else
            {
                holder.tvDiscount.setRotation(40);
            }

            deal.isFav = Favorite.isFavorite(deal.id);

            holder.ivFav.setSelected(deal.isFav);

            holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

            Image image = deal.image;
            if (image != null && image.gridBannerUrl != null) {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                Logger.print("Pos: " + position + "imgUrl: " + imgUrl + ", bitmap: " + bitmap);
                if (bitmap != null) {
                    holder.ivThumbnail.setImageBitmap(bitmap);
                    holder.progressBar.setVisibility(View.GONE);

                    if(!deal.isLogoDisplayed)
                    {
                        deal.isLogoDisplayed = true;

                        AnimatorUtils.fadeIn(holder.ivThumbnail);
                    }

                } else {
                    holder.ivThumbnail.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.banner)));
                    holder.progressBar.setVisibility(View.VISIBLE);

                    CommonHelper.fallBackToDiskCache(imgUrl, diskCache, memoryCache, this, reqWidth, reqHeight);
                }
            } else {
                holder.ivThumbnail.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.banner)));
                holder.progressBar.setVisibility(View.GONE);
            }

           /* if (deal.discount == 0) {
                holder.tvDiscount.setVisibility(View.GONE);
            } else {
                holder.tvDiscount.setVisibility(View.VISIBLE);
            }*/

            holder.tvTitle.setText(deal.businessName);
            holder.tvDesc.setText(deal.title);
           // holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

            return grid;
        }
        /*else
            if(listingType.equals("brands"))
            {
                if (grid == null) {
                    grid = inflater.inflate(layoutResId, parent, false);

                    holder = new Holder();

                    holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
                    holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
                    holder.tvDesc = (TextView) grid.findViewById(R.id.desc);
                    holder.tvTitle = (TextView) grid.findViewById(R.id.title);
                    FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);
                    holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);
                    FontUtils.setFontWithStyle(context, holder.tvDiscount, Typeface.BOLD);
                    holder.progressBar = (ProgressBar) grid.findViewById(R.id.progress_bar);

                    grid.setTag(holder);
                } else {
                    holder = (Holder) grid.getTag();
                }

                final Brand brand = (Brand) getItem(position);

                if(brand.color == 0)
                {
                    brand.color = Color.parseColor(ColorUtils.getColorCode());
                }

                brand.isFavorite = Favorite.isFavorite(brand.id);

                holder.ivFav.setSelected(brand.isFavorite);

                holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

                Image image = brand.image;
                if (image != null && image.gridBannerUrl != null) {
                    String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                    Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                    Logger.print("Pos: " + position + "imgUrl: " + imgUrl + ", bitmap: " + bitmap);
                    if (bitmap != null) {
                        holder.ivThumbnail.setImageBitmap(bitmap);

                        holder.progressBar.setVisibility(View.GONE);


                    } else {
                        holder.ivThumbnail.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.banner)));
                       // holder.ivThumbnail.setBackgroundColor(context.getResources().getColor(R.color.banner));
                        holder.progressBar.setVisibility(View.VISIBLE);
                        fallBackToDiskCache(imgUrl);
                    }
                } else {
                    holder.ivThumbnail.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.banner)));
//                    holder.ivThumbnail.setBackgroundColor(context.getResources().getColor(R.color.banner));
                    holder.progressBar.setVisibility(View.GONE);
                }

                holder.tvTitle.setText(brand.title);
                holder.tvDesc.setText(brand.description);

                return grid;
            }*/

        return null;
    }



    List<Brand> brands = new ArrayList<>();

    public void setBrandsList(List<Brand> brands) {
        this.brands = brands;
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            GenericDeal genericDeal = (GenericDeal) getItem(position);

            Logger.logI("FAV_DEAL: " + genericDeal.id, String.valueOf(genericDeal.isFav));

            genericDeal.isFav = !isSelected;

            v.setSelected(!isSelected);

            Favorite favorite = new Favorite(genericDeal);
            Favorite.updateFavorite(favorite, false);
        }
    }

    private static class Holder
    {
        ImageView ivThumbnail, ivFav, ivDiscountTag;

        TextView tvTitle, tvDiscount, tvDesc, tvPrice, tvValidity;;

        ProgressBar progressBar;
    }
}