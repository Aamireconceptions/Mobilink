package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Babar on 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, List<GenericDeal>>
{
    private ListViewBaseAdapter adapter;

    public DealsTask(ListViewBaseAdapter adapter)
    {
        this.adapter = adapter;
    }

    @Override
    protected List<GenericDeal> doInBackground(String... params)
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
    protected void onPostExecute(List<GenericDeal> result) {
        super.onPostExecute(result);

        if(result != null) {
            adapter.setData(result);
            adapter.notifyDataSetChanged();
        }
    }

    private List<GenericDeal> getDeals(String type) throws IOException {
        List<NameValuePair> params = BizStore.getUserCredentials();

        params.add(new BasicNameValuePair("type", type));

        setServiceUrl("deals", params);

        URL url = new URL(serviceUrl);

        HttpURLConnection connection = openConnectionAndConnect(url);

        InputStream inputStream = connection.getInputStream();

        Reader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();

        List<GenericDeal> deals = Arrays.asList(gson.fromJson(reader, GenericDeal[].class));

        return deals;
    }

}
