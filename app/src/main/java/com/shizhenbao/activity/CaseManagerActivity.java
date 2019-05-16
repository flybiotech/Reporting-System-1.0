package com.shizhenbao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.application.MyApplication;
import com.orhanobut.logger.Logger;
import com.shizhenbao.pop.User;
import com.util.AlignedTextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CaseManagerActivity extends AppCompatActivity implements View.OnClickListener{

    private User mUser;
    private TextView tv_dengji,tv_result, tv_pinggu,title,tv_imagenameshow01;
    private Button btn_left, btn_right;
    private ImageView image;
    private Button btnPre, btnNext;
    //存放图片的路径
    private List<String> imagePathList = new ArrayList<>();
    private boolean isChain = true;//判断语言是否是中文

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casemanager);
        initView();
//        getIntent().getBundleExtra("user");

        mUser= (User) getIntent().getSerializableExtra("user");
//        Logger.e("选中的患者的信息 ：" + mUser.getpName() + "  选中患者的电话： " + mUser.getTel());


        showUserInfoDengji();//显示登记信息
        showUserInfoResult();//显示报告打印界面的诊断信息
        showUserInfoPingGu();//显示报告打印界面的评估信息
        getImagePathFromSD(mUser.getGatherPath()+"/PHOTOS");//获取图片资源
//        Logger.e(" CaseManagerActivity   患者路径 = "+mUser.getGatherPath());



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imagePathList.size() > 0) {
            image.setVisibility(View.VISIBLE);
            btnPre.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            showImages(imagePathList.get(0));
        } else {
            image.setVisibility(View.GONE);
            btnPre.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);

        }

    }

    private void initView() {
        tv_dengji = (TextView) findViewById(R.id.tv_dengji_clm);//展示登记信息
        tv_result = (TextView) findViewById(R.id.tv_zhenduanresult_clm);//展示诊断结果
        tv_pinggu = (TextView) findViewById(R.id.tv_pinggu_clm);//展示评估结果
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
        image = (ImageView) findViewById(R.id.image_clm);

        mUser = new User();
        isChain = MyApplication.getInstance().getCountry().endsWith("CN");
//        tv_dengji.setText(AlignedTextUtils.addForeColorSpan("细胞学"));
    }

    StringBuilder sb1 = new StringBuilder();
    List<Integer> listAllLength1 = new ArrayList<>();
    List<Integer> startList1 = new ArrayList<>();
    List<Integer> endList1 = new ArrayList<>();
    //登记
    private void showUserInfoDengji() {
        if (mUser != null) {
            if (isChain) {
                sb1.delete(0, sb1.length());
                listAllLength1.clear();
                startList1.clear();
                startList1.clear();
                sb1.append(getString(R.string.patient_id)+" : "+mUser.getpId().toString() + "\n\n");
                startList1.add(0);
                endList1.add(2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_name)+" : "+mUser.getpName().toString() + "\n\n");
                startList1.add(listAllLength1.get(0));
                endList1.add(listAllLength1.get(0)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_telephone)+" : "+mUser.getTel().toString() + "\n\n");
                startList1.add(listAllLength1.get(1));
                endList1.add(listAllLength1.get(1)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_age)+" : "+mUser.getAge().toString() + "\n\n");
                startList1.add(listAllLength1.get(2));
                endList1.add(listAllLength1.get(2)+2);
                listAllLength1.add( sb1.toString().length());


                sb1.append(getString(R.string.patient_gravidity_num)+" : "+mUser.getPregnantCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(3));
                endList1.add(listAllLength1.get(3)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_parity_num)+" : "+mUser.getChildCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(4));
                endList1.add(listAllLength1.get(4)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_occupation)+" : "+mUser.getWork().toString() + "\n\n");
                startList1.add(listAllLength1.get(5));
                endList1.add(listAllLength1.get(5)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_smoking_history)+" : "+mUser.getSmokeTime().toString() + "\n\n");
                startList1.add(listAllLength1.get(6));
                endList1.add(listAllLength1.get(6)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_abortion_num)+" : "+mUser.getAbortionCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(7));
                endList1.add(listAllLength1.get(7)+4);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_sex_num)+" : "+mUser.getSexPartnerCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(8));
                endList1.add(listAllLength1.get(8)+4);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_medical_record_number)+" : "+mUser.getCaseNumbe().toString() + "\n\n");
                startList1.add(listAllLength1.get(9));
                endList1.add(listAllLength1.get(9)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_social_security_number)+" : "+mUser.getsSNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(10));
                endList1.add(listAllLength1.get(10)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_HCG)+" : "+mUser.gethCG().toString() + "\n\n");
