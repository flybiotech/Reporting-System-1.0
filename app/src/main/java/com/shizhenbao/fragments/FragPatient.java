package com.shizhenbao.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.activity.SearchActivity;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;
import com.view.MyToast;

import org.litepal.LitePal;

import java.util.List;


/**
 * Created by dell on 2017/5/22.
 * adb shell monkey -p 你测试的app的包名 --ignore-crashes --ignore-timeouts --monitor-native-crashes -v -v 1000 >D:\XXX.txt”
 */

public class FragPatient  extends BaseFragment implements View.OnClickListener  {

    private static final String TAG = "TAG_FragPatient";

    private Button btn_left, btn_right,text_cancle,text_save,text_dengji_next; //身份证号，       病例 号。      社保号
    private EditText edit_bianhao, edit_djName, edit_djPhone, edit_djAge,edit_IdNumber,edit_CaseNumbe,edit_SSNumber;
    private EditText edit_hCG;
    private EditText edit_work;//职业
    private EditText edit_pregnantCount;//怀孕次数
    private EditText edit_childCount;//产次
    private EditText edit_abortionCount;//流产次数
    private EditText edit_sexPartnerCount;//性伙伴数量
    private EditText edit_smokeTime;//吸烟史
    private EditText edit_checkNotes;//检查注释
//    private double screenInches;//屏幕的尺寸
    private Spinner spin_source, spin_marry,spin_birthControlMode,spin_bloodType;
    private String source,marry,birthControlMode,bloodType;
    private UserManager userManager;//这个是管理用户的类
    private LoginRegister loginRegister;//登陆注册，获取医生相关信息的类
    private RelativeLayout rl;
    private TextView title_text;
    private TextView tv01,tv02,tv03,tv04,tv05,tv06,tv07,tv08,tv09,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18,tv19;
//    private String[] tvName = new String[]{"编号", "姓名","电话", "年龄", "孕次", "产次", "职业", "吸烟史", "流产次数", "性伙伴数", "病历号", "社保号", "HCG", "身份证", "检查注释", "血型", "避孕方式", "婚否", "病人来源"};
    private String[] marryDatas ;
    private String[] bhdDatas ;
    private String[] bloodDatas ;
    private String[] sourceDatas ;

