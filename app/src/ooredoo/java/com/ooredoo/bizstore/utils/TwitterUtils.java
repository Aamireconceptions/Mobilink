package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.net.Uri;

import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Image;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Babar on 18-Nov-16.
 */
public class TwitterUtils
{
    public static void tweet(Context context, String tweet, Image image){
        String imageKey =  getImageURL(image)+".0";

        Logger.print("Twitter image URL: "+imageKey);
        //String imageKey = "be7059231a08c737ab0c228f1be24eba.0";
        String tweetImagePath = context.getExternalCacheDir() + "/ooredoo_thumbnails/"+imageKey;

        File imageFile = new File(tweetImagePath);

        Logger.print("Fabric: file: " + imageFile.toString());
        Uri uri = null;
        if(imageFile.exists()) {
            uri = Uri.fromFile(imageFile);

            Logger.print("Fabric Uri: "+uri.toString());
        }

        URL url = null;
        try {
            url = new URL("https://play.google.com/store/apps/details?id=pk.com.mobilink.bizstore");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                            .text(tweet)
                                            .url(url);

        if(uri != null){
            builder.image(uri);
        }

        builder.show();
    }

    private static String getImageURL(Image image)
    {
        String imageUrl = BaseAsyncTask.IMAGE_BASE_URL;
        if(image != null)
        {
            imageUrl += (image.detailBannerUrl != null && !image.detailBannerUrl.isEmpty()) ? image.detailBannerUrl
                    : (image.bannerUrl != null && !image.bannerUrl.isEmpty()) ? image.bannerUrl
                    : (image.gridBannerUrl != null && !image.gridBannerUrl.isEmpty()) ? image.gridBannerUrl
                    : (image.promotionalUrl != null && !image.promotionalUrl.isEmpty()) ? image.promotionalUrl
                    : image.logoUrl;

           return CryptoUtils.encryptToMD5(imageUrl);
        }

        return null;
    }
}