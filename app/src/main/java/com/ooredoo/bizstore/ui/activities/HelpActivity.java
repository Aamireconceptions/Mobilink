package com.ooredoo.bizstore.ui.activities;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ContactTask;
import com.ooredoo.bizstore.utils.FontUtils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
@EActivity
public class HelpActivity extends BaseActivity {

    ActionBar mActionBar;
    public HelpActivity() {
        super();
        layoutResId = R.layout.activity_help;
    }

    @ViewById(R.id.your_message)
    EditText etHelp;

    @ViewById(R.id.q1)
    TextView tvQ1;

    @ViewById(R.id.q2)
    TextView tvQ2;

    @ViewById(R.id.q3)
    TextView tvQ3;

    @ViewById(R.id.q4)
    TextView tvQ4;

    @ViewById(R.id.q5)
    TextView tvQ5;

    @ViewById(R.id.q6)
    TextView tvQ6;

    @ViewById(R.id.your_feedback)
    TextView tvYourFeedback;

    @Override
    public void init() {
        setupToolbar();

        FontUtils.setFontWithStyle(this, etHelp, Typeface.NORMAL);
        FontUtils.setFontWithStyle(this, tvQ1, Typeface.BOLD);
        FontUtils.setFontWithStyle(this, tvQ2, Typeface.BOLD);
        FontUtils.setFontWithStyle(this, tvQ3, Typeface.BOLD);
        FontUtils.setFontWithStyle(this, tvQ4, Typeface.BOLD);
        FontUtils.setFontWithStyle(this, tvQ5, Typeface.BOLD);
        FontUtils.setFontWithStyle(this, tvQ6, Typeface.BOLD);
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

    @ViewById
    Toolbar toolbar;
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
        mActionBar.setTitle(getString(R.string.help));
    }
}