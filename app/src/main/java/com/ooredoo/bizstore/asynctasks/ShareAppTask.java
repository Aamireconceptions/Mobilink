package com.ooredoo.bizstore.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Babar on 07-Jul-15.
 */
public class ShareAppTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private final static String SERVICE_NAME = "/shareApp?";

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

        progressDialog = ProgressDialog.show(context, "", context.getString(R.string.please_wait));
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return shareApp(params[0], params[1]);
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

        progressDialog.dismiss();

        if(result != null)
        {
            snackBarUtils.showSimple(R.string.success_shared, Snackbar.LENGTH_SHORT);
        }
        else
        {
            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String shareApp(String phoneNum, String msg) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(PHONE_NUMBER, phoneNum);
        params.put(MESSAGE, msg);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        result = getJson(url);

        Logger.print("shareApp result:"+result);

        return result;
    }
}