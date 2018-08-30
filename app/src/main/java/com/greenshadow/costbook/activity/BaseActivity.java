package com.greenshadow.costbook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.greenshadow.costbook.CostBookApp;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getApp().getSavedTheme());
        super.onCreate(savedInstanceState);
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
