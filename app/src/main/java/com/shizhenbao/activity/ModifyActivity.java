package com.shizhenbao.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.db.UserManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.OneItem;
import com.util.AlignedTextUtils;
import com.view.MyToast;


import org.litepal.LitePal;

import java.util.List;

//修改用户信息的activity
public class ModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_left, btn_right;
    private TextView text_cancle, text_save;
    private TextView tv_title;                                            //身份证，        病例号码         社保信息      护照信息
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
    private TextView tv01,tv02,tv03,tv04,tv05,tv06,tv07,tv08,tv09,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18,tv19;
    private String[] tvName;
    private Spinner spin_source, spin_marry, spin_birthControlMode, spin_bloodType;
    private final User user = new User();
    private String source, marry,birthControlMode,bloodType;
    private UserManager userManager;
    private LoginRegister loginRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
            setContentView(R.layout.activity_modify_layout);
        intiView();
        initText();
        initOriginalData();
        initData();
        initClick();
    }

    /**
     * 得到原有数据
     */
    private void initOriginalData() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("second");//得到传递过来的User的id
        edit_bianhao.setText(id);//将得到的病人的id显示在编号输入框中
        OneItem.getOneItem().setId(id);//修改单例模式类中id字段的值
        List<User> list = LitePal.where("pId=?", edit_bianhao.getText().toString()).find(User.class);//得到要修改的User
        for (int i = 0; i < list.size(); i++) {//得到要修改的记录的原有的数据
            edit_djName.setText(list.get(i).getpName());//显示要修改的病人的姓名
            edit_djAge.setText(list.get(i).getAge());//显示要修改的病人的年龄
            edit_djPhone.setText(list.get(i).getTel());//显示要修改的病人的电话
            edit_IdNumber.setText(list.get(i).getIdNumber());//显示要修改的病人的身份证信息
            edit_CaseNumbe.setText(list.get(i).getCaseNumbe());//显示要修改的病人的病历号
            edit_SSNumber.setText(list.get(i).getsSNumber());  //显示要修改的病人的社保号

            edit_hCG.setText(list.get(i).gethCG());  //
            edit_work.setText(list.get(i).getWork());  //
            edit_pregnantCount.setText(list.get(i).getPregnantCount());  //
            edit_abortionCount.setText(list.get(i).getAbortionCount());  //
            edit_sexPartnerCount.setText(list.get(i).getSexPartnerCount());  //
            edit_smokeTime.setText(list.get(i).getSmokeTime());  //
            edit_childCount.setText(list.get(i).getChildCount());  //
            edit_checkNotes.setText(list.get(i).getCheckNotes());  //
            String source = list.get(i).getpSource();//显示要修改的病人的来源
            switch (source) {
                case "无":
                    spin_source.setSelection(0, true);//设为true时显示
                    break;
                case "住院":
                    spin_source.setSelection(1, true);//设为true时显示为住院
                    break;
                case "门诊":
                    spin_source.setSelection(2, true);//设为true时显示为门诊
                    break;
                case "其他":
                    spin_source.setSelection(3, true);//设为true时显示为其他
                    break;
                default:
                    break;
            }
            String marray = list.get(i).getMarry();//显示要修改的病人的婚否
            switch (marray) {
                case "无":
                    spin_marry.setSelection(0, true);//设为true时显示
                    break;
                case "已婚":
                    spin_marry.setSelection(1, true);//设为true时显示为已婚
                    break;
                case "未婚":
                    spin_marry.setSelection(2, true);//设为true时显示为未婚
                    break;
                default:
                    break;
            }

            String birthControlMode = list.get(i).getBirthControlMode();//避孕方式
            switch (birthControlMode){
                case "无"://病人避孕方式
                    spin_birthControlMode.setSelection(0, true);//设为true时显示
                    break;
                case "安全套"://病人避孕方式
                    spin_birthControlMode.setSelection(1, true);//设为true时显示为安全套
                    break;
                case "避孕药"://
                    spin_birthControlMode.setSelection(2, true);
                    break;

                case "女性安全套":
                    spin_birthControlMode.setSelection(4, true);
                    break;
                case "男女结扎"://
                    spin_birthControlMode.setSelection(5, true);
                    break;
                case "女人上节育环"://
                    spin_birthControlMode.setSelection(6, true);
                    break;
                default:
                    break;
            }
            String bloodType = list.get(i).getBloodType();//血型
            switch (bloodType){
                case "无"://病人避孕方式
                    spin_bloodType.setSelection(0, true);//设为true时显示
                    break;
                case "A型"://病人避孕方式
                    spin_bloodType.setSelection(1, true);//设为true时显示为A型
                    break;
                case "B型"://
                    spin_bloodType.setSelection(2, true);
                    break;
                case "AB型"://
                    spin_bloodType.setSelection(3, true);
                    break;
                case "O型":
                    spin_bloodType.setSelection(4, true);
                    break;
                case "其它"://
                    spin_bloodType.setSelection(5, true);
                    break;
                default:
                    break;
            }
        }
    }

    private void initText() {
        tv01 = (TextView) findViewById(R.id.tv_patient_01);
        tv02 = (TextView) findViewById(R.id.tv_patient_02);
        tv03 = (TextView) findViewById(R.id.tv_patient_03);
        tv04 = (TextView) findViewById(R.id.tv_patient_04);
        tv05 = (TextView) findViewById(R.id.tv_patient_05);
        tv06 = (TextView) findViewById(R.id.tv_patient_06);
        tv07 = (TextView) findViewById(R.id.tv_patient_07);
        tv08 = (TextView) findViewById(R.id.tv_patient_08);
        tv09 = (TextView) findViewById(R.id.tv_patient_09);
        tv10 = (TextView) findViewById(R.id.tv_patient_10);
        tv11 = (TextView) findViewById(R.id.tv_patient_11);
        tv12 = (TextView) findViewById(R.id.tv_patient_12);
        tv13 = (TextView) findViewById(R.id.tv_patient_13);
        tv14 = (TextView) findViewById(R.id.tv_patient_14);
        tv15 = (TextView) findViewById(R.id.tv_patient_15);
        tv16 = (TextView) findViewById(R.id.tv_patient_16);
        tv17 = (TextView) findViewById(R.id.tv_patient_17);
        tv18 = (TextView) findViewById(R.id.tv_patient_18);
        tv19 = (TextView) findViewById(R.id.tv_patient_19);

        TextView[] tvData = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19};
