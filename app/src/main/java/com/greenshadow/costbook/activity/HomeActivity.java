package com.greenshadow.costbook.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.TracksAdapter;
import com.greenshadow.costbook.provider.Constants;
import com.greenshadow.costbook.utils.Log;
import com.greenshadow.costbook.view.EmptyRecyclerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class HomeActivity extends BaseActivity {
    public static final int WHAT_REFRESH_LIST = 101;

    private Toolbar mToolBar;
    private DrawerLayout mDrawer;
    private NavigationView mDrawerNavigation;
    private FloatingActionButton mFab;
    private EmptyRecyclerView mCostList;
    private Cursor mCursor;
    private TracksAdapter mAdapter;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REFRESH_LIST:
                    refreshList();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = findViewById(R.id.tool_bar);
        mDrawer = findViewById(R.id.drawer);
        if (mToolBar != null && mDrawer != null) {
            setupToolBar();
        }

        mDrawerNavigation = findViewById(R.id.drawer_navigation);
        if (mDrawerNavigation != null) {
            setupDrawer();
        }

        mFab = findViewById(R.id.fab_add_cost);
        if (mFab != null) {
            setupFab();
        }

        mCostList = findViewById(R.id.cost_list);
        if (mCostList != null) {
            setupList();
        }
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.w(this, "Action bar is null!");
            return;
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolBar, 0, 0);
        toggle.syncState();
        mDrawer.addDrawerListener(toggle);
    }

    private void setupDrawer() {
        ImageView iconView = mDrawerNavigation.getHeaderView(0).findViewById(R.id.drawer_icon);
        if (iconView != null) {
            Resources res = getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_maruko);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res, bitmap);
            drawable.setCircular(true);
            iconView.setImageDrawable(drawable);
        }

        mDrawerNavigation.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_theme:
                    startActivity(new Intent(this, ThemeChooseActivity.class));
                    break;
                case R.id.menu_about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.menu_settings:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
                default:
                    return false;
            }
            mDrawer.closeDrawers();
            return true;
        });
    }

    private void setupFab() {
        mFab.setOnClickListener(this::onClick);
    }

    private void setupList() {
        mAdapter = new TracksAdapter(this, mCursor, mHandler);
        mCostList.setEmptyView(findViewById(R.id.cost_list_empty));
        mCostList.setAdapter(mAdapter);
        mCostList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_decoration));
        mCostList.addItemDecoration(decoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeMessages(WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(WHAT_REFRESH_LIST);
    }

    private void refreshList() {
        mCursor = getContentResolver().query(Constants.CostList.TRACKS_URI,
                new String[]{ "*", "SUM(price) AS " + Constants.CostList.PRICE_SUM },
                null, null, Constants.CostList.TIME);
        mAdapter.changeCursor(mCursor);
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_cost:
                startActivity(new Intent(AddCostActivity.ACTION_ADD_THREAD));
                break;
            default:
                break;
        }
    }
}
