package com.shizhenbao.util;

import android.support.v7.app.AlertDialog;

import com.shizhenbao.pop.User;

import java.io.File;
import java.util.List;

/**
 * Created by 用户 on 2017/4/10.
 */

public class OneItem {
    private int tab;
    private String id;//病人id
    private String name;//医生姓名
    private boolean b;//判断哪个页面跳转到诊断页面
    private boolean c;//判断诊断页面是否可以输入
    private String gather_path,Hospital_name,Printer_wifi_user,Printer_wifi_pwd,Szb_wifi_user,Szb_wifi_pwd,Hospital_keshi;//系统设置
    private long  Line_timer;//定时时长
    private List<String>list;//诊断时添加的图片的集合
    private static OneItem oneItem;
    private String jianjiPath;//剪辑的保存路径
    private int d;//判断是否选择图片 1：表示没有添加图片，2： 表示添加了图片
    private double x,y;
    private float screenSize;
    private String userId;
    private List<User> listUser;//获取从数据库中查到 的数据
    private String backUpPath;//本地备份路径设置
    private String backUpNetPath;//ftp备份路径设置
    private File file;//生成的pdf文件
    private String printResult;//打印结果
    private String adminName;//超级用户名称
    private int timing;//定时
    private AlertDialog dialog;//打印界面选择患者对话框
    private int intImage;//获取图像界面是否弹出对话框，0不，1弹出
    private int imageId;
    private boolean temp;//图片编辑时判断由哪个界面跳转过来，false为设置，true为查询
    private int selectFTP;//判断选择哪个服务器，0为默认，1为设置
    private OneItem(){
    }

    public int getSelectFTP() {
        return selectFTP;
    }

    public void setSelectFTP(int selectFTP) {
        this.selectFTP = selectFTP;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getIntImage() {
        return intImage;
    }

    public void setIntImage(int intImage) {
        this.intImage = intImage;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public int getTiming() {
        return timing;
    }

    public void setTiming(int timing) {
        this.timing = timing;
    }

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPrintResult(){
        return printResult;
    }
    public void setPrintResult(String printResult){
        this.printResult=printResult;
    }
    public File getFile(){
        return file;
    }
    public void setFile(File file){
        this.file=file;
    }
    public String getHospital_keshi(){
        return Hospital_keshi;
    }
    public void setHospital_keshi(String hospital_keshi){
        this.Hospital_keshi=hospital_keshi;
    }
    public  String getBackUpNetPath(){
        return backUpNetPath;
    }
    public void setBackUpNetPath(String backUpNetPath){
        this.backUpNetPath=backUpNetPath;
    }
    public String getBackUpPath(){
        return backUpPath;
    }
    public void setBackUpPath(String backUpPath){
        this.backUpPath=backUpPath;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public float getScreenSize(){
        return screenSize;
    }
    public void setScreenSize(float screenSize){
        this.screenSize=screenSize;
    }
    public double getX(){
        return x;
    }
    public void  setX(double x){
        this.x=x;
    }
    public double getY(){
        return y;
    }
    public void setY(double y){
        this.y=y;
    }
    public int getD(){
        return d;
    }
    public void setD(int d){
        this.d=d;
    }
    public String getGather_path(){//图片路径
        return gather_path;
    }
    public void setGather_path(String gather_path){
        this.gather_path=gather_path;
    }
    public String getJianjiPath(){//图片路径
        return jianjiPath;
    }
    public void setJianjiPath(String jianjiPath){
        this.jianjiPath=jianjiPath;
    }
    public String getHospital_name(){//医院信息
        return Hospital_name;
    }
    public void setHospital_name(String hospital_name){
        this.Hospital_name=hospital_name;
    }
    public String getPrinter_wifi_user(){//打印机wifi名称
        return Printer_wifi_user;
    }
    public void setPrinter_wifi_user(String printer_wifi_user){
        this.Printer_wifi_user=printer_wifi_user;
    }
    public String getPrinter_wifi_pwd(){//打印机wifi密码
        return Printer_wifi_pwd;
    }
    public void setPrinter_wifi_pwd(String printer_wifi_pwd){
        this.Printer_wifi_pwd=printer_wifi_pwd;
    }
    public String getSzb_wifi_user(){//视珍宝wifi名称
        return Szb_wifi_user;
    }
    public void setSzb_wifi_user(String szb_wifi_user){
        this.Szb_wifi_user=szb_wifi_user;
    }
    public String getSzb_wifi_pwd(){//视珍宝wifi密码
        return Szb_wifi_pwd;
    }
    public void setSzb_wifi_pwd(String szb_wifi_pwd){
        this.Szb_wifi_pwd=szb_wifi_pwd;
    }
    public long getLine_timer(){//定时器时长
        return Line_timer;
    }
    public void setLine_timer(long line_timer){
        this.Line_timer=line_timer;
    }
    public boolean getC(){
        return c;
    }
    public void setC(boolean c){
        this.c=c;
    }
    public List<String> getList(){
        return list;
    }
    public void setList(List<String> list){
        this.list=list;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public boolean getB(){
        return b;
    }
    public void setB(boolean b){
        this.b=b;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }

    public static OneItem getOneItem(){
        if(oneItem==null){
            synchronized (OneItem.class){
                if(oneItem==null){
                    oneItem=new OneItem();
                }
            }
        }
        return oneItem;
    }
}
