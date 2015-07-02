package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
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
public class PromoTask extends BaseAsyncTask<String, Void, String> {
    private PromoStatePagerAdapter adapter;

    private ViewPager viewPager;

    public PromoTask(PromoStatePagerAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getPromos();
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

            List<GenericDeal> deals = response.deals;

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        } else {
            Logger.print("PromoAsyncTask: Failed to download Banners due to no internet");
        }
    }

    private String getPromos() throws IOException {

        setServiceUrl("featureddeals", new HashMap<String, String>());

        String result = getJson();

        Logger.logI("PROMO_DEALS", result);

        return result;
    }
}