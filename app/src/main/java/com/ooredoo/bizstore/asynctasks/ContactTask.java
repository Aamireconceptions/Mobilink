package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.*;

/**
 * Created by Babar on 15-Dec-15.
 */
public class ContactTask extends AsyncTask<String, Void, String>
{
    private Context context;

    EditText etHelp;

    private Dialog dialog;

    public ContactTask(Context context, EditText etHelp)
    {
        this.context = context;
        this.etHelp = etHelp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader((Activity) context, context.getString(R.string.please_wait));
        dialog.show();;;
    }

    private static final String SERVICE_NAME = "/sendfeedback?";
    @Override
    protected String doInBackground(String... params) {
        try
        {
            return sendMessage(params[0]);
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

        dialog.dismiss();

        if(result != null)
        {
            try
            {
                if(new JSONObject(result).getInt("result") == 0)
                {
                    etHelp.setText("");
                    Toast.makeText(context, R.string.thanks_contact, Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();

                Toast.makeText(context, R.string.error_server_down, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String sendMessage(String message) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("feedback", message);

        String query;

        StringBuilder stringBuilder = new StringBuilder();

        boolean isFirst = true;

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(isFirst)
            {
                isFirst = false;
            }
            else
            {
                stringBuilder.append("&");
            }

            stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        query = stringBuilder.toString();

        URL url = new URL(BASE_URL +BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("ContactUs URL:" + url);

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

                    return true;
                }
            };

            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestProperty(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
            connection.setRequestProperty(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));
            Logger.print("Username: base64: " + CryptoUtils.encodeToBase64(BizStore.username));
            Logger.print("Password: base64: " + CryptoUtils.encodeToBase64(BizStore.secret));
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
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

        InputStream is = connection.getInputStream();

        StringBuilder builder = new StringBuilder();

        BufferedReader reader = null;
        try
        {
           reader =new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }

            result = builder.toString();

            Logger.print("ContactUs: "+result);

            return result;
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
    }
}
