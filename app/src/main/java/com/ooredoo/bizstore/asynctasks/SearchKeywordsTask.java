package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.model.PopularSearches;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 01-Jul-15
 */
public class SearchKeywordsTask extends BaseAsyncTask<Void, Void, String> {

    private HomeActivity mActivity;

    public SearchKeywordsTask(HomeActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return getPredefinedSearches();
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
                AppData.popularSearches = new Gson().fromJson(result, PopularSearches.class);

                if(AppData.popularSearches != null && AppData.popularSearches.list == null)
                    AppData.popularSearches.list = new ArrayList<>();

                Logger.logI("POPULAR_SEARCH_COUNT", String.valueOf(AppData.popularSearches.list.size()));

                if(AppData.popularSearches != null && AppData.popularSearches.list != null) {
                    mActivity.setPopularSearches(AppData.popularSearches.list);
                }
            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPredefinedSearches() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        setServiceUrl("getpredefinedsearches", params);

        result = getJson();

        Logger.print("Search Suggestions:" + result);

        return result;
    }

}
