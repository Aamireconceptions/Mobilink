package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;

import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    public boolean hasUserSelectedPic = false;
    EditText etName;
    boolean canEditDp = false;
    boolean canEditName = false;
    ImageView ivProfilePic;
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
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.PROFILE_PIC_URL, options);
        ivProfilePic.setImageBitmap(bitmap);
        ivProfilePic.setBackground(null);
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
                    //TODO upload picture to server
                } else {
                    makeText(getApplicationContext(), "Please select picture", LENGTH_SHORT).show();
                }
            }
        }
    }

    public void takePicture() {
        Log.i("camera", "startCameraActivity()");
        File file = new File(AppConstant.PROFILE_PIC_URL);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, 1);
    }

    public void selectPicture() {
        File file = new File(AppConstant.PROFILE_PIC_URL);
        Uri outputFileUri = Uri.fromFile(file);
        final Intent intent = new Intent();
        intent.setAction(Intent.EXTRA_INITIAL_INTENTS);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, 1);
    }

    boolean isCropEnabled;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.i("RequestCode: " + requestCode, "resultCode: " + resultCode);

        if(resultCode == RESULT_OK && null != data) {

            hasUserSelectedPic = true;
            Uri selectedImage = data.getData();

            String path = selectedImage.getPath();

            Log.i("PIC", path);

            Bundle extras = data.getExtras();

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.PROFILE_PIC_URL, options);
            if(extras != null) {
                //get the cropped bitmap
                bitmap = extras.getParcelable("data");
            }
            ivProfilePic.setBackground(null);
            ivProfilePic.setImageBitmap(bitmap);
            profilePicture.setImageBitmap(bitmap);
            profilePicture.setBackground(null);

        } else {

            if(isCropEnabled) {
                isCropEnabled = false;
            } else {
                switch(resultCode) {
                    case 0:
                        Log.i("SonaSys", "User cancelled");
                        break;
                    case -1:
                        isCropEnabled = true;
                        cropImage();
                        onPhotoTaken();
                        break;
                }
            }
        }
    }

    private void cropImage() {

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        File file = new File(AppConstant.PROFILE_PIC_URL);
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

    protected void onPhotoTaken() {
        Log.i("---", "onPhotoTaken");
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.PROFILE_PIC_URL, options);
        ivProfilePic.setBackground(null);
        ivProfilePic.setImageBitmap(bitmap);
        if(profilePicture != null) {
            profilePicture.setImageBitmap(bitmap);
            profilePicture.setBackground(null);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}