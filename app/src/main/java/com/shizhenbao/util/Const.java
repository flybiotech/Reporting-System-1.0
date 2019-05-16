package com.shizhenbao.util;

import android.graphics.Bitmap;

import com.shizhenbao.pop.User;

import java.util.List;

/**
 * Created by fly on 2017/4/17.
 */

public class Const {
    /**
     * 判断蓝牙是从患者信息界面自动连接的还是从系统中心手动连接的
     * true是表示从患者信息界面自动连接的
     */
//    public static boolean startModeBlue = true;
//    public static boolean BlueConnectState = false;//表示蓝牙是否已经连上了
//    public static String nameWifi = "";
//    public static String passWifi = "";
    public static final int typeWifi = 3;
    public static String nameHP = "DIRECT-A5-HP DeskJet 3630 series";
    public static String nameShiZhenBao = "IPCAM_AP_80003016";
    public static String passHP = "12345678";
    public static String passShiZhenBao = "12345678";
//    public static String HPpackageName = "com.hp.android.print";
//    public static String packageNameTowSZB = "com.qcam"; //第二代视珍宝的软件的包名

    public static boolean dialogWifiSetting = true;//判断是否弹出 对话框，这个对话框可以跳到wifi设置界面
    public static boolean isIntent = true;//判断是不是从其他三个fragment 跳到 FragGetImage的
    public static boolean isIntentFrag = false;//判断当前的fragment （主要是用在FragGetImage）的是否可见，
    public static String PageHp = "org.mopria.printplugin"; // org.mopria.printplugin

    public static boolean wifiMark = false; //wifi广播的标记，这个作用： true 表示，wifi连接成功时，可以回调相关的方法
    public static boolean wifiRepeat = false;//wifi当前的wifi是否是重复连接

    public static String saveImageFilePath = null; //图像获取界面中，选择的患者的图片保存的路径

    public static int loginCount = 1;//保存用户登录的次数，主要用于在图像获取界面 的wifi设置
    public static List<User> mListUser = null;

    public static boolean printOptionEnable = false;//用于判断拍照时 是否将照片保存到本地
//    public static boolean btnOptionEnable = false;//用于判断拍照时 ,显示对话框，是否是正在保存图片

    public static Bitmap inBitmap = null; //用于拍照时，保存拍照时的bitmap，用于选择是碘油还是醋酸白还是原图
    //用在图片编辑，选择照片中的  删除的功能  ，主要是判断哪个界面显示出来了 1 :表示为编辑的图片页面 ,2 : 表示已编辑的图片界面
    public static int isVisible = 1;

    public static String nameJianJi = ""; //用于显示图片剪辑界面的用户的名称

    public static int threshold = 0;//阈值
    public static int thresholddiainyou = 0;//阈值
    public static int autoSnapshotCount = 0;//自动拍照的次数
    public static int delayTime = 3;//设置自动跳转的时间
    /**
     * true 表示已登记
     */
//    public static boolean isDengJi = false;//判断是否已经登记
    public static boolean isSave = true;//已经保存，可以新增下一个患者

    public static String pdfPath = "";//用于保存报告打印界面中生成的pdf，然后给打印机 打印

    //    public static ListView userListView;//展示未登记的患者信息
    public static int usersize = 0;//未登记患者的数量
    public static List<User> list;//患者信息结合
    public static int dialogshow;//判断图片剪辑是否展示选择框，0显示，1不显示
    public static int imageshow;//判断是否进入图片管理界面，-1是，

//    public static DevModel devModel = null;//视珍宝主机的设备信息

    public static int offSetCount = 0; //数据库查询时的偏移量
    //这里我们又连缀了一个limit()方法，这个方法接收一个整型参数，用于指定查询前几条数据，这里指定成30，意思就是查询所有匹配结果中的前30条数据。
    public static int limitCount = 30;//数据库查询时 每次查出的数量
    //    public static boolean isItem = false;//判断listview的item是否可以点击
    public static int cancleIndex = 0;
    public static List<String>pdfview_list;//报告预览数据源
    public static List<String>pdfmodify_list;//报告修改列表
    public static List<Item>patient_list;//患者信息数据源
    public static String sn;

}
