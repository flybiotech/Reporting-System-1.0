package com.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.adapter.ResoluteAdapter;
import com.model.DevModel;
import com.model.LampStateModel;
import com.model.RecordConfigModel;
import com.model.RetModel;
import com.model.SnapShotConfigModel;
import com.model.WIFIStateModel;
import com.network.Network;
import com.orhanobut.logger.Logger;
import com.shizhenbao.fragments.FragGetImage;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.LogUtil;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.SPUtils;
import com.suke.widget.SwitchButton;
import com.util.SouthUtil;
import com.view.LoadingDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.schedulers.Schedulers;


/**
 * Created by gyl1 on 12/22/16.
 */

public class ConfigActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    static final String tag = "ConfigActivity";
    int index; //
    DevModel model;
    Button btn_ap;
    Button btn_set_snapshot;//保存拍照设置的按钮
    Button btn_set_record;
    //    SwitchButton switchButton ;
    Button btn_sta;
    //    Spinner sp_snap0;//只拍一张或者连续拍照
    Spinner sp_snap1;//时长 3分钟或者5分钟或者6分钟或者8分钟
    Spinner sp_snap2;//时间间隔10秒或者15秒或者20秒或者30秒
    Spinner sp_record;
    private int snap1_index = 0; //记录选择的设置拍照的时长
    private int snap2_index = 0; //记录选择的拍照的间隔时间
    private int record_index = 0; //记录选择了第几个 摄像的时间
    ResoluteAdapter snap0Adapter;
    ResoluteAdapter snap1Adapter;
    ResoluteAdapter snap2Adapter;
    ResoluteAdapter recordAdapter;
    RelativeLayout lay_set_apsta;
    RelativeLayout lay_snap_set;
    RelativeLayout lay_record_set;
    RelativeLayout lay_lamp_set;
    SwitchButton switchLamp0;
    SwitchButton switchLamp1;

    LinkedHashMap<String, Integer> map_snap0Items = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> map_snap1Items = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> map_snap2Items = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> map_recordItems = new LinkedHashMap<String, Integer>();
    List<String> sp_snap0Items = new ArrayList<>();
    List<String> sp_snap1Items = new ArrayList<>();
    List<String> sp_snap2Items = new ArrayList<>();
    List<String> sp_recordItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_config);
        Bundle bundle = this.getIntent().getExtras();
        index = bundle.getInt("index");
        model = FragGetImage.listnode.get(index);
        handler.postDelayed(runnable, 500);
        handler.postDelayed(runnable1, 800);


    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {

            getConfig();
        }
    };

    public void initView() {
        btn_ap = (Button) findViewById(R.id.btn_ap);
        btn_sta = (Button) findViewById(R.id.btn_sta);
        sp_snap1 = (Spinner) findViewById(R.id.sp_snap1);
        sp_snap2 = (Spinner) findViewById(R.id.sp_snap2);
        sp_record = (Spinner) findViewById(R.id.sp_record);
        btn_set_snapshot = (Button) findViewById(R.id.btn_set_snapshot);//拍照设置
        btn_set_record = (Button) findViewById(R.id.btn_set_record);
        switchLamp0 = (SwitchButton) findViewById(R.id.switch_lamp0);//白光
        switchLamp1 = (SwitchButton) findViewById(R.id.switch_lamp1);//绿光
        lay_set_apsta = (RelativeLayout) findViewById(R.id.lay_set_apsta);
        lay_snap_set = (RelativeLayout) findViewById(R.id.lay_snap_set);
        lay_record_set = (RelativeLayout) findViewById(R.id.lay_record_set);
        lay_lamp_set = (RelativeLayout) findViewById(R.id.lay_lamp_set);

        btn_ap.setOnClickListener(this);
        btn_sta.setOnClickListener(this);
        btn_set_snapshot.setOnClickListener(this);
        btn_set_record.setOnClickListener(this);
//      sp_snap1

        switchLamp0.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                controlLamp(0, isChecked);
            }
        });
        switchLamp1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                controlLamp(1, isChecked);
            }
        });


        initSpinner();
        resetLayoutParamters();
        setSpannerPosition();
    }


