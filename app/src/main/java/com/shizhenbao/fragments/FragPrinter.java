package com.shizhenbao.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.print.PrintJob;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.application.MyApplication;
import com.hp.mss.hpprint.util.PItem;
import com.hp.mss.hpprint.util.PrintMetricsCollector;
import com.itextpdf.text.DocumentException;
import com.orhanobut.logger.Logger;
import com.shizhenbao.activity.DengjiYulanActivity;
import com.shizhenbao.activity.ImageActivity;
import com.shizhenbao.activity.XuanzePDFActivity;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.db.AlertDialogDao;
import com.shizhenbao.db.Backup;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.draw.GraffitiActivity;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.printer.HPprinter;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.PDFCreate;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.util.AlignedTextUtils;
import com.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by dell on 2017/5/22.
 */
//isPkgInstalled
public class FragPrinter  extends BaseFragment implements View.OnClickListener,PDFCreate.PdfCreateInterface{
    //bt_choise：选择图片    bt_baocun：生成报告   bt_new :选择患者    bt_choise：选择图片
    private Button btn_right,bt_giveup,bt_choise,bt_baocun,bt_left,bt_new,bt_address;
    private TextView tv_title;
    private EditText et_name,et_xibaoxue,et_dna,et_zhengzhuang,et_pinggu,et_quyu,et_yindaojin,et_nizhen,et_yijian,et_bianhao,et_handle;
    private EditText et_bianjie,et_color,et_xueguan,et_dianranse,et_cusuan;
    private ArrayList<String> mResults = new ArrayList<>();
    private List<Item> list;
    private List<User> userlist=new ArrayList<>();
    private List<User> userList=new ArrayList<>();
    AlertDialogDao dialog;
    Diacrisis dia = null;
    User user=null;
    Doctor doctor = null;
    private int a=1;//判断执行哪个方法
    private int c;//设置患者的pid
    private List<String>showList;
    private List<String>diaList;
    private List<Diacrisis>diacrisisList;
    private boolean saveState = false; //表示可以跳到打印机
    int b=0;//报告是否打印成功
    ProgressDialog xh_pDialog;//进度提示框
    AlertDialog select_dialog=null;
    private RelativeLayout rl;
    //声明打印机的类
    private HPprinter hpPrint;
    private static String TAG = "TAG_FragPrinter";
    private LoadingDialog loadingDialog;
    private TextView tv01,tv02,tv03,tv04,tv05,tv06,tv07,tv08,tv09,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18;
    private String[] tvName;
    PrintJob printJob;
    public static final int REQ_CODE_GRAFFITI = 101;
    ListView listView = null;
    int num=0;//判断翻页次数
    double temp;//患者信息所占页数
    int dialogset=1;//设置对话框是否消失
    int usersize=0;//所有未诊断患者的数量
    String oldId=null;
    ListView listView_self = null;
    EditText edit_self = null;
    private boolean isChina = true; //判断是否是英文的系统 true ：表示中文
    SimpleDateFormat formatter = null; //转化时间
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                b=0;
                xh_pDialog.dismiss();
            }else if(msg.what==0){
            }else if(msg.what==3){
            }else if(msg.what==-1){
                if(select_dialog.isShowing()){
                    select_dialog.dismiss();
                    Toast.makeText(getContext(), getContext().getString(R.string.image_dialog_automatic), Toast.LENGTH_SHORT).show();
                    a=2;
                    c=0;
                    onResume();
                }
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        view=inflater.inflate(R.layout.frag_layout_printer, container, false);

        initText(view);//解决文字两端对齐的问题
        init(view);
        try {
            if(OneItem.getOneItem().getName()!=null&&!OneItem.getOneItem().getName().equals("")){
                doctor =new LoginRegister().getDoctor(OneItem.getOneItem().getName());//登录用户信息查询
            }else {
                new UserManager().getExceName();
                doctor =new LoginRegister().getDoctor(OneItem.getOneItem().getName());//登录用户信息查询
            }
            setSimulateClick(view, 10, 5);
//            1：表示没有添加图片，2： 表示添加了图片
            OneItem.getOneItem().setD(1);
            initAddImage();
            initClick();

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
    private void initDele(){
        List<User>userList=DataSupport.where("pId=?",et_bianhao.getText().toString().trim()).find(User.class);
        if(userList.size()==0){
            a=3;
            onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        List<SystemSet> systemSet=DataSupport.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            String localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
    }

    @Override
    public void onResume() {//页面刷新
        super.onResume();
        PDFCreate.setPdfCreateInterfaceListener(this);
        printJob= PItem.getOneItem().getPrintJob();
        if(printJob!=null){
            getPosStatus();
        }
        if(a==1){
            if(OneItem.getOneItem().getC()){
                et_xibaoxue.setEnabled(true);
                et_yijian.setEnabled(true);
                et_nizhen.setEnabled(true);
                et_dna.setEnabled(true);
                et_pinggu.setEnabled(true);
                et_quyu.setEnabled(true);
                et_zhengzhuang.setEnabled(true);
                et_yindaojin.setEnabled(true);
                et_bianjie.setEnabled(true);
                et_color.setEnabled(true);
                et_xueguan.setEnabled(true);
                et_dianranse.setEnabled(true);
                et_cusuan.setEnabled(true);
                et_name.setEnabled(false);
                et_handle.setEnabled(false);
            }else {
                et_bianhao.setText("");
                et_name.setText("");
                et_name.setEnabled(false);
                et_xibaoxue.setEnabled(false);
                et_yijian.setEnabled(false);
                et_nizhen.setEnabled(false);
                et_dna.setEnabled(false);
                et_pinggu.setEnabled(false);
                et_quyu.setEnabled(false);
                et_zhengzhuang.setEnabled(false);
                et_yindaojin.setEnabled(false);
                et_bianjie.setEnabled(false);
                et_color.setEnabled(false);
                et_cusuan.setEnabled(false);
                et_dianranse.setEnabled(false);
                et_xueguan.setEnabled(false);
                et_handle.setEnabled(false);
                et_handle.setText("");
                et_yindaojin.setText("");
                et_xibaoxue.setText("");
                et_zhengzhuang.setText("");
                et_yijian.setText("");
                et_dna.setText("");
                et_nizhen.setText("");
                et_pinggu.setText("");
                et_quyu.setText("");
                et_bianjie.setText("");
                et_xueguan.setText("");
                et_cusuan.setText("");
                et_dianranse.setText("");
                et_color.setText("");
            }
        }else if(a==2){
            oldId=et_bianhao.getText().toString().trim();
            try {
                if(list.size()==0){//如果是第一次登记，id默认为0
                    saveState = false;
                    et_bianhao.setText(getString(R.string.patient_id));
                }else {
                    user=new LoginRegister().getUserName(Integer.parseInt(list.get(c).getpId()));//查询user数据库中最后一条记录
                    saveState = true;
                    et_bianhao.setText(user.getpId());
                    et_name.setText(user.getpName());//给病人姓名设值
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if(oldId!=null&&!oldId.equals("")){
                if(!oldId.equals(et_bianhao.getText().toString().trim())){
                    give_up();
                }
            }
            et_bianhao.setEnabled(false);
            et_name.setEnabled(false);//设置姓名输入框为不可操作
            et_xibaoxue.setEnabled(true);
            et_yijian.setEnabled(true);
            et_nizhen.setEnabled(true);
            et_dna.setEnabled(true);
            et_pinggu.setEnabled(true);
            et_quyu.setEnabled(true);
            et_zhengzhuang.setEnabled(true);
            et_yindaojin.setEnabled(true);
            et_bianjie.setEnabled(true);
            et_xueguan.setEnabled(true);
            et_dianranse.setEnabled(true);
            et_cusuan.setEnabled(true);
            et_color.setEnabled(true);
            et_handle.setEnabled(true);
        }else if(a==3){
            et_name.setEnabled(false);
            et_xibaoxue.setEnabled(false);
            et_yijian.setEnabled(false);
            et_nizhen.setEnabled(false);
            et_dna.setEnabled(false);
            et_pinggu.setEnabled(false);
            et_quyu.setEnabled(false);
            et_zhengzhuang.setEnabled(false);
            et_yindaojin.setEnabled(false);
            et_bianjie.setEnabled(false);
            et_color.setEnabled(false);
            et_cusuan.setEnabled(false);
            et_dianranse.setEnabled(false);
            et_xueguan.setEnabled(false);
            et_handle.setEnabled(false);
            et_name.setText("");
            et_bianhao.setText("");
            et_handle.setText("");
            et_yindaojin.setText("");
            et_xibaoxue.setText("");
            et_zhengzhuang.setText("");
            et_yijian.setText("");
            et_dna.setText("");
            et_nizhen.setText("");
            et_pinggu.setText("");
            et_quyu.setText("");
            et_bianjie.setText("");
            et_xueguan.setText("");
            et_cusuan.setText("");
            et_dianranse.setText("");
            et_color.setText("");
        }
    }

    private void init(View view){

        rl= (RelativeLayout) view.findViewById(R.id.rl);
        rl.requestFocus();
        bt_address= (Button) view.findViewById(R.id.bt_address);
        btn_right = (Button) view.findViewById(R.id.btn_right);//这个是菜单按钮
        btn_right.setText(getString(R.string.patient_menu));
//        btn_right.setBackgroundResource(R.drawable.caidan);
//         android.view.ViewGroup.LayoutParams cancelBtnPara = btn_right.getLayoutParams();
//        cancelBtnPara.height = 59;
//        cancelBtnPara.width=59;
//        btn_right.setLayoutParams(cancelBtnPara);
        tv_title = (TextView) view.findViewById(R.id.title_text);//这个是标题栏
        bt_baocun= (Button) view.findViewById(R.id.bt_baocun);//生成报告按钮
        rl.requestFocus();//得到焦点，防止edittext得到焦点导致小键盘出现
        bt_choise= (Button) view.findViewById(R.id.bt_choise);//选择图片
        bt_giveup= (Button) view.findViewById(R.id.bt_giveup);//清除
        et_handle= (EditText) view.findViewById(R.id.et_handle);
        et_dna= (EditText) view.findViewById(R.id.et_dna);//dna
        et_name= (EditText) view.findViewById(R.id.et_name);//姓名
        et_xibaoxue= (EditText) view.findViewById(R.id.et_xibaoxue);//细胞学
        et_zhengzhuang= (EditText) view.findViewById(R.id.et_zhengzhuang);//症状
        et_pinggu= (EditText) view.findViewById(R.id.et_pinggu);//评估
        et_yindaojin= (EditText) view.findViewById(R.id.et_yindaojin);//阴道镜
        et_quyu= (EditText) view.findViewById(R.id.et_quyu);//区域
        et_nizhen= (EditText) view.findViewById(R.id.et_nizhen);//拟诊
        et_yijian= (EditText) view.findViewById(R.id.et_yijian);//意见
        et_bianhao= (EditText) view.findViewById(R.id.et_bianhao);//编号
        bt_left= (Button) view.findViewById(R.id.btn_left);//返回
        bt_new= (Button) view.findViewById(R.id.bt_new);//选择患者
        et_bianjie= (EditText) view.findViewById(R.id.et_bianjie);
        et_color= (EditText) view.findViewById(R.id.et_color);
        et_dianranse= (EditText) view.findViewById(R.id.et_dianranse);
        et_cusuan= (EditText) view.findViewById(R.id.et_cusuan);
        et_xueguan= (EditText) view.findViewById(R.id.et_xueguan);
        bt_left.setText("");
        tv_title.setText(getString(R.string.print_baogaoshengcheng));
        bt_new.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        bt_giveup.setOnClickListener(this);
        bt_choise.setOnClickListener(this);
        bt_baocun.setOnClickListener(this);
        bt_address.setOnClickListener(this);
        et_bianjie.setFocusableInTouchMode(false);
        et_bianjie.setClickable(true);
        et_color.setFocusableInTouchMode(false);
        et_color.setClickable(true);
        et_dianranse.setFocusableInTouchMode(false);
        et_dianranse.setClickable(true);
        et_cusuan.setFocusableInTouchMode(false);
        et_cusuan.setClickable(true);
        et_xueguan.setFocusableInTouchMode(false);
        et_xueguan.setClickable(true);
        et_xibaoxue.setFocusableInTouchMode(false);
        et_xibaoxue.setClickable(true);
        et_dna.setFocusableInTouchMode(false);
        et_dna.setClickable(true);
        et_zhengzhuang.setFocusableInTouchMode(false);
        et_zhengzhuang.setClickable(true);
        et_pinggu.setFocusableInTouchMode(false);
        et_pinggu.setClickable(true);
        et_quyu.setFocusableInTouchMode(false);
        et_quyu.setClickable(true);
        et_yindaojin.setFocusableInTouchMode(false);
        et_yindaojin.setClickable(true);
        et_nizhen.setFocusableInTouchMode(false);
        et_nizhen.setClickable(true);
        et_yijian.setFocusableInTouchMode(false);
        et_yijian.setClickable(true);
        et_handle.setFocusableInTouchMode(false);
        et_handle.setClickable(true);
//        wifiAutoConnectManager = new WifiAutoConnectManager(mWifiManager);
        dialog = new AlertDialogDao(getActivity());
        hpPrint = new HPprinter(getActivity());
        isChina = MyApplication.getInstance().getCountry().endsWith("CN");
    }
    private void initClick(){
        et_xibaoxue.setOnClickListener(this);
        et_dna.setOnClickListener(this);
        et_zhengzhuang.setOnClickListener(this);
        et_pinggu.setOnClickListener(this);
        et_quyu.setOnClickListener(this);
        et_yindaojin.setOnClickListener(this);
        et_nizhen.setOnClickListener(this);
        et_yijian.setOnClickListener(this);
        et_bianjie.setOnClickListener(this);
        et_color.setOnClickListener(this);
        et_xueguan.setOnClickListener(this);
        et_dianranse.setOnClickListener(this);
        et_cusuan.setOnClickListener(this);
        et_handle.setOnClickListener(this);
        loadingDialog = new LoadingDialog(getContext());
//        PDFCreate.setPdfCreateInterfaceListener(this);
//        import android.speech.srec.Recognizer
//           Recogniz
    }

    //将TextView 中的文字两边对齐
    private void initText(View view) {
        tvName = new String[]{getContext().getString(R.string.print_patient_id), getContext().getString(R.string.print_diagnosis_result), getContext().getString(R.string.patient_name), getContext().getString(R.string.print_cytology), getContext().getString(R.string.print_HPV_DNA),getContext().getString(R.string.print_symptom),
                getContext().getString(R.string.print_Overall_assessment), getContext().getString(R.string.print_Lesion_area), getContext().getString(R.string.print_colposcopic), getContext().getString(R.string.print_Suspected), getContext().getString(R.string.print_attention),getContext().getString(R.string.print_opinion),
                getContext().getString(R.string.print_Assessment_results), getContext().getString(R.string.print_border),  getContext().getString(R.string.print_color), getContext().getString(R.string.print_blood_vessel),
                getContext().getString(R.string.print_Iodine_staining), getContext().getString(R.string.print_Acetic_acid_change)};
        tv01 = (TextView) view.findViewById(R.id.tv_print_01);
        tv02 = (TextView) view.findViewById(R.id.tv_print_02);
        TextPaint tp = tv02.getPaint();
        tp.setFakeBoldText(true);
        tv03 = (TextView) view.findViewById(R.id.tv_print_03);
        tv04 = (TextView) view.findViewById(R.id.tv_print_04);
        tv05 = (TextView) view.findViewById(R.id.tv_print_05);
        tv06 = (TextView) view.findViewById(R.id.tv_print_06);
        tv07 = (TextView) view.findViewById(R.id.tv_print_07);
        tv08 = (TextView) view.findViewById(R.id.tv_print_08);
        tv09 = (TextView) view.findViewById(R.id.tv_print_09);
        tv10 = (TextView) view.findViewById(R.id.tv_print_10);
        tv11 = (TextView) view.findViewById(R.id.tv_print_11);
        tv12 = (TextView) view.findViewById(R.id.tv_print_12);

        tv13 = (TextView) view.findViewById(R.id.tv_print_13);
        TextPaint tp1 = tv13.getPaint();
        tp1.setFakeBoldText(true);
        tv14 = (TextView) view.findViewById(R.id.tv_print_14);
        tv15 = (TextView) view.findViewById(R.id.tv_print_15);
        tv16 = (TextView) view.findViewById(R.id.tv_print_16);
        tv17 = (TextView) view.findViewById(R.id.tv_print_17);
        tv18 = (TextView) view.findViewById(R.id.tv_print_18);//处理意见，这个是后来才加的

        TextView[] tvData = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17,tv18};
//        if (screenInches > 6.0) {
        for (int i=0;i<tvName.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tvName[i], 4));
            }
        }
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_right://菜单按钮
                getPopupMenu();
                break;
            case R.id.bt_baocun://保存选择生成pdf文件
                if (!getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString().trim()) && et_bianhao.getText().toString().trim() != null && !"".equals(et_bianhao.getText().toString().trim())) {
                    createPDFDialog();

                } else {
                    Toast.makeText(getActivity(),getContext().getString(R.string.print_id_null), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_address:
                if(!et_bianhao.getText().toString().equals(getContext().getString(R.string.patient_id))&&et_bianhao.getText().toString()!=getContext().getString(R.string.patient_id)&&!et_bianhao.getText().toString().equals("")&&et_bianhao.getText().toString()!=null){
                    // 图片路径
                    User user=new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));

                    String mImagePath = Environment.getExternalStorageDirectory()+"/AFLY/fwt.png";
                    String mSavePath=user.getGatherPath()+"/";
//                    Toast.makeText(getContext(), "保存路径："+mSavePath, Toast.LENGTH_SHORT).show();
                    GraffitiActivity.startActivityForResult(getActivity(), mImagePath,mSavePath, true,REQ_CODE_GRAFFITI);
                }else {
                    Toast.makeText(getContext(), getContext().getString(R.string.print_patient_select), Toast.LENGTH_SHORT).show();

                    select(0);
                }
                break;
            case R.id.bt_choise://选择照片

                openImage();//选择图片
                break;
            case R.id.bt_new://选择用户
                num=0;
                select(0);

                break;
            case R.id.bt_giveup://清除数据
                dialogViewDelete();
//                give_up();
                break;
            case R.id.et_xibaoxue:
                showTerm(1);
                break;
            case R.id.et_dna:
                showTerm(2);
                break;
            case R.id.et_zhengzhuang:
                showTerm(3);
                break;
            case R.id.et_pinggu:
                showTerm(4);
                break;
            case R.id.et_quyu:
                showTerm(5);
                break;
            case R.id.et_yindaojin:
                showTerm(6);
                break;
            case R.id.et_nizhen:
                showTerm(7);
                break;
            case R.id.et_yijian:
                showTerm(8);
                break;
            case R.id.et_color:
                showTerm(9);
                break;
            case R.id.et_xueguan:
                showTerm(10);
                break;
            case R.id.et_dianranse:
                showTerm(11);
                break;
            case R.id.et_cusuan:
                showTerm(12);
                break;
            case R.id.et_bianjie:
                showTerm(13);
                break;
            case R.id.et_handle:
                showTerm(14);
                break;
            default:break;
        }
    }
    //-------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GRAFFITI) {
            if (data == null) {
                return;
            }
            if (resultCode == GraffitiActivity.RESULT_OK) {
                String path = data.getStringExtra(GraffitiActivity.KEY_IMAGE_PATH);
                if (TextUtils.isEmpty(path)) {
                    return;
                }
//            Toast.makeText(getContext(), "路径："+path, Toast.LENGTH_SHORT).show();
            } else if (resultCode == GraffitiActivity.RESULT_ERROR) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //-------------------------------------------------------------
    /**
     * 获取打印机状态
     * @return
     */
    public void getPosStatus() {
        if(printJob!=null){
            xh_pDialog=new ProgressDialog(getContext());
            xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            xh_pDialog.setTitle(getString(R.string.print_dialog_title));
            xh_pDialog.setCancelable(false);
            xh_pDialog.setIndeterminate(true);
            xh_pDialog.setMessage(getString(R.string.print_dialog_message));
            xh_pDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (b != 1) {
                        Message mess = handler.obtainMessage();
                        b = new PrintMetricsCollector().returnPrintResult(printJob);
                        if (b == 1) {//打印完成
                            mess.what = 1;
                            PItem.getOneItem().setPrintJob(null);
                            printJob=null;
                        } else if (b == 0) {//开始打印
                            mess.what = 0;
                        } else if (b == 2) {//等待打印
                            mess.what = 3;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(mess);
                    }
                }
            }).start();

        }
    }
    //点击保存时对填写的诊断信息完成程度进行判断
    private void initPanduan() {
        final List<Doctor>doctorList=DataSupport.where("dName=?","Admin").find(Doctor.class);//只有超级用户才可以设置医院，查询Admin表中的数据
        if(!getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString().trim())&&et_bianhao.getText().toString().trim()!=null&&!"".equals(et_bianhao.getText().toString().trim())){
            user = new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));//根据pId得到对应的User
            if (user.getHpv_dna() == null || user.getAdvice() == null || OneItem.getOneItem().getList() == null) {
                Toast.makeText(getContext(), getContext().getString(R.string.print_message_supplement), Toast.LENGTH_SHORT).show();
            } else {
                for(int i=0;i<doctorList.size();i++){
                    if(!doctorList.get(0).getEdit_hos_name().equals("")&&!doctorList.get(0).getEdit_hos_keshi().equals("")){//判断医院名称是否为空
                        SystemSet s=new LoginRegister().getSystem();//新建数据库记录
                        OneItem.getOneItem().setHospital_name(s.getHospital_name());//添加医院名称记录
                        OneItem.getOneItem().setGather_path(s.getGather_path());//添加采集图片保存路径
                        user.setbResult(true);//设置为true时代表已经生成报告
                        user.save();//保存数据
                        initCreatePDF(user);//生成pdf文件
                        new Backup(getContext()).initBackups();
                    }else {
                        Doctor doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
                        if(doctor.isdAdmin()){
                            if(doctorList.get(0).getEdit_hos_name().equals("")){
                                a=3;
                                Toast.makeText(getContext(), getContext().getString(R.string.print_hospital_setting), Toast.LENGTH_SHORT).show();
                                new FragSetting().HosName(getContext());
                            }else if(doctorList.get(0).getEdit_hos_keshi().equals("")){
                                a=3;
                                Toast.makeText(getContext(),getContext().getString(R.string.print_department_setting), Toast.LENGTH_SHORT).show();
                                new FragSetting().Departments(getContext());
                            }
                        }else {
                            loadingDialog.dismiss();
                            user.setIs_diag(1);
                            Toast.makeText(getContext(),getContext().getString(R.string.print_setting_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    public void initCreatePDF(final User u){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new PDFCreate(getContext()).initView(u,1);//点击生成pdf文件
                        OneItem.getOneItem().setD(1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到文件大小
     */
    public  long getFileSize(File file){
        long size =0;
        if(file.exists()){
            FileInputStream fis=null;
            try {
                fis=new FileInputStream(file);
                size=fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }
    //清除输入框内容重新输入
    private void give_up(){
        et_yindaojin.setText("");
        et_xibaoxue.setText("");
        et_zhengzhuang.setText("");
        et_yijian.setText("");
        et_dna.setText("");
        et_nizhen.setText("");
        et_pinggu.setText("");
        et_quyu.setText("");
        et_bianjie.setText("");
        et_cusuan.setText("");
        et_dianranse.setText("");
        et_xueguan.setText("");
        et_color.setText("");
        et_handle.setText("");
        et_xibaoxue.setFocusable(true);//获取焦点

    }
    //PopupMenu 菜单
    private void getPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(getActivity(),btn_right);
        popupMenu.getMenuInflater().inflate(R.menu.printer,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=null;
                switch (item.getItemId()){
                    case R.id.printer01://报告打印
                        isPkgInstalled(Const.PageHp);
//                        Logger.e("Const.PageHp = "+Const.PageHp);
                        break;
                    case R.id.printer02://b报告预览
                        intent=new Intent(getContext(), XuanzePDFActivity.class);//跳转到本地pdf展示页面
                        startActivity(intent);
                        break;
                    case R.id.printer03://报告修改
                        intent=new Intent(getContext(), DengjiYulanActivity.class);//跳转到诊断信息预览
                        if (doctor != null) {
                            intent.putExtra("msg", doctor.isdAdmin());//判断是否由超级用户跳转过去,false不是
                        } else {
                            intent.putExtra("msg", false);//判断是否由超级用户跳转过去,false不是
                        }

                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        popupMenu.show();
    }

    //跳转到照片展示页面
    private void openImage(){
        Intent intent=new Intent(getContext(),ImageActivity.class);
        intent.putExtra("msg",et_bianhao.getText().toString());
        intent.putExtra("msgName",et_name.getText().toString());
        startActivity(intent);
    }
    private void initAddImage(){//当病人采集图片为空时，自动添加空白图片
        for(int i=0;i<3;i++){
            mResults.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/kb.png");
            OneItem.getOneItem().setList(mResults);
        }
    }
    public void showList(){//添加默认图片
        showList=new ArrayList<>();
        OneItem.getOneItem().getList().clear();
        if (new Item().getSD() != null) {
            File[] files = new File(new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim())).getGatherPath()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//所有.jpg格式的文件添加到数组中
                        a = true;
                    }
                    return a;
                }
            });
            //遍历数组动态添加图片到对象中去
            for (File file : files) {
                if(!file.getName().equals("方位.jpg")&&file.getName()!="方位.jpg"){
                    showList.add((new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim())).getGatherPath()+"/" + file.getName()));
                }
            }
            if(showList.size()>0 && showList.size()<=2){
                for(int i=0;i<showList.size();i++){
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            }else if(showList.size()==0){
                for(int j=0;j<3;j++){
                    OneItem.getOneItem().getList().add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/kb.png");
                }
            }else {
                for(int i=0;i<3;i++){
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            }
            if(et_bianhao.getText().toString()!=null&&!getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString())&&!"".equals(et_bianhao.getText().toString().trim())){
                user=new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));
                user.setImgPath(OneItem.getOneItem().getList());
                user.save();
            }
        }
    }


    //修改数据库
    private void initBaocun(){
//        if (isChina) {
             formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss", Locale.getDefault());//系统当前时间
//        } else {
//             formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss 'CST' yyyy", Locale.ENGLISH);
//        }

        Date curDate =  new Date();
        user.setAdvice(et_yijian.getText().toString());//
        user.setHpv_dna(et_dna.getText().toString());//dna
        user.setSymptom(et_zhengzhuang.getText().toString());//症状
        user.setSummary(et_pinggu.getText().toString());//评估
        user.setBingbian(et_quyu.getText().toString());//区域
        user.setYindaojin(et_yindaojin.getText().toString());//阴道镜
        user.setNizhen(et_nizhen.getText().toString());//拟诊
        user.setAdvice(et_xibaoxue.getText().toString());//细胞学
        user.setZhuyishixiang(et_yijian.getText().toString());//注意事项
        user.setCheckDate(formatter.format(curDate));//检查时间
        user.setIs_diag(2);//是否添加诊断信息
        user.setBianjie(et_bianjie.getText().toString().trim());//边界
        user.setColor(et_color.getText().toString().trim());//颜色
        user.setXueguna(et_xueguan.getText().toString().trim());//血管
        user.setDianranse(et_dianranse.getText().toString().trim());//碘染色
        user.setCusuan(et_cusuan.getText().toString().trim());//醋酸
        user.setPdfPath(user.getGatherPath()+"/"+(user.getpId() +"_"+ user.getpName() + ".pdf"));
        user.setHandle(et_handle.getText().toString().trim());
        user.save();//保存所有改变
        OneItem.getOneItem().setC(false);//如果为true,诊断信息可以添加，如果为false,诊断信息不可以添加（设置输入框的状态）
        Const.pdfPath = user.getGatherPath() + "/" + (user.getpId() + "_" + user.getpName() + ".pdf");
        //点击保存时对填写的诊断信息完成程度进行判断
        initPanduan();
    }

    //    List<User> userlist = new ArrayList<User>();
    private void isPkgInstalled(final String pkgName) {
        PackageInfo packageInfo = null;//管理已安装app包名
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null)  {//判断是否安装惠普打印机服务插件
            String path = Environment.getExternalStorageDirectory() + "/hp.apk";//打印服务插件本地路径
            try {
                File file = new File(path);
                if (!file.exists()) {
                    copyAPK2SD(path);//将项目中的服务插件复制到本地路径下
                }
                installApk(getActivity(),path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Const.pdfPath == null || Const.pdfPath.equals("")) {
                Toast.makeText(getActivity(), getContext().getString(R.string.print_report_select), Toast.LENGTH_SHORT).show();
                return;
            }else{
                File filePdf = new File(Const.pdfPath);
                if (getFileSize(filePdf) != 0) {//判断文件的大小（间接判断该pdf文件是否是正常的）
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(getString(R.string.print_report_dialog_title));

                    dialog.setMessage(getString(R.string.print_report_dialog_message));
                    dialog.setPositiveButton(getString(R.string.print_report_dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finish();
                            hpPrint.continueButtonPrint(Const.pdfPath);
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.print_report_dialog_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog1 = dialog.create();
                    dialog1.setCanceledOnTouchOutside(false);
                    dialog1.show();
                }
            }
        }
    }

    //安装apk
    private  void installApk(Context context, String fileApk) {
        //    通过隐式意图调用系统安装程序安装APK
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Log.e(TAG, "downloadFileUri: "+downloadFileUri);
        if (fileApk != null) {
            File file = new File(fileApk);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri apkUri =
                        FileProvider.getUriForFile(context,context.getPackageName()+ ".fileprovider", file);
//                content://com.qcam.fileprovider/external_files/Download/update.apk
                Log.e(TAG, "android 7.0 : apkUri "+apkUri );
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            } else {
//                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                //Uri.fromFile(file) : file:///storage/emulated/0/Download/update.apk
                Log.e(TAG, "Uri.fromFile(file) : "+Uri.fromFile(file) );

                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.print_download_faild), Toast.LENGTH_SHORT).show();
        }
    }

    //模拟点击事件
    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }


    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    private void copyAPK2SD(String strOutFileName) throws IOException {
        LogUtil.createDipPath(strOutFileName);
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = getContext().getAssets().open("hp.apk");
            myOutput = new FileOutputStream(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            myOutput.flush();
            myInput.close();
            myOutput.close();
        }
    }
    private void select(int yeshu){
        list=new ArrayList<>();
        list.clear();
        userlist.clear();
        userList.clear();
        try {
            Doctor doc=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
            if(doc.isdAdmin()){
                userList=DataSupport.findAll(User.class);
            }else {
                userList= DataSupport.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
            }
        }catch (Exception e){

        }
        if (userList.size() > 0) {//这个判断是为了保证程序的健壮性，以防出现 userList.size=0 的情况
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getIs_diag() == 1) {
                    userlist.add(userList.get(i));
                }
            }
            Const.list=userlist;
            lv_list(yeshu);
        }
    }

    public void lv_list(int yeshu){
        list.clear();
        double userSize=userlist.size();
        temp = userSize / 5.0;
        usersize=userlist.size();
        if (temp-yeshu >=1||temp-yeshu==temp) {
            if(temp>=1){
                for (int j = yeshu * 5; j < yeshu * 5 +5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list.add(item);
                }
            }else {
                for (int j = yeshu * 5; j < userlist.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list.add(item);
                }
            }
        }else if(temp-yeshu<1){
            for (int j = yeshu * 5; j < userlist.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name)+":" + userlist.get(j).getpName() + "  " +getString(R.string.patient_age)+ ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone)+":" + userlist.get(j).getTel();
                item.setpId(userlist.get(j).getpId());
                item.setpName(zhanshi);
                list.add(item);
            }
        }
        if (list.size() >= 1) {
            lv_add(list);
        } else if (list.size() == 0) {
            Toast.makeText(getActivity(),getContext().getString(R.string.print_patient_nothing), Toast.LENGTH_SHORT).show();
        }
    }
    private void lv_add(final List<Item>list){

        listView=new ListView(getContext());
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        listView.setFadingEdgeLength(0);
        listView.setFocusableInTouchMode(true);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        SelectAdapter selectAdapter=new SelectAdapter(getContext(),list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview

        select_dialog=new AlertDialog.Builder(getContext()).setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain).setNeutralButton(R.string.image_cancel,null).setPositiveButton(R.string.image_next_page,null).setNegativeButton(R.string.image_Previous_page,null).create();
        select_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button bt_cancle=select_dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before=select_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next=select_dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select_dialog.dismiss();
                        dialogset=0;
                        num=0;
                    }
                });
                bt_before.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(temp-num!=temp){
                            if(num>0){
                                num--;
                            }
                            select_dialog.dismiss();
                            dialogset=1;
                            lv_list(num);
                        }else {
                            Toast.makeText(getContext(), getContext().getString(R.string.image_patients_first), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if(temp>num){
                            select_dialog.dismiss();
                            dialogset=1;
                            lv_list(num);
                        }else {
                            num--;
                            Toast.makeText(getContext(),  getContext().getString(R.string.image_patients_last), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        select_dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击

        select_dialog.show();

        OneItem.getOneItem().setDialog(select_dialog);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                c=arg2;
                select_dialog.cancel();
                a=2;
                num=0;
                onResume();
            }
        });
        if(usersize==1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message=handler.obtainMessage();
                    message.what=-1;
                    int temp=0;
                    while (temp<Const.delayTime){
                        try {
                            Thread.sleep(1000);
                            temp++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(message);
                }
            }).start();
        }
    }
    private void showTerm(int temp){
        diaList=new ArrayList<>();
        diacrisisList=DataSupport.findAll(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            switch (temp){
                case 1:
                    if(diacrisisList.get(i).getCytology()!=null&&!"".equals(diacrisisList.get(i).getCytology())){
                        diaList.add(diacrisisList.get(i).getCytology());
                    }
                    break;
                case 2:
                    if(diacrisisList.get(i).getDna()!=null&&!"".equals(diacrisisList.get(i).getDna())){
                        diaList.add(diacrisisList.get(i).getDna());
                    }
                    break;
                case 3:
                    if(diacrisisList.get(i).getSymptom()!=null&&!"".equals(diacrisisList.get(i).getSymptom())){
                        diaList.add(diacrisisList.get(i).getSymptom());
                    }
                    break;
                case 4:
                    if(diacrisisList.get(i).getAssessment()!=null&&!"".equals(diacrisisList.get(i).getAssessment())){
                        diaList.add(diacrisisList.get(i).getAssessment());
                    }
                    break;
                case 5:
                    if(diacrisisList.get(i).getRegion()!=null&&!"".equals(diacrisisList.get(i).getRegion())){
                        diaList.add(diacrisisList.get(i).getRegion());
                    }
                    break;
                case 6:
                    if(diacrisisList.get(i).getColposcopy()!=null&&!"".equals(diacrisisList.get(i).getColposcopy())){
                        diaList.add(diacrisisList.get(i).getColposcopy());
                    }

                    break;
                case 7:
                    if(diacrisisList.get(i).getSuspected()!=null&&!"".equals(diacrisisList.get(i).getSuspected())){
                        diaList.add(diacrisisList.get(i).getSuspected());
                    }
                    break;
                case 8:
                    if(diacrisisList.get(i).getAttention()!=null&&!"".equals(diacrisisList.get(i).getAttention())){
                        diaList.add(diacrisisList.get(i).getAttention());
                    }
                    break;
                case 9:
                    if(diacrisisList.get(i).getColor()!=null&&!"".equals(diacrisisList.get(i).getColor())){
                        diaList.add(diacrisisList.get(i).getColor());
                    }
                    break;
                case 10:
                    if(diacrisisList.get(i).getXueguna()!=null&&!"".equals(diacrisisList.get(i).getXueguna())){
                        diaList.add(diacrisisList.get(i).getXueguna());
                    }
                    break;
                case 11:
                    if(diacrisisList.get(i).getDianranse()!=null&&!"".equals(diacrisisList.get(i).getDianranse())){
                        diaList.add(diacrisisList.get(i).getDianranse());
                    }
                    break;
                case 12:
                    if(diacrisisList.get(i).getCusuan()!=null&&!"".equals(diacrisisList.get(i).getCusuan())){
                        diaList.add(diacrisisList.get(i).getCusuan());
                    }
                    break;
                case 13:
                    if(diacrisisList.get(i).getBianjie()!=null&&!"".equals(diacrisisList.get(i).getBianjie())){
                        diaList.add(diacrisisList.get(i).getBianjie());
                    }
                    break;
                case 14:
                    if(diacrisisList.get(i).getHandle()!=null&&!"".equals(diacrisisList.get(i).getHandle())){
                        diaList.add(diacrisisList.get(i).getHandle());
                    }
                default:break;
            }
        }
        lv_addDia(diaList,temp);
    }

    private void lv_addDia(final List<String>list, final int temp){

        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        View view_self=LayoutInflater.from(getContext()).inflate(R.layout.simple_list,null);
        linearLayoutMain.addView(view_self);
        view_self.setFadingEdgeLength(0);
        view_self.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView_self= (ListView) view_self.findViewById(R.id.lv_show);
        edit_self= (EditText) view_self.findViewById(R.id.et_show);
        ArrayAdapter selectAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
        listView_self.setAdapter(selectAdapter);
        new Item().setListViewHeightBasedOnChildren(listView_self);//重新计算listview每个item的高度
//        lv_select.setEmptyView(tv_empty);//当listview 没有数据时，就显示这个view
//        final ListView listView = new ListView(getContext());//this为获取当前的上下文
//        listView.setFadingEdgeLength(0);
//        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        ArrayAdapter selectAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
//        listView.setAdapter(selectAdapter);
//        linearLayoutMain.addView(listView);//往这个布局中加入listview
//        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
//        final EditText edit = new EditText(getContext());
        switch (temp){//给editText初始化值
            case 1:
                edit_self.setText(et_xibaoxue.getText().toString().trim());
                break;
            case 2:
                edit_self.setText(et_dna.getText().toString().trim());
                break;
            case 3:
                edit_self.setText(et_zhengzhuang.getText().toString().trim());
                break;
            case 4:
                edit_self.setText(et_pinggu.getText().toString().trim());
                break;
            case 5:
                edit_self.setText(et_quyu.getText().toString().trim());
                break;
            case 6:
                edit_self.setText(et_yindaojin.getText().toString().trim());
                break;
            case 7:
                edit_self.setText(et_nizhen.getText().toString().trim());
                break;
            case 8:
                edit_self.setText(et_yijian.getText().toString().trim());
                break;
            case 9:
                edit_self.setText(et_color.getText().toString().trim());
                break;
            case 10:
                edit_self.setText(et_xueguan.getText().toString().trim());
                break;
            case 11:
                edit_self.setText(et_dianranse.getText().toString().trim());
                break;
            case 12:
                edit_self.setText(et_cusuan.getText().toString().trim());
                break;
            case 13:
                edit_self.setText(et_bianjie.getText().toString().trim());
                break;
            case 14:
                edit_self.setText(et_handle.getText().toString().trim());
                break;
        }

//        linearLayoutMain.addView(edit);
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.print_Choosing_terms)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (temp){//将edit的值赋给各个选项的输入框
                            case 1:
                                et_xibaoxue.setText(edit_self.getText().toString());
                                break;
                            case 2:
                                et_dna.setText(edit_self.getText().toString());
                                break;
                            case 3:
                                et_zhengzhuang.setText(edit_self.getText().toString());
                                break;
                            case 4:
                                et_pinggu.setText(edit_self.getText().toString());
                                break;
                            case 5:
                                et_quyu.setText(edit_self.getText().toString());
                                break;
                            case 6:
                                et_yindaojin.setText(edit_self.getText().toString());
                                break;
                            case 7:
                                et_nizhen.setText(edit_self.getText().toString());
                                break;
                            case 8:
                                et_yijian.setText(edit_self.getText().toString());
                                break;
                            case 9:
                                et_color.setText(edit_self.getText().toString());
                                break;
                            case 10:
                                et_xueguan.setText(edit_self.getText().toString());
                                break;
                            case 11:
                                et_dianranse.setText(edit_self.getText().toString());
                                break;
                            case 12:
                                et_cusuan.setText(edit_self.getText().toString());
                                break;
                            case 13:
                                et_bianjie.setText(edit_self.getText().toString());
                                break;
                            case 14:
                                et_handle.setText(edit_self.getText().toString());
                                break;
                        }
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
//        listView.setFocusable(false);
        listView_self.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (temp){//根据点击给edit赋值
                    case 1:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 2:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 3:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 4:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 5:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 6:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 7:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 8:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 9:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 10:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 11:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 12:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 13:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 14:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                }
                onResume();
            }
        });

        listView_self.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Diacrisis>diacrisisList;
                int diaId = 0;
                switch (temp){
                    case 1:
                        diacrisisList=DataSupport.where("cytology=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,1,list,i, listView_self,edit_self);
                        break;
                    case 2:
                        diacrisisList=DataSupport.where("dna=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,2,list,i, listView_self,edit_self);
                        break;
                    case 3:
                        diacrisisList=DataSupport.where("Symptom=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,3,list,i, listView_self,edit_self);
                        break;
                    case 4:
                        diacrisisList=DataSupport.where("Assessment=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,4,list,i, listView_self,edit_self);
                        break;
                    case 5:
                        diacrisisList=DataSupport.where("region=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,5,list,i, listView_self,edit_self);
                        break;
                    case 6:
                        diacrisisList=DataSupport.where("Colposcopy=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,6,list,i, listView_self,edit_self);
                        break;
                    case 7:
                        diacrisisList=DataSupport.where("Suspected=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,7,list,i, listView_self,edit_self);
                        break;
                    case 8:
                        diacrisisList=DataSupport.where("attention=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,8,list,i, listView_self,edit_self);
                        break;
                    case 9:
                        diacrisisList=DataSupport.where("color=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,9,list,i, listView_self,edit_self);
                        break;
                    case 10:
                        diacrisisList=DataSupport.where("xueguna=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,10,list,i, listView_self,edit_self);
                        break;
                    case 11:
                        diacrisisList=DataSupport.where("dianranse=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,11,list,i, listView_self,edit_self);
                        break;
                    case 12:
                        diacrisisList=DataSupport.where("cusuan=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,12,list,i, listView_self,edit_self);
                        break;
                    case 13:
                        diacrisisList=DataSupport.where("bianjie=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,13,list,i, listView_self,edit_self);
                        break;
                    case 14:
                        diacrisisList=DataSupport.where("handle=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,14,list,i, listView_self,edit_self);
                        break;
                }
                return false;
            }
        });
    }

    //诊断信息 长按删除
    private void longDeleteDis( EditText edit,List<String>list,int m,ListView listView) {
        dia.save();
        edit.setText("");
        list.remove(m);
        ArrayAdapter selectAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
        selectAdapter.notifyDataSetChanged();
        listView.setAdapter(selectAdapter);
    }

    //长按就显示出dialog，然后可以选择删除诊断的相关信息
    private void initId(int diaId, final int temp, final List<String>list, final int m, final ListView listView,final EditText edit){
        List<Diacrisis>diacrisisList=DataSupport.where("diaId=?",diaId+"").find(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            dia=diacrisisList.get(0);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.print_terms_delete));
        builder.setTitle(getString(R.string.print_terms_delete_title));
        builder.setPositiveButton(getString(R.string.image_manage_delete_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (temp){
                    case 1:
                        dia.setCytology("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 2:
                        dia.setDna("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 3:
                        dia.setSymptom("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 4:
                        dia.setAssessment("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 5:
                        dia.setRegion("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 6:
                        dia.setColposcopy("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 7:
                        dia.setSuspected("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 8:
                        dia.setAttention("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 9:
                        dia.setColor("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 10:
                        dia.setXueguna("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 11:
                        dia.setDianranse("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 12:
                        dia.setCusuan("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 13:
                        dia.setBianjie("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                    case 14:
                        dia.setHandle("");
                        longDeleteDis(edit, list, m, listView);
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.image_manage_delete_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        OneItem.getOneItem().setIntImage(1);
//        isVisible=false;
        initDele();
        if (isVisible) {
            WifiAutoConnectManager wifiAutoConnectManager1 = new WifiAutoConnectManager(mWifiManager, getActivity().getApplicationContext());
            wifiAutoConnectManager1.connect(Const.nameHP, Const.passHP, Const.typeWifi);
//            if(et_bianhao.getText().toString().trim().equals("")||et_bianhao.getText().toString().trim()==null){
            select(0);
//            }
        } else {

        }
    }

    //获取PDF的路径
    private Doctor doctor1;
    private List<User> user1;
    private List<String>userlist1=new ArrayList<>();
    private String getPathPDF() {
        doctor1=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        user1= DataSupport.where("operId=?",String.valueOf(doctor1.getdId())).find(User.class);
        for(User u:user1){
            if (u.getPdfPath() != null) {//保证程序的健壮性 ,有时候u的值会为空。必须要排除 u=null 的情况
                Log.d("TAG1", " u.getPdfPath():----- "+u.getPdfPath());
                userlist1.add(u.getPdfPath());
            }
        }
        if (userlist1.size() >= 1) {
            return userlist1.get(userlist1.size() - 1);
        }
        return null;

    }

    //pdf 生成之后 被调用
    @Override
    public void pdfSuccess(int index) {
        Logger.e("生成报告的返回值 ："+index);
        if (index == 2) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            return;
        }
        try {
            if(getFileSize(OneItem.getOneItem().getFile())!=0){
                Logger.e("报告生成成功");
                Log.e(TAG, "报告生成成功 1: " );
                a = 1;//为1是刷新输入框
                saveState = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        onResume();
                        if (saveState) {
                            isPkgInstalled(Const.PageHp);
                        }

                    }
                });

            }else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getContext().getString(R.string.print_Generate_report_faild), Toast.LENGTH_SHORT).show();
                        if(OneItem.getOneItem().getFile().exists()){
                            OneItem.getOneItem().getFile().delete();
                        }
                        user.setPdfPath("");
                        user.setIs_diag(1);
                        user.save();
                    }
                });
            }

            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            Log.e(TAG, "pdfSuccess:错误 " );
            e.printStackTrace();
        }finally {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }

                }
            });

        }

    }

    AlertDialog dialogdelete = null;
    private void dialogViewDelete() {
        if (dialogdelete == null) {
            dialogdelete = new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.print_Notice_title))
                    .setCancelable(false)
                    .setMessage(getString(R.string.patient_clear_sure))
                    .setPositiveButton(getString(R.string.patient_empty), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            give_up();//清除
                            Toast.makeText(getActivity(),getContext().getString(R.string.print_data_clear
                            ), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.patient_no_empty), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                            Logger.e("");
//                            adapter.notifyDataSetChanged();
                        }
                    })
                    .create();
        }
        dialogdelete.show();
    }


   //创建生成pdf的dialog
    private void createPDFDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.print_pdf_dialog_title));
        builder.setMessage(getString(R.string.print_pdf_dialog_message));

        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //正在生成报告
                loadingDialog.setMessage(getString(R.string.print_pdf_dialog_loding));
                loadingDialog.dialogShow();
                Logger.e("是否添加图片"+OneItem.getOneItem().getD());
                if (OneItem.getOneItem().getD() == 1) {
                    showList();//添加默认图片
                    initBaocun();//保存添加的诊断信息 并且生成PDF报告
                    new Backup(getContext(),Const.sn).initBackups();
                } else if (OneItem.getOneItem().getD() == 2) {
                    a = 1;
                    initBaocun();
                    new Backup(getContext(),Const.sn).initBackups();
                    onResume();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.image_cancel), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    public void onPause() {
        super.onPause();
//        Logger.e("onPause  FragPrinter");
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if(select_dialog!=null){
            if(select_dialog.isShowing()){
                select_dialog.dismiss();
                select_dialog=null;
            }
        }

    }
}