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
import com.ooredoo.bizstore.utils.AnimUtils;

import java.util.List;

public class RecentSearchesAdapter extends ArrayAdapter<SearchItem> {

    Activity mActivity;
    int layoutResID;
    List<SearchItem> items;

    public static HomeActivity homeActivity;

    private int prevItem = -1;

    public RecentSearchesAdapter(Activity activity, int layoutResourceID, List<SearchItem> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
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

        holder.tvKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.executeSearchTask(item.keyword);
                mActivity.onBackPressed();
            }
        });

        AnimUtils.slideView(mActivity, view, prevItem < position);

        prevItem = position;

        return view;
    }

    private static class Holder {
        TextView tvKeyword, tvResultCount;
    }
}