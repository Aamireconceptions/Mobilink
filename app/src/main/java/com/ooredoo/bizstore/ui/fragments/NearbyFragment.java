package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Marker;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.NearbyFilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyFragment extends Fragment implements OnFilterChangeListener,
                                                               OnDealsTaskFinishedListener,
                                                               OnSubCategorySelectedListener,
                                                               SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ListView listView;

    private boolean isCreated = false;

    private boolean isRefreshed = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    HashMap<String, Marker> genericDealHashMap = new HashMap<>();

    public static NearbyFragment newInstance() {
        NearbyFragment fragment = new NearbyFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearby, container, false);

        init(v, inflater, savedInstanceState);

        if(HomeActivity.lat !=0 && HomeActivity.lng != 0)
        {
            fetchAndDisplayFoodAndDining(progressBar);
        }
       else
        {
            tvEmptyView.setText(R.string.error_no_location);
            listView.setEmptyView(tvEmptyView);
        }

        isCreated = true;

        return v;
    }

    GoogleMap googleMap;
    MapFragment mapFragment;

    MapView mapView;
    private void init(View v, LayoutInflater inflater, Bundle savedInstanceState)
    {
        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);

        /* ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);*/

        ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_header, null);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals, this);
        adapter.setCategory(ResourceUtils.FOOD_AND_DINING);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

      // mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        try
        {
            MapsInitializer.initialize(activity.getApplicationContext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();

        /*if(mapFragment != null)
        {
            googleMap = mapFragment.getMap();
        }*/

        listView = (ListView) v.findViewById(R.id.list_view);
        listView.addHeaderView(ivBanner);
        listView.addHeaderView(rlHeader);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        NearbyFilterOnClickListener clickListener = new NearbyFilterOnClickListener(activity, CategoryUtils.CT_NEARBY,
                listView, mapFragment);
        clickListener.setLayout(rlHeader);
    }

    private void fetchAndDisplayFoodAndDining(ProgressBar progressBar) {
        DealsTask dealsTask = new DealsTask(activity, adapter,
                                            progressBar, ivBanner,
                                            this);

        String cache = dealsTask.getCache("nearby");

        if(cache != null && !isRefreshed)
        {
            dealsTask.setData(cache);
        }
        else
        {
            dealsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "nearby");
        }
    }

    @Override
    public void onFilterChange()
    {
        Logger.print("FoodAndDiningFragment onFilterChange");

        fetchAndDisplayFoodAndDining(progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
    }



    @Override
    public void onRefresh()
    {
        diskCache.remove(adapter.deals);

        memoryCache.remove(adapter.deals);

        isRefreshed = true;

        fetchAndDisplayFoodAndDining(null);

        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals()
    {
        ivBanner.setImageResource(R.drawable.food_dinning_banner);

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onSubCategorySelected()
    {
        Logger.print("IsCreated:" + isCreated);

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
    public void onPause() {
        super.onPause();

        Logger.print("onPause: FoodANdDiinignFrag");

        mapView.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("is_fav", false);

            adapter.genericDeal.isFav = isFav;

            String voucher = data.getStringExtra("voucher");

            if(voucher != null)
            {
                adapter.genericDeal.voucher = voucher;
                adapter.genericDeal.status = "Available";
            }

            int views = data.getIntExtra("views", -1);

            adapter.genericDeal.views = views;

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {

        if(!activity.isDestroyed())
        {
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }

        super.onDestroyView();
    }
}