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
import android.widget.TextView;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.shizhenbao.activity.PreviewActivity;
import com.shizhenbao.util.Item;
import com.view.ImageViewRotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 用户 on 2017/4/1.
 */

public class ImageDCAdapter extends BaseAdapter {
    private List<Item> list;//展示的图片的集合
    private Context context;
    private static List<String> list1=null;//选择的图片的集合
    private int i;

    public ImageDCAdapter(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
        list1 = new ArrayList<>();
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
        Glide.with(context)//图片展示框架
                .load(list.get(position).getPath())//图片展示的路径
                .crossFade()
                .into(viewHolder.iv);//图片展示的位置

        viewHolder.iv.setOnClickListener(new View.OnClickListener() {//点击图片进行预览
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra("msg", list.get(position).getPath());
                context.startActivity(intent);
            }
        });
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    list1.add(list.get(position).getPath());//将选择的图片路径添加到list集合里
                } else {
                    list1.remove(list.get(position).getPath());
//                    for (int j = 0; j < list1.size(); j++) {
//                        if (list1.get(j) == list1.get(i)) {
//                            list1.remove(j);//如果取消选择，相应的图片会从list集合中移除
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

    public static List getList() {//返回选择的图片的集合
        return list1;
    }

}
