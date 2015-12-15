package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.DialogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Babar on 15-Dec-15.
 */
public class ContactTask extends AsyncTask<String, Void, String>
{
    private Context context;

    private Dialog dialog;

    public ContactTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader((Activity) context, context.getString(R.string.please_wait));
        dialog.show();;;
    }

    private static final String SERVICE_NAME = "/contact_us?";
    @Override
    protected String doInBackground(String... params) {

        try
        {
            sendMessage(params[0]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        dialog.dismiss();

        if(result != null)
        {
            try
            {
                if(result.equals("success"))
                {
                    Toast.makeText(context, "Thanks for contacting us", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();

                Toast.makeText(context, R.string.error_server_down, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String sendMessage(String message) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(BaseAsyncTask.OS, BaseAsyncTask.ANDROID);
        params.put("message", message);

        String query;

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

            stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        query = stringBuilder.toString();

        URL url = new URL(BaseAsyncTask.BASE_URL + SERVICE_NAME + query);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(BaseAsyncTask.HTTP_X_USERNAME, BizStore.username);
        connection.setRequestProperty(BaseAsyncTask.HTTP_X_PASSWORD, BizStore.password);
        connection.connect();

        InputStream is = connection.getInputStream();

        StringBuilder builder = new StringBuilder();

        BufferedReader reader = null;
        try
        {
           reader =new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }

            result = builder.toString();

            return result;
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
    }
}
