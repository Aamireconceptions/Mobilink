package com.ooredoo.bizstore.asynctasks;

import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 03-Jul-15
 */
public class UpdateAccountTask extends BaseAsyncTask<Void, Void, String> {

    private static final String LOG_TAG = "UpdateAccountTask";

    private MyAccountActivity myAccountActivity;

    private String name, pathProfilePic;

    ProgressBar progressBar;

    public UpdateAccountTask(MyAccountActivity accountActivity, String name, String pathProfilePic) {

        this.name = name;
        this.pathProfilePic = pathProfilePic;
        this.myAccountActivity = accountActivity;
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        progressBar = (ProgressBar) myAccountActivity.findViewById(R.id.progress_bar);
    }

    @Override
    protected void onPreExecute() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        super.onPreExecute();
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return updateSettings(name, pathProfilePic);
        } catch(IOException e) {
            e.printStackTrace();

            myAccountActivity.showMessage();

        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if(isNotNullOrEmpty(result)) {
            try {
                Logger.print("RESULT: " + result);
                JSONObject response = new JSONObject(result);
                int resultCode = response.has("result") ? response.getInt("result") : -1;
                if(resultCode == 0) {

                    if(response.has("name"))
                    {
                        AppData.userAccount.name = response.getString("name");
                    }


                    if(HomeActivity.tvName != null && !isNullOrEmpty(AppData.userAccount.name))
                    {
                        HomeActivity.tvName.setText(AppData.userAccount.name);
                    }

                }
                String path = response.has("image") ? response.getString("image") : "";
                Logger.logI("UPLOADED_IMG_PATH", path);
                /*if(pathProfilePic != null) {
                    myAccountActivity.updateProfilePicture();
                }*/
                if(path != null && !path.isEmpty()) {
                    myAccountActivity.updateProfilePicture();
                }
            } catch(JSONException jse) {
                jse.printStackTrace();
            }

            Logger.logI(LOG_TAG, isNotNullOrEmpty(result) ? result : "ERR_UPLOADING_PICTURE");
        }
        else
        {
            Toast.makeText(myAccountActivity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param name
     * @param imagePath
     * @return json string
     * @throws IOException
     */
    private String updateSettings(String name, String imagePath) throws IOException {

        String baseUrl = BASE_URL + BizStore.getLanguage() + "/changesettings?os=android";

        String result = null;

        String encoded_image;

        HashMap<String, String> params = new HashMap<>();

        String query = null;
        if(isNotNullOrEmpty(imagePath)) {
            Logger.print("Image Path: " + imagePath);

            byte[] bytes = getImageBytes(imagePath);

            encoded_image = Base64.encodeToString(bytes, Base64.DEFAULT);

            params.put("image", encoded_image);

            query = createQuery(params);
        }

        if(isNotNullOrEmpty(name)) {
            name = URLEncoder.encode(name, ENCODING);
            Logger.print("Name: " + name);
            baseUrl = baseUrl + "&name=" + name;

        }

        HttpsURLConnection connection;

        URL url = new URL(baseUrl);

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream is = BizStore.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(is);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            } finally {
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

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostName, SSLSession sslSession) {

                    return true;
                }
            };

            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestProperty(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
            connection.setRequestProperty(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));

            Logger.print("Rula: user" + CryptoUtils.encodeToBase64(BizStore.username));
            Logger.print("Rula: password" + CryptoUtils.encodeToBase64(BizStore.secret));
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if(query != null) {
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
            }

            Logger.print("UpdateAccountTask URL:" + url);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            result = readStream(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return result;
    }

    private byte[] getImageBytes(String path) throws FileNotFoundException
    {
        File fileName = new File(path);
        InputStream inputStream = new FileInputStream(fileName);
        byte[] bytes;
        byte[] buffer = new byte[512];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        return bytes;
    }
}