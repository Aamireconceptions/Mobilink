package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.PictureUploadTask;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.PROFILE_PIC_URL;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;
import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    public boolean hasUserSelectedPic = false;
    EditText etName;
    boolean canEditDp = false;
    boolean canEditName = false;
    public ImageView ivProfilePic;
    //TODO change image path

    public MyAccountActivity() {
        super();
        layoutResId = R.layout.activity_my_account;
    }

    @Override
    public void init() {
        setupToolbar();
        etName = (EditText) findViewById(R.id.et_name);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        findViewById(R.id.iv_edit_dp).setOnClickListener(this);
        findViewById(R.id.iv_edit_name).setOnClickListener(this);
        loadPicture();
    }

    private void loadPicture() {

        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null) {
            ivProfilePic.setImageBitmap(bitmap);
        } else {
            int width = (int) convertDpToPixels(225);
            int height = width;
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            BitmapDownloadTask bitmapTask = new BitmapDownloadTask(ivProfilePic, progressBar);
            bitmapTask.execute(PROFILE_PIC_URL, valueOf(width), valueOf(height));
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_edit_name) {
            canEditName = !canEditName;
            Log.i("NAME", etName.getText().toString());
            ImageView ivEdit = (ImageView) v;
            etName.setFocusable(canEditName);
            etName.setFocusableInTouchMode(canEditName);
            ivEdit.setImageResource(canEditName ? R.drawable.ic_done_white : R.drawable.ic_edit_grey);
            if(canEditName) {
                etName.clearFocus();
            } else {
                etName.requestFocus();
            }
        } else if(viewId == R.id.iv_edit_dp) {
            canEditDp = !canEditDp;
            ImageView ivEdit = (ImageView) v;
            ivEdit.setImageResource(canEditDp ? R.drawable.ic_done_white : R.drawable.ic_edit_grey);
            if(canEditDp) {
                takePicture();
            } else {
                if(hasUserSelectedPic) {
                    uploadImageToServer(path);
                } else {
                    makeText(getApplicationContext(), "Please select picture", LENGTH_SHORT).show();
                }
            }
        }
    }

    String path = "/storage/emulated/0/images/obs_user_dp.png";

    public void takePicture() {
        Log.i("camera", "startCameraActivity()");
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        Log.i("RequestCode: " + requestCode, "resultCode: " + resultCode);

        hasUserSelectedPic = true;

        if(resultCode == RESULT_OK && null != intent) {

            Uri selectedImage = intent.getData();

            path = selectedImage.getPath();

            Log.i("PIC", path);

        } else {

                switch(resultCode) {
                    case 0:
                        Log.i("SonaSys", "User cancelled");
                        break;
                    case -1:
                        //isCropEnabled = true;
                        Bitmap bitmap = decodeFile(path);
                        ivProfilePic.setImageBitmap(bitmap);
                        profilePicture.setImageBitmap(bitmap);
                        break;
            }
        }
    }

    public void uploadImageToServer(String path) {
        Logger.print("IMG_PATH: " + path);
        Bitmap bitmap = decodeFile(path);
        ivProfilePic.setImageBitmap(bitmap);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        PictureUploadTask uploadTask = new PictureUploadTask(this, path);
        uploadTask.execute();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void updateProfilePicture(String path) {
        if(isNotNullOrEmpty(path)) {
            loadPicture();
            Bitmap dpBitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

            if(dpBitmap != null) {
                profilePicture.setImageBitmap(dpBitmap);
            } else {
                int reqWidth = (int) convertDpToPixels(75);
                int reqHeight = reqWidth;
                BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(profilePicture, null);
                bitmapDownloadTask.execute(PROFILE_PIC_URL, valueOf(reqWidth), valueOf(reqHeight));
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap decodeFile(String path) {
        int orientation;
        try {
            if(path == null) {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.e("ExifInteface .........", "rotation =" + orientation);

            // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
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
        } catch(Exception e) {
            return null;
        }

    }
}