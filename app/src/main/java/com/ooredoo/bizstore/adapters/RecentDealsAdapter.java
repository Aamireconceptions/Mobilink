package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.RecentDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static java.lang.String.valueOf;

public class RecentDealsAdapter extends ArrayAdapter<RecentDeal> {

    Activity mActivity;
    int layoutResID;
    List<RecentDeal> deals;

    public RecentDealsAdapter(Activity activity, int layoutResourceID, List<RecentDeal> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.deals = items;
        this.layoutResID = layoutResourceID;
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

            holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.tvViews = (TextView) view.findViewById(R.id.tv_views);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.ivFav = (ImageView) view.findViewById(R.id.iv_fav);
            holder.ivShare = (ImageView) view.findViewById(R.id.iv_share);

            view.setTag(holder);
        }

        holder.tvDesc.setText(deal.desc);
        holder.tvTitle.setText(deal.title);
        holder.tvViews.setText(valueOf(deal.views));
        holder.tvDiscount.setText(String.valueOf(deal.discount) + "% OFF");

        holder.tvDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailActivity(DEAL_CATEGORIES[2], deal.id);
            }
        });

        deal.isFavorite = RecentDeal.isFavorite(deal);

        holder.ivFav.setSelected(deal.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.isFavorite = !deal.isFavorite;
                deal.save();
                v.setSelected(deal.isFavorite);
            }
        });

        return view;
    }

    public void showDetailActivity(String dealCategory, long typeId) {
        Intent intent = new Intent();
        intent.setClass(mActivity, DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, typeId);
        intent.putExtra(CATEGORY, dealCategory);
        mActivity.startActivity(intent);
    }

    private static class Holder {
        ImageView ivFav, ivShare;
        TextView tvTitle, tvViews, tvDesc, tvDiscount;
    }
}