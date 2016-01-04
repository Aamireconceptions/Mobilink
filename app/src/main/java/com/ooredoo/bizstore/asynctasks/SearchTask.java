package com.ooredoo.bizstore.asynctasks;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Results;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.BaseFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.searchType;
import static com.ooredoo.bizstore.utils.DialogUtils.createCustomLoader;

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
        dialog = createCustomLoader(mActivity, mActivity.getString(R.string.searching));
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
        closeDialog(dialog);

        if(result != null) {
            try {
                searchResults = new Gson().fromJson(result, Results.class);

                if(searchResults.list == null)
                    searchResults.list = new ArrayList<>();

                Logger.logI("TYPE", searchType);

                Logger.logI("COUNT", String.valueOf(searchResults.list.size()));

                List<SearchResult> results = new ArrayList<>();
                if(searchResults.list.size() > 0) {
                    if(searchType.equalsIgnoreCase("business")) {
                        results = HomeActivity.getBusinesses();
                    } else {
                        results = HomeActivity.getDeals();
                    }
                }

                if(searchResults.list.size() > 0) {
                     SearchItem searchItem = new SearchItem(0, keyword, results.size());
                    SearchItem.addToRecentSearches(searchItem);

                    mActivity.setupSearchResults(keyword, results, false);
                    BaseFragment.hideKeyboard(mActivity);
                } else {
                    HomeActivity.isShowResults = false;
                    mActivity.acSearch.setText("");
                    mActivity.acSearch.setHint(R.string.search);
                    Toast.makeText(mActivity, R.string.error_no_search_item_found, LENGTH_SHORT).show();
                    mActivity.hideSearchResults();
                    mActivity.hideSearchPopup();
                    mActivity.showHideSearchBar(false);
                    mActivity.showHideSearchBar(true);
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();

                Toast.makeText(mActivity, R.string.error_server_down, LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(mActivity, R.string.error_no_internet, LENGTH_SHORT).show();
        }
    }

    private String search(String keyword) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("keyword", keyword);

        setServiceUrl("search", params);

        result = getJson();

        Logger.print("search:" + result);

        return result;
    }
}