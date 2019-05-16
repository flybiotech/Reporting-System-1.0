package com.shizhenbao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activity.R;

import java.util.List;

/**
 * Created by dell on 2017/6/19.
 */

public class SettingAdapter extends BaseAdapter {

    public List<String> list;
    private Context context;
    private double screenInches;//屏幕的尺寸

    public SettingAdapter(List<String> list, Context context,double screenInches) {
        this.list = list;
        this.context = context;
        this.screenInches = screenInches;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (screenInches > 6.0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_phone, null);
            }


            viewHolder.textView = (TextView) convertView.findViewById(R.id.listview_tv_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(list.get(position));
        return convertView;
    }

    public class ViewHolder {
        TextView textView;

    }



}
