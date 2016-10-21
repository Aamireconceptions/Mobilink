package com.ooredoo.bizstore.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ExifInterface;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
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

import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.*;

/**
 * @author  Babar
 * @since 15-Jun-15.
 */
public class BitmapProcessor
{
    HttpsURLConnection connection;
    public Bitmap decodeSampledBitmapFromStream(BufferedInputStream inputStream, URL url,
                                                int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream, null, options);

        int width = options.outWidth;
        int height = options.outHeight;

        Logger.print("@Req Width: "+reqWidth);
        Logger.print("@Req Height: " + reqHeight);

        Logger.print("@Actual Width: "+width);
        Logger.print("@Actual Height: " + height);

        int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream is = BizStore.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(is);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            } finally {
                is.close();
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

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostName, SSLSession sslSession) {
                    /*HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    Logger.print("Https Hostname: "+hostName);

                    return hv.verify(s, sslSession);*/

                    return true;
                }
            };

            //inputStream.reset();
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

            inputStream = openStream();

            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            if (bitmap != null) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();

                Logger.print("@inSample:" + sampleSize);
                Logger.print("@Modified Width: " + width);
                Logger.print("@Modified Height: " + height);
                Logger.print("@Modified URL: " + url.toString());
            }

            return bitmap;
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
        } catch (MalformedURLException e) {
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

    public Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fd)
    {
        /*final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;*/

        return BitmapFactory.decodeFileDescriptor(fd);

    }

    public  Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);

        int width = options.outWidth;
        int height = options.outHeight;

        Logger.print("@Req Width: "+reqWidth);
        Logger.print("@Req Height: " + reqHeight);

        Logger.print("@Actual Width: "+width);
        Logger.print("@Actual Height: " + height);

        int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;

        bitmap = BitmapFactory.decodeFile(pathName, options);

        if(bitmap != null)
        {
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            Logger.print("@inSample:"+sampleSize);
            Logger.print("@Modified Width: "+width);
            Logger.print("@Modified Height: " + height);

        }

        return bitmap != null ? rotateBitmapIfNeeded(pathName, bitmap) : null;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int requiredWidth, int requiredHeight)
    {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;

        if(width > requiredWidth || height > requiredHeight)
        {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > requiredWidth
                                    ||
                    (halfHeight / inSampleSize) > requiredHeight)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap rotateBitmapIfNeeded(String pathName, Bitmap bitmap) throws IOException {
        ExifInterface exifInterface = new ExifInterface(pathName);

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                        ExifInterface.ORIENTATION_NORMAL);

        Logger.logI("BITMAP_ORIENTATION: " + orientation, pathName);

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:

                return rotateBitmap(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:

                return rotateBitmap(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:

                return rotateBitmap(bitmap, 270);
        }

        return bitmap;
    }

    public static Bitmap makeBitmapRound(Bitmap src)
    {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);

        return mutableBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap downsizeBitmap(Bitmap bitmap, int width, int height)
    {
        return Bitmap.createBitmap(bitmap, 0, 0, width, height);
    }
}
