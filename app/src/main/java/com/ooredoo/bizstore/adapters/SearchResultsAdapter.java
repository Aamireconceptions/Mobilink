package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ooredoo.bizstore.ui.activities.HomeActivity;

public class SearchResultsAdapter extends ArrayAdapter<String> {

    HomeActivity mActivity;
    int layoutResID;
    String[] items;

    public SearchResultsAdapter(HomeActivity activity, int layoutResourceID, String[] items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(layoutResID, null);

        return convertView;
    }

}
