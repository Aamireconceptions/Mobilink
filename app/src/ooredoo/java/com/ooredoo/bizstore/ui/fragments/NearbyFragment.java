package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;

import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.interfaces.ScrollToTop;
import com.ooredoo.bizstore.listeners.FabScrollListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.CONNECTION_TIME_OUT;
import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.IMAGE_BASE_URL;
import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.METHOD;
import static com.ooredoo.bizstore.asynctasks.BaseAsyncTask.READ_TIME_OUT;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

public class NearbyFragment extends Fragment implements OnFilterChangeListener,
        OnDealsTaskFinishedListener,
        OnSubCategorySelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        ScrollToTop{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private ScrollView scrollView;

    private LinearLayout llLocationEmptyView;

    private TextView tvEmptyView;

    private ListView listView;

    private boolean isCreated = false;

    private boolean isRefreshed = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    HashMap<String, GenericDeal> genericDealHashMap = new HashMap<>();

    public static NearbyFragment newInstance() {
        NearbyFragment fragment = new NearbyFragment();

        return fragment;
    }

    ViewGroup parent;

    Resources resources;

RelativeLayout rlParent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = container;

        View v = inflater.inflate(R.layout.fragment_nearby, container, false);

        init(v, inflater, savedInstanceState);

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            fetchAndDisplayNearby(progressBar);
        }
        else
        {
            //tvEmptyView.setText(R.string.error_no_location);
            scrollView.setVisibility(View.VISIBLE);
            listView.setEmptyView(llLocationEmptyView);
        }

        isCreated = true;

        rlParent = (RelativeLayout) v.findViewById(R.id.map_frame);

        return v;
    }

    public GoogleMap googleMap;
    MapFragment mapFragment;

    MapView mapView;

    LayoutInflater inflater;

    private int reqWidth, reqHeight;

    LocationManager locationManager;
    BitmapProcessor bitmapProcessor;

    private View layoutFilterTags;

    private TextView tvFilter;

    private ImageView ivClose;

    private void init(View v, LayoutInflater inflater, Bundle savedInstanceState)
    {
        this.inflater = inflater;

        activity = (HomeActivity) getActivity();

        resources = activity.getResources();

        /*reqWidth = resources.getDisplayMetrics().widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._105sdp)
                / resources.getDisplayMetrics().density);*/

        bitmapProcessor = new BitmapProcessor();


       mapView = (MapView) inflater.inflate(R.layout.layout_map, rlParent, false);



        //ImageView imageView =  (ImageView) linearLayout.findViewById(R.id.dummy);

        /*imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Logger.print("down");
                   // listView.setScrollContainer(false);

                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP)
                {Logger.print("up");
                   // listView.setScrollContainer(true);

                    return true;
                }

                return false;
            }
        });*/

       // mapView = (MapView) linearLayout.findViewById(R.id.mapView);
      /*  mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return false;
            }
        });*/

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

        if(googleMap != null)
        {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            //googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker)
                {
                    String id = marker.getId();

                    GenericDeal genericDeal = genericDealHashMap.get(id);

                    Intent intent = new Intent();
                    intent.setClass(activity, DealDetailActivity.class);
                    intent.putExtra("generic_deal", genericDeal);
                    intent.putExtra(CATEGORY, "");

                    activity.startActivity(intent);

                }
            });
        }

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.nearby_list_view, R.id.empty_view,
                R.id.scrollView);
        swipeRefreshLayout.setOnRefreshListener(this);

        /* ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);*/

        ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_header, null);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals, this);
        adapter.setCategory(ResourceUtils.FOOD_AND_DINING);
        adapter.setListingType("deals");
        //adapter.setMapLayout(linearLayout);

        scrollView = (ScrollView) v.findViewById(R.id.scrollView);

        llLocationEmptyView = (LinearLayout) v.findViewById(R.id.location_empty_view);

        Button btEnableLoc = (Button) v.findViewById(R.id.enable_location);
        btEnableLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                NearbyFragment.this.startActivityForResult(intent, 10);
            }
        });
        FontUtils.setFontWithStyle(activity, btEnableLoc, Typeface.BOLD);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        // mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

       // adapter.setMapView(mapView);

        adapter.setGenericDealHashMap(genericDealHashMap);

        //adapter.setGoogleMap(googleMap);
        /*if(mapFragment != null)
        {
            googleMap = mapFragment.getMap();
        }*/

        listView = (ListView) v.findViewById(R.id.nearby_list_view);
        //listView.setOnScrollListener(new FabScrollListener(activity));
       // listView.addHeaderView(ivBanner);
       // listView.addHeaderView(rlHeader);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    //return false;


                return false;
            }
        });

        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new FabScrollListener(activity));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity,
                CategoryUtils.CT_NEARBY);
        clickListener.setLayout(v);

        initMarker();

        layoutFilterTags = v.findViewById(R.id.filter_tags);

        tvFilter = (TextView) layoutFilterTags.findViewById(R.id.filter);

        ivClose = (ImageView) v.findViewById(R.id.close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFilterTags.setVisibility(View.GONE);

                activity.resetFilters();
                onFilterChange();
            }
        });
    }

    ImageView markerImageView;
    TextView tvBrandText;
    FrameLayout linearLayout;
    void initMarker()
    {
        linearLayout = (FrameLayout) inflater.inflate(R.layout.marker, null);

        markerImageView = (ImageView) linearLayout.findViewById(R.id.brand_icon);

        tvBrandText = (TextView) linearLayout.findViewById(R.id.brand_text);
        //imageView.setImageBitmap(bitmap);

        //linearLayout.setDrawingCacheEnabled(true);

        linearLayout.measure(View.MeasureSpec.makeMeasureSpec((int) resources.getDimension(R.dimen._35sdp), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((int) resources.getDimension(R.dimen._45sdp), View.MeasureSpec.EXACTLY));

        linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());

        //linearLayout.buildDrawingCache();
    }

    DealsTask dealsTask;

    private void fetchAndDisplayNearby(ProgressBar progressBar) {

        tvEmptyView.setVisibility(View.GONE);

        dealsTask = new DealsTask(activity, adapter,
                progressBar, ivBanner,
                this);

        dealsTask.setNearbyFragment(this);
        dealsTask.category = "nearby";

        if(DealsTask.sortColumn.equals("views"))
        {
            dealsTask.setType("map");
        }
        else
        {
            dealsTask.setType(null);
        }

        String cache = dealsTask.getCache("nearby");

        cache = null;

        if(cache != null && !isRefreshed)
        {
            dealsTask.setData(cache);
        }
        else
        {
            Logger.print("NearbyTask Called");

            if(HomeActivity.lat != 0 && HomeActivity.lng != 0)
            {
                dealsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "nearby");
            }
            else
            {
                onNoDeals(R.string.error_no_loc);
            }
        }
    }

    //public static boolean isMap = false;
    @Override
    public void onFilterChange()
    {
        Logger.print("NearbyFragment onFilterChange");

       /* if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            return;
        }*/

        listView.setEmptyView(null);
        scrollView.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);

        if(DealsTask.sortColumn.equals("createdate"))
        {
           // adapter.setListingType("deals");

            //isMap = false;

           // swipeRefreshLayout.setEnabled(true);

            listView.setVisibility(View.VISIBLE);

            //mapView.setVisibility(View.INVISIBLE);

            rlParent.removeView(mapView);

            rlParent.setVisibility(View.GONE);
        }
        else
        {
            listView.setVisibility(View.GONE);

            rlParent.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            params.addRule(RelativeLayout.BELOW, R.id.header);
            params.topMargin = (int) resources.getDimension(R.dimen._8sdp);

            if(rlParent != mapView.getParent())
            {
                Logger.print("MapView Specs: " + mapView.getHeight());
                Logger.print("Container Specs: "+rlParent.getHeight());
                rlParent.addView(mapView);
            }

           // mapView.setVisibility(View.VISIBLE);
            //adapter.setListingType("map");

            //isMap = true;

           // swipeRefreshLayout.setEnabled(false);
        }

       // adapter.notifyDataSetChanged();

        filterTagUpdate();

        fetchAndDisplayNearby(progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();


    }

    @Override
    public void onRefresh()
    {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            layoutFilterTags.setVisibility(View.GONE);

            diskCache.remove(adapter.deals);

            memoryCache.remove(adapter.deals);

            final String KEY = PREFIX_DEALS.concat("nearby");
            final String UPDATE_KEY = KEY.concat("_UPDATE");

            clearCache(activity, KEY);
            clearCache(activity, UPDATE_KEY);

            activity.resetFilters();

            CategoryUtils.showSubCategories(activity, CategoryUtils.CT_NEARBY);

            isRefreshed = true;

            fetchAndDisplayNearby(null);

            isRefreshed = false;
        }
        else
        {
            //tvEmptyView.setText(R.string.error_no_location);
            adapter.clearData();;
            adapter.notifyDataSetChanged();

            scrollView.setVisibility(View.VISIBLE);
            listView.setEmptyView(llLocationEmptyView);

            swipeRefreshLayout.setRefreshing(false);
        }
        /*diskCache.remove(adapter.deals);

        memoryCache.remove(adapter.deals);

        activity.resetFilters();

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_NEARBY);

        isRefreshed = true;

        fetchAndDisplayFoodAndDining(null);

        isRefreshed = false;*/
    }

    public void onLocationFound()
    {
        if(tvEmptyView != null)
        {
            tvEmptyView.setText("");

            fetchAndDisplayNearby(progressBar);
        }
    }


    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals()
    {
        //ivBanner.setImageResource(R.drawable.nearby_banner);

        //rlHeader.setVisibility(View.VISIBLE);

        tvEmptyView.setText("");
    }

    @Override
    public void onNoDeals(int stringResId) {
        //ivBanner.setImageDrawable(null);

        //rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);

       /* if(adapter.deals != null && adapter.deals.size() > 0 && adapter.filterHeaderDeal != null)
        {
            adapter.filterHeaderDeal = null;
            adapter.deals.remove(0);
            adapter.notifyDataSetChanged();
        }*/

        adapter.filterHeaderDeal = null;
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

        if(requestCode == 10)
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                scrollView.setVisibility(View.GONE);
                listView.setEmptyView(null);

                fetchAndDisplayNearby(progressBar);
            }
            else
            {
                scrollView.setVisibility(View.VISIBLE);
                listView.setEmptyView(llLocationEmptyView);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {

       /* if(!activity.isDestroyed())
        {
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }*/

        super.onDestroyView();
    }

    public void populateMap(List<GenericDeal> deals)
    {
        if(googleMap != null)
        {
            googleMap.clear();
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(final GenericDeal deal : deals)
        {
            //Image image = deal.image;

            String businessLogoUrl = deal.businessLogo;

            builder.include(new LatLng(deal.latitude, deal.longitude));

            if(businessLogoUrl != null) {
                final String url = IMAGE_BASE_URL + businessLogoUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(url);

                if (bitmap == null) {
                    bitmap = diskCache.getBitmapFromDiskCache(url);
                }

                if (bitmap != null) {
                    addMarker(bitmap, deal);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = downloadBitmap(url, String.valueOf((int) resources.getDimension(R.dimen._60sdp)),
                                    String.valueOf((int) resources.getDimension(R.dimen._60sdp)));

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addMarker(bitmap, deal);
                                }
                            });
                        }
                    }).start();
                }
            }
            else
            {
                addMarker(null, deal);
            }
        }

        if(deals.size() > 0)
        {
            builder.include(new LatLng(HomeActivity.lat, HomeActivity.lng));

            LatLngBounds bounds = builder.build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, resources.getDisplayMetrics().widthPixels,
                    resources.getDisplayMetrics().heightPixels - (int) resources.getDimension(R.dimen._140sdp), 150);

            if(googleMap != null)
            {
                googleMap.animateCamera(cameraUpdate);
            }
        }

    }
    private void addMarker(Bitmap bitmap, GenericDeal deal)
    {
        linearLayout.setDrawingCacheEnabled(true);

        if(bitmap != null)
        {
            tvBrandText.setVisibility(View.GONE);
            markerImageView.setVisibility(View.VISIBLE);
            markerImageView.setImageBitmap(bitmap);

            bitmap = linearLayout.getDrawingCache();

            if(bitmap != null)
            {
                BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);

                MarkerOptions options = new MarkerOptions()
                        .title(deal.title)
                        .snippet(deal.description)
                        .position(new LatLng(deal.latitude, deal.longitude))
                        .icon(bd);

                if(googleMap != null)
                {
                    Marker marker = googleMap.addMarker(options);
                    genericDealHashMap.put(marker.getId(), deal);

                    linearLayout.setDrawingCacheEnabled(false);
                }

            }
        }
        else
        {
            markerImageView.setVisibility(View.GONE);

            tvBrandText.setVisibility(View.VISIBLE);
            if(deal.businessName != null && !deal.businessName.isEmpty())
            {
                tvBrandText.setText(String.valueOf(deal.businessName.charAt(0)));
            }
            else
            if(deal.title != null && !deal.title.isEmpty())
            {
                tvBrandText.setText(String.valueOf(deal.title.charAt(0)));
            }

            bitmap = linearLayout.getDrawingCache();

            if(bitmap != null)
            {
                BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);

                MarkerOptions options = new MarkerOptions()
                        .title(deal.title)
                        .snippet(deal.description)
                        .position(new LatLng(deal.latitude, deal.longitude))
                        .icon(bd);

                if(googleMap != null)
                {
                    Marker marker = googleMap.addMarker(options);
                    genericDealHashMap.put(marker.getId(), deal);

                    linearLayout.setDrawingCacheEnabled(false);
                }

            }
        }
    }

    HttpsURLConnection connection = null;

    public Bitmap downloadBitmap(String imgUrl, String reqWidth, String reqHeight)
    {

            if(memoryCache.getBitmapFromCache(imgUrl) != null)
            {
                return memoryCache.getBitmapFromCache(imgUrl);
                /*Logger.print("Already downloaded. Cancelling task");

                cancel(true);*/
            }

            Bitmap b = diskCache.getBitmapFromDiskCache(imgUrl);
            if(b != null)
            {
                return b;
            }

            if(BizStore.forceStopTasks)
            {
                Logger.print("Force stopped bitmap download task");

                return null;
            }



        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream is = BizStore.context.getResources().openRawResource(R.raw.cert);
            Certificate ca;
            try
            {
                ca = cf.generateCertificate(is);

                Logger.print("ca = " + ((X509Certificate) ca).getSubjectDN());
            }
            finally
            {
                is.close();
            }

            String keystoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            // Initialise the TMF as you normally would, for example:
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier hostnameVerifier = new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostName, SSLSession sslSession)
                {
                    /*HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    Logger.print("Https Hostname: "+hostName);

                    return hv.verify(s, sslSession);*/

                    return true;
                }
            };

            Logger.print("Bitmap Url: " + imgUrl);
            URL url = new URL(imgUrl);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(hostnameVerifier);

            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setRequestMethod(METHOD);
            connection.setDoInput(true);
            connection.connect();

            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());

           /* int width = (int) Converter.convertDpToPixels(Integer.parseInt(reqWidth));
            int height = (int) Converter.convertDpToPixels(Integer.parseInt(reqHeight));*/

            int width = Integer.parseInt(reqWidth);

            int height = Integer.parseInt(reqHeight);

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromStream(inputStream, url, width, height);

            if(bitmap != null)
            {
                diskCache.addBitmapToDiskCache(imgUrl, bitmap);
                memoryCache.addBitmapToCache(imgUrl, bitmap);
            }

            return bitmap;
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void scroll() {
        listView.setSelection(0);
    }

    public void filterTagUpdate()
    {
        String filter = "";

        if(activity.doApplyDiscount)
        {
            filter = "Discount: Highest to lowest, ";
        }

        if(activity.doApplyRating)
        {
            filter += "Rating " + activity.ratingFilter +", ";
        }

        if(activity.distanceFilter != null )
        {
            filter += activity.getString(R.string.distance)
                    + ": " + activity.distanceFilter
                    + " " + activity.getString(R.string.km)+", ";
        }

        String categories = CategoryUtils.getSelectedSubCategoriesForTag(CategoryUtils.CT_NEARBY);

        if(!categories.isEmpty())
        {
            filter += "Sub Categories: "+categories ;
        }

        if(! filter.isEmpty() && filter.charAt(filter.length() - 2) == ',')
        {
            filter = filter.substring(0, filter.length() - 2);
        }

        if(!filter.isEmpty())
        {
            layoutFilterTags.setVisibility(View.VISIBLE);

            FontUtils.changeColorAndMakeBold(tvFilter,
                    activity.getString(R.string.filter) + " : " + filter,
                    activity.getString(R.string.filter) + " : ",
                    activity.getResources().getColor(R.color.black));
        }
        else
        {
            layoutFilterTags.setVisibility(View.GONE);
        }
    }
}