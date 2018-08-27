package com.greenshadow.costbook.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.greenshadow.costbook.R;
import com.greenshadow.costbook.activity.AddCostActivity;
import com.greenshadow.costbook.activity.ThreadListActivity;
import com.greenshadow.costbook.provider.Constants;
import com.greenshadow.costbook.utils.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class ThreadAdapter extends AbsCursorRecyclerAdapter<ThreadAdapter.CursorViewHolder> {
    // This request code will handle in ThreadListActivity
    public static final int REQUEST_EDIT_RECORD = 1000;

    private Activity mContext;
    private Handler mHandler;

    private AlertDialog mDeleteDialog;

    public ThreadAdapter(Activity context, Cursor cursor, Handler handler) {
        super(cursor);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public ThreadAdapter.CursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.item_record_list, parent, false);
        return new CursorViewHolder(item);
    }

    @Override
    public void onBindViewHolder(CursorViewHolder holder, Cursor cursor) {
        // total
        String totalText = cursor.getString(cursor.getColumnIndex(Constants.CostList.TOTAL));
        BigDecimal totalNum = null;
        try {
            totalNum = new BigDecimal(totalText);
        } catch (NumberFormatException e) {
            Log.e(this, "Format total error!", e);
        }
        if (totalNum != null) {
            if (totalNum.compareTo(BigDecimal.ZERO) > 0) {
                holder.mTotalView.setText("+");
                holder.mTotalView.append(totalNum.toPlainString());
                holder.mTotalView.setTextColor(
                        mContext.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                holder.mTotalView.setText(totalNum.toPlainString());
                holder.mTotalView.setTextColor(
                        mContext.getResources().getColor(android.R.color.holo_green_dark));
            }
        }

        // date
        long dateMillis = cursor.getLong(cursor.getColumnIndex(Constants.CostList.TIME));
        Date date = new Date(dateMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        holder.mDateView.setText(dateFormat.format(date));

        // note
        String noteText = cursor.getString(cursor.getColumnIndex(Constants.CostList.NOTE));
        holder.mNoteView.setText(noteText);

        holder.mItem.setHapticFeedbackEnabled(true);
        holder.mItem.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        // item click
        holder.mItem.setOnClickListener(v -> v.post(() -> {
            if (TextUtils.isEmpty(noteText)) {
                holder.mNoteContainer.setVisibility(View.GONE);
            } else {
                holder.mNoteContainer.setVisibility(View.VISIBLE);
            }

            boolean expand;
            if (holder.mBottomContainer.getVisibility() == View.VISIBLE) {
                holder.mBottomContainer.setVisibility(View.GONE);
                expand = false;
            } else {
                holder.mBottomContainer.setVisibility(View.VISIBLE);
                expand = true;
            }
            holder.mItem.setSelected(expand);
        }));

        // item long click
        holder.mItem.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate(R.menu.delete_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    tryDeleteRecord(cursor);
                    return true;
                }
                return false;
            });
            menu.show();
            return true;
        });

        // edit button clicked
        holder.mEditButton.setOnClickListener(v -> {
            if (!holder.mItem.isSelected()) {
                return;
            }

            editRecord(cursor);
        });

        // delete button clicked
        holder.mDeleteButton.setOnClickListener(v -> {
            if (!holder.mItem.isSelected()) {
                return;
            }

            tryDeleteRecord(cursor);
        });
    }

    private void editRecord(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Constants.CostList._ID));
        Intent i = new Intent(AddCostActivity.ACTION_EDIT_RECORD);
        i.putExtra(AddCostActivity.EXTRA_RECORD_ID, id);
        mContext.startActivityForResult(i, REQUEST_EDIT_RECORD);
    }

    private void tryDeleteRecord(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Constants.CostList._ID));
        if (mDeleteDialog == null) {
            mDeleteDialog = new AlertDialog.Builder(mContext, R.style.AppTheme_Dialog)
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.delete_record)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
        mDeleteDialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                mContext.getString(android.R.string.ok),
                (dialog, which) -> performDeleteRecord(id)
        );

        if (mDeleteDialog.isShowing()) {
            mDeleteDialog.dismiss();
        }
        mDeleteDialog.show();
    }

    private void performDeleteRecord(int recordId) {
        Uri recordUri = Uri.withAppendedPath(Constants.CostList.RECORD_URI, String.valueOf(recordId));
        mContext.getContentResolver().delete(recordUri, null, null);
        mHandler.removeMessages(ThreadListActivity.WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(ThreadListActivity.WHAT_REFRESH_LIST);
    }

    public class CursorViewHolder extends RecyclerView.ViewHolder {
        private View mItem;
        private TextView mTotalView;
        private TextView mDateView;
        private View mBottomContainer;
        private View mNoteContainer;
        private TextView mNoteView;
        private MaterialButton mEditButton;
        private MaterialButton mDeleteButton;

        public CursorViewHolder(View itemView) {
            super(itemView);

            mItem = itemView;
            mTotalView = itemView.findViewById(R.id.record_total);
            mDateView = itemView.findViewById(R.id.record_date);
            mBottomContainer = itemView.findViewById(R.id.record_bottom_container);
            mNoteContainer = itemView.findViewById(R.id.record_note_container);
            mNoteView = itemView.findViewById(R.id.record_note);
            mEditButton = itemView.findViewById(R.id.record_edit);
            mDeleteButton = itemView.findViewById(R.id.record_delete);
        }
    }
}
