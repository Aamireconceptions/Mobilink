package com.ooredoo.bizstore.asynctasks;

import android.os.AsyncTask;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Babar on 29-Aug-16.
 */
public class LocationUpdateTask extends AsyncTask<Double, Void, Void>
{
    private final static String SERVICE_NAME = "updateLoc";

    @Override
    protected Void doInBackground(Double... params)
    {
        try
        {
            updateLocation(params[0], params[1]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void updateLocation(double latitude, double longitude) throws IOException
    {
        HttpURLConnection connection = null;

        BufferedReader reader = null;

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(BaseAsyncTask.OS, BaseAsyncTask.ANDROID);
            params.put(BaseAsyncTask.MSISDN, BizStore.username);
            params.put("lat", String.valueOf(latitude));
            params.put("lng", String.valueOf(longitude));

            String query = createQuery(params);

            URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(BaseAsyncTask.CONNECTION_TIME_OUT);
            connection.setReadTimeout(BaseAsyncTask.READ_TIME_OUT);
            connection.connect();

            InputStream is = connection.getInputStream();

            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(is, BaseAsyncTask.ENCODING));

            String line;
            while((line = reader.readLine()) != null)
            {
                builder.append(line);
            }

            Logger.print("updateLoc response: " +builder.toString() );

        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }

            if(reader != null)
            {
                reader.close();
            }
        }
    }

    public String createQuery(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean isFirst = true;

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(isFirst)
            {
                isFirst = false;
            }
            else
            {
                stringBuilder.append(BaseAsyncTask.AMPERSAND);
            }

            stringBuilder.append(URLEncoder.encode(entry.getKey(), BaseAsyncTask.ENCODING));
            stringBuilder.append(BaseAsyncTask.EQUAL);
            stringBuilder.append(URLEncoder.encode(entry.getValue(), BaseAsyncTask.ENCODING));
        }

        Logger.print("URL Query: " + stringBuilder.toString());

        return stringBuilder.toString();
    }
}