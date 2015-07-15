package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static com.ooredoo.bizstore.BizStore.username;
import static com.ooredoo.bizstore.utils.DialogUtils.createCustomLoader;

/**
 * @author Babar
 * @since 07-Jul-15.
 */
public class ShareAppTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private final static String SERVICE_NAME = "/recommendservice?";

    private ProgressDialog progressDialog;

    private SnackBarUtils snackBarUtils;

    public ShareAppTask(Context context, SnackBarUtils snackBarUtils)
    {
        this.context = context;

        this.snackBarUtils = snackBarUtils;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        dialog = createCustomLoader((Activity) context, "Sharing...");
        /*progressDialog = ProgressDialog.show(context, "", context.getString(R.string.please_wait));*/
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return shareApp(params[0]);
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

        closeDialog(dialog);
        closeDialog(progressDialog);

        if(result != null)
        {
            try
            {
                Gson gson = new Gson();

                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    snackBarUtils.showSimple(R.string.success_shared, Snackbar.LENGTH_SHORT);
                }
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();

                snackBarUtils.showSimple(R.string.error_server_down, Snackbar.LENGTH_SHORT);
            }
        }
        else
        {
            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String shareApp(String phoneNum) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(MSISDN_TO, phoneNum);
        params.put(MSISDN_FROM, username == null ? "3331234567" : username); //TODO remove temp number i.e 3331234567
        //params.put(MESSAGE, msg); //MESSAGE IS STORED ON SERVER

        String query = createQuery(params);

        String serviceUrl = BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query;

        Logger.logI("SERVICE_URL", serviceUrl);

        URL url = new URL(serviceUrl);

        result = getJson(url);

        Logger.print("shareApp result:"+result);

        return result;
    }
}