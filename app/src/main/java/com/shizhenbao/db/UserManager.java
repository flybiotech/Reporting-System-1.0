package com.shizhenbao.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import com.activity.R;
import com.orhanobut.logger.Logger;
import com.shizhenbao.constant.CreateFileConstant;
import com.shizhenbao.pop.Diacrisis;
import com.shizhenbao.pop.ExceptionManager;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/23.
 */

public class UserManager {
    Context context;
    public static List<Item>listItem=new ArrayList<Item>();
    public static final String TAG = "TAG3";
    OneItem oneItem;
    private BackupsUtils backupsUtils;
    public UserManager() {
        oneItem = OneItem.getOneItem();
    }
    public List<Item>stringList;//倒序集合
    public UserManager(Context context){
        this.context=context;
        oneItem = OneItem.getOneItem();
    }
    //保存病人信息
    public void save(User user, String source, String marry, String pId, String userName, String userAge,
                     String userTel, int doctorId, boolean isResult, String idNumber, String caseNumbe, String sSNumber ){
        user.setpSource(source);//病人来源
        user.setMarry(marry);//病人婚否
        user.setpId(pId);//病人id
        user.setpName(userName);//病人姓名
        user.setAge(userAge);//病人年龄
        user.setTel(userTel);//病人电话
        user.setOperId(doctorId);//该病人的诊断医生id
        user.setbResult(isResult);//true为生成报告，false为未生成报告
        user.setIdNumber(idNumber);//身份证号码
        user.setCaseNumbe(caseNumbe);//病历号
        user.setsSNumber(sSNumber);//社保号
        user.setIs_diag(1);
        user.setImage(1);
        user.setRegistDate(CreateFileConstant.getCurrentDate());//患者登记的时间
        user.setRegistDateint(isDate2Bigger(CreateFileConstant.getCurrentDate()));//患者登记的时间
        user.save();

    }

    //保存病人信息
    public boolean save(User user,String source,String marry,String pId,String userName,String userAge,
                     String userTel,int doctorId,boolean isResult,String idNumber,String caseNumbe,
                     String sSNumber ,String hCG,String work,String pregnantCount ,String  childCount,
                     String abortionCount , String bloodType ,String sexPartnerCount ,String  smokeTime,
                     String birthControlMode, String checkNotes  ){
        List<User>users=LitePal.where("pId=?",pId).find(User.class);
        boolean isSave = false;
        if(users.size()==0){
            user.setpSource(source);//病人来源
            user.setMarry(marry);//病人婚否
            user.setpId(pId);//病人id
            user.setpName(userName);//病人姓名
            user.setAge(userAge);//病人年龄
            user.setTel(userTel);//病人电话
            user.setOperId(doctorId);//该病人的诊断医生id
            user.setbResult(isResult);//true为生成报告，false为未生成报告
            user.setIdNumber(idNumber);//身份证号码
            user.setCaseNumbe(caseNumbe);//病历号
            user.setsSNumber(sSNumber);//社保号
            user.setIs_diag(1);
            user.setRegistDate(CreateFileConstant.getCurrentDate());//患者登记的时间
            user.setRegistDateint(isDate2Bigger(CreateFileConstant.getCurrentDate()));//患者登记的时间
            user.sethCG(hCG);
            user.setWork(work);
            user.setPregnantCount(pregnantCount);
            user.setChildCount(childCount);
            user.setAbortionCount(abortionCount);
            user.setBloodType(bloodType);
            user.setSexPartnerCount(sexPartnerCount);
            user.setSmokeTime(smokeTime);
            user.setBirthControlMode(birthControlMode);
            user.setCheckNotes(checkNotes);
            user.setGatherPath("/"+OneItem.getOneItem().getGather_path()+"/");
            user.setImage(1);
            isSave = user.save();
            new CreateFileConstant().initCreateUser(user,pId+"_"+userName);
            new CreateFileConstant().initCreateCutPath(user,pId+"_"+userName,"User_cut");
        }else {
            for(int i=0;i<users.size();i++){
                users.get(0).setpSource(source);//病人来源
                users.get(0).setMarry(marry);//病人婚否
                users.get(0).setpId(pId);//病人id
                users.get(0).setpName(userName);//病人姓名
                users.get(0).setAge(userAge);//病人年龄
                users.get(0).setTel(userTel);//病人电话
                users.get(0).setOperId(doctorId);//该病人的诊断医生id
                users.get(0).setbResult(isResult);//true为生成报告，false为未生成报告
                users.get(0).setIdNumber(idNumber);//身份证号码
                users.get(0).setCaseNumbe(caseNumbe);//病历号
                users.get(0).setsSNumber(sSNumber);//社保号
                users.get(0).setIs_diag(1);
                users.get(0).setImage(1);
                users.get(0).setRegistDate(CreateFileConstant.getCurrentDate());//患者登记的时间
                users.get(0).setRegistDateint(isDate2Bigger(CreateFileConstant.getCurrentDate()));//患者登记的时间
                users.get(0).sethCG(hCG);
                users.get(0).setWork(work);
                users.get(0).setPregnantCount(pregnantCount);
                users.get(0).setChildCount(childCount);
                users.get(0).setAbortionCount(abortionCount);
                users.get(0).setBloodType(bloodType);
                users.get(0).setSexPartnerCount(sexPartnerCount);
                users.get(0).setSmokeTime(smokeTime);
                users.get(0).setBirthControlMode(birthControlMode);
                users.get(0).setCheckNotes(checkNotes);
//                users.get(0).setGatherPath("/"+OneItem.getOneItem().getGather_path()+"/");
                isSave = users.get(0).save();
            }
        }
//        backupsUtils = new BackupsUtils(context);
////        backupsUtils.initBackUpUser(1);
        return isSave;
    }

