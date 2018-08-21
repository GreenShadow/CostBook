package com.greenshadow.costbook.avtivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.TracksAdapter;
import com.greenshadow.costbook.provider.Constants;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private Toolbar mToolBar;
    private DrawerLayout mDrawer;
    private NavigationView mDrawerNavigation;
    private FloatingActionButton mFab;
    private ListView mCostList;
    private Cursor mCursor;
    private TracksAdapter mAdapter;

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
            Log.w(TAG, "Action bar is null!");
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
            Log.d(TAG, "'" + menuItem.getTitle() + "' selected");
            return false;
        });
    }

    private void setupFab() {
        mFab.setOnClickListener(this::onClick);
    }

    private void setupList() {
        mCostList.setEmptyView(findViewById(R.id.cost_list_empty));

        mAdapter = new TracksAdapter(this, mCursor);
        mCostList.setAdapter(mAdapter);
        mCostList.setOnItemClickListener((parent, view, position, id) -> clickedAt(position));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
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
                startActivity(new Intent(this, AddCostActivity.class));
                break;
            default:
                break;
        }
    }

    private void clickedAt(int position) {
        Intent i = new Intent(this, ThreadListActivity.class);
        i.putExtra(ThreadListActivity.EXTRA_TITLE, mAdapter.getTitleAt(position));
        i.putExtra(ThreadListActivity.EXTRA_COST, mAdapter.getCostAt(position));
        startActivity(i);
    }
}