//        if (screenInches > 6.0) {
        for (int i=0;i<tvName.length;i++) {
            if (tvData[i] != null) {
                tvData[i].setText(AlignedTextUtils.justifyString(tvName[i], 4));
            }

        }
    }

    private void initClick() {//点击事件
        text_cancle.setOnClickListener(this);//取消
        text_save.setOnClickListener(this);//保存
        btn_left.setOnClickListener(this);//返回
    }

    private void intiView() {
        tvName = new String[]{getString(R.string.print_patient_id),getString(R.string.patient_name), getString(R.string.patient_telephone), getString(R.string.patient_age),getString(R.string.patient_medical_record_number),
                getString(R.string.patient_parity_num), getString(R.string.patient_occupation),getString(R.string.patient_smoking_history), getString(R.string.patient_abortion_num), getString(R.string.patient_sex_num),getString(R.string.patient_gravidity_num)
                , getString(R.string.patient_social_security_number), getString(R.string.patient_HCG),getString(R.string.patient_ID),getString(R.string.patient_check_notes), getString(R.string.patient_blood_type), getString(R.string.patient_contraceptive_methods), getString(R.string.patient_marriage),getString(R.string.patient_source)};
        source=getString(R.string.patient_nothing); marry=getString(R.string.patient_nothing);birthControlMode=getString(R.string.patient_nothing);bloodType=getString(R.string.patient_nothing);

        text_cancle = (TextView) findViewById(R.id.text_modify_cancle);//取消
        text_save = (TextView) findViewById(R.id.text_modify_save);//保存
        spin_marry = (Spinner) findViewById(R.id.spin_modify_marry);//婚否
        spin_source = (Spinner) findViewById(R.id.spin_modify_source);//来源

        spin_birthControlMode = (Spinner) findViewById(R.id.spin_modify_birthControlMode);//避孕方式
        spin_bloodType = (Spinner) findViewById(R.id.spin_modify_bloodType);//避孕方式

        //标题栏控件
        tv_title = (TextView) findViewById(R.id.title_text);//标题
        btn_right = (Button) findViewById(R.id.btn_right);//菜单
        btn_left = (Button) findViewById(R.id.btn_left);//返回
        btn_right.setVisibility(View.GONE);
        btn_right.setText("");
        tv_title.setText(getString(R.string.patient_show_updata));//修改标题为“修改”
        btn_left.setVisibility(View.VISIBLE);//设置按钮为可见
//        text_save.setText("修改");
        edit_bianhao = (EditText) findViewById(R.id.edit_modify_bianhao);//编号
        edit_djAge = (EditText) findViewById(R.id.edit_modify_djAge);//年龄
        edit_djName = (EditText) findViewById(R.id.edit_modify_djName);//病人姓名
        edit_djPhone = (EditText) findViewById(R.id.edit_modify_djPhone);//电话

        edit_IdNumber= (EditText) findViewById(R.id.edit_modify_idNumber);//身份证号码
        edit_CaseNumbe= (EditText) findViewById(R.id.edit_modify_CaseNumbe);//病例号
        edit_CaseNumbe.setRawInputType(Configuration.KEYBOARD_QWERTY);
        edit_SSNumber= (EditText) findViewById(R.id.edit_modify_ssNumber);//社保号，

        edit_hCG = (EditText) findViewById(R.id.edit_modify_HCG);//hcg
        edit_work = (EditText) findViewById(R.id.edit_modify_work);//职业
        edit_pregnantCount = (EditText) findViewById(R.id.edit_modify_pregnantCount);//孕次
        edit_childCount = (EditText) findViewById(R.id.edit_modify_childCount);//产次
        edit_abortionCount = (EditText) findViewById(R.id.edit_modify_abortionCount);//流产次数
        edit_sexPartnerCount = (EditText) findViewById(R.id.edit_modify_sexPartnerCount);//性伙伴数
        edit_smokeTime = (EditText) findViewById(R.id.edit_modify_smokeTime);//吸烟史
        edit_checkNotes = (EditText) findViewById(R.id.edit_modify_checkNotes);//检查注释

        userManager = new UserManager();
        loginRegister = new LoginRegister();
    }
    private void initData() {
        spin_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//spinner点击事件
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        source = getString(R.string.patient_nothing);
                        break;
                    case 1:
                        source = getString(R.string.patient_hospitalization);
                        break;
                    case 2:
                        source = getString(R.string.patient_Outpatient_Department);
                        break;
                    case 3:
                        source = getString(R.string.patient_other);
                        break;
                        default:
                            break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin_marry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        marry = getString(R.string.patient_nothing);
                        break;
                    case 1:
                        marry = getString(R.string.patient_married);
                        break;
                    case 2:
                        marry = getString(R.string.patient_unmarried);
                        break;
                    default:
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
                switch (position) {
                    case 0://病人避孕方式
                        birthControlMode = getString(R.string.patient_nothing);
                        break;
                    case 1://病人避孕方式
                        birthControlMode = getString(R.string.patient_condom);
                        break;
                    case 2://
                        birthControlMode = getString(R.string.patient_acyterion);
                        break;
                    case 3://
                        birthControlMode = getString(R.string.patient_Contraceptive_needle);
                        break;
                    case 4://
                        birthControlMode = getString(R.string.patient_female_condom);
                        break;
                    case 5://
                        birthControlMode = getString(R.string.patient_Ligation);
                        break;
                    case 6://
                        birthControlMode = getString(R.string.patient_Women__IUD);
                        break;
                    default:
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
                switch (position) {
                    case 0://病人血型
                        bloodType = getString(R.string.patient_nothing);
                        break;
                    case 1://病人血型
                        bloodType = getString(R.string.patient_A_type);
                        break;
                    case 2://
                        bloodType = getString(R.string.patient_B_type);
                        break;
                    case 3://
                        bloodType = getString(R.string.patient_AB_type);
                        break;
                    case 4://
                        bloodType = getString(R.string.patient_O_type);
                        break;
                    case 5://
                        bloodType = getString(R.string.patient_other_type);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * 清除输入框数据
     * @param
     */
    private void initClear(){
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

    /**
     * 保存输入框信息
     * @param
     */
    private void initSave(){

        if(TextUtils.isEmpty(edit_djName.getText().toString().trim()) && TextUtils.isEmpty(edit_djPhone.getText().toString().trim())){
            edit_djName.requestFocus();
            MyToast.showToast(this, getString(R.string.patient_select_name));
//            Toast.makeText(getContext(),getContext().getString(R.string.patient_select_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(edit_djName.getText().toString().trim())){
            edit_djName.requestFocus();
            MyToast.showToast(this, getString(R.string.patient_select_name));
        }else{

            if(TextUtils.isEmpty(edit_djPhone.getText().toString().trim())){//判断电话是否为空
                edit_djPhone.requestFocus();
                MyToast.showToast(this, getString(R.string.case_select_telephoen_hint));
//                Toast.makeText(getContext(),getContext().getString(R.string.case_select_telephoen_hint), Toast.LENGTH_SHORT).show();
            }else {
                int phoneLength = edit_djPhone.getText().toString().trim().length();
//            int idNumberLength = edit_IdNumber.getText().toString().trim().length();
                if(edit_djPhone.getText().toString().length()==11 ||edit_djPhone.getText().toString().length()==8 ||edit_djPhone.getText().toString().length()==7){
//            if(phoneLength>4&&phoneLength<16){
//
//                if (idNumberLength==0||idNumberLength<31) {
                    String hCG = edit_hCG.getText().toString().trim();
                    String work=edit_work.getText().toString().trim();
                    String pregnantCount =edit_pregnantCount.getText().toString().trim();
                    String  childCount=edit_childCount.getText().toString().trim();
                    String abortionCount =edit_abortionCount.getText().toString().trim();
                    String sexPartnerCount =edit_sexPartnerCount.getText().toString().trim();
                    String  smokeTime=edit_smokeTime.getText().toString().trim();
                    String  checkNotes=edit_checkNotes.getText().toString().trim();

                    userManager.upDataUser(loginRegister.getUserName(Integer.parseInt(OneItem.getOneItem().getId())), edit_djName.getText().toString(),
                            edit_djPhone.getText().toString(), edit_djAge.getText().toString(), source, marry, OneItem.getOneItem().getId(),
                            edit_IdNumber.getText().toString().trim(), edit_CaseNumbe.getText().toString().trim(),
                            edit_SSNumber.getText().toString().trim(), hCG, work, pregnantCount, childCount, abortionCount,
                            bloodType, sexPartnerCount, smokeTime, birthControlMode, checkNotes);
                    Intent i = new Intent(ModifyActivity.this, MainActivity.class);
                    startActivity(i);
                    MyToast.showToast(this,getString(R.string.patient_modify_success));
//                    Toast.makeText(this,getString(R.string.patient_modify_success), Toast.LENGTH_SHORT).show();

//                } else {
//                    Toast.makeText(this, getString(R.string.patient_ID_error)+edit_IdNumber.getText().toString().trim().length()+getString(R.string.patient_ID_error2), Toast.LENGTH_SHORT).show();
//                }
//
                }else {
                    MyToast.showToast(this,getString(R.string.patient_telephone_error));
//                    Toast.makeText(this, getString(R.string.patient_telephone_error), Toast.LENGTH_SHORT).show();
                }
            }
//
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_modify_cancle:
               initClear();
                break;
            case R.id.text_modify_save:
                initSave();
                break;
            case R.id.btn_left:
                finish();
                break;
            default:
                break;
        }
    }
}
