package com.ooredoo.bizstore.views;

/*
Copyright 2014 Stephan Tittel and Yahoo Inc.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;

import java.math.BigDecimal;

/**
 * Created by Babar on 16-Jun-15.
 */
public class RangeSeekBar<T extends Number> extends ImageView
{
    public static final Integer DEFAULT_MINIMUM = 0;
    public static final Integer DEFAULT_MAXIMUM = 100;

    // Localized constants from MotionEvent for compatibility
    // with API < 8 "Froyo".
    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
                            ACTION_POINTER_INDEX_SHIFT = 8;
    /**
     * An invalid pointer id.
     */
    public static final int INVALID_POINTER_ID = 255;
    public static final int HEIGHT_IN_DP = 30;
    public static final int TEXT_LATERAL_PADDING_IN_DP = 3;
    private static final int INITIAL_PADDING_IN_DP = 8;
    private final int LINE_HEIGHT_IN_DP = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mScaledTouchSlop;
    private int mTextOffset;
    private int mTextSize;
    private int mDistanceToTop;
    private static final int DEFAULT_TEXT_SIZE_IN_DP = 14;
    private static final int DEFAULT_TEXT_DISTANCE_TO_BUTTON_IN_DP = 8;
    private static final int DEFAULT_TEXT_DISTANCE_TO_TOP_IN_DP = 8;

    private RectF mRect;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_about);
    private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_about);
    private final Bitmap thumbDisabledImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_about);

    private final float thumbWidth = thumbImage.getWidth();
    private final float thumbHalfWidth = 0.5f * thumbWidth;
    private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();
    private float INITIAL_PADDING;
    private float padding;
    private float mDownMotionX;

    private T absoluteMinValue, absoluteMaxValue;

    private NumberType numberType;

    private double absoluteMinValuePrim, absoluteMaxValuePrim;
    private double normalizedMinValue = 0d;
    private double normalizedMaxValue = 1d;

    private Thumb pressedThumb = null;

    private boolean notifyWhileDragging = false;
    private boolean mIsDragging;
    private boolean mSingleThumb;

    private OnRangeSeekBarChangeListener<T> listener;

    public  final int DEFAULT_COLOR = getResources().getColor(R.color.red);

    public RangeSeekBar(Context context)
    {
        super(context);
    }

    private void init(Context context, AttributeSet attrs)
    {
        if(attrs == null)
        {
            setRangeToDefaultValues();
        }
        else
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RangeSeekBar, 0, 0);
        }
    }

    // only used to set default values when initialised from XML without any values specified
    @SuppressWarnings("unchecked")
    private void setRangeToDefaultValues()
    {
        this.absoluteMinValue = (T) DEFAULT_MINIMUM;
        this.absoluteMaxValue = (T) DEFAULT_MAXIMUM;

        setValuePrimAndNumberType();
    }

    private void setValuePrimAndNumberType()
    {
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMinValue.doubleValue();

        numberType = NumberType.fromNumber(absoluteMinValue);
    }






    /**
     * Utility enumeration used to convert between Numbers and doubles.
     *
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    private static enum NumberType
    {
        LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

        public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException
        {
            if(value instanceof Long)
            {
                return LONG;
            }

            if(value instanceof Double)
            {
                return DOUBLE;
            }

            if(value instanceof Integer)
            {
                return INTEGER;
            }

            if(value instanceof Float)
            {
                return FLOAT;
            }

            if(value instanceof Short)
            {
                return SHORT;
            }

            if(value instanceof Byte)
            {
                return BYTE;
            }

            if(value instanceof BigDecimal)
            {
                return BIG_DECIMAL;
            }

            throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
        }

        public Number toNumber(double value)
        {
            switch (this)
            {
                case LONG:
                    return Long.valueOf((long) value);
                case DOUBLE:
                    return value;
                case INTEGER:
                    return Integer.valueOf((int) value);
                case FLOAT:
                    return Float.valueOf((float)value);
                case SHORT:
                    return Short.valueOf((short) value);
                case BYTE:
                    return Byte.valueOf((byte) value);
                case BIG_DECIMAL:
                    return BigDecimal.valueOf(value);
            }

            throw new InstantiationError("Can't convert " + this + " to a Number object");
        }

    }

    /**
     * Thumb constants (min and max).
     */
    private static enum Thumb
    {
        MIN, MAX;
    }

    /**
     * Callback listener interface to notify about changed range values.
     *
     * @param <T> The Number type the RangeSeekBar has been declared with.
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    public interface OnRangeSeekBarChangeListener<T>
    {
        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, T minValue, T maxValue);
    }


}
