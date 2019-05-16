package com.shizhenbao.draw;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.util.OneItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.ThreadUtil;

/**
 * 涂鸦界面，根据GraffitiView的接口，提供页面交互
 * （这边代码和ui比较粗糙，主要目的是告诉大家GraffitiView的接口具体能实现什么功能，实际需求中的ui和交互需另提别论）
 * Created by huangziwei(154330138@qq.com) on 2016/9/3.
 */
public class GraffitiActivity extends Activity {
    public static final String TAG = "Graffiti";

    public static final int RESULT_ERROR = -111; // 出现错误

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param params      涂鸦参数
     * @param requestCode startActivityForResult的请求码
     * @see GraffitiParams
     */
    public static void startActivityForResult(Activity activity, GraffitiParams params, int requestCode) {
        Intent intent = new Intent(activity, GraffitiActivity.class);
        intent.putExtra(GraffitiActivity.KEY_PARAMS, params);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param imagePath   　图片路径
     * @param savePath    　保存路径
     * @param isDir       　保存路径是否为目录
     * @param requestCode 　startActivityForResult的请求码
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, String savePath, boolean isDir, int requestCode) {
        GraffitiParams params = new GraffitiParams();
        params.mImagePath = imagePath;
        params.mSavePath = savePath;
        params.mSavePathIsDir = isDir;
        startActivityForResult(activity, params, requestCode);
    }

