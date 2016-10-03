package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NotificationUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Babar on 31-Jul-15.
 */
public class BitmapNotificationTask extends BaseAsyncTask<String, Void, Bitmap[]>
{
    private Context context;

    private int id;

    private String title, desc;

    private URL url;

    private BitmapProcessor bitmapProcessor;

    public BitmapNotificationTask(Context context, int id, String title, String desc)
    {
        this.context = context;

        this.id = id;

        this.title = title;

        this.desc = desc;

        bitmapProcessor = new BitmapProcessor();
    }

    @Override
    protected Bitmap[] doInBackground(String... params)
    {
        return downloadBitmap(params[0], params[1], params[2], params[3]);
    }

    HttpsURLConnection connection = null;
    public Bitmap[] downloadBitmap(String imgUrl, String reqWidth, String reqHeight, String brandLogo)
    {


        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream is = HomeActivity.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try
            {
                ca = cf.generateCertificate(is);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            }
            finally
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            Logger.print("notification Bitmap Url: " + imgUrl);

            url = new URL(imgUrl);

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

            Bitmap[] bitmaps = new Bitmap[2];;

            BufferedInputStream inputStream = openStream();

            int width = (int) Converter.convertDpToPixels(Integer.parseInt(reqWidth));
            int height = (int) Converter.convertDpToPixels(Integer.parseInt(reqHeight));

            bitmaps[0] = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

            url = new URL(brandLogo);

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

            inputStream = openStream();

            bitmaps[1] = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

            return bitmaps;
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public BufferedInputStream openStream() throws IOException
    {
        return new BufferedInputStream(connection.getInputStream());
    }

    @Override
    protected void onPostExecute(Bitmap... bitmap)
    {
        super.onPostExecute(bitmap);

        if(bitmap != null)
        {
            NotificationUtils.showNotification(context, title, desc, id, bitmap);
        }
        else
        {
            NotificationUtils.showNotification(context, title, desc, id, null);

            Logger.print("Failed to download notification img");
        }
    }
}