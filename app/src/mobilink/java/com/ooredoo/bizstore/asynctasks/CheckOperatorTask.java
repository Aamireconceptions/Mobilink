package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.dialogs.ChargesDialog;
import com.ooredoo.bizstore.dialogs.MsisdnDialog;
import com.ooredoo.bizstore.model.Operator;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Babar on 08-Feb-17.
 */
public class CheckOperatorTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private MsisdnDialog msisdnDialog;

    private String serviceName = "/checkOp?";

    public CheckOperatorTask(Context context, MsisdnDialog msisdnDialog)
    {
        this.context = context;

        this.msisdnDialog = msisdnDialog;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader((Activity) context, context.getString(R.string.please_wait));
        dialog.show();
    }

    String msisdn;
    @Override
    protected String doInBackground(String... strings) {
        try {
            msisdn = strings[0];
            return checkOperator(strings[0]);
        } catch (IOException e) {
                e.printStackTrace();
        }

        return null;
    }
   public static ChargesDialog chargesDialog;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        dialog.dismiss();

        if(s != null)
        {
            Operator operator = new Gson().fromJson(s, Operator.class);

            chargesDialog = ChargesDialog.newInstance(msisdn);
            chargesDialog.show(((Activity) context).getFragmentManager(), null);

            /*if(operator.name.equals("Mobilink"))
            {
               FragmentUtils.replaceFragment((Activity) context, android.R.id.content,
                       ChargesDialog.newInstance(msisdn), null);
            }
            else
            {

            }*/

        }
        else
        {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String checkOperator(String msisdn) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(MSISDN, msisdn);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + serviceName + query);

        String json = getJson(url);

        Logger.print("checkOperator response: "+url.toString()+", "+json);

        return json;
    }
}