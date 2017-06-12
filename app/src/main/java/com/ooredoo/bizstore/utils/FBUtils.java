package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.VerifyMerchantCodeTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Babar on 22-Jul-16.
 */
public class FBUtils
{
    private Activity activity;

    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    private Tracker tracker;

    public enum PENDING_ACTION
    {
        CHECK_IN,;
    }

    public PENDING_ACTION pending_action;

    public FBUtils(Activity activity)
    {
        this.activity = activity;

        BizStore bizStore = (BizStore) activity.getApplication();
        tracker = bizStore.getDefaultTracker();
    }

    public void init()
    {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.logV("FbUtils FacebookCallback", loginResult.toString());

                Logger.logV("FbUtils AccessToken", AccessToken.getCurrentAccessToken().toString());

                performPendingAction();
            }

            @Override
            public void onCancel() {
                dismissDialog();
                Logger.logV("FbUtils FacebookCallback ", "onCancel");
            }

            @Override
            public void onError(FacebookException e) {

                Toast.makeText(activity, "Something went wrong @ Facebook side. Please retry",
                               Toast.LENGTH_SHORT).show();
                dismissDialog();
                e.printStackTrace();
            }
        });
    }

    public void loginWithReadPermission(String permissions)
    {
        List<String> permissionsList = null;

        if(permissions != null)
        {
            permissionsList = Arrays.asList(permissions);
        }

        LoginManager.getInstance().logInWithReadPermissions(activity, permissionsList);
    }

    public void logInWithPublishPermissions(String permissions)
    {
        LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(permissions));
    }

    private String contentTitle, contentDescription,
                   contentUrl, imageUrl, searchText;

    public boolean lookupPlaceId;

    public void checkIn(String contentTitle, String contentDescription,
                        String contentUrl, String imageUrl, String searchText,
                        String placeId)
    {
        if(callbackManager == null)
        {
            throw new NullPointerException("Did you forget to call init()?");
        }

        this.contentTitle = contentTitle;
        this.contentDescription = contentDescription;
        this.contentUrl = contentUrl;
        this.imageUrl = imageUrl;
        this.searchText = searchText;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null)
        {
            Logger.logV("FbUtils", "Check In AccessToken not null:"+accessToken.getToken()
                    +", expired:"+accessToken.isExpired());

            if(lookupPlaceId)
            {
                Logger.logV("FbUtils", "Check In LookingupForPlaceId");

                getPlaceId(searchText);
            }
            else
            {
                Logger.logV("FbUtils", "Check In NOT LookingupForPlaceId");

                ShareLinkContent.Builder builder = new ShareLinkContent.Builder()
                        .setContentTitle(contentTitle)
                        .setContentDescription(contentDescription)
                        .setImageUrl(Uri.parse(imageUrl))
                        .setContentUrl(Uri.parse(contentUrl));

                Logger.logV("FbUtils", "ImageUrl:"+imageUrl);

                if (placeId != null) {
                    builder.setPlaceId(placeId);
                }

                ShareLinkContent content = builder.build();

                if(canShowShareDialog())
                {
                    dismissDialog();

//                   VerifyMerchantCodeTask.ivClose.performClick();

                    ShareDialog shareDialog =  new ShareDialog(activity);
                    shareDialog.registerCallback(callbackManager,
                            new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {

                                    VerifyMerchantCodeTask.ivClose.performClick();

                                    tracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Facebook Check-in")
                                            .build());
                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onError(FacebookException e) {
                                    e.printStackTrace();
                                }
                            });


                    shareDialog.show(content);

                 //   ShareDialog.show(activity, content);
                }
                else
                {
                    if(hasPermissions(Permissions.PUBLIC_PERMISSION))
                    {
                        ShareApi.share(content, new FacebookCallback<Sharer.Result>()
                        {
                            @Override
                            public void onSuccess(Sharer.Result result)
                            {
                                VerifyMerchantCodeTask.ivClose.performClick();

                                tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Facebook Check-in")
                                        .build());
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onError(FacebookException e)
                            {
                                e.printStackTrace();
                            }
                        });
                    }
                    else
                    {
                        lookupPlaceId = false;
                        pending_action = PENDING_ACTION.CHECK_IN;

                        logInWithPublishPermissions(Permissions.PUBLIC_PERMISSION);
                    }

                }
            }
        }
        else
        {
            pending_action = PENDING_ACTION.CHECK_IN;

            loginWithReadPermission(null);
        }
    }

    public void getPlaceId(final String searchText)
    {
        GraphRequest.newPlacesSearchRequest(AccessToken.getCurrentAccessToken(),
                null, 0, 1, searchText, new GraphRequest.GraphJSONArrayCallback()
                {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse)
                    {
                        try {

                            String placeId = null;
                            if (jsonArray != null && jsonArray.length() > 0) {
                                try {
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    placeId = object.getString("id");

                                    Logger.logV("FbUtils", "PlaceId found: " + placeId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            lookupPlaceId = false;
                            checkIn(contentTitle, contentDescription, contentUrl,
                                    imageUrl, null, placeId);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }

    private boolean canShowShareDialog()
    {
        return ShareDialog.canShow(ShareLinkContent.class);
    }

    private boolean hasPermissions(String permission)
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        return accessToken != null && accessToken.getPermissions().contains(permission);
    }

    private void performPendingAction()
    {
        if(pending_action != null)
        {
            switch (pending_action)
            {
                case CHECK_IN:

                    checkIn(contentTitle, contentDescription, contentUrl,
                            imageUrl, searchText, null);

                    break;
            }

            pending_action = null;
        }
    }

    public void showDialog()
    {
        progressDialog = ProgressDialog.show(activity, null,
                activity.getString(R.string.please_wait));

        progressDialog.show();
    }

    private void dismissDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.logI("FbUtils onActivityResult", "requestCode: "+requestCode+", resultCode: "+resultCode);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static class Permissions
    {
        public static String PUBLIC_PERMISSION = "publish_actions";
    }
}