package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
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

    public TopBrandsTask(TopBrandsStatePagerAdapter adapter, ViewPager viewPager) {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopBrands();
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

            List<Brand> brands = new ArrayList<>();//TODO response.brands;
            brands.add(new Brand());
            brands.add(new Brand());
            brands.add(new Brand());
            brands.add(new Brand());
            brands.add(new Brand());

            adapter.setData(brands);
            adapter.notifyDataSetChanged();
        } else {
            Logger.print("TopBrandsAsyncTask: Failed to download Banners due to no internet");
        }
    }

    private String getTopBrands() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put("category", "top_brands");

        setServiceUrl("deals", params);

        result = getJson();

        Logger.print("getDeals:" + result);

        return result;
    }
}