    //自动生成编号
    public void bianhao(EditText editText){
        List<User> list= LitePal.where("pName!=?","").find(User.class);//查询所有不为空的User数据库中的数据
        for(int i=0;i<list.size();i++){
            editText.setText(String.valueOf(Integer.parseInt(list.get(list.size()-1).getpId())+1)+"("+context.getString(R.string.patient_unregistered) +")");//遍历list集合，将最后一项的pId+1赋值给edit_bianhao
        }
        OneItem oneItem= OneItem.getOneItem();//单例模式赋值
        if(list.size()==0){
            editText.setText("1"+"("+context.getString(R.string.patient_unregistered) +")");//当第一次登记时，默认id为0
        }else{
            oneItem.setId(String.valueOf(Integer.parseInt(list.get(list.size()-1).getpId())+1)+"("+context.getString(R.string.patient_unregistered) +")");//设置实体类id字段的值
        }
    }
    public void getExceName(){//异常处理，得到登陆的账户
        List<ExceptionManager>exceptionManagerList=LitePal.findAll(ExceptionManager.class);
        for(int i=0;i<exceptionManagerList.size();i++){
            OneItem.getOneItem().setName(exceptionManagerList.get(0).getLoginName());
        }
    }
    //修改病人信息 //得到符合条件的User
    public void upDataUser(User user, String userName, String userTel, String userAge, String source,
                           String marry, String userId,String idNumber,String caseNumbe,String sSNumber,
                           String hCG,String work,String pregnantCount ,String  childCount,
                           String abortionCount , String bloodType ,String sexPartnerCount ,String  smokeTime,
                           String birthControlMode, String checkNotes  ) {
        user.setpName(userName);
        user.setTel(userTel);
        user.setAge(userAge);
        user.setpSource(source);
        user.setMarry(marry);
        user.setIdNumber(idNumber);
        user.setCaseNumbe(caseNumbe);
        user.setsSNumber(sSNumber);

        user.sethCG(hCG);
        user.setWork(work);
        user.setPregnantCount(pregnantCount);
        user.setChildCount(childCount);
        user.setAbortionCount(abortionCount);
        user.setBloodType(bloodType);
        user.setSexPartnerCount(sexPartnerCount);
        user.setSmokeTime(smokeTime);
        user.setBirthControlMode(birthControlMode);
        user.setCheckNotes(checkNotes);

        user.updateAll("pId=?",userId );//修改符合条件的User
        user.save();
    }


    /**
     *  其中的 % 表示的是模糊查询
     * @param operId
     * @param f1
     * @param f2
     * @param f3
     * @param f4
     * @param f5
     * @param list
     */
    //跟据社保号，身份证号或者其他号码，来查询病人相关的信息
     //   身份证号，          病例号                社保号           护照号
    //f1,f2,f3,f4 分别代表相关 的证件号有没有数据
    //这个是医生用的，可以查看医生名下的所有患者的信息
    public  void getUserBySearch(String operId,boolean f1,boolean f2,boolean f3,boolean f4,boolean f5,List<String >list,int offSetCount) {

        List<User> list1 = new ArrayList<User>();
        List<String> list2 = new ArrayList<String>();
        int limitCount = 30;
        int i=0;
        StringBuilder sBuilder = new StringBuilder();
        if (f1) {
            i=i+1;
//            sBuilder.append("idNumber =?   and   ");
            list2.add("idNumber like  ?");
        }
        if (f2) {
            i=i+1;
            list2.add("caseNumbe like  ?");
        }
        if (f3) {
            i=i+1;
            list2.add("sSNumber like  ?");
        }

        if (f4) {
            i=i+1;
            list2.add("pName like  ?");
        }

        if (f5) {
            i=i+1;
            list2.add("registDate like  ?");
        }


        switch (i) {
            case 0://查询所有的患者的信息
                list1 = LitePal.where("operId=?", operId).limit(limitCount).find(User.class);

                break;
            case 1:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0),operId,"%"+list.get(0)+"%").limit(limitCount).find(User.class);
                break;
            case 2:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1),operId,"%"+list.get(0)+"%","%"+list.get(1)+"%").limit(limitCount).find(User.class);
                break;
            case 3:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2),operId, "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%").limit(limitCount).find(User.class);
                break;
            case 4:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), operId,"%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%").limit(limitCount).find(User.class);
                break;

            case 5:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),operId,"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%").limit(limitCount).find(User.class);
                break;
        }

        oneItem.setListUser(list1);
