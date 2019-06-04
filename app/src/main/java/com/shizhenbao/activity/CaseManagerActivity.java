package com.shizhenbao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.ImageBrowserActivity;
import com.activity.R;
import com.activity.VideoPlayerActivity;
import com.adapter.RecycleViewDivider;
import com.adapter.SDImageRecyclerAdapter;
import com.adapter.SDVideoRecyclerAdapter;
import com.application.MyApplication;
import com.bumptech.glide.Glide;
import com.model.SDVideoModel;
import com.orhanobut.logger.Logger;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.LogUtil;
import com.util.AlignedTextUtils;
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


public class CaseManagerActivity extends AppCompatActivity implements View.OnClickListener,SDVideoRecyclerAdapter.OnTextClickListner{

    private User mUser;
    private TextView tv_dengji,tv_result, tv_pinggu,title,tv_imagenameshow01,tv_case_video;
    private Button btn_left, btn_right;
    private ImageViewRotation image;
    private Button btnPre, btnNext;
    public SDVideoRecyclerAdapter adapter;//视频播放
    public List<SDVideoModel> mVideos = new ArrayList<>();
    public LinearLayoutManager mLinearLayoutManager;
    //存放图片的路径
//    private List<String> imagePathList = new ArrayList<>();
    public ArrayList<String> urlsImage = new ArrayList<>(); //保存图片的路径
//    private boolean isChain = true;//判断语言是否是中文
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_casemanager);

        initView();
//        getIntent().getBundleExtra("user");

        mUser= (User) getIntent().getSerializableExtra("user");
//        Logger.e("选中的患者的信息 ：" + mUser.getpName() + "  选中患者的电话： " + mUser.getTel());


        showUserInfoDengji();//显示登记信息
        showUserInfoResult();//显示报告打印界面的诊断信息
        showUserInfoPingGu();//显示报告打印界面的评估信息
//        getImagePathFromSD(mUser.getGatherPath());//获取图片资源
//        Logger.e(" CaseManagerActivity   患者路径 = "+mUser.getGatherPath());



    }

    @Override
    protected void onStart() {
        super.onStart();
        Const.videoType = 2;
        getImageData();

        initRecylerViewData();

    }

    private void initView() {
        tv_dengji = (TextView) findViewById(R.id.tv_dengji_clm);//展示登记信息
        tv_result = (TextView) findViewById(R.id.tv_zhenduanresult_clm);//展示诊断结果
        tv_pinggu = (TextView) findViewById(R.id.tv_pinggu_clm);//展示评估结果
        tv_case_video = (TextView) findViewById(R.id.tv_case_video);//视频列表
        tv_imagenameshow01 = (TextView) findViewById(R.id.tv_imagenameshow01);//图像展示
        title = (TextView) findViewById(R.id.title_text);//title
        title.setText(getString(R.string.case_message_title));
        btn_left = (Button) findViewById(R.id.btn_left);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.GONE);
        btn_right.setText("");

        btnPre = (Button) findViewById(R.id.btn_pre01);//上一张
        btnNext = (Button) findViewById(R.id.btn_next01);//下一张
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        //显示图片
        image = (ImageViewRotation) findViewById(R.id.image_clm);
        image.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.case_recycler_vdieo);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.divider)));
        mUser = new User();
//        isChain = MyApplication.getInstance().getCountry().endsWith("CN");

        adapter = new SDVideoRecyclerAdapter(this, mVideos);
        adapter.setOnTextClickListner(this);
