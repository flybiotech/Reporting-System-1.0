package com.activity;
import android.app.Activity;
import android.content.Intent;
import android.drm.DrmErrorEvent;
import android.drm.DrmManagerClient;
import android.drm.DrmManagerClient.OnErrorListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.model.DevModel;
import com.model.RetModel;
import com.network.Network;
import com.orhanobut.logger.Logger;
import com.util.FileUtil;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoPlayerActivity extends Activity implements OnPreparedListener, OnErrorListener{
    private static String tag =  "VideoPlayerActivity";
    private VideoView vv_video;
    private MediaController mController;


//    private ImageButton btn_trash;
    //private Button btn_save;

    private String url;
//    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_playvid);
        vv_video=(VideoView) findViewById(R.id.vv_video);
//        btn_trash = (ImageButton) findViewById(R.id.trash);
        //btn_save = (Button) findViewById(R.id.save);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            url  = bundle.getString("videoPath");
        }
        Logger.e(" videoPath = "+url);

        // 实例化MediaController
        mController=new MediaController(this);

        if (FileUtil.isFileExist(url))
        {
            // vv_video.setVideoPath(file.getAbsolutePath());
            // 为VideoView指定MediaController
            //Uri uri = Uri.parse(url);
            try {
                vv_video.setMediaController(mController);
                // 为MediaController指定控制的VideoView
                mController.setMediaPlayer(vv_video);
                mController.setKeepScreenOn(true);
                vv_video.setOnPreparedListener(this);
                vv_video.setVideoPath(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            vv_video.seekTo(10);
            vv_video.start();
            // vv_video.start();
            // 增加监听上一个和下一个的切换事件，默认这两个按钮是不显示的
//            mController.setPrevNextListeners(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // Toast.makeText(VideoPlayerAct.this, "下一个",0).show();
//                }
//            }, new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //Toast.makeText(VideoPlayerAct.this, "上一个",0).show();
//                }
//            });
        }
        else{
            MyToast.showToast(this,getString(R.string.image_manager_pickup_error));
//            SouthUtil.showToast(this,getString(R.string.image_manager_pickup_error));
        }
    }

    public void back(View v){
        Intent intent = VideoPlayerActivity.this.getIntent();
        VideoPlayerActivity.this.setResult(0,intent);
        finish();
    }
    @Override
    public void onError(DrmManagerClient arg0, DrmErrorEvent arg1) {
        // TODO Auto-generated method stub
        Log.e(tag,arg1.toString());

    }
    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub
        vv_video.start();

    }

    public void save(View v) {

//        final String path_root = Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/south_save/";
//        final String path = Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/south_save/video/";
//        if(!FileUtil.makeDir(path_root)) {
//            return;
//        }
//        if(!FileUtil.makeDir(path)) {
//            return;
//        }
//        SimpleDateFormat sDateFormat1   =   new   SimpleDateFormat("yyyyMMddHHmmss");
//        Date d = new   java.util.Date();
//        String   date1   =   sDateFormat1.format(d);
//        final String name = date1+".mp4";
//        Log.e(tag,"download url is "+url);
//
//        if (lod == null){
//            lod = new LoadingDialog(this);
//        }
//        lod.dialogShow();
//        OkHttpUtils//
//                .get()//
//                .url(url)//
//                .build()//
//                .execute(new FileCallBack(path, name)//
//                {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        lod.dismiss();
//                    }
//
//                    @Override
//                    public void onResponse(File response, int id) {
//                        lod.dismiss();
//                        SouthUtil.showToast(VideoPlayerActivity.this,"已保存至根目录下的south_save下的video文件夹");
//                    }
//                });
    }

//    Observer<List<RetModel>> observer = new Observer<List<RetModel>>() {
//        @Override
//        public void onCompleted() {
//            lod.dismiss();
//        }
//        @Override
//        public void onError(Throwable e) {
//            lod.dismiss();
//            Log.e(tag, e.toString());
//        }
//
//        @Override
//        public void onNext(List<RetModel> rets) {
//            lod.dismiss();
//            if (rets.get(0).ret == 1){
//                SouthUtil.showToast(VideoPlayerActivity.this,getString(R.string.image_manager_pickup_delete));
//                Intent intent = VideoPlayerActivity.this.getIntent();
//                VideoPlayerActivity.this.setResult(2,intent);
//                finish();
//
//                VideoPlayerActivity.this.finish();
//            }
//            else{
//                SouthUtil.showToast(VideoPlayerActivity.this,getString(R.string.image_manager_pickup_delete_faild));
//            }
//        }
//    };
    protected Subscription subscription;
    LoadingDialog lod;
    private DevModel model;
}
