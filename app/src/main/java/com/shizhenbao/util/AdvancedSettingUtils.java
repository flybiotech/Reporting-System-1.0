package com.shizhenbao.util;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.activity.R;
import com.shizhenbao.UI.MyEditTextDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统中心高级设置功能类
 */
public class AdvancedSettingUtils {

    private Context mContext;
    private MyEditTextDialog myEditTextDialog;
    private List<String> stringList;//高级设置中listview的集合
    private RecoveryUtils recoveryUtils;
    public AdvancedSettingUtils(Context mContext){
        this.mContext = mContext;
        myEditTextDialog = new MyEditTextDialog(mContext);
        recoveryUtils = new RecoveryUtils(mContext);
    }

    /**
     * 给listview 集合添加数据
     */
    public List<String> addData(){
        stringList = new ArrayList<>();
        stringList.add(mContext.getString(R.string.setting_hospital_modify));
        stringList.add(mContext.getString(R.string.setting_department_modify));
//        stringList.add(mContext.getString(R.string.setting_FTP));
        stringList.add(mContext.getString(R.string.setting_picture_Route));
//        stringList.add(mContext.getString(R.string.setting_FTP_Route));
//        stringList.add(mContext.getString(R.string.setting_local_route));
        stringList.add(mContext.getString(R.string.setting_dialog_dissmiss));
        return stringList;
    }
    /**
     * 自定义弹出框布局
     */
    public void advancedDialog(List stringList){
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(mContext);//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ArrayAdapter arrayAdapter=new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,stringList);
        listView.setAdapter(arrayAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.setting_please_change)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long l) {
                switch (arg2){
                    case 0:
                        myEditTextDialog.editDialogShow(mContext.getString(R.string.user_regester_hospital_hint),0);
                        break;
                    case 1:
                        myEditTextDialog.editDialogShow(mContext.getString(R.string.user_regester_department_hint),1);
                        break;
//                    case 2:
//                        recoveryUtils.FTPSetDialog();
//                        break;
                    case 2:
                        myEditTextDialog.editDialogShow(mContext.getString(R.string.setting_save_Route),2);
                        break;
//                    case 4:
//                        myEditTextDialog.editDialogShow(mContext.getString(R.string.setting_FTP_Route_hint),3);
//                        break;
//                    case 3:
//                        myEditTextDialog.editDialogShow(mContext.getString(R.string.setting_local_Route_hint),4);
//                        break;
                    case 3:
                        myEditTextDialog.editDialogShow(mContext.getString(R.string.setting_dialog_dissmiss_hint),5);
                        break;
                    default:break;
                }
                dialog.cancel();
            }
        });
    }

}
