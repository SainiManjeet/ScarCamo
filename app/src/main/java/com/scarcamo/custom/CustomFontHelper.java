package com.scarcamo.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.scarcamo.R;


/**
 * Created by Ravinder on 12/27/2017.
 */

public class CustomFontHelper {
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs, int customFont) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
        String font = a.getString(customFont);
        setCustomFont(textview, font, context);
        a.recycle();
    }

    public static void setCustomFont(TextView textview, String font, Context context) {
        if (font == null) {
            return;
        }

        Typeface tf = com.scarcamo.custom.FontCache.get(font, context);

        if (tf != null) {
            textview.setTypeface(tf);
        }
    }
}
