package com.view;


import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.UI.MyTitleView;
import com.shizhenbao.util.Const;


public class LoadingDialog {
    private Dialog loadingDialog;
    private TextView textView;
    private MyTitleView mt_tv_show;
    public static LoadingDialog instance=null;
    public LoadingDialog(Context context){
        loadingDialog=new Dialog(context,R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.view_loadingdlg);
        loadingDialog.setCanceledOnTouchOutside(false);
        //用于平板的返回键是否起作用
//        loadingDialog.setCancelable(false);
        textView= (TextView) loadingDialog.findViewById(R.id.loading_message);
    }

    public LoadingDialog(Context context,boolean b){
        loadingDialog=new Dialog(context,R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.view_loadingdlg);
        loadingDialog.setCanceledOnTouchOutside(false);
        //用于平板的返回键是否起作用
        loadingDialog.setCancelable(b);
        textView= (TextView) loadingDialog.findViewById(R.id.loading_message);
    }

 //旋转90度的
    public LoadingDialog(Context context, String myTextview) {
        loadingDialog = new Dialog(context, R.style.myDialogTheme);
        loadingDialog.setContentView(R.layout.toast_view);
        loadingDialog.setCanceledOnTouchOutside(false);
        mt_tv_show = loadingDialog.findViewById(R.id.toast_viewss);
        mt_tv_show.setTextColor("#F8F8FF");
    }

    public void setMyMessage(String message) {
        mt_tv_show.setText(message);
    }
    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        textView.setText(message);
    }

    public boolean isShow() {
        return loadingDialog.isShowing();
    }

    /**
     *
     */
    public void dismiss(){
        loadingDialog.dismiss();
    }
    /**
     *
     */
    public void dialogShow(){
        loadingDialog.show();
    }


    public static LoadingDialog getInstance(Context context) {
        synchronized (LoadingDialog.class) {
            if (instance == null) {
                instance = new LoadingDialog(context);
            }
        }
        return instance;
    }

    public static LoadingDialog getInstance(Context context,boolean cancle) {
        synchronized (LoadingDialog.class) {
            if (instance == null) {
                instance = new LoadingDialog(context,cancle);
            }
        }
        return instance;
    }

    public static LoadingDialog getInstance() {
        return instance;
    }

}

