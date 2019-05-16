package com.shizhenbao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.shizhenbao.util.Item;

import java.util.List;

/**
 * Created by zhangbin on 2017/4/28.
 */

public class JianjiAdapter extends BaseAdapter {
    private List<Item>list;
    private Context context;
    public JianjiAdapter(Context context, List<Item>list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.jianji_preview, null);
            viewHolder.iv_jianji = (ImageView) view.findViewById(R.id.iv_jianji);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Glide.with(context)//图片加载框架
                .load(list.get(i).getJianjiPath())
                .crossFade()
                .into(viewHolder.iv_jianji);
        return view;
    }
    private class ViewHolder{
        private ImageView iv_jianji;
    }
}
