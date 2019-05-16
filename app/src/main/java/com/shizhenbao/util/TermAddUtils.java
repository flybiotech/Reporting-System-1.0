package com.shizhenbao.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activity.R;
import com.shizhenbao.pop.Diacrisis;
import com.util.AlignedTextUtils;

import org.litepal.LitePal;

import java.util.List;

public class TermAddUtils {

    private Context mContext;
    private EditText et_xibaoxue,et_dna,et_zhengzhuang,et_pinggu,et_quyu,et_yindaojin,et_nizhen,et_yijian,et_handle;
    private EditText et_bianjie,et_color,et_xueguan,et_dianranse,et_cusuan;
    private String []tv_name;
    private TextView tv_01,tv_02,tv_03,tv_04,tv_05,tv_06,tv_07,tv_08,tv_09,tv_10,tv_11,tv_12,tv_13,tv_14;
    private int diaId;
    public TermAddUtils(Context mContext){
        this.mContext = mContext;
    }

    private void initView(View view){
        tv_name = new String[]{ mContext.getString(R.string.print_cytology),mContext.getString(R.string.print_HPV_DNA),mContext.getString(R.string.print_symptom),
                mContext.getString(R.string.print_Overall_assessment), mContext.getString(R.string.print_Lesion_area), mContext.getString(R.string.print_colposcopic),
                mContext.getString(R.string.print_Suspected),mContext.getString(R.string.print_attention), mContext.getString(R.string.print_opinion),
                mContext.getString(R.string.print_border),  mContext.getString(R.string.print_color), mContext.getString(R.string.print_blood_vessel),
                mContext.getString(R.string.print_Iodine_staining), mContext.getString(R.string.print_Acetic_acid_change)};
        et_xibaoxue= (EditText) view.findViewById(R.id.et_xibaoxue);
        et_dna= (EditText) view.findViewById(R.id.et_dna);
        et_zhengzhuang= (EditText) view.findViewById(R.id.et_zhengzhuang);
        et_pinggu= (EditText) view.findViewById(R.id.et_pinggu);
        et_quyu= (EditText) view.findViewById(R.id.et_quyu);
        et_yindaojin= (EditText) view.findViewById(R.id.et_yindaojin);
        et_yijian= (EditText) view.findViewById(R.id.et_yijian);
        et_nizhen= (EditText) view.findViewById(R.id.et_nizhen);
        et_bianjie= (EditText) view.findViewById(R.id.et_bianjie);
        et_xueguan= (EditText) view.findViewById(R.id.et_xueguan);
        et_color= (EditText) view.findViewById(R.id.et_color);
        et_dianranse= (EditText) view.findViewById(R.id.et_dianranse);
        et_cusuan= (EditText) view.findViewById(R.id.et_cusuan);
        et_handle= (EditText) view.findViewById(R.id.et_handle);
        tv_01= (TextView) view.findViewById(R.id.tv_01);
        tv_02= (TextView) view.findViewById(R.id.tv_02);
        tv_03= (TextView) view.findViewById(R.id.tv_03);
        tv_04= (TextView) view.findViewById(R.id.tv_04);
        tv_05= (TextView) view.findViewById(R.id.tv_05);
        tv_06= (TextView) view.findViewById(R.id.tv_06);
        tv_07= (TextView) view.findViewById(R.id.tv_07);
        tv_08= (TextView) view.findViewById(R.id.tv_08);
        tv_09= (TextView) view.findViewById(R.id.tv_09);
        tv_10= (TextView) view.findViewById(R.id.tv_10);
        tv_11= (TextView) view.findViewById(R.id.tv_11);
        tv_12= (TextView) view.findViewById(R.id.tv_12);
        tv_13= (TextView) view.findViewById(R.id.tv_13);
        tv_14= (TextView) view.findViewById(R.id.tv_14);
        TextView[] tvData = {tv_01, tv_02, tv_03, tv_04, tv_05, tv_06, tv_07, tv_08, tv_09, tv_10, tv_11, tv_12, tv_13, tv_14};
//        if (screenInches > 6.0) {
        for (int i=0;i<tv_name.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tv_name[i], 5));
            }
        }
    }
    /**
     * 显示添加术语的提示框
     */
    public void showTermDialog(){
        //判断屏幕的大小
        View view= LayoutInflater.from(mContext).inflate(R.layout.linear_view,null);
        initView(view);
        LinearLayout linearLayoutMain = new LinearLayout(mContext);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        linearLayoutMain.addView(view);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.setting_input_terms)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(mContext.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton(mContext.getString(R.string.patient_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(et_xibaoxue.getText().toString().trim())//判断诊断信息是否为空,为空时点击保存弹出框直接消失，否则先保存信息
                                &&TextUtils.isEmpty(et_dna.getText().toString().trim())
                                &&TextUtils.isEmpty(et_zhengzhuang.getText().toString().trim())
                                &&TextUtils.isEmpty(et_pinggu.getText().toString().trim())
                                &&TextUtils.isEmpty(et_quyu.getText().toString().trim())
                                &&TextUtils.isEmpty(et_yindaojin.getText().toString().trim())
                                &&TextUtils.isEmpty(et_nizhen.getText().toString().trim())
                                &&TextUtils.isEmpty(et_yijian.getText().toString().trim())
                                &&TextUtils.isEmpty(et_bianjie.getText().toString().trim())
                                &&TextUtils.isEmpty(et_color.getText().toString().trim())
                                &&TextUtils.isEmpty(et_cusuan.getText().toString().trim())
                                &&TextUtils.isEmpty(et_dianranse.getText().toString().trim())
                                &&TextUtils.isEmpty(et_xueguan.getText().toString().trim())
                                &&TextUtils.isEmpty(et_handle.getText().toString().trim())
                                ){
                            dialogInterface.cancel();
                        }else{
                            initBianhao();
                            Diacrisis diacrisis=new Diacrisis();
                            diacrisis.setDiaId(diaId+1);
                            diacrisis.setCytology(et_xibaoxue.getText().toString());
                            diacrisis.setDna(et_dna.getText().toString());
                            diacrisis.setSymptom(et_zhengzhuang.getText().toString());
                            diacrisis.setAssessment(et_pinggu.getText().toString());
                            diacrisis.setRegion(et_quyu.getText().toString());
                            diacrisis.setColposcopy(et_yindaojin.getText().toString());
                            diacrisis.setSuspected(et_nizhen.getText().toString());
                            diacrisis.setAttention(et_yijian.getText().toString());
                            diacrisis.setBianjie(et_bianjie.getText().toString().trim());
                            diacrisis.setColor(et_color.getText().toString().trim());
                            diacrisis.setCusuan(et_cusuan.getText().toString().trim());
                            diacrisis.setDianranse(et_dianranse.getText().toString().trim());
                            diacrisis.setXueguna(et_xueguan.getText().toString().trim());
                            diacrisis.setHandle(et_handle.getText().toString().trim());
                            diacrisis.save();
                            dialogInterface.cancel();
                        }
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
    }
    private void initBianhao(){
        List<Diacrisis> diacrisisList= LitePal.findAll(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            diaId=diacrisisList.get(diacrisisList.size()-1).getDiaId();
        }
    }
}
