package com.shizhenbao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.activity.R;
import com.bumptech.glide.Glide;
import com.fragment.SDImageFragment;
import com.shizhenbao.activity.JianjiActivity;
import com.shizhenbao.activity.PreviewActivity;
import com.shizhenbao.fragments.fragmentJianji.NOTYETFragment;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.view.ImageViewRotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2017/9/27.
 */

public class ImageManagerAdapter02 extends BaseAdapter {
    private List<Item> list;//战士的图片的集合
    private Activity context;
    private static List<String> list1 = new ArrayList<>();//选择的图片的路径的集合
    private int i;//图片选择时的position
    private SDImageFragment sdImageFragment;
    public ImageManagerAdapter02(Activity context, List<Item> list) {
        this.context = context;
        this.list = list;
    }
    public SDImageFragment getSDImageFragment(){
        return sdImageFragment;
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
            view = LayoutInflater.from(context).inflate(R.layout.imagemanager, null);
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

                    Intent intent = new Intent(context, JianjiActivity.class);
                    intent.putExtra("msg1", list.get(position).getPath());
                    Const.imageshow=2;
//                    intent.putExtra("msg",1);
                    Const.dialogshow=1;
//                    intent.putExtra("msg2",2);
                    context.startActivity(intent);
                    context.finish();
            }
        });
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//通过checkbox选择多张图片
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//如果chekbox被选中，与其对应的图片路径会被加入到list集合里
                    list1.add(list.get(position).getPath());
                } else {
                    list1.remove(list.get(position).getPath());
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
        list1.clear();
    }

    public static List getList() {//返回被选中图片的list集合
        return list1;
    }

    public void setNotyetFragment(NOTYETFragment fragment) {

    }


}
