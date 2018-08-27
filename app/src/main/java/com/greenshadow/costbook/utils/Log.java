package com.greenshadow.costbook.utils;

public final class Log {
    private static final String TAG = "CostBook";

    public static int i(Object prefix, String msg) {
        return android.util.Log.i(TAG, getSubTag(prefix) + " - " + msg);
    }

    public static int d(Object prefix, String msg) {
        return android.util.Log.d(TAG, getSubTag(prefix) + " - " + msg);
    }

    public static int v(Object prefix, String msg) {
        return android.util.Log.v(TAG, getSubTag(prefix) + " - " + msg);
    }

    public static int w(Object prefix, String msg) {
        return android.util.Log.w(TAG, getSubTag(prefix) + " - " + msg);
    }

    public static int w(Object prefix, String msg, Throwable tr) {
        return android.util.Log.w(TAG, getSubTag(prefix) + " - " + msg, tr);
    }

    public static int e(Object prefix, String msg) {
        return android.util.Log.e(TAG, getSubTag(prefix) + " - " + msg);
    }

    public static int e(Object prefix, String msg, Throwable tr) {
        return android.util.Log.e(TAG, getSubTag(prefix) + " - " + msg, tr);
    }

    public static int wtf(Object prefix, String msg) {
        return android.util.Log.wtf(TAG, getSubTag(prefix) + " - " + msg, new Throwable());
    }

    private static String getSubTag(Object prefix) {
        if (prefix == null) {
            return "<null>";
        }

        if (prefix instanceof CharSequence) {
            return prefix.toString();
        }

        return prefix.getClass().getSimpleName();
    }
}
