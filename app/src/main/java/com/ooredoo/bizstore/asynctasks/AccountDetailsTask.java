package com.ooredoo.bizstore.asynctasks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.model.UserAccount;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 29-Jul-15
 */
public class AccountDetailsTask extends BaseAsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String msisdn = params[0];
        try {
            return getAccountDetails(msisdn);
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
                UserAccount userAccount = new Gson().fromJson(result, UserAccount.class);

                if(userAccount != null) {
                    AppData.userAccount.name = userAccount.name;
                    AppData.userAccount.picture = SERVER_URL + userAccount.picture;
                }
            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get account details i.e. name & picture path
     *
     * @param msisdn
     * @return json string
     * @throws IOException
     */
    private String getAccountDetails(String msisdn) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("msisdn", msisdn);

        setServiceUrl("viewprofile", params);

        result = getJson();

        Logger.print("viewprofile: " + result);

        return result;
    }

}
