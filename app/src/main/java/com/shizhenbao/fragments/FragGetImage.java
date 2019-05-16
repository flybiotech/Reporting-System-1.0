package com.shizhenbao.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.UI.WifiLoadingAnim;
import com.shizhenbao.activity.WifiSettingActivity;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.wifiinfo.WifiAutoConnectManager;
import com.shizhenbao.wifiinfo.WifiReceiver;
import com.view.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;



/**
 * Created by dell on 2017/5/22.
 */

public class FragGetImage extends BaseFragment implements View.OnClickListener,
        WifiReceiver.WifiConnectinfo {
    private static String TAG = "myCarch_FragGetImage";
    private Button btn_left, btn_right,bt_image;
    private TextView tv_title;
    private CreateFileConstant filePath;
    private Uri imageUri;
    private static final int TAKE_PHOTO = 1;//相机
    private static final int TAKE_PICTURE = 2;//android 7.0
    private LoginRegister lr;
    LoadingDialog dialog;
    static final String tag = "Devlistactivity";
    AlertDialog dialogView = null;
    private WifiLoadingAnim wifiLoadingAnim;
    private static boolean mState = false;//检测是否是当前页面
    int num=0;//判断翻页次数
    double temp;//患者信息所占页数
    String packageName="com.fang.li";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_layout_getimage1, container, false);
        Log.e(TAG, "FragGetImage");
        init(view);
        return view;
    }

    private void init(View view) {
        btn_left = (Button) view.findViewById(R.id.btn_left);//返回按钮，，暂时被隐藏了
        btn_right = (Button) view.findViewById(R.id.btn_right);//这个是菜单按钮
        tv_title = (TextView) view.findViewById(R.id.title_text);//这个是标题栏
        tv_title.setText(getString(R.string.image_get_image));
        btn_left.setText(getString(R.string.image_camera));
        btn_right.setText(getString(R.string.image_search));
        bt_image= (Button) view.findViewById(R.id.bt_image);
        bt_image.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_right.setVisibility(View.VISIBLE);
        btn_left.setVisibility(View.VISIBLE);
        filePath = new CreateFileConstant();
        lr = new LoginRegister();

        wifiLoadingAnim = (WifiLoadingAnim) view.findViewById(R.id.wifiLoadingAnim);
    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    //防止短时间内多次点击
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_right: //搜索按钮
                        Const.wifiMark = true;
                        Const.isIntentFrag = true;
                        Const.isIntent = true;
                        if (mWifiManager != null) {
                            WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(mWifiManager,getActivity().getApplicationContext());
                            wifiAutoConnectManager.connect(Const.nameShiZhenBao, Const.passShiZhenBao, Const.typeWifi);
                            if (!Const.wifiRepeat) {
                                wifiLoadingAnim.setVisibility(View.VISIBLE);
                                wifiLoadingAnim.startAnim();
                            } else {
                                wifiLoadingAnim.stopAnim();
                            }
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
            case R.id.bt_image:
                new Item(getContext()).isPkgInstalled(packageName);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
        WifiReceiver.setWifiConnectionListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogView != null) {
            dialogView.dismiss();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    //wifi广播   wifi连接成功或者失败的回调方法
    @Override
    public void wifiConnectSuccess(int state) {

        Logger.e(" wifiConnectSuccess   wifi连接成功 state:"+state+"    此时 mstate的状态是 :"+mState);
        // if (state == 1&&mState)
        if (state == 1&&mState) {
            wifiLoadingAnim.stopAnim();
        }
    }

    @Override
    public void wifiConnectFaile(int state) {

    }

    //当前 fragment 可见和不可见时，会调用这个方法
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(OneItem.getOneItem().getDialog()!=null){
            OneItem.getOneItem().getDialog().dismiss();
        }
        if (isVisible) {
            Const.wifiMark = true;//这个作用： true 表示，wifi连接成功时，可以回调相关的方法
            mState = true;//true ;表示正在当前页面


            //设置wifi的dialog 只跳出来一次
            if (Const.loginCount < 2&& Const.dialogWifiSetting &&OneItem.getOneItem().getName().equals("Admin")) {
                dialogView();
            } else {
                Const.isIntentFrag = true;//这个的作用是判断当前的fragment 的是否可见，
                if (mWifiManager != null) {
                    WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(mWifiManager,getActivity().getApplicationContext());
                    wifiAutoConnectManager.connect(Const.nameShiZhenBao, Const.passShiZhenBao, Const.typeWifi);
                    if (!Const.wifiRepeat) {
                        wifiLoadingAnim.setVisibility(View.VISIBLE);
                        wifiLoadingAnim.startAnim();
                    } else {
                        wifiLoadingAnim.stopAnim();
                    }
                }
            }
        } else {
            mState = false;
            Const.isIntentFrag = false;
            if (dialogView != null) {
                dialogView.dismiss();
            }
        }
        Logger.e(" FragGetImage  当前的页面的状态 ： isVisible="+isVisible +"   mstate="+mState);
    }


    //获取本地摄像头
    String nameId =CreateFileConstant.getCurrentTime();
    @Override
    public void doOpenCamera() {
        //创建File对象 ，用于存储拍照后的图片
        filePath.initCreateDocPath("Camera");
        File outputImage = null;
        String path = new Item().getSD() + "/"+OneItem.getOneItem().getGather_path()+"/" +OneItem.getOneItem().getName()+"/Camera";
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        if (path != null) {
            outputImage = new File(path,"image_"+nameId+".jpg");
            if (Build.VERSION.SDK_INT >= 24) {//这个任意一个字节就可以了
                imageUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName()+".fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
        }
//        nameId++;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion<24){
            //启动相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }else { //android 7.0
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, outputImage.getAbsolutePath());
            Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
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
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        //暂时没有这个需求
//                        image.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case TAKE_PICTURE://当系统是android 7.0 时
                break;
        }
    }


    private void dialogView() {
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
//                            adapter.notifyDataSetChanged();
                        }
                    })
                    .create();
        }
        dialogView.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}