package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GalleryStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.DealDetailMiscTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.FileDownloadTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.asynctasks.ReportAsyncTask;
import com.ooredoo.bizstore.asynctasks.VerifyMerchantCodeTask;
import com.ooredoo.bizstore.dialogs.MsisdnDialog;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.Gallery;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.ImageViewerFragment;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FBUtils;
import com.ooredoo.bizstore.utils.FileUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.FragmentUtils;
import com.ooredoo.bizstore.utils.IntentUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.io.File;
import java.util.List;

import bolts.AppLinks;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.DIALER_PREFIX;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;


/**
 * @author Muhammad Babar
 */

@EActivity
public class DealDetailActivity extends BaseActivity implements OnClickListener, LocationListener

{
    public String category;
    static String packageName;
    public boolean showBanner = false;


    private String qticketUrl = "https://www.q-tickets.com/";

   // public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    @ViewById(R.id.scrollView1)
    ScrollView scrollView;

    @ViewById(R.id.get_code)
    public static Button btGetCode;

    RadioGroup radioGroup;    // declare in report_dialog_box.

    RadioButton someOther;    // declare in report_dialog_box.

    RadioButton other;        // declare in report_dialog_box.

    EditText enterReport;    // declare in report_dialog_box.

    String reportMesage;     // Use for send the report according to deal.

    TextView titleReportBox;

    int reportEdittxt_Check=0;

    Button reportBtn_dialogBox;

    private ImageView ivVerifyMerchantCode;

    ColorDrawable cd;

    private int id = -1;

    public Deal src;

    boolean isFav = false;

    public static GenericDeal selectedDeal, genericDeal;

    private Dialog ratingDialog;

    public MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private SnackBarUtils snackBarUtils;

    ListViewBaseAdapter commonAdapter;
    public DealDetailActivity() {
        super();
        layoutResId = R.layout.frag_deal_detail;
    }

    @ViewById(R.id.vouchers_claimed)
    TextView tvVoucherClaimed;

    Tracker tracker;

    int reqWidth, reqHeight;
    @Override
    public void init() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        reqWidth = displayMetrics.widthPixels;
        reqHeight = displayMetrics.heightPixels / 2;

        String username = SharedPrefUtils.getStringVal(this, "username");
        String password = SharedPrefUtils.getStringVal(this, "password");
        String secret = SharedPrefUtils.getStringVal(this, "secret");

        if(!username.equals(SharedPrefUtils.EMPTY)) {
            BizStore.username = username;
        }

        if(!password.equals(SharedPrefUtils.EMPTY)) {
            BizStore.password = password;
        }

        BizStore.secret = secret;

        diskCache.requestInit(this);

        handleIntentFilter();

        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.print("DEAL_DETAIL: onNewIntent");

        setIntent(intent);