//            sb.append("真的好"+" : "+mUser.gethCG() + "\n\n");
                startList1.add(listAllLength1.get(11));
                endList1.add(listAllLength1.get(11)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_ID)+" : "+mUser.getIdNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(12));
                endList1.add(listAllLength1.get(12)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_check_notes)+" : "+mUser.getCheckNotes().toString() + "\n\n");
                startList1.add(listAllLength1.get(13));
                endList1.add(listAllLength1.get(13)+4);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_blood_type)+" : "+mUser.getBloodType().toString() + "\n\n");
                startList1.add(listAllLength1.get(14));
                endList1.add(listAllLength1.get(14)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_contraceptive_methods)+" : "+mUser.getBirthControlMode().toString() + "\n\n");
                startList1.add(listAllLength1.get(15));
                endList1.add(listAllLength1.get(15)+4);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_marriage)+" : "+mUser.getMarry().toString() + "\n\n");
                startList1.add(listAllLength1.get(16));
                endList1.add(listAllLength1.get(16)+2);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_source)+" : "+mUser.getpSource().toString() + "\n\n");
                startList1.add(listAllLength1.get(17));
                endList1.add(listAllLength1.get(17)+4);
                listAllLength1.add( sb1.toString().length());
                tv_dengji.setText(AlignedTextUtils.addConbine1(sb1.toString(),startList1,endList1));
                Logger.e("登记信息：sb.toString()" +sb1.toString());
            } else {//英文
                sb1.delete(0, sb1.length());
                listAllLength1.clear();
                startList1.clear();
                startList1.clear();
                sb1.append(getString(R.string.patient_id)+" : "+mUser.getpId().toString() + "\n\n");
                startList1.add(0);
                endList1.add(6);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_name)+" : "+mUser.getpName().toString() + "\n\n");
                startList1.add(listAllLength1.get(0));
                endList1.add(listAllLength1.get(0)+4);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_telephone)+" : "+mUser.getTel().toString() + "\n\n");
                startList1.add(listAllLength1.get(1));
                endList1.add(listAllLength1.get(1)+5);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_age)+" : "+mUser.getAge().toString() + "\n\n");
                startList1.add(listAllLength1.get(2));
                endList1.add(listAllLength1.get(2)+3);
                listAllLength1.add( sb1.toString().length());


                sb1.append(getString(R.string.patient_gravidity_num)+" : "+mUser.getPregnantCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(3));
                endList1.add(listAllLength1.get(3)+7);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_parity_num)+" : "+mUser.getChildCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(4));
                endList1.add(listAllLength1.get(4)+6);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_occupation)+" : "+mUser.getWork().toString() + "\n\n");
                startList1.add(listAllLength1.get(5));
                endList1.add(listAllLength1.get(5)+10);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_smoking_history)+" : "+mUser.getSmokeTime().toString() + "\n\n");
                startList1.add(listAllLength1.get(6));
                endList1.add(listAllLength1.get(6)+15);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_abortion_num)+" : "+mUser.getAbortionCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(7));
                endList1.add(listAllLength1.get(7)+18);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_sex_num)+" : "+mUser.getSexPartnerCount().toString() + "\n\n");
                startList1.add(listAllLength1.get(8));
                endList1.add(listAllLength1.get(8)+15);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_medical_record_number)+" : "+mUser.getCaseNumbe().toString() + "\n\n");
                startList1.add(listAllLength1.get(9));
                endList1.add(listAllLength1.get(9)+17);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_social_security_number)+" : "+mUser.getsSNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(10));
                endList1.add(listAllLength1.get(10)+18);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_HCG)+" : "+mUser.gethCG().toString() + "\n\n");
