package com.shizhenbao.fragments.fragmentcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activity.R;
import com.application.MyApplication;
import com.orhanobut.logger.Logger;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.util.AlignedTextUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/9/13.
 */

public class ZhenDuanFragment extends Fragment {
    private List<User> list = new ArrayList<>();
    private TextView newTextView, oldTextView,tvName01,tvName02;
    private User mUser0;
    private User mUser1;
    private boolean isChain = true;//判断语言是否是中文
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_zhenduan_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        newTextView = (TextView) view.findViewById(R.id.textView_franzhenduan_newcase);//原病例
        oldTextView = (TextView) view.findViewById(R.id.textView_franzhenduan_oldcase);//对比病例
        tvName01 = (TextView) view.findViewById(R.id.textview_casezhenduan01);//患者姓名
        tvName02 = (TextView) view.findViewById(R.id.textview_casezhenduan02);//患者姓名
        mUser0 = new User();
        mUser1 = new User();
        isChain = MyApplication.getInstance().getCountry().endsWith("CN");

    }

    @Override
    public void onStart() {
        super.onStart();
        list = Const.mListUser;
        try {
            mUser0 = list.get(0);
            mUser1 = list.get(1);
            tvName01.setText(getString(R.string.case_diagnosis_result_show)+"("+mUser0.getpName()+"):");
            tvName02.setText(getString(R.string.case_contrast_diagnosis_result_show)+"("+mUser1.getpName()+"):");
            showUserInfoResult(newTextView, mUser0);
            showUserInfoResult(oldTextView, mUser1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    StringBuilder sb1 = new StringBuilder();
//    List<Integer> listAllLength = new ArrayList<>();
//    List<Integer> startList = new ArrayList<>();
//    List<Integer> endList = new ArrayList<>();
    StringBuilder sb2 = new StringBuilder();
    List<Integer> listAllLength2 = new ArrayList<>();
    List<Integer> startList2 = new ArrayList<>();
    List<Integer> endList2 = new ArrayList<>();

    //诊断
    private void showUserInfoResult(TextView view,User mUser) {
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
                    sb2.append(getString(R.string.print_colposcopic) + " : " + mUser.getYindaojin() + "\n\n");
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
                    sb2.append(getString(R.string.print_Suspected) + " : " + mUser.getNizhen() + "\n\n");
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
                    sb2.append(getString(R.string.print_attention) + " : " + mUser.getZhuyishixiang() + "\n\n");
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
                    sb2.append(getString(R.string.print_colposcopic) + " : " + mUser.getYindaojin() + "\n\n");
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
                    sb2.append(getString(R.string.print_Suspected) + " : " + mUser.getNizhen() + "\n\n");
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
                    sb2.append(getString(R.string.print_attention) + " : " + mUser.getZhuyishixiang() + "\n\n");
                    startList2.add(listAllLength2.get(6));
                    endList2.add(listAllLength2.get(6) + 9);
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
            }

            try {
                view.setText(AlignedTextUtils.addConbine2(sb2.toString(), startList2, endList2));
            } catch (Exception e) {
                Logger.e("病例对比时 诊断时的 错误信息： " + e.getMessage());
                e.printStackTrace();
            }


        }

    }


}
