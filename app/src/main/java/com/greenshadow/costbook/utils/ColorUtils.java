package com.greenshadow.costbook.utils;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;

public class ColorUtils {
    public static int getColor(Context context, @ColorRes int id) {
        return context.getResources().getColor(id);
    }

    public static int getThemeColor(Context context, @AttrRes int id) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(id, tv, true);
        return tv.data;
    }
}