//            sb.append("真的好"+" : "+mUser.gethCG() + "\n\n");
                startList1.add(listAllLength1.get(11));
                endList1.add(listAllLength1.get(11)+3);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_ID)+" : "+mUser.getIdNumber().toString() + "\n\n");
                startList1.add(listAllLength1.get(12));
                endList1.add(listAllLength1.get(12)+7);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_check_notes)+" : "+mUser.getCheckNotes().toString() + "\n\n");
                startList1.add(listAllLength1.get(13));
                endList1.add(listAllLength1.get(13)+7);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_blood_type)+" : "+mUser.getBloodType().toString() + "\n\n");
                startList1.add(listAllLength1.get(14));
                endList1.add(listAllLength1.get(14)+16);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_contraceptive_methods)+" : "+mUser.getBirthControlMode().toString() + "\n\n");
                startList1.add(listAllLength1.get(15));
                endList1.add(listAllLength1.get(15)+13);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_marriage)+" : "+mUser.getMarry().toString() + "\n\n");
                startList1.add(listAllLength1.get(16));
                endList1.add(listAllLength1.get(16)+8);
                listAllLength1.add( sb1.toString().length());

                sb1.append(getString(R.string.patient_source)+" : "+mUser.getpSource().toString() + "\n\n");
                startList1.add(listAllLength1.get(17));
                endList1.add(listAllLength1.get(17)+8);
                listAllLength1.add( sb1.toString().length());
                tv_dengji.setText(AlignedTextUtils.addConbine1(sb1.toString(),startList1,endList1));
                Logger.e("登记信息：sb.toString()" +sb1.toString());
            }


        }
    }

    StringBuilder sb2 = new StringBuilder();
    List<Integer> listAllLength2 = new ArrayList<>();
    List<Integer> startList2 = new ArrayList<>();
    List<Integer> endList2 = new ArrayList<>();
    //诊断结果
    private void showUserInfoResult() {
        if (mUser != null) {
            if (isChain) {
                sb2.delete(0, sb2.length());
                listAllLength2.clear();
                startList2.clear();
                endList2.clear();
                if (mUser.getSymptom() != null) {
                    sb2.append(getString(R.string.print_symptom) + " : " + mUser.getSymptom() + "\n\n");
                    startList2.add(0);
                    endList2.add(2);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_symptom) + " : " + "\n\n");
                    startList2.add(0);
                    endList2.add(2);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getAdvice() != null) {
                    sb2.append(getString(R.string.print_cytology) + " : " + mUser.getAdvice() + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + 3);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_cytology) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + 3);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHpv_dna() != null) {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + mUser.getHpv_dna() + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + 7);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + 7);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getBingbian() != null) {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + mUser.getBingbian() + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + 4);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + 4);
                    listAllLength2.add(sb2.toString().length());
                }


                if (mUser.getSummary() != null) {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + mUser.getSummary() + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + 4);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + 4);
                    listAllLength2.add(sb2.toString().length());
                }



                if (mUser.getYindaojin() != null) {
                    sb2.append(getString(R.string.print_colposcopic)+ " : " + mUser.getYindaojin() + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + 5);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_colposcopic) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + 5);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getNizhen() != null) {
                    sb2.append(getString(R.string.print_Suspected)+ " : " + mUser.getNizhen() + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + 2);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Suspected) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + 2);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getZhuyishixiang() != null) {
                    sb2.append(getString(R.string.print_attention)+ " : " + mUser.getZhuyishixiang() + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) + 4);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_attention) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) + 4);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHandle() != null) {
                    sb2.append(getString(R.string.print_opinion) + " : " + mUser.getHandle() + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + 4);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_opinion) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + 4);
                    listAllLength2.add(sb2.toString().length());
                }

                try {
                    tv_result.setText(AlignedTextUtils.addConbine2(sb2.toString(), startList2, endList2));
                } catch (Exception e) {
                    Logger.e("病例对比时 诊断时的 错误信息： " + e.getMessage());
                    e.printStackTrace();
                }

            } else { //英文
                sb2.delete(0, sb2.length());
                listAllLength2.clear();
                startList2.clear();
                endList2.clear();
                if (mUser.getSymptom() != null) {
                    sb2.append(getString(R.string.print_symptom) + " : " + mUser.getSymptom() + "\n\n");
                    startList2.add(0);
                    endList2.add(7);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_symptom) + " : " + "\n\n");
                    startList2.add(0);
                    endList2.add(7);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getAdvice() != null) {
                    sb2.append(getString(R.string.print_cytology) + " : " + mUser.getAdvice() + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + 8);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_cytology) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(0));
                    endList2.add(listAllLength2.get(0) + 8);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHpv_dna() != null) {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + mUser.getHpv_dna() + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + 7);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_HPV_DNA) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(1));
                    endList2.add(listAllLength2.get(1) + 7);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getBingbian() != null) {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + mUser.getBingbian() + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + 10);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Lesion_area) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(2));
                    endList2.add(listAllLength2.get(2) + 10);
                    listAllLength2.add(sb2.toString().length());
                }


                if (mUser.getSummary() != null) {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + mUser.getSummary() + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + 10);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Overall_assessment) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(3));
                    endList2.add(listAllLength2.get(3) + 10);
                    listAllLength2.add(sb2.toString().length());
                }



                if (mUser.getYindaojin() != null) {
                    sb2.append(getString(R.string.print_colposcopic)+ " : " + mUser.getYindaojin() + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + 10);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_colposcopic) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(4));
                    endList2.add(listAllLength2.get(4) + 10);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getNizhen() != null) {
                    sb2.append(getString(R.string.print_Suspected)+ " : " + mUser.getNizhen() + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + 8);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_Suspected) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(5));
                    endList2.add(listAllLength2.get(5) + 8);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getZhuyishixiang() != null) {
                    sb2.append(getString(R.string.print_attention)+ " : " + mUser.getZhuyishixiang() + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) +9);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_attention) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) + 9);
                    listAllLength2.add(sb2.toString().length());
                }

                if (mUser.getHandle() != null) {
                    sb2.append(getString(R.string.print_opinion) + " : " + mUser.getHandle() + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + 9);
                    listAllLength2.add(sb2.toString().length());
                } else {
                    sb2.append(getString(R.string.print_opinion) + " : " + "\n\n");
                    startList2.add(listAllLength2.get(7));
                    endList2.add(listAllLength2.get(7) + 9);
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


    }




    StringBuilder sb3 = new StringBuilder();
    List<Integer> listAllLength3 = new ArrayList<>();
    List<Integer> startList3 = new ArrayList<>();
    List<Integer> endList3 = new ArrayList<>();
   //评估
    private void showUserInfoPingGu() {
        if (mUser != null) {
            if (isChain) {
                sb3.delete(0, sb3.length());
                listAllLength3.clear();
                startList3.clear();
                endList3.clear();
                if (mUser.getBingbian() != null) {
                    sb3.append(getString(R.string.print_border) + " : " + mUser.getBingbian() + "\n\n");
                    startList3.add(0);
                    endList3.add(5);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_border)+ " : "  + "\n\n");
                    startList3.add(0);
                    endList3.add(5);
                    listAllLength3.add(sb3.toString().length());
                }

                if (mUser.getColor() != null) {
                    sb3.append(getString(R.string.print_color) + " : " + mUser.getColor() + "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0) + 2);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_color)+" : "+ "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0)+2);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getXueguna() != null) {
                    sb3.append(getString(R.string.print_blood_vessel)+ " : " + mUser.getXueguna() + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1) + 2);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_blood_vessel)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1)+2);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getDianranse() != null) {
                    sb3.append(getString(R.string.print_Iodine_staining) + " : " + mUser.getDianranse() + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2) + 3);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Iodine_staining)+ " : " + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2)+3);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getCusuan() != null) {
                    sb3.append(getString(R.string.print_Acetic_acid_change) + " : " + mUser.getCusuan() + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3) + 4);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Acetic_acid_change)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3)+4);
                    listAllLength3.add( sb3.toString().length());
                }
                tv_pinggu.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
            } else { //英文
                sb3.delete(0, sb3.length());
                listAllLength3.clear();
                startList3.clear();
                endList3.clear();
                if (mUser.getBingbian() != null) {
                    sb3.append(getString(R.string.print_border) + " : " + mUser.getBingbian() + "\n\n");
                    startList3.add(0);
                    endList3.add(16);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_border)+ " : "  + "\n\n");
                    startList3.add(0);
                    endList3.add(16);
                    listAllLength3.add(sb3.toString().length());
                }

                if (mUser.getColor() != null) {
                    sb3.append(getString(R.string.print_color) + " : " + mUser.getColor() + "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0) + 5);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_color)+" : "+ "\n\n");
                    startList3.add(listAllLength3.get(0));
                    endList3.add(listAllLength3.get(0)+5);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getXueguna() != null) {
                    sb3.append(getString(R.string.print_blood_vessel)+ " : " + mUser.getXueguna() + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1) + 12);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_blood_vessel)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(1));
                    endList3.add(listAllLength3.get(1)+12);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getDianranse() != null) {
                    sb3.append(getString(R.string.print_Iodine_staining) + " : " + mUser.getDianranse() + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2) + 12);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Iodine_staining)+ " : " + "\n\n");
                    startList3.add(listAllLength3.get(2));
                    endList3.add(listAllLength3.get(2)+12);
                    listAllLength3.add( sb3.toString().length());
                }

                if (mUser.getCusuan() != null) {
                    sb3.append(getString(R.string.print_Acetic_acid_change) + " : " + mUser.getCusuan() + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3) + 10);
                    listAllLength3.add(sb3.toString().length());
                } else {
                    sb3.append(getString(R.string.print_Acetic_acid_change)+" : " + "\n\n");
                    startList3.add(listAllLength3.get(3));
                    endList3.add(listAllLength3.get(3)+10);
                    listAllLength3.add( sb3.toString().length());
                }
                tv_pinggu.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
            }

        }

    }


    //展示图片
    private void showImages(String filePath) {
        Bitmap bitmap = getBitmap(filePath);
        if (bitmap !=null) {
            image.setImageBitmap(bitmap);
            getPathSplitName(filePath);
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
                if (imagePathList.size() == 0||!isFastClick()) {
                    return;
                }
                index--;
                if (index  >= 0&&index<imagePathList.size()) {
                    showImages(imagePathList.get(index));

//                    List<String> pathSplitName = getPathSplitName(imagePathList.get(index));
//                    tv_imagenameshow01.setText("图像展示:"+);

                } else {
//                    Toast.makeText(this, "当前是最后一张", Toast.LENGTH_SHORT).show();
                    showImages(imagePathList.get(imagePathList.size()-1));
                    index = imagePathList.size()-1;
                }
//                index--;
                break;
            case R.id.btn_next01://下一张
                if (imagePathList.size() == 0||!isFastClick()) {
                    return;
                }
                index++;
                Logger.e("照片的数量："+ imagePathList.size());
                if (index  >=imagePathList.size()||index<0) {
//                    Toast.makeText(this, "当前是第一张", Toast.LENGTH_SHORT).show();
                    showImages(imagePathList.get(0));
                    index = 0;
                } else {
                    showImages(imagePathList.get(index));
                }

                break;

        }

    }

    /**
     * 获取图片资源
     * @return
     */

    private void getImagePathFromSD(String filePath) {
        // 图片列表
//        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
//        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
//                + "image";
        // 得到该路径文件夹下所有的文件
        if (filePath == null) {
            return;
        }
        Logger.e("   filePath = "+filePath);
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        try {
//            Logger.e("CaseManager    files.length = "+files.length);
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
    //                得到的图片列表
                    imagePathList.add(file.getPath());
                }
            }
        } catch (Exception e) {
            Logger.e("查找文件夹报错"+e.getMessage());
//            e.printStackTrace();
        }
//        return imagePathList;
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

    private List<String> fileSplitNameList = new ArrayList<>();

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
                    tv_imagenameshow01.setText("图片展示:" + imageCatagory + "状态下," + split[2] + "秒/时长" + split[3] + "分钟");
                } else {
                    String imageCatagory = changeInfo(split[1]);
                    tv_imagenameshow01.setText("图片展示:" + imageCatagory );
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

}
