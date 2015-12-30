package com.ooredoo.bizstore.ui.activities;

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
import android.os.Environment;
import android.os.SystemClock;
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
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.ProfilePicDownloadTask;
import com.ooredoo.bizstore.asynctasks.UpdateAccountTask;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.FileUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    boolean hasUserSelectedPic, canEditDp = true, canEditName;

    EditText etName, etNumber;

    ImageView ivProfilePic;

    public MyAccountActivity() {
        super();
        layoutResId = R.layout.activity_my_account;

        bitmapProcessor = new BitmapProcessor();
    }

    ImageView ivEdit;
    @Override
    public void init() {
        setupToolbar();
        final ImageView ivEditName = (ImageView) findViewById(R.id.iv_edit_name);
        etName = (EditText) findViewById(R.id.et_name);
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ivEditName.performClick();
                    etName.clearFocus();


                    //etName.setFocusable(false);
                    // ivEditName.setFocusableInTouchMode(false);
                }
                return false;
            }
        });
        etNumber = (EditText) findViewById(R.id.et_number);

        FontUtils.setFont(this, BizStore.getLanguage().equals("en") ? BizStore.DEFAULT_FONT
        : BizStore.ARABIC_DEFAULT_FONT, etNumber);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        findViewById(R.id.iv_edit_dp).setOnClickListener(this);

        ivEditName.setOnClickListener(this);

        if(userAccount != null && isNotNullOrEmpty(userAccount.name)) {
            etName.setText(userAccount.name);
        }

        etNumber.setText(BizStore.username);

        loadPicture();
    }

    private void loadPicture() {
        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null) {

            ivProfilePic.setImageBitmap(bitmap);
            /*Bitmap rotatedBitmap = bitmap;
            try {
                rotatedBitmap = rotateBitmapIfNeeded(PROFILE_PIC_URL, bitmap);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            ivProfilePic.setImageBitmap(rotatedBitmap);*/
        } else {
            int width = (int) convertDpToPixels(250);
            int height = width;
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            //ExecutorService executorService = Executors.newSingleThreadExecutor();
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
                // etName.requestFocus();
                ivEdit.setImageResource(canEditName ? R.drawable.ic_done_white : R.drawable.ic_edit_white);
            }

            if(canEditName) {
                etName.setFocusable(true);
               etName.requestFocus();


                /*etName.performClick();

                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
              //  keyboard.showSoftInput(etName, 0);

                keyboard.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);*/

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

                /*InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                //  keyboard.showSoftInput(etName, 0);

                keyboard.hideSoftInputFromInputMethod(etName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
               // etName.clearFocus();

                String name = etName.getText().toString();
                if(isNotNullOrEmpty(name)) {
                    isValidName = true;
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


               // etName.setFocusable(false);
            }



        } else if(viewId == R.id.iv_edit_dp) {
           // canEditDp = !canEditDp;
            ivEdit = (ImageView) v;
            ivEdit.setImageResource(R.drawable.ic_edit_white);
            //ivEdit.setImageResource(canEditDp ? R.drawable.ic_done_white : R.drawable.ic_edit_white);
            if(canEditDp) {
                takePicture();
            } else {
                if(hasUserSelectedPic) {
                    uploadImageToServer(path);
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

        /*Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra("return-data", true);*/

        /*List<Intent> cameraIntent = new ArrayList<Intent>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)*/

        Intent pickIntent = new Intent();
        pickIntent.setType("image");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        //pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

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

        //hasUserSelectedPic = true;

        /*if(resultCode == RESULT_OK && null != intent) {

            Uri selectedImage = intent.getData();

            String[] orientationColumn = { MediaStore.Images.Media.ORIENTATION };
            Cursor cur = managedQuery(selectedImage, orientationColumn, null, null, null);
            if(cur != null && cur.moveToFirst()) {
                rotation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
            }

            path = selectedImage.getPath();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            Logger.logI("PIC_WIDTH", String.valueOf(photoW));
            Logger.logI("PIC_HEIGHT", String.valueOf(photoH));

            int targetHeight = (int) Converter.convertDpToPixels(225);
            int scaleFactor = Math.min(photoW, photoH / targetHeight);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            int height = (int) Converter.convertDpToPixels(225);
            // decode with inSampleSize
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), height, true);
            Log.i("PIC", path);

        } else {

            switch(resultCode) {
                case 0:
                    hasUserSelectedPic = false;
                    Log.i("SonaSys", "User cancelled");
                    break;
                case -1:
                    processImage();
                    break;
            }
        }*/

        if(resultCode == RESULT_OK)
        {
            if(intent != null)
            {

                String action = intent.getAction();

                Logger.print("XYZ: "+action);

                if(action == null)
                {
                    path = intent.getData().getPath();

                    Logger.print("action was null; "+path);
                }
            }

            Resources res= getResources();

            try {
                hasUserSelectedPic = true;
                canEditDp = false;
                ivEdit.setImageResource(R.drawable.ic_done_white);
                bitmap = bitmapProcessor.decodeSampledBitmapFromFile(path,
                        (int)Converter.convertDpToPixels(600),
                        (int)Converter.convertDpToPixels(600));

                FileUtils.saveBitmap(bitmap, path);

                ivProfilePic.setImageBitmap(bitmap);
                profilePicture.setImageBitmap(bitmap);
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

       /* switch(resultCode) {
            case 0:
                hasUserSelectedPic = false;
                Log.i("SonaSys", "User cancelled");
                break;
            case -1:
                processImage();
                break;
        }*/
    }

    private void processImage() {
        Bitmap bitmap = decodeFile(path);

        int height = (int) Converter.convertDpToPixels(225);

        Bitmap rotatedBitmap = bitmap;
        try {
            rotatedBitmap = rotateBitmapIfNeeded(path, bitmap);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        // decode with inSampleSize
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth(), height, true);

        ivProfilePic.setImageBitmap(rotatedBitmap);
        profilePicture.setImageBitmap(rotatedBitmap);

        //cropImage();
    }

    boolean isCropEnabled = false;

    private void cropImage() {

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        cropIntent.setType("image/*");
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("outputX", 1440);
        cropIntent.putExtra("outputY", 1440);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", "true");
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 1);
        isCropEnabled = true;
    }

    public void uploadImageToServer(String path) {
        Logger.print("IMG_PATH: " + path);
        UpdateAccountTask uploadTask = new UpdateAccountTask(this, null, path);
        uploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.my_account);
    }

    public void updateProfilePicture() {

        Toast.makeText(this, R.string.profile_update, Toast.LENGTH_SHORT).show();

        MemoryCache memoryCache = MemoryCache.getInstance();
        memoryCache.removeBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null){
        memoryCache.addBitmapToCache(PROFILE_PIC_URL, bitmap);

        int reqWidth = (int) convertDpToPixels(200);
        int reqHeight = reqWidth;
        ProfilePicDownloadTask profilePicDownloadTask = new ProfilePicDownloadTask(profilePicture, null);
        profilePicDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, PROFILE_PIC_URL,
                valueOf(reqWidth), valueOf(reqHeight));
        }

        /*loadPicture();

        Bitmap dpBitmap = memoryCache.getBitmapFromCache(PROFILE_PIC_URL);

        if(dpBitmap != null) {
            profilePicture.setImageBitmap(dpBitmap);
        } else {
            int reqWidth = (int) convertDpToPixels(225);
            int reqHeight = reqWidth;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(profilePicture, null);
            bitmapDownloadTask.executeOnExecutor(executorService, PROFILE_PIC_URL, valueOf(reqWidth), valueOf(reqHeight));
        }*/
    }

    private Bitmap decodeFile(String path) {
        int orientation;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getRealMetrics(displayMetrics);

        if(path == null) {
            return null;
        }

        try {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            Logger.logI("PIC_WIDTH", String.valueOf(photoW));
            Logger.logI("PIC_HEIGHT", String.valueOf(photoH));

            int targetHeight = (int) Converter.convertDpToPixels(225);
            int scaleFactor = Math.min(photoW, photoH / targetHeight);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            // decode with inSampleSize
            Bitmap bm = BitmapFactory.decodeFile(path, bmOptions);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);

            Log.e("ExifInteface ........." + width, "orientation =" + orientation);

            Matrix m = new Matrix();

            m.postRotate(rotation);

            if(orientation == ExifInterface.ORIENTATION_NORMAL) {
                Log.e("ORIENTATION", "NORMAL");
                m.postRotate(0);
            } else if((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.preRotate(180);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(0);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch(OutOfMemoryError e) {
            MemoryCache.getInstance().tearDown();
            return null;
        } catch(IOException ioe) {
            return null;
        }
    }

    public void showMessage() {
        new SnackBarUtils(this, ivProfilePic).showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
    }
}