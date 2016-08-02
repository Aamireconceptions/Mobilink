package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.widget.Toast;

import com.ooredoo.bizstore.utils.CryptoUtils;
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

    private NotificationUtils notificationUtils;

    public FileDownloadTask(Context context, File pathToSave, int id, String fileUrl)
    {
        this.context = context;

        this.pathToSave = pathToSave;

        this.id = id;

        this.fileUrl = fileUrl;

        notificationUtils = new NotificationUtils(context);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

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

        Logger.print("onProgressUpdate:" + values[0]);

        notificationUtils.updateNotificationProgress(values[0], id);
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
        }
        else
        {
            notificationMsg = "Download Complete";
            notificationSubMsg = "Tap to open";

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
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                error = "Server is down currently";

                return error;
            }

           // File file = new File(dir, CryptoUtils.encodeToBase64(fileUrl) + ".pdf");

            int contentLength = connection.getContentLength();

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

            while((count = is.read(data)) != -1)
            {
                total += count;
                fos.write(data, 0, count);
            }
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

    private void publishProgress(int contentLength, long totalRead)
    {
        if(contentLength > 0)
        {
            float progress = ((float) totalRead / contentLength) * 100;

            publishProgress(progress);
        }
    }
}