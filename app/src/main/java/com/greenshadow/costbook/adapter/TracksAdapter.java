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

public class TracksAdapter extends CursorAdapter {
    private static String TAG = TracksAdapter.class.getCanonicalName();

    public TracksAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_tracks_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = view.findViewById(R.id.title);
        TextView status = view.findViewById(R.id.status);
        TextView cost = view.findViewById(R.id.cost);

        title.setText(cursor.getString(cursor.getColumnIndex(Constants.CostList.TITLE)));
        String priceText = cursor.getString(cursor.getColumnIndex(Constants.CostList.PRICE_SUM));

        BigDecimal priceNum = null;
        try {
            priceNum = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Format number error!", e);
        }

        if (priceNum != null) {
            if (priceNum.compareTo(BigDecimal.ZERO) > 0) {
                status.setText(R.string.cost);
                status.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                cost.setText(priceNum.toPlainString());
                cost.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                status.setText(R.string.profit);
                status.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                cost.setText(priceNum.abs().toPlainString());
                cost.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            cost.setText(priceText);
            cost.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
            status.setVisibility(View.GONE);
        }
    }

    public String getTitleAt(int position) {
        Cursor cursor = getCursorAt(position);
        if (cursor == null) {
            return "";
        }

        return cursor.getString(cursor.getColumnIndex(Constants.CostList.TITLE));
    }

    public String getCostAt(int position) {
        Cursor cursor = getCursorAt(position);
        if (cursor == null) {
            return "";
        }

        return cursor.getString(cursor.getColumnIndex(Constants.CostList.PRICE_SUM));
    }

    private Cursor getCursorAt(int position) {
        return (Cursor) getItem(position);
    }
}
