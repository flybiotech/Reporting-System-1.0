package com.shizhenbao.db;
import com.shizhenbao.pop.Admin;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.pop.User;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by fly on 2017/3/24.
 */

public class LoginRegister {

    //注册一个用户，相当于在数据库的表里面增加一条信息 //手动注册 一条信息（a 密码123）
    public int addDoctor(String name, String pass, String email) {
        boolean f = isRejisterName(name);
        if (f) {//f=true，表示该用户名应该被注册了，不能再注册了
            return 1;
        }
        return 2;
    }

    //查询数据库中有多少条医生的信息
    public int selectDoctor() {
        List<Doctor> list = LitePal.findAll(Doctor.class);
        if(list.size()==0){//如果为第一次注册，用户id默认为1
            return 1;
        }else {
            return (list.get(list.size()-1).getdId()+1);//不是第一次注册，用户id为数据库最后注册用户的id+1
        }
    }

    //登陆姓名和密码查询
    public boolean loginDoctor(String name, String pass) {
        List<Doctor> doctors = LitePal
                .where("dName=? and dPassword=? ", name, pass)
                .find(Doctor.class);
        if (doctors.size() != 0) {
            return true;
        }
        return false;
    }
    //登陆姓名和密码查询
    public boolean loginController(String name, String pass) {
        List<Admin> doctors = LitePal
                .where("controllerName=? and controllerPassword=? ", name, pass)
                .find(Admin.class);
        if (doctors.size() != 0) {
            return true;
        }
        return false;
    }
    //姓名查询  相同的用户名不能再注册
    public boolean isRejisterName(String name) {
        List<Doctor> doctors = LitePal.where("dName=? ", name).find(Doctor.class);
        if (doctors.size() != 0) {
            return true;
        }
        return false;
    }

    //查询病人信息

    public User getUserName(int id) {
        List<User> list = LitePal.where("pId=?", id + "").find(User.class);
        if (list.size() == 0) {
            return null;
        } else {
            User user = list.get(list.size() - 1);
            return user;
        }
    }
    public Doctor getDoctor(String name){
        List<Doctor>list= LitePal.where("dName=?",name+"").find(Doctor.class);
        if(list.size()==0){
            return null;
        }else {
            Doctor doctor=list.get(list.size()-1);
            return doctor;
        }
    }

    //获取医生的id
    public int getDoctorId(String name) {
        return getDoctor(name).getdId();
    }


    public SystemSet getSystem(){
        List<SystemSet>list= LitePal.findAll(SystemSet.class);
        if(list.size()==0){
            return null;
        }else {
            SystemSet system=list.get(0);
            return system;
        }
    }

    //获取医生登陆的次数
    public int getDoctorLoginCount(String name) {
        List<Doctor> doctors = LitePal.where("dName=? ", name).find(Doctor.class);
        if (doctors.size() > 0) {
            return doctors.get(0).getLoginCount();
        }
        return 1;
    }


}