//        adapter.setDevModel(model);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(adapter);
//        tv_dengji.setText(AlignedTextUtils.addForeColorSpan("细胞学"));
    }

    StringBuilder sb1 = new StringBuilder();
    List<Integer> listAllLength1 = new ArrayList<>();
    List<Integer> startList1 = new ArrayList<>();
    List<Integer> endList1 = new ArrayList<>();
    //登记
    private void showUserInfoDengji() {
        if (mUser != null) {
                sb1.delete(0, sb1.length());
                listAllLength1.clear();
                startList1.clear();
                startList1.clear();
                sb1.append(getString(R.string.patient_id)+" : "+mUser.getpId().toString() + "\n\n");
                startList1.add(0);
                endList1.add(getString(R.string.patient_id).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_name)+" : "+mUser.getpName().toString() + "\n\n");
                startList1.add(listAllLength1.get(0));
                endList1.add(listAllLength1.get(0)+getString(R.string.patient_name).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_telephone)+" : "+mUser.getTel().toString() + "\n\n");
                startList1.add(listAllLength1.get(1));
                endList1.add(listAllLength1.get(1)+getString(R.string.patient_telephone).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_age)+" : "+mUser.getAge().toString() + "\n\n");
                startList1.add(listAllLength1.get(2));
                endList1.add(listAllLength1.get(2)+getString(R.string.patient_age).length());
                listAllLength1.add( sb1.toString().length());


                sb1.append(getString(R.string.patient_gravidity_num)+" : "+mUser.getPregnantCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(3));
                endList1.add(listAllLength1.get(3)+getString(R.string.patient_gravidity_num).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_parity_num)+" : "+mUser.getChildCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(4));
                endList1.add(listAllLength1.get(4)+getString(R.string.patient_parity_num).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_occupation)+" : "+mUser.getWork().toString() + "\n\n");
                startList1.add(listAllLength1.get(5));
                endList1.add(listAllLength1.get(5)+getString(R.string.patient_occupation).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_smoking_history)+" : "+mUser.getSmokeTime().toString() + "\n\n");
                startList1.add(listAllLength1.get(6));
                endList1.add(listAllLength1.get(6)+getString(R.string.patient_smoking_history).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_abortion_num)+" : "+mUser.getAbortionCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(7));
                endList1.add(listAllLength1.get(7)+getString(R.string.patient_abortion_num).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_sex_num)+" : "+mUser.getSexPartnerCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(8));
                endList1.add(listAllLength1.get(8)+getString(R.string.patient_sex_num).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_medical_record_number)+" : "+mUser.getCaseNumbe().toString() + "\n\n");
                startList1.add(listAllLength1.get(9));
                endList1.add(listAllLength1.get(9)+getString(R.string.patient_medical_record_number).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_social_security_number)+" : "+mUser.getsSNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(10));
                endList1.add(listAllLength1.get(10)+getString(R.string.patient_social_security_number).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_HCG)+" : "+mUser.gethCG().toString() + "\n\n");
