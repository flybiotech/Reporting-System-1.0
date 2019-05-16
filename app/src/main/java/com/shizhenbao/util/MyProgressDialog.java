package com.shizhenbao.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.activity.R;

public class MyProgressDialog extends ProgressDialog {
    private ProgressDialog progressDialog ;
    private Context mContext;
    public MyProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * dialog显示的方法
     */
    public void dialogShow(String msg){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(mContext.getString(R.string.print_dialog_title));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.setMessage(msg);
        progressDialog.setButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.cancel();
            }
        });
        progressDialog.show();
    }

    /**
     * dialog取消的方法
     */
    public void dialogCancel(){
        if(progressDialog != null){
            progressDialog.cancel();
        }
    }
}
