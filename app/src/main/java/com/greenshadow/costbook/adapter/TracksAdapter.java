package com.greenshadow.costbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenshadow.costbook.R;
import com.greenshadow.costbook.activity.HomeActivity;
import com.greenshadow.costbook.activity.ThreadListActivity;
import com.greenshadow.costbook.provider.Constants;
import com.greenshadow.costbook.utils.Log;

import java.math.BigDecimal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class TracksAdapter extends AbsCursorRecyclerAdapter<TracksAdapter.CursorViewHolder> {
    private Context mContext;

    private AlertDialog mAlert;
    private Handler mHandler;

    public TracksAdapter(Context context, Cursor c, Handler handler) {
        super(c);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public CursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.item_tracks_list, parent, false);
        return new CursorViewHolder(item);
    }

    @Override
    public void onBindViewHolder(CursorViewHolder holder, Cursor cursor) {
        holder.bind(cursor);
    }

    private void tryDeleteRow(String title) {
        if (mAlert == null) {
            mAlert = new AlertDialog.Builder(mContext, R.style.AppTheme_Dialog)
                    .setTitle(R.string.are_you_sure)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> deleteRow(title))
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
        mAlert.setMessage(mContext.getString(R.string.delete_content, title));

        if (!mAlert.isShowing()) {
            mAlert.show();
        }
    }

    private void deleteRow(String title) {
        if (TextUtils.isEmpty(title)) {
            Log.w(this, "delete error! empty title");
            return;
        }

        Uri deleteUri = Uri.withAppendedPath(Constants.CostList.THREAD_URI, title);
        int row = mContext.getContentResolver().delete(deleteUri, null, null);
        if (row != 1) {
            Log.w(this, "delete error at " + title + ", SQLite returns = " + row);
        }
        mHandler.removeMessages(HomeActivity.WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(HomeActivity.WHAT_REFRESH_LIST);
    }

    private Drawable getIcon(Context context, int currencyType) {
        switch (currencyType) {
            case 0: // CNY
                return context.getResources().getDrawable(R.drawable.ic_cny);
            case 1: // BTC
                return context.getResources().getDrawable(R.drawable.ic_btc);
            case 2: // ETH
                return context.getResources().getDrawable(R.drawable.ic_eth);
            case 3: // USDT
                return context.getResources().getDrawable(R.drawable.ic_usdt);
            default:
                return context.getResources().getDrawable(R.drawable.ic_unknown);
        }
    }

    class CursorViewHolder extends RecyclerView.ViewHolder {
        private View mItem;
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mStatus;
        private TextView mCost;

        CursorViewHolder(View itemView) {
            super(itemView);

            mItem = itemView;
            mIcon = itemView.findViewById(R.id.coin_icon);
            mTitle = itemView.findViewById(R.id.title);
            mStatus = itemView.findViewById(R.id.status);
            mCost = itemView.findViewById(R.id.cost);
        }

        private void bind(Cursor cursor) {
            int currencyType = cursor.getInt(cursor.getColumnIndex(Constants.CostList.CURRENCY_TYPE));
            mIcon.setImageDrawable(getIcon(mContext, currencyType));

            String title = cursor.getString(cursor.getColumnIndex(Constants.CostList.TITLE));
            mTitle.setText(title);

            String priceText = cursor.getString(cursor.getColumnIndex(Constants.CostList.PRICE_SUM));
            BigDecimal priceNum = null;
            try {
                priceNum = new BigDecimal(priceText);
            } catch (NumberFormatException e) {
                Log.w(this, "Format number error!", e);
            }

            if (priceNum != null) {
                if (priceNum.compareTo(BigDecimal.ZERO) > 0) {
                    mStatus.setText(R.string.cost);
                    mStatus.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                    mCost.setText(priceNum.toPlainString());
                    mCost.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    mStatus.setText(R.string.profit);
                    mStatus.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
                    mCost.setText(priceNum.abs().toPlainString());
                    mCost.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
                }
            } else {
                mStatus.setVisibility(View.GONE);
                mCost.setText(priceText);
                mCost.setTextColor(mContext.getResources().getColor(android.R.color.primary_text_light));
            }

            mItem.setHapticFeedbackEnabled(true);
            mItem.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            mItem.setOnClickListener(v -> {
                Intent i = new Intent(mContext, ThreadListActivity.class);
                i.putExtra(ThreadListActivity.EXTRA_TITLE, title);
                mContext.startActivity(i);
            });

            mItem.setOnLongClickListener(v -> {
                PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
                menu.inflate(R.menu.delete_menu);
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        tryDeleteRow(title);
                        return true;
                    }
                    return false;
                });
                menu.show();
                return true;
            });
        }
    }
}
