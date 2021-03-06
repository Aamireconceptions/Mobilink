package com.ooredoo.bizstore.asynctasks;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.GcmPreferences;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Babar on 31-Jul-15.
 */
public class GCMRegisterTask extends BaseAsyncTask<Void, Void, String>
{
    private Context context;

    private GcmPreferences gcmPreferences;

    private String serviceName = "/setgcmtoken?";

    private String token;

    public GCMRegisterTask(Context context, GcmPreferences gcmPreferences)
    {
        this.context = context;

        this.gcmPreferences = gcmPreferences;
    }
    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            return registerWithGCM();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        if(result != null)
        {
            Logger.print("json:"+ result);

            Gson gson = new Gson();

            try
            {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    Logger.print("Description: " + response.desc);

                    String userToken = BizStore.username + "_" + token;

                    Logger.print("userToken " + userToken);

                    gcmPreferences.saveUserGCMToken(userToken, userToken);
                }
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String registerWithGCM() throws IOException
    {
        token = gcmPreferences.getDeviceGCMToken();

        if(token == null)
        {
            InstanceID instanceID = InstanceID.getInstance(context);

            token = instanceID.getToken(context.getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Logger.print("GCM Token:" + token);

            gcmPreferences.setDeviceGCMToken(token);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(GCM_TOKEN, token);

        String query = createQuery(params);

        URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + serviceName + query);

        Logger.print("GCM URL: "+url);

        String json = getJson(url);

        return json;
    }
}