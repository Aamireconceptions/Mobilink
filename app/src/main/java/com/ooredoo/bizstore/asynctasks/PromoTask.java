package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.gson.Gson;
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
    private HomeActivity activity;

    private PromoStatePagerAdapter adapter;

    private ViewPager viewPager;

    private final static String SERVICE_NAME = "/promotionaldeals?";

    CirclePageIndicator circlePageIndicator;

    public PromoTask(HomeActivity activity, PromoStatePagerAdapter adapter, ViewPager viewPager)
    {
        this.activity = activity;

        this.adapter = adapter;

        this.viewPager = viewPager;
    }

    public void setIndicator(CirclePageIndicator circlePageIndicator) {
        this.circlePageIndicator = circlePageIndicator;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            Logger.print("doInBackground: PromoTask");
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

        activity.onRefreshCompleted();

        adapter.clear();

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            List<GenericDeal> deals = new ArrayList<>();

            if(response.deals != null)
            {
                viewPager.setBackground(null);

                deals = response.deals;
            }

            if(deals.size() > 1) {
                circlePageIndicator.setVisibility(View.VISIBLE);
            } else {
                circlePageIndicator.setVisibility(View.GONE);
            }

            for(GenericDeal genericDeal : deals)
            {
                Logger.print("PromoTask :"+genericDeal.image.promotionalUrl);
            }

            adapter.setData(deals);

            if(BizStore.getLanguage().equals("ar"))
            {
                adapter.notifyDataSetChanged();

                viewPager.setCurrentItem(deals.size() - 1);
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
        String result = null;

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