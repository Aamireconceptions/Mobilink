package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.model.Results;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.BaseFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.searchType;

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
                AppData.searchResults = new Gson().fromJson(result, Results.class);

                if(AppData.searchResults.list == null)
                    AppData.searchResults.list = new ArrayList<>();

                Logger.logI("TYPE", searchType);

                Logger.logI("COUNT", String.valueOf(AppData.searchResults.list.size()));

                List<SearchResult> results = new ArrayList<>();
                if(AppData.searchResults.list.size() > 0) {
                    if(searchType.equalsIgnoreCase("all")) {
                        results = searchResults.list;
                    } else if(searchType.equalsIgnoreCase("business")) {
                        results = HomeActivity.getBusinesses();
                    } else {
                        results = HomeActivity.getDeals();
                    }
                }

                mActivity.setupSearchResults(keyword, results);

                BaseFragment.hideKeyboard(mActivity);
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