    /**
     * {@link GraffitiActivity#startActivityForResult(Activity, String, String, boolean, int)}
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, int requestCode) {
        GraffitiParams params = new GraffitiParams();
        params.mImagePath = imagePath;
        startActivityForResult(activity, params, requestCode);
    }

    public static final String KEY_PARAMS = "key_graffiti_params";
    public static final String KEY_IMAGE_PATH = "key_image_path";

    private String mImagePath,mSavePath;
    private Bitmap mBitmap;

    private FrameLayout mFrameLayout;
    private GraffitiView mGraffitiView;

    private View.OnClickListener mOnClickListener;

    private SeekBar mPaintSizeBar;
    private TextView mPaintSizeView;

    private View mBtnColor;
    private Runnable mUpdateScale;

    private int mTouchMode;
    private boolean mIsMovingPic = false;

    // 手势操作相关
    private float mOldScale, mOldDist, mNewDist, mToucheCentreXOnGraffiti,
            mToucheCentreYOnGraffiti, mTouchCentreX, mTouchCentreY;// 双指距离
    private boolean mDone = false;
    private float mTouchLastX, mTouchLastY;

    private boolean mIsScaling = false;
    private float mScale = 1;
    private final float mMaxScale = 3.5f; // 最大缩放倍数
    private final float mMinScale = 0.25f; // 最小缩放倍数
    private final int TIME_SPAN = 40;
    private View mBtnMovePic, mBtnHidePanel, mSettingsPanel;
    private View mShapeModeContainer;
    private View mSelectedTextEditContainer;
    private View mEditContainer;

    private int mTouchSlop;

    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画

    // 当前屏幕中心点对应在GraffitiView中的点的坐标
    float mCenterXOnGraffiti;
    float mCenterYOnGraffiti;

    private GraffitiParams mGraffitiParams;

    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    //触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mShowDelayRunnable;

//    private float mSelectableItemSize;
    private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

    }
};
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARAMS, mGraffitiParams);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mGraffitiParams = savedInstanceState.getParcelable(KEY_PARAMS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGraffitiParams == null) {
            mGraffitiParams = getIntent().getExtras().getParcelable(KEY_PARAMS);
        }
        if (mGraffitiParams == null) {
            LogUtil.e("TAG", "mGraffitiParams is null!");
            this.finish();
            return;
        }
        mSavePath=mGraffitiParams.mSavePath;
        mImagePath = mGraffitiParams.mImagePath;
        if (mImagePath == null) {
            LogUtil.e("TAG", "mImagePath is null!");
            this.finish();
            return;
        }
        LogUtil.d("TAG", mImagePath);

        mBitmap = ImageUtils.createBitmapFromPath(mImagePath, this);
        if (mBitmap == null) {
            LogUtil.e("TAG", "bitmap is null!");
            this.finish();
            return;
        }

        setContentView(R.layout.layout_graffiti);
        mFrameLayout = (FrameLayout) findViewById(R.id.graffiti_container);

        // /storage/emulated/0/DCIM/Graffiti/1479369280029.jpg
        mGraffitiView = new GraffitiView(this, mBitmap, mGraffitiParams.mEraserPath, mGraffitiParams.mEraserImageIsResizeable,
                new GraffitiListener() {
                    @Override
                    public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) { // 保存图片
                        if (bitmapEraser != null) {
                            bitmapEraser.recycle(); // 回收图片，不再涂鸦，避免内存溢出
                        }
                        File graffitiFile = null;
                        File file = null;
//                        String savePath = mGraffitiParams.mSavePath;
                        boolean isDir = mGraffitiParams.mSavePathIsDir;
                        if (TextUtils.isEmpty(mSavePath)) {
                            File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
                            graffitiFile = new File(dcimFile, "Graffiti");
                            //　保存的路径
                            file = new File(graffitiFile, System.currentTimeMillis() + ".png");
                        } else {
                            if (isDir) {
                                graffitiFile = new File(mSavePath);
                                //　保存的路径
                                file = new File(graffitiFile,"方位.png");
                            } else {
                                file = new File(mSavePath);
                                graffitiFile = file.getParentFile();
                            }
                        }
                        graffitiFile.mkdirs();

                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
                            Intent intent = new Intent();
                            intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            onError(GraffitiView.ERROR_SAVE, e.getMessage());
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String msg) {
                        setResult(RESULT_ERROR);
                        finish();
                    }

                    @Override
                    public void onReady() {
                        mGraffitiView.setPaintSize(mGraffitiParams.mPaintSize > 0 ? mGraffitiParams.mPaintSize
                                : mGraffitiView.getPaintSize());
                        mPaintSizeBar.setProgress((int) (mGraffitiView.getPaintSize() + 10));
                        mPaintSizeBar.setMax((int) (Math.min(mGraffitiView.getBitmapWidthOnView(), mGraffitiView.getBitmapHeightOnView()) / 3 * DrawUtil.GRAFFITI_PIXEL_UNIT));
                        mPaintSizeView.setText("" + mPaintSizeBar.getProgress());
//                        findViewById(R.id.btn_pen_hand).performClick();
//                        findViewById(R.id.btn_hand_write).performClick();
                    }

                    @Override
                    public void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected) {
                        if (selected) {
                            if (mGraffitiView.getSelectedItemColor().getType() == GraffitiColor.Type.BITMAP) {
                                mBtnColor.setBackgroundDrawable(new BitmapDrawable(mGraffitiView.getSelectedItemColor().getBitmap()));
                            } else {
                                mBtnColor.setBackgroundColor(mGraffitiView.getSelectedItemColor().getColor());
                            }
                            mPaintSizeBar.setProgress((int) (mGraffitiView.getSelectedItemSize() + 10));
                        } else {
                            if (mGraffitiView.getColor().getType() == GraffitiColor.Type.BITMAP) {
                                mBtnColor.setBackgroundDrawable(new BitmapDrawable(mGraffitiView.getColor().getBitmap()));
                            } else {
                                mBtnColor.setBackgroundColor(mGraffitiView.getColor().getColor());
                            }

                            mPaintSizeBar.setProgress((int) (mGraffitiView.getPaintSize() + 10));
                        }
                    }

                    @Override
                    public void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y) {
                        if (pen == GraffitiView.Pen.TEXT) {
                            createGraffitiText(null, x, y);
                        } else if (pen == GraffitiView.Pen.BITMAP) {
                            createGraffitiBitmap(null, x, y);
                        }
                    }
                });
        mGraffitiView.setIsDrawableOutside(mGraffitiParams.mIsDrawableOutside);
        mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mOnClickListener = new GraffitiOnClickListener();
        mTouchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
        initView();
    }

    private void createGraffitiText(final GraffitiText graffitiText, final float x, final float y) {
        Activity activity = this;
        int t= OneItem.getOneItem().getTab();
        if(t<=8){
            OneItem.getOneItem().setTab(1);
            String text =t+"";
            if (TextUtils.isEmpty(text)) {
                return;
            }
            if (graffitiText == null) {
                mGraffitiView.addSelectableItem(new GraffitiText(mGraffitiView.getPen(), text, mGraffitiView.getPaintSize(), mGraffitiView.getColor().copy(),
                        0, mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY()));
            } else {
                graffitiText.setText(text);
            }
            mGraffitiView.invalidate();
            t=t+1;
            OneItem.getOneItem().setTab(t);
        }else {
            Toast.makeText(activity,getString(R.string.print_eight_no_more), Toast.LENGTH_SHORT).show();
        }
        if (graffitiText == null) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
        }

    }

    private void createGraffitiBitmap(final GraffitiBitmap graffitiBitmap, final float x, final float y) {
        Activity activity = this;

        Resources res=getResources();
        int t= OneItem.getOneItem().getTab();
        if(t<=8) {
            OneItem.getOneItem().setTab(1);
            Bitmap bitmap = null;
            switch (t){
                case 1:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b1);
                    break;
                case 2:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b2);
                    break;
                case 3:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b3);
                    break;
                case 4:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b4);
                    break;
                case 5:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b5);
                    break;
                case 6:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b6);
                    break;
                case 7:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b7);
                    break;
                case 8:
                    bitmap= BitmapFactory.decodeResource(res, R.drawable.b8);
                    break;
            }
            if (graffitiBitmap == null) {
                mGraffitiView.addSelectableItem(new GraffitiBitmap(mGraffitiView.getPen(), bitmap, mGraffitiView.getPaintSize(), mGraffitiView.getColor().copy(),
                        0, mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY()));
            } else {
                graffitiBitmap.setBitmap(bitmap);
            }
        mGraffitiView.invalidate();
        t=t+1;
        OneItem.getOneItem().setTab(t);
    }else {
        Toast.makeText(activity, getString(R.string.print_eight_no_more), Toast.LENGTH_SHORT).show();
    }
//        selectorContainer.addView(selectorView);
    }

    private void initView() {

        findViewById(R.id.btn_clear).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_undo).setOnClickListener(mOnClickListener);

        findViewById(R.id.graffiti_btn_finish).setOnClickListener(mOnClickListener);
        findViewById(R.id.graffiti_btn_back).setOnClickListener(mOnClickListener);
        mBtnColor = findViewById(R.id.btn_set_color);
        mBtnColor.setOnClickListener(mOnClickListener);
        mSettingsPanel = findViewById(R.id.graffiti_panel);
        if (mGraffitiView.getGraffitiColor().getType() == GraffitiColor.Type.COLOR) {
            mBtnColor.setBackgroundColor(mGraffitiView.getGraffitiColor().getColor());
        } else if (mGraffitiView.getGraffitiColor().getType() == GraffitiColor.Type.BITMAP) {
            mBtnColor.setBackgroundDrawable(new BitmapDrawable(mGraffitiView.getGraffitiColor().getBitmap()));
        }

        mPaintSizeBar = (SeekBar) findViewById(R.id.paint_size);
        mPaintSizeView = (TextView) findViewById(R.id.paint_size_text);
        mPaintSizeBar.setProgress(90);
        mPaintSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    mPaintSizeBar.setProgress(90);
                    return;
                }
                mPaintSizeView.setText("" + progress);
                if (mGraffitiView.isSelectedItem()) {
                    mGraffitiView.setSelectedItemSize(progress);
                } else {
                    mGraffitiView.setPaintSize(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ScaleOnTouchListener onTouchListener = new ScaleOnTouchListener();


        // 添加涂鸦的触摸监听器，移动图片位置
        mGraffitiView.setOnTouchListener(new View.OnTouchListener() {

            boolean mIsBusy = false; // 避免双指滑动，手指抬起时处理单指事件。

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!mIsMovingPic) {
                    return false;  // 交给下一层的涂鸦处理
                }
                mScale = mGraffitiView.getScale();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = 1;
                        mTouchLastX = event.getX();
                        mTouchLastY = event.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mTouchMode = 0;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode < 2) { // 单点滑动
                            if (mIsBusy) { // 从多点触摸变为单点触摸，忽略该次事件，避免从双指缩放变为单指移动时图片瞬间移动
                                mIsBusy = false;
                                mTouchLastX = event.getX();
                                mTouchLastY = event.getY();
                                return true;
                            }
                            float tranX = event.getX() - mTouchLastX;
                            float tranY = event.getY() - mTouchLastY;
                            mGraffitiView.setTrans(mGraffitiView.getTransX() + tranX, mGraffitiView.getTransY() + tranY);
                            mTouchLastX = event.getX();
                            mTouchLastY = event.getY();
                        } else { // 多点
                            mNewDist = spacing(event);// 两点滑动时的距离
                            if (Math.abs(mNewDist - mOldDist) >= mTouchSlop) {
                                float scale = mNewDist / mOldDist;
                                mScale = mOldScale * scale;

                                if (mScale > mMaxScale) {
                                    mScale = mMaxScale;
                                }
                                if (mScale < mMinScale) { // 最小倍数
                                    mScale = mMinScale;
                                }
                                // 围绕坐标(0,0)缩放图片
                                mGraffitiView.setScale(mScale);
                                // 缩放后，偏移图片，以产生围绕某个点缩放的效果
                                float transX = mGraffitiView.toTransX(mTouchCentreX, mToucheCentreXOnGraffiti);
                                float transY = mGraffitiView.toTransY(mTouchCentreY, mToucheCentreYOnGraffiti);
                                mGraffitiView.setTrans(transX, transY);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_POINTER_UP:
                        mTouchMode -= 1;
                        return true;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mTouchMode += 1;
                        mOldScale = mGraffitiView.getScale();
                        mOldDist = spacing(event);// 两点按下时的距离
                        mTouchCentreX = (event.getX(0) + event.getX(1)) / 2;// 不用减trans
                        mTouchCentreY = (event.getY(0) + event.getY(1)) / 2;
                        mToucheCentreXOnGraffiti = mGraffitiView.toX(mTouchCentreX);
                        mToucheCentreYOnGraffiti = mGraffitiView.toY(mTouchCentreY);
                        mIsBusy = true; // 标志位多点触摸
                        return true;
                }
                return true;
            }
        });

        mViewShowAnimation = new AlphaAnimation(0, 1);
        mViewShowAnimation.setDuration(500);
        mViewHideAnimation = new AlphaAnimation(1, 0);
        mViewHideAnimation.setDuration(500);
        mHideDelayRunnable = new Runnable() {
            public void run() {
                hideView(mSettingsPanel);
            }

        };
        mShowDelayRunnable = new Runnable() {
            public void run() {
                showView(mSettingsPanel);
            }
        };
        mGraffitiView.setPen(GraffitiView.Pen.BITMAP);
        mDone = true;
        OneItem.getOneItem().setTab(1);
    }

    /**
     * 计算两指间的距离
     *
     * @param event
     * @return
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private class GraffitiOnClickListener implements View.OnClickListener {

        private View mLastPenView, mLastShapeView;
        @Override
        public void onClick(View v) {
            mDone = false;
            if (mDone) {
                if (mLastPenView != null) {
                    mLastPenView.setSelected(false);
                }
                v.setSelected(true);
                mLastPenView = v;
                return;
            }

            if (v.getId() == R.id.btn_clear) {
                if (!(GraffitiParams.getDialogInterceptor() != null
                        && GraffitiParams.getDialogInterceptor().onShow(GraffitiActivity.this, mGraffitiView, GraffitiParams.DialogType.CLEAR_ALL))) {
                    DialogController.showEnterCancelDialog(GraffitiActivity.this,
                            getString(R.string.print_Clear_the_screen), getString(R.string.graffiti_cant_undo_after_clearing),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mGraffitiView.clear();
                                    OneItem.getOneItem().setTab(1);
                                }
                            }, null
                    );
                }
                mDone = true;
            } else if (v.getId() == R.id.btn_undo) {
                mGraffitiView.undo();
                mDone = true;
            } else if (v.getId() == R.id.btn_set_color) {
                if (!(GraffitiParams.getDialogInterceptor() != null
                        && GraffitiParams.getDialogInterceptor().onShow(GraffitiActivity.this, mGraffitiView, GraffitiParams.DialogType.COLOR_PICKER))) {
                    new ColorPickerDialog(GraffitiActivity.this, mGraffitiView.getGraffitiColor().getColor(), "画笔颜色",
                            new ColorPickerDialog.OnColorChangedListener() {
                                public void colorChanged(int color) {
                                    mBtnColor.setBackgroundColor(color);
                                    if (mGraffitiView.isSelectedItem()) {
                                        mGraffitiView.setSelectedItemColor(color);
                                    } else {
                                        mGraffitiView.setColor(color);
                                    }
                                }

                                @Override
                                public void colorChanged(Drawable color) {
                                    mBtnColor.setBackgroundDrawable(color);
                                    if (mGraffitiView.isSelectedItem()) {
                                        mGraffitiView.setSelectedItemColor(ImageUtils.getBitmapFromDrawable(color));
                                    } else {
                                        mGraffitiView.setColor(ImageUtils.getBitmapFromDrawable(color));
                                    }
                                }
                            }).show();
                }
                mDone = true;
            }
            if (mDone) {
                return;
            }

          if (v.getId() == R.id.graffiti_btn_finish) {
                mGraffitiView.setColor(Color.WHITE);
                mGraffitiView.save();
                mDone = true;

                finish();
            } else if (v.getId() == R.id.graffiti_btn_back) {
                if (!mGraffitiView.isModified()) {
                    finish();
                    return;
                }
                if (!(GraffitiParams.getDialogInterceptor() != null
                        && GraffitiParams.getDialogInterceptor().onShow(GraffitiActivity.this, mGraffitiView, GraffitiParams.DialogType.SAVE))) {
                    DialogController.showEnterCancelDialog(GraffitiActivity.this, getString(R.string.graffiti_saving_picture), null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mGraffitiView.save();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
                mDone = true;
            }
            if (mDone) {
                return;
            }
//            if (v.getId() == R.id.graffiti_selectable_edit) {
//                if (mGraffitiView.getSelectedItem() instanceof GraffitiText) {
//                    createGraffitiText((GraffitiText) mGraffitiView.getSelectedItem(), -1, -1);
//                } else if (mGraffitiView.getSelectedItem() instanceof GraffitiBitmap) {
//                    createGraffitiBitmap((GraffitiBitmap) mGraffitiView.getSelectedItem(), -1, -1);
//                }
//                mDone = true;
//            }
//            if (mDone) {
//                return;
//            }


            if (mLastShapeView != null) {
                mLastShapeView.setSelected(false);
            }
            v.setSelected(true);
            mLastShapeView = v;
        }
    }

    /**
     * 放大缩小
     */
    private class ScaleOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    scalePic(v);
                    v.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsScaling = false;
                    v.setSelected(false);
                    break;
            }
            return true;
        }
    }

    /**
     * 缩放
     *
     * @param v
     */
    public void scalePic(View v) {
        if (mIsScaling)
            return;
        mIsScaling = true;
        mScale = mGraffitiView.getScale();

        // 确定当前屏幕中心点对应在GraffitiView中的点的坐标，之后将围绕这个点缩放
        mCenterXOnGraffiti = mGraffitiView.toX(mGraffitiView.getWidth() / 2);
        mCenterYOnGraffiti = mGraffitiView.toY(mGraffitiView.getHeight() / 2);

    }

    private void updateScale() {
        if (mUpdateScale == null) {

            mUpdateScale = new Runnable() {
                public void run() {
                    // 围绕坐标(0,0)缩放图片
                    mGraffitiView.setScale(mScale);
                    // 缩放后，偏移图片，以产生围绕某个点缩放的效果
                    float transX = mGraffitiView.toTransX(mGraffitiView.getWidth() / 2, mCenterXOnGraffiti);
                    float transY = mGraffitiView.toTransY(mGraffitiView.getHeight() / 2, mCenterYOnGraffiti);
                    mGraffitiView.setTrans(transX, transY);
                }
            };
        }
        ThreadUtil.getInstance().runOnMainThread(mUpdateScale);
    }

    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.clearAnimation();
        view.startAnimation(mViewShowAnimation);
        view.setVisibility(View.VISIBLE);
        if (view == mSettingsPanel || mBtnHidePanel.isSelected()) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            if (view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0) {
                mGraffitiView.setAmplifierScale(-1);
            }
            return;
        }
        view.clearAnimation();
        view.startAnimation(mViewHideAnimation);
        view.setVisibility(View.GONE);
        if (view == mSettingsPanel && !mBtnHidePanel.isSelected() && !mBtnMovePic.isSelected()) {
            // 当设置面板隐藏时才显示放大器
            mGraffitiView.setAmplifierScale(mGraffitiParams.mAmplifierScale);
        } else if ((view == mSettingsPanel && mGraffitiView.getAmplifierScale() > 0)) {
            mGraffitiView.setAmplifierScale(-1);
        }
    }

}
