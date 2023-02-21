package com.scarcamo.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Ravinder on 12/27/2017.
 */

public class CEditTextBold extends EditText {

    public CEditTextBold(Context context) {
        super(context);
        setFont();

    }

    public CEditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CEditTextBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }



    /**
     * This method is used to set the given font to the TextView.
     */
    private void setFont() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SFUIDISPLAY-BOLD.OTF");
        setTypeface(tf);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
