package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.List;

public class SportsAdapter extends ArrayAdapter<Deal> {

    HomeActivity mActivity;
    int layoutResID;
    List<Deal> items;

    public SportsAdapter(HomeActivity activity, int layoutResourceID, List<Deal> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Deal deal = this.items.get(position);

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

        //TODO un-comment after implementing web services
        /*holder.tvDesc.setText(deal.desc);
        holder.tvTitle.setText(deal.title);
        holder.tvViews.setText(deal.views);
        holder.tvDiscount.setText(deal.discount);*/

        ImageView ivFav = (ImageView) view.findViewById(R.id.iv_fav);

        updateFavoriteIcon(deal.isFavorite, ivFav);

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.isFavorite = !deal.isFavorite;
                updateFavoriteIcon(deal.isFavorite, holder.ivFav);
            }
        });
        return view;
    }

    public static void updateFavoriteIcon(boolean isFavorite, ImageView ivFav) {
        int favResId = R.drawable.ic_like_inactive;
        if(isFavorite) {
            favResId = R.drawable.ic_like_active;
        }
        ivFav.setImageResource(favResId);
    }

    private static class Holder {
        ImageView ivFav, ivShare;
        TextView tvTitle, tvViews, tvDesc, tvDiscount;
    }
}