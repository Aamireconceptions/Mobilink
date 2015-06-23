package com.ooredoo.bizstore.ui.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;

public class MyDealsActivity extends AppCompatActivity implements View.OnClickListener
{
    private View lastSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_deals);

        init();
    }

    private void init()
    {
        setupToolbar();

        Button btNewDeals = (Button) findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(this);

        Button btPopularDeals = (Button) findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(this);

        ListView listView = (ListView) findViewById(R.id.list_view);

        ListViewBaseAdapter adapter = new ListViewBaseAdapter(this, R.layout.list_deal, null);

        listView.setAdapter(adapter);
    }

    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.new_deals:

                setSelected(v);

                break;

            case R.id.popular_deals:

                setSelected(v);

                break;
        }

    }

    private void setSelected(View v)
    {
        if(lastSelected != null)
        {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

}
