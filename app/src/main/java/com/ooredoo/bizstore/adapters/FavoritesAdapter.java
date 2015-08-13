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
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimUtils;

import java.util.List;

import static java.lang.String.valueOf;

public class FavoritesAdapter extends ArrayAdapter<Favorite> {

    Activity mActivity;
    int layoutResID;
    List<Favorite> favorites;
    private int prevItem = -1;

    public FavoritesAdapter(Activity activity, int layoutResourceID, List<Favorite> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.favorites = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Favorite favorite = this.favorites.get(position);

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

        /*String category = favorite.category;
        holder.tvCategory.setText(category);
        int categoryIcon = ResourceUtils.getDrawableResId(favorite.category);
        if(categoryIcon > 0) {
            holder.ivCategory.setImageResource(categoryIcon);
        }*/

        holder.tvDesc.setText(favorite.description);
        holder.tvTitle.setText(favorite.title);
        holder.tvViews.setText(valueOf(favorite.views));

        holder.tvDiscount.setVisibility(favorite.isBusiness ? View.GONE : View.VISIBLE);
        holder.ivTypeIcon.setImageResource(favorite.isBusiness ? R.drawable.ic_business : R.drawable.ic_deal_tag);

        if(favorite.discount == 0) {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        holder.tvDiscount.setText(String.valueOf(favorite.discount) + getContext().getString(R.string.percentage_off));

        holder.ratingBar.setRating(favorite.rating);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealDetailActivity.selectedDeal = null;
                BusinessDetailActivity.selectedBusiness = null;


                showDetailActivity(favorite);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite.isBusiness) {
                    BusinessDetailActivity.shareBusiness(mActivity, favorite.id);
                } else {
                    DealDetailActivity.shareDeal(mActivity, favorite.id);
                }
            }
        });

        favorite.isFavorite = true; //Deal.isFavorite(favorite.id);

        holder.ivFav.setSelected(favorite.isFavorite);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite.isFavorite = false;
                favorite.save();
                favorites.remove(favorite);
                notifyDataSetChanged();
                ((MyFavoritesActivity) mActivity).toggleEmptyView(getCount());
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void showDetailActivity(Favorite favorite) {
        Intent intent = new Intent();
        if(favorite.isBusiness) {
            intent.setClass(mActivity, BusinessDetailActivity.class);
            intent.putExtra("business", new Business(favorite));
        } else {
            intent.setClass(mActivity, DealDetailActivity.class);
            intent.putExtra("generic_deal", new GenericDeal(favorite));
        }
        intent.putExtra(AppConstant.ID, favorite.id);
        mActivity.startActivity(intent);
        RecentViewedActivity.addToRecentViewed(favorite);
    }

    private static class Holder {
        View detail;
        RatingBar ratingBar;
        ImageView ivTypeIcon, ivShare, ivFav;
        TextView tvTitle, tvDesc, tvDiscount, tvViews;
    }
}