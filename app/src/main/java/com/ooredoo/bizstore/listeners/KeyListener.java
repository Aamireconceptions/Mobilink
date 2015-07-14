package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Logger;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;

/**
 * @author Pehlaj Rai
 * @since 7/1/2015.
 */
public class KeyListener implements View.OnKeyListener {
    Activity activity;

    public KeyListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int viewId = v.getId();
        if(viewId == R.id.ac_search) {
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if(hasInternetConnection(activity)) {
                    //TODO implement Search
                    String keyword = ((AutoCompleteTextView) v).getText().toString();
                    Logger.print("SEARCH_KEYWORD: " + keyword);
                    //new SearchTask(activity).execute(keyword);
                } else {
                    makeText(activity.getApplicationContext(), activity.getString(R.string.error_no_internet), LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }
}
