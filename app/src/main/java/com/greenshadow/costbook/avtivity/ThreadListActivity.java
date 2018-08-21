package com.greenshadow.costbook.avtivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.ThreadAdapter;
import com.greenshadow.costbook.provider.Constants;

import java.math.BigDecimal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ThreadListActivity extends AppCompatActivity {
    private static final String TAG = ThreadListActivity.class.getSimpleName();

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_COST = "cost";

    private Toolbar mToolbar;
    private ListView mList;
    private FloatingActionButton mFab;

    private Cursor mCursor;
    private ThreadAdapter mAdapter;
    private String mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(mTitle)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_thread_list);

        mToolbar = findViewById(R.id.tool_bar);
        if (mToolbar != null) {
            setupToolBar();
        }

        setUpCost();

        mList = findViewById(R.id.thread_list);
        if (mList != null) {
            setupList();
        }

        mFab = findViewById(R.id.fab_add_record);
        if (mFab != null) {
            setupFab();
        }
    }

    private void setupToolBar() {
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.w(TAG, "Action bar is null!");
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpCost() {
        String costString = getIntent().getStringExtra(EXTRA_COST);
        if (TextUtils.isEmpty(costString)) {
            findViewById(R.id.cost_panel).setVisibility(View.GONE);
            return;
        }

        BigDecimal costNum = null;
        try {
            costNum = new BigDecimal(costString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Format number error !", e);
        }

        if (costNum == null) {
            return;
        }

        TextView status = findViewById(R.id.status);
        TextView cost = findViewById(R.id.cost);

        if (costNum.compareTo(BigDecimal.ZERO) > 0) {
            status.setText(R.string.cost);
            status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            cost.setText(costNum.toPlainString());
            cost.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            status.setText(R.string.profit);
            status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            cost.setText(costNum.abs().toPlainString());
            cost.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void setupList() {
        mList.setEmptyView(findViewById(R.id.thread_list_empty));

        mAdapter = new ThreadAdapter(this, null);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener((parent, view, position, id) -> onItemClick(view));
    }

    private void setupFab() {
        mFab.setOnClickListener(this::onClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        Uri getThreads = Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle);
        try {
            mCursor = getContentResolver().query(getThreads, null, null,
                    null, null);
            mAdapter.changeCursor(mCursor);
        } catch (Exception e) {
            Log.e(TAG, "setupList : init adapter error", e);
        }
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

    private void onItemClick(View v) {
        TextView noteView = v.findViewById(R.id.record_note);
        if (noteView == null) {
            return;
        }
        String note = noteView.getText().toString();
        if (TextUtils.isEmpty(note)) {
            return;
        }

        if (noteView.getVisibility() == View.VISIBLE) {
            noteView.setVisibility(View.GONE);
        } else {
            noteView.setVisibility(View.VISIBLE);
        }
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_record:
                Intent i = new Intent(this, AddCostActivity.class);
                i.putExtra(AddCostActivity.EXTRA_TITLE, mTitle);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }
}
