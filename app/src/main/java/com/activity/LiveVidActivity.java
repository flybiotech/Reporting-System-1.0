package com.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.model.DevModel;
import com.model.RetModel;
import com.network.Network;
import com.shizhenbao.UI.CustomConfirmDialog;
import com.shizhenbao.UI.MyTitleView;
import com.shizhenbao.UI.XCArcMenuView;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.southtech.thSDK.lib;
import com.util.EnumSoundWav;
import com.util.PlayVoice;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;
import com.view.VoiceImageButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LiveVidActivity extends BaseActivity implements View.OnClickListener, XCArcMenuView.OnMenuItemClickListener {

    public GlBufferView mPlayer;
    private XCArcMenuView view_xcarc;
    VoiceImageButton btn_capture, btn_record, btn_photo_manager, btn_setting, btn_back;
    private MyTitleView tx_record, tx_snap_time, tv_huanzhe, bt_claer, bt_start;
    private MyTitleView tx_xcarc;
    private LinearLayout ctl_right;
    private boolean isRecord = false;
    private boolean isSnap_time = false;
    private static String TAG = "TAG_LiveVidActivity : ";
    private int recordTotalTime = 0;
    private int recordCurrentTime = 0;
    private int snapTotalTime = 0;//总的拍摄照片的时长 ，例如180秒，240秒等等
    private int snapCurrentTime = 0;//当前计时的时间
    private int snapIntervalTime = 0; //拍照的间隔时间
    private boolean captureBackgroup = false;//拍照保存
    private int index; //
    private DevModel dev;
    private String imageName = "yuantu"; //原图的照片的命名规则
    private LoadingDialog mDialog;
    private int imageCount = 0;//标记醋酸白连续拍照时的图片的位置
    //定时刷新列表
    public int TIME = 1000;
    public boolean updateDevTimeExec = false;

    //已经在FragGetImage 中，初始化视珍宝拍照和拍摄视频的默认时间
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
//                    MyToast.showToast(LiveVidActivity.this, (String) msg.obj);

                    SouthUtil.showToastInfo(LiveVidActivity.this, (String) msg.obj);
                    break;

                case 2:
//                    MyToast.showToast(LiveVidActivity.this, (String) msg.obj);
                    SouthUtil.showToastInfo(LiveVidActivity.this, getString(R.string.saveImage));
                    showDiolog(getString(R.string.saveImage));
//                    SouthUtil.showToastInfo(LiveVidActivity.this, getString(R.string.saveImage));
                    break;

                case 3:
                    captureBackgroup = false;
//                    MyToast.showToast(LiveVidActivity.this, (String) msg.obj);
//                    showDiolog(getString(R.string.saveImageSuccess));
                    dismissDiolog();
                    SouthUtil.showToastInfo(LiveVidActivity.this, getString(R.string.saveImageSuccess));
                    break;

                case 4:
                    dismissDiolog();
                    SouthUtil.showToastInfo(context, context.getString(R.string.image_photograph_error));
                    captureBackgroup = false;

                    break;
                default:
                    break;
            }
