package com.greenshadow.costbook.activity;

import android.os.Bundle;

import com.greenshadow.costbook.R;
import com.greenshadow.costbook.utils.Log;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseToolBarBackActivity extends BaseActivity {
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        mToolBar = findViewById(R.id.tool_bar);
        if (mToolBar != null) {
            setupToolBar();
        } else {
            Log.e(this, "No R.id.tool_bar at the layout file! Ignore setup ToolBar!");
        }

        initViews();
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void initViews();

    protected final void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
