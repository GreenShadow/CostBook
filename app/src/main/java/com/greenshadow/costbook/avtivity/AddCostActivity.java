package com.greenshadow.costbook.avtivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.provider.Constants;

import java.math.BigDecimal;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddCostActivity extends AppCompatActivity {
    private static final String TAG = AddCostActivity.class.getSimpleName();

    public static String EXTRA_TITLE = "extra_title";
    private static final int MENU_SAVE = 100;

    private Toolbar mToolBar;
    private TextInputLayout mTitleView;
    private TextInputLayout mTotalView;
    private TextInputLayout mPriceView;
    private TextInputLayout mBuyTimeView;
    private Spinner mPriceTypeView;
    private Spinner mCurrencyTypeView;
    private EditText mNoteView;

    private String mTitle, mBuyTime, mNote;
    private BigDecimal mTotal, mPrice;

    private DatePickerDialog mDialog;

    private boolean mIsRecordMode = false;

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
        mNoteView = findViewById(R.id.et_cost_note);
        setupInputs();

        setResult(RESULT_CANCELED);
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.w(TAG, "Action bar is null!");
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupInputs() {
        Intent i = getIntent();
        String title = i.getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            Log.d(TAG, "setupInputs : enter into add record mode.");
            mTitle = title;
            enterRecordMode();
        } else {
            Log.d(TAG, "setupInputs : enter into add tracks mode.");
            enterNormalMode();
        }

        mBuyTimeView.setOnClickListener(v -> showDatePickerDialog());
        mBuyTimeView.getEditText().setOnClickListener(v -> mBuyTimeView.callOnClick());
    }

    private void enterRecordMode() {
        Uri getThreads = Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle);
        try (Cursor c = getContentResolver().query(getThreads, null, null,
                null, null)) {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                Log.w(TAG, "Invalid cursor " + c);
                enterNormalMode();
                return;
            }

            mTitleView.setHintAnimationEnabled(false);
            mTitleView.getEditText().setText(mTitle);
            mTitleView.setEnabled(false);

            int currencySelection = c.getInt(c.getColumnIndex(Constants.CostList.CURRENCY_TYPE));
            if (currencySelection >= 0 && currencySelection <= 3) {
                mCurrencyTypeView.setSelection(currencySelection);
                mCurrencyTypeView.setEnabled(false);
            } else {
                Log.w(TAG, "Invalid selection " + currencySelection);
                enterNormalMode();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "query error!", e);
            enterNormalMode();
        }
        mIsRecordMode = true;
    }

    private void enterNormalMode() {
        mTitleView.setHintAnimationEnabled(true);
        mTitleView.setEnabled(true);
        mTitleView.getEditText().getText().clear();
        mCurrencyTypeView.setEnabled(true);
        mCurrencyTypeView.setSelection(0);
        mIsRecordMode = false;
    }

    private void showDatePickerDialog() {
        if (mDialog == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            mDialog = new DatePickerDialog(AddCostActivity.this,
                    android.R.style.Theme_Material_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = year + "/" + month + "/" + dayOfMonth;
                        mBuyTimeView.getEditText().setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            mDialog.setCanceledOnTouchOutside(false);
        } else if (mDialog.isShowing()) {
            return;
        }
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
                if (checkInputs() && tryAddTrack()) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void clearErrors() {
        mTitleView.setError(null);
        mTotalView.setError(null);
        mPriceView.setError(null);
    }

    private boolean checkInputs() {
        mTitle = mTitleView.getEditText().getText().toString();
        String mTotalStr = mTotalView.getEditText().getText().toString();
        String mPriceStr = mPriceView.getEditText().getText().toString();
        mBuyTime = mBuyTimeView.getEditText().getText().toString();
        mNote = mNoteView.getText().toString();

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

        if (!mIsRecordMode && !checkTitle()) {
            result = false;
        }

        return result;
    }

    private boolean checkTitle() {
        try (Cursor c = getContentResolver().query(
                Uri.withAppendedPath(Constants.CostList.THREAD_URI, mTitle),
                new String[]{Constants.CostList._COUNT}, null, null, null)) {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                Log.e(TAG, "checkTitle : invalid cursor : " + c);
                return false;
            }

            int count = c.getInt(0);
            if (count > 0) {
                Log.w(TAG, "checkTitle : title " + mTitle + " already exist!");
                mTitleView.setError(getString(R.string.title_exist, mTitle));
                return false;
            }
        }
        return true;
    }

    private boolean tryAddTrack() {
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
        cv.put(Constants.CostList.CURRENCY_TYPE, mCurrencyTypeView.getSelectedItemPosition());
        cv.put(Constants.CostList.BUY_TIME, mBuyTime);
        cv.put(Constants.CostList.NOTE, mNote);
        cv.put(Constants.CostList.TIME, System.currentTimeMillis());

        Uri result = getContentResolver().insert(Constants.CostList.THREAD_URI, cv);
        if (result == null) {
            Log.e(TAG, "insert error. cv = " + cv);
            Toast.makeText(this, getString(R.string.add_failed), Toast.LENGTH_LONG).show();
            return false;
        } else {
            Toast.makeText(this, getString(R.string.add_success), Toast.LENGTH_LONG).show();
        }

        return true;
    }
}