//

        }
    };

    Runnable runnable_fresh = new Runnable() {
        @Override
        public void run() {

            snapImage();//拍照
            recordVedio();//录制视频
            handler.postDelayed(runnable_fresh, TIME);

            if (!updateDevTimeExec) {
                updateDevTimeExec = true;
                updateDevTime();
            }

        }
    };


    //拍照
    private void snapImage() {
        if (!isSnap_time) {
            return;
        }
        if (snapTotalTime > snapCurrentTime) {
            snapCurrentTime++;

            if (tx_snap_time != null) {
                tx_snap_time.setText(SouthUtil.getTimeSSMM(snapCurrentTime));
            }
            if ((snapCurrentTime % snapIntervalTime) == 0) {
//                MyToast.showToast(LiveVidActivity.this,getString(R.string.saveImage));
                SouthUtil.showToastInfo(this, getString(R.string.saveImage));
                //保存照片
                SouthUtil.setImageType(imageName);
                mPlayer.isTakePicture = true;
                SouthUtil.setCusuImageTime(snapTotalTime/60,snapCurrentTime);
                mPlayer.mHandler = handler;
                PlayVoice.playClickVoice(LiveVidActivity.this, EnumSoundWav.SNAP);//拍照的声音
            }
            isEnableClick(false, false, true, true, false);
        } else {
            isSnap_time = false;
            isEnableClick(true, true, false, false, true);
            snapCurrentTime = 0;
            if (tx_snap_time != null) {
                tx_snap_time.setText("00:00");
            }


        }

    }

    //录制视频
    private void recordVedio() {
        if (dev == null || !isRecord) {
            return;
        }

        if (recordTotalTime > recordCurrentTime) {
            recordCurrentTime++;
            tx_record.setVisibility(View.VISIBLE);

            tx_record.setText("REC:" + SouthUtil.getTimeSSMM(recordCurrentTime));
            tx_record.setTextColor("#FF69B4");
            tx_record.setBackgroundResource(R.color.bg_xcarc);
        } else {

            if (isRecord) {
                lib.jlocal_StopRec(0);//停止录制视频
//                MyToast.showToast(LiveVidActivity.this,getString(R.string.image_pickup_end));
                SouthUtil.showToastInfo(LiveVidActivity.this, getString(R.string.image_pickup_end));
            }
            recordCurrentTime = 0;
            isRecord = false;
            if (btn_record != null) {
                btn_record.setSelected(false);
            }
            tx_record.setText("");
            tx_record.setBackgroundResource(R.color.bg_xcarc);
            tx_record.setVisibility(View.GONE);
        }


    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dev = (DevModel) getIntent().getParcelableExtra("devModel");
        initView();
        onClickListenerImpl();
//        isEnableClick(true,false,false,false,true);

    }

    private void initView() {
        setContentView(R.layout.activity_live_vid);
        tv_huanzhe = (MyTitleView) findViewById(R.id.tv_huanzhe);
        tv_huanzhe.setText(Const.nameJianJi);
        tv_huanzhe.setEnabled(false);
        tv_huanzhe.setTextColor("#51516F");
        mPlayer = (GlBufferView) findViewById(R.id.player);
        bt_start = (MyTitleView) findViewById(R.id.bt_start);//开始计时
        tx_snap_time = (MyTitleView) findViewById(R.id.snap_time);
        tx_snap_time.setText("00:00");
        tx_xcarc = (MyTitleView) findViewById(R.id.id_button);
        view_xcarc = (XCArcMenuView) findViewById(R.id.arcmenu);
        view_xcarc.setOnMenuItemClickListener(this);
        mPlayer.setLongClickable(true);
        ctl_right = (LinearLayout) findViewById(R.id.ctl_right);
        btn_capture = (VoiceImageButton) findViewById(R.id.btn_capture);//拍照
        btn_record = (VoiceImageButton) findViewById(R.id.btn_record);//录像
        btn_photo_manager = (VoiceImageButton) findViewById(R.id.btn_photo_manager);//照片管理
        btn_setting = (VoiceImageButton) findViewById(R.id.btn_setting);//设置
        tx_record = (MyTitleView) findViewById(R.id.tx_record);
        btn_back = (VoiceImageButton) findViewById(R.id.btn_back);//返回按钮
        bt_claer = (MyTitleView) findViewById(R.id.bt_claer);
        bt_claer.setText(getString(R.string.image_timing_clear));
        btn_capture.setEnumSoundWav(EnumSoundWav.SNAP);
        btn_record.setEnumSoundWav(EnumSoundWav.CLICK);
        btn_photo_manager.setEnumSoundWav(EnumSoundWav.CLICK);
        btn_setting.setEnumSoundWav(EnumSoundWav.CLICK);



    }

    private void onClickListenerImpl() {
        bt_claer.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_capture.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_photo_manager.setOnClickListener(this);
        btn_setting.setOnClickListener(this);

    }

    //初始化拍照录像的时间      //默认的返回值，单位都是秒
    private void setSnapTime() {
        recordTotalTime = (int) SPUtils.get(this, Const.RECORD_TOTALTIME, 60);
        snapTotalTime = (int) SPUtils.get(this, Const.SNAP_TOTALTIME, 180);
        snapIntervalTime = (int) SPUtils.get(this, Const.SNAP_INTERVAL, 15);
    }


    /**
     * @param isCuSuanBai    选择 原图或者醋酸白或者碘油
     * @param isStartTime    开始计时
     * @param isIntervalTime 显示连续拍照时的时间
     * @param isPause        暂停计时
     * @param isbottom       底部一排按钮 是否显示
     */
    private void isEnableClick(boolean isCuSuanBai, boolean isStartTime, boolean isIntervalTime, boolean isPause, boolean isbottom) {
        tx_xcarc.setEnabled(isCuSuanBai); //选择 原图或者醋酸白或者碘油
        bt_start.setEnabled(isStartTime); //开始计时
        tx_snap_time.setEnabled(isIntervalTime);//显示连续拍照时的时间
        bt_claer.setEnabled(isPause); //暂停计时
        settingBtnEnableIsClick(isbottom); // 底部 按钮 是否显示

        if (isCuSuanBai) {
            tx_xcarc.setBackgroundResource(R.drawable.text_shape_select);

            tx_xcarc.setTextColor("#ffffffff");
        } else {
            tx_xcarc.setTextColor("#51516F");
            tx_xcarc.setBackgroundResource(R.drawable.text_shape_nor);

        }

        if (isStartTime) {
            bt_start.setTextColor("#ffffffff");
            bt_start.setBackgroundResource(R.drawable.text_shape_select);
        } else {
            bt_start.setTextColor("#51516F");
            bt_start.setBackgroundResource(R.drawable.text_shape_nor);
        }

        if (isIntervalTime) {
            tx_snap_time.setTextColor("#000000");
//            tx_snap_time.setBackgroundResource(R.drawable.text_shape_select);
        } else {
            tx_snap_time.setTextColor("#51516F");
//            tx_snap_time.setBackgroundResource(R.drawable.text_shape_nor);
        }

        if (isPause) {
            bt_claer.setTextColor("#ffffffff");
            bt_claer.setBackgroundResource(R.drawable.text_shape_select);
        } else {
            bt_claer.setTextColor("#51516F"); //深灰色
            bt_claer.setBackgroundResource(R.drawable.text_shape_nor);
        }

    }

    //视频右边的按钮
    private void settingBtnEnableIsClick(boolean isclick) {
        if (isclick) {
            btn_capture.setEnabled(true);
            btn_record.setEnabled(true);
            btn_photo_manager.setEnabled(true);
            btn_setting.setEnabled(true);
            btn_back.setEnabled(true);
            btn_capture.setVisibility(View.VISIBLE);
            btn_record.setVisibility(View.VISIBLE);
            btn_photo_manager.setVisibility(View.VISIBLE);
            btn_setting.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.VISIBLE);
        } else {
            btn_capture.setEnabled(false);
            btn_record.setEnabled(false);
            btn_photo_manager.setEnabled(false);
            btn_setting.setEnabled(false);
            btn_back.setEnabled(false);
            btn_capture.setVisibility(View.GONE);
            btn_record.setVisibility(View.GONE);
            btn_photo_manager.setVisibility(View.GONE);
            btn_setting.setVisibility(View.GONE);
            btn_back.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUp();//
        if (imageName.equals("cusuanbai")) {
            isEnableClick(true, true, false, false, true);
        } else {
            isEnableClick(true, false, false, false, true);
        }
        setSnapTime();


    }

    void setUp() {
        mPlayer.cnx = this;
        mPlayer.dev = dev;
        mPlayer.play();

    }
