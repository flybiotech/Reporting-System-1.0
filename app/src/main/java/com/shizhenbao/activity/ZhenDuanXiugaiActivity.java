package com.shizhenbao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.itextpdf.text.DocumentException;
import com.shizhenbao.constant.DeviceOfSize;
import com.shizhenbao.db.Backup;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.draw.GraffitiActivity;
import com.shizhenbao.fragments.FragSetting;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.PDFCreate;
import com.util.AlignedTextUtils;
import com.view.LoadingDialog;

import org.litepal.crud.DataSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class ZhenDuanXiugaiActivity extends AppCompatActivity implements View.OnClickListener,PDFCreate.PdfCreateInterface{
    private Button btn_right,bt_giveup,bt_choise,bt_baocun,bt_left,bt_address;
    private TextView tv_title;
    private EditText et_name,et_xibaoxue,et_dna,et_zhengzhuang,et_pinggu,et_quyu,et_yindaojin,et_nizhen,et_yijian,et_bianhao;
    private EditText et_bianjie,et_color,et_xueguan,et_dianranse,et_cusuan;
    User user=null;
    private String id;
    Diacrisis dia = null;
    private List<String>diaList;
    private List<Diacrisis>diacrisisList;
    private List<String>showList;
    private double screenInches;//屏幕的尺寸
    private LoadingDialog loadingDialog;
    EditText et_handle;
    public static final int REQ_CODE_GRAFFITI = 101;
    private TextView tv01,tv02,tv03,tv04,tv05,tv06,tv07,tv08,tv09,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18;
    private String[] tvName;
    ListView listView_self = null;
    EditText edit_self = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenInches = DeviceOfSize.getScreenSizeOfDevice2(this);
        if (screenInches > 6.0) {
            setContentView(R.layout.activity_zhen_duan_xiugai);
        } else {
            setContentView(R.layout.activity_zhen_duan_xiugai_phone);
        }

        Intent i=getIntent();
        id=i.getStringExtra("msg");
        initView();
        initText();//解决文字两端对齐的问题
        initData();
        initClick();
    }

    private void initData() {//页面展示数据源
        et_name.setText(user.getpName());
        et_xibaoxue.setText(user.getAdvice());
        et_dna.setText(user.getHpv_dna());
        et_zhengzhuang.setText(user.getSymptom());
        et_pinggu.setText(user.getSummary());
        et_quyu.setText(user.getBingbian());
        et_yindaojin.setText(user.getYindaojin());
        et_nizhen.setText(user.getNizhen());
        et_yijian.setText(user.getZhuyishixiang());
        et_bianjie.setText(user.getBianjie());
        et_color.setText(user.getColor());
        et_dianranse.setText(user.getDianranse());
        et_cusuan.setText(user.getCusuan());
        et_xueguan.setText(user.getXueguna());
        et_handle.setText(user.getHandle());
        loadingDialog = new LoadingDialog(this);

        PDFCreate.setPdfCreateInterfaceListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        user= new LoginRegister().getUserName(Integer.parseInt(id));//根据pId得到对应的User
        List<SystemSet> systemSet=DataSupport.findAll(SystemSet.class);
        for(int i=0;i<systemSet.size();i++){
           String localsn=systemSet.get(0).getLocal_svn();
            Const.sn=localsn;
        }
    }

    private void initView() {
        tvName = new String[]{getContext().getString(R.string.print_patient_id), getContext().getString(R.string.print_diagnosis_result), getContext().getString(R.string.patient_name), getContext().getString(R.string.print_cytology), getContext().getString(R.string.print_HPV_DNA),getContext().getString(R.string.print_symptom),
                getContext().getString(R.string.print_Overall_assessment), getContext().getString(R.string.print_Lesion_area), getContext().getString(R.string.print_colposcopic), getContext().getString(R.string.print_Suspected), getContext().getString(R.string.print_attention),getString(R.string.print_opinion),
                getContext().getString(R.string.print_Assessment_results), getContext().getString(R.string.print_border),  getContext().getString(R.string.print_color), getContext().getString(R.string.print_blood_vessel),
                getContext().getString(R.string.print_Iodine_staining), getContext().getString(R.string.print_Acetic_acid_change)};
        user= new LoginRegister().getUserName(Integer.parseInt(id));//根据pId得到对应的User
//        user.getImage();
        btn_right = (Button) findViewById(R.id.btn_right);//这个是菜单按钮
        tv_title = (TextView)findViewById(R.id.title_text);//这个是标题栏
        bt_baocun= (Button)findViewById(R.id.bt_baocun);//保存按钮
        bt_choise= (Button)findViewById(R.id.bt_choise);//选择图片
        bt_giveup= (Button) findViewById(R.id.bt_giveup);//清除
        et_dna= (EditText) findViewById(R.id.et_dna);//dna
        et_name= (EditText)findViewById(R.id.et_name);//姓名
        et_xibaoxue= (EditText) findViewById(R.id.et_xibaoxue);//细胞学
        et_zhengzhuang= (EditText) findViewById(R.id.et_zhengzhuang);//症状
        et_pinggu= (EditText) findViewById(R.id.et_pinggu);//评估
        et_yindaojin= (EditText)findViewById(R.id.et_yindaojin);//阴道镜
        et_quyu= (EditText) findViewById(R.id.et_quyu);//区域
        et_nizhen= (EditText)findViewById(R.id.et_nizhen);//拟诊
        et_yijian= (EditText)findViewById(R.id.et_yijian);//意见
        et_bianhao= (EditText)findViewById(R.id.et_bianhao);//编号
        bt_left= (Button)findViewById(R.id.btn_left);//返回
        bt_address= (Button) findViewById(R.id.bt_address);
        et_bianjie= (EditText) findViewById(R.id.et_bianjie);
        et_color= (EditText) findViewById(R.id.et_color);
        et_dianranse= (EditText)findViewById(R.id.et_dianranse);
        et_cusuan= (EditText) findViewById(R.id.et_cusuan);
        et_xueguan= (EditText) findViewById(R.id.et_xueguan);
        et_handle= (EditText) findViewById(R.id.et_handle);
        bt_left.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.print_Report_updata_title));
        btn_right.setOnClickListener(this);
        bt_giveup.setOnClickListener(this);
        bt_choise.setOnClickListener(this);
        bt_baocun.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        et_handle.setOnClickListener(this);
        bt_address.setOnClickListener(this);
        et_bianhao.setText(id);
        et_name.setEnabled(false);
        et_bianhao.setEnabled(false);
        et_handle.setEnabled(true);
        et_name.setEnabled(false);//设置姓名输入框为不可操作
        et_xibaoxue.setEnabled(true);
        et_yijian.setEnabled(true);
        et_nizhen.setEnabled(true);
        et_dna.setEnabled(true);
        et_pinggu.setEnabled(true);
        et_quyu.setEnabled(true);
        et_zhengzhuang.setEnabled(true);
        et_yindaojin.setEnabled(true);
        et_xibaoxue.setFocusableInTouchMode(false);
        et_xibaoxue.setClickable(true);
        et_dna.setFocusableInTouchMode(false);
        et_dna.setClickable(true);
        et_zhengzhuang.setFocusableInTouchMode(false);
        et_zhengzhuang.setClickable(true);
        et_pinggu.setFocusableInTouchMode(false);
        et_pinggu.setClickable(true);
        et_quyu.setFocusableInTouchMode(false);
        et_quyu.setClickable(true);
        et_yindaojin.setFocusableInTouchMode(false);
        et_yindaojin.setClickable(true);
        et_nizhen.setFocusableInTouchMode(false);
        et_nizhen.setClickable(true);
        et_yijian.setFocusableInTouchMode(false);
        et_yijian.setClickable(true);
        et_bianjie.setFocusableInTouchMode(false);
        et_bianjie.setClickable(true);
        et_color.setFocusableInTouchMode(false);
        et_color.setClickable(true);
        et_dianranse.setFocusableInTouchMode(false);
        et_dianranse.setClickable(true);
        et_cusuan.setFocusableInTouchMode(false);
        et_cusuan.setClickable(true);
        et_xueguan.setFocusableInTouchMode(false);
        et_xueguan.setClickable(true);
        et_handle.setFocusableInTouchMode(false);
        et_handle.setClickable(true);
        btn_right.setVisibility(View.INVISIBLE);
    }

    private void initClick(){
        et_xibaoxue.setOnClickListener(this);
        et_dna.setOnClickListener(this);
        et_zhengzhuang.setOnClickListener(this);
        et_pinggu.setOnClickListener(this);
        et_quyu.setOnClickListener(this);
        et_yindaojin.setOnClickListener(this);
        et_nizhen.setOnClickListener(this);
        et_yijian.setOnClickListener(this);
        et_bianjie.setOnClickListener(this);
        et_color.setOnClickListener(this);
        et_xueguan.setOnClickListener(this);
        et_dianranse.setOnClickListener(this);
        et_cusuan.setOnClickListener(this);
        et_handle.setOnClickListener(this);
    }


    //将TextView 中的文字两边对齐
    private void initText() {
        tv01 = (TextView)findViewById(R.id.tv_print_01);
        tv02 = (TextView)findViewById(R.id.tv_print_02);
        TextPaint tp = tv02.getPaint();
        tp.setFakeBoldText(true);
        tv03 = (TextView) findViewById(R.id.tv_print_03);
        tv04 = (TextView) findViewById(R.id.tv_print_04);
        tv05 = (TextView) findViewById(R.id.tv_print_05);
        tv06 = (TextView) findViewById(R.id.tv_print_06);
        tv07 = (TextView) findViewById(R.id.tv_print_07);
        tv08 = (TextView) findViewById(R.id.tv_print_08);
        tv09 = (TextView) findViewById(R.id.tv_print_09);
        tv10 = (TextView) findViewById(R.id.tv_print_10);
        tv11 = (TextView) findViewById(R.id.tv_print_11);
        tv12 = (TextView) findViewById(R.id.tv_print_12);
        tv13 = (TextView) findViewById(R.id.tv_print_13);
        TextPaint tp1 = tv13.getPaint();
        tp1.setFakeBoldText(true);
        tv14 = (TextView) findViewById(R.id.tv_print_14);
        tv15 = (TextView) findViewById(R.id.tv_print_15);
        tv16 = (TextView) findViewById(R.id.tv_print_16);
        tv17 = (TextView) findViewById(R.id.tv_print_17);
        tv18 = (TextView) findViewById(R.id.tv_print_18);
        TextView[] tvData = {tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17,tv18};
//        if (screenInches > 6.0) {
            for (int i=0;i<tvName.length;i++) {
                if (tvData[i] != null) {
                    tvData[i].setText(AlignedTextUtils.justifyString(tvName[i], 4));
                }

//            }
        }
    }



    //修改数据库
    private void initBaocun(){
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");//系统当前时间
        Date curDate =  new Date(System.currentTimeMillis());
        user.setAdvice(et_yijian.getText().toString());
        user.setHpv_dna(et_dna.getText().toString());
        user.setSymptom(et_zhengzhuang.getText().toString());
        user.setSummary(et_pinggu.getText().toString());
        user.setBingbian(et_quyu.getText().toString());
        user.setYindaojin(et_yindaojin.getText().toString());
        user.setNizhen(et_nizhen.getText().toString());
        user.setAdvice(et_xibaoxue.getText().toString());
        user.setZhuyishixiang(et_yijian.getText().toString());
        user.setCheckDate(formatter.format(curDate));
//        if(user.getImage()==2){
//            user.setImgPath(OneItem.getOneItem().getList());
//        }
//        user.setImgPath(OneItem.getOneItem().getList());
        user.setIs_diag(2);
        user.setBianjie(et_bianjie.getText().toString().trim());
        user.setColor(et_color.getText().toString().trim());
        user.setXueguna(et_xueguan.getText().toString().trim());
        user.setDianranse(et_dianranse.getText().toString().trim());
        user.setCusuan(et_cusuan.getText().toString().trim());
        user.setHandle(et_handle.getText().toString().trim());
        user.setPdfPath(user.getGatherPath()+"/"+(user.getpId() +"_"+ user.getpName() + ".pdf"));
        user.save();//保存所有改变
        OneItem.getOneItem().setC(false);//如果为true,诊断信息可以添加，如果为false,诊断信息不可以添加
        Const.pdfPath = user.getGatherPath() + "/" + (user.getpId() + "_" + user.getpName() + ".pdf");
        initCreatePDF(user);
    }
    //清除重新输入
    private void give_up(){
        et_yindaojin.setText("");
        et_xibaoxue.setText("");
        et_zhengzhuang.setText("");
        et_yijian.setText("");
        et_dna.setText("");
        et_nizhen.setText("");
        et_pinggu.setText("");
        et_quyu.setText("");
        et_bianjie.setText("");
        et_cusuan.setText("");
        et_dianranse.setText("");
        et_xueguan.setText("");
        et_color.setText("");
        et_handle.setText("");
        et_xibaoxue.setFocusable(true);//获取焦点
    }
    //跳转到照片展示页面
    private void openImage(){
        Intent intent=new Intent(getContext(),ImageActivity.class);
        intent.putExtra("msg",et_bianhao.getText().toString());//将编号传递过去
        intent.putExtra("msgName",et_name.getText().toString());
        startActivity(intent);
    }
    public void initCreatePDF(final User u){

        try {
            if (loadingDialog != null) {
                loadingDialog.setMessage(getString(R.string.print_pdf_dialog_loding));
                loadingDialog.dialogShow();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new PDFCreate(getContext()).initView(u,2);//点击生成pdf文件
                        OneItem.getOneItem().setD(1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showList(){//添加默认图片
        showList=new ArrayList<>();
        OneItem.getOneItem().getList().clear();
        if (new Item().getSD() != null) {
            File[] files = new File(new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim())).getGatherPath()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//所有.jpg格式的文件添加到数组中
                        a = true;
                    }
                    return a;
                }
            });
            //遍历数组动态添加图片到对象中去
            for (File file : files) {
                showList.add((new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim())).getGatherPath()+"/" + file.getName()));
            }
            if(showList.size()>0 && showList.size()<=2){
                for(int i=0;i<showList.size();i++){
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            }else if(showList.size()==0){
                for(int j=0;j<3;j++){
                    OneItem.getOneItem().getList().add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/kb.png");
                }
            }else {
                for(int i=0;i<3;i++){
                    OneItem.getOneItem().getList().add(showList.get(i));
                }
            }
            user=new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));
            user.setImgPath(OneItem.getOneItem().getList());
            user.save();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_giveup:
