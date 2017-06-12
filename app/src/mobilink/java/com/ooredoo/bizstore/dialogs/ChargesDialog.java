package com.ooredoo.bizstore.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.SubscriptionTask;

/**
 * Created by Babar on 08-Feb-17.
 */
public class ChargesDialog extends DialogFragment
{
    public static String Operator1;

    public static ChargesDialog newInstance(String msisdn,String operator,String Package, String type)
    {

        Bundle bundle = new Bundle();
        bundle.putString("msisdn", msisdn);
        bundle.putString("operator", operator);
        bundle.putString("package", Package);
        bundle.putString("billing", type);
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
 public static String msisdn, operator, description, type;
    private void init(View v)
    {
        TextView txtChargesTiltle   = (TextView) v.findViewById(R.id.charges_title);
        TextView txtChargesValue    = (TextView) v.findViewById(R.id.charges_value);
        Button btSubscribe          = (Button) v.findViewById(R.id.btn_subscribe);
        operator    = getArguments().getString("operator");
        operator.startsWith("\"");
       // operator.replaceAll("\",")
        description = getArguments().getString("package");
        type        = getArguments().getString("billing");
        if(operator.equals("Mobilink"))
        {
            if(description.equals("Prepaid"))
            {
                txtChargesTiltle.setText("Jazz Pre Paid");
                txtChargesValue.setText("Rs 8.36/week");

            }else if(description.equals("Postpaid"))
            {
                txtChargesTiltle.setText("Jazz Post Paid");
                txtChargesValue.setText("Rs 35.85/month");

            }/*else if(description.equals("champion"))
            {
                txtChargesTiltle.setText("Jazz Champion");
                txtChargesValue.setText("Rs 15.50/month");

            }else if(description.equals("nonchampion"))
            {
                txtChargesTiltle.setText("Jazz Non Champion");
                txtChargesValue.setText("Rs 15.50/month");
            }*/

        }
        else if(operator.equals("Warid"))
        {
            if(description.equals("Prepaid"))
            {
                txtChargesTiltle.setText("Warid Pre Paid");
                txtChargesValue.setText("Rs 1.20/daily");

            }else if(description.equals("postpaid"))
            {
                txtChargesTiltle.setText("Warid Pre Paid");
                txtChargesValue.setText("Rs 1.20/daily");

            }
        }
        else
        {
            txtChargesTiltle.setText("JAZZ gives you a chance to enjoy the discounts for free!");
            txtChargesValue.setVisibility(View.GONE);
        }
        btSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msisdn = getArguments().getString("msisdn");

                new SubscriptionTask(getActivity()).execute(msisdn);
            }
        });
    }

}
