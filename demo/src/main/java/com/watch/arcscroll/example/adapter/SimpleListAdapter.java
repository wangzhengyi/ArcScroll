package com.watch.arcscroll.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.watch.arcscroll.example.R;

import java.util.List;

/**
 * Simple list adapter for list view
 */
public class SimpleListAdapter extends BaseAdapter {
    private List<String> mList;
    private final LayoutInflater mInflater;

    public SimpleListAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.id_list_item);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mTextView.setText((CharSequence) getItem(position));

        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
    }
}