//            sb.append("真的好"+" : "+mUser.gethCG() + "\n\n");
                startList1.add(listAllLength1.get(11));
                endList1.add(listAllLength1.get(11)+getString(R.string.patient_HCG).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_ID)+" : "+mUser.getIdNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(12));
                endList1.add(listAllLength1.get(12)+getString(R.string.patient_ID).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_check_notes)+" : "+mUser.getCheckNotes().toString() + "\n\n");
                startList1.add(listAllLength1.get(13));
                endList1.add(listAllLength1.get(13)+getString(R.string.patient_check_notes).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_blood_type)+" : "+mUser.getBloodType().toString() + "\n\n");
                startList1.add(listAllLength1.get(14));
                endList1.add(listAllLength1.get(14)+getString(R.string.patient_blood_type).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_contraceptive_methods)+" : "+mUser.getBirthControlMode().toString() + "\n\n");
                startList1.add(listAllLength1.get(15));
                endList1.add(listAllLength1.get(15)+getString(R.string.patient_contraceptive_methods).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_marriage)+" : "+mUser.getMarry().toString() + "\n\n");
                startList1.add(listAllLength1.get(16));
                endList1.add(listAllLength1.get(16)+getString(R.string.patient_marriage).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_source)+" : "+mUser.getpSource().toString() + "\n\n");
                startList1.add(listAllLength1.get(17));
                endList1.add(listAllLength1.get(17)+getString(R.string.patient_source).length());
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.case_image_show_time)+" : "+mUser.getRegistDate().toString() + "\n\n");
                startList1.add(listAllLength1.get(18));
                endList1.add(listAllLength1.get(18)+getString(R.string.case_image_show_time).length());
                listAllLength1.add( sb1.toString().length());

                tv_dengji.setText(AlignedTextUtils.addConbine1(sb1.toString(),startList1,endList1));
                Logger.e("登记信息：sb.toString()" +sb1.toString());
            }
    }

    StringBuilder sb2 = new StringBuilder();
    List<Integer> listAllLength2 = new ArrayList<>();
    List<Integer> startList2 = new ArrayList<>();
    List<Integer> endList2 = new ArrayList<>();
    //诊断结果
    private void showUserInfoResult() {
        if (mUser != null) {
                sb2.delete(0, sb2.length());
                listAllLength2.clear();
                startList2.clear();
                endList2.clear();
                if (mUser.getSymptom() != null) {
                    sb2.append(getString(R.string.print_symptom) + " : " + mUser.getSymptom() + "\n\n");
                    startList2.add(0);
                    endList2.add(getString(R.string.print_symptom).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_symptom) + " : " + "\n\n");
                    startList2.add(0);
                    endList2.add(getString(R.string.print_symptom).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getAdvice() != null) {
                    sb2.append(getString(R.string.print_cytology) + " : " + mUser.getAdvice() + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + getString(R.string.print_cytology).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_cytology) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + getString(R.string.print_cytology).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHpv_dna() != null) {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + mUser.getHpv_dna() + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + getString(R.string.print_HPV_DNA).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + getString(R.string.print_HPV_DNA).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getBingbian() != null) {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + mUser.getBingbian() + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + getString(R.string.print_Lesion_area).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + getString(R.string.print_Lesion_area).length());
                    listAllLength2.add(sb2.toString().length());
                }


                if (mUser.getSummary() != null) {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + mUser.getSummary() + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + getString(R.string.print_Overall_assessment).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + getString(R.string.print_Overall_assessment).length());
                    listAllLength2.add(sb2.toString().length());
                }



                if (mUser.getYindaojin() != null) {
                    sb2.append(getString(R.string.print_colposcopic)+ " : " + mUser.getYindaojin() + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + getString(R.string.print_colposcopic).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_colposcopic) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + getString(R.string.print_colposcopic).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getNizhen() != null) {
                    sb2.append(getString(R.string.print_Suspected)+ " : " + mUser.getNizhen() + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + getString(R.string.print_Suspected).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Suspected) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + getString(R.string.print_Suspected).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getZhuyishixiang() != null) {
                    sb2.append(getString(R.string.print_attention)+ " : " + mUser.getZhuyishixiang() + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) +getString(R.string.print_attention).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_attention) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) + getString(R.string.print_attention).length());
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHandle() != null) {
                    sb2.append(getString(R.string.print_opinion) + " : " + mUser.getHandle() + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + getString(R.string.print_opinion).length());
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_opinion) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + getString(R.string.print_opinion).length());
                    listAllLength2.add(sb2.toString().length());
                }

                try {
                    tv_result.setText(AlignedTextUtils.addConbine2(sb2.toString(), startList2, endList2));
                } catch (Exception e) {
                    Logger.e("病例对比时 诊断时的 错误信息： " + e.getMessage());
                    e.printStackTrace();
                }

            }

    }




    StringBuilder sb3 = new StringBuilder();
    List<Integer> listAllLength3 = new ArrayList<>();
    List<Integer> startList3 = new ArrayList<>();
    List<Integer> endList3 = new ArrayList<>();
   //评估
    private void showUserInfoPingGu() {
        if (mUser != null) {
                sb3.delete(0, sb3.length());
                listAllLength3.clear();
                startList3.clear();
                endList3.clear();
                if (mUser.getBingbian() != null) {
                    sb3.append(getString(R.string.print_border) + " : " + mUser.getBingbian() + "\n\n");
                    startList3.add(0);
                    endList3.add(getString(R.string.print_border).length());
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_border)+ " : "  + "\n\n");
                    startList3.add(0);
                    endList3.add(getString(R.string.print_border).length());
                    listAllLength3.add(sb3.toString().length());
                }

                if (mUser.getColor() != null) {
                    sb3.append(getString(R.string.print_color) + " : " + mUser.getColor() + "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0) + getString(R.string.print_color).length());
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_color)+" : "+ "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0)+getString(R.string.print_color).length());
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getXueguna() != null) {
                    sb3.append(getString(R.string.print_blood_vessel)+ " : " + mUser.getXueguna() + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1) + getString(R.string.print_blood_vessel).length());
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_blood_vessel)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1)+getString(R.string.print_blood_vessel).length());
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getDianranse() != null) {
                    sb3.append(getString(R.string.print_Iodine_staining) + " : " + mUser.getDianranse() + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2) + getString(R.string.print_Iodine_staining).length());
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Iodine_staining)+ " : " + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2)+getString(R.string.print_Iodine_staining).length());
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getCusuan() != null) {
                    sb3.append(getString(R.string.print_Acetic_acid_change) + " : " + mUser.getCusuan() + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3) + getString(R.string.print_Acetic_acid_change).length());
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Acetic_acid_change)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3)+ getString(R.string.print_Acetic_acid_change).length());
                    listAllLength3.add( sb3.toString().length());
                }
                tv_pinggu.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
            }
    }


    //展示图片
    private void showImages(int position) {
        if (urlsImage.size() < 1) {
            return;
        }
            //显示照片
            Glide.with(this)//图片加载框架
                    .load(urlsImage.get(position))//图片的路径
                    .crossFade()
                    .into(image);
            //显示照片的名称
            getPathSplitName(urlsImage.get(position));

    }