//        return list1;
    }


    //这个是admin 使用的，可以查看所有病人的信息
    public  void getUserBySearch(boolean f1,boolean f2,boolean f3,boolean f4,boolean f5,List<String >list) {
        List<User> list1 = new ArrayList<User>();
        List<String> list2 = new ArrayList<String>();
        int limitCount = 30;
        int i=0;
        if (f1) {
            i=i+1;
            list2.add("idNumber like  ?");
        }
        if (f2) {
            i=i+1;
            list2.add("caseNumbe like  ?");
        }
        if (f3) {
            i=i+1;
            list2.add("sSNumber like  ?");
        }

        if (f4) {
            i=i+1;
            list2.add("pName like  ?");
        }

        if (f5) {
            i=i+1;
            list2.add("registDate like  ?");
        }

        String str = "pName!=?";
        switch (i) {

            case 0://查询所有的患者的信息
                list1 = LitePal.where("pName!=?","").limit(limitCount).find(User.class);
                break;
            case 1:
                list1 = LitePal.where(list2.get(0),"%"+list.get(0)+"%").limit(limitCount).find(User.class);

                break;
            case 2:
                list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1),"%"+list.get(0)+"%","%"+list.get(1)+"%").limit(limitCount).find(User.class);
                break;
            case 3:
                list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%").limit(limitCount).find(User.class);
                break;
            case 4:
                list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%").limit(limitCount).find(User.class);
                break;

            case 5:
                list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%").limit(limitCount).find(User.class);
                break;

        }
        oneItem.setListUser(list1);
    }

    //Item是一个公共变量，将数据保存在这个公共变量里
    public   List<Item> getUserDate() {
        stringList=new ArrayList<>();
        stringList.clear();
        listItem.clear();
        Log.d(TAG, "getUserDate: ");
        for(User u:oneItem.getListUser()){
            if(u.getIs_diag()==1){
//                String zhanshi=context.getString(R.string.patient_name)+":"+u.getpName()+"  "+context.getString(R.string.patient_age)+":"+u.getAge()+"  "+context.getString(R.string.patient_telephone)+":"+u.getTel()+"  "+context.getString(R.string.patient_source)+":"+u.getpSource()+"  "+context.getString(R.string.patient_marriage)+":"+u.getMarry();
                Item item=new Item();
                item.setpId(u.getpId());
                item.setpName(u.getpName());
                item.setAge(u.getAge());
                item.setTel(u.getTel());
                item.setTv_pResouce(u.getpSource());
                item.setTv_pMarray(u.getMarry());
                listItem.add(item);

//                Log.d(TAG, "getUserDate:  "+zhanshi);
            }
        }
        for(int j=listItem.size()-1;j<listItem.size()&&j>=0;j--){
            stringList.add(listItem.get(j));
//            stringList.get(j).getpName();
        }
        return stringList;
    }



    //-----------------------病例管理----------------------------------

    /**
     *  根据  姓名，       电话号码，   病例号     ， 社保号来查询病人的病例信息
     * f1,f2,f3,f4 分别代表相关 的证件号有没有数据
     * @param operId
     * @param f1
     * @param f2
     * @param f3
     * @param f4
     * @param list
     * 这个是医生用的，可以查看医生名下的所有患者的信息
     */
    public  List<User> getUserCaseBySearch(String operId,boolean f1,boolean f2,boolean f3,boolean f4,boolean f5,boolean f6,List<String >list) {
        List<User> list1 = new ArrayList<User>();
        List<String> list2 = new ArrayList<String>();
        int i=0;
//        StringBuilder sBuilder = new StringBuilder();
        if (f1) {
            i=i+1;
//            sBuilder.append("idNumber =?   and   ");
            list2.add("pName like  ?");//姓名
        }
        if (f2) {
            i=i+1;
            list2.add("tel like  ?");//电话号码
        }
        if (f3) {
            i=i+1;
            list2.add("caseNumbe like  ?");//病例号
        }

        if (f4) {
            i=i+1;
            list2.add("sSNumber like  ?");//社保号
        }

        if (f5) {  //比较开始日期
            i=i+1;
            list2.add("registDateint >  ?");
        }

        if (f6) {  //比较结束日期
            i=i+1;
            list2.add("registDateint <  ?");
        }


        switch (i) {
            case 0://查询所有的患者的信息
                list1 = LitePal.where("operId=?", operId).find(User.class);
                break;
            case 1:
                list1 = LitePal.where("operId=?"+"   and   "+list2.get(0),operId,"%"+list.get(0)+"%").find(User.class);
                break;
            case 2:
                if (f5 && f6) {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1),operId,list.get(0),list.get(1)).find(User.class);
                } else {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1),operId,"%"+list.get(0)+"%","%"+list.get(1)+"%").find(User.class);
                }

                break;
            case 3:
                if (f5) {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2),operId, "%"+list.get(0)+"%",list.get(1),list.get(2)).find(User.class);

                } else {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2),operId, "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%").find(User.class);

                }
                break;
            case 4:
                if (f5) {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), operId,"%"+list.get(0)+"%","%"+list.get(1)+"%",list.get(2),list.get(3)).find(User.class);

                } else {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), operId,"%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%").find(User.class);

                }
                    break;

            case 5:
                if (f5) {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),operId,"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%",list.get(3),list.get(4)).find(User.class);

                } else {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),operId,"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%").find(User.class);

                }
                  break;
            case 6:
                if (f5) {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4)+"   and   "+list2.get(5),operId,"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%",list.get(4),list.get(5)).find(User.class);

                } else {
                    list1 = LitePal.where("operId=?"+"   and   "+list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4)+"   and   "+list2.get(5),operId,"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%","%"+list.get(5)+"%").find(User.class);

                }
                break;

        }

