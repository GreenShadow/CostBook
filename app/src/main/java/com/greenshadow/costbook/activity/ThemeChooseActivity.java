package com.greenshadow.costbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.greenshadow.costbook.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class ThemeChooseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_choose);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.theme_light).setOnClickListener(this::onClick);
        findViewById(R.id.theme_dark).setOnClickListener(this::onClick);
        findViewById(R.id.theme_dark_amoled).setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.theme_light:
                getApp().setTheme(R.style.AppTheme);
                break;
            case R.id.theme_dark:
                getApp().setTheme(R.style.AppTheme_Dark);
                break;
            case R.id.theme_dark_amoled:
                getApp().setTheme(R.style.AppTheme_Dark_Amoled);
                break;
            default:
                return;
        }
        restart();
    }

    private void restart() {
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
