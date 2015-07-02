package com.ooredoo.bizstore.asynctasks;

import android.os.AsyncTask;

import com.ooredoo.bizstore.BizStore;
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
    private final static String SLASH = "/";
    private final static String QUESTION_MARK = "?";

    public static String BASE_URL = "";

    public final static int CONNECTION_TIME_OUT = 15 * 1000;

    public final static int  READ_TIME_OUT = 10 * 1000;

    public final static String METHOD = "GET";

    public final static String ID = "id";

    public final static String OS = "os";

    public final static String ANDROID = "android";

    public final static String IMAGE_BASE_URL = "http://10.1.3.10/";

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

    public String getJson() throws IOException {

        String result;

        URL url = new URL(serviceUrl);

        HttpURLConnection connection = openConnectionAndConnect(url);

        InputStream inputStream = connection.getInputStream();

        result = readStream(inputStream);

        return result;
    }

    public String readStream(InputStream stream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, ENCODING));

            String line;

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

                Logger.print("BaseAsyncClosing Reader");
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

    protected String serviceUrl;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setServiceUrl(String serviceName, HashMap<String, String> params) {
        String lang = BizStore.getLanguage();
        String baseUrl = BASE_URL.concat(lang + SLASH + serviceName + QUESTION_MARK);
        HashMap<String, String> credentials = BizStore.getUserCredentials();
        HashMap<String, String> serviceParams = new HashMap<>();
        //serviceParams.putAll(credentials);
        serviceParams.putAll(params);
        serviceParams.put("os", "android");
        try {
            String query = createQuery(serviceParams);
            serviceUrl = baseUrl.concat(query); //TODO remove comment
            //serviceUrl = baseUrl + SLASH + "26";//TODO
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.logI("SERVICE_URL", serviceUrl);
    }

}