package com.scarcamo.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.Button;

import com.scarcamo.R;


/**
 * Created by Ravinder on 12/27/2017.
 */

public class CButtonBold extends Button {

    public CButtonBold(Context context) {
        super(context);
    }

    public CButtonBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        com.scarcamo.custom.CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontBold);
    }

    public CButtonBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        com.scarcamo.custom.CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontBold);
    }

    /**
     * Created by Ravinder on 12/27/2017.
     */

    public static class CTextViewBlack extends AppCompatTextView {

        public CTextViewBlack(Context context) {
            super(context);
        }

        public CTextViewBlack(Context context, AttributeSet attrs) {
            super(context, attrs);
            CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontBlack);
        }

        public CTextViewBlack(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            CustomFontHelper.setCustomFont(this, context, attrs, R.styleable.CustomFont_cFontBlack);
        }
    }
}
