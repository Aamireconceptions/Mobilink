package com.ooredoo.bizstore.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author  Babar
 * @since 15-Jun-15.
 */
public class BitmapProcessor
{
    private BitmapDownloadTask bitmapDownloadTask;

    public BitmapProcessor(BitmapDownloadTask bitmapDownloadTask)
    {
        this.bitmapDownloadTask = bitmapDownloadTask;
    }

    public Bitmap decodeSampledBitmapFromStream(InputStream inputStream,
                                                int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream, null, options);

        int width = options.outWidth;
        int height = options.outHeight;

        Logger.print("Actual Width: "+width);
        Logger.print("Actual Height: " + height);

        int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;

        inputStream = bitmapDownloadTask.openStream();

        bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Logger.print("inSample:"+sampleSize);
        Logger.print("Modified Width: "+width);
        Logger.print("Modified Height: " + height);

        return bitmap;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int requiredWidth, int requiredHeight)
    {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;

        if(width > requiredWidth || height > requiredHeight)
        {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > requiredWidth
                                    &&
                    (halfHeight / inSampleSize) > requiredHeight)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap makeBitmapRound(Bitmap src)
    {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

       // Canvas canvas = new Canvas(bitmap);

        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);



        RectF rectF = new RectF(0.0f, 0.0f, width, height);

        // rect contains the bounds of the shape
        // radius is the radius in pixels of the rounded corners
        // paint contains the shader that will texture the shape

        Canvas canvas = new Canvas(src);

        canvas.drawRoundRect(rectF, 30, 30, paint);

        return src;
    }
}
