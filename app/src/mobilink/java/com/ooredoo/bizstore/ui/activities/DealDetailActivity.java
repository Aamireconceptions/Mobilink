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
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.wallet.fragment.BuyButtonAppearance;
import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.asynctasks.LocationsTask;
import com.ooredoo.bizstore.asynctasks.RedeemViaSmsTask;
import com.ooredoo.bizstore.asynctasks.ReportAsyncTask;
import com.ooredoo.bizstore.asynctasks.VerifyMerchantCodeTask;
import com.ooredoo.bizstore.interfaces.LocationNotifies;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
//import com.ooredoo.bizstore.utils.ScrollViewHelper;
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
public class DealDetailActivity extends BaseActivity implements OnClickListener

{

    public String category;
    static String packageName;
    public boolean showBanner = false;
    public EditText etMerchantCode;

    RelativeLayout rlMerchandCode;

    private String qticketUrl = "https://www.q-tickets.com/";

   // public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    ScrollView scrollView;

    private Button btGetCode;

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

    private  MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private SnackBarUtils snackBarUtils;

    ListViewBaseAdapter commonAdapter;
    public DealDetailActivity() {
        super();
        layoutResId = R.layout.frag_deal_detail;
    }

    TextView tvVoucherClaimed;

    Tracker tracker;

    @Override
    public void init() {

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

    TextView tvPrices;
    ScrollViewListener mOnScrollViewListener;
    LinearLayout llHead, llDiscount;
    TextView tvHeadTitle, tvHeadDescription;
    TableLayout tableLayout;
    RelativeLayout rlDetails;

TextView tvDiscount;

    private void initViews()
    {
        scrollView=(ScrollView) findViewById(R.id.scrollView1);
         cd = new ColorDrawable(getResources().getColor(R.color.red));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        llHead = (LinearLayout) findViewById(R.id.head);
        tvHeadTitle = (TextView) findViewById(R.id.head_title);
        FontUtils.setFontWithStyle(this, tvHeadTitle, Typeface.BOLD);
        tvHeadDescription = (TextView) findViewById(R.id.head_description);

        tvPrices = (TextView) findViewById(R.id.prices);

        tvVoucherClaimed = (TextView) findViewById(R.id.vouchers_claimed);

        ivVerifyMerchantCode = (ImageView) findViewById(R.id.verify_merchant_code);
        ivVerifyMerchantCode.setOnClickListener(this);

        rlMerchandCode = (RelativeLayout) findViewById(R.id.merchant_code_layout);

        etMerchantCode = (EditText) findViewById(R.id.merchant_code_field);
        etMerchantCode.setMaxWidth(etMerchantCode.getWidth());
        etMerchantCode.setMaxLines(4);

        FontUtils.setFontWithStyle(this, etMerchantCode, Typeface.BOLD);

        Logger.print("etMerchant Code width:"+etMerchantCode.getWidth());

        packageName = getPackageName();
        if(genericDeal != null) {

            id = genericDeal.id;

            Logger.logI("DETAIL_ID", valueOf(id));
        }

        llDiscount = (LinearLayout) findViewById(R.id.discount_layout);

        tableLayout = (TableLayout) findViewById(R.id.table);

        rlDetails = (RelativeLayout) findViewById(R.id.ll_details);

        mOnScrollViewListener = new ScrollViewListener(mActionBar);

        category = intent.getStringExtra(CATEGORY);

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

       ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
       if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        findViewById(R.id.tv_call).setOnClickListener(this);
        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.tv_rate).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        btGetCode = (Button) findViewById(R.id.get_code);
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
    }                    //initViews function End.

    ImageView ivDetail;
    ProgressBar progressBar;
    LinearLayout llDirections;
    RelativeLayout rlHeader;

    RelativeLayout rlVoucher;
    GenericDeal mDeal;

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

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

            cd = new ColorDrawable(getResources().getColor(R.color.red));
            mActionBar.setBackgroundDrawable(cd);

