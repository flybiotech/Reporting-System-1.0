package com.shizhenbao.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.activity.R;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.connect.DevConnect;

/**
 * 删除数据类，包括本地和数据库（数据进行FTP备份后询问是否删除，确认2次后开始删除，只删除超过6天的数据）
 */
public class DeleteDataUtils {
    private Context mContext;
    private CustomConfirmDialog customConfirmDialog;
    private MyProgressDialog myProgressDialog;

    public DeleteDataUtils(Context mContext){
        this.mContext = mContext;
        customConfirmDialog = new CustomConfirmDialog(mContext);
        myProgressDialog = new MyProgressDialog(mContext);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myProgressDialog.dialogCancel();
        }
    };
    /**
     * 弹出询问框，是否删除
     */
    public void askDialog(){
        customConfirmDialog.show(mContext.getString(R.string.patient_show_delete_all_prompt), mContext.getString(R.string.setting_next), mContext.getString(R.string.button_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customConfirmDialog.dimessDialog();
                askAgainDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customConfirmDialog.dimessDialog();
            }
        });
    }

    /**
     * 再次弹出询问框，是否删除
     */
    private void askAgainDialog(){
        customConfirmDialog.show(mContext.getString(R.string.setting_delete_sure_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确定删除
//                LitePal.deleteAll(User.class);
                customConfirmDialog.dimessDialog();
                myProgressDialog.dialogShow(mContext.getString(R.string.print_dialog_title));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Message message=handler.obtainMessage();
                        new DevConnect(mContext,handler).initData();
                        handler.sendMessage(message);
                    }
                }).start();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//不删除
                customConfirmDialog.dimessDialog();
            }
        });
    }
}
