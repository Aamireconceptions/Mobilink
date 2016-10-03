package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ViewedRatedAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.DODResponse;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.*;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * Created by Babar on 13-Jan-16.
 */
public class ViewedRatedTask extends AsyncTask<String, Void, String>
{
    private Context context;

    private ViewedRatedAdapter adapter;

    private String category;

    private HomeFragment homeFragment;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;

    private static final String SERVICE_NAME  = "/viewednrated?";

    public ViewedRatedTask(Context context, ViewedRatedAdapter adapter, Fragment fragment)
    {
        this.context = context;

        this.adapter = adapter;

        this.homeFragment = (HomeFragment) fragment;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;
    }

    @Override
    protected String doInBackground(String... params)
    {
            try {
                category = params[0];

                return getViewedRated();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        setData(s);
    }

    public void setData(String result)
    {
        Logger.print("ViewedRated result: "+result);
        dealsTaskFinishedListener.onRefreshCompleted();
        adapter.clear();

        if(result != null) {
            Gson gson = new Gson();

            try {

                DODResponse response = gson.fromJson(result, DODResponse.class);

                if (response.resultCode != -1) {
                    if(response.dods != null  && response.dods.size() > 0)
                    {
                        dealsTaskFinishedListener.onHaveDeals();

                        if(category != null) {
                            final String KEY = PREFIX_DEALS.concat(category);
                            final String UPDATE_KEY = KEY.concat("_UPDATE");

                            updateVal((Activity) context, KEY, result);
                            updateVal((Activity) context, UPDATE_KEY, currentTimeMillis());
                        }
                    }

                   // adapter.setData(response.dods);

                    homeFragment.addMostViewedAndTopRated(response.dods);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            dealsTaskFinishedListener.onNoDeals(0);
        }

        adapter.notifyDataSetChanged();
    }

    private String getViewedRated() throws IOException {
        HttpsURLConnection connection = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream isCert = HomeActivity.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try
            {
                ca = cf.generateCertificate(isCert);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            }
            finally
            {
                isCert.close();
            }

            String keystoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            // Initialise the TMF as you normally would, for example:
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier hostnameVerifier = new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostName, SSLSession sslSession)
                {
                    /*HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    Logger.print("Https Hostname: "+hostName);

                    return hv.verify(s, sslSession);*/

                    return true;
                }
            };

            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put("lat", ""+HomeActivity.lat);
            params.put("lng", "" + HomeActivity.lng);
            params.put("nearby", "true");

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestProperty(HTTP_X_USERNAME, BizStore.username);
            connection.setRequestProperty(HTTP_X_PASSWORD, BizStore.password);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
            connection.connect();

            InputStream is = connection.getInputStream();

            BufferedReader reader = null;
            try {

                reader = new BufferedReader(new InputStreamReader(is, ENCODING));

                StringBuilder stringBuilder = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();
            }
            finally {
                if(reader != null)
                {
                    reader.close();
                }
            }
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String createQuery(HashMap<String, String> params) throws UnsupportedEncodingException
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
                stringBuilder.append("&");
            }

            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }

        return stringBuilder.toString();
    }

    public String getCache(String category)
    {
        String result = null;

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        Activity activity = (Activity) context;

        String cacheData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, UPDATE_KEY);

        if(!isNullOrEmpty(cacheData) && (!hasInternetConnection(activity) || !updateFromServer))
        {
            result = cacheData;
        }

        return result;
    }
}