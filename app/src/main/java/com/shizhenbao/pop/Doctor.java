package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by fly on 2017/3/23.
 */
//测试账号： a 密码123
public class Doctor extends DataSupport {
    private int dId;
    private String dName;
    private String dPassword;
    private String dEmail;
    private boolean dAdmin;//是不是超级管理员
    private int loginCount;//判断用户登录的次数
    private int dTemp;//判断是否选择记住密码，0为否，1为是

    public int getdTemp() {
        return dTemp;
    }

    public void setdTemp(int dTemp) {
        this.dTemp = dTemp;
    }

    private String edit_hos_name,edit_hos_keshi;//医院名称，医院科室

    public String getEdit_hos_name() {
        return edit_hos_name;
    }

    public void setEdit_hos_name(String edit_hos_name) {
        this.edit_hos_name = edit_hos_name;
    }

    public String getEdit_hos_keshi() {
        return edit_hos_keshi;
    }

    public void setEdit_hos_keshi(String edit_hos_keshi) {
        this.edit_hos_keshi = edit_hos_keshi;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getdId() {
        return dId;
    }

    public void setdId(int dId) {
        this.dId = dId;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdPassword() {
        return dPassword;
    }

    public void setdPassword(String dPassword) {
        this.dPassword = dPassword;
    }

    public String getdEmail() {
        return dEmail;
    }

    public void setdEmail(String dEmail) {
        this.dEmail = dEmail;
    }

    public boolean isdAdmin() {
        return dAdmin;
    }

    public void setdAdmin(boolean dAdmin) {
        this.dAdmin = dAdmin;
    }
}
