package com.greenshadow.costbook.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import com.greenshadow.costbook.BuildConfig;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.adapter.IconSpinnerAdapter;
import com.greenshadow.costbook.provider.Constants;
import com.greenshadow.costbook.utils.ColorUtils;
import com.greenshadow.costbook.utils.Log;
import com.greenshadow.costbook.view.IconInputTextLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class AddCostActivity extends BaseActivity {
    public static final String ACTION_ADD_THREAD = "com.greenshadow.costbook.activity.AddCostActivity.ACTION_ADD_THREAD";
    public static final String ACTION_ADD_RECORD = "com.greenshadow.costbook.activity.AddCostActivity.ACTION_ADD_RECORD";
    public static final String ACTION_EDIT_RECORD = "com.greenshadow.costbook.activity.AddCostActivity.ACTION_EDIT_RECORD";

    public static final String EXTRA_RECORD_ID = "extra_record_id";
    public static final String EXTRA_TITLE = "extra_title";

    private enum Mode {
        MODE_NORMAL,
        MODE_ADD_RECORD,
        MODE_EDIT_RECORD,
    }

    private static final int MENU_SAVE = 100;

    private Toolbar mToolBar;
    private IconInputTextLayout mTitleView;
    private IconInputTextLayout mTotalView;
    private IconInputTextLayout mPriceView;
    private IconInputTextLayout mBuyTimeView;
    private Spinner mPriceTypeView;
    private Spinner mCurrencyTypeView;
    private IconInputTextLayout mNoteView;

    private String mTitle, mBuyTime, mNote;
    private BigDecimal mTotal, mPrice;

    private DatePickerDialog mDialog;

    private int mRecordId;
    private Mode mMode = Mode.MODE_NORMAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cost);

        mToolBar = findViewById(R.id.tool_bar);
        if (mToolBar != null) {
            setupToolBar();
        }

        mTitleView = findViewById(R.id.til_cost_title);
        mTotalView = findViewById(R.id.til_cost_total);
        mPriceView = findViewById(R.id.til_cost_price);
        mBuyTimeView = findViewById(R.id.til_cost_buy_time);
        mPriceTypeView = findViewById(R.id.spinner_price_type);
        mCurrencyTypeView = findViewById(R.id.spinner_price_currency_type);
        mNoteView = findViewById(R.id.cost_note);
        setupInputs();
        setupSpinners();

        setResult(RESULT_CANCELED);
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.w(this, "Action bar is null!");
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupInputs() {
        Intent i = getIntent();
        String action = i.getAction();
        if (action == null) {
            Log.e(this, "There should has an action when try to start AddCostActivity");
            if (BuildConfig.LOG_DEBUG) {
                toast("no action");
            }
            finish();
            return;
        }
        switch (action) {
            case ACTION_ADD_RECORD:
                String title = i.getStringExtra(EXTRA_TITLE);
                if (!TextUtils.isEmpty(title)) {
                    Log.d(this, "setupInputs : enter into add record mode.");
                    mTitle = title;
                    enterRecordMode();
                } else {
                    Log.w(this, "setupInputs : got add record action but no valid extras! enter normal mode");
                    enterNormalMode();
                }
                break;
            case ACTION_ADD_THREAD:
                Log.d(this, "setupInputs : enter into add tracks mode.");
                enterNormalMode();
                break;
            case ACTION_EDIT_RECORD:
                int id = i.getIntExtra(EXTRA_RECORD_ID, -1);
                if (id < 0) {
                    Log.e(this, "setupInputs : got edit action but invalid id!");
                    if (BuildConfig.LOG_DEBUG) {
                        toast("Invalid ID");
                    }
                    finish();
                    return;
                }
                mRecordId = id;
                enterEditMode();
                break;
            default:
                Log.e(this, "unknown action " + action);
                if (BuildConfig.LOG_DEBUG) {
                    toast("Unknown action " + action);
                }
                finish();
                break;
        }

        mBuyTimeView.getEditText().setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupSpinners() {
        List<IconSpinnerAdapter.IconItem> prices = new ArrayList<>();
        prices.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_unit_price, R.string.unit_price));
        prices.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_total_price, R.string.total_price));
        mPriceTypeView.setAdapter(new IconSpinnerAdapter(this, prices));

        List<IconSpinnerAdapter.IconItem> currencies = new ArrayList<>();
        currencies.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_cny, R.string.currency_cny));
        currencies.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_btc, R.string.currency_btc));
        currencies.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_eth, R.string.currency_eth));
        currencies.add(new IconSpinnerAdapter.IconItem(R.drawable.ic_usdt, R.string.currency_usdt));
        mCurrencyTypeView.setAdapter(new IconSpinnerAdapter(this, currencies));
    }

    private void enterRecordMode() {
        Uri getThreads = Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle);
        try (Cursor c = getContentResolver().query(getThreads, null, null,
                null, null)) {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                Log.w(this, "Invalid cursor " + c);
                enterNormalMode();
                return;
            }

            mTitleView.setText(mTitle);
            mTitleView.setEnabled(false);

            int currencySelection = c.getInt(c.getColumnIndex(Constants.CostList.CURRENCY_TYPE));
            if (currencySelection >= 0 && currencySelection <= 3) {
                mCurrencyTypeView.setSelection(currencySelection);
                mCurrencyTypeView.setEnabled(false);
            } else {
                Log.w(this, "Invalid selection " + currencySelection);
                enterNormalMode();
                return;
            }
        } catch (Exception e) {
            Log.e(this, "query error!", e);
            enterNormalMode();
        }
        mMode = Mode.MODE_ADD_RECORD;
    }

    private void enterNormalMode() {
        mTitleView.setEnabled(true);
        mTitleView.getEditText().getText().clear();
        mCurrencyTypeView.setEnabled(true);
        mCurrencyTypeView.setSelection(0);
        mMode = Mode.MODE_NORMAL;
    }

    private void enterEditMode() {
        Uri recordUri = Uri.withAppendedPath(Constants.CostList.RECORD_URI, String.valueOf(mRecordId));
        try (Cursor c = getContentResolver().query(recordUri, null, null, null, null)) {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                Log.e(this, "Invalid cursor " + c);
                if (BuildConfig.LOG_DEBUG) {
                    toast("Error when query db");
                }
                finish();
                return;
            }

            mTitle = c.getString(c.getColumnIndex(Constants.CostList.TITLE));
            mTitleView.setText(mTitle);
            mTitleView.setEnabled(false);

            int currencySelection = c.getInt(c.getColumnIndex(Constants.CostList.CURRENCY_TYPE));
            if (currencySelection >= 0 && currencySelection <= 3) {
                mCurrencyTypeView.setSelection(currencySelection);
                mCurrencyTypeView.setEnabled(false);
            } else {
                Log.w(this, "Invalid selection " + currencySelection);
                if (BuildConfig.LOG_DEBUG) {
                    toast("Invalid currency selection");
                }
                finish();
                return;
            }

            String total = c.getString(c.getColumnIndex(Constants.CostList.TOTAL));
            mTotal = new BigDecimal(total);
            mTotalView.setText(total);

            String price = c.getString(c.getColumnIndex(Constants.CostList.PRICE));
            mPrice = new BigDecimal(price);
            mPriceView.setText(price);

            int priceType = c.getInt(c.getColumnIndex(Constants.CostList.PRICE_TYPE));
            mPriceTypeView.setSelection(priceType);

            mBuyTime = c.getString(c.getColumnIndex(Constants.CostList.BUY_TIME));
            mBuyTimeView.setText(mBuyTime);

            mNote = c.getString(c.getColumnIndex(Constants.CostList.NOTE));
            mNoteView.setText(mNote);
        } catch (Exception e) {
            Log.e(this, "query error! Uri = " + recordUri, e);
            if (BuildConfig.LOG_DEBUG) {
                toast("Query error");
            }
            finish();
            return;
        }
        mMode = Mode.MODE_EDIT_RECORD;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (mDialog == null) {
            mDialog = new DatePickerDialog(AddCostActivity.this,
                    R.style.AppTheme_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = year + "/" + month + "/" + dayOfMonth;
                        mBuyTimeView.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        } else if (mDialog.isShowing()) {
            return;
        }
        mDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        mDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SAVE, 0, R.string.save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case MENU_SAVE:
                clearErrors();
                if (checkInputs() && trySaveRecord()) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(this, newConfig.toString());
    }

    private void clearErrors() {
        mTitleView.setError(null);
        mTotalView.setError(null);
        mPriceView.setError(null);
    }

    private boolean checkInputs() {
        mTitle = mTitleView.getText().trim();
        String mTotalStr = mTotalView.getText();
        String mPriceStr = mPriceView.getText();
        mBuyTime = mBuyTimeView.getText();
        mNote = mNoteView.getText().trim();

        boolean result = true;

        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setError(getString(R.string.cost_total_error));
            result = false;
        }

        if (TextUtils.isEmpty(mTotalStr)) {
            mTotalView.setError(getString(R.string.cost_total_error));
            result = false;
        } else {
            try {
                mTotal = new BigDecimal(mTotalStr);
            } catch (NumberFormatException e) {
                mTotalView.setError(getString(R.string.number_format_error));
                result = false;
            }
        }

        if (TextUtils.isEmpty(mPriceStr)) {
            mPriceView.setError(getString(R.string.buying_price_error));
            result = false;
        } else {
            try {
                mPrice = new BigDecimal(mPriceStr);
            } catch (NumberFormatException e) {
                mPriceView.setError(getString(R.string.number_format_error));
                result = false;
            }
        }

        if (mMode == Mode.MODE_NORMAL && !checkTitle()) {
            result = false;
        }

        return result;
    }

    private boolean checkTitle() {
        try (Cursor c = getContentResolver().query(
                Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle),
                new String[]{ Constants.CostList._COUNT }, null, null, null)) {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                Log.e(this, "checkTitle : invalid cursor : " + c);
                return false;
            }

            int count = c.getInt(0);
            if (count > 0) {
                Log.w(this, "checkTitle : title " + mTitle + " already exist!");
                mTitleView.setError(getString(R.string.title_exist, mTitle));
                return false;
            }
        }
        return true;
    }

    private boolean trySaveRecord() {
        if (TextUtils.isEmpty(mTitle)) {
            return false;
        }

        int priceType = mPriceTypeView.getSelectedItemPosition();
        switch (priceType) {
            case 0:
                mPrice = mPrice.multiply(mTotal);
                break;
            case 1:
                break;
            default:
                return false;
        }

        ContentValues cv = new ContentValues();
        cv.put(Constants.CostList.TITLE, mTitle);
        cv.put(Constants.CostList.TOTAL, mTotal.toString());
        cv.put(Constants.CostList.PRICE, mPrice.toString());
        cv.put(Constants.CostList.PRICE_TYPE, mPriceTypeView.getSelectedItemPosition());
        cv.put(Constants.CostList.CURRENCY_TYPE, mCurrencyTypeView.getSelectedItemPosition());
        cv.put(Constants.CostList.BUY_TIME, mBuyTime);
        cv.put(Constants.CostList.NOTE, mNote);
        cv.put(Constants.CostList.TIME, System.currentTimeMillis());

        if (mMode == Mode.MODE_EDIT_RECORD) {
            String stringId = String.valueOf(mRecordId);
            Uri recordUri = Uri.withAppendedPath(Constants.CostList.RECORD_URI, stringId);
            int result = getContentResolver().update(recordUri, cv, null, null);
            if (result == 1) {
                return true;
            } else {
                Log.e(this, "update error, db returns " + result);
                return false;
            }
        } else {
            Uri result = getContentResolver().insert(Constants.CostList.THREAD_URI, cv);
            if (result == null) {
                Log.e(this, "insert error. cv = " + cv);
                toast(R.string.add_failed);
                return false;
            } else {
                toast(R.string.add_success);
            }
        }

        return true;
    }
}
