package com.shizhenbao.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ListView;

import com.model.DevModel;
import com.shizhenbao.pop.User;

import java.util.List;

/**
 * Created by fly on 2017/4/17.
 */

public class Const {

    public static final int typeWifi = 3;
    public static String HP_WIFI_SSID_KEY = "HP_Name";
    public static String HP_WIFI_PASS_KEY = "HP_Pass";
    public static String SZB_WIFI_SSID_KEY = "SZB_Name";
    public static String SZB_WIFI_PASS_KEY = "SZB_Pass";

    public static String HP_SSID_FILTER = "HP"; //WiFi过滤
    public static String SZB_SSID_FILTER = "FLY";
    public static String WIFI_TYPE_HP_PDF= "TYPE_HP_PDF";//设置连接wifi的类型
    public static String WIFI_TYPE_HP= "TYPE_HP";//设置连接wifi的类型
    public static String WIFI_TYPE_SZB = "TYPE_SZB";//设置连接wifi的类型
    public static int wifiConnectTime =1000*3; //3 秒

    public static boolean dialogWifiSetting = true;//判断是否弹出 对话框，这个对话框可以跳到wifi设置界面
    public static boolean isIntent = true;//判断是不是从其他三个fragment 跳到 FragGetImage的
    public static boolean isIntentFrag = false;//判断当前的fragment （主要是用在FragGetImage）的是否可见，
    public static String PageHp = "com.hp.android.printservice"; // 惠普打印机服务插件包名
    public static String PageMapria = "org.mopria.printplugin"; // org.mopria.printplugin
    public static boolean wifiMark = false; //wifi广播的标记，这个作用： true 表示，wifi连接成功时，可以回调相关的方法
    public static boolean wifiRepeat = false;//wifi当前的wifi是否是重复连接


    public static String saveImageFilePath = null; //图像获取界面中，选择的患者的图片保存的路径

    public static String LOGINCOUNT = "loginCount";//保存用户登录的次数，主要用于在图像获取界面 的wifi设置
    public static boolean FIRSTSHOW =false;//判断是否是第一次进入到 fragGetImage 页面，
    public static List<User> mListUser = null;
    //用在图片编辑，选择照片中的  删除的功能  ，主要是判断哪个界面显示出来了 1 :表示为编辑的图片页面 ,2 : 表示已编辑的图片界面
    public static int isVisible = 1;

    public static String nameJianJi = ""; //用于显示图片剪辑界面的用户的名称

    public static int threshold = 0;//阈值
    public static int thresholddiainyou = 0;//阈值
    public static int delayTime = 3;//设置自动跳转的时间

    public static boolean isSave = false;//已经保存，可以新增下一个患者

    public static String pdfPath = "";//用于保存报告打印界面中生成的pdf，然后给打印机 打印

    public static int usersize = 0;//未登记患者的数量
    public static List<User> list;//患者信息结合
    public static int dialogshow;//判断图片剪辑是否展示选择框，0显示，1不显示
    public static int imageshow;//判断是否进入图片管理界面，-1是，


    public static int offSetCount = 0; //数据库查询时的偏移量

    public static String sn;
    public static int  videoType =0; //0： 表示默认状态，1 :表示 从视珍宝界面里面进行播放的视频 2 ：表示从病例管理里面进行的播放
    public static int  imagepositionSingle =0; //病例管理中，图片展示的位置
    public static int  imageposition =0; //病例管理中，病历对比 中的对比图片的 位置
    public static int  imagepositioncompare =0; //病例管理中，对比 的图片展示的位置
    public static int sumnumber;//评估结果
    public static int surface=-1;//表面评分
    public static int color=-1;//颜色评分
    public static int vessel=-1;//血管评分
    public static int stain=-1;//碘染色评分

    public static int sumnumberM;//诊断修改评估结果

    //设置拍照录像的计时用的名称
    public static String SNAP_TOTALTIME="snapTotalTime";//定时 设置醋酸白状态下，连续拍照的总时间
    public static String SNAP_INTERVAL="snapInterval";// 设置醋酸白状态下，连续拍照的间隔时间
    public static String RECORD_TOTALTIME="recordTotalTime";// 设置录制视频的时间
    public static String SP_TOTALTIME_INDEX ="snap_total_index";// 记录选择的设置拍照的时长
    public static String SP_INTERVAL_INDEX ="snap_inteval_index";// 记录选择的拍照的间隔时间
    public static String SP_RECORD_INDEX ="snap_record_index";// 记录选择了第几个 摄像的时间


    public static String [] AllRoutePath = null;

    public static final String NetworkUrl = "http://www.baidu.com";//测试的url


    public static String zipFilePath = "";//下载的压缩文件的保存位置
    public static String unzipFilePath = "";//解压后文件保存位置

    public static String display = "display";

    public static String localPath = Environment.getExternalStorageDirectory()+"/FALUYUAN/";

//    public static String nodisplay = "nodisplay";
    public static String backupPath = Environment.getExternalStorageDirectory()+"/";
    public static String smallestWidthDP = "WidthDP";//获得当前平板的最小宽度

    public static Context context;
}
