package com.greenshadow.costbook;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.StyleRes;

public class CostBookApp extends Application {
    public static final String PREF_KEY_THEME_NAME = "theme_name";
    public static final String PREF_KEY_WELCOME_PAGE = "welcome_page";

    public static final String DEFAULT_THEME_NAME = "";
    public static final boolean DEFAULT_WELCOME_ENABLE = false;

    private static final String THEME_LIGHT = "theme_light";
    private static final String THEME_DARK = "theme_dark";
    private static final String THEME_DARK_AMOLED = "theme_dark_amoled";

    private SharedPreferences mSp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public SharedPreferences getSharedPreferences() {
        return mSp;
    }

    @Override
    public void setTheme(@StyleRes int resId) {
        super.setTheme(resId);

        String themeName;

        switch (resId) {
            case R.style.AppTheme:
                themeName = THEME_LIGHT;
                break;
            case R.style.AppTheme_Dark:
                themeName = THEME_DARK;
                break;
            case R.style.AppTheme_Dark_Amoled:
                themeName = THEME_DARK_AMOLED;
                break;
            default:
                return;
        }

        getSharedPreferences().edit().putString(PREF_KEY_THEME_NAME, themeName).apply();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();

        String themeName = getSharedPreferences().getString(PREF_KEY_THEME_NAME, "");
        if (TextUtils.isEmpty(themeName)) {
            return theme;
        }

        switch (themeName) {
            case THEME_LIGHT:
                theme.applyStyle(R.style.AppTheme, true);
                break;
            case THEME_DARK:
                theme.applyStyle(R.style.AppTheme_Dark, true);
                break;
            case THEME_DARK_AMOLED:
                theme.applyStyle(R.style.AppTheme_Dark_Amoled, true);
                break;
        }
        return theme;
    }

    public int getSavedTheme() {
        String themeName = getSharedPreferences().getString(PREF_KEY_THEME_NAME, "");
        if (TextUtils.isEmpty(themeName)) {
            return R.style.AppTheme;
        }

        switch (themeName) {
            case THEME_LIGHT:
                return R.style.AppTheme;
            case THEME_DARK:
                return R.style.AppTheme_Dark;
            case THEME_DARK_AMOLED:
                return R.style.AppTheme_Dark_Amoled;
        }
        return R.style.AppTheme;
    }
}
