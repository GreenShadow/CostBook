package com.greenshadow.costbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class CostRateView extends LinearLayout {
    private int mWidth = -1;
    private int mHeight = -1;

    public CostRateView(Context context) {
        this(context, null);
    }

    public CostRateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CostRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CostRateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(HORIZONTAL);

        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
            }
        });
    }

    public void setData() {
    }
}
