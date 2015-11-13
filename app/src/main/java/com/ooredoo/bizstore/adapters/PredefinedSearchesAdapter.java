package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.KeywordSearch;
import com.ooredoo.bizstore.model.Results;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.AnimUtils;

import java.util.List;

public class PredefinedSearchesAdapter extends ArrayAdapter<KeywordSearch> {

    HomeActivity mActivity;
    int layoutResID;

    List<KeywordSearch> predefinedSearches;

    public PredefinedSearchesAdapter(HomeActivity activity, int layoutResourceID, List<KeywordSearch> predefinedSearches) {
        super(activity, layoutResourceID, predefinedSearches);
        this.mActivity = activity;
        this.predefinedSearches = predefinedSearches;
        this.layoutResID = layoutResourceID;
    }

    public void setData(List<KeywordSearch> predefinedSearches) {
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

        final KeywordSearch keywordSearch = this.predefinedSearches.get(position);
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickListener(keywordSearch);
            }
        });
        textView.setText(keywordSearch.title);

        AnimUtils.slideView(mActivity, convertView, true);

        return convertView;
    }

    protected void setClickListener(KeywordSearch keywordSearch) {
        Results results = new Results();
        results.list = keywordSearch.results;
        AppData.searchResults = results;
        //mActivity.selectDealsAndBusiness();
        mActivity.setupSearchResults(keywordSearch.title, keywordSearch.results, true);
    }
}