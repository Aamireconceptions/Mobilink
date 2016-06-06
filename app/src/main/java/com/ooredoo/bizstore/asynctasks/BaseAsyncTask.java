package com.ooredoo.bizstore.asynctasks;

import android.app.Dialog;
import android.os.AsyncTask;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
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
    protected final static String ENCODING = "UTF-8";

    protected final static String AMPERSAND = "&";

    protected final static String EQUAL = "=";
    protected final static String SLASH = "/";
    protected final static String QUESTION_MARK = "?";

    public static String SERVER_URL = BuildConfig.FLAVOR.equals("ooredoo")
    ? "http://ooredoo.bizstore.com.pk/" : BuildConfig.FLAVOR.equals("telenor")
            ? "http://telenor.bizstore.com.pk/" : BuildConfig.FLAVOR.equals("dealionare")
    ? "http://dealionare.bizstore.com.pk/" : "http://jazz.bizstore.com.pk/";

    /*public static String SERVER_URL = BuildConfig.FLAVOR.equals("ooredoo")
            ? "http://ooredoostage.bizstore.com.pk/" : BuildConfig.FLAVOR.equals("telenor")
            ? "http://telenor.bizstore.com.pk/" : BuildConfig.FLAVOR.equals("dealionare")
            ? "http://dealionare.bizstore.com.pk/" : "http://jazz.bizstore.com.pk/";*/

    public static String BASE_URL = BuildConfig.FLAVOR.equals("ooredoo")
    ? "http://ooredoo.bizstore.com.pk/index.php/api/" : BuildConfig.FLAVOR.equals("telenor")
            ? "http://telenor.bizstore.com.pk/index.php/api/" : BuildConfig.FLAVOR.equals("dealionare")
    ? "http://dealionare.bizstore.com.pk/index.php/api/" : "http://jazz.bizstore.com.pk/index.php/api/";

    /*public static String BASE_URL = BuildConfig.FLAVOR.equals("ooredoo")
            ? "http://ooredoostage.bizstore.com.pk/" : BuildConfig.FLAVOR.equals("telenor")
            ? "http://telenor.bizstore.com.pk/index.php/api/" : BuildConfig.FLAVOR.equals("dealionare")
            ? "http://dealionare.bizstore.com.pk/index.php/api/" : "http://jazz.bizstore.com.pk/index.php/api/";*/

    public final static int CONNECTION_TIME_OUT = 30 * 1000;

    public final static int  READ_TIME_OUT = 30 * 1000;

    public final static String METHOD = "GET";

    public final static String ID = "id";

    public final static String OS = "os";

    public final static String ANDROID = "android";

    public final static String CATEGORY = "category";

    public final static String PHONE_NUMBER = "phone_number";

    public final static String MESSAGE = "message";

    public final static String MSISDN = "msisdn";

    public final static String MSISDN_FROM = "msisdn_from";

    public final static String MSISDN_TO = "msisdn_to";

    public final static String GCM_TOKEN = "gcm_token";

    public final static String TYPE = "type";

    public final static String HTTP_X_USERNAME = "HTTP_X_USERNAME";

    public final static String HTTP_X_PASSWORD = "HTTP_X_PASSWORD";

    public final static String IMAGE_BASE_URL = BuildConfig.FLAVOR.equals("ooredoo")
    ? "http://ooredoo.bizstore.com.pk" : BuildConfig.FLAVOR.equals("telenor")
            ? "http://telenor.bizstore.com.pk" : BuildConfig.FLAVOR.equals("dealionare")
    ? "http://dealionare.bizstore.com.pk" : "http://jazz.bizstore.com.pk";

    protected Dialog dialog;

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

    public String getJson(URL url) throws IOException
    {
        String result;

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
           // Logger.print("BaseAsync Entered Finally");

            if(reader != null)
            {
                reader.close();

                //Logger.print("BaseAsync Closing Reader");
            }
        }
    }

    public HttpURLConnection openConnectionAndConnect(URL url) throws IOException
    {
        String credentials = BizStore.username + ":" + BizStore.password;

       // String basicAuth = "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(HTTP_X_USERNAME, BizStore.username);
        connection.setRequestProperty(HTTP_X_PASSWORD, BizStore.password);
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
        serviceParams.put(OS, ANDROID);
        serviceParams.putAll(params);

        try {
            String query = createQuery(serviceParams);
            serviceUrl = baseUrl.concat(query);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.print("SERVICE_URL "+ serviceUrl);
    }

    protected void closeDialog(Dialog dialog) {
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public void handleIndicatorVisibility(int count, CirclePageIndicator circlePageIndicator)
    {
        if(count > 1) {
            circlePageIndicator.setVisibility(View.VISIBLE);
        } else {
            circlePageIndicator.setVisibility(View.GONE);
        }
    }
}