package com.greenshadow.costbook.utils;

import android.content.Context;

public class DimensionUtils {
    public static float sp2px(Context context, float sp) {
        float density = context.getResources().getDisplayMetrics().density;
        return sp * density;
    }

    public static float px2sp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }
}
