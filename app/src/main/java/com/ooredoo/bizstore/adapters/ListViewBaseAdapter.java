package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.DEAL;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.utils.ResourceUtils.getDrawableResId;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class ListViewBaseAdapter extends BaseAdapter {
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    private String category;

    public ListViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals) {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setData(List<GenericDeal> deals) {
        this.deals = deals;
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public GenericDeal getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deals.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GenericDeal deal = getItem(position);

        View row = convertView;

        if(row == null) {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvCategory = (TextView) row.findViewById(R.id.category_icon);
            holder.ivFav = (ImageView) row.findViewById(R.id.fav);
            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            holder.tvViews = (TextView) row.findViewById(R.id.views);
            holder.rbRatings = (RatingBar) row.findViewById(R.id.ratings);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        String category = deal.category;
        holder.tvCategory.setText(category);

        int drawable = getDrawableResId(context, this.category);
        if(drawable > 0) {
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        }

        deal.isFav = Deal.isFavorite(deal.id);

        holder.ivFav.setSelected(deal.isFav);
        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        holder.tvTitle.setText(deal.title);

        holder.tvDetail.setText(deal.detail);

        holder.tvDiscount.setText(String.valueOf(deal.discount) + "% OFF");

        holder.tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Deal recentDeal = new Deal(deal);
                RecentViewedActivity.addToRecentViewed(recentDeal);
                ((HomeActivity) context).showDetailActivity(DEAL, DEAL_CATEGORIES[2], deal.id);
            }
        });

        // holder.rbRatings.setRating(getItem(position).rating);

        // holder.tvViews.setText(String.valueOf(getItem(position).views));

        return row;
    }

    private class FavouriteOnClickListener implements View.OnClickListener {
        private int position;

        public FavouriteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            boolean isSelected = v.isSelected();

            v.setSelected(!isSelected);

            GenericDeal genericDeal = getItem(position);
            genericDeal.isFav = !isSelected;

            Deal favDeal = new Deal(genericDeal);
            favDeal.save();
        }
    }

    private static class Holder {
        ImageView ivFav;

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvViews;

        RatingBar rbRatings;
    }
}
