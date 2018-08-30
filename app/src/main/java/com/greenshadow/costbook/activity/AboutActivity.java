package com.greenshadow.costbook.activity;

import android.view.MenuItem;
import android.widget.TextView;

import com.greenshadow.costbook.BuildConfig;
import com.greenshadow.costbook.R;

public class AboutActivity extends BaseToolBarBackActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void initViews() {
        TextView version = findViewById(R.id.about_version);
        version.setText(getString(R.string.version_prefix));
        version.append(BuildConfig.VERSION_NAME);
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
