package com.shizhenbao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.util.Item;

import java.util.List;

/**
 * Created by zhangbin on 2017/11/10.
 */

public class SelectAdapter1 extends BaseAdapter {
    private Context context;
    private List<Item> list;

    public SelectAdapter1(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_new, null);
            viewHolder.tv_pid = (TextView) convertView.findViewById(R.id.tv_pid);
            viewHolder.tv_pName = (TextView) convertView.findViewById(R.id.tv_pName);
            viewHolder.tv_pAge= (TextView) convertView.findViewById(R.id.tv_pAge);
            viewHolder.tv_pTel= (TextView) convertView.findViewById(R.id.tv_pTel);
            viewHolder.tv_pResouce= (TextView) convertView.findViewById(R.id.tv_pResouce);
            viewHolder.tv_pMarray= (TextView) convertView.findViewById(R.id.tv_pMarray);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_pid.setText(list.get(position).getpId());
        viewHolder.tv_pName.setText(list.get(position).getpName());
        viewHolder.tv_pAge.setText(list.get(position).getAge());
        viewHolder.tv_pTel.setText(list.get(position).getTel());
        viewHolder.tv_pResouce.setText(list.get(position).getTv_pResouce());
        viewHolder.tv_pMarray.setText(list.get(position).getTv_pMarray());
//        viewHolder.tv_pAge.setText("年龄："+list.get(position).getAge());
//        viewHolder.tv_pTel.setText("电话："+list.get(position).getTel());
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_pid, tv_pName,tv_pAge,tv_pTel,tv_pResouce,tv_pMarray;
    }
}
