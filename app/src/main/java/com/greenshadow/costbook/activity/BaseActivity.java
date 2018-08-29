package com.greenshadow.costbook.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.greenshadow.costbook.CostBookApp;
import com.greenshadow.costbook.utils.Log;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getApp().getSavedTheme());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshUi();
    }

    protected void refreshUi() {
        Resources.Theme theme = getApp().getTheme();
        View root = findViewById(android.R.id.content);
        ergodicViews(root);
    }

    private void ergodicViews(View view) {
        if (view instanceof ViewGroup) {
            Log.d(this, "ViewGroup : " + view.toString());
            ViewGroup root = (ViewGroup) view;
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                ergodicViews(child);
            }
        } else {
            Log.d(this, "    ergodicViews : " + view.toString());
        }
    }

    protected CostBookApp getApp() {
        return (CostBookApp) getApplicationContext();
    }

    protected SharedPreferences getSharedPreferences() {
        return getApp().getSharedPreferences();
    }

    protected void toast(@StringRes int stringId) {
        toast(getString(stringId));
    }

    protected void toast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            mToast.show();
        } else {
            mToast.setText(msg);
        }
    }
}
