package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Babar on 22-Jun-15.
 */
public class HomeAsyncTask extends BaseAsyncTask<Void, Void, String>
{
    private Context context;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private final static String SERVICE_URL = "";

    public HomeAsyncTask(Context context, ProgressBar progressBar, SnackBarUtils snackBarUtils)
    {
        this.context = context;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            return getHomeData();
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");

            Gson gson = gsonBuilder.create();

            List<GenericDeal> deals = new ArrayList<>();

            deals = Arrays.asList(gson.fromJson(result, GenericDeal.class));
        }
        else
        {
            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getHomeData() throws IOException
    {
        String result = null;

        InputStream inputStream = null;

        URL url = new URL(BASE_URL + SERVICE_URL);

        HttpURLConnection connection = openConnectionAndConnect(url);

        inputStream = connection.getInputStream();

        result = readStream(inputStream);

        Logger.print("HomeAsyncTask getHomeData(): " + result);

        if(inputStream != null)
        {
            inputStream.close();
        }

        return result;
    }


}
