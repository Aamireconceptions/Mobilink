package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class TopMallsTask extends BaseAsyncTask<String, Void, String> {
    private TopMallsStatePagerAdapter adapter;

    private ViewPager viewPager;

    public TopMallsTask(TopMallsStatePagerAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopMalls();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null) {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            //TODO remove comment Response response = gson.fromJson(result, Response.class);

            List<Mall> malls = new ArrayList<>();//TODO response.malls;
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());

            adapter.setData(malls);
            adapter.notifyDataSetChanged();
        } else {
            Logger.print("TopBrandsAsyncTask: Failed to download Banners due to no internet");
        }
    }

    private String getTopMalls() throws IOException {
        String result = null;

        HashMap<String, String> params = new HashMap<>();
        params.put("category", "top_malls");

        setServiceUrl("deals", params);

        //result = getJson();

        Logger.print("getDeals:" + result);

        return result;
    }
}