//                give_up();
                dialogViewDelete();
                break;
            case R.id.bt_choise:
                    Resources res = this.getResources();//添加空白图片到本地文件夹
                    BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.drawable.kb);
                    Bitmap img = d.getBitmap();
                    String fn = "kb.png";
                    String path = new Item().getSD() + "/AFLY/" + fn;
                    try{
                        OutputStream os = new FileOutputStream(path);
                        img.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.close();
                    }catch(Exception e){
                        Log.e("TAG", "", e);
                    }
                    openImage();
                break;
            case R.id.bt_baocun:
                final List<Doctor>doctorList=DataSupport.where("dName=?","Admin").find(Doctor.class);//只有超级用户才可以设置医院，查询Admin表中的数据
                for(int i=0;i<doctorList.size();i++){
                    if(!doctorList.get(0).getEdit_hos_name().equals("")&&!doctorList.get(0).getEdit_hos_keshi().equals("")){//判断医院名称是否为空
                        SystemSet s=new LoginRegister().getSystem();//新建数据库记录
                        OneItem.getOneItem().setHospital_name(s.getHospital_name());//添加医院名称记录
                        OneItem.getOneItem().setGather_path(s.getGather_path());//添加采集图片保存路径
                        user.setbResult(true);//设置为true时代表已经生成报告
                        user.save();//保存数据
                        createPDFDialog();//生成pdf文件
                    }else {
                        Doctor doctor=new LoginRegister().getDoctor(OneItem.getOneItem().getName());
                        if(doctor.isdAdmin()){
                            if(doctorList.get(0).getEdit_hos_name().equals("")){
//                                a=3;
                                Toast.makeText(getContext(), getString(R.string.print_hospital_setting), Toast.LENGTH_SHORT).show();
                                new FragSetting().HosName(getContext());
                            }else if(doctorList.get(0).getEdit_hos_keshi().equals("")){
//                                a=3;
                                Toast.makeText(getContext(),getString(R.string.print_department_setting), Toast.LENGTH_SHORT).show();
                                new FragSetting().Departments(getContext());
                            }
                        }else {
                            loadingDialog.dismiss();
//                            user.setIs_diag(1);
                            Toast.makeText(getContext(),getString(R.string.print_setting_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;
            case R.id.btn_left:
                finish();
                break;
            case R.id.et_xibaoxue:
                showTerm(1);
                break;
            case R.id.et_dna:
                showTerm(2);
                break;
            case R.id.et_zhengzhuang:
                showTerm(3);
                break;
            case R.id.et_pinggu:
                showTerm(4);
                break;
            case R.id.et_quyu:
                showTerm(5);
                break;
            case R.id.et_yindaojin:
                showTerm(6);
                break;
            case R.id.et_nizhen:
                showTerm(7);
                break;
            case R.id.et_yijian:
                showTerm(8);
                break;
            case R.id.et_color:
                showTerm(9);
                break;
            case R.id.et_xueguan:
                showTerm(10);
                break;
            case R.id.et_dianranse:
                showTerm(11);
                break;
            case R.id.et_cusuan:
                showTerm(12);
                break;
            case R.id.et_bianjie:
                showTerm(13);
                break;
            case R.id.et_handle:
                showTerm(14);
                break;
            case R.id.bt_address:
                // 图片路径
                User user=new LoginRegister().getUserName(Integer.parseInt(et_bianhao.getText().toString().trim()));

                String mImagePath = Environment.getExternalStorageDirectory()+"/AFLY/fwt.png";
                String mSavePath=user.getGatherPath()+"/";
//                    Toast.makeText(getContext(), "保存路径："+mSavePath, Toast.LENGTH_SHORT).show();
                GraffitiActivity.startActivityForResult(this, mImagePath,mSavePath, true,REQ_CODE_GRAFFITI);
                break;
            default:break;
        }
    }
    private void showTerm(int temp){
        diaList=new ArrayList<>();
        diacrisisList= DataSupport.findAll(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            switch (temp){
                case 1:
                    if(diacrisisList.get(i).getCytology()!=null&&!"".equals(diacrisisList.get(i).getCytology())){
                        diaList.add(diacrisisList.get(i).getCytology());
                    }
                    break;
                case 2:
                    if(diacrisisList.get(i).getDna()!=null&&!"".equals(diacrisisList.get(i).getDna())){
                        diaList.add(diacrisisList.get(i).getDna());
                    }
                    break;
                case 3:
                    if(diacrisisList.get(i).getSymptom()!=null&&!"".equals(diacrisisList.get(i).getSymptom())){
                        diaList.add(diacrisisList.get(i).getSymptom());
                    }
                    break;
                case 4:
                    if(diacrisisList.get(i).getAssessment()!=null&&!"".equals(diacrisisList.get(i).getAssessment())){
                        diaList.add(diacrisisList.get(i).getAssessment());
                    }
                    break;
                case 5:
                    if(diacrisisList.get(i).getRegion()!=null&&!"".equals(diacrisisList.get(i).getRegion())){
                        diaList.add(diacrisisList.get(i).getRegion());
                    }
                    break;
                case 6:
                    if(diacrisisList.get(i).getColposcopy()!=null&&!"".equals(diacrisisList.get(i).getColposcopy())){
                        diaList.add(diacrisisList.get(i).getColposcopy());
                    }

                    break;
                case 7:
                    if(diacrisisList.get(i).getSuspected()!=null&&!"".equals(diacrisisList.get(i).getSuspected())){
                        diaList.add(diacrisisList.get(i).getSuspected());
                    }
                    break;
                case 8:
                    if(diacrisisList.get(i).getAttention()!=null&&!"".equals(diacrisisList.get(i).getAttention())){
                        diaList.add(diacrisisList.get(i).getAttention());
                    }
                    break;
                case 9:
                    if(diacrisisList.get(i).getColor()!=null&&!"".equals(diacrisisList.get(i).getColor())){
                        diaList.add(diacrisisList.get(i).getColor());
                    }
                    break;
                case 10:
                    if(diacrisisList.get(i).getXueguna()!=null&&!"".equals(diacrisisList.get(i).getXueguna())){
                        diaList.add(diacrisisList.get(i).getXueguna());
                    }
                    break;
                case 11:
                    if(diacrisisList.get(i).getDianranse()!=null&&!"".equals(diacrisisList.get(i).getDianranse())){
                        diaList.add(diacrisisList.get(i).getDianranse());
                    }
                    break;
                case 12:
                    if(diacrisisList.get(i).getCusuan()!=null&&!"".equals(diacrisisList.get(i).getCusuan())){
                        diaList.add(diacrisisList.get(i).getCusuan());
                    }
                    break;
                case 13:
                    if(diacrisisList.get(i).getBianjie()!=null&&!"".equals(diacrisisList.get(i).getBianjie())){
                        diaList.add(diacrisisList.get(i).getBianjie());
                    }
                    break;
                case 14:
                    if(diacrisisList.get(i).getHandle()!=null&&!"".equals(diacrisisList.get(i).getHandle())){
                        diaList.add(diacrisisList.get(i).getHandle());
                    }
                default:break;
            }
        }
        lv_addDia(diaList,temp);
    }
    AlertDialog dialogdelete = null;
    private void dialogViewDelete() {
        if (dialogdelete == null) {
            dialogdelete = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.print_Notice_title))
                    .setCancelable(false)
                    .setMessage(getString(R.string.patient_clear_sure))
                    .setPositiveButton(getString(R.string.patient_empty), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            give_up();//清除
                            Toast.makeText(ZhenDuanXiugaiActivity.this,R.string.print_data_clear, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.patient_no_empty), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                            Logger.e("");
//                            adapter.notifyDataSetChanged();
                        }
                    })
                    .create();
        }
        dialogdelete.show();
    }
    private void lv_addDia(final List<String> list, final int temp){

        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        View view_self= LayoutInflater.from(this).inflate(R.layout.simple_list,null);
        linearLayoutMain.addView(view_self);
        view_self.setFadingEdgeLength(0);
        view_self.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView_self= (ListView) view_self.findViewById(R.id.lv_show);
        edit_self= (EditText) view_self.findViewById(R.id.et_show);
        ArrayAdapter selectAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView_self.setAdapter(selectAdapter);
        new Item().setListViewHeightBasedOnChildren(listView_self);//重新计算listview每个item的高度
        switch (temp){//给editText初始化值
            case 1:
                edit_self.setText(et_xibaoxue.getText().toString().trim());
                break;
            case 2:
                edit_self.setText(et_dna.getText().toString().trim());
                break;
            case 3:
                edit_self.setText(et_zhengzhuang.getText().toString().trim());
                break;
            case 4:
                edit_self.setText(et_pinggu.getText().toString().trim());
                break;
            case 5:
                edit_self.setText(et_quyu.getText().toString().trim());
                break;
            case 6:
                edit_self.setText(et_yindaojin.getText().toString().trim());
                break;
            case 7:
                edit_self.setText(et_nizhen.getText().toString().trim());
                break;
            case 8:
                edit_self.setText(et_yijian.getText().toString().trim());
                break;
            case 9:
                edit_self.setText(et_color.getText().toString().trim());
                break;
            case 10:
                edit_self.setText(et_xueguan.getText().toString().trim());
                break;
            case 11:
                edit_self.setText(et_dianranse.getText().toString().trim());
                break;
            case 12:
                edit_self.setText(et_cusuan.getText().toString().trim());
                break;
            case 13:
                edit_self.setText(et_bianjie.getText().toString().trim());
                break;
            case 14:
                edit_self.setText(et_handle.getText().toString().trim());
                break;
        }

//        linearLayoutMain.addView(edit);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.print_Choosing_terms)).setView(linearLayoutMain)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (temp){//将edit的值赋给各个选项的输入框
                            case 1:
                                et_xibaoxue.setText(edit_self.getText().toString());
                                break;
                            case 2:
                                et_dna.setText(edit_self.getText().toString());
                                break;
                            case 3:
                                et_zhengzhuang.setText(edit_self.getText().toString());
                                break;
                            case 4:
                                et_pinggu.setText(edit_self.getText().toString());
                                break;
                            case 5:
                                et_quyu.setText(edit_self.getText().toString());
                                break;
                            case 6:
                                et_yindaojin.setText(edit_self.getText().toString());
                                break;
                            case 7:
                                et_nizhen.setText(edit_self.getText().toString());
                                break;
                            case 8:
                                et_yijian.setText(edit_self.getText().toString());
                                break;
                            case 9:
                                et_color.setText(edit_self.getText().toString());
                                break;
                            case 10:
                                et_xueguan.setText(edit_self.getText().toString());
                                break;
                            case 11:
                                et_dianranse.setText(edit_self.getText().toString());
                                break;
                            case 12:
                                et_cusuan.setText(edit_self.getText().toString());
                                break;
                            case 13:
                                et_bianjie.setText(edit_self.getText().toString());
                                break;
                            case 14:
                                et_handle.setText(edit_self.getText().toString());
                                break;
                        }
                        dialog.cancel();
                    }
                }).create();
