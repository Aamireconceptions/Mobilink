package com.ooredoo.bizstore.asynctasks;

import android.os.AsyncTask;
import android.util.Base64;

import com.ooredoo.bizstore.utils.Logger;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.util.Base64.encode;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private final static String ENCODING = "UTF-8";
    private final static String AMPERSAND = "&";
    private final static String EQUAL = "=";
    private final static String QUESTION_MARK = "?";

    public static String BASE_URL = "";

    public final static int CONNECTION_TIME_OUT = 5 * 1000;

    public final static int  READ_TIME_OUT = 10 * 1000;

    public final static String METHOD = "GET";

    public final static String ID = "id";

    protected String serviceUrl;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setServiceUrl(String serviceName, List<NameValuePair> params) {
        String baseUrl = BASE_URL.concat(serviceName.concat(QUESTION_MARK));
        String query = createQuery(params);
        serviceUrl = baseUrl.concat(query);
    }

    public static String encryptVal(String str) {
        String tmp = "";
        if(isNotNullOrEmpty(str)) {
            try {
                tmp = new String(encode(str.getBytes(), Base64.DEFAULT)).trim();
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public String createQuery(List<NameValuePair> params)
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean isFirst = true;

        for(NameValuePair nameValuePair : params) {
            if(isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append(AMPERSAND);
            }

            stringBuilder.append(nameValuePair.getName());
            stringBuilder.append(EQUAL);
            stringBuilder.append(nameValuePair.getValue());
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
}