//    private void initData() {
//        OneItem.getOneItem().setSpTotalTime(180);//设置默认连续拍照时长
//        OneItem.getOneItem().setSpInterval(15);//设置默认连续拍照间隔时间
//        OneItem.getOneItem().setRecordTotalTime(60);//设置默认录像时长
//    }

    void initControlButtonViewParam() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SouthUtil.convertDpToPixel(50, this), LayoutParams.MATCH_PARENT);
        if (SouthUtil.getButtonIsAlignRightValue(this)) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.rightMargin = SouthUtil.convertDpToPixel(10, this);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.leftMargin = SouthUtil.convertDpToPixel(10, this);
        }

        ctl_right.setLayoutParams(layoutParams);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dev == null) {
            return;
        }
        if (!TextUtils.isEmpty(dev.ip)) {
            int port = Integer.parseInt(mPlayer.dev.dataport);
            lib.jthNet_PlayLive(0, mPlayer.dev.ip, mPlayer.dev.usr, mPlayer.dev.pwd, port, 0);
//            Log.e(TAG, "resolute is " + dev.resolute);
        }
        initControlButtonViewParam();
        handler.postDelayed(runnable_fresh, TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        lib.jlocal_StopRec(0);
        lib.jthNet_StopPlay(0); //停止连接视珍宝
        handler.removeCallbacks(runnable_fresh);

        try {
            bt_start.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //返回时
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Const.isIntent = false;

            mPlayer.stop();
            //主要用于加载wifi的动画，和从这个界面返回时，能够弹出患者信息的对话框
            OneItem.getOneItem().setViewPagerCount(3);
            this.finish();

        } //音量键＋的监听事件
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //保存照片
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                //保存图片
                SouthUtil.setImageType(imageName);
                mPlayer.isTakePicture = true;
//                SouthUtil.getSaveImageOrRecord(this, 1, Const.saveImageFilePath, imageName, handler);
                PlayVoice.playClickVoice(this, EnumSoundWav.SNAP);//拍照的声音
            }