//    //获取bitmap
//    public static Bitmap getBitmap(String filePath) {
//        if (filePath == null) {
//            return null;
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath,options);
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(filePath, options);
//
//    }

//    //获取bitmap
//    public static Bitmap getBitmap(String filePath,int maxWidth,int maxHeight) {
//        if (filePath == null) {
//            return null;
//        }
//        BitmapFactory.Options options = null;
//        try {
//            options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(filePath,options);
//            options.inSampleSize = computeSampleSize(options, maxWidth, maxHeight);
//            options.inJustDecodeBounds = false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return BitmapFactory.decodeFile(filePath, options);
//
//    }

    //计算 inSampleSize
//    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
//        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
//        int roundedSize;
//        if (initialSize <= 8) {
//            roundedSize = 1;
//            while (roundedSize < initialSize) {
//                roundedSize <<= 1;
//            }
//        } else {
//            roundedSize = (initialSize + 7) / 8 * 8;
//        }
//        return roundedSize;
//    }
//
//    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
//        double w = options.outWidth;
//        double h = options.outHeight;
//        int lowerBound = (maxNumOfPixels == -1) ?
//                1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
//
//        int upperBound = (minSideLength == -1) ?
//                128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
//
//        if (upperBound < lowerBound) {
//            // return the larger one when there is no overlapping zone.
//            return lowerBound;
//        }
//        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
//            return 1;
//        } else if (minSideLength == -1) {
//            return lowerBound;
//        } else {
//            return upperBound;
//        }
//    }

    //图片位置
    private static int index=0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                break;
            case R.id.btn_pre01://上一张
                Const.imagepositionSingle = Const.imagepositionSingle - 1;
                if (Const.imagepositionSingle < 0) {
                    Const.imagepositionSingle = 0;
                    MyToast.showToast(this, getString(R.string.image_manager_picture_nothing));
//                    Toast.makeText(this, getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                }
                showImages(Const.imagepositionSingle);
//                if (imagePathList.size() == 0||!isFastClick()) {
//                    return;
//                }
//                index--;
//                if (index  >= 0&&index<imagePathList.size()) {
//                    showImages(imagePathList.get(index));
//
//                } else {
//                    showImages(imagePathList.get(imagePathList.size()-1));
//                    index = imagePathList.size()-1;
//                }
                break;
            case R.id.btn_next01://下一张
                Const.imagepositionSingle = Const.imagepositionSingle + 1;
                if (Const.imagepositionSingle > urlsImage.size() - 1) {
                    Const.imagepositionSingle = urlsImage.size() - 1;
                    MyToast.showToast(this, getString(R.string.image_manager_picture_nothing));
//                    Toast.makeText(this, getString(R.string.image_manager_picture_nothing), Toast.LENGTH_SHORT).show();
                }
                showImages(Const.imagepositionSingle);
                    break;
            case R.id.image_clm:

                //        显示放大之后的图片
                Intent intent = new Intent(this, ImageBrowserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("urls", urlsImage);
                bundle.putInt("channel", Const.imagepositionSingle);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }


    }

    /**
     * 获取图片资源
     * @return
     */

    private void getImageData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(mUser.getGatherPath());
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
                        showListImage(s);
                    }
                });



    }



    //展示该文件下病人所有的..jpg格式的图片
    public void showListImage( String  filePath) {
        urlsImage.clear();
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
                            image.setVisibility(View.VISIBLE);
                            btnPre.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.VISIBLE);
                            tv_imagenameshow01.setVisibility(View.VISIBLE);

                            showImages(Const.imagepositionSingle);//展示图片

                        } else {
                            image.setVisibility(View.GONE);
                            btnPre.setVisibility(View.GONE);
                            btnNext.setVisibility(View.GONE);
                            tv_imagenameshow01.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
//                        SDImageModel sdImageModel = new SDImageModel();
//                        sdImageModel.sdImage = s;
//                        mImages.add(sdImageModel);
                        urlsImage.add(s);
//                        Logger.e(" 图片的数量 onNext mImages = " + s);
                    }
                });
    }