//        dialog.setView(edit);
        dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        dialog.show();
        listView_self.setFocusable(false);
        listView_self.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (temp){//根据点击给edit赋值
                    case 1:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
//                        diaList.remove(arg2);
                        break;
                    case 2:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 3:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 4:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 5:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 6:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 7:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 8:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 9:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 10:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 11:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 12:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 13:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                    case 14:
                        edit_self.setText(edit_self.getText().toString()+list.get(arg2));
                        break;
                }
                onResume();
            }
        });
        listView_self.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Diacrisis>diacrisisList;
                int diaId = 0;
                switch (temp){
                    case 1:
                        diacrisisList=DataSupport.where("cytology=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,1,list,i,listView_self,edit_self);
                        break;
                    case 2:
                        diacrisisList=DataSupport.where("dna=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,2,list,i,listView_self,edit_self);
                        break;
                    case 3:
                        diacrisisList=DataSupport.where("Symptom=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,3,list,i,listView_self,edit_self);
                        break;
                    case 4:
                        diacrisisList=DataSupport.where("Assessment=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,4,list,i,listView_self,edit_self);
                        break;
                    case 5:
                        diacrisisList=DataSupport.where("region=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,5,list,i,listView_self,edit_self);
                        break;
                    case 6:
                        diacrisisList=DataSupport.where("Colposcopy=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,6,list,i,listView_self,edit_self);
                        break;
                    case 7:
                        diacrisisList=DataSupport.where("Suspected=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,7,list,i,listView_self,edit_self);
                        break;
                    case 8:
                        diacrisisList=DataSupport.where("attention=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,8,list,i,listView_self,edit_self);
                        break;
                    case 9:
                        diacrisisList=DataSupport.where("color=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,9,list,i,listView_self,edit_self);
                        break;
                    case 10:
                        diacrisisList=DataSupport.where("xueguna=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,10,list,i,listView_self,edit_self);
                        break;
                    case 11:
                        diacrisisList=DataSupport.where("dianranse=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,11,list,i,listView_self,edit_self);
                        break;
                    case 12:
                        diacrisisList=DataSupport.where("cusuan=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,12,list,i,listView_self,edit_self);
                        break;
                    case 13:
                        diacrisisList=DataSupport.where("bianjie=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,13,list,i,listView_self,edit_self);
                        break;
                    case 14:
                        diacrisisList=DataSupport.where("handle=?",list.get(i)).find(Diacrisis.class);
                        for(int j=0;j<diacrisisList.size();j++){
                            diaId=diacrisisList.get(0).getDiaId();
                        }
                        initId(diaId,14,list,i,listView_self,edit_self);
                        break;
                }
                return false;
            }
        });
    }

    private void initId(int diaId, final int temp, final List<String>list, final int m, final ListView listView,final EditText edit){
        List<Diacrisis>diacrisisList=DataSupport.where("diaId=?",diaId+"").find(Diacrisis.class);
        for(int i=0;i<diacrisisList.size();i++){
            dia=diacrisisList.get(0);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.print_terms_delete));
        builder.setTitle(getString(R.string.print_terms_delete_title));
        builder.setPositiveButton(getString(R.string.image_manage_delete_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (temp){
                    case 1:
                        dia.setCytology("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter);
                        break;
                    case 2:
                        dia.setDna("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter1=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter1.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter1);
                        break;
                    case 3:
                        dia.setSymptom("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter2=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter2.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter2);
                        break;
                    case 4:
                        dia.setAssessment("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter3=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter3.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter3);
                        break;
                    case 5:
                        dia.setRegion("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter4=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter4.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter4);
                        break;
                    case 6:
                        dia.setColposcopy("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter5=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter5.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter5);
                        break;
                    case 7:
                        dia.setSuspected("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter6=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter6.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter6);
                        break;
                    case 8:
                        dia.setAttention("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter7=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter7.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter7);
                        break;
                    case 9:
                        dia.setColor("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter8=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter8.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter8);
                        break;
                    case 10:
                        dia.setXueguna("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter9=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter9.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter9);
                        break;
                    case 11:
                        dia.setDianranse("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter10=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter10.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter10);
                        break;
                    case 12:
                        dia.setCusuan("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter11=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter11.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter11);
                        break;
                    case 13:
                        dia.setBianjie("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter12=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter12.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter12);
                        break;
                    case 14:
                        dia.setHandle("");
                        dia.save();
                        edit.setText("");
                        list.remove(m);
                        ArrayAdapter selectAdapter13=new ArrayAdapter(ZhenDuanXiugaiActivity.this,android.R.layout.simple_list_item_1,list);
                        selectAdapter13.notifyDataSetChanged();
                        listView.setAdapter(selectAdapter13);
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.image_manage_delete_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void createPDFDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.print_pdf_dialog_title));
        builder.setMessage(getString(R.string.print_pdf_dialog_message));

//        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    if(user.getImage()==1){
                        showList();
                        initBaocun();//保存添加的诊断信息 并且生成PDF报告
                        onResume();//保存完成后刷新当前页面，使输入框为不可操作的状态
//                        OneItem.getOneItem().setD(1);
                        new Backup(ZhenDuanXiugaiActivity.this,Const.sn).initBackups();
                    }else if(user.getImage()==2){
                        initBaocun();
                        onResume();
                        new Backup(ZhenDuanXiugaiActivity.this,Const.sn).initBackups();
//                        OneItem.getOneItem().setD(2);
                    }

            }
        });
        builder.setNegativeButton(getString(R.string.image_cancel), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void pdfSuccess(int index) {
        if (index == 1) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                Toast.makeText(ZhenDuanXiugaiActivity.this, R.string.print_Report_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
