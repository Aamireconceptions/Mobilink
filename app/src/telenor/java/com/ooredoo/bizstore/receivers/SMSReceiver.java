package com.ooredoo.bizstore.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;


/** Use for auto reading the pincode from the inbox
 * Created by Babar on 06-Oct-15.
 */
public class SMSReceiver extends BroadcastReceiver
{

    private final static String SERVICE_NUM = "5005";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        {
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs;

            String msgFrom;

            if(bundle != null)
            {
                try
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    msgs = new SmsMessage[pdus.length];

                    for(int i = 0; i < msgs.length; i++)
                    {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                        msgFrom = msgs[i].getOriginatingAddress();

                        if(msgFrom.equals(SERVICE_NUM))
                        {
                            String msgBody = msgs[i].getMessageBody();

                            Logger.print("SMS RECEIVED: " + "FROM: " + msgFrom + ", MSG: " + msgBody);

                            if (msgBody.contains("Password for")) {
                                String code = msgBody.substring(msgBody.length() - 6);

                                BizStore.password = code;

                                DialogUtils.etCode.setText(code);
                                DialogUtils.etCode.setSelection(code.length());
                                DialogUtils.progressBar.setVisibility(View.GONE);

                                DialogUtils.processVerificationCode();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}