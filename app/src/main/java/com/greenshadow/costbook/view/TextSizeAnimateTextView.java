package com.greenshadow.costbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Keep;

public class TextSizeAnimateTextView extends TextView {
    public TextSizeAnimateTextView(Context context) {
        super(context);
    }

    public TextSizeAnimateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextSizeAnimateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Keep
    public int getTextColor() {
        return getCurrentTextColor();
    }

    @Keep
    public float getCurrentTextSize() {
        return getTextSize() / getContext().getResources().getDisplayMetrics().density;
    }

    @Keep
    public void setCurrentTextSize(float textSize) {
        setTextSize(textSize);
    }
}