        handleIntentFilter();
    }
    private boolean isNotification = false;
    private void handleIntentFilter() {
        Intent intent = getIntent();

        Logger.print("HandleIntentFilter");

        if(intent != null) {
            Uri uri = intent.getData();

            if(uri != null)
            {
                String paramId = uri.getQueryParameter("id");

                Logger.print("Extras: " + paramId);

                if(paramId == null)
                {
                    Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
                    String fbParamId = targetUrl.getQueryParameter("id");

                    String decodedFbParamId = CryptoUtils.decodeBase64(fbParamId);

                    Logger.print("FB targetURL: "+targetUrl+" encoded id: "+fbParamId+", decodedId:"+decodedFbParamId);

                    getIntent().putExtra(AppConstant.ID, Integer.parseInt(decodedFbParamId));
                }
                else
                {
                    getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
                }

            }

            id = intent.getIntExtra(AppConstant.ID, 0);
            isNotification = intent.getBooleanExtra("notification", false);
            getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);

        }
    }

    @ViewById(R.id.prices)
    TextView tvPrices;

    ScrollViewListener mOnScrollViewListener;

    @ViewById(R.id.head)
    LinearLayout llHead;

    @ViewById(R.id.discount_layout)
    LinearLayout llDiscount;

    @ViewById(R.id.head_title)
    TextView tvHeadTitle;

    @ViewById(R.id.head_description)
    TextView tvHeadDescription;

    @ViewById(R.id.table)
    TableLayout tableLayout;

    @ViewById(R.id.ll_details)
    RelativeLayout rlDetails;

    @ViewById(R.id.brochure)
    TextView tvBrochure;

    File cacheDir;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.progressBar)
    ProgressBar progressBar;

    private void initViews()
    {
        tvBrochure.setOnClickListener(this);

        cacheDir = FileUtils.getDiskCacheDir(this, "Pdf Docs");

        if(!cacheDir.exists())
        {
            Logger.print("Creating Dir: ");

            cacheDir.mkdir();
        }
        else
        {
            Logger.print("Dir Exists: ");
        }

        Logger.print("Cache Dir: " + cacheDir);

        cd = new ColorDrawable(getResources().getColor(R.color.red));

        setSupportActionBar(toolbar);

        mActionBar  = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        BizStore bizStore = (BizStore) getApplication();
        tracker = bizStore.getDefaultTracker();

        tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Action")
        .setAction("Deal Detail")
        .build());

        genericDeal = (GenericDeal) intent.getSerializableExtra("generic_deal");

        FontUtils.setFontWithStyle(this, tvHeadTitle, Typeface.BOLD);

        packageName = getPackageName();
        if(genericDeal != null) {

            id = genericDeal.id;

            Logger.logI("DETAIL_ID", valueOf(id));
        }

        mOnScrollViewListener = new ScrollViewListener(mActionBar);

        category = intent.getStringExtra(CATEGORY);

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

       if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        findViewById(R.id.tv_call).setOnClickListener(this);
        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.tv_rate).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        btGetCode.setOnClickListener(this);

        FontUtils.setFontWithStyle(this, btGetCode, Typeface.BOLD);


        if(genericDeal == null) {
            DealDetailTask dealDetailTask = new DealDetailTask(this, null, snackBarUtils);
            dealDetailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
        }
        else
        {
            populateData(genericDeal);
        }
    }

    @ViewById(R.id.detail_img)
    ImageView ivDetail;

    @ViewById(R.id.directions_layout)
    LinearLayout llDirections;

    @ViewById(R.id.rl_header)
    RelativeLayout rlHeader;

    GenericDeal mDeal;
    @ViewById(R.id.tv_share)
    TextView tvAvailedDeals;

    FBUtils fbUtils;

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @ViewById(R.id.directions)
    TextView tvDistance;

    @ViewById(R.id.time_stamp)
    TextView tvTimeStamp;

    @ViewById(R.id.discount_availed)
    TextView tvDiscountAvailed;

    @ViewById(R.id.last_redeemed_layout)
    LinearLayout llTimeStamp;

    @ViewById(R.id.brand_name)
    TextView tvBrand;

    @ViewById(R.id.brand_address)
    TextView tvBrandAddress;

    @ViewById(R.id.validity)
    TextView tvValidity;

    @ViewById(R.id.how_this_work_note)
    TextView tvHTWNote;

    @ViewById(R.id.tos_note)
    TextView tvTermsServices;

    @ViewById(R.id.description)
    TextView tvDescription;

    @ViewById(R.id.rating_bar)
    RatingBar ratingBar;

    @ViewById(R.id.tv_views)
    TextView tvView;

    @ViewById(R.id.iv_views)
    ImageView ivViews;

    @ViewById(R.id.brand_logo)
    ImageView ivBrandLogo;

    @ViewById(R.id.brand_txt)
    TextView tvBrandTxt;

    private long minTimeMillis = 10 * 1000;
    private float distanceMeters = 0;
    LocationManager locationManager;
     Dialog reportDialog;
    public void populateData(final GenericDeal deal) {
        if(deal != null) {

            fbUtils = new FBUtils(this);
            fbUtils.init();

            if(HomeActivity.lat != 0 && HomeActivity.lng != 0 )
            {
                userLocation = new Location("");
                userLocation.setLatitude(HomeActivity.lat);
                userLocation.setLongitude(HomeActivity.lng);
            }

            if(deal.latitude != 0 && deal.longitude != 0) {
                 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMillis, distanceMeters, this);

                }

                if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMillis,
                            distanceMeters, this);
                }
            }

            FontUtils.setFontWithStyle(this, tvDiscountAvailed, Typeface.BOLD);
            FontUtils.setFont(this, tvTimeStamp);

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



            src = new Deal(deal);
            src.id = deal.id;

            String type;

            if(isNotification)
            {
                type = "notification";
            }
            else
            {
                type = "deals";
            }

            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, type, id);
            incrementViewsTask.execute();

            tvBrand.setText(deal.businessName);

            tvBrandAddress.setText(deal.address);

            tvValidity.setText("Validity Date: " + deal.endDate);

            llDirections.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDirections();
                }
            });

            if(deal.distance == 0)
            {
                if(HomeActivity.lat != 0 && HomeActivity.lng != 0
                        && deal.latitude != 0 && deal.longitude != 0)
                {
                    float results[] = new float[3];

                    Location.distanceBetween(HomeActivity.lat, HomeActivity.lng,
                            deal.latitude, deal.longitude, results);

                    float distance = results[0];

                    deal.distance = Double.parseDouble(String.format("%.1f", distance / 1000));
                }
            }

            if(deal.distance != 0)
            {
                llDirections.setVisibility(View.VISIBLE);

                tvDistance.setText(String.format("%.1f", deal.distance) + " km");
            }

            if(deal.how_works != null && !deal.how_works.isEmpty())
            {
                tvHTWNote.setVisibility(View.VISIBLE);
                tvHTWNote.setOnClickListener(this);
            }

            if(deal.terms_services != null && !deal.terms_services.isEmpty())
            {
                tvTermsServices.setOnClickListener(this);
            }

            if(isNotNullOrEmpty(deal.category) && deal.category.contains(".")) {
                deal.category = deal.category.replace(".", ",");
            }

            tvDescription.setText(deal.description);

            ratingBar.setRating(deal.rating);

            reportDialog = new Dialog(DealDetailActivity.this);
            reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            reportDialog.setContentView(R.layout.report_dialog_box);

            radioGroup=(RadioGroup) reportDialog.findViewById(R.id.radioGroup);
            someOther=(RadioButton) reportDialog.findViewById(R.id.someOther_radioButton);
            FontUtils.setFont(this, someOther);
            other=(RadioButton)  reportDialog.findViewById(R.id.other_radioButton);
            FontUtils.setFont(this, other);
            reportBtn_dialogBox=(Button) reportDialog.findViewById(R.id.reportButton_report_dialog_box);
            FontUtils.setFont(this, reportBtn_dialogBox);
            titleReportBox=(TextView) reportDialog.findViewById(R.id.title_reportBox);
            FontUtils.setFontWithStyle(this,titleReportBox,Typeface.BOLD);
            enterReport=(EditText) reportDialog.findViewById(R.id.enterReport_editText);

            ivViews.setOnClickListener(this);
            tvView.setOnClickListener(this);

            if(deal.isQticket == 1)
            {
                btGetCode.setText(R.string.get_qticket);
            }

            Logger.print("businessId Deal,"+deal.businessId);

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
                    new CommonHelper().fallBackToDiskCache(this, imgUrl, diskCache, memoryCache,
                            ivBrandLogo, progressBar, reqWidth, reqHeight);
                }
            }
            else
            {
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

                if(bitmap != null) {
                    ivDetail.setImageBitmap(bitmap);

                    ivDetail.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorUtils.startDetailAnimation(rlDetails, tableLayout, llDiscount);
                        }
                    });
                }
                else
                {
                    new CommonHelper().fallBackToDiskCache(this, imgUrl, diskCache, memoryCache,
                            ivDetail, progressBar, reqWidth, reqHeight);
                }
            }

        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }

        DealDetailMiscTask detailMiscTask = new DealDetailMiscTask(this, null, null, null);
        detailMiscTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(deal.id),
                String.valueOf(deal.businessId));

    }    //populateData function end.


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:

                sendResult();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


