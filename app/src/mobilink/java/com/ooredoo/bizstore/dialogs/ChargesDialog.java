package com.ooredoo.bizstore.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private void init(View v)
    {
        Button btSubscribe = (Button) v.findViewById(R.id.btn_subscribe);
        btSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msisdn = getArguments().getString("msisdn");

                new SubscriptionTask(getActivity()).execute(msisdn);
            }
        });
    }

}
