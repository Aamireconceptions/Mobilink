package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FavoritesAdapter;
import com.ooredoo.bizstore.listeners.FavoritesFilterOnClickListener;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.utils.FontUtils;

import java.util.List;

public class MyFavoritesActivity extends AppCompatActivity {

    public int isBusiness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_deals);

        init();
    }

    private void init() {

        setupToolbar();

        FavoritesFilterOnClickListener listener = new FavoritesFilterOnClickListener(this);
        Button btNewDeals = (Button) findViewById(R.id.deals);
        btNewDeals.setOnClickListener(listener);
        listener.setButtonSelected(btNewDeals);
        FontUtils.setFont(this,  btNewDeals);

        Button btPopularDeals = (Button) findViewById(R.id.businesses);
        btPopularDeals.setOnClickListener(listener);

        FontUtils.setFont(this,  btPopularDeals);

        showFavs();
    }

    FavoritesAdapter adapter;
    List<Favorite> favorites;
    public void showFavs()
    {
        Select select = new Select();

       favorites = select.all().from(Favorite.class).where("isFavorite = 1 AND isBusiness = "+isBusiness)
                .orderBy("id DESC").execute();

        //FavoritesAdapter adapter = new FavoritesAdapter(this, R.layout.favorite_item, favorites);
        adapter = new FavoritesAdapter(this, R.layout.list_deal_promotional, favorites);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        toggleEmptyView(favorites.size());
    }

    public void toggleEmptyView(int count) {
        boolean isEmpty = count == 0;
        findViewById(R.id.list_view).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.no_data_view).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        String msg = getString(R.string.no_fav_item);
        ((TextView) findViewById(R.id.tv_no_data)).setText(msg);
        ((ImageView) findViewById(R.id.ic_no_data)).setImageResource(R.drawable.ic_fav_deals);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.my_favorites));
    }

    MenuItem miClear;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(favorites.size() > 0)
        {
            getMenuInflater().inflate(R.menu.menu_recent_viewed, menu);

            miClear = menu.findItem(R.id.clear);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clear:

                Delete delete = new Delete();
                delete.from(Favorite.class).execute();

                miClear.setVisible(false);

                adapter.clear();

                toggleEmptyView(favorites.size());

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}