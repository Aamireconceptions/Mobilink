package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.DealGridOnItemClickListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SnackBarUtils;
import com.ooredoo.bizstore.views.HeaderGridView;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SHOPPING;
import static com.ooredoo.bizstore.utils.CategoryUtils.showSubCategories;

/**
 * @author Babar
 */
public class ShoppingFragment extends Fragment implements OnFilterChangeListener,
                                                          OnDealsTaskFinishedListener,
                                                          OnSubCategorySelectedListener,
                                                          SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private List<GenericDeal> deals;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private HeaderGridView gridView;

    private boolean isCreated = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private DealGridOnItemClickListener dealGridOnItemClickListener;

    private boolean isRefreshed = false;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping, container, false);

        init(v, inflater);

        isCreated = true;

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(View v, LayoutInflater inflater) {
        activity = (HomeActivity) getActivity();

        snackBarUtils = new SnackBarUtils(activity, v);

        deals = new ArrayList<>();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.shopping_gridview, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);

       // rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_shopping_header, null);

        adapter = new GridViewBaseAdapter(activity, R.layout.grid_generic, deals);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        gridView = (HeaderGridView) v.findViewById(R.id.shopping_gridview);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            if(BizStore.getLanguage().equals("ar"))
                gridView.setHorizontalSpacing((int) -activity.getResources().getDimension(R.dimen._6sdp));
            else
                gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._6sdp));
        }

        gridView.addHeaderView(rlHeader);
       // gridView.setEmptyView(tvEmptyView);
        dealGridOnItemClickListener = new DealGridOnItemClickListener(activity, adapter, this);
        gridView.setOnItemClickListener(dealGridOnItemClickListener);
        gridView.setAdapter(adapter);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            gridView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CT_SHOPPING);
        clickListener.setLayout(rlHeader);

        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);

        showSubCategories(activity, CT_SHOPPING);

        loadDeals(progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if(newConfig.getLayoutDirection() == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL)
            gridView.setHorizontalSpacing((int) -activity.getResources().getDimension(R.dimen._6sdp));
        else
            gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._6sdp));
    }



    private void loadDeals(ProgressBar progressBar)
    {
        ShoppingTask shoppingTask = new ShoppingTask(activity, adapter, progressBar, snackBarUtils, this);

        String cache = shoppingTask.getCache("shopping");

        if(cache != null && !isRefreshed)
        {
            shoppingTask.setData(cache);
        }
        else
        {
            shoppingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "shopping");
        }
    }

    public static ShoppingFragment newInstance() {
        ShoppingFragment fragment = new ShoppingFragment();

        return fragment;
    }

    @Override
    public void onFilterChange() {
        loadDeals(progressBar);
    }

    @Override
    public void onRefresh() {
        memoryCache.remove(adapter.deals);

        diskCache.remove(adapter.deals);

        isRefreshed = true;
        loadDeals(null);
        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals()
    {
        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        gridView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onSubCategorySelected()
    {
        if(!isCreated)
        {
            onFilterChange();
        }
        else
        {
            isCreated = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("is_fav", false);

            dealGridOnItemClickListener.genericDeal.isFav = isFav;

            String voucher = data.getStringExtra("voucher");

            if(voucher != null)
            {
                dealGridOnItemClickListener.genericDeal.voucher = voucher;
                dealGridOnItemClickListener.genericDeal.status = "Available";
            }

            int views = data.getIntExtra("views", -1);

            dealGridOnItemClickListener.genericDeal.views = views;

            adapter.notifyDataSetChanged();
        }
    }
}