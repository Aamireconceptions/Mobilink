package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.model.MallResponse;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class TopMallsTask extends BaseAsyncTask<String, Void, String> {
    private HomeActivity activity;

    private TopMallsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_NAME = "/topbrand?";

    public TopMallsTask(HomeActivity activity, TopMallsStatePagerAdapter adapter, ViewPager viewPager) {
        this.activity = activity;

        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopMalls(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        activity.onRefreshCompleted();

        adapter.clear();

        if(result != null) {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            MallResponse mallResponse = gson.fromJson(result, MallResponse.class);

            if(mallResponse.resultCode != - 1)
            {
                adapter.setData(mallResponse.malls);
            }

            if(BizStore.getLanguage().equals("ar"))
            {
                adapter.notifyDataSetChanged();

                viewPager.setCurrentItem(mallResponse.malls.size() - 1);
            }

        } else {
            Logger.print("TopMallsAsyncTask: Failed to download Banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getTopMalls(String category) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(CATEGORY, category);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getTopMalls() URL:"+ url.toString());

        result = getJson(url);

        Logger.print("getTopMalls:" + result);

        return result;
    }
}