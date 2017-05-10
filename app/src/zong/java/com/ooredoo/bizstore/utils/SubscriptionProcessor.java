package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Subscription;

import static com.ooredoo.bizstore.utils.DialogUtils.showVerificationCodeDialog;

/**
 * Created by Babar on 10-Feb-17.
 */
public class SubscriptionProcessor
{
    public static void processSubscription(Context context, Subscription subscription) {
        String errMsg = "Error";
        if(subscription != null)
        {
            if(subscription.resultCode != -1)
            {
                showVerificationCodeDialog((Activity) context);
            }
            else
            {
                errMsg = subscription.desc;
            }
        }
        else {
            errMsg = context.getString(R.string.error_server_down);
        }

        if(!errMsg.equals("Error"))
        {

            if(errMsg.equals("-1"))
            {
                errMsg = "Something went wrong. Please make sure you entered a valid number";
            }

            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}