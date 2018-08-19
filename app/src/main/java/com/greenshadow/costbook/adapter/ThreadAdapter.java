package com.greenshadow.costbook.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.greenshadow.costbook.R;
import com.greenshadow.costbook.provider.Constants;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThreadAdapter extends CursorAdapter {
    private static final String TAG = ThreadAdapter.class.getSimpleName();

    public ThreadAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_record_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView totalView = view.findViewById(R.id.record_total);
        String totalText = cursor.getString(cursor.getColumnIndex(Constants.CostList.TOTAL));
        BigDecimal totalNum = null;
        try {
            totalNum = new BigDecimal(totalText);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Format total error!", e);
        }
        if (totalNum != null) {
            if (totalNum.compareTo(BigDecimal.ZERO) > 0) {
                totalView.setText("+");
                totalView.append(totalNum.toPlainString());
                totalView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                totalView.setText(totalNum.toPlainString());
                totalView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        }

        TextView dateView = view.findViewById(R.id.record_date);
        long dateMillis = cursor.getLong(cursor.getColumnIndex(Constants.CostList.TIME));
        Date date = new Date(dateMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        dateView.setText(dateFormat.format(date));

        TextView noteView = view.findViewById(R.id.record_note);
        noteView.setText(cursor.getString(cursor.getColumnIndex(Constants.CostList.NOTE)));
    }
}
