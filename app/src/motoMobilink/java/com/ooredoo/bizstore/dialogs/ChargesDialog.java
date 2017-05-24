package com.ooredoo.bizstore.dialogs;

import android.Manifest;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.SubscriptionTask;

/**
 * Created by Babar on 08-Feb-17.
 */
public class ChargesDialog extends DialogFragment
{
    public static ChargesDialog newInstance(String msisdn)
    {

        Bundle bundle = new Bundle();
        bundle.putString("msisdn", msisdn);

        ChargesDialog dialog = new ChargesDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ask_subscribe, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        init(v);

        return v;
    }
    public static String msisdn;
    private int receiveSmsRequestCode = 1;
    private void init(View v)
    {
        Button btSubscribe = (Button) v.findViewById(R.id.btn_subscribe);
        btSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECEIVE_SMS}, receiveSmsRequestCode);
                }

                msisdn = getArguments().getString("msisdn");

                new SubscriptionTask(getActivity()).execute(msisdn);
            }
        });
    }

}
