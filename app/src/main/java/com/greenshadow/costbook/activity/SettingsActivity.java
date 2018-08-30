package com.greenshadow.costbook.activity;

import android.content.SharedPreferences;
import android.view.MenuItem;

import com.greenshadow.costbook.CostBookApp;
import com.greenshadow.costbook.R;

import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends BaseToolBarBackActivity {
    private SwitchCompat mWelcome;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViews() {
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