//            SouthUtil.getSaveImageOrRecord(LiveVidActivity.this, 1, Const.saveImageFilePath, imageName);
//            PlayVoice.playClickVoice(this,EnumSoundWav.SNAP);//拍照的声音
            return true;
        }

        return false;
    }

    //防止过快点击
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    //监听事件
    @Override
    public void onClick(View v) {

        view_xcarc.close();

        switch (v.getId()) {
            case R.id.btn_capture: { //拍照
                Log.e(TAG, "onClick: captureBackgroup = "+captureBackgroup );
                if (!captureBackgroup) {
                    captureBackgroup = true;
                    SouthUtil.setImageType(imageName);
                    mPlayer.mHandler = handler;
                    PlayVoice.playClickVoice(this,EnumSoundWav.SNAP);//拍照的声音
                    SouthUtil.setCusuImageTime(0,0);
                    mPlayer.isTakePicture = true;
                }


            }
            break;

            case R.id.btn_record: { //录制视频
                if (dev == null) {
                    return;
                }
                if (!isRecord) {
                    isRecord = !isRecord;
                    btn_record.setSelected(true);
                    //保存视频的名称

                    lib.jthNet_MediaKeepAlive(0);
                    SouthUtil.saveVideo(this, Const.saveImageFilePath, imageName, recordTotalTime / 60, handler);

                } else {
                    isRecord = !isRecord;
                    int i = lib.jlocal_StopRec(0);
//                    MyToast.showToast(LiveVidActivity.this,getString(R.string.image_pickup_end));
                    SouthUtil.showToastInfo(LiveVidActivity.this, getString(R.string.image_pickup_end));
                    tx_record.setText("");
                    tx_record.setBackgroundResource(R.color.bg_xcarc);
                    tx_record.setVisibility(View.GONE);
                    recordCurrentTime = 0;
                    btn_record.setSelected(false);
                }
                Log.e(TAG, " 视频总时间 recordTotalTime: " + recordTotalTime + "  , isRecord : " + isRecord);
            }
            break;

            case R.id.btn_photo_manager: { //图片视频管理
                int i = lib.jthNet_StopPlay(0);//停止连接视珍宝
                Runnable runnable_stepActivity = new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context.getApplicationContext(), SDMediaActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                };
                handler.postDelayed(runnable_stepActivity, 100);

            }
            break;

            case R.id.btn_setting: { //视珍宝设置
                int i = lib.jthNet_StopPlay(0);//停止连接视珍宝
//            Logger.e("  停止连接视珍宝:  停止连接视珍宝 =" + i);
                Runnable runnable_stepActivity = new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context.getApplicationContext(), ConfigActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                };
                handler.postDelayed(runnable_stepActivity, 100);
            }
            break;

            case R.id.btn_back: { //返回
                Const.isIntent = false;
                //主要用于加载wifi的动画，和从这个界面返回时，能够弹出患者信息的对话框
                OneItem.getOneItem().setViewPagerCount(3);
                finish();
            }
            break;

            case R.id.bt_start: {//开始计时
                isSnap_time = true;
//                isEnableClick(false,false,true,true,false);
            }
            break;

            case R.id.bt_claer: { //暂停拍照
                //停止拍摄
                isSnap_time = false;
                isEnableClick(true, true, false, false, true);

            }
            break;
        }


    }

    public void setTextViewName(int position) {

        switch (position) {
            case 0:
                imageName = "yuantu";
                isEnableClick(true, false, false, false, true);
                break;
            case 1:
                imageName = "cusuanbai"; //醋酸白的图片的命名规则
                isEnableClick(true, true, false, false, true);
                break;
            case 2:
                imageName = "dianyou"; //碘油的图片的命名规则
                isEnableClick(true, false, false, false, true);
                break;
            default:
                break;
        }

    }



    /**
     * 设置设备时间
     */
    void updateDevTime() {
        if (dev == null) {
            return;
        }
        if (!TextUtils.isEmpty(dev.ip)) {

            Network.getCommandApi(dev).updateTime(dev.usr, dev.pwd, 17, SouthUtil.getTimeYyyymmddhhmmss(), SouthUtil.getRandom())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RetModel>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(RetModel ret) {

                        }
                    });
        }
    }

    public IPCHandler ipc = new IPCHandler();

    //改变控件的背景颜色
    @Override
    public void onChangeBackground(boolean isChange) {
        Log.i("TAG_12", "onChangeBackground: isChange = "+isChange);
        //判断是否处于展开状态
        if (isChange) {
            view_xcarc.setBackgroundColor(00000000);
        } else {

            view_xcarc.setBackgroundResource(R.drawable.text_shape_select);
        }
    }

    //选择原图，醋酸白，碘油 的回调方法
    @Override
    public void onClickMenu(View view, int pos) {
        String tag = (String) view.getTag();
        tx_xcarc.setText(tag);
        //这个pos 是从1 开始的，
        setTextViewName(pos - 1);
    }


    private void showDiolog(String msg) {
        try {
            if (mDialog != null && mDialog.isShow()) {
                mDialog.setMyMessage(msg);
            } else {
                if (mDialog == null) {
                    mDialog = new LoadingDialog(LiveVidActivity.this, "rotate");
                }
                mDialog.setMyMessage(msg);
                mDialog.dialogShow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void dismissDiolog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

    }



    class IPCHandler extends Handler {
        int length;

        IPCHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    break;

                default:
                    break;
            }
        }

        public void getFirstFrame() {
            sendMessage(Message.obtain(ipc, 0));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDiolog();
        if (isRecord) {
            //停止录像
            lib.jlocal_StopRec(0);
        }
        mPlayer = null;


    }


}


