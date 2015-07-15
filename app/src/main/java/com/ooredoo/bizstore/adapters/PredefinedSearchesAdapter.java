package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.AnimUtils;

public class PredefinedSearchesAdapter extends ArrayAdapter<String> {

    HomeActivity mActivity;
    int layoutResID;
    String[] predefinedSearches;

    public PredefinedSearchesAdapter(HomeActivity activity, int layoutResourceID, String[] predefinedSearches) {
        super(activity, layoutResourceID, predefinedSearches);
        this.mActivity = activity;
        this.predefinedSearches = predefinedSearches;
        this.layoutResID = layoutResourceID;
    }

    public void setData(String[] predefinedSearches) {
        this.predefinedSearches = predefinedSearches;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(layoutResID, null);

        final String item = this.predefinedSearches[position];
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickListener(item);
            }
        });
        textView.setText(item);

        AnimUtils.slideView(mActivity, convertView, true);

        return convertView;
    }

    protected void setClickListener(String keyword) {
        mActivity.executeSearchTask(keyword);
    }
}