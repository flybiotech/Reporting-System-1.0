package com.shizhenbao.UI;

import android.app.AlertDialog;
import android.content.Context;
//import android.support.v7.app.AlertController;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.util.Const;

/**
 * Created by zhangbin on 2017/10/13.
 */

public class PrinterDialog {
    private Button bt_cancel, btn_printerbefore,btn_printernext;
    private TextView tvTitle;
    private AlertDialog.Builder builder;
    private Context context;
    private AlertDialog alertDialog;
    private ListView lv_showp;
//    private final AlertController.AlertParams P;
    /**
     * 初始化自定义确认框
     *
     * @param context
     * @param
     * @param确认按钮监听
     */
    public PrinterDialog(Context context) {
        this.context = context;
    }

    public void show(LinearLayout linearLayoutMain, String title, String btnPosi, String btnNega, String btncenter, View.OnClickListener positiveListener, View.OnClickListener negativeListener, View.OnClickListener nextListerner) {
        builder = new AlertDialog.Builder(context);
        View customView=getCustomView(linearLayoutMain,title, btnPosi, btnNega,btncenter ,positiveListener, negativeListener,nextListerner);
        builder.setView(customView);
        alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void show(LinearLayout linearLayoutMain,String title,View.OnClickListener positiveListener, View.OnClickListener negativeListener,View.OnClickListener nextListerner) {
        String bt_cancel = "取消";
        String bt_printerbefore = "上一页";
        String btn_printernext = "下一页";
        builder = new AlertDialog.Builder(context);

        View customView=getCustomView(linearLayoutMain,title, bt_cancel, bt_printerbefore,btn_printernext ,positiveListener, negativeListener,nextListerner);

        builder.setView(customView);

        alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void dimessDialog() {
//        Const.btnOptionEnable = false;
        if (alertDialog != null) {
            alertDialog.cancel();
        }
    }

    private View getCustomView(LinearLayout linearLayoutMain,String title, String btnPosi,String btnNega,String btncenter,View.OnClickListener positiveListener, View.OnClickListener negativeListener,View.OnClickListener nextListerner) {
        View mView = LayoutInflater.from(context).inflate(R.layout.printerdialogshow, null);
//        tvTitle = (TextView) mView.findViewById(R.id.tv_constumdialog);//title
//        tvTitle.setText(title);
//        lv_showp= (ListView) mView.findViewById(R.id.lv_printershow);
//        lv_showp.

        bt_cancel = (Button) mView.findViewById(R.id.bt_cancel);//取消
        bt_cancel.setText(btnPosi);
        bt_cancel.setOnClickListener(positiveListener);

        btn_printerbefore = (Button) mView.findViewById(R.id.btn_printerbefore);//上一页
        btn_printerbefore.setText(btnNega);
        btn_printerbefore.setOnClickListener(negativeListener);

        btn_printernext = (Button) mView.findViewById(R.id.btn_printernext);//下一页
        btn_printernext.setText(btncenter);
        btn_printernext.setOnClickListener(negativeListener);

//        btn_yuantu = (Button) mView.findViewById(R.id.btn_yuantu);//原图
//        btn_yuantu.setOnClickListener(regativeListener);

        return mView;
    }

}
