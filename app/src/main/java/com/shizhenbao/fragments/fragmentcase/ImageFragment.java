package com.shizhenbao.fragments.fragmentcase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/9/13.
 */

public class ImageFragment extends Fragment implements View.OnClickListener {

    private Button btnPre01,btnNext01,btnPre02, btnNext02;
    private ImageView image01, image02;
    private TextView tvName01, tvName02;
    private List<User> mList = new ArrayList<User>();
    private List<String> listImage01 = new ArrayList<String>();
    private List<String> listImage02 = new ArrayList<String>();

    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    listImage01 = imagePathList1;
                    if (listImage01.size() != 0) {
//                        tvName01.setText(Const.mListUser.get(0).getpName());
                        showImages01(listImage01.get(0));
                    }
                    break;
                case 1:
                    listImage02 = imagePathList2;
                    if (listImage02.size() != 0) {
                        showImages02(listImage02.get(0));
//                        tvName02.setText(Const.mListUser.get(1).getpName());
                    }
                    break;

                case 2:
                    Toast.makeText(getActivity(), R.string.image_manager_picture_nothing, Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragimage_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btnPre01 = (Button) view.findViewById(R.id.btn_casepre01);//原图上一张
        btnNext01 = (Button) view.findViewById(R.id.btn_casenext01);//原图下一张
        btnPre02 = (Button) view.findViewById(R.id.btn_casepre02);//对比图上一张
        btnNext02 = (Button) view.findViewById(R.id.btn_casenext02);//对比图下一张
        image01 = (ImageView) view.findViewById(R.id.frag_imagecase_01);//显示原图
        image02 = (ImageView) view.findViewById(R.id.frag_imagecase_02);//显示对比图
        tvName01 = (TextView) view.findViewById(R.id.textview_caseName01); //对比病例的姓名
        tvName02 = (TextView) view.findViewById(R.id.textview_caseName02); //对比病例的姓名

        btnPre01.setOnClickListener(this);
        btnNext01.setOnClickListener(this);
        btnPre02.setOnClickListener(this);
        btnNext02.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
//        mList = Const.mListUser;
        if ( Const.mListUser.size() > 0) {
            tvName01.setText("");
            tvName02.setText("");

            try {
                getImagePathFromSD( Const.mListUser.get(0).getGatherPath()+"/PHOTOS",1);//原图
                getImagePathFromSD02( Const.mListUser.get(1).getGatherPath()+"/PHOTOS",2);//对比图
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //图片位置
    private static int index=0;
    @Override
    public void onClick(View v) {

        try {
            switch (v.getId()) {

                case R.id.btn_casepre01://上一张
                    if (listImage01.size() == 0||!isFastClick()) {
                        return;
                    }
                    index--;
                    if (index  >= 0) {
                        showImages01(listImage01.get(index));

                    } else {
    //                    Toast.makeText(this, "当前是最后一张", Toast.LENGTH_SHORT).show();
                        showImages01(listImage01.get(listImage01.size()-1));
                        index = listImage01.size()-1;
                    }
    //                index--;
                    break;
                case R.id.btn_casenext01://下一张
                    if (listImage01.size() == 0||!isFastClick()) {
                        return;
                    }
                    index++;
                    Logger.e("照片的数量："+ listImage01.size());
                    if (index  >=listImage01.size()) {
    //                    Toast.makeText(this, "当前是第一张", Toast.LENGTH_SHORT).show();
                        showImages01(listImage01.get(0));
                        index = 0;
                    } else {
                        showImages01(listImage01.get(index));
                    }

                    break;

    //---------------------------------------对比图------------------------------------------------
                case R.id.btn_casepre02://对比图上一张
                    if (listImage02.size() == 0||!isFastClick()) {
                        return;
                    }
                    index--;
                    if (index  >= 0) {
                        showImages02(listImage02.get(index));

                    } else {
    //                    Toast.makeText(this, "当前是最后一张", Toast.LENGTH_SHORT).show();
                        showImages02(listImage02.get(listImage02.size()-1));
                        index = listImage02.size()-1;
                    }
    //                index--;
                    break;
                case R.id.btn_casenext02://对比图下一张
                    if (listImage02.size() == 0||!isFastClick()) {
                        return;
                    }
                    index++;
                    Logger.e("照片的数量："+ listImage02.size());
                    if (index  >=listImage02.size()) {
    //                    Toast.makeText(this, "当前是第一张", Toast.LENGTH_SHORT).show();
                        showImages02(listImage02.get(0));
                        index = 0;
                    } else {
                        showImages02(listImage02.get(index));
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    //展示图片
    private void showImages01(String filePath) {
        Bitmap bitmap = getBitmap(filePath);
        if (bitmap !=null) {
            image01.setImageBitmap(bitmap);
            if (Const.mListUser.size() > 0) {
                getPathSplitName(filePath,tvName01,Const.mListUser.get(0).getpName());
            }

        }


    }

    //展示对比图片
    private void showImages02(String filePath) {
        Bitmap bitmap = getBitmap(filePath);
        if (bitmap !=null) {
            image02.setImageBitmap(bitmap);
            if (Const.mListUser.size() > 1) {
                getPathSplitName(filePath,tvName02,Const.mListUser.get(1).getpName());
            }

        }

    }


    //获取bitmap
    public static Bitmap getBitmap(String filePath) {
        if (filePath == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }

    //获取bitmap
    public static Bitmap getBitmap(String filePath,int maxWidth,int maxHeight) {
        if (filePath == null) {
            return null;
        }
        BitmapFactory.Options options = null;
        try {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath,options);
            options.inSampleSize = computeSampleSize(options, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(filePath, options);

    }

    //计算 inSampleSize
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ?
                1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ?
                128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }



    /**
     * 获取图片资源
     *
     * @return
     */
    final List<String> imagePathList1 = new ArrayList<String>();
//    final List<String> imagePathList2 = new ArrayList<String>();
    private void getImagePathFromSD(final String filePath, final int index) {
//        final List<String> imagePathList1 = new ArrayList<String>();
//        imagePathList1.clear();
       final Message msg = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (filePath != null) {
//                    return null;
                    File fileAll = new File(filePath);
                    File[] files = fileAll.listFiles();
                    // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (checkIsImageFile(file.getPath())) {
//                得到的图片列表
                            imagePathList1.add(file.getPath());
//                            list02.add(file.getPath());
                        }
                    }
                    if (index == 1) {
                        msg.obj = imagePathList1;
//                        listImage01 = imagePathList1;
                        handle.sendEmptyMessage(0);
                    } else if (index==2){
                        msg.obj = imagePathList1;
                        handle.sendEmptyMessage(1);
                    }

                } else {
                    handle.sendEmptyMessage(2);
                }

            }
        }).start();



        // 图片列表
//        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
//        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
//                + "image";
        // 得到该路径文件夹下所有的文件

    }


    final List<String> imagePathList2 = new ArrayList<String>();
    private void getImagePathFromSD02(final String filePath, final int index) {

//        imagePathList1.clear();
        final Message msg = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (filePath != null) {
//                    return null;
                    File fileAll = new File(filePath);
                    File[] files = fileAll.listFiles();
                    // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (checkIsImageFile(file.getPath())) {
//                得到的图片列表
                            imagePathList2.add(file.getPath());
//                            list02.add(file.getPath());
                        }
                    }
                    if (index == 1) {
//                        msg.obj = imagePathList1;
//                        listImage01 = imagePathList1;
                        handle.sendEmptyMessage(0);
                    } else if (index==2){
//                        msg.obj = imagePathList1;
                        handle.sendEmptyMessage(1);
                    }

                } else {
                    handle.sendEmptyMessage(2);
                }

            }
        }).start();



        // 图片列表
