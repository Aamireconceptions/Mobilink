package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class TopMallsTask extends BaseAsyncTask<String, Void, String>
{
    private TopMallsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_URL = "";

    public TopMallsTask(TopMallsStatePagerAdapter adapter, ViewPager viewPager)
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

            List<Mall> malls = new ArrayList<>();//response.malls;
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());
            malls.add(new Mall());

            adapter.setData(malls);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Logger.print("TopBrandsAsyncTask: Failed to download Banners due to no internet");
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