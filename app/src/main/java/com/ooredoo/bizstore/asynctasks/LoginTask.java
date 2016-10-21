package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class LoginTask extends BaseAsyncTask<Void, Void, String> {

    private TopBrandsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private Dialog dialog;

    private final static String SERVICE_URL= "/login?";
    private Context activity;

    public LoginTask(Context activity) {

        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(BuildConfig.FLAVOR.equals("dealionare") || BuildConfig.FLAVOR.equals("ufone")
                || BuildConfig.FLAVOR.equals("mobilink")) {
            dialog = DialogUtils.createCustomLoader((Activity) activity, "Logging In....");
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return login();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

       if(dialog != null)
           dialog.dismiss();

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if(response.resultCode != -1)
            {
                if(response.resultCode == 0)
                {
                    if(response.desc.equals("Invalid Pincode"))
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.invalid_password).show();

                        return;
                    }
                }
                if(response.resultCode == 500)
                {
                    DialogUtils.createAlertDialog(activity, 0, R.string.error_server_down).show();

                    return;
                }

                if(response.desc.equals("Logged in successfully"))
                {
                    if(BuildConfig.FLAVOR.equals("ooredoo"))
                    {
                        BizStore.password = CryptoUtils.encryptToAES(BizStore.password);
                    }

                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
                    sharedPrefUtils.updateVal((Activity) activity, "username", BizStore.username);
                    sharedPrefUtils.updateVal((Activity) activity, "password", BizStore.password);

                    if(BuildConfig.FLAVOR.equals("mobilink"))
                    {
                        response.apiToken = BizStore.password;
                    }

                    sharedPrefUtils.updateVal((Activity) activity, "secret", response.apiToken);

                    DialogUtils.activity = (Activity) activity;

                    DialogUtils.startWelcomeFragment();
                }
                else
                if(response.desc.equals("password msg is sent to user"))
                {
                    BizStore.password = CryptoUtils.encryptToAES(response.password);

                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
                    sharedPrefUtils.updateVal((Activity) activity, "username", BizStore.username);
                    sharedPrefUtils.updateVal((Activity) activity, "password", BizStore.password);

                    DialogUtils.activity = (Activity) activity;

                    DialogUtils.startWelcomeFragment();
                }

                if(response.resultCode == 4)
                {
                    if(response.desc.equals("Not Billed"))
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.error_insufficient_balance).show();
                    }
                }
                else
                    if(response.resultCode == 1)
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.error_signin_failure_ufone).show();
                    }
            }
            else
            {
                DialogUtils.dismissPasswordDialog();

                DialogUtils.createAlertDialog(activity, 0, 0).show();
            }
        }
        else
        {
            Toast.makeText(activity, "Please make sure you are connected to internet and try again!", Toast.LENGTH_LONG).show();
        }
    }

    private String login() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        if(!BuildConfig.FLAVOR.equals("dealionare")) {
            params.put("pincode",  BizStore.password);
        }

        String query = createQuery(params);

       // URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_URL + query);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_URL + query);




/*
        if(BuildConfig.FLAVOR.equals("ufone"))
        {
            params.clear();
            params.put(MSISDN, BizStore.username);
            params.put("password", "ZwRq5CsY96w3zCMD");

            query = createQuery(params);

            url = new URL("http://203.215.183.98:30119/yellowPages2/mobileAppSupport"
            + SERVICE_URL + query);
        }*/

        Logger.print("login() URL:" + url.toString());

        result = getJson(url, query);

        Logger.print("Login result:" + result);

        return result;
    }


    public HttpsURLConnection openConnectionAndConnect(URL url, String query) throws IOException{
        /*String credentials = BizStore.username + ":" + BizStore.password;

        String basicAuth = "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));*/

        HttpsURLConnection connection = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream is = BizStore.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try
            {
                ca = cf.generateCertificate(is);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            }
            finally
            {
                is.close();
            }

            String keystoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            // Initialise the TMF as you normally would, for example:
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier hostnameVerifier = new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostName, SSLSession sslSession)
                {
                    /*HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    Logger.print("Https Hostname: "+hostName);

                    return hv.verify(s, sslSession);*/

                    return true;
                }
            };

            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestProperty(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
            connection.setRequestProperty(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
           // connection.setDoOutput(true);

            /*OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();*/

            connection.connect();
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        }

        return connection;
    }

    public String getJson(URL url, String query) throws IOException
    {
        String result;

        HttpURLConnection connection = openConnectionAndConnect(url, query);

        InputStream inputStream = connection.getInputStream();

        result = readStream(inputStream);

        return result;
    }
}