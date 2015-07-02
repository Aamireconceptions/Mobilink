package com.ooredoo.bizstore.asynctasks;

import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.SearchResultsAdapter;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 01-Jul-15
 */
public class SearchTask extends BaseAsyncTask<String, Void, String> {

    private HomeActivity mActivity;

    private String keyword;

    public SearchTask(HomeActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        keyword = params[0];
        try {
            return search(keyword);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null) {
            try {
                //TODO Results results = new Gson().fromJson(result, Results.class);

                List<Deal> deals = new ArrayList<>();//results.list;
                for(Deal deal : deals) {
                    Logger.print("Search Result: " + deal.title);
                }

                View searchDropDown = mActivity.searchPopup.getContentView();
                searchDropDown.findViewById(R.id.divider).setVisibility(View.GONE);
                TextView tvSearchResults = (TextView) searchDropDown.findViewById(R.id.tv_search_results);
                tvSearchResults.setText("Showing " + deals.size() + " Results");
                new SearchItem(0, keyword, deals.size()).save();
                tvSearchResults.setVisibility(View.VISIBLE);
                ((View) tvSearchResults.getParent()).setVisibility(View.VISIBLE);
                SearchResultsAdapter adapter = new SearchResultsAdapter(mActivity, R.layout.search_result_item, deals);
                mActivity.mSearchResultsListView.setAdapter(adapter);
                mActivity.mSuggestionsListView.setVisibility(View.GONE);
                mActivity.mSearchResultsListView.setVisibility(View.VISIBLE);
                adapter.setData(deals);
                adapter.notifyDataSetChanged();
            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private String search(String keyword) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", keyword);

        setServiceUrl("search", params);

        result = getJson();

        Logger.print("search:" + result);

        return result;
    }

}
