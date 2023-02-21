package com.scarcamo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.scarcamo.R;


/**
 * Created by Ravinder on 12/27/2017.
 */

public class CTextViewHeavy extends TextView {

    public CTextViewHeavy(Context context) {
        super(context);
    }

    public CTextViewHeavy(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontHeavy);
    }

    public CTextViewHeavy(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontHeavy);
    }
}
