package com.ooredoo.bizstore.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.StringUtils;

import java.util.List;

import static com.ooredoo.bizstore.AppConstant.BUSINESS;
import static com.ooredoo.bizstore.AppConstant.DEAL;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.PERCENT_OFF;
import static com.ooredoo.bizstore.AppConstant.SEARCH_BUSINESS;
import static com.ooredoo.bizstore.AppConstant.SEARCH_DEALS;
import static com.ooredoo.bizstore.AppConstant.SEARCH_DEALS_AND_BUSINESS;
import static java.lang.String.valueOf;

public class SearchResultsAdapter extends ArrayAdapter<SearchResult> {

    public static int searchType = 0;
    HomeActivity mActivity;
    int layoutResID;
    List<SearchResult> results;

    public SearchResultsAdapter(HomeActivity activity, int layoutResourceID, List<SearchResult> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.results = items;
        this.layoutResID = layoutResourceID;
    }

    public void setData(List<SearchResult> deals) {
        this.results = deals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = mActivity.getLayoutInflater();

        final SearchResult result = this.results.get(position);

        final Holder holder;
        View view = convertView;

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

            holder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

            view.setTag(holder);
        }

        final boolean isBusiness = StringUtils.isNotNullOrEmpty(result.type) && result.type.equalsIgnoreCase("business");
        holder.tvDiscount.setVisibility(isBusiness ? View.GONE : View.VISIBLE);
        holder.ivTypeIcon.setImageResource(isBusiness ? R.drawable.ic_business : R.drawable.ic_deal_tag);

        holder.tvDesc.setText(result.desc);
        holder.tvTitle.setText(result.title);
        holder.tvViews.setText(valueOf(result.views));
        holder.tvDiscount.setText(String.valueOf(result.discount) + PERCENT_OFF);

        holder.ratingBar.setRating(result.rating);

        if(searchType == SEARCH_DEALS_AND_BUSINESS) {
            view.setVisibility(View.VISIBLE);
        } else if(searchType == SEARCH_BUSINESS && !isBusiness) {
            view.setVisibility(View.GONE);
        } else if(searchType == SEARCH_DEALS && isBusiness) {
            view.setVisibility(View.GONE);
        }

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ITEM", String.valueOf(result.type));
                if(isBusiness) {
                    BusinessDetailActivity.selectedBusiness = new Business(result);
                    mActivity.showDetailActivity(BUSINESS, DEAL_CATEGORIES[2], result.id);
                } else {
                    mActivity.showDetailActivity(DEAL, DEAL_CATEGORIES[0], result.id);
                }
            }
        });
        return view;
    }

    private static class Holder {
        View detail;
        RatingBar ratingBar;
        ImageView ivTypeIcon;
        TextView tvTitle, tvDesc, tvDiscount, tvViews;
    }
}
