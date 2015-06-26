package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class PromoTask extends BaseAsyncTask<String, Void, String>
{
    private FeaturedStatePagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_URL = "";

    public PromoTask(FeaturedStatePagerAdapter adapter, ViewPager viewPager)
    {
        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getPromos();
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

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Logger.print("PromoAsyncTask: Failed to download Banners due to no internet");
        }
    }

    private String getPromos() throws IOException
    {
        String result = null;

        InputStream inputStream = null;

        try
        {
            URL url = new URL(BASE_URL + SERVICE_URL);

            HttpURLConnection connection = openConnectionAndConnect(url);

            inputStream = connection.getInputStream();

            result = readStream(inputStream);

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