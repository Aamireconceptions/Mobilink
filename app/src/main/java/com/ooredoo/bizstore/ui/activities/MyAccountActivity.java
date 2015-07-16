package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.StringUtils;

import java.io.File;
import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.NAME;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    public boolean hasUserSelectedPic = false;
    EditText etName, etNumber;
    boolean canEditDp = false;
    boolean canEditName = false;
    ImageView ivProfilePic;
    //TODO change image path

    int reqWidth, reqHeight;

    private BitmapProcessor bitmapProcessor;

    public MyAccountActivity() {
        super();
        layoutResId = R.layout.activity_my_account;
    }

    @Override
    public void init() {
        setupToolbar();
        etName = (EditText) findViewById(R.id.et_name);
        etNumber = (EditText) findViewById(R.id.et_number);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        findViewById(R.id.iv_edit_dp).setOnClickListener(this);
        findViewById(R.id.iv_edit_name).setOnClickListener(this);

        String name = getStringVal(this, NAME);
        if(StringUtils.isNotNullOrEmpty(name)) {
            etName.setText(name);
        }

        etNumber.setText(BizStore.username);

        bitmapProcessor = new BitmapProcessor(null);

        loadPicture();
    }

    private void loadPicture() {
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.PROFILE_PIC_URL, options);
        ivProfilePic.setImageBitmap(bitmap);
        ivProfilePic.setBackground(null);*/

        reqWidth = getResources().getDisplayMetrics().widthPixels;

        reqHeight = (int) (Converter.convertDpToPixels(getResources().getDimension(R.dimen._225sdp))
                / getResources().getDisplayMetrics().density);


        try
        {
            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromFile(AppConstant.PROFILE_PIC_URL,
                                                                        reqWidth, reqHeight);

            ivProfilePic.setImageBitmap(bitmap);

        } catch (IOException e)
        {
            e.printStackTrace();
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
            if(canEditName) {
                etName.clearFocus();
            } else {
                String name = etName.getText().toString();
                if(StringUtils.isNotNullOrEmpty(name)) {
                    isValidName = true;
                    updateVal(this, NAME, name);
                } else {

                }
                etName.requestFocus();
            }

            if(canEditName || isValidName) {
                etName.setFocusable(canEditName);
                etName.setFocusableInTouchMode(canEditName);
                ivEdit.setImageResource(canEditName ? R.drawable.ic_done_white : R.drawable.ic_edit_grey);
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
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setType("image/*");
        intent.setAction(Intent.EXTRA_INITIAL_INTENTS);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, 2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && null != data) {

            hasUserSelectedPic = true;
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String path = cursor.getString(columnIndex);

            cursor.close();

            Logger.print("PIC" + path);

            try
            {
                reqWidth = (int) (Converter.convertDpToPixels(getResources().getDimension(R.dimen._75sdp))
                        / getResources().getDisplayMetrics().density);

                reqHeight = (int) (Converter.convertDpToPixels(getResources().getDimension(R.dimen._75sdp))
                        / getResources().getDisplayMetrics().density);

                Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromFile(path, reqWidth, reqHeight);
                ivProfilePic.setImageBitmap(bitmap);

                User.dp = bitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            /*Bitmap bitmap = BitmapFactory.decodeFile(path);
            ivProfilePic.setBackground(null);
            ivProfilePic.setImageBitmap(bitmap);
            profilePicture.setImageBitmap(bitmap);
            profilePicture.setBackground(null);

            User.dp = bitmap;*/
        } else {

            Logger.print("SonaSys: resultCode: " + resultCode);
            switch(resultCode) {
                case 0:
                    Logger.print("SonaSys: User cancelled");
                    break;
                case -1:
                    onPhotoTaken(AppConstant.PROFILE_PIC_URL);
                    break;
            }
        }
    }

    protected void onPhotoTaken(String path) {
        Logger.print("onPhotoTaken");
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.PROFILE_PIC_URL, options);
        ivProfilePic.setBackground(null);
        ivProfilePic.setImageBitmap(bitmap);
        if(profilePicture != null) {
            profilePicture.setImageBitmap(bitmap);
            profilePicture.setBackground(null);
        }

        User.dp = bitmap;*/

        try
        {
            reqWidth = (int) (Converter.convertDpToPixels(getResources().getDimension(R.dimen._75sdp))
                    / getResources().getDisplayMetrics().density);

            reqHeight = (int) (Converter.convertDpToPixels(getResources().getDimension(R.dimen._75sdp))
                    / getResources().getDisplayMetrics().density);

            Bitmap bitmap = bitmapProcessor.decodeSampledBitmapFromFile(path, reqWidth, reqHeight);
            profilePicture.setImageBitmap(bitmap);
            ivProfilePic.setImageBitmap(bitmap);

            User.dp = bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}