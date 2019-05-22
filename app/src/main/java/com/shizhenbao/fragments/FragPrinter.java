package com.shizhenbao.fragments;

import android.app.Activity;
import android.app.KeyguardManager;
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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.application.MyApplication;
import com.hp.mss.hpprint.util.PItem;
import com.hp.mss.hpprint.util.PrintMetricsCollector;
import com.itextpdf.text.DocumentException;
import com.orhanobut.logger.Logger;
import com.shizhenbao.UI.MyEditTextDialog;
import com.shizhenbao.UI.MyPrintingProgressDialog;
import com.shizhenbao.activity.DengjiYulanActivity;
import com.shizhenbao.activity.ImageActivity;
import com.shizhenbao.activity.SelectActivity;
import com.shizhenbao.activity.XuanzePDFActivity;
import com.shizhenbao.adapter.Printdialogadapter;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.bean.PrintdialogBean;
import com.shizhenbao.db.AlertDialogDao;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.draw.GraffitiActivity;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.printer.HPprinter;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.InstallApkUtils;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.PDFCreate;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiConnectManager;
import com.util.AlignedTextUtils;
import com.util.FileUtil;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static org.litepal.LitePalApplication.getContext;


/**
 * Created by dell on 2017/5/22.
 */
//isPkgInstalled
public class FragPrinter extends BaseFragment implements View.OnClickListener, PDFCreate.PdfCreateInterface, WifiConnectManager.WifiConnectListener {
    //bt_choise：选择图片    bt_baocun：生成报告   bt_new :选择患者    bt_choise：选择图片
    private Button btn_right, bt_giveup, bt_choise, bt_baocun, bt_left, bt_new, bt_address;
    private TextView tv_title;
    private EditText et_name, et_xibaoxue, et_dna, et_zhengzhuang, et_pinggu, et_quyu, et_yindaojin, et_nizhen, et_yijian, et_bianhao, et_handle;
    private EditText et_bianjie, et_color, et_xueguan, et_dianranse, et_cusuan;
    private ArrayList<String> mResults = new ArrayList<>();
    private List<Item> list;
    private List<User> userlist = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    AlertDialogDao dialog;
    Diacrisis dia = null;
    User user = null;
    Doctor doctor = null;
    private int a = 1;//判断执行哪个方法
    private int c;//设置患者的pid
    private List<String> showList;
    private List<String> diaList;
    private List<Diacrisis> diacrisisList;
    private boolean saveState = false; //表示可以跳到打印机
    int b = 0;//报告是否打印成功
    AlertDialog select_dialog = null;
    private RelativeLayout rl;
    //声明打印机的类
    private HPprinter hpPrint;
    private static String TAG = "TAG_FragPrinter";
    private LoadingDialog loadingDialog;
    private TextView tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18;
    private String[] tvName;
    PrintJob printJob;
    public static final int REQ_CODE_GRAFFITI = 101;
    public static final int REQUEST_CODE_APP_INSTALL = 102;
    ListView listView = null;
    int num = 0;//判断翻页次数
    double temp;//患者信息所占页数
    int dialogset = 1;//设置对话框是否消失
    int usersize = 0;//所有未诊断患者的数量
    String oldId = null;
    ListView listView_self = null;
    private ScrollView scrollView;
    EditText edit_self = null;
    private boolean isChina = true; //判断是否是英文的系统 true ：表示中文
    SimpleDateFormat formatter = null; //转化时间
    private boolean mState = false; //检测是否是当前页面
    private InstallApkUtils installApkUtils;//打印机安装工具
    private FileUtil fileUtil;//文件操作工具
    private BackupsUtils backupsUtils;
    private MyEditTextDialog myEditTextDialog;
    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();////用来存放CheckBox的选中状态，true为选中,false为没有选中
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.what ==4) {
                Log.e("printer5",b+"aa");
                b = 0;
                myPrintingProgressDialog.initDialogDismiss();
            } else if (msg.what == 0) {
            } else if (msg.what == 3) {
            } else if (msg.what == -1) {
                if (select_dialog.isShowing()) {
                    select_dialog.dismiss();
                    MyToast.showToast(getContext(), getContext().getString(R.string.image_dialog_automatic));
//                    Toast.makeText(getContext(), getContext().getString(R.string.image_dialog_automatic), Toast.LENGTH_SHORT).show();
                    a = 2;
                    c = 0;
                    Const.sumnumber = 0;
                    Const.surface = -1;
                    Const.stain = -1;
                    Const.vessel = -1;
                    Const.color = -1;
                    onResume();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        view = inflater.inflate(R.layout.frag_layout_printer, container, false);

        initText(view);//解决文字两端对齐的问题
        init(view);
        try {
            if (OneItem.getOneItem().getName() != null && !OneItem.getOneItem().getName().equals("")) {
                doctor = new LoginRegister().getDoctor(OneItem.getOneItem().getName());//登录用户信息查询
            } else {
                new UserManager().getExceName();
                doctor = new LoginRegister().getDoctor(OneItem.getOneItem().getName());//登录用户信息查询
            }
            setSimulateClick(view, 10, 5);
//            1：表示没有添加图片，2： 表示添加了图片
            OneItem.getOneItem().setD(1);
            initAddImage();
            initClick();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initDele() {
        List<User> userList = LitePal.where("pId=?", et_bianhao.getText().toString().trim()).find(User.class);
        if (userList.size() == 0) {
            a = 3;
            onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        List<SystemSet> systemSet = LitePal.findAll(SystemSet.class);

        for (int i = 0; i < systemSet.size(); i++) {
            String localsn = systemSet.get(0).getLocal_svn();
            Const.sn = localsn;
        }

    }

    @Override
    public void onResume() {//页面刷新
        super.onResume();
        KeyguardManager mKeyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        if (!flag) {
            PDFCreate.setPdfCreateInterfaceListener(this);
            printJob = PItem.getOneItem().getPrintJob();
            if (printJob != null) {
                getPosStatus();
            }
        }
        if (a == 1) {
            if (OneItem.getOneItem().getC()) {
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
            } else {
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
        } else if (a == 2) {
            oldId = et_bianhao.getText().toString().trim();
            try {
                if (list.size() == 0) {//如果是第一次登记，id默认为0
                    saveState = false;
                    et_bianhao.setText(getString(R.string.patient_id));
                } else {
                    user = new LoginRegister().getUserName(Integer.parseInt(list.get(c).getpId()));//查询user数据库中最后一条记录
                    saveState = true;
                    et_bianhao.setText(user.getpId());
                    et_name.setText(user.getpName());//给病人姓名设值
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (oldId != null && !oldId.equals("")) {
                if (!oldId.equals(et_bianhao.getText().toString().trim())) {
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
        } else if (a == 3) {
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

    private void init(View view) {
        myPrintingProgressDialog = new MyPrintingProgressDialog(getContext());
        myEditTextDialog = new MyEditTextDialog(getContext());
        installApkUtils = new InstallApkUtils(getContext());
        backupsUtils = new BackupsUtils(getContext());
        fileUtil = new FileUtil();
        rl = (RelativeLayout) view.findViewById(R.id.rl);
        bt_address = (Button) view.findViewById(R.id.bt_address);
        btn_right = (Button) view.findViewById(R.id.btn_right);//这个是菜单按钮
        btn_right.setText(getString(R.string.patient_menu));
        tv_title = (TextView) view.findViewById(R.id.title_text);//这个是标题栏
        bt_baocun = (Button) view.findViewById(R.id.bt_baocun);//生成报告按钮
        rl.requestFocus();//得到焦点，防止edittext得到焦点导致小键盘出现
        bt_choise = (Button) view.findViewById(R.id.bt_choise);//选择图片
        bt_giveup = (Button) view.findViewById(R.id.bt_giveup);//清除
        et_handle = (EditText) view.findViewById(R.id.et_handle);
        et_dna = (EditText) view.findViewById(R.id.et_dna);//dna
        et_name = (EditText) view.findViewById(R.id.et_name);//姓名
        et_xibaoxue = (EditText) view.findViewById(R.id.et_xibaoxue);//细胞学
        et_zhengzhuang = (EditText) view.findViewById(R.id.et_zhengzhuang);//症状
        et_pinggu = (EditText) view.findViewById(R.id.et_pinggu);//评估
        et_yindaojin = (EditText) view.findViewById(R.id.et_yindaojin);//阴道镜
        et_quyu = (EditText) view.findViewById(R.id.et_quyu);//区域
        et_nizhen = (EditText) view.findViewById(R.id.et_nizhen);//拟诊
        et_yijian = (EditText) view.findViewById(R.id.et_yijian);//意见
        et_bianhao = (EditText) view.findViewById(R.id.et_bianhao);//编号
        bt_left = (Button) view.findViewById(R.id.btn_left);//返回
        bt_new = (Button) view.findViewById(R.id.bt_new);//选择患者
        et_bianjie = (EditText) view.findViewById(R.id.et_bianjie);
        et_color = (EditText) view.findViewById(R.id.et_color);
        et_dianranse = (EditText) view.findViewById(R.id.et_dianranse);
        et_cusuan = (EditText) view.findViewById(R.id.et_cusuan);
        et_xueguan = (EditText) view.findViewById(R.id.et_xueguan);
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

        dialog = new AlertDialogDao(getActivity());
        hpPrint = new HPprinter(getActivity());
//        Const.printActivity=getActivity();
        isChina = MyApplication.getInstance().getCountry().endsWith("CN");

    }

    private void initClick() {
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

    }

    //将TextView 中的文字两边对齐
    private void initText(View view) {
        tvName = new String[]{getContext().getString(R.string.print_patient_id), getContext().getString(R.string.print_diagnosis_result), getContext().getString(R.string.patient_name), getContext().getString(R.string.print_cytology), getContext().getString(R.string.print_HPV_DNA), getContext().getString(R.string.print_symptom),
                getContext().getString(R.string.print_Overall_assessment), getContext().getString(R.string.print_Lesion_area), getContext().getString(R.string.print_colposcopic), getContext().getString(R.string.print_Suspected), getContext().getString(R.string.print_attention), getContext().getString(R.string.print_opinion),
                getContext().getString(R.string.print_Assessment_results), getContext().getString(R.string.print_border), getContext().getString(R.string.print_color), getContext().getString(R.string.print_blood_vessel),
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
        tv13.setVisibility(View.INVISIBLE);
        tv14 = (TextView) view.findViewById(R.id.tv_print_14);
        tv15 = (TextView) view.findViewById(R.id.tv_print_15);
        tv16 = (TextView) view.findViewById(R.id.tv_print_16);
        tv17 = (TextView) view.findViewById(R.id.tv_print_17);
        tv18 = (TextView) view.findViewById(R.id.tv_print_18);//处理意见，这个是后来才加的

        TextView[] tvData = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18};

        for (int i = 0; i < tvName.length; i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tvName[i], 4));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right://菜单按钮
                getPopupMenu();
                break;
            case R.id.bt_baocun://保存选择生成pdf文件
                if (!getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString().trim()) && et_bianhao.getText().toString().trim() != null && !"".equals(et_bianhao.getText().toString().trim())) {
                    createPDFDialog();

                } else {
                    MyToast.showToast(getActivity(), getString(R.string.print_id_null));
//                    Toast.makeText(getActivity(), getContext().getString(R.string.print_id_null), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_address:
                if (!getContext().getString(R.string.patient_id).trim().equals(et_bianhao.getText().toString().trim()) && !TextUtils.isEmpty(et_bianhao.getText().toString().trim())) {
                    // 图片路径
                    User user = new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));

                    String mImagePath = Environment.getExternalStorageDirectory() + "/AFLY/position.png";
                    String mSavePath = user.getGatherPath() + "/";
//                    Toast.makeText(getContext(), "保存路径："+mSavePath, Toast.LENGTH_SHORT).show();
                    GraffitiActivity.startActivityForResult(getActivity(), mImagePath, mSavePath, true, REQ_CODE_GRAFFITI);
                } else {
                    MyToast.showToast(getContext(), getContext().getString(R.string.print_patient_select));
//                    Toast.makeText(getContext(), getContext().getString(R.string.print_patient_select), Toast.LENGTH_SHORT).show();

                    select(0);
                }
                break;
            case R.id.bt_choise://选择照片

                openImage();//选择图片
                break;
            case R.id.bt_new://选择用户
                num = 0;
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
            default:
                break;
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
            } else if (resultCode == GraffitiActivity.RESULT_ERROR) {
                MyToast.showToast(getActivity(),getString(R.string.fragPrintererror));
//                SouthUtil.showToast(getActivity(), getString(R.string.fragPrintererror));
            }
        } else if (requestCode == REQUEST_CODE_APP_INSTALL) {
            if (resultCode == Activity.RESULT_OK) {
                installApkUtils.initInstallAPK(Const.hpApkName);
            }
        }
    }

    /**
     * 获取打印机状态
     *
     * @return
     */
    MyPrintingProgressDialog myPrintingProgressDialog = null;
    public void getPosStatus() {
        if (printJob != null) {
            myPrintingProgressDialog.initDialogShow(printJob);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (b != 1) {
                        Message mess = handler.obtainMessage();
                        b = new PrintMetricsCollector().returnPrintResult(printJob);
                        try {
                            if (b == 0 || b == 2) {
                                Thread.sleep(8000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (b == 1) {//打印完成
                            mess.what = 1;
                            PItem.getOneItem().setPrintJob(null);
                            printJob = null;
                        } else if (b == 0) {//开始打印
                            mess.what = 0;
                        } else if (b == 2) {//等待打印
                            mess.what = 3;
                        }else if(b==3){//打印失败
                            mess.what=4;
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
        final List<Doctor> doctorList = LitePal.where("dName=?", "Admin").find(Doctor.class);//只有超级用户才可以设置医院，查询Admin表中的数据
        if (!getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString().trim()) && et_bianhao.getText().toString().trim() != null && !"".equals(et_bianhao.getText().toString().trim())) {
            user = new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));//根据pId得到对应的User
            if (user.getHpv_dna() == null || user.getAdvice() == null || OneItem.getOneItem().getList() == null) {
                MyToast.showToast(getContext(), getContext().getString(R.string.print_message_supplement));
//                Toast.makeText(getContext(), getContext().getString(R.string.print_message_supplement), Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < doctorList.size(); i++) {
                    if (!doctorList.get(0).getEdit_hos_name().equals("") && !doctorList.get(0).getEdit_hos_keshi().equals("")) {//判断医院名称是否为空
                        SystemSet s = new LoginRegister().getSystem();//新建数据库记录
                        OneItem.getOneItem().setHospital_name(s.getHospital_name());//添加医院名称记录
                        OneItem.getOneItem().setGather_path(s.getGather_path());//添加采集图片保存路径
                        user.setbResult(true);//设置为true时代表已经生成报告
                        user.save();//保存数据
                        initCreatePDF(user);//生成pdf文件
                        backupsUtils.initBackUpUser(1);
                    } else {
                        Doctor doctor = new LoginRegister().getDoctor(OneItem.getOneItem().getName());
                        if (doctor.isdAdmin()) {
                            if(doctorList.get(0).getEdit_hos_name().equals("")){
//                                a=3;
                                MyToast.showToast(getActivity(), getString(R.string.print_hospital_setting));
//                                Toast.makeText(getContext(), getString(R.string.print_hospital_setting), Toast.LENGTH_SHORT).show();
//                                new FragSetting().HosName(getContext());
                                myEditTextDialog.editDialogShow(getString(R.string.print_hospital_setting),0);
                            }else if(doctorList.get(0).getEdit_hos_keshi().equals("")){
//                                a=3;
                                MyToast.showToast(getActivity(), getString(R.string.print_department_setting));
//                                Toast.makeText(getContext(),getString(R.string.print_department_setting), Toast.LENGTH_SHORT).show();
                                myEditTextDialog.editDialogShow(getString(R.string.print_department_setting),1);
                            }
                        }else {
                            loadingDialog.dismiss();
//                            user.setIs_diag(1);
                            MyToast.showToast(getActivity(), getString(R.string.print_setting_message));
//                            Toast.makeText(getContext(),getString(R.string.print_setting_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    public void initCreatePDF(final User u) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new PDFCreate(getContext()).initView(u, 1);//点击生成pdf文件
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

    //清除输入框内容重新输入
    private void give_up() {
        Const.sumnumber = 0;
        Const.surface = -1;
        Const.stain = -1;
        Const.vessel = -1;
        Const.color = -1;
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
    private void getPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), btn_right);
        popupMenu.getMenuInflater().inflate(R.menu.printer, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.printer01://报告打印
                        isPkgInstalled(Const.PageHp);
//                        Logger.e("Const.PageHp = "+Const.PageHp);
                        break;
                    case R.id.printer02://b报告预览
                        intent = new Intent(getContext(), XuanzePDFActivity.class);//跳转到本地pdf展示页面
                        startActivity(intent);
                        break;
                    case R.id.printer03://报告修改
                        intent = new Intent(getContext(), DengjiYulanActivity.class);//跳转到诊断信息预览
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
    private void openImage() {
        Intent intent = new Intent(getContext(), ImageActivity.class);
        intent.putExtra("msg", et_bianhao.getText().toString());
        intent.putExtra("msgName", et_name.getText().toString());
        startActivity(intent);
    }

    private void initAddImage() {//当病人采集图片为空时，自动添加空白图片
        for (int i = 0; i < 3; i++) {
            mResults.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
            OneItem.getOneItem().setList(mResults);
        }
    }

    public void showList() {//添加默认图片
        showList = new ArrayList<>();
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
                if (!file.getName().equals("方位.jpg") && file.getName() != "方位.jpg") {
                    showList.add((new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim())).getGatherPath() + "/" + file.getName()));
                }
            }
            if (showList.size() > 0 && showList.size() <= 2) {
                for (int i = 0; i < showList.size(); i++) {
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            } else if (showList.size() == 0) {
                for (int j = 0; j < 3; j++) {
                    OneItem.getOneItem().getList().add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            }
            if (et_bianhao.getText().toString() != null && !getContext().getString(R.string.patient_id).equals(et_bianhao.getText().toString()) && !"".equals(et_bianhao.getText().toString().trim())) {
                user = new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));
                user.setImgPath(OneItem.getOneItem().getList());
                user.save();
            }
        }
    }


    //修改数据库
    private void initBaocun() {
        if (isChina) {
            formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss", Locale.getDefault());//系统当前时间
        } else {
            formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss 'CST' yyyy", Locale.ENGLISH);
        }

        Date curDate = new Date();
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
        user.setPdfPath(user.getGatherPath() + "/" + (user.getpId() + "_" + user.getpName() + ".pdf"));
        user.setHandle(et_handle.getText().toString().trim());
        user.setSurfacenum(Const.surface);//边界评分
        user.setColornnum(Const.color);//颜色评分
        user.setVesselnum(Const.vessel);//血管评分
        user.setStainnum(Const.stain);//碘染色评分
        user.save();//保存所有改变
        OneItem.getOneItem().setC(false);//如果为true,诊断信息可以添加，如果为false,诊断信息不可以添加（设置输入框的状态）
        Const.pdfPath = user.getGatherPath() + "/" + (user.getpId() + "_" + user.getpName() + ".pdf");
        //点击保存时对填写的诊断信息完成程度进行判断
        initPanduan();
    }

    //    List<User> userlist = new ArrayList<User>();
    private void isPkgInstalled(final String pkgName) {
        PackageInfo packageInfo = null;//管理已安装app包名
        packageInfo = installApkUtils.isInstallApk(pkgName);
        if (packageInfo == null) {//判断是否安装惠普打印机服务插件

            try {
                boolean isApkExists = installApkUtils.copyAPK2SD(Const.hpApkName);
                if(isApkExists){
                    installApkUtils.installApk(getActivity(),Environment.getExternalStorageDirectory() + "/" + Const.hpApkName);
                }else {
                    MyToast.showToast(getActivity(),getString(R.string.apkinstallfaild));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Const.pdfPath == null || Const.pdfPath.equals("")) {
                MyToast.showToast(getActivity(), getString(R.string.print_report_select));
//                Toast.makeText(getActivity(), getContext().getString(R.string.print_report_select), Toast.LENGTH_SHORT).show();
                return;
            } else {
                File filePdf = new File(Const.pdfPath);
                if (fileUtil.getFileSize(filePdf) != 0) {//判断文件的大小（间接判断该pdf文件是否是正常的）
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

    private void select(int yeshu) {
        list = new ArrayList<>();
        list.clear();
        userlist.clear();
        userList.clear();
        try {
            Doctor doc = new LoginRegister().getDoctor(OneItem.getOneItem().getName());
            if (doc.isdAdmin()) {
                userList = LitePal.findAll(User.class);
            } else {
                userList = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
            }
        } catch (Exception e) {

        }
        if (userList.size() > 0) {//这个判断是为了保证程序的健壮性，以防出现 userList.size=0 的情况
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getIs_diag() == 1) {
                    userlist.add(userList.get(i));
                }
            }
            Const.list = userlist;
            lv_list(yeshu);
        }
    }

    public void lv_list(int yeshu) {
        list.clear();
        double userSize = userlist.size();
        temp = userSize / 5.0;
        usersize = userlist.size();
        if (temp - yeshu >= 1 || temp - yeshu == temp) {
            if (temp >= 1) {
                for (int j = yeshu * 5; j < yeshu * 5 + 5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list.add(item);
                }
            } else {
                for (int j = yeshu * 5; j < userlist.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list.add(item);
                }
            }
        } else if (temp - yeshu < 1) {
            for (int j = yeshu * 5; j < userlist.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                item.setpId(userlist.get(j).getpId());
                item.setpName(zhanshi);
                list.add(item);
            }
        }
        if (list.size() >= 1) {
            lv_add(list);
        } else if (list.size() == 0) {
            MyToast.showToast(getActivity(),getString(R.string.print_patient_nothing));
//            SouthUtil.showToast(getActivity(), getContext().getString(R.string.print_patient_nothing));
        }
    }

    private void lv_add(final List<Item> list) {

        listView = new ListView(getContext());
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        listView.setFadingEdgeLength(0);
        listView.setFocusableInTouchMode(true);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        SelectAdapter selectAdapter = new SelectAdapter(getContext(), list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview

        select_dialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.image_Select_patients)).setView(linearLayoutMain).setNeutralButton(R.string.image_cancel, null).setPositiveButton(R.string.image_next_page, null).setNegativeButton(R.string.image_Previous_page, null).create();
        select_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if(select_dialog == null){
                    return;
                }
                Button bt_cancle = select_dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before = select_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next = select_dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select_dialog.dismiss();
                        dialogset = 0;
                        num = 0;
                    }
                });
                bt_before.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (temp - num != temp) {
                            if (num > 0) {
                                num--;
                            }
                            select_dialog.dismiss();
                            dialogset = 1;
                            lv_list(num);
                        } else {
                            MyToast.showToast(getActivity(), getString(R.string.image_patients_first));
//                            Toast.makeText(getContext(), getContext().getString(R.string.image_patients_first), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if (temp > num) {
                            select_dialog.dismiss();
                            dialogset = 1;
                            lv_list(num);
                        } else {
                            num--;
                            MyToast.showToast(getActivity(), getString(R.string.image_patients_last));
//                            Toast.makeText(getContext(), getContext().getString(R.string.image_patients_last), Toast.LENGTH_SHORT).show();
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
                c = arg2;
                select_dialog.cancel();
                a = 2;
                num = 0;
                Const.sumnumber = 0;
                Const.surface = -1;
                Const.stain = -1;
                Const.vessel = -1;
                Const.color = -1;
                onResume();
            }
        });
        if (usersize == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = handler.obtainMessage();
                    message.what = -1;
                    int temp = 0;
                    while (temp < Const.delayTime) {
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

    private void showTerm(int temp) {
        diaList = new ArrayList<>();
        diacrisisList = LitePal.findAll(Diacrisis.class);
        for (int i = 0; i < diacrisisList.size(); i++) {
            switch (temp) {
                case 1:
                    if (diacrisisList.get(i).getCytology() != null && !"".equals(diacrisisList.get(i).getCytology())) {
                        diaList.add(diacrisisList.get(i).getCytology());
                    }
                    break;
                case 2:
                    if (diacrisisList.get(i).getDna() != null && !"".equals(diacrisisList.get(i).getDna())) {
                        diaList.add(diacrisisList.get(i).getDna());
                    }
                    break;
                case 3:
                    if (diacrisisList.get(i).getSymptom() != null && !"".equals(diacrisisList.get(i).getSymptom())) {
                        diaList.add(diacrisisList.get(i).getSymptom());
                    }
                    break;
                case 4:
                    if (diacrisisList.get(i).getAssessment() != null && !"".equals(diacrisisList.get(i).getAssessment())) {
                        diaList.add(diacrisisList.get(i).getAssessment());
                    }
                    break;
                case 5:
                    if (diacrisisList.get(i).getRegion() != null && !"".equals(diacrisisList.get(i).getRegion())) {
                        diaList.add(diacrisisList.get(i).getRegion());
                    }
                    break;
                case 6:
                    if (diacrisisList.get(i).getColposcopy() != null && !"".equals(diacrisisList.get(i).getColposcopy())) {
                        diaList.add(diacrisisList.get(i).getColposcopy());
                    }

                    break;
                case 7:
                    if (diacrisisList.get(i).getSuspected() != null && !"".equals(diacrisisList.get(i).getSuspected())) {
                        diaList.add(diacrisisList.get(i).getSuspected());
                    }
                    break;
                case 8:
                    if (diacrisisList.get(i).getAttention() != null && !"".equals(diacrisisList.get(i).getAttention())) {
                        diaList.add(diacrisisList.get(i).getAttention());
                    }
                    break;
                case 9:
                    if (diacrisisList.get(i).getColor() != null && !"".equals(diacrisisList.get(i).getColor())) {
                        diaList.add(diacrisisList.get(i).getColor());
                    }
                    break;
                case 10:
                    if (diacrisisList.get(i).getXueguna() != null && !"".equals(diacrisisList.get(i).getXueguna())) {
                        diaList.add(diacrisisList.get(i).getXueguna());
                    }
                    break;
                case 11:
                    if (diacrisisList.get(i).getDianranse() != null && !"".equals(diacrisisList.get(i).getDianranse())) {
                        diaList.add(diacrisisList.get(i).getDianranse());
                    }
                    break;
                case 12:
                    if (diacrisisList.get(i).getCusuan() != null && !"".equals(diacrisisList.get(i).getCusuan())) {
                        diaList.add(diacrisisList.get(i).getCusuan());
                    }
                    break;
                case 13:
                    if (diacrisisList.get(i).getBianjie() != null && !"".equals(diacrisisList.get(i).getBianjie())) {
                        diaList.add(diacrisisList.get(i).getBianjie());
                    }
                    break;
                case 14:
                    if (diacrisisList.get(i).getHandle() != null && !"".equals(diacrisisList.get(i).getHandle())) {
                        diaList.add(diacrisisList.get(i).getHandle());
                    }
                    break;

                default:
                    break;
            }
        }
        lv_addDia(diaList, temp);
    }

    //将之前选择的术语对应的选择显示出来
    private void showSelected(String data,List<String> listTerm,ListView listView){
        if(listTerm.size() > 0){
            for(int i = 0;i<listTerm.size();i++){
                if(data.contains(listTerm.get(i))){
                    sparseBooleanArray.put(i,true);
                    listView.setItemChecked(i,true);
                }
            }
        }
    }

    private Printdialogadapter printdialogadapter;
    private void lv_addDia(final List<String> listTerm, final int temp) {

        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        View view_self = LayoutInflater.from(getContext()).inflate(R.layout.simple_list, null);
        linearLayoutMain.addView(view_self);
        view_self.setFadingEdgeLength(0);
        view_self.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView_self = (ListView) view_self.findViewById(R.id.lv_show);
        scrollView = view_self.findViewById(R.id.sv);
        listView_self.requestFocus();
        listView_self.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        edit_self = (EditText) view_self.findViewById(R.id.et_show);
        edit_self.clearFocus();
        scrollView.requestFocus();
        printdialogadapter = new Printdialogadapter(getContext(),listTerm);
//        ArrayAdapter selectAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, list);
        listView_self.setAdapter(printdialogadapter);
        listView_self.setSelection(ListView.FOCUS_DOWN);
        new Item().setListViewHeightBasedOnChildren(listView_self);//重新计算listview每个item的高度
        switch (temp) {//给editText初始化值
            case 1:
                String cytology = et_xibaoxue.getText().toString().trim();
                edit_self.setText(cytology);
                showSelected(cytology,listTerm,listView_self);
                break;
            case 2:
                String dna = et_dna.getText().toString().trim();
                edit_self.setText(dna);
                showSelected(dna,listTerm,listView_self);
                break;
            case 3:
                String diagnosis = et_zhengzhuang.getText().toString().trim();
                edit_self.setText(diagnosis);
                showSelected(diagnosis,listTerm,listView_self);
                break;
            case 4:
                String assessment = et_pinggu.getText().toString().trim();
                edit_self.setText(assessment);
                showSelected(assessment,listTerm,listView_self);
                break;
            case 5:
                String region = et_quyu.getText().toString().trim();
                edit_self.setText(region);
                showSelected(region,listTerm,listView_self);
                break;
            case 6:
                String colposcopy = et_yindaojin.getText().toString().trim();
                edit_self.setText(colposcopy);
                showSelected(colposcopy,listTerm,listView_self);
                break;
            case 7:
                String suspected = et_nizhen.getText().toString().trim();
                edit_self.setText(suspected);
                showSelected(suspected,listTerm,listView_self);
                break;
            case 8:
                String opinion = et_yijian.getText().toString().trim();
                edit_self.setText(opinion);
                showSelected(opinion,listTerm,listView_self);
                break;
            case 9:
                String color = et_color.getText().toString().trim();
                edit_self.setText(et_color.getText().toString().trim());
                showSelected(color,listTerm,listView_self);
                break;
            case 10:
                String bloodvessel = et_xueguan.getText().toString().trim();
                edit_self.setText(bloodvessel);
                showSelected(bloodvessel,listTerm,listView_self);
                break;
            case 11:
                String iodinestaining = et_dianranse.getText().toString().trim();
                edit_self.setText(iodinestaining);
                showSelected(iodinestaining,listTerm,listView_self);
                break;
            case 12:
                String aceticacid = et_cusuan.getText().toString().trim();
                edit_self.setText(aceticacid);
                showSelected(aceticacid,listTerm,listView_self);
                break;
            case 13:
                String boundary = et_bianjie.getText().toString().trim();
                edit_self.setText(boundary);
                showSelected(boundary,listTerm,listView_self);
                break;
            case 14:
                String handle = et_handle.getText().toString().trim();
                edit_self.setText(handle);
                showSelected(handle,listTerm,listView_self);
                break;
            default:
                break;
        }

//        linearLayoutMain.addView(edit);
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.print_Choosing_terms)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(sparseBooleanArray != null){
                            sparseBooleanArray.clear();
                        }

                        switch (temp) {//将edit的值赋给各个选项的输入框
                            case 1:
                                et_xibaoxue.setText(edit_self.getText().toString().trim());
                                break;
                            case 2:
                                et_dna.setText(edit_self.getText().toString().trim());
                                break;
                            case 3:
                                et_zhengzhuang.setText(edit_self.getText().toString().trim());
                                break;
                            case 4:
                                et_pinggu.setText(edit_self.getText().toString().trim());
                                break;
                            case 5:
                                et_quyu.setText(edit_self.getText().toString().trim());
                                break;
                            case 6:
                                et_yindaojin.setText(edit_self.getText().toString().trim());
                                break;
                            case 7:
                                et_nizhen.setText(edit_self.getText().toString().trim());
                                break;
                            case 8:
                                et_yijian.setText(edit_self.getText().toString().trim());
                                break;
                            case 9:
                                et_color.setText(edit_self.getText().toString().trim());
                                break;
                            case 10:
                                et_xueguan.setText(edit_self.getText().toString().trim());
                                break;
                            case 11:
                                et_dianranse.setText(edit_self.getText().toString().trim());
                                break;
                            case 12:
                                et_cusuan.setText(edit_self.getText().toString().trim());
                                break;
                            case 13:
                                et_bianjie.setText(edit_self.getText().toString().trim());
                                break;
                            case 14:
                                et_handle.setText(edit_self.getText().toString().trim());
                                break;
                            default:
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
                boolean isChecked = false;
                if(temp == 9||temp==10||temp==11||temp==13){//将listview 的多选改造成单选
                    for(int i = 0;i < listTerm.size();i++){
                        if(i != arg2){//只要不是点击的item,都设置为false,未点击状态
                            sparseBooleanArray.put(i,false);
                            listView_self.setItemChecked(i,false);
                        }else {
                            isChecked = sparseBooleanArray.get(arg2);
                            if(isChecked){//如果处于选中状态，则取消该状态
                                sparseBooleanArray.put(i,false);
                                listView_self.setItemChecked(i,false);
                            }else {//如果处于未选中状态，则修改为选中状态
                                sparseBooleanArray.put(i,true);
                                listView_self.setItemChecked(i,true);
                            }
                        }
                    }
                    isChecked = sparseBooleanArray.get(arg2);

                }else {
                    SparseBooleanArray sparseBooleanArray1 = listView_self.getCheckedItemPositions();
                    isChecked = sparseBooleanArray1.get(arg2);
                }
                switch (temp) {//根据点击给edit赋值
                    case 1:
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 2:
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 3:

                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 4:

                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 5:

                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 6:
                            setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 7:
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 8:
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 9://颜色
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 10://血管
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 11://碘染色
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 12://评估结果
                        edit_self.setText(listTerm.get(arg2));
                        break;
                    case 13://边界
                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    case 14:

                        setSelection(temp,isChecked,arg2,listTerm);
                        break;
                    default:
                        break;
                }
                onResume();
            }
        });

        listView_self.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Diacrisis> diacrisisList;
                int diaId = 0;
                switch (temp) {
                    case 1:
                        diacrisisList = LitePal.where("cytology=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 1, listTerm, i, listView_self, edit_self);
                        break;
                    case 2:
                        diacrisisList = LitePal.where("dna=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 2, listTerm, i, listView_self, edit_self);
                        break;
                    case 3:
                        diacrisisList = LitePal.where("Symptom=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 3, listTerm, i, listView_self, edit_self);
                        break;
                    case 4:
                        diacrisisList = LitePal.where("Assessment=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 4, listTerm, i, listView_self, edit_self);
                        break;
                    case 5:
                        diacrisisList = LitePal.where("region=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 5, listTerm, i, listView_self, edit_self);
                        break;
                    case 6:
                        diacrisisList = LitePal.where("Colposcopy=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 6, listTerm, i, listView_self, edit_self);
                        break;
                    case 7:
                        diacrisisList = LitePal.where("Suspected=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 7, listTerm, i, listView_self, edit_self);
                        break;
                    case 8:
                        diacrisisList = LitePal.where("attention=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 8, listTerm, i, listView_self, edit_self);
                        break;
                    case 9:
                        diacrisisList = LitePal.where("color=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 9, listTerm, i, listView_self, edit_self);
                        break;
                    case 10:
                        diacrisisList = LitePal.where("xueguna=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 10, listTerm, i, listView_self, edit_self);
                        break;
                    case 11:
                        diacrisisList = LitePal.where("dianranse=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 11, listTerm, i, listView_self, edit_self);
                        break;
                    case 12:
                        diacrisisList = LitePal.where("cusuan=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 12, listTerm, i, listView_self, edit_self);
                        break;
                    case 13:
                        diacrisisList = LitePal.where("bianjie=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 13, listTerm, i, listView_self, edit_self);
                        break;
                    case 14:
                        diacrisisList = LitePal.where("handle=?", listTerm.get(i)).find(Diacrisis.class);
                        for (int j = 0; j < diacrisisList.size(); j++) {
                            diaId = diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId, 14, listTerm, i, listView_self, edit_self);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
    //删除数组指定位置的数据
    private String  delete(String data,String click_data){
        String [] datas = data.split(";");
        if(datas.length > 0){
            List<String>list = Arrays.asList(datas);
            for(int i =0; i < list.size();i++){
                if(list.get(i).contains(click_data)){
                    data = "";
                    List<String>arraayList = new ArrayList<String>(list);
                    arraayList.remove(click_data);
                    for(int j=0;j<arraayList.size();j++){
                        data += arraayList.get(j)+";";
                    }
                }
            }
        }

        return data;
    }


    //诊断信息 长按删除
    private void longDeleteDis(EditText edit, List<String> listTerm, int m, ListView listView) {
        dia.save();
        edit.setText("");
        listTerm.remove(m);
        printdialogadapter = new Printdialogadapter(getContext(),listTerm);
        printdialogadapter.notifyDataSetChanged();
        listView.setAdapter(printdialogadapter);
    }

    //长按就显示出dialog，然后可以选择删除诊断的相关信息
    private void initId(int diaId, final int temp, final List<String> list, final int m, final ListView listView, final EditText edit) {
        List<Diacrisis> diacrisisList = LitePal.where("diaId=?", diaId + "").find(Diacrisis.class);
        for (int i = 0; i < diacrisisList.size(); i++) {
            dia = diacrisisList.get(0);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.print_terms_delete));
        builder.setTitle(getString(R.string.print_terms_delete_title));
        builder.setPositiveButton(getString(R.string.image_manage_delete_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (temp) {
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
                    default:
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


    private String HP_WIFI_NAME;
    private String HP_WIFI_PASS;

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        OneItem.getOneItem().setIntImage(1);
        initDele();
        if (isVisible) {
            mState = true;
            HP_WIFI_NAME = (String) SPUtils.get(getActivity(), Const.HP_WIFI_SSID_KEY, "");
            HP_WIFI_PASS = (String) SPUtils.get(getActivity(), Const.HP_WIFI_PASS_KEY, "12345678");
            WifiConnectManager.getInstance().connectWifi(HP_WIFI_NAME, HP_WIFI_PASS, Const.WIFI_TYPE_HP, this);
            if(et_bianhao.getText().toString().equals("")){
                select(0);
            }

        } else {
            mState = false;
            if (select_dialog != null) {
                select_dialog.dismiss();
            }
        }
    }
    //pdf 生成之后 被调用
    @Override
    public void pdfSuccess(int index) {
        Logger.e("生成报告的返回值 ：" + index);
        if (index == 2) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            return;
        }
        try {
            if (fileUtil.getFileSize(OneItem.getOneItem().getFile()) != 0) {
                Logger.e("报告生成成功");
                Log.e(TAG, "报告生成成功 1: ");
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

            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), getString(R.string.print_Generate_report_faild));
//                        Toast.makeText(getActivity(), getContext().getString(R.string.print_Generate_report_faild), Toast.LENGTH_SHORT).show();
                        if (OneItem.getOneItem().getFile().exists()) {
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
            Log.e(TAG, "pdfSuccess:错误 ");
            e.printStackTrace();
        } finally {

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
                            MyToast.showToast(getActivity(), getString(R.string.print_data_clear));
//                            Toast.makeText(getActivity(), getContext().getString(R.string.print_data_clear
//                            ), Toast.LENGTH_SHORT).show();
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


    private void createPDFDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.print_pdf_dialog_title));
        builder.setMessage(getString(R.string.print_pdf_dialog_message));

        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                loadingDialog.setMessage(getString(R.string.print_pdf_dialog_loding));
                loadingDialog.dialogShow();
                if (OneItem.getOneItem().getD() == 1) {
                    showList();//添加默认图片
                    initBaocun();//保存添加的诊断信息 并且生成PDF报告
                    backupsUtils.initBackUpUser(1);
                } else if (OneItem.getOneItem().getD() == 2) {
                    a = 1;
                    initBaocun();
                    backupsUtils.initBackUpUser(1);
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
        if (select_dialog != null) {
            if (select_dialog.isShowing()) {
                select_dialog.dismiss();
                select_dialog = null;
            }
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        WifiConnectManager.getInstance().stopThreadConnectWifi();

    }

    @Override //wifi开始连接
    public void startWifiConnecting(String type) {
        if (type.equals(Const.WIFI_TYPE_HP) && mState) {


        }

    }

    @Override //wifi连接成功
    public void wifiConnectSuccess(String type) {
        if (type.equals(Const.WIFI_TYPE_HP) && mState) {
            MyToast.showToast(getActivity(),getString(R.string.wifiSuccessMsg));
//            SouthUtil.showToast(getActivity(), getString(R.string.wifiSuccessMsg));
        }
    }

    @Override //wifi连接失败
    public void wifiConnectFalid(String type) {
        if (type.equals(Const.WIFI_TYPE_HP) && mState) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    MyToast.showToast(getActivity(),getString(R.string.wifiFailMsgHP));
                    SouthUtil.showToast(getActivity(), getString(R.string.wifiFailMsgHP));

                }
            });

        }
    }

    @Override // 没有找到指定wifi，开始循环搜索
    public void wifiCycleSearch(String type, boolean isSSID, int count) {
        if (type.equals(Const.WIFI_TYPE_HP) && mState) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID) {
                        WifiConnectManager.getInstance().connectWithWpa(HP_WIFI_NAME, HP_WIFI_PASS);
                    } else {
//                        MyToast.showToast(getActivity(),getString(R.string.wifiFailMsgprint));
                        SouthUtil.showToast(getActivity(), getString(R.string.wifiFailMsgprint));
                    }
                }
            });


        }
    }

    @Override // wifi名称为空
    public void wifiInputNameEmpty(String type) {
        Log.e(TAG, "wifiInputNameEmpty: hp");
        if (type.equals(Const.WIFI_TYPE_HP) && mState) {
//            MyToast.showToast(getActivity(),getString(R.string.wifi_HPname_empty));
            SouthUtil.showToast(getActivity(), getString(R.string.wifi_HPname_empty));
        }
    }

    private void setSelection(int temp,boolean isChecked,int arg2,List<String>listTerm){
        if(temp == 9||temp == 10||temp ==11 ||temp==13){
            if(isChecked){
                edit_self.setText((CharSequence) listTerm.get(arg2));
                Const.sumnumber += arg2;
                Log.e("printer3",Const.sumnumber+"  ,, cc");
                if(temp == 9){
                    if (Const.color != -1 && Const.sumnumber != 0) {
                        Const.sumnumber -= Const.color;
                    }
                    Const.color = arg2;
                }
                if(temp == 10){
                    if (Const.vessel != -1 && Const.sumnumber != 0) {
                        Const.sumnumber -= Const.vessel;
                    }
                    Const.vessel = arg2;
                }
                if(temp == 11){
                    if (Const.stain != -1 && Const.sumnumber != 0) {
                        Const.sumnumber -= Const.stain;
                    }
                    Const.stain = arg2;
                }
                if(temp == 13){
                    if (Const.surface != -1 && Const.sumnumber != 0) {
                        Const.sumnumber -= Const.surface;
                    }
                    Const.surface = arg2;
                }
                Log.e("printer1",Const.sumnumber+"  ,, aa");
                if (Const.sumnumber >= 0 && Const.sumnumber <= 2) {
                    et_cusuan.setText(Const.sumnumber + "(CIN1)");
                } else if (Const.sumnumber >= 3 && Const.sumnumber <= 4) {
                    et_cusuan.setText(Const.sumnumber + "(CIN1 or CIN2)");
                } else if (Const.sumnumber >= 5 && Const.sumnumber <= 8) {
                    et_cusuan.setText(Const.sumnumber + "(CIN 2-3)");
                }
            }else {
                edit_self.setText("");
                if(temp == 9){
                    Const.sumnumber -= Const.color;
                    Const.color = -1;
                }
                if(temp == 10){
                    Const.sumnumber -= Const.vessel;
                    Const.vessel = -1;
                }
                if(temp == 11){
                    Const.sumnumber -= Const.stain;
                    Const.stain = -1;
                }
                if(temp == 13){
                    Const.sumnumber -= Const.surface;
                    Const.surface = -1;
                }

                Log.e("printer2",Const.sumnumber+"  ,, bb");
                if (Const.sumnumber >= 0 && Const.sumnumber <= 2) {
                    et_cusuan.setText(Const.sumnumber + "(CIN1)");
                } else if (Const.sumnumber >= 3 && Const.sumnumber <= 4) {
                    et_cusuan.setText(Const.sumnumber + "(CIN1 or CIN2)");
                } else if (Const.sumnumber >= 5 && Const.sumnumber <= 8) {
                    et_cusuan.setText(Const.sumnumber + "(CIN 2-3)");
                }
            }
        }else {
            if(isChecked){
                edit_self.setText(listTerm.get(arg2) + ";" + edit_self.getText().toString().trim());
            }else {
                String data = edit_self.getText().toString().toString();
                String click_data = listTerm.get(arg2);
                String new_data = delete(data,click_data);
                edit_self.setText(new_data);
            }
        }
    }
}