public static MsisdnDialog dialog;
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

            }
        }
        else if(viewId == R.id.iv_rate || viewId == R.id.tv_rate) {
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
        } else if(viewId == R.id.share || viewId == R.id.share) {

            shareDeal(this, id);
        }
        else
        if(viewId == R.id.get_code)
        {
            if(!BizStore.username.isEmpty()) {

                if(genericDeal.latitude != 0 && genericDeal.longitude != 0) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        DialogUtils.createLocationDialog(this,
                                "Dear user, Please turn on GPS to avail discount").show();
                        return;
                    }
                }

                if (userLocation != null && mDeal.latitude != 0 && mDeal.longitude != 0) {
                    float results[] = new float[3];

                    Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                            mDeal.latitude, mDeal.longitude, results);

                    if (results[0] >= 250) {
                        DialogUtils.createAlertDialog(this, 0, R.string.error_out_of_range).show();

                        return;
                    }

                    VerifyMerchantCodeTask verifyMerchantCodeTask =
                            new VerifyMerchantCodeTask(this, snackBarUtils, tracker, fbUtils);
                    verifyMerchantCodeTask
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    String.valueOf(id), "0", String.valueOf(mDeal.businessId));
                } else {
                    DialogUtils.createAlertDialog(this, 0, R.string.redeem_loc_error).show();
                }
            }
            else
            {
                dialog = MsisdnDialog.newInstance();
                dialog.show(getFragmentManager(), null);

            }
        }
        else
        if(viewId == R.id.directions)
        {
                    startDirections();
        }
        else
        if(viewId == R.id.how_this_work_note)
                        {
                            if(llHead.getVisibility()==View.GONE){
                                llHead.setVisibility(View.VISIBLE);

                                tvHeadTitle.setText(R.string.how_this_works);
                                tvHeadDescription.setText(mDeal.how_works);

                                scrollView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollView.smoothScrollTo(0, llHead.getBottom());
                                    }
                                }, 200);

                            }
                            else{
                                llHead.setVisibility(View.GONE);
                            }


                        }
        else
                            if(viewId == R.id.tos_note)
                            {
                                llHead.setVisibility(View.VISIBLE);

                                tvHeadTitle.setText(R.string.tos);
                                tvHeadDescription.setText(mDeal.terms_services);
                            }
        else
                                if(viewId == R.id.brochure || viewId == R.id.download)
                                {
                                    Object tag = tvBrochure.getTag();

                                    if(tag != null)
                                    {
                                        if(tag.equals("Downloading"))
                                        {
                                            return;
                                        }

                                        if(tag.equals("Downloaded")) {
                                            Intent intent = IntentUtils.getPdfIntent(brochureFile);
                                            try {
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(this, "No application found to open pdf,", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                    else
                                    {
                                        downloadFile();
                                    }
                                }
        else
                                    if(viewId == R.id.view_all)
                                    {
                                        FragmentUtils.addFragmentWithBackStack(this, android.R.id.content,
                                                ImageViewerFragment.newInstance(this.galleryList, 0), "gallery");
                                    }
        else
                                        if(viewId == R.id.iv_views || viewId == R.id.tv_views)
                                        {

                                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                                    if(checkedId==R.id.someOther_radioButton) {
                                                        enterReport.setVisibility(View.GONE);
                                                        reportMesage=(String) someOther.getText();
                                                        reportEdittxt_Check=1;
                                                    }
                                                    else if(checkedId==R.id.other_radioButton) {
                                                        enterReport.setVisibility(View.VISIBLE);
                                                        reportMesage="";
                                                        reportEdittxt_Check=2;
                                                    }

                                                }
                                            });


                                            reportBtn_dialogBox.setOnClickListener(new OnClickListener() {     // Report button on click listener.
                                                @Override
                                                public void onClick(View v) {


                                                    if(reportEdittxt_Check==2) {
                                                        reportMesage=enterReport.getText().toString();
                                                        if (reportMesage.matches("")) {
                                                            Toast.makeText(getApplicationContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            new ReportAsyncTask(DealDetailActivity.this,reportMesage,genericDeal.businessId,genericDeal.id).execute();
                                                            reportDialog.dismiss();
                                                        }
                                                    }
                                                    else if(reportEdittxt_Check==1 )  {

                                                        new ReportAsyncTask(DealDetailActivity.this,reportMesage,genericDeal.businessId,genericDeal.id).execute();
                                                        reportDialog.dismiss();
                                                    }
                                                    else {
                                                        Toast.makeText(getApplicationContext(), "Please Select the one option", Toast.LENGTH_SHORT).show();
                                                    }

                                                    enterReport.setText("");

                                                }
                                            });

                                            reportDialog.show();

                                        }
    }

    public void showCode(int voucherClaimed, int maxAllowed, boolean hide)
    {
        genericDeal.voucher_count = genericDeal.voucher_count + 1;

        tvAvailedDeals.setText(genericDeal.voucher_count + " AVAILED");

        if(genericDeal.date != null && !genericDeal.date.isEmpty())
        {
            llTimeStamp.setVisibility(View.VISIBLE);

            tvTimeStamp.setText("on "+genericDeal.date+ " at " + genericDeal.time);
        }

        if(hide)
        {
            btGetCode.setVisibility(View.VISIBLE);
        }

        tvVoucherClaimed.setVisibility(View.VISIBLE);

        if(voucherClaimed >= maxAllowed)
        {
            btGetCode.setVisibility(View.GONE);

            tvVoucherClaimed.setText(getString(R.string.already_availed_discount)+" " + maxAllowed + " " + getString(R.string.timess));

            return;
        }
        else
        {
            tvVoucherClaimed.setVisibility(View.VISIBLE);
        }

        tvVoucherClaimed.setText(getString(R.string.out_of) + " " + maxAllowed
                + ", " + getString(R.string.you_have_availed) + " "
                + (voucherClaimed) + " " + getString(R.string.dealss));

    }

    public static void shareDeal(Activity activity, long dealId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DEAL_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        String appName = activity.getString(R.string.app_name);

        uri = "View this awesome deal on " + appName + " http://jazzdiscountbazar.deal/deal_detail?id=" + dealId +
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

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

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

       if(locationManager != null) locationManager.removeUpdates(this);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();

            Logger.print("backpressed return");

            return;
        }

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

    File brochureFile;

   public GalleryStatePagerAdapter adapter;

    public List<Gallery> galleryList;

    @ViewById(R.id.download)
    ImageView ivDownload;

    @ViewById(R.id.gallery_items_count)
    TextView tvGalleryCount;

    @ViewById(R.id.view_all)
    TextView tvViewAll;

    @ViewById(R.id.gallery_layout)
    LinearLayout llGallery;

    @ViewById(R.id.gallery)
    ViewPager galleryPager;

    @ViewById(R.id.code_layout)
    LinearLayout llCode;

    @ViewById(R.id.brochure_layout)
    RelativeLayout rlBrochure;

    public void onHaveData(GenericDeal genericDeal)
    {
        if( genericDeal.galleryList != null && genericDeal.galleryList.size() > 0)
        {
            tvGalleryCount.setText(genericDeal.galleryList.size() + " Photos");

            tvViewAll.setOnClickListener(this);

            llGallery.setVisibility(View.VISIBLE);

            this.galleryList = genericDeal.galleryList;

            adapter = new GalleryStatePagerAdapter(getFragmentManager(), genericDeal.galleryList, true);

            galleryPager.setPageMargin((int) Converter.convertDpToPixels(4));
            galleryPager.setAdapter(adapter);
        }

        if(genericDeal.document != null && !genericDeal.document.isEmpty()) {

            fileUrl = BaseAsyncTask.SERVER_URL + genericDeal.document;

            rlBrochure.setVisibility(View.VISIBLE);

            ivDownload.setOnClickListener(this);

            brochureFile = new File(cacheDir, CryptoUtils.encodeToBase64(fileUrl) + ".pdf");

            String docName = "";

            if(genericDeal.documentName != null)
            {
                docName = genericDeal.documentName;
            }

            if (FileUtils.isFileAvailable(brochureFile)) {


                tvBrochure.setText("View "+docName);
                tvBrochure.setTag("Downloaded");
                ivDownload.setVisibility(View.GONE);
            } else {
                tvBrochure.setText("Download "+docName);
            }
        }



        tvAvailedDeals.setText(genericDeal.voucher_count + " AVAILED");

       if(genericDeal.date != null && !genericDeal.date.isEmpty())
       {
           llTimeStamp.setVisibility(View.VISIBLE);

           tvTimeStamp.setText("on "+genericDeal.date+ " at " + genericDeal.time);
       }

       this.genericDeal = genericDeal;

       if(genericDeal.is_exclusive == 0 || mDeal.isQticket == 1)
       {
           return;
       }
        llCode.setVisibility(View.VISIBLE);

       if(genericDeal.vouchers_claimed == 0)
       {
           tvVoucherClaimed.setVisibility(View.VISIBLE);
           tvVoucherClaimed.setText(R.string.discount_not_availed);

           return;
       }

       if(genericDeal.vouchers_claimed == genericDeal.vouchers_max_allowed)
       {
           btGetCode.setVisibility(View.GONE);
       }
        else
       {
           btGetCode.setText("Get Discount Again");
       }

       tvVoucherClaimed.setVisibility(View.VISIBLE);

       tvVoucherClaimed.setText(getString(R.string.out_of) + " " + genericDeal.vouchers_max_allowed
               + ", " + getString(R.string.you_have_availed) + " "
               + (genericDeal.vouchers_claimed) + " " + getString(R.string.dealss));
   }

    public void onNoData()
    {
    }

    String fileUrl;
    private void downloadFile()
    {
        FileDownloadTask downloadTask = new FileDownloadTask(this, brochureFile, genericDeal.id,
                                                             fileUrl, tvBrochure, ivDownload);
        downloadTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fbUtils.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 303 && resultCode == RESULT_OK)
        {
            Logger.print("Code: "+data.getStringExtra("code"));

            btGetCode.setVisibility(View.GONE);
        }
    }

    Location userLocation;
    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;

        float results[] = new float[3];

        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                mDeal.latitude, mDeal.longitude, results);
        mDeal.distance = results[0] / 1000;

        tvDistance.setText(String.format("%.1f", mDeal.distance) +  " km");

        Logger.print("Distance: "+mDeal.distance);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}