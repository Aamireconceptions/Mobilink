package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ShareAppTask;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SnackBarUtils;

public class ShareAppActivity extends AppCompatActivity {
    private EditText etPhoneNum;

    private SnackBarUtils snackBarUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_app);

        setupToolbar();

        init();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.share_app_with_friends));
    }

    private void init() {

        EditText etCountry = (EditText) findViewById(R.id.country_code);
        //etCountry.setText("974");
       FontUtils.setFont(this, BizStore.DEFAULT_FONT, etCountry);

        etPhoneNum = (EditText) findViewById(R.id.phone_number);
        etPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

                startActivityForResult(pickContactIntent, 101);
            }
        });

        snackBarUtils = new SnackBarUtils(this, etPhoneNum);
    }

    public void shareApp(View v) {
        String phoneNum = etPhoneNum.getText().toString().trim();

        if(!phoneNum.isEmpty() && phoneNum.length() >= AppConstant.MSISDN_MIN_LEN) {
            phoneNum = "+974" + phoneNum;

            ShareAppTask shareAppTask = new ShareAppTask(this, snackBarUtils);
            shareAppTask.execute(phoneNum);
        } else {
            snackBarUtils.showSimple(R.string.error_provide_num, Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101)
        {
            if(resultCode == RESULT_OK)
            {
                Uri contactUri = data.getData();

                String projection[] = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String number = cursor.getString(column);

                etPhoneNum.setText(number);
                etPhoneNum.setSelection(number.length());
            }

        }
    }
}