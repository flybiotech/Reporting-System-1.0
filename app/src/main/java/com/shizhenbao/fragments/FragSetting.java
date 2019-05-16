package com.shizhenbao.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.activity.GuanliyonghuActivity;
import com.shizhenbao.activity.GuanyuActivity;
import com.shizhenbao.activity.HelpActivity;
import com.shizhenbao.activity.JianjiActivity;
import com.shizhenbao.activity.LoginActivity;
import com.shizhenbao.activity.WIFITestActivity;
import com.shizhenbao.activity.WifiSettingActivity;
import com.shizhenbao.activity.YinjianActivity;
import com.shizhenbao.activity.base.XiugaiPasswordActivity;
import com.shizhenbao.adapter.CommonAdapter;
import com.shizhenbao.adapter.SettingAdapter;
import com.shizhenbao.adapter.ViewHolder;
import com.shizhenbao.connect.DevConnect;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.Backup;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.Path;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.updataapk.Updater;
import com.shizhenbao.updataapk.UpdaterConfig;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.FTP;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.ProgressInputStream;
import com.shizhenbao.util.ZipUtil;
import com.shizhenbao.wifiinfo.WifiReceiver;
import com.util.AlignedTextUtils;
import com.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;


/**
 * Created by dell on 2017/5/22.
 */

public class FragSetting  extends BaseFragment implements View.OnClickListener,WifiReceiver.WifiConnectinfo {

