package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.text.InputType;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.model.UserAccount;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Pehlaj Rai
 * @since 29-Jul-15
 */
public class AccountDetailsTask extends BaseAsyncTask<String, Void, String> {

    private final static String SERVICE_NAME = "/viewprofile?";

    TextView tvName;

    Context context;

    public AccountDetailsTask(TextView tvName, Context context) {
        super();
        this.context = context;
        this.tvName = tvName;
    }

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

                    tvName.setText(userAccount.name);
                    //tvName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
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

        String query = createQuery(params);

        URL url =  new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getAccountDetails() URL:" + url.toString());

        result = getJson(url);
       // setServiceUrl("viewprofile", params);

       // result = getJson();

        Logger.print("viewprofile result: " + result);

        return result;
    }

}
