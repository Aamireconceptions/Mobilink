package com.ooredoo.bizstore.asynctasks;

import android.widget.ProgressBar;

import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Babar on 25-Jun-15.
 */
public class FeaturedAsyncTask extends BaseAsyncTask<String, Void, String>
{
    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private final static String SERVICE_URL = "";

    public FeaturedAsyncTask(ProgressBar progressBar,
                            SnackBarUtils snackBarUtils)
    {
        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getFeatured();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
    }

    private String getFeatured() throws IOException
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
