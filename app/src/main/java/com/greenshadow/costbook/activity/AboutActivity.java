package com.greenshadow.costbook.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.greenshadow.costbook.BuildConfig;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.utils.ColorUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = findViewById(R.id.about_version);
        version.setText(getString(R.string.version_prefix));
        version.append(BuildConfig.VERSION_NAME);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
