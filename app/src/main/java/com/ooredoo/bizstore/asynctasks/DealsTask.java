package com.ooredoo.bizstore.asynctasks;

import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Babar on 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, String>
{
    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private final static String SERVICE_URL = "http://10.1.3.38/econ/ooredoo/index.php/api/en/deals/26";

    public DealsTask(ListViewBaseAdapter adapter, ProgressBar progressBar)
    {
        this.adapter = adapter;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getDeals(params[0]);
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

        progressBar.setVisibility(View.GONE);

        if(result != null)
        {
            List<GenericDeal> deals;

            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            deals = response.deals;

            for(GenericDeal genericDeal : deals)
            {
                Logger.print("title:"+genericDeal.title);
            }

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        }
    }

    private String getDeals(String type) throws IOException
    {
        String result = null;

        URL url = new URL(BASE_URL + SERVICE_URL);

        HttpURLConnection connection = openConnectionAndConnect(url);

        InputStream inputStream = connection.getInputStream();

        result = readStream(inputStream);

        Logger.print("getDeals:"+result);

        return result;
    }

}
