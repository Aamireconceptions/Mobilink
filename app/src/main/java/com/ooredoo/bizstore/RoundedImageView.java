package com.ooredoo.bizstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    public RoundedImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if(drawable == null) {
            return;
        }

        if(getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if(b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            int radius = getWidth(); //Radius = width

            Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, radius);

            try {
                canvas.drawBitmap(roundBitmap, 0, 0, null);
            } catch(OutOfMemoryError error) {
                error.printStackTrace();
            }
        }

    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if(bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            /*float _w_rate = 0.9f * radius / bitmap.getWidth();
            float _h_rate = 0.9f * radius / bitmap.getHeight();
            float _rate = _w_rate < _h_rate ? _h_rate : _w_rate;*/

           //finalBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * _rate), (int)(bitmap.getHeight() * _rate), false);
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius , radius, false);
        }
        else {
            finalBitmap = bitmap;
        }

        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#D1D1D1"));
        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f, finalBitmap.getHeight() / 2 + 0.7f, finalBitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

}

