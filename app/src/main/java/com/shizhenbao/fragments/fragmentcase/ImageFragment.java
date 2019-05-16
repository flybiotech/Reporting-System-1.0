package com.shizhenbao.fragments.fragmentcase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activity.ImageBrowserActivity;
import com.activity.R;
import com.bumptech.glide.Glide;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.util.SouthUtil;
import com.view.ImageViewRotation;
import com.view.MyToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by dell on 2017/9/13.
 */

public class ImageFragment extends Fragment implements View.OnClickListener {

    private Button btnPre01,btnNext01,btnPre02, btnNext02;
    private ImageViewRotation image01, image02;
    private TextView tvName01, tvName02;
    private List<User> mList = new ArrayList<User>();
    private ArrayList<String> listImage01 = new ArrayList<String>();
    private ArrayList<String> listImage02 = new ArrayList<String>();


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
        image01 = (ImageViewRotation) view.findViewById(R.id.frag_imagecase_01);//显示原图
        image02 = (ImageViewRotation) view.findViewById(R.id.frag_imagecase_02);//显示对比图
        tvName01 = (TextView) view.findViewById(R.id.textview_caseName01); //对比病例的姓名
        tvName02 = (TextView) view.findViewById(R.id.textview_caseName02); //对比病例的姓名


        btnPre01.setOnClickListener(this);
        btnNext01.setOnClickListener(this);
        btnPre02.setOnClickListener(this);
        btnNext02.setOnClickListener(this);
        image01.setOnClickListener(this);
        image02.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
//        mList = Const.mListUser;
        if ( Const.mListUser.size() > 0) {
            tvName01.setText("");
            tvName02.setText("");

            try {
                getImageData(Const.mListUser.get(0),Const.imageposition,listImage01,image01,tvName01);
                getImageData(Const.mListUser.get(1),Const.imagepositioncompare,listImage02,image02,tvName02);

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
                    Const.imageposition = Const.imageposition - 1;
                    if (Const.imageposition < 0) {
                        Const.imageposition = 0;
                        MyToast.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        SouthUtil.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        Toast.makeText(MyApplication.getContext(), getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                    }
                    showImages(Const.imageposition,listImage01,Const.mListUser.get(0),image01,tvName01);

                    break;
                case R.id.btn_casenext01://下一张

                    Const.imageposition = Const.imageposition + 1;
                    if (Const.imageposition > listImage01.size() - 1) {
                        Const.imageposition = listImage01.size() - 1;
                        MyToast.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        SouthUtil.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        Toast.makeText(MyApplication.getContext(), getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                    }
                    showImages(Const.imageposition,listImage01,Const.mListUser.get(0),image01,tvName01);

                    break;

    //---------------------------------------对比图------------------------------------------------
                case R.id.btn_casepre02://对比图上一张
                    Const.imagepositioncompare = Const.imagepositioncompare - 1;
                    if (Const.imagepositioncompare < 0) {
                        Const.imagepositioncompare = 0;
                        MyToast.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        SouthUtil.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        Toast.makeText(MyApplication.getContext(), getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                    }
                    showImages(Const.imagepositioncompare,listImage02,Const.mListUser.get(1),image02,tvName02);

                    break;
                case R.id.btn_casenext02://对比图下一张
                    Const.imagepositioncompare = Const.imagepositioncompare + 1;
                    if (Const.imagepositioncompare > listImage02.size() - 1) {
                        Const.imagepositioncompare = listImage02.size() - 1;
                        MyToast.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        SouthUtil.showToast(getActivity(),getString(R.string.image_manager_picture_nothing));
//                        Toast.makeText(MyApplication.getContext(), getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                    }
                    showImages(Const.imagepositioncompare,listImage02,Const.mListUser.get(1),image02,tvName02);
                    break;

                case R.id.frag_imagecase_01: //图像展示的点击事件
                    //        显示放大之后的图片
                    Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("urls", listImage01);
                    bundle.putInt("channel", Const.imageposition);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case R.id.frag_imagecase_02:
                    //        显示放大之后的图片
                    Intent intent2 = new Intent(getActivity(), ImageBrowserActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putStringArrayList("urls", listImage02);
                    bundle2.putInt("channel", Const.imagepositioncompare);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    //展示图片
    private void showImages(int position,List<String>urlsImage,User user,ImageView image,TextView imageName) {
        if (urlsImage.size() < 1) {
            return;
        }
        //显示照片
        Glide.with(this)//图片加载框架
                .load(urlsImage.get(position))//图片的路径
                .crossFade()
                .into(image);
        //显示照片的名称
//        getPathSplitName(urlsImage.get(position));
        getPathSplitName(urlsImage.get(position),imageName,user.getpId(),user.getpName(),user.getRegistDate());
    }


    /**
     * 获取图片资源
     */

    private void getImageData(final User user, final int position, final List<String>list, final ImageView image, final TextView imageName) {
        list.clear();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(user.getGatherPath());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        showListImage(s,position,list,user,image,imageName);
                    }
                });



    }



    //展示该文件下病人所有的..jpg格式的图片
    public void showListImage(String  filePath, final int position, final List<String>urlsImage, final User user, final ImageView image, final TextView imageName) {

        File[] files = new File(filePath).listFiles(); //获取当前文件夹下的所有文件和文件夹
        Observable.from(files)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.getName().endsWith(".jpg") && !file.getName().equals("方位.png");
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return file.getAbsolutePath();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        if (urlsImage.size() > 0) {
                            showImages(position,urlsImage,user,image,imageName);//展示图片
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        urlsImage.add(s);
                    }
                });
    }




    /**
     * 将图片路径进行分割，获取拍照时相关的信息
     *
     * @param filePath
     * @return
     */
    private void getPathSplitName(String filePath,TextView textView,String pId,String  name,String registerDate) {
        if (textView == null) {
            return;
        }
        try {
            String[] split = filePath.split("[.]");
            if (split.length > 0) {


                if (split[1].equals("cusuanbai")) {
                    String imageCatagory = changeInfo(split[1]);
                    textView.setText(getString(R.string.patient_id)+":"+pId+"\n"+getString(R.string.patient_name) + ":"+ name
                            + "\n" + getString(R.string.case_image_show_time) + ":" + registerDate
                            + "\n"+ getString(R.string.case_image_showbi)+ ":" + imageCatagory + " " + split[2] + getString(R.string.imageacitivty_acetowhite_second) + split[3] + getString(R.string.imageacitivty_acetowhite_minute));
                } else {
                    String imageCatagory = changeInfo(split[1]);
                    textView.setText(getString(R.string.patient_id)+":"+pId+"\n"+getString(R.string.patient_name) + ":" + name
                            + "\n" + getString(R.string.case_image_show_time) + ":" + registerDate
                            + "\n" + getString(R.string.case_image_showbi) + ":" + imageCatagory);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String changeInfo(String filePath) {
        String imageCatagory =getString(R.string.image_artword);
        switch (filePath) {
            case "yuantu":
                imageCatagory= getString(R.string.image_artword);
                break;
            case "cusuanbai":
                imageCatagory= getString(R.string.image_acetic_acid_white);
                break;
            case "dianyou":
                imageCatagory= getString(R.string.image_Lipiodol);
                break;
            default:
                break;

        }
        return imageCatagory;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Const.imageposition = 0;
        Const.imagepositioncompare = 0;

    }
}