    private double screenInches;//屏幕的尺寸
    private TextView title_text;
    private ListView lv;
    private List<String> list;
    private Button bt_right;
    Doctor doctor=null;
    boolean i=true;
    private List<SystemSet> system;
    private List<String> dialogList;
    private int diaId;
    int mItemLayoutId;//adapter的xml文件的id
    private static final String TAG = "TAG1_setting";
    private String localsn="";
    private String versionCode="1",versionName=null,updatapath=null;
    private EditText et_name,et_xibaoxue,et_dna,et_zhengzhuang,et_pinggu,et_quyu,et_yindaojin,et_nizhen,et_yijian,et_bianhao,et_handle;
    private EditText et_bianjie,et_color,et_xueguan,et_dianranse,et_cusuan;
    String ImgfilePath= Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path();//需要压缩的图片文件的路径
    String zipFilePath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip";//压缩后图片文件保存的路径
    String UpImgfilePath= Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path()+"/" ;//解压后图片文件保存的路径
    ProgressDialog xh_pDialog;//进度提示框
    String Localversion;//本机版本号
    private LinkedList<File> fileList;
    private static URL url;//请求的url地址
    private static int state=-1;//网络请求返回值
    private static HttpURLConnection urlConnection;
    private int temp;//设置操作参数，0为备份，1为恢复，2为更新
    DevConnect devConnect ;
//    private static boolean mStatess = false;
    private CustomConfirmDialog customConfirmDialog = null;//删除确定
    private CustomConfirmDialog customConfirmDialog1 = null;//删除确定
    LoadingDialog lod;
    private String []tv_name;
    private Backup backup=null;
    private EditText et_ftpid,et_ftpport,et_ftpname,et_ftppassword;//配置FTP服务器
    private TextView tv_01,tv_02,tv_03,tv_04,tv_05,tv_06,tv_07,tv_08,tv_09,tv_10,tv_11,tv_12,tv_13,tv_14;
    private static final String banbenpath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + "banben.txt";
    private String userpath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + localsn+"user.txt";
    private String doctorpath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + localsn+"doctor.txt";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                String result= (String) msg.obj;
                try {
                    List<User>userList=DataSupport.findAll(User.class);
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray array=jsonObject.getJSONArray("persondata");
                    if(userList.size()>0){
                        DataSupport.deleteAll(User.class);
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                                    if(obj.getInt("is_diag")==2){
                                        User user = new User();
                                        user.setpId(obj.getString("pId"));
                                        user.setpName(obj.getString("pName"));
                                        user.setTel(obj.getString("tel"));
                                        user.setAge(obj.getString("age"));
                                        user.setRegistDateint(obj.getLong("registDateint"));
                                        user.setIdNumber(obj.getString("idNumber"));
                                        user.setsSNumber(obj.getString("sSNumber"));
                                        user.setCaseNumbe(obj.getString("caseNumbe"));
                                        user.sethCG(obj.getString("hCG"));
                                        user.setWork(obj.getString("work"));
                                        user.setPregnantCount(obj.getString("pregnantCount"));
                                        user.setChildCount(obj.getString("childCount"));
                                        user.setAbortionCount(obj.getString("abortionCount"));
                                        user.setSexPartnerCount(obj.getString("sexPartnerCount"));
                                        user.setSmokeTime(obj.getString("smokeTime"));
                                        user.setCheckNotes(obj.getString("checkNotes"));
                                        user.setBloodType(obj.getString("bloodType"));
                                        user.setBirthControlMode(obj.getString("birthControlMode"));
                                        user.setpSource(obj.getString("pSource"));
                                        user.setMarry(obj.getString("marry"));
                                        user.setAdvice(obj.getString("advice"));
                                        user.setHpv_dna(obj.getString("Hpv_dna"));
                                        user.setSymptom(obj.getString("symptom"));
                                        user.setBingbian(obj.getString("bingbian"));
                                        user.setYindaojin(obj.getString("yindaojin"));
                                        user.setNizhen(obj.getString("nizhen"));
                                        user.setZhuyishixiang(obj.getString("zhuyishixiang"));
                                        user.setBianjie(obj.getString("bianjie"));
                                        user.setHandle(obj.getString("handle"));
                                        user.setColor(obj.getString("color"));
                                        user.setXueguna(obj.getString("xueguna"));
                                        user.setDianranse(obj.getString("dianranse"));
                                        user.setCusuan(obj.getString("cusuan"));
                                        user.setRegistDate(obj.getString("registDate"));
                                        user.setCheckDate(obj.getString("checkDate"));
                                        user.setOperId(obj.getInt("operId"));
//                          user.setImgPath(obj.get("imgPath"));
                                        user.setImage(obj.getInt("image"));
                                        user.setCutPath(obj.getString("cutPath"));
                                        user.setRegistDateint(obj.getLong("registDateint"));
                                        user.setbResult(obj.getBoolean("bResult"));
                                        user.setIs_diag(obj.getInt("is_diag"));
                                        user.setGatherPath(obj.getString("gatherPath"));
                                        user.setPdfPath(obj.getString("pdfPath"));
                                        user.save();
                                    }else {
                                        User user = new User();
                                        user.setpId(obj.getString("pId"));
                                        user.setpName(obj.getString("pName"));
                                        user.setTel(obj.getString("tel"));
                                        user.setAge(obj.getString("age"));
//                                        user.setCheckDate(obj.getString("checkDate"));
                                        user.setRegistDateint(obj.getLong("registDateint"));
                                        user.setIdNumber(obj.getString("idNumber"));
                                        user.setsSNumber(obj.getString("sSNumber"));
                                        user.setCaseNumbe(obj.getString("caseNumbe"));
                                        user.sethCG(obj.getString("hCG"));
                                        user.setWork(obj.getString("work"));
                                        user.setImage(obj.getInt("image"));
                                        user.setCutPath(obj.getString("cutPath"));
//                                        user.setRegistDateint(obj.getLong("registDateint"));
                                        user.setRegistDate(obj.getString("registDate"));
                                        user.setPregnantCount(obj.getString("pregnantCount"));
                                        user.setChildCount(obj.getString("childCount"));
                                        user.setAbortionCount(obj.getString("abortionCount"));
                                        user.setSexPartnerCount(obj.getString("sexPartnerCount"));
                                        user.setSmokeTime(obj.getString("smokeTime"));
                                        user.setCheckNotes(obj.getString("checkNotes"));
                                        user.setBloodType(obj.getString("bloodType"));
                                        user.setBirthControlMode(obj.getString("birthControlMode"));
                                        user.setpSource(obj.getString("pSource"));
                                        user.setMarry(obj.getString("marry"));
                                        user.setOperId(obj.getInt("operId"));
                                        user.setGatherPath(obj.getString("gatherPath"));
                                        user.setIs_diag(obj.getInt("is_diag"));
                                        user.save();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(msg.what==2){
                deleLitepal();
                Toast.makeText(getContext(), R.string.setting_backups_success_message, Toast.LENGTH_SHORT).show();
            }else if(msg.what==3){
                Toast.makeText(getContext(),R.string.setting_backups_success_message, Toast.LENGTH_SHORT).show();
            }else if(msg.what==4){
                Toast.makeText(getContext(),R.string.setting_recovery_success_message, Toast.LENGTH_SHORT).show();
            }else if(msg.what==5){
                Toast.makeText(getContext(), R.string.setting_recovery_success_message, Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==6){
                Toast.makeText(getContext(), R.string.setting_data_clear_success_message, Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(getContext(),LoginActivity.class);
//                intent.putExtra("msg",1);
//                startActivity(intent);
//                getActivity().finish();
            }else if(msg.what==7){
                Log.e("msg","我是六");
                if(updatapath!=null){
                    showDialog();
                }else {
                    Toast.makeText(getContext(), R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
                }
            }else if(msg.what==8){
                initFTPSET(1);
                Toast.makeText(getContext(), R.string.setting_link_server_faild, Toast.LENGTH_SHORT).show();
            }else if(msg.what==9){
                initFTPSET(1);
                Toast.makeText(getContext(),R.string.setting_link_server_faild, Toast.LENGTH_SHORT).show();
            }else if(msg.what==-1){
                devConnect.lostwifi();
                Toast.makeText(getContext(),R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
            }else if(msg.what==0){
                devConnect.lostwifi();
                if(temp==0){
                    if(localsn!=null&&!localsn.equals("")){
                        initUpLoad();
                    }else {
                        Intent intent=new Intent(getContext(),GuanyuActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(), R.string.setting_local_sn, Toast.LENGTH_SHORT).show();
                    }
                }else if(temp==1){
                    DownFile();
                }else if(temp==2){
                    showDialogUpdate();
                }

            }else if(msg.what==-2){
                String result= (String) msg.obj;
                try {
                    List<Doctor>doctorlist=DataSupport.findAll(Doctor.class);
                    if(doctorlist.size()>0){
                        DataSupport.deleteAll(Doctor.class);
                    }
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray array=jsonObject.getJSONArray("persondata");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Doctor doctor=new Doctor();
                        doctor.setdId(obj.getInt("dId"));
                        doctor.setdName(obj.getString("dName"));
                        doctor.setdPassword(obj.getString("dPassword"));
                        doctor.setdEmail(obj.getString("dEmail"));
                        doctor.setdAdmin(obj.getBoolean("dAdmin"));
                        doctor.setLoginCount(obj.getInt("loginCount"));
                        doctor.setdTemp(obj.getInt("dTemp"));
                        doctor.setEdit_hos_name(obj.getString("edit_hos_name"));
                        doctor.setEdit_hos_keshi(obj.getString("edit_hos_keshi"));
                        doctor.save();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(msg.what==-4){
                if(xh_pDialog!=null){
                    if(xh_pDialog.isShowing()){
                        xh_pDialog.dismiss();
                        Toast.makeText(getContext(), getString(R.string.setting_data_backupoast), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        screenInches = DeviceOfSize.getScreenSizeOfDevice2(getActivity());
       View view=inflater.inflate(R.layout.frag_layout_setting, container, false);
        init(view);
        if(OneItem.getOneItem().getName()!=null&&!OneItem.getOneItem().getName().equals("")){
            doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        }else {
            new UserManager().getExceName();
            doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        }
        if (doctor != null) {
            i=doctor.isdAdmin();
        }
        if(i){//普通用户。默认就是普通用户
            initData();
            initItemClick();
        }else {
            initDataAdmin();//超级用户
            initItemClickAdmin();
        }
        return view;
    }

    @Override
    public void onStart() {//得到本机SN号
        super.onStart();
//        mStatess = false;
        List<SystemSet> systemSet=DataSupport.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
//        WifiReceiver.setWifiConnectionListener(this);
    }
    private void deleLitepal(){
        customConfirmDialog.show(getString(R.string.patient_show_delete_all_prompt), getString(R.string.setting_next), getString(R.string.button_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleLocal();
                customConfirmDialog.dimessDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customConfirmDialog.dimessDialog();
            }
        });
//        customConfirmDialog.show(getString(R.string.patient_show_delete_all_prompt), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//确定删除
//                deleLocal();
//                customConfirmDialog.dimessDialog();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//不删除
//                customConfirmDialog.dimessDialog();
//            }
//        });
    }
    private void deleLocal(){
        customConfirmDialog1.show(getString(R.string.setting_delete_sure_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确定删除
//                DataSupport.deleteAll(User.class);
                customConfirmDialog1.dimessDialog();
                xh_pDialog=new ProgressDialog(getContext());
                xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                xh_pDialog.setTitle(getString(R.string.print_dialog_title));
                xh_pDialog.setCancelable(true);
                xh_pDialog.setIndeterminate(true);
                xh_pDialog.setMessage(getString(R.string.setting_backups_loading));
                xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        xh_pDialog.cancel();
                    }
                });
                xh_pDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        final Message message=handler.obtainMessage();
                        new DevConnect(getContext(),handler).initData();
                        xh_pDialog.cancel();
//                        handler.sendMessage(message);
                    }
                }).start();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//不删除
                customConfirmDialog1.dimessDialog();
            }
        });
    }
    private void initItemClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;
                switch (position) {
                    case 0:
                        Const.isSave=true;
                        intent=new Intent(getContext(), LoginActivity.class);//切换用户
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case 1://用户管理
                        intent=new Intent(getContext(), GuanliyonghuActivity.class);
                        startActivity(intent);
                        break;
                    case 2://修改密码
                        intent=new Intent(getContext(), XiugaiPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 3://打印机wifi设置，视珍宝wifi设置
                        intent=new Intent(getContext(),WifiSettingActivity.class);
                        intent.putExtra("wifiIndex", 0);
                        startActivity(intent);
                        break;
                    case 4://wifi联网测试
                        intent=new Intent(getContext(),WIFITestActivity.class);
                        startActivity(intent);
                        break;
                    case 5://添加术语
                        lv_addDia();
                        break;
                    case 6://高级设置
                        lv_add();
                        break;
                    case 7://图片编辑
                        intent=new Intent(getContext(), JianjiActivity.class);
                        Const.dialogshow=0;
                        startActivity(intent);
                        break;
                    case 8://数据备份
                        initBackUpDialog();
//                        initLocalBackup();
                        break;
                    case 9://数据恢复
                        userpath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + localsn+"user.txt";
                        doctorpath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath() + File.separator + localsn+"doctor.txt";
                        initUpDialog();
                        break;
                    case 10://视珍宝硬件参数设置
                        intent=new Intent(getContext(), YinjianActivity.class);
                        startActivity(intent);
                        break;
//                    case 11://一键清除
//
//                        dialogViewDeleteData(getActivity());
//                        break;
//                    case 11://版本更新
//                        boolean ab=isNetworkAvailable();//判断wifi是否可以连接
//                        if(ab){
//                            Localversion= new GuanyuActivity().getVersionName(getContext());
//                            temp=2;
//                            getUrl("http://www.baidu.com");
//                        }else {
//                            Toast.makeText(getContext(), R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
//                        }
//                        break;
                    case 11://关于
                        intent=new Intent(getContext(), GuanyuActivity.class);
                        startActivity(intent);
                        break;
                    case 12://帮助
                        intent=new Intent(getContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    /**
     * 得到当前连接wifi名称
     */
    private String getConnectWifiSsid(){
        WifiManager wifiManager= (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }
    /**
      * 检测网络是否连接
      *
      * @return
      */
    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接

        return (info != null && info.isAvailable());
    }

    public void initUpLoad(){//多文件上传，在使用FTP备份时，需要先在本地生成文件

        final List<SystemSet>systemSets=DataSupport.findAll(SystemSet.class);
//        View view=null;
        if(systemSets.size()>1){
            if(systemSets.get(1).getHostName()==null||systemSets.get(1).getHostName().equals("")){
//           view= LayoutInflater.from(getContext()).inflate(R.layout.ftplink,null);
                initFTPSET(0);
            }else {
                xh_pDialog=new ProgressDialog(getContext());
                xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                xh_pDialog.setTitle(getString(R.string.print_dialog_title));
                xh_pDialog.setCancelable(true);
                xh_pDialog.setIndeterminate(true);
                xh_pDialog.setMessage(getString(R.string.setting_backups_loading));
                xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        xh_pDialog.cancel();
                        try {
                            new FTP().closeConnect();
                            Toast.makeText(getContext(),getContext().getString(R.string.setting_ftp_disconnect), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                xh_pDialog.show();
                fileList=new LinkedList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Message message=handler.obtainMessage();
                        message.what=3;
//                        initBackups();//数据库文件保存在本地
                        backup.initBackups();
                        initImageBackUp();//图片文件生成压缩文件保存在本地
                        // 上传
                        File file1 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip");//本地数据压缩文件名称
                        File file2 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"user.txt");//数据库文件
                        File file3 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"doctor.txt");//数据库文件
                        fileList.add(file1);
                        fileList.add(file2);
                        fileList.add(file3);
                        OneItem.getOneItem().setSelectFTP(1);
                        try {
                            //多文件上传
                            new FTP().uploadMultiFile(fileList,OneItem.getOneItem().getBackUpNetPath(),new FTP.UploadProgressListener(){
                                @Override
                                public void onUploadProgress(String currentStep,long uploadSize,File file) {
                                    // TODO Auto-generated method stub
                                    if(currentStep.equals(FTP.FTP_CONNECT_FAIL)){
                                        xh_pDialog.dismiss();
                                        message.what=8;
                                        handler.sendMessage(message);
                                        return;
                                    }else if(currentStep.equals(FTP.FTP_CONNECT_SUCCESSS)){
                                        if(currentStep.equals(Item.FTP_UPLOAD_SUCCESS)){
                                        } else if(currentStep.equals(Item.FTP_UPLOAD_LOADING)){
                                            long fize = file.length();
                                            float num = (float)uploadSize / (float)fize;
                                            int result = (int)(num * 100);
                                        }
                                    }
                                }
                            });
                            xh_pDialog.cancel();
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else {
            initFTPSET(1);
        }
    }

    private void DownFile(){

        xh_pDialog=new ProgressDialog(getContext());
        xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        xh_pDialog.setTitle(getString(R.string.print_dialog_title));
        xh_pDialog.setCancelable(true);
        xh_pDialog.setIndeterminate(true);
        xh_pDialog.setMessage(getString(R.string.setting_recovery_loading));
        xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                xh_pDialog.cancel();
            }
        });
        xh_pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Message message=handler.obtainMessage();
                message.what=4;
                // 下载
                try {
                    File file1 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip");//本地数据压缩文件名称
                    File file2 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"user.txt");//数据库文件
                    File file3 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"doctor.txt");//数据库文件
                    if(file1.exists()&&file2.exists()&&file3.exists()){
                        file2.delete();
                        file1.delete();
                        file3.delete();
                    }
                    OneItem.getOneItem().setSelectFTP(1);
                    //单文件下载
                    new FTP().downloadSingleFile(OneItem.getOneItem().getBackUpNetPath()+localsn+"user.txt",new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/",localsn+"user.txt",new FTP.DownLoadProgressListener(){
                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            if(currentStep.equals(FTP.FTP_CONNECT_FAIL)){
                                xh_pDialog.dismiss();
                                message.what=9;
                                handler.sendMessage(message);
                                return;
                            }else {
                                if(currentStep.equals(Item.FTP_DOWN_SUCCESS)){

                                } else if(currentStep.equals(Item.FTP_DOWN_LOADING)){

                                }
                            }
                        }
                    });
                    new FTP().downloadSingleFile(OneItem.getOneItem().getBackUpNetPath()+localsn+"doctor.txt",new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/",localsn+"doctor.txt",new FTP.DownLoadProgressListener(){
                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            if(currentStep.equals(FTP.FTP_CONNECT_FAIL)){
                                xh_pDialog.dismiss();
                                message.what=9;
                                handler.sendMessage(message);
                                return;
                            }else {
                                if(currentStep.equals(Item.FTP_DOWN_SUCCESS)){

                                } else if(currentStep.equals(Item.FTP_DOWN_LOADING)){

                                }
                            }
                        }
                    });
                    new FTP().downloadSingleFile(OneItem.getOneItem().getBackUpNetPath()+localsn+"IMGZIP.zip",new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/",localsn+"IMGZIP.zip",new FTP.DownLoadProgressListener(){

                        @Override
                        public void onDownLoadProgress(String currentStep, long  downProcess, File file) {
                            if(currentStep.equals(FTP.FTP_CONNECT_FAIL)){
                                xh_pDialog.dismiss();
                                message.what=9;
                                handler.sendMessage(message);
                                return;
                            }else {
                                if(currentStep.equals(Item.FTP_DOWN_SUCCESS)){

                                } else if(currentStep.equals(Item.FTP_DOWN_LOADING)){

                                }
                            }

                        }

                    });
                    File file=new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath());
                    if(file.exists()){
                        file.mkdirs();
                    }
                    if(!new File(ImgfilePath).exists()){
                        new File(ImgfilePath).mkdirs();
                    }
                    initRenew();
                    initImageUp();
                    xh_pDialog.cancel();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
//    /*
//    选择FTP服务器
//     */
    private void initFTPSET(int temp){

            View  view= LayoutInflater.from(getContext()).inflate(R.layout.ftplink,null);
            initFTP(view);
            LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
            linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
            final List<SystemSet> setList = DataSupport.findAll(SystemSet.class);
//            if(temp==1) {
                try {
                    if (setList.size() > 1) {
                        for (int i = 0; i < setList.size(); i++) {
                            et_ftpid.setText(setList.get(1).getHostName());
                            et_ftpport.setText(setList.get(1).getServerPort()+"");
                            et_ftpname.setText(setList.get(1).getUserName());
                            et_ftppassword.setText(setList.get(1).getPassword());
                        }
                    }
//                    else {
//                        initFTPSET(0);
//                    }
                } catch (Exception e) {
//                Toast.makeText(getContext(), "请先进行FTP备份", Toast.LENGTH_SHORT).show();
                }
//            }
//            }else {
                linearLayoutMain.addView(view);
                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.setting_ftp_server_title)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                        .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                return;
                            }
                        }).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!TextUtils.isEmpty(et_ftpid.getText().toString().trim())//判断信息是否为空
                                        &&!TextUtils.isEmpty(et_ftpname.getText().toString().trim())
                                        &&!TextUtils.isEmpty(et_ftppassword.getText().toString().trim())
                                        &&!TextUtils.isEmpty(et_ftpport.getText().toString().trim())
                                        ){
                                    List<SystemSet>setList=DataSupport.findAll(SystemSet.class);
                                    if(setList.size()==1){
                                        SystemSet systemSet=new SystemSet();
                                        systemSet.setHostName(et_ftpid.getText().toString().trim());
                                        systemSet.setServerPort(Integer.parseInt(et_ftpport.getText().toString().trim()));
                                        systemSet.setUserName(et_ftpname.getText().toString().trim());
                                        systemSet.setPassword(et_ftppassword.getText().toString().trim());
                                        systemSet.save();
                                    }else {
                                        for(int j=0;j<setList.size();j++){
                                            setList.get(1).setHostName(et_ftpid.getText().toString().trim());
                                            setList.get(1).setServerPort(Integer.parseInt(et_ftpport.getText().toString().trim()));
                                            setList.get(1).setUserName(et_ftpname.getText().toString().trim());
                                            setList.get(1).setPassword(et_ftppassword.getText().toString().trim());
                                        }
                                        setList.get(1).save();
                                    }
                                    dialogInterface.cancel();
                                    Toast.makeText(getContext(), R.string.setting_ftp_server_success, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(),R.string.setting_ftp_server_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create();
                if(!dialog.isShowing()){
                    dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
                    dialog.show();
                }

//            }

    }
    private void initLocalBackup(){
        if(localsn!=null&&!localsn.equals("")){
//            initUpLoad();
            final String SDsize=backup.getSDMemorysd();
            if(SDsize==null){
//                message.what=-4;
//                handler.sendMessage(message);
                Toast.makeText(getContext(), getString(R.string.setting_data_backupoast), Toast.LENGTH_SHORT).show();
                return;
            }
            xh_pDialog=new ProgressDialog(getContext());
            xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            xh_pDialog.setTitle(getString(R.string.print_dialog_title));
            xh_pDialog.setCancelable(true);
            xh_pDialog.setIndeterminate(true);
            xh_pDialog.setMessage(getString(R.string.setting_backups_loading));
            xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    xh_pDialog.cancel();
                }
            });
            xh_pDialog.show();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Message message=handler.obtainMessage();
                    String BackupFileSize= backup.getAutoFilesSize();
                    String BackupFileSize_new=BackupFileSize.substring(0,BackupFileSize.indexOf("G"));
                    String SDsize_new=SDsize.substring(0,SDsize.indexOf("G"));
                    if(Double.parseDouble(SDsize_new)-Double.parseDouble(BackupFileSize_new)<0.1){
                        message.what=-3;

                    }else {
                        message.what=2;
//                        initBackups();//患者数据库数据
                        backup.initBackDoctor();
                        backup.initBackups();
//                        initBackDoctor();//医生数据库数据
//                                initImageBackUp();
                        new Backup(getContext()).getSD();

                    }
                    xh_pDialog.cancel();
                    handler.sendMessage(message);
                }
            }.start();
        }else {
            Intent intent=new Intent(getContext(),GuanyuActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), R.string.setting_local_sn, Toast.LENGTH_SHORT).show();
        }

    }
    private void initBackUpDialog(){//数据备份
        final CharSequence[] items = {getString(R.string.setting_ftp_backups), getString(R.string.setting_local_backups)};//弹出框展示内容
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//声明弹出框
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item){
                    case 0:
                        boolean ab=isNetworkAvailable();//判断wifi是否可以连接
                        if(ab){
                            temp=0;
                            getUrl("http://www.baidu.com");
                        }else {
                            Toast.makeText(getContext(), R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:

                         initLocalBackup();

                        break;
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
//    private CustomConfirmDialog customConfirmDialog = null;
    private void initUpDialog(){//数据恢复
        final CharSequence[] items = {getString(R.string.setting_ftp_recovery), getString(R.string.setting_local_recovery)};//弹出框展示内容
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//声明弹出框
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item){
                    case 0:
//                        List<User> backupsList = DataSupport.findAll(User.class);
//                        if(backupsList.size()!=0){
//                            Toast.makeText(getContext(), R.string.setting_data_base_have_data, Toast.LENGTH_SHORT).show();
////                            deleteAll();
//                        }else {
                            boolean ab=isNetworkAvailable();//判断wifi是否可以连接
                            if(ab){
                                temp=1;
                                getUrl("http://www.baidu.com");
                            }else {
                                Toast.makeText(getContext(), R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
//                            }
                        }
                        break;
                    case 1:
//                        initUpBack();
                        local_recover();
                        break;
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void local_recover(){
        File file1 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"doctor.txt");//医生数据库文件
        File file2 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"user.txt");//患者数据库文件
//        List<User> backupsList = DataSupport.findAll(User.class);
//        if(backupsList.size()!=0){
//            Toast.makeText(getContext(), R.string.setting_data_base_have_data, Toast.LENGTH_SHORT).show();
//        }else {
            if(file1.exists()&&file2.exists()){
                xh_pDialog=new ProgressDialog(getContext());
                xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                xh_pDialog.setTitle(getString(R.string.print_dialog_title));
                xh_pDialog.setCancelable(true);
                xh_pDialog.setIndeterminate(true);
                xh_pDialog.setMessage(getString(R.string.setting_recovery_loading));
                xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        xh_pDialog.cancel();
                    }
                });
                xh_pDialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message message=handler.obtainMessage();
                        message.what=5;
                        initRenew();
                        initdoctor();
//                        initImageUp();
                        xh_pDialog.cancel();
                        handler.sendMessage(message);
                    }
                }.start();
            }
            else {
            Toast.makeText(getContext(), R.string.setting_recovery_no_data, Toast.LENGTH_SHORT).show();
        }
