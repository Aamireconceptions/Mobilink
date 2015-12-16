package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ContactTask;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class HelpActivity extends BaseActivity {

    ActionBar mActionBar;
    public HelpActivity() {
        super();
        layoutResId = R.layout.activity_help;
    }

    EditText etHelp;

    @Override
    public void init() {
        setupToolbar();

        etHelp = (EditText) findViewById(R.id.your_message);

    }

    public void send(View v)
    {
        String message = etHelp.getText().toString().trim();

        if(!message.isEmpty())
        {
            ContactTask contactTask = new ContactTask(this, etHelp);
            contactTask.execute(message);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
        mActionBar.setTitle(getString(R.string.help));
    }
}