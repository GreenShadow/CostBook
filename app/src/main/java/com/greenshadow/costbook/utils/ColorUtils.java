package com.greenshadow.costbook.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

public class ColorUtils {
    public static int getColor(Context context, @ColorRes int id) {
        return context.getResources().getColor(id);
    }

    public static int getThemeColor(Context context, @AttrRes int id) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(id, tv, true);
        return tv.data;
    }

    public static ColorStateList getTintList(@ColorInt int color) {
        return ColorStateList.valueOf(color);
    }

    public static int getColorFromList(@Nullable ColorStateList list) {
        if (list == null) {
            return Color.TRANSPARENT;
        }

        return list.getDefaultColor();
    }
}
