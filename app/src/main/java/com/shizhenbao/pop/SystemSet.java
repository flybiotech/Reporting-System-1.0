package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangbin on 2017/4/28.
 */

public class SystemSet extends DataSupport {
    private int id;
    private String gather_path;//采集图片保存路径
    private String Hospital_name;//医院信息
    private long Line_timer;//定时时长；
    private String Printer_wifi_user;//惠普打印机wifi名称
    private String Printer_wifi_pwd;//惠普打印机wifi密码
    private String Szb_wifi_user;//视珍宝wifi名称
    private String Szb_wifi_pwd;//视珍宝wifi密码
    private String backUpPath;//本地备份路径设置
    private String backUpNetPath;//ftp备份路径设置
    private String bluetoothName; //连接的蓝牙的名称
    private String bluetoothAddress;//连接的蓝牙的地址
    private String local_svn;//本机svn号
    private int threshold; //原图的阈值
    private int thresholddianyou;//碘油的阈值
    private int dialog_time;//设置对话框消失时长

   	/**
     * 服务器名.
     */
    private String hostName;

    /**
     * 端口号
     */
    private int serverPort;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    public int getDialog_time() {
        return dialog_time;
    }

    public void setDialog_time(int dialog_time) {
        this.dialog_time = dialog_time;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThresholddianyou() {
        return thresholddianyou;
    }

    public void setThresholddianyou(int thresholddianyou) {
        this.thresholddianyou = thresholddianyou;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocal_svn() {
        return local_svn;
    }

    public void setLocal_svn(String local_svn) {
        this.local_svn = local_svn;
    }

    public String getBackUpPath() {
        return backUpPath;
    }

    public void setBackUpPath(String backUpPath) {
        this.backUpPath = backUpPath;
    }

    public String getBackUpNetPath() {
        return backUpNetPath;
    }

    public void setBackUpNetPath(String backUpNetPath) {
        this.backUpNetPath = backUpNetPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGather_path() {
        return gather_path;
    }

    public void setGather_path(String gather_path) {
        this.gather_path = gather_path;
    }

    public String getHospital_name() {
        return Hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        Hospital_name = hospital_name;
    }

    public long getLine_timer() {
        return Line_timer;
    }

    public void setLine_timer(long line_timer) {
        Line_timer = line_timer;
    }

    public String getPrinter_wifi_user() {
        return Printer_wifi_user;
    }

    public void setPrinter_wifi_user(String printer_wifi_user) {
        Printer_wifi_user = printer_wifi_user;
    }

    public String getPrinter_wifi_pwd() {
        return Printer_wifi_pwd;
    }

    public void setPrinter_wifi_pwd(String printer_wifi_pwd) {
        Printer_wifi_pwd = printer_wifi_pwd;
    }

    public String getSzb_wifi_user() {
        return Szb_wifi_user;
    }

    public void setSzb_wifi_user(String szb_wifi_user) {
        Szb_wifi_user = szb_wifi_user;
    }

    public String getSzb_wifi_pwd() {
        return Szb_wifi_pwd;
    }

    public void setSzb_wifi_pwd(String szb_wifi_pwd) {
        Szb_wifi_pwd = szb_wifi_pwd;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }
}
