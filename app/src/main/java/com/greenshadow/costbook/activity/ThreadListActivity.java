package com.greenshadow.costbook.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.ThreadAdapter;
import com.greenshadow.costbook.provider.Constants;
import com.greenshadow.costbook.utils.Log;
import com.greenshadow.costbook.view.EmptyRecyclerView;

import java.math.BigDecimal;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ThreadListActivity extends BaseToolBarBackActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_COST = "cost";

    public static final int WHAT_REFRESH_LIST = 100;

    private EmptyRecyclerView mList;
    private FloatingActionButton mFab;

    private Cursor mCursor;
    private ThreadAdapter mAdapter;
    private String mTitle;

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
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(mTitle)) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_thread_list;
    }

    @Override
    protected void initViews() {
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
            Log.e(this, "Format number error !", e);
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
        mAdapter = new ThreadAdapter(this, null, mHandler);
        mList.setEmptyView(findViewById(R.id.thread_list_empty));
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFab() {
        mFab.setOnClickListener(this::onClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeMessages(WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(WHAT_REFRESH_LIST);
    }

    private void refreshList() {
        Uri getThreads = Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle);
        try {
            mCursor = getContentResolver().query(getThreads, null, null,
                    null, null);
            if (mCursor == null || mCursor.getCount() == 0) {
                Log.w(this, "got an empty cursor");
                finish();
                return;
            }
            mAdapter.changeCursor(mCursor);
        } catch (Exception e) {
            Log.e(this, "setupList : init adapter error", e);
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

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_record:
                Intent i = new Intent(AddCostActivity.ACTION_ADD_RECORD);
                i.putExtra(AddCostActivity.EXTRA_TITLE, mTitle);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode != ThreadAdapter.REQUEST_EDIT_RECORD) {
            return;
        }

        // edit record OK
        mHandler.removeMessages(WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(WHAT_REFRESH_LIST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.onActivityStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
        mAdapter.changeCursor(null);
    }
}
