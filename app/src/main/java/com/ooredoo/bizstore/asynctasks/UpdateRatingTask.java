package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.widget.RatingBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 03-Jul-15
 */
public class UpdateRatingTask extends BaseAsyncTask<Void, Void, String> {

    private static final String LOG_TAG = "UpdateRatingTask";

    private Activity mActivity;

    private String type;
    private long typeId;
    private float rating;

    public UpdateRatingTask(Activity activity, String type, long typeId, float rating) {
        this.type = type;
        this.typeId = typeId;
        this.rating = rating;
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return updateRating();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        float rating = 0;
        try {
            JSONObject response = new JSONObject(result);
            rating = (float) (response.has("rating") ? response.getDouble("rating") : rating);
            Logger.logI("RATING", String.valueOf(rating));
        } catch(JSONException jse) {
            jse.printStackTrace();
        }

        ((RatingBar) mActivity.findViewById(R.id.rating_bar)).setRating(rating);
        Logger.logI(LOG_TAG, isNotNullOrEmpty(result) ? result : "ERR_UPDATING_RATING");
    }

    private String updateRating() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();

        params.put("type", type);
        params.put("id", String.valueOf(typeId));
        params.put("rating", String.valueOf(rating));

        setServiceUrl("rate", params);

        result = getJson();

        Logger.print(LOG_TAG + result);

        return result;
    }

}
