package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.ViewedRatedAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.DODResponse;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * Created by Babar on 13-Jan-16.
 */
public class ViewedRatedTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private ViewedRatedAdapter adapter;

    private String category;

    private HomeFragment homeFragment;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;

    private static final String SERVICE_NAME  = "/viewednrated?";

    public ViewedRatedTask(Context context, ViewedRatedAdapter adapter, Fragment fragment)
    {
        this.context = context;

        this.adapter = adapter;

        this.homeFragment = (HomeFragment) fragment;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;
    }

    @Override
    protected String doInBackground(String... params)
    {
            try {
                category = params[0];

                return getViewedRated();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        setData(s);
    }

    public void setData(String result)
    {
        Logger.print("ViewedRated result: "+result);
        dealsTaskFinishedListener.onRefreshCompleted();
        adapter.clear();

        if(result != null) {
            Gson gson = new Gson();

            try {

                DODResponse response = gson.fromJson(result, DODResponse.class);

                if (response.resultCode != -1) {
                    if(response.dods != null  && response.dods.size() > 0)
                    {
                        dealsTaskFinishedListener.onHaveDeals();

                        if(category != null) {
                            final String KEY = PREFIX_DEALS.concat(category);
                            final String UPDATE_KEY = KEY.concat("_UPDATE");

                            updateVal((Activity) context, KEY, result);
                            updateVal((Activity) context, UPDATE_KEY, currentTimeMillis());
                        }
                    }

                    homeFragment.addMostViewedAndTopRated(response.dods);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            dealsTaskFinishedListener.onNoDeals(0);
        }

        adapter.notifyDataSetChanged();
    }

    private String getViewedRated() throws IOException {

            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put("lat", "" + HomeActivity.lat);
            params.put("lng", "" + HomeActivity.lng);
            params.put("nearby", "true");

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            String json = getJson(url);

            return json;
    }

    public String getCache(String category)
    {
        String result = null;

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        Activity activity = (Activity) context;

        String cacheData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, UPDATE_KEY);

        if(!isNullOrEmpty(cacheData) && (!hasInternetConnection(activity) || !updateFromServer))
        {
            result = cacheData;
        }

        return result;
    }
}