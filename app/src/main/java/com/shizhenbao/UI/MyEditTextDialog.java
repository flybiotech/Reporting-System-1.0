package com.shizhenbao.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DialerKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.EditRegularUtils;
import com.shizhenbao.util.OneItem;
import com.view.MyToast;

import org.apache.tools.ant.types.resources.comparators.Type;
import org.litepal.LitePal;

import java.io.File;
import java.sql.Types;
import java.util.List;

public class MyEditTextDialog {
    Doctor doctor;
    private Context mContext;
    private List<SystemSet> systemSets;
    public MyEditTextDialog(Context mContext){
        this.mContext = mContext;
    }
    /**
     * 带输入框的dialog
     */
    public void editDialogShow(String title,int temp){
        doctor = new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title); //设置对话框标题
        final EditText edit = new EditText(mContext);
        EditRegularUtils.editLenth(edit);
        builder.setView(edit);
        if(temp == 0){
            EditRegularUtils.editmaxLenth(edit,15);
            edit.setText(doctor.getEdit_hos_name());
        }else if(temp == 1){//科室
            EditRegularUtils.editmaxLenth(edit,10);
            edit.setText(doctor.getEdit_hos_keshi());
        }else if(temp == 2){//采集图片路径
            EditRegularUtils.editmaxLenth(edit,10);
            if(!OneItem.getOneItem().getGather_path().equals("")||OneItem.getOneItem().getGather_path()!=null){
                edit.setText(OneItem.getOneItem().getGather_path());
            }
        }else if(temp == 3){//ftp备份路径
            EditRegularUtils.editmaxLenth(edit,10);
            if(!OneItem.getOneItem().getBackUpNetPath().equals("")||OneItem.getOneItem().getBackUpNetPath()!=null){
                edit.setText(OneItem.getOneItem().getBackUpNetPath());
            }
        }else if(temp ==4){//本地备份
            EditRegularUtils.editmaxLenth(edit,10);
            if(!OneItem.getOneItem().getBackUpPath().equals("")||OneItem.getOneItem().getBackUpPath()!=null){
                edit.setText(OneItem.getOneItem().getBackUpPath());
            }
        }else if(temp ==5){//弹出框消失时间
            EditRegularUtils.editmaxLenth(edit,2);
            edit.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle(title+"（s）");
            systemSets = LitePal.findAll(SystemSet.class);
            if(systemSets.size() > 0){
                for(int i=0;i<systemSets.size();i++){
                    edit.setText(systemSets.get(0).getDialog_time()+"");
                }
            }
        }
        builder.setPositiveButton(mContext.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(temp == 0){//医院
                    doctor.setEdit_hos_name(edit.getText().toString().trim());
                    doctor.save();
                }else if(temp == 1){//科室
                    doctor.setEdit_hos_keshi(edit.getText().toString().trim());
                    doctor.save();
                }else if(temp == 2){
                    setSavePath(edit);
                }else if(temp == 3){
                    setSaveFTPPath(edit);
                }else if(temp ==4){
                  setLocalBackupPath(edit);
                }else if(temp == 5){
                   setDialogDismissTime(edit);
                }

            }
        });

        builder.setNegativeButton(mContext.getString(R.string.image_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    /**
     * 设置本地保存图片的路径
     */
    private void setSavePath(EditText savePath){
        String msg = savePath.getText().toString().trim();
        Path path = new Path();//这个数据表是为了一键清除时删除所有设置过的图片保存路径
        path.setPicPath(msg);
        path.save();
        systemSets = LitePal.findAll(SystemSet.class);
        SystemSet systemSet = null;
        //系统设置的数据表中最多只有一条数据
        if(systemSets.size() == 0){
            systemSet= new SystemSet();
            systemSet.setGather_path(msg);
        }else {
            for(int i =0;i<systemSets.size();i++){
                systemSet = systemSets.get(0);
                systemSet.setGather_path(msg);
            }
        }
        systemSet.save();
        //将图片保存途径暂时保存到软件中，退出清除
        OneItem.getOneItem().setGather_path(msg);
    }
    /**
     * 设置ftp保存图片的路径
     */
    private void setSaveFTPPath(EditText saveFTPPath){
        String ftpPath = saveFTPPath.getText().toString().trim();
        systemSets = LitePal.findAll(SystemSet.class);
        SystemSet systemSet = null;
        if(systemSets.size() == 0){
            systemSet = new SystemSet();
            systemSet.setBackUpNetPath(ftpPath);
        }else if(systemSets.size() > 0){
            for(int i =0;i<systemSets.size();i++){
                systemSet = systemSets.get(0);
                systemSet.setBackUpNetPath(ftpPath);
            }
        }
        systemSet.save();
        OneItem.getOneItem().setBackUpNetPath(ftpPath);
    }

    /**
     * 本地备份路径
     */
    private void setLocalBackupPath(EditText editText){
        String path = editText.getText().toString().trim();
        systemSets = LitePal.findAll(SystemSet.class);
        SystemSet systemSet = null;
        if(systemSets.size() == 0){
            systemSet = new SystemSet();
            systemSet.setBackUpPath(path);
        }else {
            for(int i=0;i<systemSets.size();i++){
                systemSet = systemSets.get(0);
                systemSet.setBackUpPath(path);
            }
        }
        systemSet.save();
        OneItem.getOneItem().setBackUpPath(path);
    }

    /**
     * 弹出框消失时间设置
     */
    private void setDialogDismissTime(EditText edit){
        int temp;
        if(edit.getText().toString().trim()!=null&&!edit.getText().toString().trim().equals("")){
            temp=Integer.parseInt(edit.getText().toString().trim());
        }else {
            MyToast.showToast(mContext,mContext.getString( R.string.setting_dialog_dissmiss_faild1));
            return;
        }
        if(temp>=3 && temp <=15){
            List<SystemSet> system= LitePal.findAll(SystemSet.class);
            if(system.size()>0){
                for(int j=0;j<system.size();j++){
                    system.get(0).setDialog_time(Integer.parseInt(edit.getText().toString().trim()));
                    Const.delayTime=Integer.parseInt(edit.getText().toString().trim());
                }
            }
            system.get(0).save();
            MyToast.showToast(mContext,mContext.getString( R.string.setting_dialog_dissmiss_success));
        }else {
            MyToast.showToast(mContext,mContext.getString( R.string.setting_dialog_dissmiss_faild2));
            return;
        }
    }
}
