package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.KeywordSearch;
import com.ooredoo.bizstore.model.Results;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.List;

/**
 * @author Pehlaj
 * @since 12-Nov-15
 */
public class PopularSearchesGridAdapter extends BaseAdapter {
    private Context context;

    private int layoutResId;

    public List<KeywordSearch> list;

    private LayoutInflater inflater;

    private Holder holder;

    public PopularSearchesGridAdapter(Context context, int layoutResId, List<KeywordSearch> list) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid = convertView;

        final KeywordSearch search = (KeywordSearch) getItem(position);

        if(grid == null) {
            grid = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvTitle = (TextView) grid.findViewById(R.id.title);
            grid.setTag(holder);
        } else {
            holder = (Holder) grid.getTag();
        }

        holder.tvTitle.setText(search.title);
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickListener(search);
            }
        });

        return grid;
    }

    protected void setClickListener(KeywordSearch keywordSearch) {
        Results results = new Results();
        results.list = keywordSearch.results;
        AppData.searchResults = results;
        HomeActivity homeActivity = (HomeActivity) context;
        homeActivity.setupSearchResults(keywordSearch.title, keywordSearch.results, true);

      /*  HomeActivity homeActivity = (HomeActivity) context;
        homeActivity.performSearch(keywordSearch.title);*/
    }

    private static class Holder {
        TextView tvTitle;
    }
}