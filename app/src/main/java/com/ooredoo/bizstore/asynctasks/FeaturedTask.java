package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class FeaturedTask extends BaseAsyncTask<String, Void, String> {
    private FeaturedStatePagerAdapter adapter;

    private ViewPager viewPager;

    public FeaturedTask(FeaturedStatePagerAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getFeatured();
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

            Response response = gson.fromJson(result, Response.class);

            List<GenericDeal> dealList = response.deals;

            for(GenericDeal genericDeal : dealList) {
                Logger.print("onPost:" + genericDeal.image.bannerUrl);
            }

            adapter.setData(dealList);
            adapter.notifyDataSetChanged();
        } else {
            Logger.print("FeaturedAsyncTask: Failed to download banners due to no internet");
        }
    }

    private String getFeatured() throws IOException {

        setServiceUrl("featureddeals", new HashMap<String, String>());

        String result = getJson();

        Logger.logI("FEATURED_DEALS", result);

        return result;
    }
}