package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.FileUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NotificationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Babar on 01-Aug-16.
 */
public class FileDownloadTask extends BaseAsyncTask<String, Float, String>
{
    private Context context;

    private File pathToSave;

    private int id;

    private String fileUrl;

    private TextView tvBrochure;

    private ImageView ivDownload;

    private NotificationUtils notificationUtils;

    public FileDownloadTask(Context context, File pathToSave, int id,
                            String fileUrl, TextView tvBrochure, ImageView ivDownload)
    {
        this.context = context;

        this.pathToSave = pathToSave;

        this.id = id;

        this.fileUrl = fileUrl;

        this.tvBrochure = tvBrochure;

        this.ivDownload = ivDownload;

        notificationUtils = new NotificationUtils(context);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        tvBrochure.setText("Downloading");
        tvBrochure.setTag("Downloading");
        notificationUtils.showDownloadingNotification(id, "Downloading", fileUrl);
    }

    @Override
    protected String doInBackground(String... params)
    {
        String error = null;

        try
        {
            error = downloadFile();
        }
        catch (IOException e)
        {
            error = "Network connection problem";

            e.printStackTrace();
        }

        return error;
    }

    @Override
    protected void onProgressUpdate(Float... values)
    {
        super.onProgressUpdate(values);

        //Logger.print("onProgressUpdate:" + values[0]);


    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        String notificationMsg = null;
        String notificationSubMsg = null;

        if(result != null)
        {
            notificationMsg = "Download Failed";
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            tvBrochure.setText("Failed to download. Retry?");
            tvBrochure.setTag(null);

            FileUtils.deleteFile(pathToSave);
        }
        else
        {
            notificationMsg = "Download Complete";
            notificationSubMsg = "Tap to open";

            tvBrochure.setText("View");
            tvBrochure.setTag("Downloaded");

            ivDownload.setVisibility(View.GONE);

            Logger.print("Download complete");
        }

        notificationUtils.onDownloadFinish(notificationMsg, notificationSubMsg, id, pathToSave);
    }

    private String downloadFile() throws IOException
    {
        String error = null;

        HttpURLConnection connection = null;

        InputStream is = null;

        FileOutputStream fos = null;

        try
        {
            Logger.print("Document URL:"+fileUrl);
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                error = "Server is down currently";

                return error;
            }

           // File file = new File(dir, CryptoUtils.encodeToBase64(fileUrl) + ".pdf");

            //int contentLength = connection.getContentLength();
            String headerContentLength = connection.getHeaderField("content-length");

            if(headerContentLength == null)
            {
                throw new IOException("404 File not found on server");
            }

            long contentLength = Long.parseLong(headerContentLength);

            Logger.print("Content Length: "+contentLength);

            is = connection.getInputStream();
            fos = new FileOutputStream(pathToSave);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while((count = is.read(data)) != -1)
            {
                total += count;

                publishProgress(contentLength, total);

                fos.write(data, 0, count);
            }

            Logger.print("Total Length: "+total);

           /* while((count = is.read(data)) != -1)
            {
                total += count;
                fos.write(data, 0, count);
            }*/
        }
        finally
        {
            if(fos != null)
            {
                fos.close();
            }

            if(is != null)
            {
                is.close();
            }

            if(connection != null)
            {
                connection.disconnect();
            }
        }

        return error;
    }

    private void publishProgress(long contentLength, long totalRead)
    {
        if(contentLength > 0)
        {
            float progress = ((float) totalRead / contentLength) * 100;

            notificationUtils.updateNotificationProgress(progress, id);

            //publishProgress(progress);
        }
    }
}