package com.shizhenbao.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.shizhenbao.activity.PreviewActivity;
import com.shizhenbao.util.Item;
import com.view.ImageViewRotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2017/9/28.
 */

public class Image2Adapter extends BaseAdapter{
    private List<Item> list;//战士的图片的集合
    private Context context;
    private static List<String> list1 = new ArrayList<>();//选择的图片的路径的集合
    private int i;//图片选择时的position

    public Image2Adapter(Context context, List<Item> list) {
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
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        i = position;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.gridviewlist, null);
            viewHolder.iv = (ImageViewRotation) view.findViewById(R.id.iv);
            viewHolder.cb = (CheckBox) view.findViewById(R.id.cb);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Glide.with(context)//图片加载框架
                .load(list.get(position).getPath())//图片的路径
                .crossFade()
                .into(viewHolder.iv);

        viewHolder.iv.setOnClickListener(new View.OnClickListener() {//点击图片进行预览
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra("msg", list.get(position).getPath());
                context.startActivity(intent);
            }
        });
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//通过checkbox选择多张图片
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//如果chekbox被选中，与其对应的图片路径会被加入到list集合里
                    list1.add(list.get(position).getPath());
                } else {
                    list1.remove(list.get(position).getPath());
//                    for (int j = 0; j < list1.size(); j++) {
//                        if (list1.get(j) == list1.get(i)) {//如果被选中的checkbox被取消，相应的图片也会从list集合里移除
//                            list1.remove(j);
//                        }
//                    }
                }
            }
        });
        return view;
    }

    class ViewHolder {
        private ImageViewRotation iv;
        private CheckBox cb;
    }

    public static void setList() {
        if (list1 != null) {
            list1.clear();
        }

    }

    public static List getList() {//返回被选中图片的list集合
        return list1;
    }
}