//    //设置图片是否镜像和反转
//    private  void getImageFlip(final int isMirror, final int isFlip) {
//        Logger.e("model.usr = "+model.usr+"    model.pwd = "+model.pwd);
//        Network.getCommandApi(model).getImageFlip(model.usr,model.pwd,40,isMirror,isFlip,SouthUtil.getRandom())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<RetModel>() {
//                    @Override
//                    public void onCompleted() {
//                        lod.dismiss();
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        lod.dismiss();
//                        Logger.e("设置图片反转 RetModel 异常 : "+e);
////                                Log.e(tag,"设置录像时间 set record error:"+e.toString());
//                    }
//                    @Override
//                    public void onNext(RetModel retModel) {
//
//                        lod.dismiss();
//                        Logger.e("设置图片反转 RetModel : "+retModel.ret +"  isMirror = "+isMirror+"  isFlip = "+isFlip);
////                                Log.e(tag,"设置录像时间 set record ret:"+retModel.ret);
//
//                    }
//                });
//    }


    void resetLayoutParamters() {
        int totalExistHeight = SouthUtil.convertDpToPixel(50 + 40 + 40 + 40 + 40, this);
        WindowManager manage = this.getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;

        int span = (screenHeight - totalExistHeight) / 6;
        RelativeLayout.LayoutParams pam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        pam.topMargin = span;
        lay_set_apsta.setLayoutParams(pam);

        RelativeLayout.LayoutParams pam1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        pam1.topMargin = span;
        pam1.addRule(RelativeLayout.BELOW, R.id.lay_set_apsta);
        lay_snap_set.setLayoutParams(pam1);


        RelativeLayout.LayoutParams pam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        pam2.topMargin = span;
        pam2.addRule(RelativeLayout.BELOW, R.id.lay_snap_set);
        lay_record_set.setLayoutParams(pam2);

        RelativeLayout.LayoutParams pam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        pam3.topMargin = span;
        pam3.addRule(RelativeLayout.BELOW, R.id.lay_record_set);
        lay_lamp_set.setLayoutParams(pam3);


    }

    public void initSpinner() {
        map_snap0Items.put("只拍一张", Integer.valueOf(0));
        map_snap0Items.put("连续拍照", Integer.valueOf(1));

        map_snap1Items.put(getString(R.string.image_duration_three), Integer.valueOf(3));
        map_snap1Items.put(getString(R.string.image_duration_five), Integer.valueOf(5));
        map_snap1Items.put(getString(R.string.image_duration_six), Integer.valueOf(6));
        map_snap1Items.put(getString(R.string.image_duration_eight), Integer.valueOf(8));
        map_snap1Items.put(getString(R.string.image_duration_ten), Integer.valueOf(10));


        map_snap2Items.put(getString(R.string.image_interval_fifteen), Integer.valueOf(15));
        map_snap2Items.put(getString(R.string.image_interval_twenty), Integer.valueOf(20));
        map_snap2Items.put(getString(R.string.image_interval_thirty), Integer.valueOf(30));

        //录像的时间设置
        map_recordItems.put(getString(R.string.image_pickup_one), Integer.valueOf(1));
        map_recordItems.put(getString(R.string.image_pickup_three), Integer.valueOf(3));
        map_recordItems.put(getString(R.string.image_pickup_five), Integer.valueOf(4));
        map_recordItems.put(getString(R.string.image_pickup_six), Integer.valueOf(5));
        map_recordItems.put(getString(R.string.image_pickup_ten), Integer.valueOf(10));


        Iterator iter0 = map_snap0Items.entrySet().iterator();
        while (iter0.hasNext()) {
            Map.Entry entry = (Map.Entry) iter0.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            sp_snap0Items.add((String) key);
        }

        Iterator iter1 = map_snap1Items.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry entry = (Map.Entry) iter1.next();
            Object key = entry.getKey();
            sp_snap1Items.add((String) key);
        }

        Iterator iter2 = map_snap2Items.entrySet().iterator();
        while (iter2.hasNext()) {
            Map.Entry entry = (Map.Entry) iter2.next();
            Object key = entry.getKey();
            sp_snap2Items.add((String) key);
        }

        Iterator iter3 = map_recordItems.entrySet().iterator();
        while (iter3.hasNext()) {
            Map.Entry entry = (Map.Entry) iter3.next();
            Object key = entry.getKey();
            sp_recordItems.add((String) key);
        }


        snap1Adapter = new ResoluteAdapter(this);
        snap1Adapter.setItems(sp_snap1Items);
        sp_snap1.setAdapter(snap1Adapter);

        snap2Adapter = new ResoluteAdapter(this);
        snap2Adapter.setItems(sp_snap2Items);
        sp_snap2.setAdapter(snap2Adapter);

        recordAdapter = new ResoluteAdapter(this);
        recordAdapter.setItems(sp_recordItems);
        sp_record.setAdapter(recordAdapter);


        sp_snap1.setOnItemSelectedListener(this);
        sp_snap2.setOnItemSelectedListener(this);
        sp_record.setOnItemSelectedListener(this);


    }


    //设置Spanner的位置
    private void setSpannerPosition() {
        //自动拍照的默认时间
        int index1 = (int) SPUtils.get(this, Const.SP_TOTALTIME_INDEX, 0);
        int index2 = (int) SPUtils.get(this, Const.SP_INTERVAL_INDEX, 0);
        int index3 = (int) SPUtils.get(this, Const.SP_RECORD_INDEX, 0);
        Log.e("TAG_", "setSpannerPosition: index1 =" + index1 + " ,index2=" + index2 + " ,index3= " + index3);
        sp_snap1.setSelection(index1);
        sp_snap2.setSelection(index2);
        sp_record.setSelection(index3);
    }


    public void back(View v) {
        this.finish();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_ap) {


            Intent intent = new Intent(context, ConfigApActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            bundle.putParcelable("wifi", mWifiStateModel);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_sta) {


            Intent intent = new Intent(context, ConfigStaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            bundle.putParcelable("wifi", mWifiStateModel);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_set_record) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.image_setting_pickup_time) + mRecordConfigModel.TotalTime + getString(R.string.image_setting_pickup_minute))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            OneItem.getOneItem().setRecordTotalTime(mRecordConfigModel.TotalTime*60);
                            SPUtils.put(ConfigActivity.this, Const.RECORD_TOTALTIME, mRecordConfigModel.TotalTime * 60);
                            SPUtils.put(ConfigActivity.this, Const.SP_RECORD_INDEX, record_index);
                            SouthUtil.showToast(ConfigActivity.this, getString(R.string.setting_picture_save_success));

                        }
                    })
                    .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        } else if (v.getId() == R.id.btn_set_snapshot) {//拍照设置的监听事件


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.image_photograph_type) + (mSnapShotConfigModel.TotalTime == 0 ? getString(R.string.image_photograph_one) : getString(R.string.image_photograph_in)
                    + " " + mSnapShotConfigModel.TotalTime + " " + getString(R.string.image_photograph_interval) + " " + mSnapShotConfigModel.IntervalTime + " " + getString(R.string.image_photograph_continuition)))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            OneItem.getOneItem().setSpTotalTime( mSnapShotConfigModel.TotalTime*60);
