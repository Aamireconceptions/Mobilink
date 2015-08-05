package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class TopDealsBannersTask extends BaseAsyncTask<String, Void, String>
{
    private HomeActivity mActivity;

    private TopDealsPagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_NAME = "/featureddeals?";

    public TopDealsBannersTask(HomeActivity mActivity, TopDealsPagerAdapter adapter, ViewPager viewPager)
    {
        this.mActivity = mActivity;

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

       // mActivity.onRefreshCompleted();

        adapter.clear();

        if(result != null)
        {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if(response.resultCode != -1)
            {
                List<GenericDeal> deals = new ArrayList<>();

                if(response.deals != null) {
                    deals = response.deals;
                }

                for(GenericDeal genericDeal : deals)
                {
                    Logger.print("onPost:"+genericDeal.image.bannerUrl);
                }

                adapter.setData(deals);


                if(BizStore.getLanguage().equals("ar"))
                {
                    adapter.notifyDataSetChanged();

                    viewPager.setCurrentItem(deals.size() - 1);
                }
            }
        }
        else
        {
            Logger.print("TopDealsBannersTask: Failed to download banners due to no internet");
        }

        adapter.notifyDataSetChanged();
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