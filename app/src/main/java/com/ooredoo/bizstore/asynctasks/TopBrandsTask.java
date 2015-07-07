package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.BrandResponse;
import com.ooredoo.bizstore.model.Response;
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
public class TopBrandsTask extends BaseAsyncTask<String, Void, String> {
    private TopBrandsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_NAME = "/topbrand?";

    public TopBrandsTask(TopBrandsStatePagerAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopBrands(params[0]);
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

            BrandResponse brand = gson.fromJson(result, BrandResponse.class);

            if(brand.resultCode != - 1)
            {
                adapter.setData(brand.brands);
                adapter.notifyDataSetChanged();
            }

        } else {
            Logger.print("TopBrandsAsyncTask: Failed to download Banners due to no internet");
        }
    }

    private String getTopBrands(String category) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(CATEGORY, category);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getTopBrands() URL:"+ url.toString());

        result = getJson(url);

        Logger.print("getTopBrands:" + result);

        return result;
    }
}