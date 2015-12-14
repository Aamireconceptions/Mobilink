package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Babar on 11-Dec-15.
 */
public class CalculateDistanceTask extends AsyncTask<String, Void, String>
{
    private GenericDeal genericDeal;

    private BaseAdapter adapter;

    private Context context;

    private GenericDeal deal;

    private Business business;

    private TextView tvDistance, tvDirections;

    private String MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    public CalculateDistanceTask(GenericDeal genericDeal, BaseAdapter adapter)
    {
        this.genericDeal = genericDeal;

        this.adapter = adapter;
    }

    RelativeLayout rlDistance; LinearLayout llDirections;

    public CalculateDistanceTask(Context context, Business business, TextView tvDistance,
                                 TextView tvDirections, RelativeLayout rlDistance, LinearLayout llDirections)
    {
        this.context = context;

        this.business = business;

        this.tvDistance = tvDistance;

        this.tvDirections = tvDirections;

        this.rlDistance = rlDistance;

        this.llDirections = llDirections;
    }

    public CalculateDistanceTask(Context context, GenericDeal deal, TextView tvDistance,
                                 TextView tvDirections, RelativeLayout rlDistance, LinearLayout llDirections)
    {
        this.context = context;

        this.deal = deal;

        this.tvDistance = tvDistance;

        this.tvDirections = tvDirections;

        this.rlDistance = rlDistance;

        this.llDirections = llDirections;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getDistance(params[0], params[1]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray rows = jsonObject.getJSONArray("rows");

                JSONObject rowObj = rows.getJSONObject(0);

                JSONArray elements = rowObj.getJSONArray("elements");

                JSONObject distanceObj = elements.getJSONObject(0).getJSONObject("distance");

                double distance = distanceObj.getDouble("value");

                Logger.print("Distance = "+distance / 1000 + " km");

                if(genericDeal!= null){genericDeal.distance = distance;}

                if(business != null)
                {
                    business.distance = distance;

                    tvDirections.setText(String.format("%.1f", distance / 1000) + " " +context.getString(R.string.km));
                    llDirections.setVisibility(View.VISIBLE);

                    tvDistance.setText(String.format("%.1f", distance / 1000) + " " + context.getString(R.string.km_away));
                    rlDistance.setVisibility(View.VISIBLE);
                }

                if(deal != null)
                {
                    deal.distance = distance;

                    tvDirections.setText(String.format("%.1f", distance / 1000) + " " +context.getString(R.string.km));
                    llDirections.setVisibility(View.VISIBLE);

                    tvDistance.setText(String.format("%.1f", distance / 1000) + " " + context.getString(R.string.km_away));
                    rlDistance.setVisibility(View.VISIBLE);

                }



                if(adapter != null){adapter.notifyDataSetChanged();}

            }
            catch (JSONException e)
            {
                e.printStackTrace();

                if(rlDistance != null) rlDistance.setVisibility(View.GONE);
                if(llDirections != null) llDirections.setVisibility(View.GONE);
            }
        }
    }

    private String getDistance(String origin, String destination) throws IOException {

        String result = null;

        MATRIX_API_URL += "origins=" + origin + "&destinations=" + destination+ "&key=AIzaSyAdokQj3OtS2jJ_Xq2vJD5hMEpmARrMaaU";

        Logger.print("Distance URL:" + MATRIX_API_URL);

        URL url = new URL(MATRIX_API_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream inputStream = connection.getInputStream();

        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            result = stringBuilder.toString();

            Logger.print("Result:"+result);
        }
        finally
        {
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
        }

        return result;
    }
}
