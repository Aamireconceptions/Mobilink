package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TestAdapter;
import com.ooredoo.bizstore.asynctasks.TestDealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TestTopDealsFragment extends Fragment implements OnFilterChangeListener,
                                                          OnDealsTaskFinishedListener,
                                                          OnSubCategorySelectedListener,
                                                          SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private TestAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private RecyclerView recyclerView;

    private boolean isCreated = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    public static TestTopDealsFragment newInstance() {
        TestTopDealsFragment fragment = new TestTopDealsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_test, container, false);

        init(v);

        loadTopDeals();

        isCreated = true;

        return v;
    }

    private void init(View v)
    {
        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);

        ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_TOP);
        clickListener.setLayout(v);

        List<GenericDeal> deals = new ArrayList<>();

       /* adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals);
        adapter.setCategory(ResourceUtils.TOP_DEALS);*/

        adapter = new TestAdapter(activity, R.layout.list_deal_promotional, deals);


        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        recyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        recyclerView.setAdapter(adapter);
      /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            recyclerView.setNestedScrollingEnabled(true);
        }*/

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void loadTopDeals()
    {
        /*DealsTask dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);
        dealsTask.execute("top_deals");*/

        TestDealsTask testDealsTask = new TestDealsTask(activity, adapter, progressBar, ivBanner, this);
        testDealsTask.execute("top_deals");
    }

    @Override
    public void onFilterChange() {
        loadTopDeals();
    }

    @Override
    public void filterTagUpdate() {

    }

    @Override
    public void onRefresh() {
        loadTopDeals();
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.top_deals_banner); //TODO replace banner (electronics->top)

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        //listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onSubCategorySelected() {
        if(!isCreated) {
            onFilterChange();
        } else {
            isCreated = false;
        }
    }
}