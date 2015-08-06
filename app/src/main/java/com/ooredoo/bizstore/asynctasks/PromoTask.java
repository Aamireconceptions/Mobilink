package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class PromoTask extends BaseAsyncTask<String, Void, String>
{
    private PromoStatePagerAdapter adapter;

    private ViewPager viewPager;

    private CirclePageIndicator circlePageIndicator;

    private final static String SERVICE_NAME = "/promotionaldeals?";

    public PromoTask(PromoStatePagerAdapter adapter,
                     ViewPager viewPager, CirclePageIndicator circlePageIndicator)
    {
        this.adapter = adapter;

        this.viewPager = viewPager;

        this.circlePageIndicator = circlePageIndicator;
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

        adapter.clear();

        if(result != null)
        {
            Gson gson = new Gson();

            try
            {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    List<GenericDeal> deals;

                    viewPager.setBackground(null);

                    deals = response.deals;

                    handleIndicatorVisibility(deals.size(), circlePageIndicator);

                    adapter.setData(deals);

                    if(BizStore.getLanguage().equals("ar"))
                    {
                        adapter.notifyDataSetChanged();

                        viewPager.setCurrentItem(deals.size() - 1);
                    }
                }
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logger.print("PromoTask: Failed to download banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getPromos() throws IOException
    {
        String result;

        InputStream inputStream = null;

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getPromos Url: "+url.toString());

            HttpURLConnection connection = openConnectionAndConnect(url);

            inputStream = connection.getInputStream();

            result = readStream(inputStream);

            Logger.print("getPromos: "+result);

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