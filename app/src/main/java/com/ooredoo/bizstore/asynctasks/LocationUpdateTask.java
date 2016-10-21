package com.ooredoo.bizstore.asynctasks;

import android.os.AsyncTask;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.Logger;

import net.hockeyapp.android.metrics.model.Base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

/**
 * Created by Babar on 29-Aug-16.
 */
public class LocationUpdateTask extends AsyncTask<Double, Void, Void>
{
    private final static String SERVICE_NAME = "vasnotifications/update_location.php?";

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
        BufferedReader reader = null;

        try
        {
            HttpsURLConnection connection = null;

            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                InputStream isCert = BizStore.context.getResources().openRawResource(R.raw.cert);
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
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("msisdn", BizStore.username);

                String query = createQuery(params);

                URL url = new URL(SERVER_URL +  SERVICE_NAME + query);

                Logger.print("Update Loc Url: "+url.toString());

                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setHostnameVerifier(hostnameVerifier);
                connection.setRequestProperty(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
                connection.setRequestProperty(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));
                connection.setConnectTimeout(CONNECTION_TIME_OUT);
                connection.setReadTimeout(READ_TIME_OUT);
                connection.setRequestMethod(METHOD);
                connection.setDoInput(true);
                connection.connect();

                InputStream is = connection.getInputStream();

                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(is, ENCODING));

                String line;
                while((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }

                Logger.print("updateLoc response: " + builder.toString());
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
                stringBuilder.append(AMPERSAND);
            }

            stringBuilder.append(URLEncoder.encode(entry.getKey(), ENCODING));
            stringBuilder.append(EQUAL);
            stringBuilder.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }

        Logger.print("URL Query: " + stringBuilder.toString());

        return stringBuilder.toString();
    }
}