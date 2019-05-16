package com.shizhenbao.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.bean.PrintdialogBean;
import com.view.ImageViewRotation;

import java.util.List;

public class Printdialogadapter extends BaseAdapter {

    private List<String>printdialogBeans;
    private Context mContext;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();//用来存放checkbox 的选中状态

    public Printdialogadapter(Context mContext,List<String>printdialogBeans){
        this.mContext = mContext;
        this.printdialogBeans = printdialogBeans;
//        this.sparseBooleanArray = stateCheckedMap;
    }

    @Override
    public int getCount() {
        return printdialogBeans.size();
    }

    @Override
    public String getItem(int i) {
        return printdialogBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return printdialogBeans.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        ViewHolder viewHolder = null;
//        if (view == null) {
//            viewHolder = new ViewHolder();
//            view = LayoutInflater.from(mContext).inflate(R.layout.print_dialog_listview, null);
//            viewHolder.tv_show =  view.findViewById(R.id.tv_show);
//            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.cb_show);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }
//        viewHolder.tv_show.setText(printdialogBeans.get(i).getTv_show());
//        viewHolder.checkBox.setChecked(sparseBooleanArray.get(i));//设置checkbox是否选中
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.print_dialog_listview,viewGroup,false);
        }
        ((TextView) view.findViewById(R.id.tv_content))
                .setText(getItem(i));
        return view;
    }

    public class ViewHolder{
        private TextView tv_show;
        public CheckBox checkBox;
    }

//    public void setShowCheckBox(boolean showCheckBox) {
////        isShowCheckBox = showCheckBox;
////    }
}