//        }
    }
//    private void initUpBack(){//数据恢复
//        File file1 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip");//本地数据压缩文件名称
//        File file2 = new File(new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/"+localsn+"json.txt");//数据库文件
//        List<User> backupsList = DataSupport.findAll(User.class);
//        if(backupsList.size()!=0){
//            Toast.makeText(getContext(), R.string.setting_data_base_have_data, Toast.LENGTH_SHORT).show();
//        }else {
//            if(file1.exists()&&file2.exists()){
//            xh_pDialog=new ProgressDialog(getContext());
//            xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            xh_pDialog.setTitle(getString(R.string.print_dialog_title));
//            xh_pDialog.setCancelable(true);
//            xh_pDialog.setIndeterminate(true);
//            xh_pDialog.setMessage(getString(R.string.setting_recovery_loading));
//            xh_pDialog.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    xh_pDialog.cancel();
//                }
//            });
//            xh_pDialog.show();
//            new Thread(){
//                @Override
//                public void run() {
//                    super.run();
//                    Message message=handler.obtainMessage();
//                    message.what=5;
//                    initRenew();
//                    initImageUp();
//                    xh_pDialog.cancel();
//                    handler.sendMessage(message);
//                }
//            }.start();
//        }else {
//                Toast.makeText(getContext(), R.string.setting_recovery_no_data, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void initImageBackUp(){
        try {
            zipFilePath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip";
            ZipUtil.zip(ImgfilePath,zipFilePath);
//            Toast.makeText(getContext(), "图片文件压缩成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initImageUp(){
        try {
            zipFilePath=Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+"/"+localsn+"IMGZIP.zip";//压缩后图片文件保存的路径
            File zipFile=new File(zipFilePath);
            UpImgfilePath= Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getGather_path()+"/" ;//解压后图片文件保存的路径
            if(zipFile.exists()){
                ZipUtil.unZip(zipFilePath,UpImgfilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //医院名称设置
    public void HosName(Context context){
        final Doctor doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.user_regester_hosptal_hint)); //设置对话框标题
        final EditText edit = new EditText(context);
        builder.setView(edit);
        edit.setText(doctor.getEdit_hos_name());
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doctor.setEdit_hos_name(edit.getText().toString().trim());
                doctor.save();
            }
        });

        builder.setNegativeButton(getString(R.string.image_cancel), new DialogInterface.OnClickListener() {
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
    //科室名称设置
    public void Departments (Context context){
        final Doctor doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.user_regester_department_hint)); //设置对话框标题
        final EditText edit = new EditText(context);
        builder.setView(edit);
        edit.setText(doctor.getEdit_hos_keshi());
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doctor.setEdit_hos_keshi(edit.getText().toString().trim());
                doctor.save();
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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

    private void lv_addDia(){
        View view = null;
        //判断屏幕的大小

//        screenInches = DeviceOfSize.getScreenSizeOfDevice2(getActivity());
//        if (screenInches > 6.0) {
            view= LayoutInflater.from(getContext()).inflate(R.layout.linear_view,null);
//        } else {
//            view= LayoutInflater.from(getContext()).inflate(R.layout.linear_view_phone,null);
//        }
        initView(view);
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        linearLayoutMain.addView(view);
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.setting_input_terms)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton(getString(R.string.patient_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(et_xibaoxue.getText().toString().trim())//判断诊断信息是否为空
                                &&TextUtils.isEmpty(et_dna.getText().toString().trim())
                                &&TextUtils.isEmpty(et_zhengzhuang.getText().toString().trim())
                                &&TextUtils.isEmpty(et_pinggu.getText().toString().trim())
                                &&TextUtils.isEmpty(et_quyu.getText().toString().trim())
                                &&TextUtils.isEmpty(et_yindaojin.getText().toString().trim())
                                &&TextUtils.isEmpty(et_nizhen.getText().toString().trim())
                                &&TextUtils.isEmpty(et_yijian.getText().toString().trim())
                                &&TextUtils.isEmpty(et_bianjie.getText().toString().trim())
                                &&TextUtils.isEmpty(et_color.getText().toString().trim())
                                &&TextUtils.isEmpty(et_cusuan.getText().toString().trim())
                                &&TextUtils.isEmpty(et_dianranse.getText().toString().trim())
                                &&TextUtils.isEmpty(et_xueguan.getText().toString().trim())
                                &&TextUtils.isEmpty(et_handle.getText().toString().trim())
                                ){
                            dialogInterface.cancel();
                        }else{
                            initBianhao();
                            Diacrisis diacrisis=new Diacrisis();
                            diacrisis.setDiaId(diaId+1);
                            diacrisis.setCytology(et_xibaoxue.getText().toString());
                            diacrisis.setDna(et_dna.getText().toString());
                            diacrisis.setSymptom(et_zhengzhuang.getText().toString());
                            diacrisis.setAssessment(et_pinggu.getText().toString());
                            diacrisis.setRegion(et_quyu.getText().toString());
                            diacrisis.setColposcopy(et_yindaojin.getText().toString());
                            diacrisis.setSuspected(et_nizhen.getText().toString());
                            diacrisis.setAttention(et_yijian.getText().toString());
                            diacrisis.setBianjie(et_bianjie.getText().toString().trim());
                            diacrisis.setColor(et_color.getText().toString().trim());
                            diacrisis.setCusuan(et_cusuan.getText().toString().trim());
                            diacrisis.setDianranse(et_dianranse.getText().toString().trim());
                            diacrisis.setXueguna(et_xueguan.getText().toString().trim());
                            diacrisis.setHandle(et_handle.getText().toString().trim());
                            diacrisis.save();
                            dialogInterface.cancel();
                        }
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
    }
    private void initBianhao(){
        List<Diacrisis>diacrisisList=DataSupport.findAll(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            diaId=diacrisisList.get(diacrisisList.size()-1).getDiaId();
        }
    }
    //采集图片路径设置
    private void buildPath(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.setting_save_Route)); //设置对话框标题
        final EditText edit = new EditText(getContext());
        edit.setEnabled(true);
        builder.setView(edit);
        if(!OneItem.getOneItem().getGather_path().equals("")||OneItem.getOneItem().getGather_path()!=null){
            edit.setText(OneItem.getOneItem().getGather_path());
        }
        edit.setText(  edit.getText().toString().trim());
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Path picpath=new Path();//图片保存路径数据表
                system= DataSupport.findAll(SystemSet.class);
                SystemSet system1 = null;
                String path=edit.getText().toString().trim();
                picpath.setPicPath(path);//每输入一个路径都保存到数据库
                picpath.save();
                if(system.size()==0){//数据为空时创建表
                    system1=new SystemSet();
                    system1.setGather_path(edit.getText().toString().trim());
                }else {
                    for(int i=0;i<system.size();i++){
                        system1=system.get(0);
                        system1.setGather_path(path);
                    }
                }
                system1.save();
                OneItem.getOneItem().setGather_path(path);
                if(!new File(UpImgfilePath).exists()){
                    new File(UpImgfilePath).mkdirs();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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
    private void initData() {
        list = new ArrayList<>();
        list.add(getString(R.string.setting_User_switching));
        list.add(getString(R.string.setting_User_management));
        list.add(getString(R.string.setting_Modify_password));
        list.add(getString(R.string.setting_wifi));
        list.add(getString(R.string.setting_Networking_test));
        list.add(getString(R.string.setting_add_terms));
        list.add(getString(R.string.setting_advanced_setting));
        list.add(getString(R.string.setting_Picture_editor));
        list.add(getString(R.string.setting_data_backup));
        list.add(getString(R.string.setting_data_recovery));
        list.add(getString(R.string.setting_Parameter_configuration));
//        list.add(getString(R.string.setting_removal));
//        list.add(getString(R.string.setting_Version_update));
        list.add(getString(R.string.setting_about_us));
        list.add(getString(R.string.setting_Common_problem));
        list.add("");

        if (screenInches > 6.0) {
            mItemLayoutId = R.layout.listview_item;
        } else {
            mItemLayoutId = R.layout.listview_item_phone;
        }
        /**
         * CommonAdapter 是通用的adapter
         */
        lv.setAdapter(new CommonAdapter<String>(getContext(), list, mItemLayoutId) {
            @Override
            public void convert(ViewHolder helper, String item) {

                helper.setText(R.id.listview_tv_item, item);
//                return null;
            }
        });
        new Item().setListViewHeightBasedOnChildren(lv);
    }
    private void initDataAdmin() {
        list = new ArrayList<>();
        list.add(getString(R.string.setting_User_switching));
        list.add(getString(R.string.setting_Modify_password));
        list.add(getString(R.string.setting_wifi));
        list.add(getString(R.string.setting_Networking_test));
        list.add(getString(R.string.setting_add_terms));
        list.add(getString(R.string.setting_Picture_editor));
        list.add(getString(R.string.setting_data_backup));
        list.add(getString(R.string.setting_Parameter_configuration));
//        list.add("蓝牙连接");
//        list.add(getString(R.string.setting_Version_update));
        list.add(getString(R.string.setting_about_us));
        list.add(getString(R.string.setting_Common_problem));
        list.add("");

        SettingAdapter adapter = new SettingAdapter(list,getContext(),screenInches);
        lv.setAdapter(adapter);
        new Item().setListViewHeightBasedOnChildren(lv);
    }

    private void initItemClickAdmin() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;

                switch (position) {
                    case 0:
                        Const.isSave=true;
                        intent=new Intent(getContext(), LoginActivity.class);//切换用户
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case 1://修改密码
                        intent=new Intent(getContext(), XiugaiPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 2://打印机,视珍宝wifi设置
                        intent=new Intent(getContext(),WifiSettingActivity.class);
                        intent.putExtra("wifiIndex", 0);
                        startActivity(intent);
                        break;
                    case 3://wifi联网测试
                        intent=new Intent(getContext(),WIFITestActivity.class);
                        Const.dialogshow=0;
                        startActivity(intent);
                        break;
                    case 4://添加术语
                        lv_addDia();
                        break;
                    case 5://图片编辑
                        intent=new Intent(getContext(), JianjiActivity.class);
                        Const.dialogshow=0;
                        startActivity(intent);
                        break;
                    case 6://备份
                        initBackUpDialog();
                        break;
                    case 7://视诊宝硬件参数配置
                        intent=new Intent(getContext(), YinjianActivity.class);
                        startActivity(intent);
                        break;

//                    case 8://版本更新
//                        boolean ab=isNetworkAvailable();//判断wifi是否可以连接
//                        if(ab){
//                            Localversion= new GuanyuActivity().getVersionName(getContext());
//                            temp=2;
//                            getUrl("http://www.baidu.com");
//                        }else {
//                            Toast.makeText(getContext(), R.string.setting_network_Unavailability, Toast.LENGTH_SHORT).show();
//                        }
//                        break;
                    case 8://关于
                        intent=new Intent(getContext(), GuanyuActivity.class);
                        startActivity(intent);
                        break;
                    case 9://帮助
                        intent=new Intent(getContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void init(View view) {
        backup=new Backup(getContext(),localsn);
        customConfirmDialog = new CustomConfirmDialog(getContext());
        customConfirmDialog1=new CustomConfirmDialog(getContext());
        devConnect= new DevConnect(getContext(),handler);
        OneItem.getOneItem().setTemp(false);
        title_text= (TextView) view.findViewById(R.id.title_text);
        lv = (ListView) view.findViewById(R.id.lv_four);
        bt_right= (Button) view.findViewById(R.id.btn_right);
        title_text.setText(getString(R.string.setting_title)+"("+OneItem.getOneItem().getName()+")");
        bt_right.setVisibility(View.INVISIBLE);
    }
    private void initView(View view){
        tv_name = new String[]{ getContext().getString(R.string.print_cytology), getContext().getString(R.string.print_HPV_DNA),getContext().getString(R.string.print_symptom),
                getContext().getString(R.string.print_Overall_assessment), getContext().getString(R.string.print_Lesion_area), getContext().getString(R.string.print_colposcopic),
                getContext().getString(R.string.print_Suspected), getContext().getString(R.string.print_attention), getContext().getString(R.string.print_opinion),
                getContext().getString(R.string.print_border),  getContext().getString(R.string.print_color), getContext().getString(R.string.print_blood_vessel),
                getContext().getString(R.string.print_Iodine_staining), getContext().getString(R.string.print_Acetic_acid_change)};
        et_xibaoxue= (EditText) view.findViewById(R.id.et_xibaoxue);
        et_dna= (EditText) view.findViewById(R.id.et_dna);
        et_zhengzhuang= (EditText) view.findViewById(R.id.et_zhengzhuang);
        et_pinggu= (EditText) view.findViewById(R.id.et_pinggu);
        et_quyu= (EditText) view.findViewById(R.id.et_quyu);
        et_yindaojin= (EditText) view.findViewById(R.id.et_yindaojin);
        et_yijian= (EditText) view.findViewById(R.id.et_yijian);
        et_nizhen= (EditText) view.findViewById(R.id.et_nizhen);
        et_bianjie= (EditText) view.findViewById(R.id.et_bianjie);
        et_xueguan= (EditText) view.findViewById(R.id.et_xueguan);
        et_color= (EditText) view.findViewById(R.id.et_color);
        et_dianranse= (EditText) view.findViewById(R.id.et_dianranse);
        et_cusuan= (EditText) view.findViewById(R.id.et_cusuan);
        et_handle= (EditText) view.findViewById(R.id.et_handle);
        tv_01= (TextView) view.findViewById(R.id.tv_01);
        tv_02= (TextView) view.findViewById(R.id.tv_02);
        tv_03= (TextView) view.findViewById(R.id.tv_03);
        tv_04= (TextView) view.findViewById(R.id.tv_04);
        tv_05= (TextView) view.findViewById(R.id.tv_05);
        tv_06= (TextView) view.findViewById(R.id.tv_06);
        tv_07= (TextView) view.findViewById(R.id.tv_07);
        tv_08= (TextView) view.findViewById(R.id.tv_08);
        tv_09= (TextView) view.findViewById(R.id.tv_09);
        tv_10= (TextView) view.findViewById(R.id.tv_10);
        tv_11= (TextView) view.findViewById(R.id.tv_11);
        tv_12= (TextView) view.findViewById(R.id.tv_12);
        tv_13= (TextView) view.findViewById(R.id.tv_13);
        tv_14= (TextView) view.findViewById(R.id.tv_14);
        TextView[] tvData = {tv_01, tv_02, tv_03, tv_04, tv_05, tv_06, tv_07, tv_08, tv_09, tv_10, tv_11, tv_12, tv_13, tv_14};
//        if (screenInches > 6.0) {
        for (int i=0;i<tv_name.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tv_name[i], 5));
            }
        }
    }
    private void initFTP(View view){
        et_ftpid= (EditText) view.findViewById(R.id.et_ftpip);
        et_ftpport= (EditText) view.findViewById(R.id.et_ftpport);
        et_ftpname= (EditText) view.findViewById(R.id.et_ftpname);
        et_ftppassword= (EditText) view.findViewById(R.id.et_ftppassword);
    }
    private void lv_add(){
        dialogList=new ArrayList<>();
        dialogList.add(getString(R.string.setting_hospital_modify));
        dialogList.add(getString(R.string.setting_department_modify));
        dialogList.add(getString(R.string.setting_FTP));
        dialogList.add(getString(R.string.setting_picture_Route));
        dialogList.add(getString(R.string.setting_FTP_Route));
        dialogList.add(getString(R.string.setting_local_route));
//        dialogList.add(getString(R.string.setting_Artwork_threshold));
//        dialogList.add(getString(R.string.setting_Lipiodol_threshold));
        dialogList.add(getString(R.string.setting_dialog_dissmiss));
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(getContext());//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,dialogList);
        listView.setAdapter(arrayAdapter);
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.setting_please_change)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2){
                    case 0:
                        HosName(getContext());
                        break;
                    case 1:
                        Departments(getContext());
                        break;
                    case 2:
                        initFTPSET(1);
                        break;
                    case 3:
                        buildPath();
                        break;
                    case 4:
                        initFTPBackUpPath();
                        break;
                    case 5:
                        initBackUpPath();
                        break;
                    case 6:
                        initDialog_time();
//                        initThreshold(1,getString(R.string.setting_Artwork_threshold_hint));
                        break;
//                    case 7:
//                        initThreshold(2,getString(R.string.setting_Lipiodol_threshold_hint));
//                        break;
//                    case 8:
//                        initDialog_time();
//                        break;
                    default:break;
                }
                dialog.cancel();
            }
        });
    }
    private void initFTPBackUpPath(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.setting_FTP_Route_hint)); //设置对话框标题
        final EditText edit = new EditText(getContext());
        builder.setView(edit);
        if(!OneItem.getOneItem().getBackUpNetPath().equals("")||OneItem.getOneItem().getBackUpNetPath()!=null){
            edit.setText(OneItem.getOneItem().getBackUpNetPath());
        }
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                system= DataSupport.findAll(SystemSet.class);
                SystemSet system1 = null;
                String path=edit.getText().toString().trim();
                if(system.size()==0){//数据为空时创建表
                    system1=new SystemSet();
                    system1.setBackUpNetPath(path);
                }else {
                    for(int i=0;i<system.size();i++){
                        system1=system.get(0);
                        system1.setBackUpNetPath(path);
                    }
                }
                system1.save();
                OneItem.getOneItem().setBackUpNetPath(path);
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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
    private void initBackUpPath(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.setting_local_Route_hint)); //设置对话框标题
        final EditText edit = new EditText(getContext());
        builder.setView(edit);
        if(!OneItem.getOneItem().getBackUpPath().equals("")||OneItem.getOneItem().getBackUpPath()!=null){
            edit.setText(OneItem.getOneItem().getBackUpPath());
        }
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                system= DataSupport.findAll(SystemSet.class);
                SystemSet system1 = null;
                String path=edit.getText().toString().trim();
                if(system.size()==0){//数据为空时创建表
                    system1=new SystemSet();
                    system1.setBackUpPath(path);
                }else {
                    for(int i=0;i<system.size();i++){
                        system1=system.get(0);
                        system1.setBackUpPath(path);
                    }
                }
                system1.save();
                OneItem.getOneItem().setBackUpPath(path);
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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


    private void initDialog_time(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.setting_dialog_dissmiss_hint));
        final EditText edit = new EditText(getContext());
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edit);
        List<SystemSet> system= DataSupport.findAll(SystemSet.class);
        if(system.size()>0){
            for(int j=0;j<system.size();j++){
                edit.setText(system.get(0).getDialog_time()+"");
            }
        }
        builder.setPositiveButton(getString(R.string.graffiti_enter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int temp;
                if(edit.getText().toString().trim()!=null&&!edit.getText().toString().trim().equals("")){
                    temp=Integer.parseInt(edit.getText().toString().trim());
                }else {
                    Toast.makeText(getContext(), R.string.setting_dialog_dissmiss_faild1, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(temp>=0){
                    List<SystemSet> system= DataSupport.findAll(SystemSet.class);
                    if(system.size()>0){
                        for(int j=0;j<system.size();j++){
                            system.get(0).setDialog_time(Integer.parseInt(edit.getText().toString().trim()));
                            Const.delayTime=Integer.parseInt(edit.getText().toString().trim());
                        }
                    }
                    system.get(0).save();
                    Toast.makeText(getContext(), R.string.setting_dialog_dissmiss_success, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), R.string.setting_dialog_dissmiss_faild2, Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }
    private void initThreshold(final int category,String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title); //设置对话框标题
        final EditText edit = new EditText(getContext());
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edit);
        if (category == 1) {
            edit.setText(Const.threshold + "");
        } else {
            edit.setText(Const.thresholddiainyou+ "");
        }
//        if(!OneItem.getOneItem().getBackUpNetPath().equals("")||OneItem.getOneItem().getBackUpNetPath()!=null){
//            edit.setText(OneItem.getOneItem().getBackUpNetPath());
//        }
        builder.setPositiveButton(getString(R.string.graffiti_enter), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<SystemSet> system= DataSupport.findAll(SystemSet.class);
                SystemSet system1 = null;
                String threshold=edit.getText().toString().trim();
                if(system.size()==0){//数据为空时创建表
                    system1=new SystemSet();
                    if (category == 1) {
                        system1.setThreshold(Integer.parseInt(threshold));
                        Const.threshold = Integer.parseInt(threshold);
                    } else {
                        system1.setThresholddianyou(Integer.parseInt(threshold));
                        Const.thresholddiainyou = Integer.parseInt(threshold);
                    }
                }else {
                    for(int i=0;i<system.size();i++){
                        system1=system.get(0);
                        if (category == 1) {
                            if (threshold.equals("") || threshold == null) {
                                threshold ="0";
                            }
                            system1.setThreshold(Integer.parseInt(threshold));
                            Const.threshold = Integer.parseInt(threshold);
                        } else {
                            if (threshold.equals("") || threshold == null) {
                                threshold ="0";
                            }
                            system1.setThresholddianyou(Integer.parseInt(threshold));
                            Const.thresholddiainyou = Integer.parseInt(threshold);
                        }
                    }
                }
                system1.save();
            }
        });

        builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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



    private void initdoctor(){//将保存的json数据提取出来保存到数据库中

        new Thread(){
            @Override
            public void run() {
                super.run();
                Message message=handler.obtainMessage();
                message.obj=getFileFromSD(doctorpath);
                message.what=-2;
                handler.sendMessage(message);
            }
        }.start();
    }
    private void initRenew(){//将保存的json数据提取出来保存到数据库中

        new Thread(){
            @Override
            public void run() {
                super.run();
                Message message=handler.obtainMessage();
                message.obj=getFileFromSD(userpath);
                message.what=1;
                handler.sendMessage(message);
            }
        }.start();
    }
    public String getFileFromSD(String path){//根据传递的路径通过输入流得到数据
        String result="";
        try {
            FileInputStream fis=new FileInputStream(path);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String line="";
            while ((line=br.readLine())!=null){
                result+=line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    //提示版本更新的对话框
    private void showDialogUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=handler.obtainMessage();
                message.what=7;
                // 下载
                try {
                    File versionfile=new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath()+File.separator+"banben.txt");
                    if(versionfile.exists()){
                        versionfile.delete();//将上次保存的版本信息删除，防止本次数据下载失败
                    }
                    OneItem.getOneItem().setSelectFTP(0);
                    //单文件下载
                    new FTP().downloadSingleFile("/LUFAYUAN/wwwroot/uploadfiles/banben/banben.txt",new Item().getSD()+"/"+OneItem.getOneItem().getBackUpPath()+"/","banben.txt",new FTP.DownLoadProgressListener(){
                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            if(currentStep.equals(Item.FTP_DOWN_SUCCESS)){
                                Log.e("msg","我是一");
                            } else if(currentStep.equals(Item.FTP_DOWN_LOADING)){
                                Log.e("msg","我是二");
                            }
                        }
                    });
                    File file=new File(Environment.getExternalStorageDirectory().toString() + File.separator + OneItem.getOneItem().getBackUpPath());
                    if(file.exists()){
                        file.mkdirs();
                    }
                    String banben= getFileFromSD(banbenpath);
                        JSONObject jsonObject=new JSONObject(banben);
                        JSONArray array=jsonObject.getJSONArray("version");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            versionCode=obj.getString("versionCode");
                            versionName=obj.getString("versionName");
                            updatapath=obj.getString("updatapath");
                        }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                handler.sendMessage(message);
            }
        }).start();

    }

    private void showDialog(){
        Log.e("msg","我是三");
        if(Double.valueOf(Localversion)<Double.valueOf(versionCode)){

            //设立的属性可以一直设置，因为每次设置后返回的是一个builder 对象
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            //设置提示框的标题
            builder.setTitle(getString(R.string.setting_Version_updata));
            //设置提示框的图标
            builder.setIcon(R.mipmap.ic_launcher);
            //设置要显示的信息
            builder.setMessage(getString(R.string.setting_Version_updata_message));
            //设置确定按钮
            builder.setPositiveButton(getString(R.string.graffiti_enter), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //下载最新的 版本程序
                    Log.d(TAG, "showDialogUpdate 版本更新的对话框: ");
                    UpdaterConfig config = new UpdaterConfig.Builder(getActivity())
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.setting_Version_updata))
                            .setFileUrl(updatapath)
                            .setCanMediaScanner(true) //能否被 MediaScanner 扫描
                            .build();
                    Updater.getInstance().download(config);
                }
            });
            //设置取消按钮。null是什么都不做。并关闭对话框
            builder.setNegativeButton(getString(R.string.button_cancel), null);
            //生产对话框
            AlertDialog alertDialog = builder.create();
            //显示对话框
            alertDialog.show();
            Log.e("msg","我是四");
        }else {
            Toast.makeText(getContext(), getString(R.string.setting_Version_Newest), Toast.LENGTH_SHORT).show();
            Log.e("msg","我是五");
        }
    }
