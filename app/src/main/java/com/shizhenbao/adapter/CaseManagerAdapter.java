package com.shizhenbao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.UnicodeSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.ListDevAdapter;
import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.activity.CaseListManagerActivity;
import com.shizhenbao.activity.CaseManagerActivity;
import com.shizhenbao.activity.NewOrOldCaseActivity;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.suke.widget.SwitchButton;
import com.view.MyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/9/12.
 */

public class CaseManagerAdapter extends BaseAdapter {
    private Context context;
    private List<User> mList;//查询的患者的集合
    private LayoutInflater inflater;
    private List<User> listSelect;//checkbox 选中的患者的集合
    private List<Boolean> isCheck;//checkbox 选中的患者的集合
//    int checkCount = 0;
    public CaseManagerAdapter(Context context, List<User> list,List<Boolean>isCheck) {
        this.context = context;
        this.mList = list;
        this.isCheck = isCheck;
        listSelect = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
//        View view;
        if (convertView == null) {
            // 如果convertView为空，则表示第一次显示该条目，需要创建一个view
            convertView = inflater.inflate(R.layout.item_casesearchlist, null);
            holder = new ViewHolder();
            //将findviewbyID的结果赋值给holder对应的成员变量
            holder.tvBianHao = (TextView) convertView.findViewById(R.id.tv_caselist_bianhao);
            holder.tvAge =(TextView) convertView.findViewById(R.id.tv_caselist_age);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_caselist_name);
            holder.tvTel = (TextView) convertView.findViewById(R.id.tv_caselist_tel);
            holder.tvCheckTime = (TextView) convertView.findViewById(R.id.tv_caselist_checktime);
            holder.mBox = (CheckBox) convertView.findViewById(R.id.switch_case_checkbox);
            holder.item_case_linear = (LinearLayout) convertView.findViewById(R.id.item_case_linear);

            holder.tvBianHao_mark = (TextView) convertView.findViewById(R.id.tv_caselistManager_biaohao);
            holder.tvAge_mark =(TextView) convertView.findViewById(R.id.tv_caselistManager_age);
            holder.tvName_mark = (TextView) convertView.findViewById(R.id.tv_caselistManager_name);
            holder.tvTel_mark = (TextView) convertView.findViewById(R.id.tv_caselistManager_tel);
            holder.tvCheckTime_mark = (TextView) convertView.findViewById(R.id.tv_caselistManager_checktime);

            // 将holder与view进行绑定
            convertView.setTag(holder);
        } else {
            // 否则表示可以复用convertView
//            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        // 直接操作holder中的成员变量即可，不需要每次都findViewById
        holder.tvBianHao.setText(":"+mList.get(position).getpId());
        holder.tvAge.setText(":"+mList.get(position).getAge());
        holder.tvName.setText(":"+mList.get(position).getpName());
        holder.tvTel.setText(":"+mList.get(position).getTel());
        holder.tvCheckTime.setText(":"+mList.get(position).getRegistDate());//病人登记的日期
        holder.tvCheckTime.setText(":"+mList.get(position).getRegistDate());
        if (mList.get(position).getIs_diag() == 1) {
            holder.tvBianHao.setTextColor(Color.GRAY);
            holder.tvAge.setTextColor(Color.GRAY);
            holder.tvName.setTextColor(Color.GRAY);
            holder.tvTel.setTextColor(Color.GRAY);
            holder.tvCheckTime.setTextColor(Color.GRAY);

            holder.tvBianHao_mark.setTextColor(Color.GRAY);
            holder.tvAge_mark.setTextColor(Color.GRAY);
            holder.tvName_mark.setTextColor(Color.GRAY);
            holder.tvTel_mark.setTextColor(Color.GRAY);
            holder.tvCheckTime_mark.setTextColor(Color.GRAY);

//            holder.linear_caselist.setBackgroundResource(R.color.gray);
        } else {
            holder.tvBianHao.setTextColor(Color.BLACK);
            holder.tvAge.setTextColor(Color.BLACK);
            holder.tvName.setTextColor(Color.BLACK);
            holder.tvTel.setTextColor(Color.BLACK);
            holder.tvCheckTime.setTextColor(Color.BLACK);


            holder.tvBianHao_mark.setTextColor(Color.BLACK);
            holder.tvAge_mark.setTextColor(Color.BLACK);
            holder.tvName_mark.setTextColor(Color.BLACK);
            holder.tvTel_mark.setTextColor(Color.BLACK);
            holder.tvCheckTime_mark.setTextColor(Color.BLACK);
//            holder.linear_caselist.setBackgroundResource(R.color.status_text);
        }

        holder.item_case_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listSelect.size()==2) {//选择了病例对比
                    Intent intent = new Intent(context, NewOrOldCaseActivity.class);
                    Const.mListUser = listSelect;
                    context.startActivity(intent);
                } else if (listSelect.size()<2){
                    Intent intent = new Intent(context, CaseManagerActivity.class);
                    intent.putExtra("user",mList.get(position));
                    Logger.e("user  :  "+mList.get(position));
                    context.startActivity(intent);
                }else {
                    MyToast.showToast(context,context.getString(R.string.case_only_two));
//                    Toast.makeText(context,context.getString(R.string.case_only_two), Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.mBox.setChecked(isCheck.get(position));
        holder.mBox.setTag(position);

          holder.mBox.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //hole =false 表示选中了， hole=true表示没选
                  boolean hold = isCheck.get((Integer) v.getTag());
                  isCheck.set((Integer) v.getTag(), !hold);
                  Logger.e("(Integer) v.getTag()  : "+(Integer) v.getTag()+" hold: "+hold);
                  if (!hold) {
                    listSelect.add(mList.get((Integer) v.getTag()));
                      MyToast.showToast(context,context.getString(R.string.case_is_two));
//                      Toast.makeText(context, context.getString(R.string.case_is_two), Toast.LENGTH_SHORT).show();
                      Logger.e("选中了 现在有几个 ："+listSelect.size());
//                    mSwitchClickListener.OnSelectClicked(listSelect,position);
//                    Toast.makeText(context, "开", Toast.LENGTH_SHORT).show();
                } else {
                    listSelect.remove(mList.get((Integer) v.getTag()));
                      Logger.e("删除了患者 还剩下 ："+listSelect.size());
//                    mSwitchClickListener.OnClearClicked(position);
//                    Toast.makeText(context, "关", Toast.LENGTH_SHORT).show();
                }
              }
          });




        return convertView;
    }

class ViewHolder{
    TextView tvBianHao;
    TextView tvAge;
    TextView tvName;
    TextView tvTel;
    TextView tvCheckTime;
    CheckBox mBox;
    LinearLayout item_case_linear;
    TextView tvBianHao_mark;
    TextView tvAge_mark;
    TextView tvName_mark;
    TextView tvTel_mark;
    TextView tvCheckTime_mark;
}



}
