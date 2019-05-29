package com.shizhenbao.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manager.DataManager;
import com.activity.LiveVidActivity;
import com.activity.R;
import com.model.DevModel;
import com.shizhenbao.UI.WifiLoadingAnim;
import com.shizhenbao.activity.WifiSettingActivity;
import com.shizhenbao.adapter.SelectAdapter;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.FileCopyUtils;
import com.shizhenbao.util.InstallApkUtils;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.shizhenbao.wifiinfo.WifiConnectManager;
import com.southtech.thSDK.lib;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//import com.adapter.ListDevAdapter;

/**
 * Created by dell on 2017/5/22.
 */

public class FragGetImage extends BaseFragment implements View.OnClickListener,
        WifiConnectManager.WifiConnectListener {
    private static String TAG = "TAG_FragGetImage";
    private Button btn_left, btn_right;
    private TextView tv_title, tv_fragimage;
    private CreateFileConstant filePath;
    private Uri imageUri;
    private static final int TAKE_PHOTO = 1;//相机
    private static final int TAKE_PICTURE = 2;//android 7.0
    DataManager dataManager = DataManager.getInstance();
    String searchMsg;
    public static List<DevModel> listnode = new ArrayList<DevModel>();//有多少设备
    static List<DevModel> listnode_search = new ArrayList<DevModel>();//后台搜索出的设备
    LoadingDialog mDialog;
    AlertDialog dialogView = null;
    private WifiLoadingAnim wifiLoadingAnim;
    boolean searchInBackground = false;// true 表示是否正在搜索视珍宝,false :表示可以搜索，或者搜索完毕，可以进行下一次搜索
    //    final OkHttpClient client = new OkHttpClient();
    private static boolean mState = false;//检测是否是当前页面
    AlertDialog dialogadd = null;//选择患者对话框
    int num = 0;//判断翻页次数
    double temp;//患者信息所占页数
    List<Item> listItem = new ArrayList<Item>();
    private List<User> userList = new ArrayList<>();
    //    HandlerThread thread = null;
    //    Handler mHandler = null;
    private List<Item> list1;
    private List<User> userlist = new ArrayList<>();//患者信息集合
    View view;
    private String SZB_WIFI_NAME = "";
    private String SZB_WIFI_PASS = "";
    private InstallApkUtils installApkUtils;
    private FileCopyUtils fileCopyUtils;
    Runnable runnable_fresh = new Runnable() {
        @Override
        public void run() {
            searchMsg = lib.jthNet_SearchDev_old();
            //搜索到设备，然后进行处理
            ipcHandler.updateSearch();

        }
    };
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                if (dialogadd != null && dialogadd.isShowing()) {
                    dialogadd.dismiss();
                    MyToast.showToast(getActivity(),getString(R.string.image_dialog_automatic));
//                    SouthUtil.showToast(getActivity(), getString(R.string.image_dialog_automatic));

                    Const.saveImageFilePath = userlist.get(0).getGatherPath();
                    Log.e("getImage",Const.saveImageFilePath);
                    SPUtils.put(getContext(),Const.patientKey,Const.saveImageFilePath);

                    Intent intent = new Intent(getContext().getPackageManager().getLaunchIntentForPackage(Const.imageApkPackage));
                    startActivity(intent);

                }
            }
        }
    };

    private static int TIME_WIFI_CONNECT = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_layout_getimage1, container, false);

        init(view);
        setUp();
        return view;
    }

    private void init(View view) {
        btn_left = (Button) view.findViewById(R.id.btn_left);//返回按钮，，暂时被隐藏了
        btn_right = (Button) view.findViewById(R.id.btn_right);//这个是菜单按钮
        tv_title = (TextView) view.findViewById(R.id.title_text);//这个是标题栏
        tv_fragimage = (TextView) view.findViewById(R.id.tv_fragimage); //显示wifi连接的状态
        tv_title.setText(getString(R.string.image_get_image));
        btn_left.setText(getString(R.string.image_camera));
        btn_right.setText(getString(R.string.image_search));
        btn_left.setVisibility(View.GONE);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
//        btn_right.setVisibility(View.GONE);
//        btn_left.setVisibility(View.VISIBLE);
        filePath = new CreateFileConstant();
        fileCopyUtils = new FileCopyUtils(getContext());
//        list = (ListView) view.findViewById(R.id.list);
        installApkUtils = new InstallApkUtils(getContext());
        wifiLoadingAnim = (WifiLoadingAnim) view.findViewById(R.id.wifiLoadingAnim);
        if (listnode_search == null) {
            listnode_search = new ArrayList();
        }
        if (listnode == null) {
            listnode = new ArrayList();
        }
    }

    private void setWifiText(String msg) {
        if (tv_fragimage != null) {
            tv_fragimage.setText(msg);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_right: //搜索按钮
                //设置wifi的dialog 只跳出来一次
                String count = (String) SPUtils.get(getActivity(), Const.LOGINCOUNT, "0");
                if (!count.equals("1")) {
                    dialogViewWifi();
                } else {
                    SZB_WIFI_NAME = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_SSID_KEY, "");
                    SZB_WIFI_PASS = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_PASS_KEY, "12345678");
//                    WifiConnectManager.getInstance().setActivity(getActivity());
                    WifiConnectManager.getInstance().connectWifi(SZB_WIFI_NAME, SZB_WIFI_PASS, Const.WIFI_TYPE_SZB, this);
                }
                break;

            case R.id.btn_left://照相机按钮

                boolean b = hasPermission(CreateFileConstant.HARDWEAR_CAMERA_PERMISSION);
                if (b) {
                    doOpenCamera();
                } else {
                    requestPermission(CreateFileConstant.HARDWEAR_CAMERA_CODE, CreateFileConstant.HARDWEAR_CAMERA_PERMISSION);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "视诊宝界面 onResume: " );
        try {
            listnode = dataManager.getAllDev();

        } catch (Exception e) {
            e.printStackTrace();
        }
        fileCopyUtils.selectFile();

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("tag", "onStart() Const.FIRSTSHOW = "+Const.FIRSTSHOW);
//        initDataTime();
        SZB_WIFI_NAME = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_SSID_KEY, "");
        SZB_WIFI_PASS = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_PASS_KEY, "12345678");
