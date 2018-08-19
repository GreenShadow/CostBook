package com.greenshadow.costbook.avtivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.TracksAdapter;
import com.greenshadow.costbook.provider.Constants;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private Toolbar mToolBar;
    private DrawerLayout mDrawer;
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
                new String[]{"*", "SUM(price) AS " + Constants.CostList.PRICE_SUM},
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
