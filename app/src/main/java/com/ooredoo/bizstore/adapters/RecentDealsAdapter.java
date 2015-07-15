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
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.RecentDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static java.lang.String.valueOf;

public class RecentDealsAdapter extends ArrayAdapter<RecentDeal> {

    Activity mActivity;
    int layoutResID;
    List<RecentDeal> deals;

    private int prevItem = -1;

    public RecentDealsAdapter(Activity activity, int layoutResourceID, List<RecentDeal> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.deals = items;
        this.layoutResID = layoutResourceID;
    }

    public void setData(List<RecentDeal> deals) {
        this.deals = deals;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final RecentDeal deal = this.deals.get(position);

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
            holder.tvCategory = (TextView) view.findViewById(R.id.tv_category);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.ivFav = (ImageView) view.findViewById(R.id.iv_fav);
            holder.ivShare = (ImageView) view.findViewById(R.id.iv_share);
            holder.ivCategory = (ImageView) view.findViewById(R.id.iv_category);

            holder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

            view.setTag(holder);
        }

        holder.tvDesc.setText(deal.description);
        holder.tvTitle.setText(deal.title);
        holder.tvCategory.setText(deal.category);
        holder.tvViews.setText(valueOf(deal.views));
        holder.tvDiscount.setText(String.valueOf(deal.discount) + mActivity.getString(R.string.percentage_off));

        int categoryIcon = ResourceUtils.getDrawableResId(mActivity, deal.category);
        if(categoryIcon > 0) {
            holder.ivCategory.setImageResource(categoryIcon);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        deal.isFavorite = RecentDeal.isFavorite(deal);

        holder.ivFav.setSelected(deal.isFavorite);

        holder.ratingBar.setRating(deal.rating);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.isFavorite = !deal.isFavorite;
                v.setSelected(deal.isFavorite);
                Deal favDeal = new Deal(deal);
                Deal.updateDealAsFavorite(favDeal);
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
        RatingBar ratingBar;
        ImageView ivFav, ivShare, ivCategory;
        TextView tvTitle, tvViews, tvDesc, tvDiscount, tvCategory;
    }
}