//        int pagerCount = OneItem.getOneItem().getViewPagerCount();
        if (Const.FIRSTSHOW) {
            try {
                Const.FIRSTSHOW = false;
                listnode = dataManager.getAllDev();
                mState = true;
                //开始 连接wifi
//                WifiConnectManager.getInstance().setActivity(getActivity());
                WifiConnectManager.getInstance().connectWifi(SZB_WIFI_NAME, SZB_WIFI_PASS, Const.WIFI_TYPE_SZB, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
            stopAnimWifi();
        dismissDiolog();
        lib.jthNet_StopPlay(0);
        WifiConnectManager.getInstance().stopThreadConnectWifi();
    }

    /*
     * 设置逻辑功能
     */
    public void setUp() {

        listnode = dataManager.getAllDev();
    }

    public void setIp(String msg) throws UnsupportedEncodingException {
        searchMsg = msg;
    }

    public IPCHandler ipcHandler = new IPCHandler(getContext());
    private GetThread mThread = null;

    void searchDev() {
        try {

            if (!searchInBackground) {
                showDiolog(getString(R.string.image_dialog_loading));
                mThread = new GetThread();
                mThread.start();
            } else {
                showDiolog(getString(R.string.gettingDevStatus));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //跳转到 LiveVidActivity
    private void intentToActivity(DevModel model) {
        Intent intent = new Intent(getActivity(), LiveVidActivity.class);
        intent.putExtra("devModel", model);
        getActivity().startActivity(intent);
    }


    //当前 fragment 可见和不可见时，会调用这个方法
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        Log.e(TAG, "onFragmentVisibleChange.isVisible = " + isVisible);


        if (OneItem.getOneItem().getDialog() != null) {
            OneItem.getOneItem().getDialog().dismiss();
        }
        if (isVisible) {
            searchInBackground = false;
            mState = true;//true ;表示正在当前页面
            //设置wifi的dialog 只跳出来一次
            String count = (String) SPUtils.get(getActivity(), Const.LOGINCOUNT, "0");
            if (!count.equals("1")) {
                dialogViewWifi();
            } else {
//                int pagerCount = OneItem.getOneItem().getViewPagerCount();
                SZB_WIFI_NAME = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_SSID_KEY, "");
                SZB_WIFI_PASS = (String) SPUtils.get(getActivity(), Const.SZB_WIFI_PASS_KEY, "12345678");
                WifiConnectManager.getInstance().connectWifi(SZB_WIFI_NAME, SZB_WIFI_PASS, Const.WIFI_TYPE_SZB, this);
            }
        } else {
            mState = false;
            stopAnimWifi();
            WifiConnectManager.getInstance().stopThreadConnectWifi();
            if (dialogView != null) {
                dialogView.dismiss();
            }
            if (dialogadd != null) {
                dialogadd.cancel();
            }
        }
    }


    private void showDiolog(String msg) {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.setMessage(msg);
        } else {
            if (mDialog == null) {
                mDialog = new LoadingDialog(getActivity(), true);
            }
            mDialog.setMessage(msg);
            mDialog.dialogShow();
        }
    }


    private void dismissDiolog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    private void startAnimWifi() {
        if (wifiLoadingAnim != null) {
            wifiLoadingAnim.setVisibility(View.VISIBLE);
            wifiLoadingAnim.startAnim();
        }

    }

    private void stopAnimWifi() {
        if (wifiLoadingAnim != null) {
            wifiLoadingAnim.stopAnim();
            wifiLoadingAnim.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断是否已安装图像获取软件
     */
    private void isInstalled(){
        PackageInfo packageInfo = null;//管理已安装app包名
        packageInfo = installApkUtils.isInstallApk(Const.imageApkPackage);
        if (packageInfo == null) {//判断是否安装惠普打印机服务插件

            try {
                boolean isApkExists = installApkUtils.copyAPK2SD(Const.imageApkName);
                if(isApkExists){
                    installApkUtils.installApk(getActivity(), Environment.getExternalStorageDirectory() + "/" + Const.imageApkName);
                }else {
                    MyToast.showToast(getActivity(),getString(R.string.apkinstallfaild));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            selectUser(0);
        }
    }

    @Override //开始连接wifi
    public void startWifiConnecting(String type) {
        Log.e(TAG, "视诊宝界面 startWifiConnecting: type = "+type );
        if (type.equals(Const.WIFI_TYPE_SZB) && mState) {
            startAnimWifi();
            setWifiText(getString(R.string.wifiProcessMsg));
        }
    }

    @Override //wifi连接成功
    public void wifiConnectSuccess(String type) {
        Log.e(TAG, "视诊宝界面 wifiConnectSuccess: type = "+type+" , mState = "+mState );
        if (type.equals(Const.WIFI_TYPE_SZB) && mState) {
            stopAnimWifi();
            setWifiText("");

            isInstalled();

        }
    }

    @Override  //wifi连接失败
    public void wifiConnectFalid(String type) {
        Log.e(TAG, "视诊宝界面 wifiConnectFalid: type = "+type+" , mState = "+mState );
        if (type.equals(Const.WIFI_TYPE_SZB) && mState) {
            stopAnimWifi();
            setWifiText(getString(R.string.wifiFailMsg));
        }
    }

    @Override //没有搜索到指定的wifi， ，所以需要循环搜索
    public void wifiCycleSearch(String type, boolean isSSID,int count) {
        Log.e(TAG, "视诊宝界面 wifiCycleSearch: type = "+type+" , mState = "+mState+" , isSSID = "+isSSID );
        if (type.equals(Const.WIFI_TYPE_SZB) && mState) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSSID) {
                        setWifiText(getString(R.string.wifiProcessMsg));
                      WifiConnectManager.getInstance().connectWithWpa(SZB_WIFI_NAME,SZB_WIFI_PASS);
                    } else {
                        setWifiText(getString(R.string.wifi_no_eixst));
                    }

                }
            });
        }
    }

    @Override //wifi的 名称为空
    public void wifiInputNameEmpty(String type) {
        if (type.equals(Const.WIFI_TYPE_SZB) && mState) {
            stopAnimWifi();
            setWifiText(getString(R.string.wifi_SZBname_empty));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    class GetThread extends Thread {

        public GetThread() {

        }

        @Override
        public void run() {
                searchInBackground = true;
                searchMsg = lib.jthNet_SearchDev_old();
                Log.e(TAG, "run: searchMsg = " + searchMsg);
                ipcHandler.updateSearch();

        }
    }

    class IPCHandler extends Handler {
        //        int length;
        Context context;

        IPCHandler(Context context1) {
            context = context1;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    dismissDiolog();
                    break;
                case 0:

                    /**
                     * 搜到设备，做以下处理
                     */
                    if (!searchMsg.equals("")) {
                        if (listnode == null) {
                            listnode = new ArrayList();
                        }
                        String[] devArray = searchMsg.split("@");
                        if (devArray.length == 0) return;
                        //add 0407 搜索设备全部删除
                        if (!dataManager.deleteAllDev()) {
                            MyToast.showToast(getActivity(),getString(R.string.dbExecFailed));
//                            SouthUtil.showToast(getActivity(), getString(R.string.dbExecFailed));
                            return;
                        }
                        listnode.clear();
                        for (int i = 0; i < devArray.length; i++) {
                            String[] array = devArray[i].split(",");
                            if (array.length < 6) {
                                continue;
                            }
                            DevModel node = new DevModel();
                            node.sn = array[1];
                            node.ip = array[0];
                            node.online = DevModel.EnumOnlineState.Online;
                            node.dataport = array[2];
                            node.httpport = array[3];
                            node.name = array[5];

                            if (!dataManager.addDev(node)) {
                                MyToast.showToast(getActivity(),getString(R.string.dbExecFailed));
//                                SouthUtil.showToast(getActivity(), getString(R.string.dbExecFailed));
                                return;
                            }
                            listnode.add(node);

                        }
                        //跳转到 LiveVidActivity
                        intentToActivity(listnode.get(0));

                    } else {
                        MyToast.showToast(getActivity(),getString(R.string.nodevice));
//                        SouthUtil.showToast(getActivity(), getString(R.string.nodevice));
                    }
                    searchInBackground = false;
                    dismissDiolog();
                    break;
                default:
                    break;
            }
        }

        public void updateSearch() {
            sendMessage(Message.obtain(ipcHandler, 0));
        }


    }

    ;


    //获取本地摄像头
    String nameId = CreateFileConstant.getCurrentTime();

    @Override
    public void doOpenCamera() {
        //创建File对象 ，用于存储拍照后的图片
        filePath.initCreateDocPath("Camera");
        File outputImage = null;
        String path = new Item().getSD() + "/" + OneItem.getOneItem().getGather_path() + "/" + OneItem.getOneItem().getName() + "/Camera";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!path.equals("")) {
            outputImage = new File(path, "image_" + nameId + ".jpg");
            if (Build.VERSION.SDK_INT >= 24) {//这个任意一个字节就可以了
                imageUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 24) {
            //启动相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else { //android 7.0
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, outputImage.getAbsolutePath());
            Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片取出来
                }
                break;

            case TAKE_PICTURE://当系统是android 7.0 时
                break;
        }
    }

    /**
     * 弹出WiFi设置的对话框
     */
    private void dialogViewWifi() {
        if (dialogView == null) {
            dialogView = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.image_set_wifi)
                    .setCancelable(false)
                    .setMessage(R.string.image_set_wifi_message)
                    .setPositiveButton(R.string.image_set_wifi_sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //跳到wifi设置界面
                            Intent intent = new Intent(getActivity(), WifiSettingActivity.class);
                            intent.putExtra("wifiIndex", 1);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.image_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
        }
        dialogView.show();
    }


    private void selectUser(int yeshu) {
        Log.e(TAG, "selectUser: 22222" );
        if (OneItem.getOneItem().getIntImage() == 1) {
            //查找医生名下的患者的个数
//        Logger.e("到了select");
            try {
                list1 = new ArrayList<>();
                userList.clear();
                listItem.clear();
                userlist.clear();
                try {
                    if (OneItem.getOneItem().getName() != null) {
                        if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                            userList = LitePal.findAll(User.class);
                        } else {
                            userList = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                        }
                    } else {
                        new UserManager().getExceName();
                        if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                            userList = LitePal.findAll(User.class);
                        } else {
                            userList = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                        }
                    }
                } catch (Exception e) {
                    new UserManager().getExceName();
                    if (new LoginRegister().getDoctor(OneItem.getOneItem().getName()).isdAdmin()) {
                        userList = LitePal.findAll(User.class);
                    } else {
                        userList = LitePal.where("operId=?", String.valueOf(new LoginRegister().getDoctor(OneItem.getOneItem().getName()).getdId())).find(User.class);
                    }
                }
                if (userList.size() > 0) {
                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).getIs_diag() == 1) {
                            userlist.add(userList.get(i));
                        }
                    }
                    Const.list = userlist;
                    lv_list(yeshu);

                } else {
                    MyToast.showToast(getActivity(),getString(R.string.image_set_patients_error));
//                    SouthUtil.showToast(getActivity(), getString(R.string.image_set_patients_error));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Const.saveImageFilePath = new LoginRegister().getUserName(OneItem.getOneItem().getImageId()).getGatherPath();
            User mUser = new LoginRegister().getUserName(OneItem.getOneItem().getImageId());
            Const.nameJianJi = mUser.getpName();
            searchDev();
        }
    }

    public void lv_list(int yeshu) {
        list1.clear();
        double userSize = userlist.size();
        temp = userSize / 5.0;
        Const.usersize = userlist.size();
        if (temp - yeshu >= 1 || temp - yeshu == temp) {
            if (temp >= 1) {
                for (int j = yeshu * 5; j < yeshu * 5 + 5; j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            } else {
                for (int j = yeshu * 5; j < userlist.size(); j++) {
                    Item item = new Item();
                    String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                    item.setpId(userlist.get(j).getpId());
                    item.setpName(zhanshi);
                    list1.add(item);
                }
            }

        } else if (temp - yeshu < 1) {
            for (int j = yeshu * 5; j < userlist.size(); j++) {
                Item item = new Item();
                String zhanshi = getString(R.string.patient_name) + ":" + userlist.get(j).getpName() + "  " + getString(R.string.patient_age) + ":" + userlist.get(j).getAge() + "  " + getString(R.string.patient_telephone) + ":" + userlist.get(j).getTel();
                item.setpId(userlist.get(j).getpId());
                item.setpName(zhanshi);
                list1.add(item);
            }
        }
        if (list1.size() >= 1) {
            lv_additem(list1);
        } else if (list1.size() == 0) {
            MyToast.showToast(getActivity(),getString(R.string.image_set_patients_error));
//            SouthUtil.showToast(getContext(), getString(R.string.image_set_patients_error));
        }
    }

    private void lv_additem(final List<Item> list) {
        if (dialogadd != null) {
            if (dialogadd.isShowing()) {
                dialogadd.dismiss();
                dialogadd = null;
            }
        }
        LinearLayout linearLayoutMain = new LinearLayout(getContext());//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(getContext());//this为获取当前的上下文
        listView.setFadingEdgeLength(0);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SelectAdapter selectAdapter = new SelectAdapter(getContext(), list);
        listView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        linearLayoutMain.addView(listView);//往这个布局中加入listview
        dialogadd = new AlertDialog.Builder(getContext()).setTitle(R.string.image_Select_patients).setView(linearLayoutMain).setNeutralButton(R.string.image_cancel, null).setPositiveButton(R.string.image_next_page, null).setNegativeButton(R.string.image_Previous_page, null).create();
        dialogadd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button bt_cancle = dialogadd.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button bt_before = dialogadd.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button bt_next = dialogadd.getButton(AlertDialog.BUTTON_POSITIVE);

                bt_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogadd.dismiss();
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
                            dialogadd.dismiss();
//                            dialogset=1;
                            lv_list(num);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.image_patients_first), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bt_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        num++;
                        if (temp > num) {
                            dialogadd.dismiss();
                            lv_list(num);
                        } else {
                            num--;
                            Toast.makeText(getContext(), getString(R.string.image_patients_last), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogadd.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialogadd.show();
        if (list.size() == 1) {
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
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (userlist.get(arg2 + num * 5).getGatherPath() != null) {

                    Const.saveImageFilePath = userlist.get(arg2 + num * 5).getGatherPath();
                    Log.e("getImage",Const.saveImageFilePath);
                    SPUtils.put(getContext(),Const.patientKey,Const.saveImageFilePath);

                    Intent intent = new Intent(getContext().getPackageManager().getLaunchIntentForPackage(Const.imageApkPackage));
                    startActivity(intent);
//                    Const.nameJianJi = userlist.get(arg2 + num * 5).getpName();
//                    searchDev();
                }
                dialogadd.cancel();
                num = 0;
            }
        });
    }


}