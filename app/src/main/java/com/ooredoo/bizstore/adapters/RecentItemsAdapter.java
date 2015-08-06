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
import com.ooredoo.bizstore.model.RecentItem;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.AnimUtils;

import java.util.List;

import static java.lang.String.valueOf;

public class RecentItemsAdapter extends ArrayAdapter<RecentItem> {

    Activity mActivity;
    int layoutResID;
    List<RecentItem> recentItems;
    private int prevItem = -1;

    public RecentItemsAdapter(Activity activity, int layoutResourceID, List<RecentItem> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.recentItems = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final RecentItem recentItem = this.recentItems.get(position);

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

            holder.detail = view.findViewById(R.id.rl_search_result);

            holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.tvViews = (TextView) view.findViewById(R.id.tv_views);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.ivTypeIcon = (ImageView) view.findViewById(R.id.iv);
            holder.ivFav = (ImageView) view.findViewById(R.id.iv_fav);
            holder.ivShare = (ImageView) view.findViewById(R.id.iv_share);

            holder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

            view.setTag(holder);
        }

        holder.tvDesc.setText(recentItem.description);
        holder.tvTitle.setText(recentItem.title);
        holder.tvViews.setText(valueOf(recentItem.views));

        holder.tvDiscount.setVisibility(recentItem.isBusiness ? View.GONE : View.VISIBLE);
        holder.ivTypeIcon.setImageResource(recentItem.isBusiness ? R.drawable.ic_business : R.drawable.ic_deal_tag);

        if(recentItem.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvDiscount.setText(String.valueOf(recentItem.discount) + getContext().getString(R.string.percentage_off));

        holder.ratingBar.setRating(recentItem.rating);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.selectedDeal = null;
                BusinessDetailActivity.selectedBusiness = null;
                showDetailActivity(recentItem);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recentItem.isBusiness) {
                    BusinessDetailActivity.shareBusiness(mActivity, recentItem.id);
                } else {
                    DealDetailActivity.shareDeal(mActivity, recentItem.id);
                }
            }
        });

        recentItem.isFavorite = Favorite.isFavorite(recentItem.id);

        holder.ivFav.setSelected(recentItem.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentItem.isFavorite = !recentItem.isFavorite;
                v.setSelected(recentItem.isFavorite);
                Favorite favorite = new Favorite(recentItem);
                Favorite.updateFavorite(favorite);
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void showDetailActivity(RecentItem recentItem) {
        Intent intent = new Intent();
        if(recentItem.isBusiness) {
            intent.setClass(mActivity, BusinessDetailActivity.class);
        } else {
            intent.setClass(mActivity, DealDetailActivity.class);
        }
        intent.putExtra(AppConstant.ID, recentItem.id);
        mActivity.startActivity(intent);
    }

    private static class Holder {
        View detail;
        RatingBar ratingBar;
        ImageView ivTypeIcon, ivShare, ivFav;
        TextView tvTitle, tvDesc, tvDiscount, tvViews;
    }
}