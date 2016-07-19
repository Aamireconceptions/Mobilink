package com.ooredoo.bizstore.asynctasks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Voucher;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Babar on 25-Jun-15.
 */
public class VerifyMerchantCodeTask extends BaseAsyncTask<String, Void, String>
{
    private DealDetailActivity detailActivity;

    private SnackBarUtils snackBarUtils;

    private Tracker tracker, ooredooTracker;

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
                        Map<String, String> redeemEvent = new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Deal Redeem")
                                .build();

                        tracker.send(redeemEvent);

                        if(BuildConfig.FLAVOR.equals("ooredoo"))
                        {
                            BizStore bizStore = (BizStore) detailActivity.getApplication();

                            ooredooTracker = bizStore.getOoredooTracker();
                            ooredooTracker.send(redeemEvent);
                        }


                        if(BuildConfig.FLAVOR.equals("mobilink"))
                        {
                            final Dialog dialog = DialogUtils.createMobilinkRedeemDialog(detailActivity);

                            dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    DealDetailActivity.genericDeal.date = voucher.date;
                                    DealDetailActivity.genericDeal.time = voucher.time;

                                    detailActivity.showCode(voucher.vouchers_claimed, voucher.max_allowed, true);
                                    detailActivity.btGetCode.setText("Get Discount Again");

                                    dialog.dismiss();
                                }
                            });

                            TextView tvDealDesc = (TextView) dialog.findViewById(R.id.deal_desc);
                            tvDealDesc.setText(DealDetailActivity.genericDeal.description);

                            TextView tvUniqueId = (TextView) dialog.findViewById(R.id.unique_id);
                            tvUniqueId.setText("" + DealDetailActivity.genericDeal.id);

                            TextView tvDate = (TextView) dialog.findViewById(R.id.date);
                            tvDate.setText(voucher.date);

                            TextView tvTime= (TextView) dialog.findViewById(R.id.time);
                            tvTime.setText(voucher.time);

                            Button btNoThanks = (Button) dialog.findViewById(R.id.no_thanks);
                            btNoThanks.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            Button btShareFb = (Button) dialog.findViewById(R.id.share_fb);
                            btShareFb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            String brandLogo = DealDetailActivity.genericDeal.businessLogo;

                            Logger.print("BrandLogo: " + brandLogo);

                            if(brandLogo != null && !brandLogo.equals(""))
                            {
                                final String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

                                Bitmap bitmap = detailActivity.memoryCache.getBitmapFromCache(imgUrl);

                                if(bitmap != null)
                                {
                                    bitmap = BitmapProcessor.makeBitmapRound(bitmap);

                                    ImageView ivLogo = (ImageView) dialog.findViewById(R.id.brand_logo);
                                    ivLogo.setImageBitmap(bitmap);
                                }
                            }

                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.show();

                            return;
                        }

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