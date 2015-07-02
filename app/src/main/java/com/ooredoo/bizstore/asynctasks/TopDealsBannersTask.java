package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class TopDealsBannersTask extends BaseAsyncTask<String, Void, String>
{
    private TopDealsPagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_NAME = "/featureddeals?";

    public TopDealsBannersTask(TopDealsPagerAdapter adapter, ViewPager viewPager)
    {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getTopDealsBanners();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        if(result != null)
        {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            List<GenericDeal> deals = response.deals;

            for(GenericDeal genericDeal : deals)
            {
                Logger.print("onPost:"+genericDeal.image.bannerUrl);
            }

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Logger.print("TopDealsBannersTask: Failed to download banners due to no internet");
        }
    }

    private String getTopDealsBanners() throws IOException
    {
        String result;

        InputStream inputStream = null;

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getTopDealsBanners URL:"+ url.toString());

            result = getJson(url);

            Logger.print("getTopDealsBanners result: "+result);

            return result;

        }
        finally
        {
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
    }
}