package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.zxing.client.android.CaptureActivity;
import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.asynctasks.CalculateDistanceTask;
import com.ooredoo.bizstore.asynctasks.DealDetailMiscTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.VerifyMerchantCodeTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.asynctasks.LocationsTask;
import com.ooredoo.bizstore.interfaces.LocationNotifies;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.IntentIntegrator;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ScrollViewHelper;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.DIALER_PREFIX;
import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryIcon;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class DealDetailActivity extends BaseActivity implements OnClickListener,
        LocationNotifies

{

    public String category;
    static String packageName;
    public boolean showBanner = false;
public EditText etMerchantCode;

    RelativeLayout rlMerchandCode;

    private String qticketUrl = "https://www.q-tickets.com/";

   // public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    ScrollViewHelper scrollViewHelper;

    private Button btGetCode;

    private ImageView ivLine, ivVerifyMerchantCode;

    private LinearLayout llVoucherCode;

    private TextView tvDiscount, tvValidity, tvCode, tvNote, tvDiscountVoucher;

    private int id = -1;

    public Deal src;

    boolean isFav = false;

    public static GenericDeal selectedDeal, genericDeal;

    private Dialog ratingDialog;

    private  MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private SnackBarUtils snackBarUtils;

    LinearLayout llSimilarNearby;


    ListViewBaseAdapter commonAdapter;
    public DealDetailActivity() {
        super();
        layoutResId = R.layout.deal_detail_activity;
    }

    TextView tvVoucherClaimed;

    Tracker tracker;

    @Override
    public void init() {

        diskCache.requestInit(this);

        header = getLayoutInflater().inflate(R.layout.frag_deal_detail, null);

        setupToolbar();

        handleIntentFilter();

        initViews();
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.print("DEAL_DETAIL: onNewIntent");

        setIntent(intent);

        handleIntentFilter();
    }

    private void handleIntentFilter() {
        Intent intent = getIntent();

        Logger.print("HandleIntentFilter");

        if(intent != null) {
            Uri uri = intent.getData();

            if(uri != null)
            {
                String paramId = uri.getQueryParameter("id");

                Logger.print("Extras: " + paramId);

                getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
            }

            id = intent.getIntExtra(AppConstant.ID, 0);
            getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Logger.print("OnCreateContextMenu");

        if(mDeal.locations != null && mDeal.locations.size() > 0) {
            for (int i = 0; i <= mDeal.locations.size() - 1; i++) {
                /*if(deal.location != null && !deal.location.equalsIgnoreCase(deal.locations.get(i).title))
                {
                    popupMenu.getMenu().add(1, deal.locations.get(i).id, 0, deal.locations.get(i).title);
                }*/
                if (mDeal.locations.size() > 1 ||
                        (mDeal.location != null
                                &&
                                (!mDeal.location.equalsIgnoreCase(mDeal.locations.get(i).title))))

                {
                    menu.add(1, mDeal.locations.get(i).id, 0, mDeal.locations.get(i).title);
                }
            }
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();

        mDeal.businessId = id;

        Logger.print("menuId: "+id);

        LocationsTask locationsTask = new LocationsTask(DealDetailActivity.this);
        locationsTask.execute(String.valueOf(id), "deals", item.getTitle().toString());

        return super.onContextItemSelected(item);
    }

    Spinner spinner;
    View header;
    ListView listView;
    PopupMenu popupMenu;
    TextView tvLocations;
    TextView tvPrices;
    ListViewBaseAdapter similarAdapter, nearbyAdapter;

    List<GenericDeal> similarDeals = new ArrayList<>(),  nearbyDeals = new ArrayList<>();
    ScrollViewListener mOnScrollViewListener;
    LinearLayout llHead, llDiscount;
    TextView tvHeadTitle, tvHeadDescription;
    TableLayout tableLayout;
    RelativeLayout rlDetails;


    private void initViews()
    {
        genericDeal = (GenericDeal) intent.getSerializableExtra("generic_deal");

        llHead = (LinearLayout) header.findViewById(R.id.head);
        tvHeadTitle = (TextView) header.findViewById(R.id.head_title);
        FontUtils.setFontWithStyle(this, tvHeadTitle, Typeface.BOLD);
        tvHeadDescription = (TextView) header.findViewById(R.id.head_description);

        tvPrices = (TextView) header.findViewById(R.id.prices);

        tvVoucherClaimed = (TextView) header.findViewById(R.id.vouchers_claimed);

        ivVerifyMerchantCode = (ImageView) header.findViewById(R.id.verify_merchant_code);
        ivVerifyMerchantCode.setOnClickListener(this);

        rlMerchandCode = (RelativeLayout) header.findViewById(R.id.merchant_code_layout);

        etMerchantCode = (EditText) header.findViewById(R.id.merchant_code_field);
        etMerchantCode.setMaxWidth(etMerchantCode.getWidth());
        etMerchantCode.setMaxLines(4);

        FontUtils.setFontWithStyle(this, etMerchantCode, Typeface.BOLD);

        Logger.print("etMerchant Code width:"+etMerchantCode.getWidth());

        packageName = getPackageName();
        if(genericDeal != null) {

            id = genericDeal.id;

            Logger.logI("DETAIL_ID", valueOf(id));
        }

        llDiscount = (LinearLayout) header.findViewById(R.id.discount_layout);

        tableLayout = (TableLayout) header.findViewById(R.id.table);

        rlDetails = (RelativeLayout) header.findViewById(R.id.ll_details);

        scrollViewHelper = new ScrollViewHelper(this);

        mOnScrollViewListener = new ScrollViewListener(mActionBar);
        llSimilarNearby = (LinearLayout) header.findViewById(R.id.similar_nearby);

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header);
        listView.setAdapter(null);
        header.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        etMerchantCode.clearFocus();
                    }
                }

                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollViewListener != null) {

                }

                View currentFcous = getCurrentFocus();
                if(currentFcous != null)
                {
                    currentFcous.clearFocus();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mOnScrollViewListener.onScrollChanged( header);
            }

        });

        category = intent.getStringExtra(CATEGORY);

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        header.findViewById(R.id.tv_call).setOnClickListener(this);
        header.findViewById(R.id.iv_call).setOnClickListener(this);
        header.findViewById(R.id.tv_rate).setOnClickListener(this);
        header.findViewById(R.id.iv_rate).setOnClickListener(this);
        header.findViewById(R.id.tv_share).setOnClickListener(this);
        header.findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);

        tvLocations = (TextView) header.findViewById(R.id.locations);

        FontUtils.setFontWithStyle(this, tvLocations, Typeface.BOLD);

        tvLocations.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // popupMenu.show();

                registerForContextMenu(v);

                openContextMenu(v);

                unregisterForContextMenu(v);
            }
        });

        popupMenu = new PopupMenu(this, tvLocations, Gravity.BOTTOM);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                int id = item.getItemId();

                Logger.print("menuId: "+id);

                LocationsTask locationsTask = new LocationsTask(DealDetailActivity.this);
                locationsTask.execute(String.valueOf(id), "deals", item.getTitle().toString());

                return false;
            }
        });

        btGetCode = (Button) header.findViewById(R.id.get_code);
        btGetCode.setOnClickListener(this);

        FontUtils.setFontWithStyle(this, btGetCode, Typeface.BOLD);

        tvDiscount = (TextView) findViewById(R.id.discount);

        if(genericDeal == null) {
            DealDetailTask dealDetailTask = new DealDetailTask(this, null, snackBarUtils);
            dealDetailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
        }
        else
        {
            populateData(genericDeal);
        }

        BizStore bizStore = (BizStore) getApplication();

        tracker = bizStore.getDefaultTracker();
    }

    Button btSimilarDeals, btNearbyDeals;
    ImageView ivDetail;
    ProgressBar progressBar;
    LinearLayout llDirections;
    RelativeLayout rlHeader;
    TextView tvCity;

    RelativeLayout rlVoucher;
    GenericDeal mDeal;

    public void populateData(final GenericDeal deal) {
        if(deal != null) {

            genericDeal = deal;

            mDeal = deal;

            if(deal.actualPrice > 0 && deal.discountedPrice > 0)
            {
                tvPrices.setVisibility(View.VISIBLE);

                String qar = getString(R.string.qar);

                String discountedPrice = qar + " " + deal.discountedPrice;

                String actualPrice = qar + " " + deal.actualPrice;

                FontUtils.strikeThrough(tvPrices, discountedPrice + "  -  " + actualPrice,
                        actualPrice, getResources().getColor(R.color.slight_grey));
            }


            mOnScrollViewListener.setTitle(deal.title.toUpperCase());

            src = new Deal(deal);
            src.id = deal.id;
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "deals", id);
            incrementViewsTask.execute();

            mActionBar.setTitle(deal.title);

            rlHeader = (RelativeLayout) header.findViewById(R.id.rl_header);

            TextView tvBrand = (TextView) findViewById(R.id.brand_name);
            tvBrand.setText(deal.businessName);

            TextView tvValidity = (TextView) findViewById(R.id.validity);
            tvValidity.setText(getString(R.string.deal_valid_till) + " " + deal.endDate);

            llDirections = (LinearLayout) findViewById(R.id.directions_layout);
            llDirections.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDirections();
                }
            });

            TextView tvCategory = (TextView) findViewById(R.id.cat);
            tvCategory.setText(deal.category);

            FontUtils.setFontWithStyle(this, tvCategory, Typeface.BOLD);

            if(deal.how_works != null && !deal.how_works.isEmpty())
            {
                TextView tvHTWNote = (TextView) header.findViewById(R.id.how_this_work_note);

                tvHTWNote.setVisibility(View.VISIBLE);
                tvHTWNote.setOnClickListener(this);
            }

            if(deal.terms_services != null && !deal.terms_services.isEmpty())
            {
                TextView tvTermsServices = (TextView) header.findViewById(R.id.tos_note);

                tvTermsServices.setVisibility(View.VISIBLE);
                tvTermsServices.setOnClickListener(this);
            }


            if(isNotNullOrEmpty(deal.category) && deal.category.contains(".")) {
                deal.category = deal.category.replace(".", ",");
            }
            TextView tvTitle = ((TextView) header.findViewById(R.id.title));
            tvTitle.setText(deal.title);

            FontUtils.setFontWithStyle(this, tvTitle, Typeface.BOLD);
            //((TextView) header.findViewById(R.id.phone)).setText(deal.contact);

            ((TextView) header.findViewById(R.id.description)).setText(deal.description);
            //((TextView) header.findViewById(R.id.address)).setText(deal.address);

            //((TextView) findViewById(R.id.tv_category)).setText(deal.category);
            ((RatingBar) header.findViewById(R.id.rating_bar)).setRating(deal.rating);


            header.findViewById(R.id.iv_views).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DealDetailActivity.this, getString(R.string.deal_has_been_viewed) + " "+deal.views + " " + getString(R.string.times), Toast.LENGTH_SHORT).show();
                }
            });

            TextView tvView = ((TextView) header.findViewById(R.id.tv_views));
            tvView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DealDetailActivity.this, getString(R.string.deal_has_been_viewed) + " "+deal.views+ " " + getString(R.string.times), Toast.LENGTH_SHORT).show();
                }
            });
            tvView.setText(valueOf(deal.views));

            rlVoucher = (RelativeLayout) header.findViewById(R.id.voucher_layout);

            if(deal.is_exclusive == 0) {
                rlVoucher.setVisibility(View.GONE);
                tvDiscount.setVisibility(View.GONE);

               // findViewById(R.id.tv_discount).setVisibility(View.GONE);
                //findViewById(R.id.tv_deal_discount).setVisibility(View.GONE);
            }
            else
            {
                rlVoucher.setVisibility(View.VISIBLE);
                tvDiscount.setVisibility(View.VISIBLE);
            }

            if(deal.discount == 0)
            {
                tvDiscount.setVisibility(View.GONE);
            }

            if(deal.isQticket == 1)
            {
                btGetCode.setText(R.string.get_qticket);
            }
           // deal.location = "doha";

            RelativeLayout rlLocationHeader = (RelativeLayout) header.findViewById(R.id.location_header);

            tvCity = ((TextView) header.findViewById(R.id.city));

            FontUtils.setFontWithStyle(this, tvCity, Typeface.BOLD);

            if(deal.location != null && !deal.location.isEmpty()) {

                tvCity.setText(deal.location);
            }
            else
            {
               // rlLocationHeader.setVisibility(View.GONE);
            }


            Logger.print("businessId Deal,"+deal.businessId);

            final ImageView ivBrandLogo = (ImageView) findViewById(R.id.brand_logo);
            ivBrandLogo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMerchantCode.getWindowToken(), 0);

                    Intent intent = new Intent();
                    intent.setClass(DealDetailActivity.this, BusinessDetailActivity.class);

                    intent.putExtra(CATEGORY, "");
                    intent.putExtra(AppConstant.ID, deal.businessId);
                    intent.putExtra("color", deal.color );
                    startActivity(intent);
                }
            });

            String brandLogo = deal.businessLogo;

            Logger.print("BrandLogo: " + brandLogo);

            if(brandLogo != null && !brandLogo.equals(""))
            {
               final String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

               Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                    ivBrandLogo.setImageBitmap(bitmap);
                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivBrandLogo);
                }
            }
            else
            {
                //ivBrandLogo.setVisibility(View.GONE);

                TextView tvBrandTxt = (TextView) header.findViewById(R.id.brand_txt);
                FontUtils.setFontWithStyle(this, tvBrandTxt, Typeface.BOLD);

                if(deal.businessName != null && !deal.businessName.isEmpty())
                {
                    if(deal.color == 0)
                    {
                        deal.color = Color.parseColor(ColorUtils.getColorCode());
                    }
                    tvBrandTxt.setBackgroundColor(deal.color);
                    tvBrandTxt.setText(String.valueOf(deal.businessName.charAt(0)));
                }
            }

            String discount = valueOf(deal.discount) + getString(R.string.percentage_off);

            src.isFavorite = Favorite.isFavorite(id);

            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);

            int categoryIcon = getCategoryIcon(deal.category);

            ivDetail = (ImageView) header.findViewById(R.id.detail_img);

            progressBar = (ProgressBar) header.findViewById(R.id.progressBar);

            String detailImageUrl = null;
            if(deal.image != null)
            {
                detailImageUrl = deal.image.detailBannerUrl;
            }

            Logger.print("detailImgUrl: " + detailImageUrl);

            if(detailImageUrl != null && !detailImageUrl.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + detailImageUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                   /* Palette palette = Palette.from(bitmap).generate();
                    if(palette != null)
                    {
                        Palette.Swatch swatch = palette.getLightMutedSwatch();
                        if(swatch != null)
                        {
                            rlHeader.setBackgroundColor(swatch.getRgb());
                        }
                    }*/

                    ivDetail.setImageBitmap(bitmap);

                    ivDetail.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorUtils.startDetailAnimation(rlDetails, tableLayout, llDiscount);
                        }
                    });

                    //AnimatorUtils.expandAndFadeIn(ivDetail);

                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivDetail);
                }
            }

            tvDiscount.setText(discount);
            FontUtils.setFontWithStyle(this, tvDiscount, Typeface.BOLD);

            btSimilarDeals = (Button) header.findViewById(R.id.similar_deals);
            btSimilarDeals.setOnClickListener(this);

            FontUtils.setFont(this, btSimilarDeals);
           // btSimilarDeals.performClick();

            btNearbyDeals = (Button) header.findViewById(R.id.nearby_deals);
            btNearbyDeals.setOnClickListener(this);

            FontUtils.setFont(this,  btNearbyDeals);

            //tvValidity.setText(getString(R.string.redeem_until) + " " + deal.endDate);

            /*if(deal.voucher != null && !deal.voucher.isEmpty())
            {
                llVoucherCode.setVisibility(View.VISIBLE);

                if(deal.status.equals("Available"))
                {
                    tvCode.setText(deal.voucher);

                    tvNote.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvCode.setText("This voucher code has been already redeemed!");

                    tvDiscountVoucher.setVisibility(View.GONE);
                }

                btGetCode.setVisibility(View.GONE);

                ivLine.setVisibility(View.VISIBLE);
            }*/

            if(deal.is_exclusive == 1)
            {
               // header.findViewById(R.id.voucher_frame).setVisibility(View.VISIBLE);
            }



        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }

      //similarAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);

       // nearbyAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, nearbyDeals, null);

        commonAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);
        commonAdapter.setListingType("deals");
        listView.setAdapter(commonAdapter);
        //listView.setAdapter(similarAdapter);
        //listView.setAdapter(nearbyAdapter);

        //String[] locations = new String[genericDeal.locations.size()];

        if(deal.locations != null && deal.locations.size() > 0)
        {
            for(int i = 0; i<=deal.locations.size() - 1; i++)
            {
                /*if(deal.location != null && !deal.location.equalsIgnoreCase(deal.locations.get(i).title))
                {
                    popupMenu.getMenu().add(1, deal.locations.get(i).id, 0, deal.locations.get(i).title);
                }*/
                if(deal.locations.size() > 1 ||
                        (deal.location != null
                                &&
                        (!deal.location.equalsIgnoreCase(deal.locations.get(i).title))))

                {
                    popupMenu.getMenu().add(1, deal.locations.get(i).id, 0, deal.locations.get(i).title);
                }
            }

            if(deal.locations.size() == 1 && deal.location.equals(deal.locations.get(0).title))
            {
                tvLocations.setVisibility(View.GONE);
            }
        }
        else
        {
            tvLocations.setVisibility(View.GONE);
        }

        if(popupMenu.getMenu().size() == 0)
        {
            tvLocations.setVisibility(View.GONE);
        }

       /* ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinner.setAdapter(spinnerAdapter);*/

        updateOutlet(deal, null);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.more_progress);

        DealDetailMiscTask detailMiscTask = new DealDetailMiscTask(this, similarDeals, nearbyDeals, progressBar);
        detailMiscTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(deal.id),
                String.valueOf(deal.businessId));
    }

    private void updateOutlet(GenericDeal deal, String selectedLoc)
    {
        if(selectedLoc != null)
        tvCity.setText(selectedLoc);

        RelativeLayout rlPhone = (RelativeLayout) findViewById(R.id.phone_layout);

        TextView tvPhone= (TextView) header.findViewById(R.id.phone);

        if(deal.contact != null && !deal.contact.isEmpty())
        {
            rlPhone.setVisibility(View.VISIBLE);
            tvPhone.setText(PhoneNumberUtils.formatNumber(deal.contact.contains("+")
                    ? deal.contact : "+" + deal.contact));
        }
        else
        {
            rlPhone.setVisibility(View.GONE);
        }

        ImageView ivArrow = (ImageView) findViewById(R.id.address_arrow);

        RelativeLayout rlDistance = (RelativeLayout) findViewById(R.id.distance_layout);

        if((deal.latitude == 0 && deal.longitude == 0)
                || (HomeActivity.lat == 0 && HomeActivity.lng == 0))
        {
            llDirections.setVisibility(View.GONE);
            rlDistance.setVisibility(View.GONE);

            ivArrow.setVisibility(View.GONE);
        }
        else
        {
            /*float results[] = new float[3];
            Location.distanceBetween(HomeActivity.lat, HomeActivity.lng,
                    deal.latitude, deal.longitude,
                    results);*/

            ivArrow.setVisibility(View.VISIBLE);

            genericDeal.latitude = deal.latitude;
            genericDeal.longitude = deal.longitude;

            TextView tvDirections = (TextView) header.findViewById(R.id.directions);

            TextView tvDistance = (TextView) findViewById(R.id.distance);
            tvDistance.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    startDirections();
                }
            });

            String origin = HomeActivity.lat + "," + HomeActivity.lng;
            String destination = deal.latitude + "," + deal.longitude;

            CalculateDistanceTask distanceTask = new CalculateDistanceTask(this, deal,
                    tvDistance, tvDirections, rlDistance, llDirections);
            distanceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, origin, destination);

            if(genericDeal.mDistance != 0) {
                llDirections.setVisibility(View.VISIBLE);
                rlDistance.setVisibility(View.VISIBLE);
                tvDistance.setText(String.format("%.1f", genericDeal.mDistance / 1000) + " " + getString(R.string.km_away));

                tvDirections.setText(String.format("%.1f", genericDeal.mDistance / 1000) + " km");
                tvDirections.setOnClickListener(this);
            }
            else {
               // llDirections.setVisibility(View.GONE);

                //rlDistance.setVisibility(View.GONE);
            }
        }

        RelativeLayout rlAddress = (RelativeLayout) findViewById(R.id.address_layout);
        rlAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startDirections();
            }
        });

        TextView tvAddress= (TextView) header.findViewById(R.id.address);

        if(deal.address != null && !deal.address.isEmpty())
        {
            rlAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(deal.address);


        }
        else
        {
            rlAddress.setVisibility(View.GONE);


        }

        RelativeLayout rlTiming = (RelativeLayout) findViewById(R.id.timing_layout);

        TextView tvTiming = (TextView) findViewById(R.id.timing);

        if(deal.timing != null && !deal.timing.isEmpty())
        {
            rlTiming.setVisibility(View.VISIBLE);
            tvTiming.setText(deal.timing);
        }
        else
        {
            rlTiming.setVisibility(View.GONE);
        }
    }

  // Bitmap bitmap;
    private void fallBackToDiskCache(final String url, final ImageView imageView)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
               final Bitmap bitmap = diskCache.getBitmapFromDiskCache(url);


                Logger.print("dCache getting bitmap from cache");

                if(bitmap != null)
                {
                    Logger.print("dCache found!");


                    Logger.print("deal detail_fallback: "+url+ " ,"+bitmap);
                    memoryCache.addBitmapToCache(url, bitmap);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {

                           /* Palette palette = Palette.from(bitmap).generate();
                            if(palette != null)
                            {
                                Palette.Swatch swatch = palette.getLightMutedSwatch();
                                if(swatch != null)
                                {
                                    rlHeader.setBackgroundColor(swatch.getRgb());
                                }
                            }*/

                            imageView.setImageBitmap(bitmap);
                            //AnimatorUtils.expandAndFadeIn(imageView);

                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                            BitmapForceDownloadTask bitmapDownloadTask = new BitmapForceDownloadTask
                                    (imageView, progressBar, rlHeader);
                        /*bitmapDownloadTask.execute(imgUrl, String.valueOf(displayMetrics.widthPixels),
                                String.valueOf(displayMetrics.heightPixels / 2));*/
                            bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    url, String.valueOf(displayMetrics.widthPixels),
                                    String.valueOf(displayMetrics.heightPixels / 2));
                        }
                    });

                }
            }
        });

        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMerchantCode.getWindowToken(), 0);

                sendResult();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    View lastSelected = null;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_favorite) {
            if(genericDeal != null) {
                genericDeal.isFav = !genericDeal.isFav;
                v.setSelected(genericDeal.isFav);
                Favorite favorite = new Favorite(genericDeal);
                Favorite.updateFavorite(favorite, false);

                isFav = genericDeal.isFav;

            } else {
                //TODO src == null => No detail found OR any exception occurred.
            }
        } else if(viewId == R.id.iv_rate || viewId == R.id.tv_rate) {
            ratingDialog = showRatingDialog(this, "deals", id);
        } else if(viewId == R.id.iv_call || viewId == R.id.tv_call) {
            if(src != null && isNotNullOrEmpty(src.contact)) {
                String phoneNumber = src.contact.trim();
                if(phoneNumber.contains(","))
                    phoneNumber = phoneNumber.split(",")[0].trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(DIALER_PREFIX.concat(phoneNumber)));
                startActivity(intent);
            } else {
                makeText(getApplicationContext(), R.string.no_contact, LENGTH_LONG).show();
            }
        } else if(viewId == R.id.iv_share || viewId == R.id.tv_share) {

            shareDeal(this, id);
        }
        else
        if(viewId == R.id.get_code)
        {
            if(BuildConfig.FLAVOR.equals("mobilink"))
            {
                /*IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.initiateScan();*/

                Intent intent  = new Intent(this, CaptureActivity.class);
                startActivity(intent);

                return;
            }

            if(mDeal.isQticket == 1)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(qticketUrl));

                startActivity(intent);
            }
            else
            {
                v.setVisibility(View.GONE);

                rlMerchandCode.setVisibility(View.VISIBLE);

                etMerchantCode.requestFocus();

                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(etMerchantCode.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        }
        else
            if(viewId == R.id.similar_deals)
            {
                setSelected(v);

                commonAdapter.setData(similarDeals);
                commonAdapter.notifyDataSetChanged();
                listView.smoothScrollToPositionFromTop(1,
                        (btSimilarDeals.getHeight() * 2 + (int) getResources().getDimension(R.dimen._9sdp)), 200);
                //similarAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);
                //similarAdapter.setListingType("deals");

              // listView.setAdapter(similarAdapter);

               // listView.scrollTo(0, (int) llSimilarNearby.getY());
                //listView.setScrollY((int) llSimilarNearby.getY());
               // listView.smoothScrollToPosition(1);

                //listView.setSelection(1);
               // listView.scrollTo(0, btSimilarDeals.getTop() - btSimilarDeals.getHeight());

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPositionFromTop(1,
                                200, 2000);
                    }
                }, 2000);*/

