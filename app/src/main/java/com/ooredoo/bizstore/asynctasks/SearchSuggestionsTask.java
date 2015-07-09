package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.model.Suggestions;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 01-Jul-15
 */
public class SearchSuggestionsTask extends BaseAsyncTask<Void, Void, String> {

    private HomeActivity mActivity;

    public SearchSuggestionsTask(HomeActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return getSearchSuggestions();
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
                AppData.searchSuggestions = new Gson().fromJson(result, Suggestions.class);

                if(AppData.searchSuggestions.list == null)
                    AppData.searchSuggestions.list = new String[] {};

                Logger.logI("SUGGESTIONS_COUNT", String.valueOf(AppData.searchSuggestions.list.length));

                mActivity.setSuggestions();
            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSearchSuggestions() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        setServiceUrl("getsearchsuggestions", params);

        result = getJson();

        Logger.print("Search Suggestions:" + result);

        return result;
    }

}
