package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author  Babar
 * @since 18-Jun-15.
 */
public class BitmapDownloadTask extends BaseAsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    private ProgressBar progressBar;

    public String imgUrl;

    private BitmapProcessor bitmapProcessor = new BitmapProcessor();

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private URL url;

    public static ConcurrentHashMap<String, String> downloadingPool = new ConcurrentHashMap<>();

    public BitmapDownloadTask(ImageView imageView, ProgressBar progressBar)
    {
        this.imageView = imageView;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        showProgress(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        imgUrl = params[0];

        Logger.print("doInBg: ->imgUrl: " + imgUrl);

        return downloadBitmap(imgUrl, params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
      super.onPostExecute(bitmap);

        showProgress(View.GONE);

        downloadingPool.remove(imgUrl);

        if(bitmap != null)
        {
            Logger.print("x3 Downloaded: " + imgUrl);

            memoryCache.addBitmapToCache(imgUrl, bitmap);

            if(imageView != null)
            {
                imageView.setImageBitmap(bitmap);
                imageView.setTag("loaded");
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        Logger.print("onCacelled");
    }

    boolean writing = false;
    HttpURLConnection connection = null;
    public Bitmap downloadBitmap(final String imgUrl, String reqWidth, String reqHeight)
    {

            if(memoryCache.getBitmapFromCache(imgUrl) != null)
            {
                Logger.print("Already downloaded. Cancelling task :" + imgUrl);

                cancel(true);
            }

            if(BizStore.forceStopTasks)
            {
                Logger.print("Force stopped bitmap download task");

                return null;
            }

            if(downloadingPool.get(imgUrl) != null)
            {
                Logger.print("x3 Image Download alreay in progress :"+imgUrl);

                cancel(true);
            }

            if(isCancelled())
            {
                Logger.print("isCancelled: true");

                return null;
            }

            Logger.print("x3 " + imgUrl + " added to pool");

            downloadingPool.put(imgUrl, imgUrl);

        try {

            Logger.print("Bitmap Url: " + imgUrl);
            url = new URL(imgUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
            connection.setRequestProperty(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
            connection.connect();



            BufferedInputStream inputStream = openStream();

            int width = Integer.parseInt(reqWidth);

            int height = Integer.parseInt(reqHeight);

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

            if(bitmap != null)
            {
                diskCache.addBitmapToDiskCache(imgUrl, bitmap);
            }

            return bitmap;
        }
         catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
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

    public void showProgress(int visible)
    {
        if(progressBar != null)
        {
            progressBar.setVisibility(visible);
        }
    }
}