//                            OneItem.getOneItem().setSpInterval( mSnapShotConfigModel.IntervalTime);
                            SPUtils.put(ConfigActivity.this, Const.SNAP_TOTALTIME, mSnapShotConfigModel.TotalTime * 60);
                            SPUtils.put(ConfigActivity.this, Const.SP_TOTALTIME_INDEX, snap1_index);
                            SPUtils.put(ConfigActivity.this, Const.SNAP_INTERVAL, mSnapShotConfigModel.IntervalTime);
                            SPUtils.put(ConfigActivity.this, Const.SP_INTERVAL_INDEX, snap2_index);
                            SouthUtil.showToast(ConfigActivity.this, getString(R.string.setting_picture_save_success));
                        }
                    })
                    .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
    }


    @Override //属于拍照设置
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.sp_snap1) {
            snap1_index = position;
            mSnapShotConfigModel.TotalTime = map_snap1Items.get(sp_snap1Items.get(position));
//            SPUtils.put(ConfigActivity.this,Const.SNAP_TOTALTIME,mSnapShotConfigModel.TotalTime*60);
//            SPUtils.put(ConfigActivity.this, Const.SP_TOTALTIME_INDEX, snap1_index);
        } else if (parent.getId() == R.id.sp_snap2) {
            snap2_index = position;
            mSnapShotConfigModel.IntervalTime = map_snap2Items.get(sp_snap2Items.get(position));
//            SPUtils.put(ConfigActivity.this,Const.SNAP_INTERVAL,mSnapShotConfigModel.IntervalTime);
//            SPUtils.put(ConfigActivity.this, Const.SP_INTERVAL_INDEX, snap2_index);
        } else if (parent.getId() == R.id.sp_record) {
            record_index = position;
            mRecordConfigModel.TotalTime = map_recordItems.get(sp_recordItems.get(position));
//            SPUtils.put(ConfigActivity.this,Const.RECORD_TOTALTIME,mRecordConfigModel.TotalTime*60);
//            SPUtils.put(ConfigActivity.this, Const.SP_RECORD_INDEX, record_index);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void getConfig() {
//        if (lod == null){
//            lod = new LoadingDialog(context);
//        }
//        lod.dialogShow();

        Observable.combineLatest(Network.getCommandApi(model).getRecordConfig(model.usr, model.pwd, 89, SouthUtil.getRandom()),
                Network.getCommandApi(model).getSnapShotConfig(model.usr, model.pwd, 87, SouthUtil.getRandom()),
                Network.getCommandApi(model).getWifiState(model.usr, model.pwd, 37, SouthUtil.getRandom()),
                Network.getCommandApi(model).getLampState(model.usr, model.pwd, 74, SouthUtil.getRandom()),
                new Func4<RecordConfigModel, SnapShotConfigModel, WIFIStateModel, LampStateModel, Boolean>() {
                    @Override
                    public Boolean call(RecordConfigModel recordConfigModel, SnapShotConfigModel snapShotConfigModel, WIFIStateModel wifiStateModel, LampStateModel lampStateModel) {

                        mWifiStateModel = wifiStateModel;
                        mLampStateModel = lampStateModel;
                        return true;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
//                        lod.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
//                        lod.dismiss();
                    }

                    @Override
                    public void onNext(Boolean vailed) {
//                        lod.dismiss();


                        switchLamp0.setChecked(mLampStateModel.Light0 == 1);
                        switchLamp1.setChecked(mLampStateModel.Light1 == 1);


                    }
                });
    }


    void controlLamp(int index, boolean open) {

        Observable.combineLatest(Network.getCommandApi(model)
                        .setLampState(model.usr, model.pwd, 73, index==0?0:1, open ? 1 : 0, SouthUtil.getRandom()),
                Network.getCommandApi(model)
                        .setLampState(model.usr, model.pwd, 73, index==0?1:0, open ? 0 : 1, SouthUtil.getRandom()), new Func2<RetModel, RetModel, RetModel>() {
                    @Override
                    public RetModel call(RetModel retModel, RetModel retModel2) {
                        return null;
                    }
                }).flatMap(new Func1<RetModel, Observable<LampStateModel>>() {
            @Override
            public Observable<LampStateModel> call(RetModel lampStateModel) {
                return Network.getCommandApi(model).getLampState(model.usr, model.pwd, 74, SouthUtil.getRandom());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer_lamp);


//        subscription = Network.getCommandApi(model)
//                .setLampState(model.usr,model.pwd,73,index,open?1:0,SouthUtil.getRandom())
//                .flatMap(new Func1<RetModel, Observable<LampStateModel>>() {
//                    @Override
//                    public Observable<LampStateModel> call(RetModel m) {
//                        //
//                        return Network.getCommandApi(model).getLampState(model.usr,model.pwd,74,SouthUtil.getRandom());
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer_lamp);
    }

    Observer<LampStateModel> observer_lamp = new Observer<LampStateModel>() {
        @Override
        public void onCompleted() {
//            lod.dismiss();
        }

        @Override
        public void onError(Throwable e) {
//            lod.dismiss();
            Log.e(tag, e.toString());
        }

        @Override
        public void onNext(LampStateModel m) {
//            lod.dismiss();
            mLampStateModel = m;
            Log.e(tag, "observer_lamp : " + m.toString());
            switchLamp0.setChecked(m.Light0 == 1);
            switchLamp1.setChecked(m.Light1 == 1);

        }
    };


    protected Subscription subscription;
//    LoadingDialog lod;

    LampStateModel mLampStateModel = new LampStateModel();
    RecordConfigModel mRecordConfigModel = new RecordConfigModel();
    SnapShotConfigModel mSnapShotConfigModel = new SnapShotConfigModel();
    WIFIStateModel mWifiStateModel = new WIFIStateModel();

}