//        oneItem.setListUser(list1);
        return list1;
    }

    /**
     * 这个是admin 使用的，可以查看所有病人的信息
     * @param list
     */
    public  List<User> getUserCaseBySearch(boolean f1,boolean f2,boolean f3,boolean f4,boolean f5,boolean f6,List<String >list) {
        List<User> list1 = new ArrayList<User>();
        List<String> list2 = new ArrayList<String>();
        int i=0;
        if (f1) {
            i=i+1;
//            sBuilder.append("idNumber =?   and   ");
            list2.add("pName like  ?");//姓名
        }
        if (f2) {
            i=i+1;
            list2.add("tel like  ?");//电话号码
        }
        if (f3) {
            i=i+1;
            list2.add("caseNumbe like  ?");//病例号
        }

        if (f4) {
            i=i+1;
            list2.add("sSNumber like  ?");//社保号
        }

        if (f5) {  //比较开始日期
            i=i+1;
            list2.add("registDateint > ? ");
        }

        if (f6) {  //比较结束日期
            i=i+1;
            list2.add("registDateint <  ?");
        }

//        String str = "pName!=?";
        switch (i) {

            case 0://查询所有的患者的信息
                list1 = LitePal.where("pName!=?","").find(User.class);
                break;
            case 1:
                list1 = LitePal.where(list2.get(0),"%"+list.get(0)+"%").find(User.class);

                break;
            case 2:
//                Logger.e("UserManager运行到这里了");
                if (f5 && f6) {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1),list.get(0),list.get(1)).find(User.class);
//                    Logger.e("  1 list1.size : "+list1.size());
                } else {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1),"%"+list.get(0)+"%","%"+list.get(1)+"%").find(User.class);
//                    Logger.e("  2 list1.size : "+list1.size());
                }
                break;
            case 3:
                if (f5) {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2), "%"+list.get(0)+"%",list.get(1),list.get(2)).find(User.class);

                } else {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%").find(User.class);
                }
                break;
            case 4:
                if (f5) {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), "%"+list.get(0)+"%","%"+list.get(1)+"%",list.get(2),list.get(3)).find(User.class);

                } else {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3), "%"+list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%").find(User.class);

                }
                break;

            case 5:
                if (f5) {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%",list.get(3),list.get(4)).find(User.class);

                } else {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4),"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%").find(User.class);

                }
                break;

            case 6:
                if (f5) {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4)+"   and   "+list2.get(5),"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%",list.get(4),list.get(5)).find(User.class);

                } else {
                    list1 = LitePal.where(list2.get(0)+"  and  "+list2.get(1)+"   and   "+list2.get(2)+"   and   "+list2.get(3)+"   and   "+list2.get(4)+"   and   "+list2.get(5),"%"+ list.get(0)+"%","%"+list.get(1)+"%","%"+list.get(2)+"%","%"+list.get(3)+"%","%"+list.get(4)+"%","%"+list.get(5)+"%").find(User.class);

                }
                break;

        }
        return list1;
//        oneItem.setListUser(list1);
    }



    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date

     * @return true <br/>false
     */
