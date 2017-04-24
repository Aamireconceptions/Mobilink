package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.model.SearchSuggestions;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ooredoo.bizstore.AppData.searchSuggestions;

/**
 * @author Pehlaj Rai
 * @since 13-Nov-15
 */
public class SearchSuggestionsTask extends BaseAsyncTask<Void, Void, String> {

    private HomeActivity mActivity;

    private String keyword;

    public boolean hasFinished;

    public SearchSuggestionsTask(HomeActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //dialog = createCustomLoader(mActivity, "Searching...");
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
                AppData.searchSuggestions = new Gson().fromJson(result, SearchSuggestions.class);

                if(AppData.searchSuggestions.list == null)
                    AppData.searchSuggestions.list = new ArrayList<>();

                Logger.logI("AUTO_SUGGESTION_COUNT", String.valueOf(AppData.searchSuggestions.list.size()));

                if(searchSuggestions != null && searchSuggestions.list != null) {
                    mActivity.setSearchSuggestions(searchSuggestions.list);
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        hasFinished = true;
    }

    private String getSearchSuggestions() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        setServiceUrl("autosuggestions", params);

        result = getJson();

        Logger.print("autosuggestions:" + result);

        return result;
    }
}