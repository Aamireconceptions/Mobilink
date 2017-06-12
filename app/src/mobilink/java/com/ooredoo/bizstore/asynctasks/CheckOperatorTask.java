package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.dialogs.ChargesDialog;
import com.ooredoo.bizstore.dialogs.MsisdnDialog;
import com.ooredoo.bizstore.model.BrandResponse;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Operator;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 08-Feb-17.
 */
public class CheckOperatorTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private MsisdnDialog msisdnDialog;

    private String serviceName = "/checkoperator?";

    public CheckOperatorTask(Context context, MsisdnDialog msisdnDialog)
    {
        this.context = context;

        this.msisdnDialog = msisdnDialog;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        dialog = this.msisdnDialog.getDialog();
       dialog = DialogUtils.createCustomLoader(DealDetailActivity.mActivity, context.getString(R.string.please_wait));
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
        if(s != null) {
            Gson gson = new Gson();

            String Operator = "";
            String Package  = "";
            String Billing_type = "";
            try {
                JsonElement jsonElement = gson.fromJson(s, JsonElement.class);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonElement je = jsonObject.get("results");

               // String data =je.toString();
                JsonObject InnerObject = je.getAsJsonObject();

                JsonElement jeOperatore = InnerObject.get("operator");
                Operator = jeOperatore.toString().replaceAll("\"","");

                try {
                    JsonElement jePackage = InnerObject.get("package");
                    Package = jePackage.toString().replaceAll("\"","");;
                }catch (Exception e)
                {

                }
                try {
                    JsonElement jeBilling = InnerObject.get("billing_type");
                    Billing_type = jeBilling.toString().replaceAll("\"","");;
                }catch (Exception e)
                {

                }

                chargesDialog = ChargesDialog.newInstance(msisdn, Operator, Package, Billing_type);
                chargesDialog.show(DealDetailActivity.mActivity.getFragmentManager(), null);

                //  Operator response = gson.fromJson(jsonElement, Operator.class);

            } catch (JsonSyntaxException e) {
                e.printStackTrace();

            }
        } else
        {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }



    }




    private String checkOperator(String msisdn) throws IOException {
        HashMap<String, String> params = new HashMap<>();

        params.put(MSISDN, msisdn);
        params.put(OS, ANDROID);
        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + serviceName + query);

        String json = getJson(url);

        Logger.print("checkOperator response: "+url.toString()+", "+json);

        return json;
    }
}