//listView.requestFocusFromTouch();

                //similarAdapter.notifyDataSetChanged();

            }
        else
                if(viewId == R.id.nearby_deals)
                {
                    if(HomeActivity.lat != 0 && HomeActivity.lng != 0)
                    {
                        if(nearbyDeals.size() > 0)
                        {
                            setSelected(v);

                            commonAdapter.setData(nearbyDeals);
                            commonAdapter.notifyDataSetChanged();

                            listView.smoothScrollToPositionFromTop(1,
                                    (btNearbyDeals.getHeight() * 2 + (int) getResources().getDimension(R.dimen._9sdp)), 200);

                           // nearbyAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, nearbyDeals, null);
                           // nearbyAdapter.setListingType("deals");

                           // listView.setAdapter(nearbyAdapter);
                           /* listView.smoothScrollToPositionFromTop(2,
                                    0, 00);*/
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listView.smoothScrollToPositionFromTop(2,
                                            0, 00);
                                }
                            }, 0);
                            */



                            //listView.setSelection(1);
                            //listView.scrollBy(0,  llSimilarNearby.getTop() - 100);

                        }
                        else
                        {
                            Toast.makeText(this, R.string.error_no_nearby_deals, Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(this, R.string.error_your_loc_not_avaliable, Toast.LENGTH_SHORT).show();
                    }

                }
                else
                if(viewId == R.id.directions)
                {
                    startDirections();
                }
            else
                    if(viewId == R.id.verify_merchant_code)
                    {
                        String code = etMerchantCode.getText().toString().trim();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etMerchantCode.getWindowToken(), 0);
                        if(!code.isEmpty())
                        {
                            VerifyMerchantCodeTask verifyMerchantCodeTask =
                                    new VerifyMerchantCodeTask(this, snackBarUtils, tracker);
                            verifyMerchantCodeTask
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                            String.valueOf(id), code, String.valueOf(mDeal.businessId));
                        }
                        else
                        {
                            Toast.makeText(this, R.string.error_empty_voucher, Toast.LENGTH_SHORT).show();
                        }
                    }
        else
                        if(viewId == R.id.how_this_work_note)
                        {
                            llHead.setVisibility(View.VISIBLE);

                            tvHeadTitle.setText(R.string.how_this_works);
                            tvHeadDescription.setText(mDeal.how_works);
                        }
        else
                            if(viewId == R.id.tos_note)
                            {
                                llHead.setVisibility(View.VISIBLE);

                                tvHeadTitle.setText(R.string.tos);
                                tvHeadDescription.setText(mDeal.terms_services);
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

    public void showCode(int voucherClaimed, int maxAllowed, boolean hide)
    {
        if(hide)
        {
            //rlMerchandCode.setVisibility(View.GONE);

            etMerchantCode.setText("");
            rlMerchandCode.setVisibility(View.GONE);

            btGetCode.setVisibility(View.VISIBLE);
        }

       // voucherClaimed += 1;

        tvVoucherClaimed.setVisibility(View.VISIBLE);

        if(voucherClaimed >= maxAllowed)
        {
            rlVoucher.setVisibility(View.GONE);

            tvVoucherClaimed.setText(getString(R.string.already_availed_discount)+" " + maxAllowed + " " + getString(R.string.timess));

            return;
        }
        else
        {
            tvVoucherClaimed.setVisibility(View.VISIBLE);

            /*tvVoucherClaimed.setText("Dear user, you have availed the discount " + voucherClaimed
                    + " number of times "
                    + (genericDeal.vouchers_max_allowed - voucherClaimed) + " attempts are still pending");*/
        }

        tvVoucherClaimed.setText(getString(R.string.out_of)+" " + genericDeal.vouchers_max_allowed
                + ", " + getString(R.string.you_have_availed) + " "
                + ( voucherClaimed) + " " + getString(R.string.dealss));

       /* tvVoucherClaimed.setText("Dear user, you have availed the discount " + voucherClaimed
                + " number of times "
                + (genericDeal.vouchers_max_allowed - voucherClaimed) + " attempts are still pending");
*/
        /*btGetCode.setVisibility(View.GONE);

        ivLine.setVisibility(View.VISIBLE);

        llVoucherCode.setVisibility(View.VISIBLE);

        tvCode.setText(code);

        tvNote.setVisibility(View.VISIBLE);

        genericDeal.voucher = code;*/
    }

    public static void shareDeal(Activity activity, long dealId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DEAL_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        String operatorName = BuildConfig.FLAVOR.equals("ooredoo") ? "Ooredoo" : "Telenor";

        uri = "View this awesome deal on " + operatorName + " BizStore http://ooredoo.bizstore.deal/deal_detail?id=" + dealId +
        "\n\nor download app from play.google.com/store/apps/details?id="+packageName ;

        startShareIntent(activity, uri, dealId);
    }

    private void startDirections()
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        if(mLat != 0 && mLong != 0)
        {
            src = "saddr=" + mLat + "," + mLong + "&";
        }

        if(genericDeal.latitude != 0 && genericDeal.longitude != 0)
        {
            dest = "daddr="+genericDeal.latitude + "," + genericDeal.longitude;
        }

        String uri = "http://maps.google.com/maps?";

        if(src != null)
        {
            uri += src;
        }

        if(dest != null)
        {
            uri += dest;
        }

        System.out.println("Directions URI:" + uri);

        if(src == null)
        {
            Toast.makeText(this, R.string.direction_no_loc, Toast.LENGTH_SHORT).show();

            return;
        }

        if(dest == null)
        {
            Toast.makeText(this, R.string.deal_loc_not_availabe, Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));

        try
        {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void startShareIntent(Activity activity, String uri, long id) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(AppConstant.ID, id);
        intent.putExtra(Intent.EXTRA_TEXT, uri);

        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {

        if(ratingDialog != null && ratingDialog.isShowing()) {
            ratingDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        sendResult();
    }

    private void sendResult()
    {
        Intent intent = new Intent();
        intent.putExtra("is_fav", isFav);
        if(genericDeal != null)
        {
            intent.putExtra("voucher", genericDeal.voucher);

            intent.putExtra("views", genericDeal.views);
        }

        setResult(RESULT_OK, intent);

        finish();
    }


    public void onHaveData(GenericDeal genericDeal)
   {
       if(genericDeal.similarDeals.size() > 0 || genericDeal.nearbyDeals.size() > 0)
       {
           llSimilarNearby.setVisibility(View.VISIBLE);

           if(genericDeal.similarDeals.size() > 0)
           {
               commonAdapter.setData(similarDeals);
               commonAdapter.notifyDataSetChanged();

               btSimilarDeals.setSelected(true);

               lastSelected = btSimilarDeals;

           }
           else {
               btSimilarDeals.setVisibility(View.GONE);
           }

           if(genericDeal.nearbyDeals.size() < 1)
           {
               btNearbyDeals.setVisibility(View.GONE);
           }
       }

       this.genericDeal = genericDeal;


       if(genericDeal.is_exclusive == 0 || mDeal.isQticket == 1)
       {
           return;
       }

       if(genericDeal.vouchers_claimed == 0)
       {
           tvVoucherClaimed.setVisibility(View.VISIBLE);
           tvVoucherClaimed.setText(R.string.discount_not_availed);

           return;
       }

       if(genericDeal.vouchers_claimed == genericDeal.vouchers_max_allowed)
       {
           rlVoucher.setVisibility(View.GONE);
       }

       tvVoucherClaimed.setVisibility(View.VISIBLE);

       tvVoucherClaimed.setText(getString(R.string.out_of)+" " + genericDeal.vouchers_max_allowed
               + ", " + getString(R.string.you_have_availed) + " "
               + ( genericDeal.vouchers_claimed) + " " + getString(R.string.dealss));

      /* if(genericDeal.vouchers_claimed >= genericDeal.vouchers_max_allowed)
       {
           //rlVoucher.setVisibility(View.GONE);
       }
       else
       {
           tvVoucherClaimed.setVisibility(View.VISIBLE);

           tvVoucherClaimed.setText("Out of " + genericDeal.vouchers_max_allowed
                   + ", you have availed "
                   + (genericDeal.vouchers_claimed) + " deals.");
       }*/

//
      // similarAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);
       //similarAdapter.setListingType("deals");

       //listView.setAdapter(similarAdapter);
   }

    public void onNoData()
    {
        llSimilarNearby.setVisibility(View.GONE);
    }

    @Override
    public void onUpdated(GenericDeal deal, String value) {
        updateOutlet(deal, value);
    }


}