            cd.setAlpha(0);

            mActionBar.setTitle(deal.title);

            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {


                @Override
                public void onScrollChanged() {
                    cd.setAlpha(getAlphaforActionBar(scrollView.getScrollY()));
                }
                private int getAlphaforActionBar(int scrollY) {
                    int minDist = 0, maxDist = 550;
                    if (scrollY > maxDist) {
                        return 255;
                    } else {
                        if (scrollY < minDist) {
                            return 0;
                        } else {
                            return (int) ((255.0 / maxDist) * scrollY);
                        }
                    }
                }
            });

            rlHeader = (RelativeLayout) findViewById(R.id.rl_header);

            TextView tvBrand = (TextView) findViewById(R.id.brand_name);
            tvBrand.setText(deal.businessName);

            TextView tvBrandAddress = (TextView) findViewById(R.id.brand_address);
            tvBrandAddress.setText(deal.address);

            TextView tvValidity = (TextView) findViewById(R.id.validity);
           tvValidity.setTextDirection(View.TEXT_DIRECTION_RTL);
            tvValidity.setText(getString(R.string.deal_valid_till) + " " + deal.endDate);

            llDirections = (LinearLayout) findViewById(R.id.directions_layout);
            llDirections.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDirections();
                }
            });

            if(deal.how_works != null && !deal.how_works.isEmpty())
            {
                TextView tvHTWNote = (TextView) findViewById(R.id.how_this_work_note);

                tvHTWNote.setVisibility(View.VISIBLE);
                tvHTWNote.setOnClickListener(this);
            }

            if(deal.terms_services != null && !deal.terms_services.isEmpty())
            {
                TextView tvTermsServices = (TextView) findViewById(R.id.tos_note);

                tvTermsServices.setVisibility(View.VISIBLE);
                tvTermsServices.setOnClickListener(this);
            }


            if(isNotNullOrEmpty(deal.category) && deal.category.contains(".")) {
                deal.category = deal.category.replace(".", ",");
            }
            TextView tvTitle = ((TextView) findViewById(R.id.title));
            tvTitle.setText(deal.title);

            FontUtils.setFontWithStyle(this, tvTitle, Typeface.BOLD);

            ((TextView) findViewById(R.id.description)).setText(deal.description);

            ((RatingBar) findViewById(R.id.rating_bar)).setRating(deal.rating);


            findViewById(R.id.iv_views).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DealDetailActivity.this, getString(R.string.deal_has_been_viewed) + " "+deal.views + " " + getString(R.string.times), Toast.LENGTH_SHORT).show();
                }
            });

            TextView tvView = ((TextView) findViewById(R.id.tv_views));
            final Dialog dialog = new Dialog(DealDetailActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.report_dialog_box);
          //  dialog.setTitle("Report Box");

            radioGroup=(RadioGroup) dialog.findViewById(R.id.radioGroup);
            someOther=(RadioButton) dialog.findViewById(R.id.someOther_radioButton);
            FontUtils.setFont(this, someOther);
            other=(RadioButton)  dialog.findViewById(R.id.other_radioButton);
            FontUtils.setFont(this, other);
            reportBtn_dialogBox=(Button) dialog.findViewById(R.id.reportButton_report_dialog_box);
            FontUtils.setFont(this, reportBtn_dialogBox);
            titleReportBox=(TextView) dialog.findViewById(R.id.title_reportBox);
            FontUtils.setFontWithStyle(this,titleReportBox,Typeface.BOLD);
            enterReport=(EditText) dialog.findViewById(R.id.enterReport_editText);

            tvView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(DealDetailActivity.this, getString(R.string.deal_has_been_viewed) + " "+deal.views+ " " + getString(R.string.times), Toast.LENGTH_SHORT).show();
                    // RadioGroup Check Listener.
                 radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if(checkedId==R.id.someOther_radioButton) {
                                enterReport.setVisibility(View.GONE);
                                reportMesage=(String) someOther.getText();
                         //       Toast.makeText(DealDetailActivity.this,reportMesage, Toast.LENGTH_SHORT).show();
                               reportEdittxt_Check=1;
                            }
                            else if(checkedId==R.id.other_radioButton) {
                                enterReport.setVisibility(View.VISIBLE);
                               reportMesage="";
                               reportEdittxt_Check=2;
                            }

                        }
                 }); // End RadioGroup Check Listener.


                    reportBtn_dialogBox.setOnClickListener(new OnClickListener() {     // Report button on click listener.
                        @Override
                        public void onClick(View v) {


                            if(reportEdittxt_Check==2) {
                                reportMesage=enterReport.getText().toString();
                                if (reportMesage.matches("")) {
                                    Toast.makeText(getApplicationContext(), "Please type the report", Toast.LENGTH_SHORT).show();
                                } else {
                                //    Toast.makeText(getApplicationContext(), "Message is:" + reportMesage, Toast.LENGTH_SHORT).show();
                                    new ReportAsyncTask(DealDetailActivity.this,reportMesage,genericDeal.businessId,genericDeal.id).execute();
                                    dialog.dismiss();
                                }
                            }
                            else if(reportEdittxt_Check==1 )  {
                                  // Toast.makeText(getApplicationContext(), "Message is: without" + reportMesage, Toast.LENGTH_SHORT).show();
                                    new ReportAsyncTask(DealDetailActivity.this,reportMesage,genericDeal.businessId,genericDeal.id).execute();
                                    dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please Select the one option", Toast.LENGTH_SHORT).show();
                            }

                            enterReport.setText("");

                        }
                    });

                    dialog.show();

                }
            });   // Report button onClick Listener End.

            rlVoucher = (RelativeLayout) findViewById(R.id.voucher_layout);

            if(deal.is_exclusive == 0) {
                rlVoucher.setVisibility(View.GONE);
                tvDiscount.setVisibility(View.GONE);
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
                TextView tvBrandTxt = (TextView) findViewById(R.id.brand_txt);
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

            ivDetail = (ImageView) findViewById(R.id.detail_img);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
                    fallBackToDiskCache(imgUrl, ivDetail);
                }
            }

            tvDiscount.setText(discount);
            FontUtils.setFontWithStyle(this, tvDiscount, Typeface.BOLD);

        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }

    }    //populateData function end.

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
        } else if(viewId == R.id.share || viewId == R.id.share) {

            shareDeal(this, id);
        }
        else
        if(viewId == R.id.get_code)
        {
            if(BuildConfig.FLAVOR.equals("mobilink"))
            {
                /*IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.initiateScan();*/

                VerifyMerchantCodeTask verifyMerchantCodeTask =
                        new VerifyMerchantCodeTask(this, snackBarUtils, tracker);
                verifyMerchantCodeTask
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                String.valueOf(id), "", String.valueOf(mDeal.businessId));

              //  startActivityForResult(new Intent(this, CaptureActivity.class), 303);

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
                            if(llHead.getVisibility()==View.GONE){
                                llHead.setVisibility(View.VISIBLE);

                                tvHeadTitle.setText(R.string.how_this_works);
                                tvHeadDescription.setText(mDeal.how_works);
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
    }

    public void showCode(int voucherClaimed, int maxAllowed, boolean hide)
    {
        if(hide)
        {

            etMerchantCode.setText("");
            rlMerchandCode.setVisibility(View.GONE);

            btGetCode.setVisibility(View.VISIBLE);
        }

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
   }

    public void onNoData()
    {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 303 && resultCode == RESULT_OK)
        {
            etMerchantCode.setText(data.getStringExtra("code"));

            Logger.print("Code: "+data.getStringExtra("code"));

            btGetCode.setVisibility(View.GONE);

            rlMerchandCode.setVisibility(View.VISIBLE);
        }
    }
}