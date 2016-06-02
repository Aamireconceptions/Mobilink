package com.ooredoo.bizstore.asynctasks;


import android.app.Dialog;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by hp on 6/1/2016.
 */
public class ReportAsyncTask extends BaseAsyncTask<String, Void, String> {

    private static final String SERVICE_NAME="/report?";

    DealDetailActivity dealDetailAct;

    Dialog dialog;

    private String reportMessage;

    String dealId;
    String ANDROID="android";
    String bussinessId;

    public ReportAsyncTask(DealDetailActivity act,String message,int bussId,int delId){
        dealDetailAct=act;
        reportMessage=message;
        bussinessId=String.valueOf(bussId);
        dealId=String.valueOf(delId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogUtils.createCustomLoader(dealDetailAct, dealDetailAct.getString(R.string.please_wait));
        dialog.show();
    }


    @Override
    protected String doInBackground(String... params) {

        String result=null;

        HashMap<String, String> parmeters = new HashMap<>();

        parmeters.put("os", ANDROID);
        parmeters.put("reportmessage", reportMessage);
        parmeters.put("dealId",dealId);
        parmeters.put("businessId",bussinessId);
        try {
           String query = createQuery(parmeters);

            URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("GCM URL: " + url);

            String json = getJson(url);

            return json;
            }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        dialog.dismiss();

        if(result != null) {
            Toast.makeText(dealDetailAct, "Your report posted successfully.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(dealDetailAct, "Error: Check your Internet Connction.", Toast.LENGTH_SHORT).show();
        }
    }
}
