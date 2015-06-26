package com.ooredoo.bizstore.adapters;

import android.util.Log;
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

import static com.ooredoo.bizstore.AppConstant.BUSINESS;
import static com.ooredoo.bizstore.AppConstant.DEAL;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.SEARCH_BUSINESS;
import static com.ooredoo.bizstore.AppConstant.SEARCH_DEALS;
import static com.ooredoo.bizstore.AppConstant.SEARCH_DEALS_AND_BUSINESS;

public class SearchResultsAdapter extends ArrayAdapter<Deal> {

    public static int searchType = 0;
    HomeActivity mActivity;
    int layoutResID;
    List<Deal> results;

    public SearchResultsAdapter(HomeActivity activity, int layoutResourceID, List<Deal> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.results = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = mActivity.getLayoutInflater();

        final Deal item = this.results.get(position);

        final Holder holder;
        View view = convertView;

        if(view == null) {
            holder = new Holder();
        } else {
            holder = (Holder) view.getTag();
        }

        if(view == null) {
            view = inflater.inflate(layoutResID, parent, false);

            holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            holder.ivTypeIcon = (ImageView) view.findViewById(R.id.iv);

            view.setTag(holder);
        }

        holder.tvDiscount.setVisibility(item.type == DEAL ? View.VISIBLE : View.GONE);
        holder.ivTypeIcon.setImageResource(item.type == DEAL ? R.drawable.ic_deal_tag : R.drawable.ic_business);

        if(searchType == SEARCH_DEALS_AND_BUSINESS) {
            view.setVisibility(View.VISIBLE);
        } else if(searchType == SEARCH_BUSINESS && item.type == DEAL) {
            view.setVisibility(View.GONE);
        } else if(searchType == SEARCH_DEALS && item.type == BUSINESS) {
            view.setVisibility(View.GONE);
        }

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ITEM", String.valueOf(item.type));
                if(item.type == DEAL) {
                    mActivity.showDetailActivity(DEAL, DEAL_CATEGORIES[0], 0L); //TODO replace 0L with deal id
                } else if(item.type == BUSINESS) {
                    mActivity.showDetailActivity(BUSINESS, DEAL_CATEGORIES[2], 0L); //TODO replace 0L with business id
                }
            }
        });
        return view;
    }

    private static class Holder {
        ImageView ivTypeIcon;
        TextView tvTitle, tvDesc, tvDiscount;
    }
}
