package com.ooredoo.bizstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.SearchTask;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.List;

public class SearchSuggestionsAdapter extends ArrayAdapter<String> {

    HomeActivity mActivity;
    int layoutResID;
    List<String> suggestions;

    public SearchSuggestionsAdapter(HomeActivity activity, int layoutResourceID, List<String> suggestions) {
        super(activity, layoutResourceID, suggestions);
        this.mActivity = activity;
        this.suggestions = suggestions;
        this.layoutResID = layoutResourceID;
    }

    public void setData(List<String> suggestions) {
        this.suggestions = suggestions;
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

        final String item = this.suggestions.get(position);
        final TextView textView = (TextView) convertView.findViewById(R.id.title);
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
        new SearchTask(mActivity).execute(suggestion);
    }
}