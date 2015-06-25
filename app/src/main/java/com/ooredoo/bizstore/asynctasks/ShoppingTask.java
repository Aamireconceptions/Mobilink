package com.ooredoo.bizstore.asynctasks;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class ShoppingTask extends BaseAsyncTask<String, Void, String>
{
    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private final static String SERVICE_URL = "";

    public ShoppingTask(GridViewBaseAdapter adapter, ProgressBar progressBar,
                        SnackBarUtils snackBarUtils)
    {
        this.adapter = adapter;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;
    }


    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getDeals();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        this.progressBar.setVisibility(View.GONE);

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            List<GenericDeal> deals = response.shoppingList;

            for(GenericDeal deal : deals)
            {
                Logger.print("Title: "+deal.title);
            }

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        }
        else
        {
            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getDeals() throws IOException
    {
        String result = null;

        InputStream inputStream = null;

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(ID, "0");

            String query = createQuery(params);

            URL url = new URL(BASE_URL + SERVICE_URL + query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setRequestMethod(METHOD);
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.connect();

            inputStream = conn.getInputStream();

            result = readStream(inputStream);

            Logger.print("Shopping getDeals: "+result);
        }
        finally
        {
            if(inputStream != null)
            {
                inputStream.close();
            }

        }

        return result;
    }
}