package com.ooredoo.bizstore.asynctasks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Subscription;
import com.ooredoo.bizstore.ui.activities.AlreadyAvailedActivity;
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
public class FOCTask extends BaseAsyncTask<String, Void, String> {

    private SignUpFragment signUpFragment;

    private String SERVICE_NAME = "/checkfoc?";

    public FOCTask(SignUpFragment signUpFragment) {
        this.signUpFragment = signUpFragment;

        this.context = signUpFragment.getActivity();
    }

    Dialog dialog;

    Context context;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader(signUpFragment.getActivity(),
                signUpFragment.getActivity().getString(R.string.please_wait));
        dialog.show();;
    }

    String msisdn;
    @Override
    protected String doInBackground(String... params) {
        msisdn = params[0];
        try {
            return checkFoc(msisdn);
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

            if(true) {
                signUpFragment.checkForFOC = false;
                new SubscriptionTask(signUpFragment).execute(msisdn);
return;
            }

            try {


                Subscription subscription = new Gson().fromJson(result, Subscription.class);

                if(subscription.resultCode == 1 && subscription.desc.equals("In-valid User"))
                {
                    Toast.makeText(context, context.getString(R.string.error_invalid_num), Toast.LENGTH_SHORT).show();
                }
                else
                if( subscription.resultCode == 0)
                {signUpFragment.checkForFOC = false;
                    new SubscriptionTask(signUpFragment).execute(msisdn);
                }
                else
                {
                    signUpFragment.checkForFOC = false;
                    context.startActivity(new Intent(context, AlreadyAvailedActivity.class));
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();

                Toast.makeText(context, context.getString(R.string.error_server_down), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, context.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Subscribe user, return verification code/password
     *
     * @param msisdn
     * @return verification_code/password
     * @throws IOException
     */
    private String checkFoc(String msisdn) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
       params.put(OS, ANDROID);
        params.put("msisdn", msisdn);
       // params.put("password", "A33w3zH2OsCMD");

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("check FOC URL:" + url.toString());

        result = getJson(url);

       // result = getJson();

        Logger.print("FOC: " + result);

        return result;
    }

}
