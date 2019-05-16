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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/9/13.
 */

public class PingJiaFragment extends Fragment {

    private List<User> list = new ArrayList<>();
    private TextView newTextView, oldTextView,tvName01,tvName02;
    private User mUser0;
    private User mUser1;
    private boolean isChain = true;//判断语言是否是中文

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pingjia_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        newTextView = (TextView) view.findViewById(R.id.textView_franzhenduan_newcase);//原病例
        oldTextView = (TextView) view.findViewById(R.id.textView_franzhenduan_oldcase);//对比病例
        tvName01 = (TextView) view.findViewById(R.id.textview_casepingjia01);//病人姓名
        tvName02 = (TextView) view.findViewById(R.id.textview_casepingjia02);//病人姓名
        mUser0 = new User();
        mUser1 = new User();
        isChain = MyApplication.getInstance().getCountry().endsWith("CN");

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            list = Const.mListUser;
            mUser0 = list.get(0);
            mUser1 = list.get(1);
            tvName01.setText(getString(R.string.case_assessment_result_show)+"("+mUser0.getpName()+"):");
            tvName02.setText(getString(R.string.case_contrast_assessment_result_show)+"("+mUser1.getpName()+"):");
            showUserInfoPingGu(newTextView, mUser0);
            showUserInfoPingGu(oldTextView, mUser1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    StringBuilder sb3 = new StringBuilder();
    List<Integer> listAllLength3 = new ArrayList<>();
    List<Integer> startList3 = new ArrayList<>();
    List<Integer> endList3 = new ArrayList<>();
    //评估
    private void showUserInfoPingGu(TextView view,User mUser) {
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
//                tv_pinggu.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
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
//                tv_pinggu.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
            }

        }
            try {
                view.setText(AlignedTextUtils.addConbine3(sb3.toString(),startList3,endList3));
            } catch (Exception e) {
                e.printStackTrace();
            }
            }





}
