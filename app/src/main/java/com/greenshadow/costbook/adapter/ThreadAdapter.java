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

public class ThreadAdapter extends AbsCursorRecyclerAdapter<ThreadAdapter.BindHolder> {
    // This request code will handle in ThreadListActivity
    public static final int REQUEST_EDIT_RECORD = 1000;

    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;

    private Activity mContext;
    private Handler mHandler;
    private int mExpandedPosition = -1;
    private ParentHolder mExpandedItem = null;

    private AlertDialog mDeleteDialog;

    public ThreadAdapter(Activity context, Cursor cursor, Handler handler) {
        super(cursor);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public BindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_CHILD:
                item = inflater.inflate(R.layout.item_record_list_child, parent, false);
                return new ChildHolder(item);
            case TYPE_PARENT:
            default:
                item = inflater.inflate(R.layout.item_record_list_parent, parent, false);
                return new ParentHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(BindHolder viewHolder, int position) {
        if (mExpandedPosition >= 0 && position >= mExpandedPosition) {
            position -= 1;
        }
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    public void onBindViewHolder(BindHolder holder, Cursor cursor) {
        holder.bind(cursor, cursor.getPosition());
    }

    @Override
    public int getItemViewType(int position) {
        if (mExpandedPosition == position) {
            return TYPE_CHILD;
        }
        return TYPE_PARENT;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (mExpandedPosition < 0) {
            return count;
        } else {
            return count + 1;
        }
    }

    public void onActivityStop() {
        // When activity recreated, this variable will point to an incorrect object. Clear it now.
        if (mExpandedItem != null) {
            mExpandedItem.onClose();
            mExpandedItem = null;
        }
    }

    private void editRecord(int id) {
        Intent i = new Intent(AddCostActivity.ACTION_EDIT_RECORD);
        i.putExtra(AddCostActivity.EXTRA_RECORD_ID, id);
        mContext.startActivityForResult(i, REQUEST_EDIT_RECORD);
    }

    private void tryDeleteRecord(int id) {
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
        mExpandedItem = null;
        mExpandedPosition = -1;

        Uri recordUri = Uri.withAppendedPath(Constants.CostList.RECORD_URI, String.valueOf(recordId));
        mContext.getContentResolver().delete(recordUri, null, null);
        mHandler.removeMessages(ThreadListActivity.WHAT_REFRESH_LIST);
        mHandler.sendEmptyMessage(ThreadListActivity.WHAT_REFRESH_LIST);
    }

    public abstract class BindHolder extends RecyclerView.ViewHolder {
        BindHolder(View itemView) {
            super(itemView);

        }

        protected abstract void bind(Cursor cursor, int position);
    }

    public class ParentHolder extends BindHolder {
        private View mItem;
        private TextView mTotalView;
        private TextView mDateView;
        private View mDivider;

        private int mId;

        ParentHolder(View itemView) {
            super(itemView);

            mItem = itemView;
            mTotalView = itemView.findViewById(R.id.record_total);
            mDateView = itemView.findViewById(R.id.record_date);
            mDivider = itemView.findViewById(R.id.divider);
        }

        @Override
        protected void bind(Cursor cursor, int position) {
            mId = cursor.getInt(cursor.getColumnIndex(Constants.CostList._ID));

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
                    mTotalView.setText("+");
                    mTotalView.append(totalNum.toPlainString());
                    mTotalView.setTextColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    mTotalView.setText(totalNum.toPlainString());
                    mTotalView.setTextColor(
                            mContext.getResources().getColor(android.R.color.holo_green_dark));
                }
            }

            // date
            long dateMillis = cursor.getLong(cursor.getColumnIndex(Constants.CostList.TIME));
            Date date = new Date(dateMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            mDateView.setText(dateFormat.format(date));

            mItem.setHapticFeedbackEnabled(true);
            mItem.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            if (position + 1 == mExpandedPosition) {
                mExpandedItem = this;
                onExpand();
            } else {
                onClose();
            }

            // item click
            mItem.setOnClickListener(v -> {
                if (mExpandedPosition < 0) { // no item selected
                    mExpandedPosition = position + 1;
                    mExpandedItem = ParentHolder.this;
                    notifyItemInserted(mExpandedPosition);
                    if (mExpandedItem != null) {
                        mExpandedItem.onExpand();
                    }
                } else if (mExpandedPosition - 1 == position) { // this item selected
                    notifyItemRemoved(mExpandedPosition);
                    mExpandedPosition = -1;
                    if (mExpandedItem != null) {
                        mExpandedItem.onClose();
                        mExpandedItem = null;
                    }
                } else { // other item selected
                    notifyItemRemoved(mExpandedPosition);
                    if (mExpandedItem != null) {
                        mExpandedItem.onClose();
                    }
                    mExpandedPosition = position + 1;
                    mExpandedItem = ParentHolder.this;
                    notifyItemInserted(mExpandedPosition);
                    if (mExpandedItem != null) {
                        mExpandedItem.onExpand();
                    }
                }
            });

            // item long click
            mItem.setOnLongClickListener(v -> {
                PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
                menu.inflate(R.menu.delete_menu);
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        tryDeleteRecord(mId);
                        return true;
                    }
                    return false;
                });
                menu.show();
                return true;
            });
        }

        private void onExpand() {
            if (mDivider != null) {
                mDivider.setVisibility(View.GONE);
            }
        }

        private void onClose() {
            if (mDivider != null) {
                mDivider.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ChildHolder extends BindHolder {
        private View mNoteContainer;
        private TextView mNoteView;
        private MaterialButton mEditButton;
        private MaterialButton mDeleteButton;

        private int mId;

        ChildHolder(View itemView) {
            super(itemView);

            mNoteContainer = itemView.findViewById(R.id.record_note_container);
            mNoteView = itemView.findViewById(R.id.record_note);
            mEditButton = itemView.findViewById(R.id.record_edit);
            mDeleteButton = itemView.findViewById(R.id.record_delete);
        }

        @Override
        protected void bind(Cursor cursor, int position) {
            mId = cursor.getInt(cursor.getColumnIndex(Constants.CostList._ID));

            // note
            String noteText = cursor.getString(cursor.getColumnIndex(Constants.CostList.NOTE));
            if (!TextUtils.isEmpty(noteText)) {
                mNoteContainer.setVisibility(View.VISIBLE);
                mNoteView.setText(noteText);
            } else {
                mNoteContainer.setVisibility(View.GONE);
            }

            mEditButton.setOnClickListener(v -> editRecord(mId));
            mDeleteButton.setOnClickListener(v -> tryDeleteRecord(mId));
        }
    }
}
