package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.List;

public class RecentSearchesAdapter extends ArrayAdapter<SearchItem> {

    Activity mActivity;
    int layoutResID;
    List<SearchItem> items;

    public static HomeActivity homeActivity;

    private int prevItem = -1;

    private boolean showResultCount = true;
    public RecentSearchesAdapter(Activity activity, int layoutResourceID, List<SearchItem> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
    }

    public void setShowResultCount(boolean showResultCount) {
        this.showResultCount = showResultCount;
    }

    public void setData(List<SearchItem> searchItems) {
        this.items = searchItems;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SearchItem item = this.items.get(position);

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

            holder.tvKeyword = (TextView) view.findViewById(R.id.tv_keyword);
            holder.tvResultCount = (TextView) view.findViewById(R.id.tv_result_count);

            view.setTag(holder);
        }

        String results = item.resultCount + " Results Found";

        holder.tvKeyword.setText(item.keyword);
        holder.tvResultCount.setText(results);

        if(showResultCount) {
            holder.tvResultCount.setVisibility(View.VISIBLE);
            holder.tvResultCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search(item);
                }
            });
        } else {
            holder.tvResultCount.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(item);
            }
        });

        //AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    public void search(SearchItem item) {
        homeActivity.executeSearchTask(item.keyword);
    }

    private static class Holder {
        TextView tvKeyword, tvResultCount;
    }
}