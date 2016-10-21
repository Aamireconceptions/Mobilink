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
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
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
                myAccountActivity.updateProfilePicture();
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

        String url = BASE_URL + BizStore.getLanguage() + "/changesettings?os=android";

        String result = null;

        ArrayList<NameValuePair> params = new ArrayList<>();

        String encoded_image = null;

        if(isNotNullOrEmpty(imagePath)) {
            Logger.print("Image Path: " + imagePath);

            byte[] bytes = getImageBytes(imagePath);

            encoded_image = Base64.encodeToString(bytes, Base64.DEFAULT);
            //Logger.logI("Base64 Image", encoded_image);
        }

        if(isNotNullOrEmpty(name)) {
            name = URLEncoder.encode(name, ENCODING);
            Logger.print("Name: " + name);
            url = url + "&name=" + name;
        }

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params2 = new BasicHttpParams();
            HttpProtocolParams.setVersion(params2, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params2, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params2, registry);

            HttpClient httpclient = new DefaultHttpClient(ccm, params2);


            HttpPost httpPost = new HttpPost(url);

            Logger.print("UpdateAccountTask URL:"+url);
            httpPost.setHeader(HTTP_X_USERNAME, CryptoUtils.encodeToBase64(BizStore.username));
            httpPost.setHeader(HTTP_X_PASSWORD, CryptoUtils.encodeToBase64(BizStore.secret));

            if(isNotNullOrEmpty(encoded_image)) {
                NameValuePair nameValuePair = new BasicNameValuePair("image", encoded_image);
                params.add(nameValuePair);
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params);
                httpPost.setEntity(formEntity);
            }

            HttpResponse response = httpclient.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                result = readStream(is);
            }
            Logger.print("STATUS_CODE: " + statusCode);

            return result;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] getImageBytes(String path) throws FileNotFoundException {
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

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }



        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
