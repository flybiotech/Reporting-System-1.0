package com.shizhenbao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.fragments.BaseFragment;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Item;

import java.util.List;

/**
 * Created by dell on 2017/9/26.
 */

public class JianJiSearchAdapter  extends BaseAdapter {
    private Context context;
    private List<User> list;

    public JianJiSearchAdapter(Context context, List<User> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.listview, null);
            viewHolder.tv_pid = (TextView) convertView.findViewById(R.id.tv_pid);
            viewHolder.tv_pName = (TextView) convertView.findViewById(R.id.tv_pName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_pid.setText(list.get(position).getpId());
        viewHolder.tv_pName.setText(list.get(position).getpName());
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_pid, tv_pName;
    }
}
