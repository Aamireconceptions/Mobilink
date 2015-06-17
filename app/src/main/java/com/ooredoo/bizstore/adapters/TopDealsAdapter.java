package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.List;

public class TopDealsAdapter extends ArrayAdapter<Deal> {

    HomeActivity mActivity;
    int layoutResID;
    List<Deal> items;

    public TopDealsAdapter(HomeActivity activity, int layoutResourceID, List<Deal> items) {
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
