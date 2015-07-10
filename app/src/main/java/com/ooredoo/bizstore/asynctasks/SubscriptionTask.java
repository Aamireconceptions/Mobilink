package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.model.Subscription;
import com.ooredoo.bizstore.ui.fragments.SignUpFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 09-Jul-15
 */
public class SubscriptionTask extends BaseAsyncTask<String, Void, String> {

    private SignUpFragment signUpFragment;

    public SubscriptionTask(SignUpFragment signUpFragment) {
        this.signUpFragment = signUpFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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

        if(result != null) {
            try {
                Subscription subscription = new Gson().fromJson(result, Subscription.class);
                signUpFragment.processSubscription(subscription);
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
        params.put(OS, ANDROID);
        params.put("msisdn", msisdn);

        setServiceUrl("subscribe", params);

        result = getJson();

        Logger.print("Subscribe: " + result);

        return result;
    }

}
