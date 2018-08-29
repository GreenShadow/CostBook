package com.greenshadow.costbook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.greenshadow.costbook.CostBookApp;
import com.greenshadow.costbook.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends BaseActivity {
    private SwitchCompat mWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mWelcome = findViewById(R.id.welcome_page_switch);
        if (mWelcome != null) {
            setupSwitch();
        }
    }

    private void setupSwitch() {
        SharedPreferences sp = getSharedPreferences();
        boolean enableWelcome = sp.getBoolean(CostBookApp.PREF_KEY_WELCOME_PAGE,
                CostBookApp.DEFAULT_WELCOME_ENABLE);
        mWelcome.setChecked(enableWelcome);

        mWelcome.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean(CostBookApp.PREF_KEY_WELCOME_PAGE, isChecked).apply());
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