    private String[] tvName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = null;
//        screenInches = DeviceOfSize.getScreenSizeOfDevice2(getActivity());
        view=inflater.inflate(R.layout.frag_layout_patient, container, false);
        init(view);
        initTextView(view);
        return view;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
//        LogUtil.e(TAG,"onFragmentVisibleChange.isVisible =  "+isVisible);
        OneItem.getOneItem().setIntImage(1);
    }

    @Override
    public void onStart() {
        super.onStart();
//        LogUtil.e(TAG,"onStart()");
        List<SystemSet> systemSet= LitePal.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
            String localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
        userManager=new UserManager(getContext());
        userManager.bianhao(edit_bianhao);//自动生成编号
        if (Const.isSave) {
            clearUserInfo();
            text_cancle.setEnabled(true);
        }
        initData();//spinner点击事件
        editTextChangeListener();

    }

    private void init(View view){
        marryDatas = new String[]{getString(R.string.patient_nothing),getString(R.string.patient_married),getString(R.string.patient_unmarried)};
        bhdDatas = new String[]{getString(R.string.patient_nothing),getString(R.string.patient_condom),getString(R.string.patient_acyterion),getString(R.string.patient_Contraceptive_needle),
                getString(R.string.patient_female_condom),getString(R.string.patient_Ligation),getString(R.string.patient_Women__IUD)};
        bloodDatas = new String[]{getString(R.string.patient_nothing),getString(R.string.patient_A_type),getString(R.string.patient_B_type),
                getString(R.string.patient_AB_type),getString(R.string.patient_O_type),getString(R.string.patient_other_type)};
        sourceDatas = new String[]{getString(R.string.patient_nothing),getString(R.string.patient_hospitalization),
                getString(R.string.patient_Outpatient_Department),getString(R.string.patient_other)};
        source = getContext().getString(R.string.patient_nothing);
        marry = getContext().getString(R.string.patient_nothing);
        birthControlMode=getContext().getString(R.string.patient_nothing);
        bloodType=getContext().getString(R.string.patient_nothing);//设置默认值
        tvName = new String[]{getContext().getString(R.string.print_patient_id), getContext().getString(R.string.patient_name), getContext().getString(R.string.case_patient_telephone), getContext().getString(R.string.patient_age), getContext().getString(R.string.patient_gravidity_num),
                getContext().getString(R.string.patient_parity_num), getContext().getString(R.string.patient_occupation), getContext().getString(R.string.patient_smoking_history), getContext().getString(R.string.patient_abortion_num), getContext().getString(R.string.patient_sex_num), getContext().getString(R.string.patient_medical_record_number), getContext().getString(R.string.patient_social_security_number), getContext().getString(R.string.patient_HCG), getContext().getString(R.string.patient_ID), getContext().getString(R.string.patient_check_notes), getContext().getString(R.string.patient_blood_type), getContext().getString(R.string.patient_contraceptive_methods), getContext().getString(R.string.patient_marriage), getContext().getString(R.string.patient_source)};
//        OneItem.getOneItem().setIntImage(1);
        rl= (RelativeLayout) view.findViewById(R.id.rl);
        btn_left = (Button) view.findViewById(R.id.btn_left);//返回按钮，，暂时被隐藏了
        btn_right = (Button) view.findViewById(R.id.btn_right);//这个是菜单按钮
        text_cancle = (Button) view.findViewById(R.id.text_dengji_cancle);//登记界面取消的按钮
        text_save = (Button) view.findViewById(R.id.text_dengji_save);//登记界面确认的按钮
        title_text = (TextView) view.findViewById(R.id.title_text);//title
        title_text.setText(getText(R.string.patients_information));
        edit_bianhao= (EditText) view.findViewById(R.id.edit_bianhao);//编号
        edit_djName= (EditText) view.findViewById(R.id.edit_djName);//姓名
        edit_djAge= (EditText) view.findViewById(R.id.edit_djAge);//年龄
        edit_djPhone= (EditText) view.findViewById(R.id.edit_djPhone);//电话
        edit_IdNumber= (EditText) view.findViewById(R.id.edit_patient_idNumber);//身份证号码
        edit_IdNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_CaseNumbe= (EditText) view.findViewById(R.id.edit_patient_CaseNumbe);//病例号
        edit_CaseNumbe.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_SSNumber= (EditText) view.findViewById(R.id.edit_patient_ssNumber);//社保号，
        text_dengji_next= (Button) view.findViewById(R.id.text_dengji_next);
        rl.requestFocus();//得到焦点

        edit_hCG= (EditText) view.findViewById(R.id.edit_patient_HCG);//hcg
        edit_work= (EditText) view.findViewById(R.id.edit_patient_work);//职业
        edit_pregnantCount= (EditText) view.findViewById(R.id.edit_patient_pregnantCount);//孕次
        edit_childCount= (EditText) view.findViewById(R.id.edit_patient_childCount);//产次
        edit_abortionCount= (EditText) view.findViewById(R.id.edit_patient_abortionCount);//流产次数
        edit_sexPartnerCount= (EditText) view.findViewById(R.id.edit_patient_sexPartnerCount);//性伙伴数
        edit_smokeTime= (EditText) view.findViewById(R.id.edit_patient_smokeTime);//吸烟史
        edit_checkNotes= (EditText) view.findViewById(R.id.edit_patient_checkNotes);//检查注释


        spin_marry= (Spinner) view.findViewById(R.id.spin_marry);//婚否
        spin_source= (Spinner) view.findViewById(R.id.spin_source);//来源

        spin_birthControlMode= (Spinner) view.findViewById(R.id.spin_patient_birthControlMode);//避孕方式
        spin_bloodType= (Spinner) view.findViewById(R.id.spin_patient_bloodType);//血型
        text_dengji_next.setOnClickListener(this);
        btn_left.setOnClickListener(this);//返回按钮的点击事件
        btn_left.setText("");//设置为不可见
        btn_right.setOnClickListener(this);//菜单按钮的点击事件
        btn_right.setText(getString(R.string.patient_right_select));
        text_cancle.setOnClickListener(this);//取消
        text_save.setOnClickListener(this);//保存
        userManager = new UserManager();//用户管理的类
        loginRegister=new LoginRegister();//登陆注册，获取医生相关信息的类

        ArrayAdapter<String> adapterMarry = new ArrayAdapter<String>(getActivity(),R.layout.adapter_item,R.id.sp_textview,marryDatas);
        ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(getActivity(),R.layout.adapter_item,R.id.sp_textview,sourceDatas);
        ArrayAdapter<String> adapterBHM = new ArrayAdapter<String>(getActivity(),R.layout.adapter_item,R.id.sp_textview,bhdDatas);
        ArrayAdapter<String> adapterBlood= new ArrayAdapter<String>(getActivity(),R.layout.adapter_item,R.id.sp_textview,bloodDatas);
        spin_marry.setAdapter(adapterMarry);
        spin_source.setAdapter(adapterSource);
        spin_birthControlMode.setAdapter(adapterBHM);
        spin_bloodType.setAdapter(adapterBlood);


    }

    private void editTextChangeListener() {
        edit_djName.addTextChangedListener(textWatcher);
        edit_djAge.addTextChangedListener(textWatcher);
        edit_djPhone.addTextChangedListener(textWatcher);
        edit_IdNumber.addTextChangedListener(textWatcher);
        edit_CaseNumbe.addTextChangedListener(textWatcher);
        edit_SSNumber.addTextChangedListener(textWatcher);

        edit_hCG.addTextChangedListener(textWatcher);
        edit_work.addTextChangedListener(textWatcher);
        edit_pregnantCount.addTextChangedListener(textWatcher);
        edit_childCount.addTextChangedListener(textWatcher);
        edit_abortionCount.addTextChangedListener(textWatcher);
        edit_sexPartnerCount.addTextChangedListener(textWatcher);

        edit_smokeTime.addTextChangedListener(textWatcher);
        edit_checkNotes.addTextChangedListener(textWatcher);
    }



    private void initTextView(View view) {
        tv01 = (TextView) view.findViewById(R.id.tv_patient_01);
        tv02 = (TextView) view.findViewById(R.id.tv_patient_02);
        tv03 = (TextView) view.findViewById(R.id.tv_patient_03);
        tv04 = (TextView) view.findViewById(R.id.tv_patient_04);
        tv05 = (TextView) view.findViewById(R.id.tv_patient_05);
        tv06 = (TextView) view.findViewById(R.id.tv_patient_06);
        tv07 = (TextView) view.findViewById(R.id.tv_patient_07);
        tv08 = (TextView) view.findViewById(R.id.tv_patient_08);
        tv09 = (TextView) view.findViewById(R.id.tv_patient_09);
        tv10 = (TextView) view.findViewById(R.id.tv_patient_10);
        tv11 = (TextView) view.findViewById(R.id.tv_patient_11);
        tv12 = (TextView) view.findViewById(R.id.tv_patient_12);
        tv13 = (TextView) view.findViewById(R.id.tv_patient_13);
        tv14 = (TextView) view.findViewById(R.id.tv_patient_14);
        tv15 = (TextView) view.findViewById(R.id.tv_patient_15);
        tv16 = (TextView) view.findViewById(R.id.tv_patient_16);
        tv17 = (TextView) view.findViewById(R.id.tv_patient_17);
        tv18 = (TextView) view.findViewById(R.id.tv_patient_18);
        tv19 = (TextView) view.findViewById(R.id.tv_patient_19);
         TextView[] tvData = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19};
