package com.greenshadow.costbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenshadow.costbook.R;

import java.util.List;

public class IconSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private List<IconItem> mData;

    public IconSpinnerAdapter(Context context, List<IconItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public IconItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconItem item;
        if (convertView != null) {
            item = (IconItem) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_icon_spinner, parent, false);
            item = getItem(position);
            convertView.setTag(item);
        }
        ImageView icon = convertView.findViewById(R.id.icon);
        TextView label = convertView.findViewById(R.id.text);
        icon.setImageResource(item.iconId);
        label.setText(item.labelId);

        return convertView;
    }

    public static class IconItem {
        public IconItem(int iconId, int labelId) {
            this.iconId = iconId;
            this.labelId = labelId;
        }

        public int iconId;
        public int labelId;
    }
}
