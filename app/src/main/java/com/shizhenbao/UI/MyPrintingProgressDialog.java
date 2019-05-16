package com.shizhenbao.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.print.PrintJob;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activity.R;
import com.hp.mss.hpprint.util.PItem;


public class MyPrintingProgressDialog extends AlertDialog {
    private ProgressBar progressBar;
    private Context mContext;
    private AlertDialog alertDialog;
    private Button bt_cancel;
    public MyPrintingProgressDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * 展示进度框
     */
    public void initDialogShow(PrintJob printJob){
        progressBar=new ProgressBar(mContext);
        TextView textView = new TextView(getContext());
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        linearLayoutMain.setPadding(30,30,20,20);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        linearLayoutMain.addView(progressBar);//往这个布局中加入进度条
        textView.setText(R.string.print_dialog_message);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        textView.setPadding(30,0,0,0);
        linearLayoutMain.addView(textView);
        alertDialog=new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.print_dialog_title)).setView(linearLayoutMain).setNegativeButton(mContext.getText(R.string.cancel),null).create();
        alertDialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                bt_cancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        printJob.cancel();
                        PItem.getOneItem().setPrintJob(null);
                    }
                });
            }
        });
        alertDialog.show();
        alertDialog.setCancelable(false);
    }
    /*
    关闭提示框
     */

    public void initDialogDismiss(){
        if(alertDialog!=null){
            alertDialog.dismiss();
        }
    }
}
