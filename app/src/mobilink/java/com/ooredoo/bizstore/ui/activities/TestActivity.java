package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.widget.TextView;

import com.ooredoo.bizstore.R;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Babar on 21-Mar-17.
 */

@EActivity(R.layout.test)
public class TestActivity extends Activity
{
    @ViewById(R.id.testView)
    TextView textView;
}
