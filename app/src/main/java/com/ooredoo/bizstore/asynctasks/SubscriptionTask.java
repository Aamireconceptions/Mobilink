package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Subscription;
import com.ooredoo.bizstore.ui.fragments.SignUpFragment;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 09-Jul-15
 */
public class SubscriptionTask extends BaseAsyncTask<String, Void, String> {

    private SignUpFragment signUpFragment;

    private String SERVICE_NAME = "http://203.215.183.98:10041/yellowPages/subscriberIdentifier/subscribe?";

    public SubscriptionTask(SignUpFragment signUpFragment) {
        this.signUpFragment = signUpFragment;
    }

    Dialog dialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader(signUpFragment.getActivity(),
                signUpFragment.getActivity().getString(R.string.subscribing));
        dialog.show();;
    }

    @Override
    protected String doInBackground(String... params) {
        String msisdn = params[0];
        try {
            return subscribe(msisdn);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        dialog.dismiss();

        if(result != null) {
            try {
                Subscription subscription = new Gson().fromJson(result, Subscription.class);

                signUpFragment.processSubscription(subscription);

                DialogUtils.processVerificationCode();

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Subscribe user, return verification code/password
     *
     * @param msisdn
     * @return verification_code/password
     * @throws IOException
     */
    private String subscribe(String msisdn) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
       // params.put(OS, ANDROID);
        params.put("msisdn", msisdn);
        params.put("password", "A33w3zH2OsCMD");

        String query = createQuery(params);

        URL url = new URL(SERVICE_NAME + query);

        Logger.print("subscribe URL:" + url.toString());

        result = getJson(url);

       // result = getJson();

        Logger.print("Subscribe: " + result);

        return result;
    }

}
