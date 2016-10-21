package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.os.AsyncTask;


import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.*;

/**
 * Created by Babar on 30-Sep-16.
 */
public class HttpsTest extends AsyncTask<Void, Void, Void>
{
    private Context context;

    private final static String SERVICE_NAME = "https://ooredoo.bizstore.com.pk/index.php/api/en/promotionaldeals?os=android";

    public HttpsTest(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        try {
            fire();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void fire() throws IOException, CertificateException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream is = BizStore.context.getResources().openRawResource(R.raw.cert);
        Certificate ca;
        try {
            ca = cf.generateCertificate(is);
            Logger.print("ca=" + ((X509Certificate) ca).getSubjectDN());
        }
        finally {
            is.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        // Initialise the TMF as you normally would, for example:
        tmf.init(keyStore);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, tmf.getTrustManagers(), null);

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                //Logger.print("Https Hostname: "+s);
               // return hv.verify(s, sslSession);
                return true;
            }
        };

        URL url = new URL(SERVICE_NAME);


        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sc.getSocketFactory());
        connection.setHostnameVerifier(hostnameVerifier);
        connection.setRequestProperty(HTTP_X_USERNAME, BizStore.username);
        connection.setRequestProperty(HTTP_X_PASSWORD, BizStore.password);
        connection.setConnectTimeout(CONNECTION_TIME_OUT);
        connection.setReadTimeout(READ_TIME_OUT);
        connection.setRequestMethod(METHOD);
        connection.setDoInput(true);
       // connection.connect();

        InputStream stream = connection.getInputStream();

        String result = readStream(stream);

        Logger.print("HTTPS Test: "+result);


       /* TrustManager[] trustManagers = tmf.getTrustManagers();
        final X509TrustManager origTrustManager = (X509TrustManager) trustManagers[0];

        TrustManager[] wrappedTrustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
                        origTrustManager.checkClientTrusted(x509Certificates, authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
                        try
                        {
                            origTrustManager.checkServerTrusted(x509Certificates, authType);
                        }
                        catch (CertificateExpiredException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return origTrustManager.getAcceptedIssuers();
                    }
                }
        };*/


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
}
