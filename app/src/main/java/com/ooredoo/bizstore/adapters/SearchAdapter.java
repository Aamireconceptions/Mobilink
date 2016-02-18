package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Deal;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 01-Jul-15.
 */
public class SearchAdapter extends android.widget.ArrayAdapter {
    private Context context;

    private int layoutResId;

    private List<Deal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    public SearchAdapter(Context context, int layoutResId, List<Deal> deals) {
        super(context, layoutResId, deals);
        this.context = context;
        this.layoutResId = layoutResId;
        this.deals = deals;
    }

    public void setData(List<Deal> deals) {
        this.deals = deals;
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Deal getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deals.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null) {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvCategory = (TextView) row.findViewById(R.id.category_icon);
            holder.ivFav = (ImageView) row.findViewById(R.id.fav);
            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        holder.ivFav.setSelected(getItem(position).isFavorite);
        holder.ivFav.setOnClickListener(new FavouriteOnClickListener(position));

        holder.tvTitle.setText(getItem(position).title);

        holder.tvDetail.setText(getItem(position).description);

        holder.tvDiscount.setText(getItem(position).discount);

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

            getItem(position).isFavorite = !isSelected;
        }
    }

    private static class Holder {
        ImageView ivFav;

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvViews;

        RatingBar rbRatings;
    }
}
