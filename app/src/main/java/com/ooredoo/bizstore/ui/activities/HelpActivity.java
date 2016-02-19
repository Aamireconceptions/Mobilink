package com.ooredoo.bizstore.ui.activities;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ContactTask;
import com.ooredoo.bizstore.utils.FontUtils;

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

        TextView tvQ1 = (TextView) findViewById(R.id.q1);
        FontUtils.setFontWithStyle(this, tvQ1, Typeface.BOLD);

        TextView tvQ2 = (TextView) findViewById(R.id.q2);
        FontUtils.setFontWithStyle(this, tvQ2, Typeface.BOLD);

        TextView tvQ3 = (TextView) findViewById(R.id.q3);
        FontUtils.setFontWithStyle(this, tvQ3, Typeface.BOLD);

        TextView tvQ4 = (TextView) findViewById(R.id.q4);
        FontUtils.setFontWithStyle(this, tvQ4, Typeface.BOLD);

        TextView tvQ5 = (TextView) findViewById(R.id.q5);
        FontUtils.setFontWithStyle(this, tvQ5, Typeface.BOLD);

        TextView tvYourFeedback = (TextView) findViewById(R.id.your_feedback);
        FontUtils.setFontWithStyle(this, tvYourFeedback, Typeface.BOLD);


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