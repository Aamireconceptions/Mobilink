package com.ooredoo.bizstore.dialogs;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.CheckOperatorTask;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.NetworkUtils;

import static com.ooredoo.bizstore.AppConstant.MSISDN_MIN_LEN;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * Created by Babar on 08-Feb-17.
 */
public class MsisdnDialog extends DialogFragment implements View.OnClickListener
{

    public static MsisdnDialog newInstance()
    {
        MsisdnDialog dialog = new MsisdnDialog();

        return dialog;
    }

    EditText etMsisdn;

    public boolean login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        init(v);

        return v;
    }

    public void init(View parent) {

        BizStore.secret = CryptoUtils.key;

        EditText etCountryCode = (EditText) parent.findViewById(R.id.et_country_code);
        FontUtils.setFont(getActivity(), etCountryCode);

        etMsisdn = (EditText) parent.findViewById(R.id.et_phone_num);
        etMsisdn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 && s.toString().charAt(0) == '0')
                {
                    String subString = s.toString().substring(1);

                    etMsisdn.setText(subString);
                }
            }
        });

        Button btNext = (Button) parent.findViewById(R.id.btn_next);
        btNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        checkOperator(v);
    }

    public static ChargesDialog chargesDialog;
    public void checkOperator(View v)
    {
        String msisdn = etMsisdn.getText().toString();

        String errMsg = "Error";
        if(NetworkUtils.hasInternetConnection(getActivity())) {
            if(isNotNullOrEmpty(msisdn) && msisdn.length() >= MSISDN_MIN_LEN) {

                CheckOperatorTask checkOperatorTask = new CheckOperatorTask(DealDetailActivity.context,MsisdnDialog.newInstance());
                checkOperatorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,msisdn);

//                chargesDialog = ChargesDialog.newInstance(msisdn);
//                chargesDialog.show(getFragmentManager(), null);

            } else {
                errMsg = getString(R.string.error_invalid_num);
            }
        } else {
            errMsg = getString(R.string.error_no_internet);
        }

        if(!errMsg.equals("Error")) {
           Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}