//    private void getImagePathFromSD(String filePath) {
//        // 图片列表
////        List<String> imagePathList = new ArrayList<String>();
//        // 得到sd卡内image文件夹的路径   File.separator(/)
////        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
////                + "image";
//        // 得到该路径文件夹下所有的文件
//        if (filePath == null) {
//            return;
//        }
//        Logger.e("   filePath = "+filePath);
//        File fileAll = new File(filePath);
//        File[] files = fileAll.listFiles();
//        try {
////            Logger.e("CaseManager    files.length = "+files.length);
//            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
//            for (int i = 0; i < files.length; i++) {
//                File file = files[i];
//                if (checkIsImageFile(file.getPath())) {
//    //                得到的图片列表
//                    imagePathList.add(file.getPath());
//                }
//            }
//        } catch (Exception e) {
//            Logger.e("查找文件夹报错"+e.getMessage());
////            e.printStackTrace();
//        }
////        return imagePathList;
//    }
//
//    /**
//     * 检查扩展名，得到图片格式的文件
//     * @param fName
//     * @return
//     */
//    private boolean checkIsImageFile(String fName) {
//        boolean isImageFile = false;
//        // 获取扩展名
//        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
//                fName.length()).toLowerCase();
//        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
//                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
//            if (fName.contains("方位.png")) {
//                isImageFile = false;
//            } else {
//                isImageFile = true;
//            }
//
//        } else {
//            isImageFile = false;
//        }
//        return isImageFile;
//    }


    // 两次点击按钮之间的点击间隔不能少于400毫秒
    private static final int MIN_CLICK_DELAY_TIME = 400;
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

//    private List<String> fileSplitNameList = new ArrayList<>();

    /**
     * 将图片路径进行分割，获取拍照时相关的信息
     *
     * @param filePath
     * @return
     */
    private void getPathSplitName(String filePath) {

        try {
//            sbImagePathName.delete(0, sbImagePathName.length());
            String[] split = filePath.split("[.]");
//            Logger.e("得到的路径= "+filePath+"    split.length="+split.length+" ----- "+split[1]);
            if (split.length > 0) {
//                Log.e("PRETTY_LOGGER", " split[0] = "+split[0]+"--  split[1] = "+split[1]+" -- split[2] = "+split[2]+" -- split[3] = "+split[3] );
//                Logger.e("  "+split[0]+"  "+split[1]+"  "+split[2]+"  "+split[3]);
                if (split[1].equals("cusuanbai")) {
                    String imageCatagory = changeInfo(split[1]);
                    tv_imagenameshow01.setText(getString(R.string.case_image_showbi)+":" + imageCatagory +" " + split[2] + getString(R.string.imageacitivty_acetowhite_second) + split[3] + getString(R.string.imageacitivty_acetowhite_minute));
                } else {
                    String imageCatagory = changeInfo(split[1]);
                    tv_imagenameshow01.setText(getString(R.string.case_image_showbi)+":" + imageCatagory );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String changeInfo(String filePath) {
        String imageCatagory =getString(R.string.image_artword);
        switch (filePath) {
            case "质控图":
                imageCatagory= getString(R.string.image_artword);
                break;
            case "醋酸白":
                imageCatagory= getString(R.string.image_acetic_acid_white);
                break;
            case "碘油":
                imageCatagory= getString(R.string.image_Lipiodol);
                break;
            default:
                break;

        }
        return imageCatagory;
    }

    //视频播放
    private void initRecylerViewData() {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(mUser.getGatherPath());
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
                        showListVideo(s);
                    }
                });


    }

    //展示该文件下病人所有的..jpg格式的图片
    public void showListVideo(final String  filePath) {
        mVideos.clear();

        File[] files = new File(filePath).listFiles(); //获取当前文件夹下的所有文件和文件夹
        Observable.from(files)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.getName().endsWith(".avi") ;
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

                        if (mVideos.size() == 0) {
                            tv_case_video.setVisibility(View.GONE);
                        } else {
                            tv_case_video.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        SDVideoModel sdVideoModel = new SDVideoModel();
                        sdVideoModel.sdVideo = s;
                        mVideos.add(sdVideoModel);

                    }
                });
    }






    @Override
    public void OnTextClicked(View view, String videoPath,int videoType) {
//        Logger.e(" 类型 type = "+videoType);
        if (videoType==0||videoType ==1) {
            return;
        }
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("videoPath",videoPath);
        intent.putExtras(bundle);
        startActivity(intent);


    }

    @Override
    public void OnCheckClicked(int videoType) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Const.videoType = 0;
        Const.imagepositionSingle = 0;
    }

}
