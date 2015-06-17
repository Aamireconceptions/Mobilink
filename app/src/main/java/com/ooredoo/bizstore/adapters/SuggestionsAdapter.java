package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

public class SuggestionsAdapter extends ArrayAdapter<String> {

    public static String[] suggestions = { "ooredoo", "ooredoo qatar", "ooredoo property portal", "ooredoo foodcourt", "ooredoo bizstore" };

    HomeActivity mActivity;
    int layoutResID;
    String[] items;

    public SuggestionsAdapter(HomeActivity activity, int layoutResourceID, String[] items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(layoutResID, null);

        final String item = this.items[position];
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickListener(item);
            }
        });
        textView.setText(item);
        return convertView;
    }

    protected void setClickListener(String suggestion) {
        Logger.logI("SUGGESTION", suggestion);
        TextView tvSearchResults = (TextView) mActivity.searchPopup.getContentView().findViewById(R.id.tv_search_results);
        tvSearchResults.setText(suggestions.length + " - SEARCH RESULTS");
        tvSearchResults.setVisibility(View.VISIBLE);
        SearchResultsAdapter adapter = new SearchResultsAdapter(mActivity, R.layout.search_result_item, suggestions);
        mActivity.mSearchResultsListView.setAdapter(adapter);
        mActivity.mSuggestionsListView.setVisibility(View.GONE);
        mActivity.mSearchResultsListView.setVisibility(View.VISIBLE);
    }
}