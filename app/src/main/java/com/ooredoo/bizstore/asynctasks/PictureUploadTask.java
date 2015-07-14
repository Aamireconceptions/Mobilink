package com.ooredoo.bizstore.asynctasks;

import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;
import com.ooredoo.bizstore.utils.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 03-Jul-15
 */
public class PictureUploadTask extends BaseAsyncTask<Void, Void, String> {

    private static final String LOG_TAG = "PictureUploadTask";

    private MyAccountActivity myAccountActivity;

    Bitmap profilePicBitmap;

    public static final String IMAGE_UPLOAD_URL = "http://203.215.183.98:10009/ooredoo/index.php/api/en/uploadpic?os=android";

    private String pathProfilePic;

    public PictureUploadTask(MyAccountActivity accountActivity, String pathProfilePic) {
        this.pathProfilePic = pathProfilePic;
        this.myAccountActivity = accountActivity;
        profilePicBitmap = MyAccountActivity.decodeFile(pathProfilePic);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return uploadPictureToServer();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        myAccountActivity.findViewById(R.id.progress_bar).setVisibility(View.GONE);

        if(isNotNullOrEmpty(result)) {
            try {
                Logger.print("RESULT: " + result);
                JSONObject response = new JSONObject(result);
                String path = response.has("image") ? response.getString("image") : "";
                Logger.logI("UPLOADED_IMG_PATH", path);
                myAccountActivity.updateProfilePicture(path);
            } catch(JSONException jse) {
                jse.printStackTrace();
            }

            Logger.logI(LOG_TAG, isNotNullOrEmpty(result) ? result : "ERR_UPLOADING_PICTURE");
        }
    }

    private String uploadPictureToServer() throws IOException {

        String result = null;
        byte[] bytes = getImageBytes(pathProfilePic);
        String encoded_image = Base64.encodeToString(bytes, Base64.DEFAULT);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("image", encoded_image));

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(IMAGE_UPLOAD_URL);
        httppost.setHeader(HTTP_X_USERNAME, BizStore.username);
        httppost.setHeader(HTTP_X_PASSWORD, BizStore.password);

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);

        StatusLine statusLine = response.getStatusLine();

        int statusCode = statusLine.getStatusCode();
        if(statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            result = readStream(is);
        }
        Logger.print("STATUS_CODE: " + statusCode);

        return result;
    }

    private byte[] getImageBytes(String path) throws FileNotFoundException {
        File fileName = new File(path);
        InputStream inputStream = new FileInputStream(fileName);
        byte[] bytes;
        byte[] buffer = new byte[8192];
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