//        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
//        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
//                + "image";
        // 得到该路径文件夹下所有的文件

    }






    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName
     * @return
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {

            if (fName.contains("方位.png")) {
                isImageFile = false;
            } else {
                isImageFile = true;
            }

        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    /**
     * 将图片路径进行分割，获取拍照时相关的信息
     *
     * @param filePath
     * @return
     */
    private void getPathSplitName(String filePath,TextView textView,String  name) {
        if (textView == null) {
            return;
        }
        try {
//            sbImagePathName.delete(0, sbImagePathName.length());
            String[] split = filePath.split("[.]");
//            Logger.e("得到的路径= "+filePath+"    split.length="+split.length+" ----- "+split[1]);
            if (split.length > 0) {
//                Log.e("PRETTY_LOGGER", " split[0] = "+split[0]+"--  split[1] = "+split[1]+" -- split[2] = "+split[2]+" -- split[3] = "+split[3] );
//                Logger.e("  "+split[0]+"  "+split[1]+"  "+split[2]+"  "+split[3]);
                if (split[1].equals("cusuanbai")) {
                    String imageCatagory = changeInfo(split[1]);
                    textView.setText("图片展示:(" + name+")"+imageCatagory + "状态下," + split[2] + "秒/时长" + split[3] + "分钟");
                } else {
                    String imageCatagory = changeInfo(split[1]);
                    textView.setText("图片展示:(" + name+")" + imageCatagory );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String changeInfo(String filePath) {
        String imageCatagory = "原图";
        switch (filePath) {
            case "yuantu":
                imageCatagory= "原图";
                break;
            case "cusuanbai":
                imageCatagory= "醋酸白";
                break;
            case "dianyou":
                imageCatagory= "碘油";
                break;
            default:
                break;

        }
        return imageCatagory;
    }


    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
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


}