//        if (screenInches > 6.0) {
            for (int i=0;i<tvName.length;i++) {
                if (tvData[i] != null) {
                    tvData[i].setText(AlignedTextUtils.justifyString(tvName[i], 4));
                }

            }
//        }
    }
    public  void initData() {
        spin_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//病人来源点击事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Const.isSave = false;
                switch (position){
                    case 0://病人来源为无
                        source=getString(R.string.patient_nothing);
                        break;
                    case 1://病人来源为住院
                        source=getString(R.string.patient_hospitalization);
                        break;
                    case 2://病人来源为门诊
                        source=getString(R.string.patient_Outpatient_Department);
                        break;
                    case 3://病人来源为其他
                        source=getString(R.string.patient_other);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin_marry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//病人婚否点击事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Const.isSave = false;
                switch (position){
                    case 0://病人为无
                        marry=getString(R.string.patient_nothing);
                        break;
                    case 1://病人为已婚
                        marry=getString(R.string.patient_married);
                        break;
                    case 2://病人为未婚
                        marry=getString(R.string.patient_unregistered);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spin_birthControlMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//病人婚否点击事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Const.isSave = false;
                switch (position){
                    case 0://病人避孕方式
                        birthControlMode=getString(R.string.patient_nothing);
                        break;
                    case 1://病人避孕方式
                        birthControlMode=getString(R.string.patient_condom);
                        break;
                    case 2://
                        birthControlMode=getString(R.string.patient_acyterion);
                        break;
                    case 3://
                        birthControlMode=getString(R.string.patient_Contraceptive_needle);
                        break;
                    case 4://
                        birthControlMode=getString(R.string.patient_female_condom);
                        break;
                    case 5://
                        birthControlMode=getString(R.string.patient_Ligation);
                        break;
                    case 6://
                        birthControlMode=getString(R.string.patient_Women__IUD);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spin_bloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//病人婚否点击事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Const.isSave = false;
                switch (position){
                    case 0://默认初始值
                        bloodType=getString(R.string.patient_nothing);
                        break;
                    case 1://病人血型
                        bloodType=getString(R.string.patient_A_type);
                        break;
                    case 2://
                        bloodType=getString(R.string.patient_B_type);
                        break;
                    case 3://
                        bloodType=getString(R.string.patient_AB_type);
                        break;
                    case 4://
                        bloodType=getString(R.string.patient_O_type);
                        break;
                    case 5://
                        bloodType=getString(R.string.patient_other_type);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_right://直接到查询界面
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.text_dengji_cancle://清空填写的病人的信息
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.patient_clear_sure)
                        .setPositiveButton(R.string.patient_empty, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearUserInfo();
                                Const.isSave = true;

                            }
                        })
                        .setNegativeButton(R.string.patient_no_empty, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();


                break;

            case R.id.text_dengji_save://保存填写的病人的信息
                if (!Const.isSave) {
                    saveUserInfo();
                }

                break;
            case R.id.text_dengji_next:
                if (Const.isSave) {//当已经保存好了用户信息之后，就可以新增下一个患者了
                    claerEdit();
                    text_cancle.setEnabled(true);
                } else {
                    MyToast.showToast(getContext(), getContext().getString(R.string.patient_save_message));
//                    Toast.makeText(getContext(), getContext().getString(R.string.patient_save_message), Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private void claerEdit(){
        //重新调用自动生成编号
        userManager.bianhao(edit_bianhao);
        edit_djName.setText("");//清空输入框
        edit_djPhone.setText("");
        edit_djAge.setText("");
        edit_IdNumber.setText("");//身份证号
        edit_CaseNumbe.setText("");//病例号
        edit_SSNumber.setText("");//社保号
        edit_hCG.setText("");//
        edit_work.setText("");//
        edit_pregnantCount.setText("");//
        edit_childCount.setText("");//
        edit_abortionCount.setText("");//
        edit_sexPartnerCount.setText("");//
        edit_smokeTime.setText("");//
        edit_checkNotes.setText("");//
        spin_source.setSelection(0, true);//设为true时显示为无
        spin_marry.setSelection(0, true);//设为true时显示为无
        spin_birthControlMode.setSelection(0, true);//设为true时显示为无
        spin_bloodType.setSelection(0, true);//设为true时显示为无
        edit_djName.requestFocus();//获取焦点
    }

    //清除病人信息
    private void clearUserInfo() {
        edit_djName.setText("");
        edit_djPhone.setText("");
        edit_djAge.setText("");
        edit_djName.setFocusable(true);//获取焦点
        edit_IdNumber.setText("");//身份证号
        edit_CaseNumbe.setText("");//病例号
        edit_SSNumber.setText("");//社保号
        edit_djName.setFocusable(true);//获取焦点

        edit_hCG.setText("");//
        edit_work.setText("");//
        edit_pregnantCount.setText("");//
        edit_childCount.setText("");//
        edit_abortionCount.setText("");//
        edit_sexPartnerCount.setText("");//
        edit_smokeTime.setText("");//
        edit_checkNotes.setText("");//
        spin_source.setSelection(0, true);//设为true时显示为无
        spin_marry.setSelection(0, true);//设为true时显示为无
        spin_birthControlMode.setSelection(0, true);//设为true时显示为无
        spin_bloodType.setSelection(0, true);//设为true时显示为无

    }

    private void saveUserInfo() {

        if(TextUtils.isEmpty(edit_djName.getText().toString().trim()) && TextUtils.isEmpty(edit_djPhone.getText().toString().trim())){
            edit_djName.requestFocus();
            MyToast.showToast(getContext(), getContext().getString(R.string.patient_select_name));
//            Toast.makeText(getContext(),getContext().getString(R.string.patient_select_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(edit_djName.getText().toString().trim())){//判断登记的姓名是否为空
            edit_djName.requestFocus();
            MyToast.showToast(getContext(), getContext().getString(R.string.patient_select_name));
//            Toast.makeText(getContext(),getContext().getString(R.string.patient_select_name), Toast.LENGTH_SHORT).show();
        }else {
            if(TextUtils.isEmpty(edit_djPhone.getText().toString().trim())){//判断电话是否为空
                edit_djPhone.requestFocus();
                MyToast.showToast(getContext(), getContext().getString(R.string.case_select_telephoen_hint));
//                Toast.makeText(getContext(),getContext().getString(R.string.case_select_telephoen_hint), Toast.LENGTH_SHORT).show();
            }else {
                //判断输入的电话号码是否正常（包括11位，8位，7位）
                if (edit_djPhone.getText().toString().trim().length() == 11 || edit_djPhone.getText().toString().trim().length() == 7 || edit_djPhone.getText().toString().trim().length() == 8) {
                    int phoneLength = edit_djPhone.getText().toString().trim().length();
                    int idNumberLength = edit_IdNumber.getText().toString().trim().length();
                    //将患者信息保存到数据库中
                    if (idNumberLength==15||idNumberLength==18||idNumberLength==0){
                        String bianhao=edit_bianhao.getText().toString().trim();
                        int temp=bianhao.indexOf("(");
                        String bianhaoindex=bianhao.substring(0,temp);
                        String hCG = edit_hCG.getText().toString().trim();
                        String work = edit_work.getText().toString().trim();
                        String pregnantCount = edit_pregnantCount.getText().toString().trim();
                        String childCount = edit_childCount.getText().toString().trim();
                        String abortionCount = edit_abortionCount.getText().toString().trim();
                        String sexPartnerCount = edit_sexPartnerCount.getText().toString().trim();
                        String smokeTime = edit_smokeTime.getText().toString().trim();
                        String checkNotes = edit_checkNotes.getText().toString().trim();

                        boolean isSave = userManager.save(new User(), source, marry, bianhaoindex,
                                edit_djName.getText().toString().trim(), edit_djAge.getText().toString().trim(), edit_djPhone.getText().toString().trim(),
                                loginRegister.getDoctorId(OneItem.getOneItem().getName()), false, edit_IdNumber.getText().toString().trim(), edit_CaseNumbe.getText().toString().trim(),
                                edit_SSNumber.getText().toString().trim(), hCG, work, pregnantCount, childCount, abortionCount, bloodType, sexPartnerCount, smokeTime, birthControlMode, checkNotes);

                        if(isSave){
                            text_cancle.setEnabled(false);
                            MyToast.showToast(getContext(), getContext().getString(R.string.patient_register_success_message));
                        }
//                        Toast.makeText(getContext(), getContext().getString(R.string.patient_register_success_message), Toast.LENGTH_SHORT).show();
                        edit_bianhao.setText(bianhaoindex+"("+getString(R.string.patient_yet_register)+")");
                        OneItem.getOneItem().setC(true);//判断诊断页面是否可以输入，为true时可以输入，为false时不可以输入
                        Const.isSave =true;//表示已经保存过了
                    } else {
                        MyToast.showToast(getContext(), getActivity().getString(R.string.patient_ID_error) + edit_IdNumber.getText().toString().trim().length() + getActivity().getString(R.string.patient_ID_error2));
//                        Toast.makeText(getContext(), getActivity().getString(R.string.patient_ID_error) + edit_IdNumber.getText().toString().trim().length() + getActivity().getString(R.string.patient_ID_error2), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyToast.showToast(getContext(), getContext().getString(R.string.patient_telephone_error));
//                    Toast.makeText(getContext(), getContext().getString(R.string.patient_telephone_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * 文本输入改变之前调用（还未改变）
     */
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        /**
         * 文本改变过程中调用（文本替换动作）
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                 Logger.e("edittext 正在改变");
        }

        /**
         * 文本改标之后调用（文本已经替换完成）
         */
        @Override
        public void afterTextChanged(Editable s) {
//            Logger.e("保存状态 = "+Const.isSave);
            Const.isSave = false;//表示文本已经改变了还没有保存

        }
    };

    @Override
    public void onPause() {
        super.onPause();
//        Logger.e("onPause  FragPatient");

    }
}
