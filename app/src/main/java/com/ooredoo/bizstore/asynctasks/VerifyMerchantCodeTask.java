package com.ooredoo.bizstore.asynctasks;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Voucher;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Babar on 25-Jun-15.
 */
public class VerifyMerchantCodeTask extends BaseAsyncTask<String, Void, String>
{
    private DealDetailActivity detailActivity;

    private SnackBarUtils snackBarUtils;

    private Tracker tracker;

    private Dialog dialog;

    private final static String SERVICE_NAME = "/redeemdiscount?";

    public VerifyMerchantCodeTask(DealDetailActivity detailActivity, SnackBarUtils snackBarUtils,
                                  Tracker tracker)
    {
        this.detailActivity = detailActivity;

        this.snackBarUtils = snackBarUtils;

        this.tracker = tracker;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader(detailActivity, detailActivity.getString(R.string.please_wait));
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getCode(params[0], params[1],params[2]);
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
            Gson gson = new Gson();

            try {
               final Voucher voucher = gson.fromJson(result, Voucher.class);

                if (voucher.resultCode != -1)
                {
                    //detailActivity.showCode(voucher.code);
                    if(voucher.resultCode == 0)
                    {
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Deal Redeem")
                                .build());

                        final Dialog dialog = DialogUtils.createAlertDialog(detailActivity, R.string.discount_redeemed,
                                R.string.success_redeemed);
                        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {

                                detailActivity.showCode(voucher.vouchers_claimed, voucher.max_allowed, true);

                                dialog.dismiss();
                            }
                        });
dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                    else
                        if(voucher.resultCode == 1)
                        {
                            final Dialog dialog = DialogUtils.createAlertDialog(detailActivity, R.string.discount_redeemed,
                                    R.string.error_max_availed);
                            dialog.show();

                            dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    detailActivity.showCode(voucher.vouchers_claimed, voucher.max_allowed, true);
                                    dialog.dismiss();
                                }
                            });
                        }
                }
                else
                {
                    Dialog dialog = DialogUtils.createAlertDialog(detailActivity, R.string.error_discount_redeemed,
                            R.string.error_invalid_merchant_code);
                    dialog.show();
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
            Logger.print("getCode: Failed to download banners due to no internet");

            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getCode(String id, String code, String businessId) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("deal", id);
        params.put("merchant", code);
        params.put("businessDetail", businessId);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getCode() URL:"+ url.toString());

        result = getJson(url);

        Logger.print("getCode: "+result);

        return result;
    }
}