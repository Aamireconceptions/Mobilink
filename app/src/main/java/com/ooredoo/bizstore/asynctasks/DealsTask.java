package com.ooredoo.bizstore.asynctasks;

import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babar on 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, String>
{
    private ListViewBaseAdapter adapter;

    private final static String SERVICE_URL = "";

    public DealsTask(ListViewBaseAdapter adapter)
    {
        this.adapter = adapter;
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

        if(result != null)
        {
            List<GenericDeal> deals;

            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            deals = response.deals;

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

        return result;
    }





}
