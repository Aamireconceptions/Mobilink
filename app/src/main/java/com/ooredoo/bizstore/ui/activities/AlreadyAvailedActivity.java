package com.ooredoo.bizstore.ui.activities;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.LoginTask;
import com.ooredoo.bizstore.utils.FontUtils;

public class AlreadyAvailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  getSupportActionBar().hide();

        setContentView(R.layout.activity_already_availed);

        FontUtils.setFontWithStyle(this, (TextView) findViewById(R.id.user), Typeface.BOLD);
        FontUtils.setFontWithStyle(this, (TextView) findViewById(R.id.info), Typeface.BOLD);
    }

    public void subscribeNow(View v)
    {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