//    public static boolean isDate2Bigger(String str1, String str2) {
//        boolean isBigger = false;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date dt1 = null;
//        Date dt2 = null;
//        try {
//            dt1 = sdf.parse(str1);
//            dt2 = sdf.parse(str2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (dt1.getTime() > dt2.getTime()) {
//            isBigger = false;
//        } else if (dt1.getTime() <= dt2.getTime()) {
//            isBigger = true;
//        }
//        return isBigger;
//    }


    public static long isDate2Bigger(String str1) {
//        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
//        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
//            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1 == null) {
            return 0;
        }

        return dt1.getTime();
    }

    public void showTermList() {
        Diacrisis diacrisis = new Diacrisis();
        diacrisis.setDiaId(1);
        diacrisis.setCytology(context.getString(R.string.term_cytology1));
        diacrisis.setDna(context.getString(R.string.term_HPV_DNA1));
        diacrisis.setSymptom(context.getString(R.string.term_symptom1));
        diacrisis.setAssessment(context.getString(R.string.term_Assessment1));
        diacrisis.setRegion(context.getString(R.string.term_Lesion1));
        diacrisis.setColposcopy(context.getString(R.string.term_colposcopic1));
        diacrisis.setSuspected(context.getString(R.string.term_Suspected1));
        diacrisis.setAttention(context.getString(R.string.term_attention1));
        diacrisis.setBianjie(context.getString(R.string.term_border1));
        diacrisis.setColor(context.getString(R.string.term_color1));
        diacrisis.setDianranse(context.getString(R.string.term_Iodine_staining1));
        diacrisis.setXueguna(context.getString(R.string.term_blood_vessel1));
        diacrisis.setCusuan(context.getString(R.string.term_Acetic_acid1));
        diacrisis.setHandle(context.getString(R.string.term_opinion1));//处理意见
        diacrisis.save();
        Diacrisis diacrisis1 = new Diacrisis();
        diacrisis1.setDiaId(2);
        diacrisis1.setCytology(context.getString(R.string.term_cytology2));
        diacrisis1.setDna(context.getString(R.string.term_HPV_DNA2));
        diacrisis1.setSymptom(context.getString(R.string.term_symptom2));
        diacrisis1.setAssessment(context.getString(R.string.term_Assessment2));
        diacrisis1.setRegion(context.getString(R.string.term_Lesion2));
        diacrisis1.setColposcopy(context.getString(R.string.term_colposcopic2));
        diacrisis1.setSuspected(context.getString(R.string.term_Suspected2));
        diacrisis1.setAttention(context.getString(R.string.term_attention2));
        diacrisis1.setBianjie(context.getString(R.string.term_border2));
        diacrisis1.setColor(context.getString(R.string.term_color2));
        diacrisis1.setDianranse(context.getString(R.string.term_Iodine_staining2));
        diacrisis1.setXueguna(context.getString(R.string.term_blood_vessel2));
        diacrisis1.setCusuan(context.getString(R.string.term_Acetic_acid2));
        diacrisis1.setHandle(context.getString(R.string.term_opinion2));
        diacrisis1.save();
        Diacrisis diacrisis2 = new Diacrisis();
        diacrisis2.setDiaId(3);
        diacrisis2.setCytology(context.getString(R.string.term_cytology3));
        diacrisis2.setDna(context.getString(R.string.term_HPV_DNA3));
        diacrisis2.setSymptom(context.getString(R.string.term_symptom3));
        diacrisis2.setAssessment(context.getString(R.string.term_Assessment3));
        diacrisis2.setRegion(context.getString(R.string.term_Lesion3));
        diacrisis2.setColposcopy(context.getString(R.string.term_colposcopic3));
        diacrisis2.setSuspected(context.getString(R.string.term_Suspected3));
        diacrisis2.setAttention(context.getString(R.string.term_attention3));
        diacrisis2.setBianjie(context.getString(R.string.term_border3));
        diacrisis2.setColor(context.getString(R.string.term_color3));
        diacrisis2.setDianranse(context.getString(R.string.term_Iodine_staining3));
        diacrisis2.setXueguna(context.getString(R.string.term_blood_vessel3));
        diacrisis2.setCusuan(context.getString(R.string.term_Acetic_acid3));
        diacrisis2.setHandle(context.getString(R.string.term_opinion3));
        diacrisis2.save();
        Diacrisis diacrisis3 = new Diacrisis();
        diacrisis3.setDiaId(4);
        diacrisis3.setCytology(context.getString(R.string.term_cytology4));
        diacrisis3.setSymptom(context.getString(R.string.term_symptom4));
        diacrisis3.setDna(context.getString(R.string.term_HPV_DNA4));
        diacrisis3.setAssessment(context.getString(R.string.term_Assessment4));
        diacrisis3.setRegion(context.getString(R.string.term_Lesion4));
        diacrisis3.setColposcopy(context.getString(R.string.term_colposcopic4));
        diacrisis3.setSuspected(context.getString(R.string.term_Suspected4));
        diacrisis3.setAttention(context.getString(R.string.term_attention4));
        diacrisis3.setBianjie(context.getString(R.string.term_border4));
        diacrisis3.setColor(context.getString(R.string.term_color4));
        diacrisis3.setDianranse(context.getString(R.string.term_Iodine_staining4));
        diacrisis3.setXueguna(context.getString(R.string.term_blood_vessel4));
        diacrisis3.setCusuan(context.getString(R.string.term_Acetic_acid4));
        diacrisis3.setHandle(context.getString(R.string.term_opinion4));
        diacrisis3.save();
        Diacrisis diacrisis4 = new Diacrisis();
        diacrisis4.setDiaId(5);
        diacrisis4.setCytology(context.getString(R.string.term_cytology5));
        diacrisis4.setDna(context.getString(R.string.term_HPV_DNA5));
        diacrisis4.setSymptom(context.getString(R.string.term_symptom5));
        diacrisis4.setAssessment(context.getString(R.string.term_Assessment5));
        diacrisis4.setRegion(context.getString(R.string.term_Lesion5));
        diacrisis4.setColposcopy(context.getString(R.string.term_colposcopic5));
        diacrisis4.setSuspected(context.getString(R.string.term_Suspected5));
        diacrisis4.setAttention(context.getString(R.string.term_attention5));
        diacrisis4.setBianjie(context.getString(R.string.term_border5));
        diacrisis4.setColor(context.getString(R.string.term_color5));
        diacrisis4.setDianranse(context.getString(R.string.term_Iodine_staining5));
        diacrisis4.setXueguna(context.getString(R.string.term_blood_vessel5));
        diacrisis4.setCusuan(context.getString(R.string.term_Acetic_acid5));
        diacrisis4.setHandle(context.getString(R.string.term_opinion5));
        diacrisis4.save();
        Diacrisis diacrisis5 = new Diacrisis();
        diacrisis5.setDiaId(6);
        diacrisis5.setCytology(context.getString(R.string.term_cytology6));
        diacrisis5.setDna(context.getString(R.string.term_HPV_DNA6));
        diacrisis5.setSymptom(context.getString(R.string.term_symptom6));
        diacrisis5.setAssessment(context.getString(R.string.term_Assessment6));
        diacrisis5.setRegion(context.getString(R.string.term_Lesion6));
        diacrisis5.setColposcopy(context.getString(R.string.term_colposcopic6));
        diacrisis5.setSuspected(context.getString(R.string.term_Suspected6));
        diacrisis5.setAttention(context.getString(R.string.term_attention6));
        diacrisis5.setBianjie(context.getString(R.string.term_border6));
        diacrisis5.setColor(context.getString(R.string.term_color6));
        diacrisis5.setDianranse(context.getString(R.string.term_Iodine_staining6));
        diacrisis5.setXueguna(context.getString(R.string.term_blood_vessel6));
        diacrisis5.setCusuan(context.getString(R.string.term_Acetic_acid6));
        diacrisis5.setHandle(context.getString(R.string.term_opinion6));
        diacrisis5.save();
        Diacrisis diacrisis6 = new Diacrisis();
        diacrisis6.setDiaId(7);
        diacrisis6.setCytology(context.getString(R.string.term_cytology7));
        diacrisis6.setDna(context.getString(R.string.term_HPV_DNA7));
        diacrisis6.setSymptom(context.getString(R.string.term_symptom7));
        diacrisis6.setAssessment(context.getString(R.string.term_Assessment7));
        diacrisis6.setRegion(context.getString(R.string.term_Lesion7));
        diacrisis6.setColposcopy(context.getString(R.string.term_colposcopic7));
        diacrisis6.setSuspected(context.getString(R.string.term_Suspected7));
        diacrisis6.setAttention(context.getString(R.string.term_attention7));
        diacrisis6.setBianjie(context.getString(R.string.term_border7));
        diacrisis6.setColor(context.getString(R.string.term_color7));
        diacrisis6.setDianranse(context.getString(R.string.term_Iodine_staining7));
        diacrisis6.setXueguna(context.getString(R.string.term_blood_vessel7));
        diacrisis6.setCusuan(context.getString(R.string.term_Acetic_acid7));
        diacrisis6.setHandle(context.getString(R.string.term_opinion7));
        diacrisis6.save();
        Diacrisis diacrisis7 = new Diacrisis();
        diacrisis7.setDiaId(8);
        diacrisis7.setCytology(context.getString(R.string.term_cytology8));
        diacrisis7.setDna(context.getString(R.string.term_HPV_DNA8));
        diacrisis7.setSymptom(context.getString(R.string.term_symptom8));
        diacrisis7.setAssessment(context.getString(R.string.term_Assessment8));
        diacrisis7.setRegion(context.getString(R.string.term_Lesion8));
        diacrisis7.setColposcopy(context.getString(R.string.term_colposcopic8));
        diacrisis7.setSuspected(context.getString(R.string.term_Suspected8));
        diacrisis7.setAttention(context.getString(R.string.term_attention8));
        diacrisis7.setBianjie(context.getString(R.string.term_border8));
        diacrisis7.setColor(context.getString(R.string.term_color8));
        diacrisis7.setDianranse(context.getString(R.string.term_Iodine_staining8));
        diacrisis7.setXueguna(context.getString(R.string.term_blood_vessel8));
        diacrisis7.setCusuan(context.getString(R.string.term_Acetic_acid8));
        diacrisis7.setHandle(context.getString(R.string.term_opinion8));
        diacrisis7.save();
        Diacrisis diacrisis8 = new Diacrisis();
        diacrisis8.setDiaId(9);
        diacrisis8.setCytology(context.getString(R.string.term_cytology9));
        diacrisis8.setDna(context.getString(R.string.term_HPV_DNA9));
        diacrisis8.setSymptom(context.getString(R.string.term_symptom9));
        diacrisis8.setAssessment(context.getString(R.string.term_Assessment9));
        diacrisis8.setRegion(context.getString(R.string.term_Lesion9));
        diacrisis8.setColposcopy(context.getString(R.string.term_colposcopic9));
        diacrisis8.setSuspected(context.getString(R.string.term_Suspected9));
        diacrisis8.setAttention(context.getString(R.string.term_attention9));
        diacrisis8.setBianjie(context.getString(R.string.term_border9));
        diacrisis8.setColor(context.getString(R.string.term_color9));
        diacrisis8.setDianranse(context.getString(R.string.term_Iodine_staining9));
        diacrisis8.setXueguna(context.getString(R.string.term_blood_vessel9));
        diacrisis8.setCusuan(context.getString(R.string.term_Acetic_acid9));
        diacrisis8.setHandle(context.getString(R.string.term_opinion9));
        diacrisis8.save();
        Diacrisis diacrisis9 = new Diacrisis();
        diacrisis9.setDiaId(10);
        diacrisis9.setCytology(context.getString(R.string.term_cytology10));
        diacrisis9.setDna(context.getString(R.string.term_HPV_DNA10));
        diacrisis9.setSymptom(context.getString(R.string.term_symptom10));
        diacrisis9.setAssessment(context.getString(R.string.term_Assessment10));
        diacrisis9.setRegion(context.getString(R.string.term_Lesion10));
        diacrisis9.setColposcopy(context.getString(R.string.term_colposcopic10));
        diacrisis9.setSuspected(context.getString(R.string.term_Suspected10));
        diacrisis9.setAttention(context.getString(R.string.term_attention10));
        diacrisis9.setBianjie(context.getString(R.string.term_border10));
        diacrisis9.setColor(context.getString(R.string.term_color10));
        diacrisis9.setDianranse(context.getString(R.string.term_Iodine_staining10));
        diacrisis9.setXueguna(context.getString(R.string.term_blood_vessel10));
        diacrisis9.setCusuan(context.getString(R.string.term_Acetic_acid10));
        diacrisis9.setHandle(context.getString(R.string.term_opinion10));
        diacrisis9.save();
        Diacrisis diacrisis10 = new Diacrisis();
        diacrisis10.setDiaId(11);
        diacrisis10.setCytology(context.getString(R.string.term_cytology11));
        diacrisis10.setDna(context.getString(R.string.term_HPV_DNA11));
        diacrisis10.setSymptom(context.getString(R.string.term_symptom11));
        diacrisis10.setAssessment(context.getString(R.string.term_Assessment11));
        diacrisis10.setRegion(context.getString(R.string.term_Lesion11));
        diacrisis10.setColposcopy(context.getString(R.string.term_colposcopic11));
        diacrisis10.setSuspected(context.getString(R.string.term_Suspected11));
        diacrisis10.setAttention(context.getString(R.string.term_attention11));
        diacrisis10.setBianjie(context.getString(R.string.term_border11));
        diacrisis10.setColor(context.getString(R.string.term_color11));
        diacrisis10.setDianranse(context.getString(R.string.term_Iodine_staining11));
        diacrisis10.setXueguna(context.getString(R.string.term_blood_vessel11));
        diacrisis10.setCusuan(context.getString(R.string.term_Acetic_acid11));
        diacrisis10.setHandle(context.getString(R.string.term_opinion11));
        diacrisis10.save();
        Diacrisis diacrisis11 = new Diacrisis();
        diacrisis11.setDiaId(12);
        diacrisis11.setCytology(context.getString(R.string.term_cytology12));
        diacrisis11.setDna(context.getString(R.string.term_HPV_DNA12));
        diacrisis11.setSymptom(context.getString(R.string.term_symptom12));
        diacrisis11.setAssessment(context.getString(R.string.term_Assessment12));
        diacrisis11.setRegion(context.getString(R.string.term_Lesion12));
        diacrisis11.setColposcopy(context.getString(R.string.term_colposcopic12));
        diacrisis11.setSuspected(context.getString(R.string.term_Suspected12));
        diacrisis11.setAttention(context.getString(R.string.term_attention12));
        diacrisis11.setBianjie(context.getString(R.string.term_border12));
        diacrisis11.setColor(context.getString(R.string.term_color12));
        diacrisis11.setDianranse(context.getString(R.string.term_Iodine_staining12));
        diacrisis11.setXueguna(context.getString(R.string.term_blood_vessel12));
        diacrisis11.setCusuan(context.getString(R.string.term_Acetic_acid12));
        diacrisis11.setHandle(context.getString(R.string.term_opinion12));
        diacrisis11.save();
        Diacrisis diacrisis12 = new Diacrisis();
        diacrisis12.setDiaId(13);
        diacrisis12.setCytology(context.getString(R.string.term_cytology13));
        diacrisis12.setDna(context.getString(R.string.term_HPV_DNA13));
        diacrisis12.setSymptom(context.getString(R.string.term_symptom13));
        diacrisis12.setAssessment(context.getString(R.string.term_Assessment13));
        diacrisis12.setRegion(context.getString(R.string.term_Lesion13));
        diacrisis12.setColposcopy(context.getString(R.string.term_colposcopic13));
        diacrisis12.setSuspected(context.getString(R.string.term_Suspected13));
        diacrisis12.setAttention(context.getString(R.string.term_attention13));
        diacrisis12.setBianjie(context.getString(R.string.term_border13));
        diacrisis12.setColor(context.getString(R.string.term_color13));
        diacrisis12.setDianranse(context.getString(R.string.term_Iodine_staining13));
        diacrisis12.setXueguna(context.getString(R.string.term_blood_vessel13));
        diacrisis12.setCusuan(context.getString(R.string.term_Acetic_acid13));
        diacrisis12.setHandle(context.getString(R.string.term_opinion13));
        diacrisis12.save();
        Diacrisis diacrisis13 = new Diacrisis();
        diacrisis13.setDiaId(14);
        diacrisis13.setCytology(context.getString(R.string.term_cytology14));
        diacrisis13.setDna(context.getString(R.string.term_HPV_DNA14));
        diacrisis13.setSymptom(context.getString(R.string.term_symptom14));
        diacrisis13.setAssessment(context.getString(R.string.term_Assessment14));
        diacrisis13.setRegion(context.getString(R.string.term_Lesion14));
        diacrisis13.setColposcopy(context.getString(R.string.term_colposcopic14));
        diacrisis13.setSuspected(context.getString(R.string.term_Suspected14));
        diacrisis13.setAttention(context.getString(R.string.term_attention14));
        diacrisis13.setBianjie(context.getString(R.string.term_border14));
        diacrisis13.setColor(context.getString(R.string.term_color14));
        diacrisis13.setDianranse(context.getString(R.string.term_Iodine_staining14));
        diacrisis13.setXueguna(context.getString(R.string.term_blood_vessel14));
        diacrisis13.setCusuan(context.getString(R.string.term_Acetic_acid14));
        diacrisis13.setHandle(context.getString(R.string.term_opinion14));
        diacrisis13.save();
        Diacrisis diacrisis14 = new Diacrisis();
        diacrisis14.setDiaId(15);
        diacrisis14.setCytology(context.getString(R.string.term_cytology15));
        diacrisis14.setDna(context.getString(R.string.term_HPV_DNA15));
        diacrisis14.setSymptom(context.getString(R.string.term_symptom15));
        diacrisis14.setAssessment(context.getString(R.string.term_Assessment15));
        diacrisis14.setRegion(context.getString(R.string.term_Lesion15));
        diacrisis14.setColposcopy(context.getString(R.string.term_colposcopic15));
        diacrisis14.setSuspected(context.getString(R.string.term_Suspected15));
        diacrisis14.setAttention(context.getString(R.string.term_attention15));
        diacrisis14.setBianjie(context.getString(R.string.term_border15));
        diacrisis14.setColor(context.getString(R.string.term_color15));
        diacrisis14.setDianranse(context.getString(R.string.term_Iodine_staining15));
        diacrisis14.setXueguna(context.getString(R.string.term_blood_vessel15));
        diacrisis14.setCusuan(context.getString(R.string.term_Acetic_acid15));
        diacrisis14.setHandle(context.getString(R.string.term_opinion15));
        diacrisis14.save();
        Diacrisis diacrisis15 = new Diacrisis();
        diacrisis15.setDiaId(16);
        diacrisis15.setCytology(context.getString(R.string.term_cytology16));
        diacrisis15.setDna(context.getString(R.string.term_HPV_DNA16));
        diacrisis15.setSymptom(context.getString(R.string.term_symptom16));
        diacrisis15.setAssessment(context.getString(R.string.term_Assessment16));
        diacrisis15.setRegion(context.getString(R.string.term_Lesion16));
        diacrisis15.setColposcopy(context.getString(R.string.term_colposcopic16));
        diacrisis15.setSuspected(context.getString(R.string.term_Suspected16));
        diacrisis15.setAttention(context.getString(R.string.term_attention16));
        diacrisis15.setBianjie(context.getString(R.string.term_border16));
        diacrisis15.setColor(context.getString(R.string.term_color16));
        diacrisis15.setDianranse(context.getString(R.string.term_Iodine_staining16));
        diacrisis15.setXueguna(context.getString(R.string.term_blood_vessel16));
        diacrisis15.setCusuan(context.getString(R.string.term_Acetic_acid16));
        diacrisis15.setHandle(context.getString(R.string.term_opinion16));
        diacrisis15.save();
        Diacrisis diacrisis16 = new Diacrisis();
        diacrisis16.setDiaId(17);
        diacrisis16.setCytology(context.getString(R.string.term_cytology17));
        diacrisis16.setDna(context.getString(R.string.term_HPV_DNA17));
        diacrisis16.setSymptom(context.getString(R.string.term_symptom17));
        diacrisis16.setAssessment(context.getString(R.string.term_Assessment17));
        diacrisis16.setRegion(context.getString(R.string.term_Lesion17));
        diacrisis16.setColposcopy(context.getString(R.string.term_colposcopic17));
        diacrisis16.setSuspected(context.getString(R.string.term_Suspected17));
        diacrisis16.setAttention(context.getString(R.string.term_attention17));
        diacrisis16.setBianjie(context.getString(R.string.term_border17));
        diacrisis16.setColor(context.getString(R.string.term_color17));
        diacrisis16.setDianranse(context.getString(R.string.term_Iodine_staining17));
        diacrisis16.setXueguna(context.getString(R.string.term_blood_vessel17));
        diacrisis16.setCusuan(context.getString(R.string.term_Acetic_acid17));
        diacrisis16.setHandle(context.getString(R.string.term_opinion17));
        diacrisis16.save();
    }

}
