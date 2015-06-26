package com.ooredoo.bizstore.asynctasks;

import android.os.AsyncTask;

import com.ooredoo.bizstore.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    private final static String ENCODING = "UTF-8";

    private final static String AMPERSAND = "&";

    private final static String EQUAL = "=";

    public final static String BASE_URL = "";

    public final static int CONNECTION_TIME_OUT = 15 * 1000;

    public final static int  READ_TIME_OUT = 10 * 1000;

    public final static String METHOD = "GET";

    public final static String ID = "id";

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
                stringBuilder.append(AMPERSAND);
            }

            stringBuilder.append(URLEncoder.encode(entry.getKey(), ENCODING));
            stringBuilder.append(EQUAL);
            stringBuilder.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }

        Logger.print("URL Query: "+stringBuilder.toString());

        return stringBuilder.toString();
    }

    public String readStream(InputStream stream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, ENCODING));

            String line = "";

            while((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        }
        finally
        {
            Logger.print("BaseAsync Entered Finally");

            if(reader != null)
            {
                reader.close();

                Logger.print("BaseAsync Closing Reader");
            }
        }
    }

    public HttpURLConnection openConnectionAndConnect(URL url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECTION_TIME_OUT);
        connection.setReadTimeout(READ_TIME_OUT);
        connection.setRequestMethod(METHOD);
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }
}