class GlBufferView extends GLSurfaceView {
    public boolean getFirstFrame = false;
    public boolean capture = true;
    public Context cnx;
    public DevModel dev;
    int glwidth;
    int glheight;
    public Bitmap bitmap;
    public boolean isTakePicture = false;
    public Handler mHandler;
    int w;
    int h;
    int bt[];
    int b[];
    IntBuffer buffer;


    public GlBufferView(Context context) {
        super(context);
        cnx = context;
        // 设置OpenGl ES的版本为2.0
        setEGLContextClientVersion(2);
        // 设置与当前GLSurfaceView绑定的Renderer
        setRenderer(new MyRenderer());
        // 设置渲染的模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        requestFocus();
        setFocusableInTouchMode(true);
        capture = true;

    }

    public GlBufferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnx = context;
        setRenderer(new MyRenderer());
        requestFocus();
        setFocusableInTouchMode(true);
        capture = true;
    }


    public void play() {

        lib.jthNet_StopPlay(0);//
    }

    public void stop() {
        lib.jthNet_StopPlay(0);//停止连接视珍宝
    }

    @SuppressWarnings("unused")//屏蔽java编译中的一些警告信息。unused这个参数是屏蔽
    public Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
            throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        bitmap = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(intBuffer);
        return bitmap;
    }


    //Renderer的功能就是这里说的画笔
    class MyRenderer implements Renderer {

        int width_surface, height_surface;
        private File dir_image;
        private int count = 0;

        private ByteBuffer mCaptureBuffer;


        // 绘制图形时调用  每隔16ms调用一次
        public void onDrawFrame(GL10 gl) {
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            int ret = lib.jopengl_Render(0);
            if (ret == 1 && !getFirstFrame) {
                getFirstFrame = true;
                ((LiveVidActivity) cnx).ipc.getFirstFrame();
            }


            saveBitmap();


        }


        //视图大小发生改变  时调用
        public void onSurfaceChanged(GL10 gl, int w, int h) {

// 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
            GLES20.glViewport(0, 0, w, h);

            float ratio = (float) w / h;

            width_surface = w;
            height_surface = h;


            int i = lib.jopengl_Resize(0, w, h);
            glwidth = w;
            glheight = h;
        }


        //SurfaceView创建
        public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        }


        /**
         * 保存方法
         */
        public void saveBitmap() {


            try {

                if (isTakePicture) {
                    isTakePicture = false;
                    //--------------------------------------------------------------------
                    mHandler.sendEmptyMessage(2);

                    Log.i("hari", "printOptionEnable if condition:isTakePicture = " + isTakePicture);
                    w = width_surface;
                    h = height_surface;

                    Log.i("hari", "w:" + w + "-----h:" + h);

                    b = new int[(int) (w * h)];
                    bt = new int[(int) (w * h)];
                    buffer = IntBuffer.wrap(b);
                    buffer.position(0);

                    Log.e("hari", "saveBitmap:time 1 =  ");
                    GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);


                    Log.e("hari", "saveBitmap:time 2.0 =  w = "+w+" , h = "+h);
                    for (int i = 0; i < h; i++) {
                        //remember, that OpenGL bitmap is incompatible with Android bitmap
                        //and so, some correction need.
                        for (int j = 0; j < w; j++) {
                            int pix = b[i * w + j];
                            int pb = (pix >> 16) & 0xff;
                            int pr = (pix << 16) & 0x00ff0000;
                            int pix1 = (pix & 0xff00ff00) | pr | pb;
                            bt[(h - i - 1) * w + j] = pix1;
                        }
                    }


                    Log.e("hari", "saveBitmap:time 2 =  ");

                    Bitmap inBitmap = null;
                    if (inBitmap == null || !inBitmap.isMutable()
                            || inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
                        inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    }
                    //Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    inBitmap.copyPixelsFromBuffer(buffer);

                    inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);


                    inBitmap = scaleBitmap(inBitmap);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    inBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

                    Log.e("hari", "saveBitmap:time 3 =  ");

                    String myfile = SouthUtil.saveImageName();
                    //Const.saveImageFilePath = /storage/emulated/0/SZB_save/Admin/2019-1-4/17-30-34_1_yey
                    Log.e("hari", "saveBitmap: Const.saveImageFilePath = " + Const.saveImageFilePath);

                    dir_image = new File(Const.saveImageFilePath);
                    dir_image.mkdirs();
                    FileOutputStream fos = null;
                    try {
                        File tmpFile = new File(dir_image, myfile);
                        fos = new FileOutputStream(tmpFile);

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = fis.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                        }


                        Log.e("hari", "saveBitmap:time 4 =  ");
                        mHandler.sendEmptyMessage(3);
                        fis.close();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(4);
                    } catch (IOException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(4);
                    } finally {
                        if (fis != null) {
                            fis.close();
                        }

                        if (fos != null) {
                            fos.close();
                        }

                        if (bos != null) {
                            bos.close();
                        }

                        if (inBitmap != null) {
                            inBitmap = null;
                        }
                        b = null;
                        bt = null;

                    }


                    Log.i("hari", "screenshots:" + dir_image.toString());
                    //--------------------------------------------------------------------


                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
                Log.i("hari", "onDrawFrame: " + e.getMessage());
            }


        }



        /**
         * 按比例缩放图片
         *
         * @param origin 原图
         *               ratio  比例
         * @return 新的bitmap
         */
        private Bitmap scaleBitmap(Bitmap origin) {
            if (origin == null) {
                return null;
            }

//            w = 1920; //长
//            h = 1080; //宽
//            int ratate = 90;
            int width = origin.getWidth();
            int height = origin.getHeight();
            float sw = 1920.0f / width;
            float sh = 1080.0f / height;

            Matrix matrix = new Matrix();
            matrix.postScale(sw, sh);
//            matrix.postRotate(ratate);



            Bitmap newBM = null;
            try {
                newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            } catch (Exception e) {
                mHandler.sendEmptyMessage(4);

                e.printStackTrace();
            } finally {

                origin.recycle();
            }

            return newBM;


        }

    }


}





