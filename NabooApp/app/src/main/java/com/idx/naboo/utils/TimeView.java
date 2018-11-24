package com.idx.naboo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextClock;


public class TimeView extends TextClock {

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);



        setFormat12Hour("HH:mm");
        setFormat24Hour("HH:mm");




    }

}