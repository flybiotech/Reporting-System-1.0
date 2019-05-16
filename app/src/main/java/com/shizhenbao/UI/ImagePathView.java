package com.shizhenbao.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.activity.R;
import com.shizhenbao.pop.Points;
import com.util.CloseableUtils;
import com.util.SouthUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ImagePathView extends View {

    private Bitmap mNewBitmap; //在原始 bitmap上 画了 直线，画多边形的bitmap
    private Bitmap mOriginalBitmap; //原始的 Bitmap
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint mPathPaint;
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private boolean isClear = false;
    private List<Points> mListPoints;
    private List<Float> mListTextPointX;
    private List<Float> mListTextPointY;
    private List<String> mListText; //保存 直线的长度 或面积
    private Context mContext;
    private float mRatioInch; //英寸与像素的比例
    private Canvas canvas1 = null;
    private int offsetX; //偏移量
    private int offsetY;
    private OnSaveImageListener mListener;
    private int saveImageType = 0; //0 :表示可以进行保存图片的操作，其他值表示图片正在保存中，不可进行下一步


    public ImagePathView(Context context) {
        this(context, null);
    }

    public ImagePathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);

    }

    private void initView(Context context) {
        mContext = context;
        mListPoints = new ArrayList<>();
        mListTextPointX = new ArrayList<>();
        mListTextPointY = new ArrayList<>();
        mListText = new ArrayList<>();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(SouthUtil.dp2px(1.5f));
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(SouthUtil.dp2px(1.0f));
        mTextPaint.setTextSize(SouthUtil.dp2px(15));

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(Color.BLUE);
        mPathPaint.setStrokeWidth(SouthUtil.dp2px(1.5f));
        mPathPaint.setStyle(Paint.Style.STROKE);


        mPath = new Path();
    }

    //设置图片
    public void setBitmapPath(String imagemPath, double ratioInch) {
//        Log.i("TAG-11", "setBitmapPath: imagemPath = " + imagemPath);
        try {
            saveImageType = 0;
            mOriginalBitmap = BitmapFactory.decodeFile(imagemPath);
            mOriginalBitmap = getNewBitmap(mOriginalBitmap, true);
            if (mOriginalBitmap != null) {
                mNewBitmap = Bitmap.createBitmap(mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                canvas1 = new Canvas(mNewBitmap);
                canvas1.drawColor(Color.WHITE);
                offsetX = (mWidth - mOriginalBitmap.getWidth()) / 2;
                offsetY = (mHeight - mOriginalBitmap.getHeight()) / 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            mRatioInch = (float) ratioInch;
            if (mListText != null) {
                mListText.clear();
            }
            if (mListTextPointX != null) {
                mListTextPointX.clear();
            }

            if (mListTextPointY != null) {
                mListTextPointY.clear();
            }

            invalidate();
        }


    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();

    }


    @Override
    protected void onDraw(Canvas canvas) {

//        super.onDraw(canvas);
        if (mOriginalBitmap != null) {
            if (isClear) {
                canvas.drawBitmap(mOriginalBitmap, offsetX, offsetY, mPaint);
                isClear = false;
            } else {

                if (canvas1 != null) {
                    canvas.drawBitmap(mOriginalBitmap, offsetX, offsetY, mPaint);
                    canvas.drawPath(mPath, mPathPaint);
                    canvas.drawLine(startX, startY, endX1, endtY1, mPaint);
                    for (int i = 0; i < mListText.size(); i++) {
                        canvas.drawText(mListText.get(i), mListTextPointX.get(i), mListTextPointY.get(i), mTextPaint);
                    }
                    canvas1.drawBitmap(mOriginalBitmap, 0, 0, mPaint);
                    canvas1.save();
                    canvas1.translate(-offsetX, -offsetY);
                    canvas1.drawPath(mPath, mPathPaint);
                    // 长度
                    for (int i = 0; i < mListText.size(); i++) {
                        canvas1.drawText(mListText.get(i), mListTextPointX.get(i), mListTextPointY.get(i), mTextPaint);
                    }

                    canvas1.restore();

                }

            }

        }

    }


    /**
     * 按比例缩放图片
     * <p>
     * param origin 原图
     * ratio  比例
     *
     * @return 新的bitmap
     */
    private Bitmap getNewBitmap(Bitmap bitmap, boolean isDirect) {
        Bitmap newBM = bitmap;
        if (newBM == null) {
            return null;
        }

        int width = newBM.getWidth();
        int height = newBM.getHeight();
        Matrix matrix = new Matrix();
        if (isDirect) {
            float sw = 0.7f;
            float sh = 0.7f;
            matrix.postScale(sw, sh);
            matrix.postRotate(90);
        } else {
            float sw = 1080.0f / width;
            float sh = 1920.0f / height;

            matrix.postScale(sw, sh);
            matrix.postRotate(-90);
        }

        try {
            newBM = Bitmap.createBitmap(newBM, 0, 0, width, height, matrix, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i("TAG_11", "getNewBitmap: newBM.width = " + newBM.getWidth() + " , newBM.height = " + newBM.getHeight()+" , width = "+width+" , height= "+height);
        return newBM;

    }


    float startX;
    float startY;
    float endX1;
    float endtY1;
    float linePointX = 0;
    float linePointY = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://按下第一个手指

                startX = event.getX();
                startY = event.getY();
                endX1 = 0;
                endtY1 = 0;
                mListPoints.clear();

                mListPoints.add(new Points(startX, startY));
                mPath.moveTo(startX, startY);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (endX1 > 0 && endtY1 > 0) {
                    mPath.lineTo(endX1, endtY1);
                    startX = endX1;
                    startY = endtY1;
                    mListPoints.add(new Points(endX1, endtY1));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                endX1 = event.getX();
                endtY1 = event.getY();

                invalidate();

                break;
            case MotionEvent.ACTION_UP:

                endX1 = startX;
                endtY1 = startY;
                mPath.close();
                mulArea(mListPoints);
                invalidate();

                break;

            case MotionEvent.ACTION_POINTER_UP:
//                Log.d(TAG, "第一根手指抬起来的时候MotionEvent.ACTION_POINTER_UP: mPath  ");
                break;
            default:
                break;
        }

        return true;
    }


    //刷新
    public void reFresh() {
        isClear = true;
        reset();
        invalidate();
    }

    private void reset() {
        saveImageType = 0;
//        mNewBitmap = mOriginalBitmap;
        mPath.reset();
        if (mListText != null) {
            mListText.clear();
        }
        if (mListTextPointX != null) {
            mListTextPointX.clear();
        }

        if (mListTextPointY != null) {
            mListTextPointY.clear();
        }
    }

    // 保存 绘制 直线长度和面积的坐标点
    private void mulArea(List<Points> point) {
        String str = "";
        if (point != null) {
            if (point.size() == 2) {
                linePointX = (mListPoints.get(0).getX() + mListPoints.get(1).getX()) / 2 - 20;
                linePointY = (mListPoints.get(0).getY() + mListPoints.get(1).getY()) / 2;
                str = mContext.getString(R.string.setting_length) + "： " + lineLength(point);
            } else if (point.size() > 2) {
                linePointX = (mListPoints.get(0).getX() + mListPoints.get(1).getX()) / 2 - 20;
                linePointY = (mListPoints.get(0).getY() + mListPoints.get(1).getY()) / 2;
                str = mContext.getString(R.string.setting_acreage) + "： " + caculate(point);
            }
            mListTextPointX.add(linePointX);
            mListTextPointY.add(linePointY);
            mListText.add(str);
        }
//        Log.i("TAG_11", "mulArea: mListText.size  = " + mListText.size() + " , mListTextPointX.size = " + mListTextPointX.size());
    }


    ///计算直线的长度 1 英寸约等于 2.54 厘米
    private float lineLength(List<Points> point) {
        float X = point.get(1).getX() - point.get(0).getX();
        float Y = point.get(1).getY() - point.get(0).getY();
        double length = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
        length = length * mRatioInch * 2.54f;
        return (float) length;
    }

    //计算图形的面积
    private float caculate(List<Points> point) {
        float temp = 0;
        for (int i = 0; i < mListPoints.size(); i++) {
            if (i < mListPoints.size() - 1) {
                Points p1 = mListPoints.get(i);
                Points p2 = mListPoints.get(i + 1);
                temp += p1.getX() * p2.getY() - p2.getX() * p1.getY();
            } else {
                Points pn = mListPoints.get(i);
                Points p0 = mListPoints.get(0);
                temp += pn.getX() * p0.getY() - p0.getX() * pn.getY();
            }
        }
        //顺时针画的坐标点是正数，逆时针画的坐标点的得到的面积是负数
        temp = temp / 2;
//        List<Float> area = getScreenSizeOfDevice();
//        temp=(temp/area.get(0))*area.get(1)*2.54f;
        temp = temp * mRatioInch * mRatioInch * 2.54f;
//        Log.d("TAG_11", "画图的面积 : " + temp);
        return Math.abs(temp);
    }


    //保存图片
    public void saveBitmap(String rootPath) {

        if (mOriginalBitmap == null) {
            if (mListener != null) {
                mListener.onSaveImageFailed(4);
            }
        } else {
            switch (saveImageType) {
                case 0: //开始保存图片
                    getFiles(rootPath, getNewBitmap(mNewBitmap, false));
                    break;

                case 1:
                    break;

                case 2:
                    //正在保存的过程中
                    if (mListener != null) {
                        mListener.onSaveImageFailed(2);
                    }
                    break;

                case 3:
                    //正在保存的过程中
                    if (mListener != null) {
                        mListener.onSaveImageFailed(3);
                    }
                    break;

                case 4:
                    break;
                default:
                    break;
            }

        }

    }


    //查找文件夹下图片的数量\

    private void getFiles(String rootPath, Bitmap mBitmap) {

        saveImageType = 2; //表示正在搜索图片
        Log.i("TAG_11", "getFiles: rootPath = " + rootPath);
        File rootFile = new File(rootPath);
        Observable.just(rootFile)
                //遍历文件夹，通过 flatMap 转换为 Observable<File>
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return listFiles(file);
                    }
                })
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        String str = file.getName();
                        return str.endsWith(".jpg") || str.endsWith(".png") || str.endsWith(".jpeg");
                    }
                })
                //得到复合条件的文件名
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return file.getName();
                    }
                })
                //通过toList装到集合(如果不用 toList，那就直接返回String，如果有1000条记录，那么onNext就会执行1000次)
                .toList()

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //通过订阅发送给观察者
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        saveImageType = 0;
                        if (mListener != null) {
                            mListener.onSaveImageFailed(0);
                        }
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        Log.i("TAG_11", "onNext:strings.size()=  "+strings.size());
                        if (strings.size() <3) { //图片少于三张，就保存照片
                            saveImageToPng(rootPath, mBitmap);
                        } else {
                            saveImageType = 0;
                            if (mListener != null) {
                                mListener.onSaveImageFailed(1);
                            }
                        }

                    }
                });


    }


    //遍历文件夹
    private Observable<File> listFiles(File file) {
        if (file.isDirectory()) {
            return Observable.from(file.listFiles())
                    .flatMap(new Func1<File, Observable<File>>() {
                        @Override
                        public Observable<File> call(File file) {
                            return listFiles(file);
                        }
                    });
        } else {
            return Observable.just(file);
        }

    }

    //将bitmap 输出到本地png
    private void saveImageToPng(String parentFile, Bitmap mBitmap) {

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int index = 0;
                //找到文件夹之后，指定文件的名字
                String fileName = System.currentTimeMillis() + ".png";
                File file = new File(parentFile, fileName);
                //将 Bitmap 写入到文件中
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    if (mBitmap != null) {
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        index = 1;
                    }
                } catch (FileNotFoundException e) {
                    Log.i("TAG_11", "404-call: " + e.getMessage());
                    index = 0;
                } finally {
                    CloseableUtils.closeQuietly(out);
                    subscriber.onNext(index);
                }

            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer s) {

                        if (s == 1) {
                            //表示已经保存了
                            saveImageType = 3;
                            if (mListener != null) {
                                reFresh();
                                //照片保存成功
                                reFresh();//保存之后刷新
                                mListener.onSaveImageSuccess();
                            }
                        } else {
                            saveImageType = 0;
                            if (mListener != null) {
                                mListener.onSaveImageFailed(0);
                            }
                        }

                    }
                });
    }

    public interface OnSaveImageListener {

        void onSaveImageSuccess();

        void onSaveImageFailed(int code);
    }


    public void setOnSaveImageListener(OnSaveImageListener listener) {
        mListener = listener;
    }


}