//    public void dialogViewDeleteData(final Context context) {
//
//        AlertDialog dialogView = new AlertDialog.Builder(context)
//                .setTitle(getString(R.string.setting_Restore_factory_settings))
//                .setCancelable(false)
//                .setMessage(R.string.setting_Restore_factory_settings_message)
//                .setPositiveButton(getString(R.string.graffiti_enter), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        mStatess = true;
//                        if (mWifiManager == null) {
//                            mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);//wifi管理器
//                        }
//                        WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(mWifiManager,getActivity().getApplicationContext());
//                        wifiAutoConnectManager.connect(Const.nameShiZhenBao, Const.passShiZhenBao, Const.typeWifi);
//                        //Const.wifiRepeat :表示wifi已经连接好了，不需要再进行连接了
//                        if (!Const.wifiRepeat) {
//                            lod = LoadingDialog.getInstance(getContext());
//                            lod.setMessage(getString(R.string.setting_connectwifi));
//                            lod.dialogShow();
//                        } else {
//                            DevConnect devConnect = new DevConnect(getContext(),handler);
//                            devConnect.searchDev();
//                        }
//
//
////                        DevConnect devConnect = new DevConnect(context,handler);
////                        devConnect.searchDev();
//                    }
//                })
//                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        mStatess = false;
//                    }
//                })
//                .create();
//
//        dialogView.show();
//    }

    public void getUrl(final String strurl){//测试网络是否可用与上网

//        devConnect.getwifi();
        new Thread(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               new Runnable() {
            @Override
            public void run() {
//                int counts=0;
//                while (counts<2){
                    try {
                        url=new URL(strurl);
                        urlConnection= (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(5000);//设置连接超时时间
                        state=urlConnection.getResponseCode();
                        if(state==302){
//                            temp=0;
                            Message mess=handler.obtainMessage();
                            mess.what=0;
                            handler.sendMessage(mess);
                            return;
                        }
//                        else {
//                            counts++;
//                            continue;
//                        }
                    } catch (Exception e) {
//                        temp=-1;
                        Message mess=handler.obtainMessage();
                        mess.what=-1;
                        handler.sendMessage(mess);
                        return;
                    }
//                }
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mStatess = false;
        Logger.e("onPause FragSetting");
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);

    }

    @Override
    public void wifiConnectSuccess(int state) {
//        if (lod != null) {
//            lod.dismiss();
//        }
//        Logger.e(" 系统设置的wifi mStatess = "+mStatess+"   state = "+state);
//        if (state == 1&&mStatess) {
//
//            DevConnect devConnect = new DevConnect(getContext(),handler);
//            devConnect.searchDev();
//        }
    }

    @Override
    public void wifiConnectFaile(int state) {

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
////            if ((System.currentTimeMillis() - exitTime) > 2000) {
//////                Toast.makeText(this, R.string.quit_program, Toast.LENGTH_SHORT).show();
////                exitTime = System.currentTimeMillis();
////            } else {
////                finish();
////            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }


}