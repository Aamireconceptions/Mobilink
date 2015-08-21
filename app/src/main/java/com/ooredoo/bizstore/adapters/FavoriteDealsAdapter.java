package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.MyDealsActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static java.lang.String.valueOf;

public class FavoriteDealsAdapter extends ArrayAdapter<Favorite> {

    Activity mActivity;
    int layoutResID;
    List<Favorite> deals;
    private int prevItem = -1;

    public FavoriteDealsAdapter(Activity activity, int layoutResourceID, List<Favorite> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.deals = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Favorite deal = this.deals.get(position);

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

            holder.layout = view.findViewById(R.id.layout_deal_detail);
            holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.tvViews = (TextView) view.findViewById(R.id.tv_views);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.tvCategory = (TextView) view.findViewById(R.id.tv_category);
            holder.ivFav = (ImageView) view.findViewById(R.id.iv_fav);
            holder.ivShare = (ImageView) view.findViewById(R.id.iv_share);
            holder.ivCategory = (ImageView) view.findViewById(R.id.iv_category);
            holder.rbRatings = (RatingBar) view.findViewById(R.id.rating_bar);

            view.setTag(holder);
        }

        String category = deal.category;
        holder.tvCategory.setText(category);

        int categoryIcon = ResourceUtils.getDrawableResId(deal.category);
        if(categoryIcon > 0) {
            holder.ivCategory.setImageResource(categoryIcon);
        }

        holder.tvDesc.setText(deal.description);
        holder.tvTitle.setText(deal.title);
        holder.tvViews.setText(valueOf(deal.views));

        if(deal.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvDiscount.setText(String.valueOf(deal.discount) + getContext().getString(R.string.percentage_off));

        holder.rbRatings.setRating(deal.rating);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentViewedActivity.addToRecentViewed(deal);
                DealDetailActivity.selectedDeal = null;
                showDetailActivity(DEAL_CATEGORIES[2], deal.id);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.shareDeal(mActivity, deal.id);
            }
        });

        deal.isFavorite = Favorite.isFavorite(deal.id);

        holder.ivFav.setSelected(deal.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.isFavorite = false;
                deal.save();
                deals.remove(deal);
                notifyDataSetChanged();
                ((MyDealsActivity) mActivity).toggleEmptyView(getCount());
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void showDetailActivity(String dealCategory, int typeId) {
        Intent intent = new Intent();
        intent.setClass(mActivity, DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, typeId);
        intent.putExtra(CATEGORY, dealCategory);
        mActivity.startActivity(intent);
    }

    private static class Holder {
        View layout;
        ImageView ivFav, ivShare, ivCategory;
        RatingBar rbRatings;
        TextView tvTitle, tvViews, tvDesc, tvDiscount, tvCategory;
    }
}