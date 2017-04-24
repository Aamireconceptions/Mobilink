package com.ooredoo.bizstore.ui.activities;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ProfilePicDownloadTask;
import com.ooredoo.bizstore.asynctasks.UpdateAccountTask;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.FileUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.PROFILE_PIC_URL;
import static com.ooredoo.bizstore.AppData.userAccount;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;
import static com.ooredoo.bizstore.utils.BitmapProcessor.rotateBitmapIfNeeded;
import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.NAME;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
@EActivity
public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    boolean hasUserSelectedPic, canEditDp = true, canEditName;

    @ViewById(R.id.et_name)
    EditText etName;

    @ViewById(R.id.et_number)
    EditText etNumber;

    @ViewById(R.id.iv_profile_pic)
    ImageView ivProfilePic;

    public MyAccountActivity() {
        super();
        layoutResId = R.layout.activity_my_account;

        bitmapProcessor = new BitmapProcessor();
    }

    ImageView ivEdit;

    @ViewById(R.id.iv_edit_name)
    ImageView ivEditName;

    @ViewById(R.id.iv_edit_dp)
    ImageView ivEditDp;

    @Override
    public void init() {
        setupToolbar();

        FontUtils.setFont(this, etName);
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ivEditName.performClick();
                    etName.clearFocus();
                }
                return false;
            }
        });

        FontUtils.setFont(this, etNumber);

        ivEditDp.setOnClickListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEditDp.setBackgroundResource(R.drawable.masked_ripple_light);

            ivEditName.setBackgroundResource(R.drawable.masked_ripple_light);
        }

        ivEditName.setOnClickListener(this);

        if(userAccount != null && isNotNullOrEmpty(userAccount.name)) {
            etName.setText(userAccount.name);
        }

        String prefix = BuildConfig.FLAVOR.equals("ooredoo") ? "+974" : "+92";
        etNumber.setText(prefix + BizStore.username);

        loadPicture();
    }

    @ViewById(R.id.progress_bar)
    ProgressBar progressBar;

    private void loadPicture() {
        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null) {

            ivProfilePic.setImageBitmap(bitmap);

        } else {
            int width = (int) convertDpToPixels(250);
            int height = width;
            ProfilePicDownloadTask profilePicDownloadTask = new ProfilePicDownloadTask(ivProfilePic, progressBar);
            profilePicDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, PROFILE_PIC_URL, valueOf(width), valueOf(height));
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_edit_name) {
            boolean isValidName = false;
            canEditName = !canEditName;
            Log.i("NAME", etName.getText().toString());
            ImageView ivEdit = (ImageView) v;

            if(canEditName || isValidName) {
                etName.setFocusable(canEditName);
                etName.setFocusableInTouchMode(canEditName);
                ivEdit.setImageResource(canEditName ? R.drawable.ic_done_white : R.drawable.ic_edit_white);
            }

            if(canEditName) {
                etName.setFocusable(true);
               etName.requestFocus();
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 100;
                float x = 0.0f;
                float y = 0.0f;
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                int metaState = 0;
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_UP,
                        x,
                        y,
                        metaState
                );

// Dispatch touch event to view
                etName.dispatchTouchEvent(motionEvent);
                etName.setCursorVisible(true);
                etName.setSelection(etName.getText().length());
            } else {
                String name = etName.getText().toString();
                if(isNotNullOrEmpty(name)) {
                    ivEdit.setImageResource(R.drawable.ic_edit_white);
                    updateVal(this, NAME, name);
                    UpdateAccountTask uploadTask = new UpdateAccountTask(this, name, null);
                    uploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    etName.setCursorVisible(false);

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                }
                else
                {
                    String msg = getString(R.string.provide_name);
                    Snackbar.make(etName, msg, Snackbar.LENGTH_SHORT).show();
                }
            }



        } else if(viewId == R.id.iv_edit_dp) {
            ivEdit = (ImageView) v;
            ivEdit.setImageResource(R.drawable.ic_edit_white);
            if(canEditDp) {
                takePicture();
            } else {
                if(hasUserSelectedPic) {
                    uploadImageToServer(path);
                    canEditDp = true;
                } else {
                    String msg = getString(R.string.select_pic);
                    makeText(getApplicationContext(), msg, LENGTH_SHORT).show();
                }
            }
        }
    }

    int rotation = 0;
    String path;

    BitmapProcessor bitmapProcessor;
    public void takePicture() {
        rotation = 0;

        //"/storage/emulated/0/obs_user_dp.png";

        File dir = FileUtils.getDiskCacheDir(this, "profile_pic");

        if(!dir.exists())
        {
            dir.mkdir();
        }

        path = dir.getPath() + "/" + "user_dp.jpg";

        Logger.print("dp: "+path);

        Log.i("camera", "startCameraActivity()");
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        String pickTitle = "Select or take picture";

        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePhotoIntent});

        startActivityForResult(chooserIntent, 1);
    }

    Bitmap bitmap;
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        Log.i("RequestCode: " + requestCode, "resultCode: " + resultCode);

        if(resultCode == RESULT_OK)
        {
            if(intent != null)
            {
                String action = intent.getAction();

                Logger.print("XYZ: "+action);

                if(action == null)
                {
                    path = getPath(this, intent.getData());

                    Logger.print("action was null; "+path);
                }
            }

            try {
                hasUserSelectedPic = true;
                canEditDp = false;
                ivEdit.setImageResource(R.drawable.ic_done_white);
                bitmap = bitmapProcessor.decodeSampledBitmapFromFile(path,
                        (int)Converter.convertDpToPixels(600),
                        (int)Converter.convertDpToPixels(600));

                FileUtils.saveBitmap(bitmap, path);

                ivProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            ivEdit.setImageResource(R.drawable.ic_edit_white);
            canEditDp = true;

            hasUserSelectedPic = false;
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void uploadImageToServer(String path) {
        Logger.print("IMG_PATH: " + path);
        UpdateAccountTask uploadTask = new UpdateAccountTask(this, null, path);
        uploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @ViewById
    Toolbar toolbar;

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.my_account);
    }

    public void updateProfilePicture() {

        Toast.makeText(this, R.string.profile_update, Toast.LENGTH_SHORT).show();

        MemoryCache memoryCache = MemoryCache.getInstance();
        memoryCache.removeBitmapFromCache(PROFILE_PIC_URL);

        int reqWidth = (int) convertDpToPixels(200);
        int reqHeight = reqWidth;
        ProfilePicDownloadTask profilePicDownloadTask = new ProfilePicDownloadTask(profilePicture, null);
        profilePicDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, PROFILE_PIC_URL,
                valueOf(reqWidth), valueOf(reqHeight));
    }

    public void showMessage() {
        new SnackBarUtils(this, ivProfilePic).showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
    }
}