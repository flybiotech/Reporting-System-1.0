//-----------------------------------------------------------------------------
// Author      : 朱红波
// Date        : 2012.01.18
// Version     : V 1.00
// Description :
//-----------------------------------------------------------------------------
#ifndef th_protocol_H
#define th_protocol_H 

#include "cm_types.h"

#pragma pack(4)//n=1,2,4,8,16

#define Port_Ax_Data           2000
#define Port_Ax_http             80
#define Port_Ax_RTSP            554
#define Port_AlarmServer       9001

#define Port_Ax_Multicast      2000
#define Port_Ax_Search_Local   1999
#define Port_onvifSearch       3702
#define IP_Ax_Multicast  "239.255.255.250"//uPnP IP查询 多播对讲IP
#define REC_FILE_EXT           "av"
#define REC_FILE_EXT_DOT      ".av"

//#pragma option push -b //start C++Builder enum 4 u8
//#pragma option push -b- //start C++Builder enum 1 u8

typedef enum TDevType {// sizeof 4
  dt_None=0,
  dt_DevX1=11,
  dt_DevOther
}TDevType;

/*网络包*/
//-----------------------------------------------------------------------------
typedef struct TSMTPCfgPkt {//sizeof 500 u8邮件配置包
  i32 Active;
  char40 SMTPServer;
  i32 SMTPPort;
  char40 FromAddress;
  char ToAddress[320];
  char40 Account;
  char40 Password;
  i32 SSL;
  i32 Flag;
  i32 Flag1;
}TSMTPCfgPkt;
//-----------------------------------------------------------------------------
typedef struct TFTPCfgPkt {//sizeof 232 u8 FTP配置包
  i32 Active;
  char100 FTPServer;
  i32 FTPPort;
  char40 Account;
  char40 Password;
  char40 UploadPath;
  //  i32 PASVMode;
  //  i32 ProxyType;// 0=off 1=http 2=socks
  //  char40 ProxyUserName;
  //  char40 ProxyPassword;
  i32 Flag;
}TFTPCfgPkt;

typedef struct Tp2pCfgPkt {//sizeof 88 u8 P2P配置包
  char40 UID;  
  union {
    struct {
      u32 SvrIP[4];//20130621
      i32 Reserved1;
    };
    char20 oldAccount;
  };

  char20 Password;
  bool Active;
  u8 StreamType;//0 主码流 1 次码流
  bool IsNew; //20130621
  u8 p2pType;   //tutk=0 self=1
  u8 Flag[4];
}Tp2pCfgPkt;

// /usr/sbin/wput ftp://administrator:123456@192.168.1.20:21/ -b -B --proxy=off   --proxy-user=user --proxy-pass=pass  /bin/cp -o cp
// /usr/sbin/wput ftp://administrator:123456@192.168.1.20:21/ -b -B --proxy=http  --proxy-user=user --proxy-pass=pass  /bin/cp -o cp
// /usr/sbin/wput ftp://administrator:123456@192.168.1.20:21/ -b -B --proxy=socks --proxy-user=user --proxy-pass=pass  /bin/cp -o cp
//-----------------------------------------------------------------------------
typedef enum TGroupType{                          //sizeof 4 u8
  pt_Cmd,
  pt_PlayLive,
  pt_PlayHistory,
  pt_PlayMedia
}TGroupType;
//-----------------------------------------------------------------------------
typedef enum TFontColor {                        //OSD 字体颜色 sizeof 4 u8
  cl_Black       =0x00,
  cl_Maroon      =0x01,
  cl_Green       =0x02,
  cl_Olive       =0x03,
  cl_Navy        =0x04,
  cl_Purple      =0x05,
  cl_Teal        =0x06,
  cl_Red         =0x07,
  cl_Lime        =0x08,
  cl_Yellow      =0x09,
  cl_Blue        =0x0a,
  cl_Fuchsia     =0x0b,
  cl_Aqua        =0X0c,
  cl_Gray        =0x0d,
  cl_Silver      =0x0e,
  cl_White       =0x0f,
  cl_Transparent =0xff
}TFontColor;

static i32 FontColors[16+1] = 
{
  0x000000,//cl_Black=0,//黑
  0x000080,//cl_Maroon=1,//暗红
  0x008000,//cl_Green=2,//深绿
  0x008080,//cl_Olive=3,//土黄
  0x800000,//cl_Navy=4,//深蓝
  0x800080,//cl_Purple=5,//紫
  0x808000,//cl_Teal=6,//深青
  0x0000FF,//cl_Red=7,//红
  0x00FF00,//cl_Lime=8,//绿
  0x00FFFF,//cl_Yellow=9,//黄
  0xFF0000,//cl_Blue=10,//蓝
  0xFF00FF,//cl_Fuchsia=11,//洋红
  0xFFFF00,//cl_Aqua=12,//青
  0x808080,//cl_Gray=13,//深灰
  0xC0C0C0,//cl_Silver=14,//灰
  0xFFFFFF,//cl_White=15,//白
  0xFFFFFF//cl_Transparent=255//透明
};
//-----------------------------------------------------------------------------
typedef enum TResolution {
  D1    = 0,
  HFD1  = 1,
  CIF   = 2,
  QCIF  = 3
}TResolution;
//-----------------------------------------------------------------------------
typedef enum TStandardEx {
  StandardExMin,
  P720x576,//1
  P720x288,
  P704x576,
  P704x288,
  P352x288,
  P176x144,
  N720x480,
  N720x240,
  N704x480,
  N704x240,
  N352x240,
  N176x120,//11

  V160x120,//  QQVGA
  V320x240,//   QVGA
  V640x480,//    VGA
  V800x600,//   SVGA
  V1024x768,//   XGA  //17
  V1280x720,
  V1280x800,//  WXGA
  //  V1280x854,//  WXGA+
  V1280x960,//  _VGA
  V1280x1024,// SXGA
  V1360x768,// WXSGA+ //22
  V1400x1050,// SXGA+
  V1600x1200,// UXGA
  V1920x1080,//1080P
  //V1680x1050,//WSXGA+
  //V2048x1536,// QXGA   //27
  //V2560x1600,//QSXGAW
  //V2560x2048,//QSXGA
  //V3400x2400,//QUXGAW
  StandardExMax
}TStandardEx;
//-----------------------------------------------------------------------------
typedef enum TVideoType {                        //视频格式sizeof 4 u8
  MPEG4          =0,
  MJPEG          =1,
  H264           =2,
}TVideoType;
//-----------------------------------------------------------------------------
typedef enum TImgFormat {
  if_RGB           =0,
  if_YUV420        =1,
  if_YUV422        =2,
}TImgFormat;
//-----------------------------------------------------------------------------
typedef enum TStandard {
  NTSC           =0,
  PAL            =1,
  VGA            =2,
}TStandard;
//-----------------------------------------------------------------------------
typedef struct TBatchCfgPkt { //批量修改配置 sizeof 16
  i32 BitRate;                                   //码流 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  u8 Standard;                                 //TStandard制式 PAL=1, NTSC=0
  u8 Resolution;                               //TResolution
  u8 FrameRate;                                //帧率 1-30 MAX:PAL 25 NTSC 30
  u8 IPInterval;                               //IP帧间隔 1-120 default 30
  i32 AudioActive;
  i32 DevTime;
}TBatchCfgPkt;
//-----------------------------------------------------------------------------
#define CBR    0
#define VBR    1
typedef struct TVideoFormat {                    //视频格式 sizeof 128
  i32  Standard;                                 //制式 PAL=1, NTSC=0 default=0xff
  i32  Width;                                    //宽 720 360 180 704 352 176 640 320 160
  i32  Height;                                   //高 480 240 120 576 288 144 
  TVideoType VideoType;                          //MPEG4=0x00, MJPEG=0x01  H264=0x02
  u8  Brightness;                               //亮度   0-255
  u8  Contrast;                                 //对比度 0-255
  u8  Hue;                                      //色度   0-255
  u8  Saturation;                               //饱和度 0-255
  i32  FrameRate;                                //帧率 1-30 MAX:PAL 25 NTSC 30
  i32  IPInterval;                               //IP帧间隔 1-120 default 30
  u8 BitRateType;                              //0定码流CBR 1定画质VBR
  u8 BitRateQuant;                             //画质  0..4
  u8  BrightnessNight;                          //亮度   0-255
  u8  ContrastNight;                            //对比度 0-255


  i32  BitRate;                                  //码流 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  i32  IsMirror;                                 //水平翻转 false or true
  i32  IsFlip;                                   //垂直翻转 false or true
  char40 Title;                                  //OSD标题 20个汉字

  u8 HueNight;
  bool ShowTitleInDev;
  bool IsShowTitle;                            //显示时间标题 false or true
  u8 TitleColor;
  i16 TitleX;
  i16 TitleY;

  u8 SaturationNight;
  bool ShowTimeInDev;
  bool IsShowTime;                               //显示水印 false or true
  u8 TimeColor;
  i16 TimeX;
  i16 TimeY;

  u8 DeInterlaceType;
  bool IsDeInterlace;
  bool IsDeInterlaceSub;//add at 2010/12/01
  bool IsWDR;
  u8  Sharpness;                               //锐度 0-255
  u8 IRCutSensitive;
  u8 Reserved[2];

  u8 SharpnessNight;
  bool ShowFrameRateInDev;
  bool IsShowFrameRate;
  u8 FrameRateColor;
  i16 FrameRateX;
  i16 FrameRateY;

  struct { //add at 2009/09/02
    u8 BitRateQuant;//画质  0..4
    u8 StandardEx;//TStandardEx
    u8 FrameRate;//帧率 1-30 MAX:PAL 25 NTSC 30
    u8 BitRateType;//0定码流 1定画质
    i32 BitRate;//码流 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  }Sub;//子码流

  i32 Flag;
}TVideoFormat;
//-----------------------------------------------------------------------------
typedef struct TVideoCfgPkt {                    //视频设置包 sizeof 148 u8
  i32  Chl;                                      //通道 0..15 对应 1..16通道
  i32  Active;                                   //是否启动(可能暂时没有用到)
  u8 InputType;                                //输入类型
  u8 Reserved[3];
  struct TVideoFormat VideoFormat;               //视频格式
  i32 Flag;                                      //立即生效=1
  i32 Flag1;
}TVideoCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TAudioType {                        //音频格式sizeof 4 u8
  PCM                   =0x0001,
  G711                  =0x0002,
  MPEGLAYER2            =0x0050,
  MPEGLAYER3            =0x0055,
}TAudioType;
//-----------------------------------------------------------------------------
typedef struct TAudioFormatEx {                    //音频格式 = sizeof 8 暂未用到
  u8 AudioType;                              //PCM=0x01, G711 = 0x02
  u8 nChannels;                               //单声道=0 立体声=1
  u16 wBitsPerSample;                          //number of bits per sample of mono data 
  u32 nSamplesPerSec;                          //采样率 
}TAudioFormatEx;
//-----------------------------------------------------------------------------
typedef struct TAudioFormat {                    //音频格式 = TWaveFormatEx = sizeof 32
  u32 wFormatTag;                              //PCM=0X0001, ADPCM=0x0011, MP2=0x0050, MP3=0X0055, GSM610=0x0031
  u32 nChannels;                               //单声道=0 立体声=1
  u32 nSamplesPerSec;                          //采样率 
  u32 nAvgu8sPerSec;                         //for buffer estimation 
  u32 nBlockAlign;                             //block size of data 
  u32 wBitsPerSample;                          //number of bits per sample of mono data 
  u32 cbSize;                                  //本包大小
  i32 Flag;                                      
}TAudioFormat;
//-----------------------------------------------------------------------------
typedef struct TAudioCfgPkt {                    //音频设置包 sizeof 48 u8
  i32  Chl;                                      //通道0..15 对应 1..16通道
  i32  Active;                                   //是否启动声音
  struct TAudioFormat AudioFormat;               //音频格式
#define MIC_IN  0
#define LINE_IN 1
  u8 InputType;                                 //0 MIC输入, 1 LINE输入
  u8 VolumeMicIn;//不用了
  u8 VolumeLineIn;
  u8 VolumeLineOut;

  bool SoundTriggerActive;
  u8 SoundTriggerSensitive;
  bool IsAudioGain;
  u8 Reserved;
}TAudioCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TPlayCtrl {                         //播放控制sizeof 4 u8
  PS_None               =0,                 //空
  PS_Play               =1,                 //播放
  PS_Pause              =2,                 //暂停
  PS_Stop               =3,                 //停止
  PS_FastBackward       =4,                 //快退
  PS_FastForward        =5,                 //快进
  PS_StepBackward       =6,                 //步退
  PS_StepForward        =7,                 //步进
  PS_DragPos            =8,                 //拖动
}TPlayCtrl; 
//-----------------------------------------------------------------------------
typedef struct TPlayCtrlPkt {
  TPlayCtrl PlayCtrl;
  u32 Speed;//如果PlayCtrl=PS_StepBackward, PS_FastForward ，则保存快进快退倍率 1 2 4 8 16 32倍率
  u32 Pos;//如果PlayCtrl=PS_DragPos，则保存文件文件位置Pos
}TPlayCtrlPkt;
//-----------------------------------------------------------------------------
typedef struct TRecFilePkt {                     //播放历史包 SizeOf 100
  char20 DevIP;
  u8 Chl;
  u8 RecType;                               //0:普通录影 1:警报录影 2媒体文件
  u8 Reserved[2];
  i32 StartTime;                              //开始时间
  i32 EndTime;                                //结束时间
  char64 FileName;                               //文件名
  i32 Flag;                                      //保留
}TRecFilePkt;
//-----------------------------------------------------------------------------
//  /sd/rec/20091120/20091120_092749_0.ra2
typedef struct TRecFileIdx {                     //录影文件索引包 sizeof 80
  char64 FileName;
  u8 Chl;
  u8 RecType;
  u8 Reserved;
  u8 Flag;
  time_t StartTime;
  time_t EndTime;
  u32 FileSize;
}TRecFileIdx;

#define RECFILELSTCOUNT 16
typedef struct TRecFileLst { //sizeof 1288
  i32 Total;
  i32 SubTotal;
  TRecFileIdx Lst[RECFILELSTCOUNT];
}TRecFileLst;
//-----------------------------------------------------------------------------
typedef struct TRecFileHead {                    //录影文件头格式 sizeof 256 u8
  u32 DevType;                                 //设备类型 = DEV_Ax
  u32 FileSize;                                //文件大小
  i32 StartTime;                              //开始时间
  i32 EndTime;                                //停止时间
  char20 DevName;                                //设备ID
  char20 DevIP;                                  //设备IP
  u32 VideoChannel;                            //视频通道数统计
  u32 AudioChannel;                            //音频通道数统计
  struct TVideoFormat VideoFormat;               //视频格式
  struct TAudioFormat AudioFormat;               //音频格式
  i32 Flag;                                      //保留
  i32 Flag1;                                      //保留
  i32 Flag2;                                      //保留
  i32 Flag3;                                      //保留
  i32 Flag4;                                      //保留
  i32 Flag5;                                      //保留
  i32 Flag6;                                      //保留
  i32 Flag7;                                      //保留
}TRecFileHead;
//-----------------------------------------------------------------------------
typedef struct TFilePkt {                        //上传文件包 sizeof 528+4
#define FILETYPE_BURNIN_UBOOT 7 //x1 burn in
#define FILETYPE_BIN          2 //x1 x2 bin 升级用
#define FILETYPE_X4ISP        3
  i32 FileType;
  u32 FileSize;
  char256 FileName;
  i32 Handle;

#define UPGRAD_UPLOAD_FAIL      0
#define UPGRAD_UPLOAD_OVER_ING  1
#define UPGRAD_UPLOAD_OVER_OK   2
#define UPGRAD_UPLOAD_FLASHING  3
#define UPGRAD_UPLOAD_ING       4
  i32 Flag;//0=上传文件失败 1=上传文件完毕,正在升级,请不要断电  2=上传文件成功 (3=正在上传文件 x5add)
  char256 DstFile;
  u32 crc;//2012-02-10
}TFilePkt;
//-----------------------------------------------------------------------------
typedef enum TAlmType {
  Alm_None             =0,//空
  Alm_MotionDetection  =1,//位移报警Motion Detection
  Alm_DigitalInput     =2,//DI报警
  Alm_SoundTrigger       =3,////声音触发报警
  Net_Disconn          =4,//网络断线
  Net_ReConn           =5,//网络重连
  Alm_HddFill          =6,//磁满
  Alm_VideoBlind       =7,//视频遮挡
  Alm_VideoLost        =8,//视频丢失
  Alm_Other3           =9,//其它报警3
  Alm_Other4           =10,//其它报警4
  Alm_RF               =11,
  Alm_OtherMax         =12,
}TAlmType;

typedef struct TAlmSendPkt {                     //警报上传包sizeof 36
  TAlmType AlmType;                              //警报类型
  i32 AlmTime;                                   //警报时间
  i32 AlmPort;                                   //警报端口
  char20 DevIP;
  i32 Flag;                                      //MD 区域索引
}TAlmSendPkt;
//-----------------------------------------------------------------------------
typedef struct TDoControlPkt {                 //do控制包　sizeof 16
  i32 Chl;
  i32 Value;                                   // 0 关　1 开
  i32 Reserved;
}TDoControlPkt;
//-----------------------------------------------------------------------------
typedef enum TTaskDayType{w_close,w_1,w_2,w_3,w_4,w_5,w_6,w_7,w_1_5,w_1_6,w_6_7,w_1_7,w_Interval} TTaskDayType;
typedef struct TTaskhm {
  u8 w;
  u8 Days;
  u8 Reserved[2];
  u8 start_h;//时 0-23
  u8 start_m;//分 0-59
  u8 stop_h;//时 0-23
  u8 stop_m;//分 0-59
}TTaskhm;
//-----------------------------------------------------------------------------
typedef struct THideAreaCfgPkt {                 //隐藏录影区域包 sizeof 72
  i32 Reserved0;
  i32 Reserved1;
  i32 Active;                                    //false or true
  TRect Rect;

  i32 IsFloatNewRect;
  struct {
    float left,top,right,bottom;
  }NewRect;

  char20 Reserved2;
  i32 Flag;
}THideAreaCfgPkt;
//-----------------------------------------------------------------------------
typedef struct TMDCfgPkt {                       //移动侦测包 sizeof 96
  u8 Reserved1[8];
  bool Active;
  u8 Reserved2;
  u8 Sensitive;                              //侦测灵敏度 0-255
  bool IsFloatNewRect;
  TRect Rect;                        //侦测区域 sizeof 8
  char40 Reserved3;
  struct TTaskhm hm;

  struct {
    float left, top, right, bottom;
  }NewRect;

  i32 SettingStandard;//设置时的分辨率 20130403
}TMDCfgPkt;
//-----------------------------------------------------------------------------
typedef struct TAlmCfgItem {
  u8 AlmType;//u8(TAlmType)
  u8 Channel;
  u8 Active;//only di
  bool IsAlmRec;
  bool IsFTPUpload;//NetSend
  bool ActiveDO;//DI关联DO通道 false close
  bool IsSendEmail;//u8 DOChannel;
  u8 Reserved2;//
}TAlmCfgItem;

typedef struct TAlmCfgPkt {                   //警报配置包 sizeof 268 -> 52 20140928
  i32 AlmOutTimeLen;                    //报警输出时长(秒) 0 ..600 s
  i32 AutoClearAlarm;
  i32 Flag;
  TAlmCfgItem DIAlm;
  TAlmCfgItem MDAlm;
  TAlmCfgItem SoundAlm;
  TAlmCfgItem Reserved[2];
}TAlmCfgPkt;
//-----------------------------------------------------------------------------
#define USER_GUEST     1 
#define USER_OPERATOR  2
#define USER_ADMIN     3
#define GROUP_GUEST    1
#define GROUP_OPERATOR 2
#define GROUP_ADMIN    3

#define MAXUSERCOUNT             20              //最大用户数量
typedef struct TUserCfgPkt {                     //sizeof 1048
  i32 Count;
  struct {
    i32 UserGroup;                                 //Guest=1 Operator=2 Administrator=3
    i32 Authority;                                 //3为admin ,
    char20 UserName;                               //用户名 admin不能更改
    char20 Password;                               //密码
    i32 Flag;
  }Lst[MAXUSERCOUNT];
  i32 Flag;
}TUserCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TPTZCmd {                           //sizeof 4 u8
  PTZ_None,
  PTZ_Up,//上
  PTZ_Up_Stop,//上停止
  PTZ_Down,//下
  PTZ_Down_Stop,//下停止
  PTZ_Left,//左
  PTZ_Left_Stop,//左停止
  PTZ_Right,//右
  PTZ_Right_Stop,//右停止

  PTZ_LeftUp,//左上
  PTZ_LeftUp_Stop,//左上停止
  PTZ_RightUp,//右上
  PTZ_RightUp_Stop,//右上停止
  PTZ_LeftDown,//左下
  PTZ_LeftDown_Stop,//左下停止
  PTZ_RightDown,//右下
  PTZ_RightDown_Stop,//右下停止

  PTZ_IrisIn,//光圈小
  PTZ_IrisInStop,//光圈停止
  PTZ_IrisOut,//光圈大
  PTZ_IrisOutStop,//光圈停止

  PTZ_ZoomIn,//倍率小
  PTZ_ZoomInStop,//倍率停止
  PTZ_ZoomOut,//倍率大
  PTZ_ZoomOutStop,//倍率停止

  PTZ_FocusIn,//焦距小
  PTZ_FocusInStop,//焦距停止
  PTZ_FocusOut,//焦距大
  PTZ_FocusOutStop,//焦距停止

  PTZ_LightOn,//灯光小
  PTZ_LightOff,//灯光大
  PTZ_RainBrushOn,//雨刷开
  PTZ_RainBrushOff,//雨刷开
  PTZ_AutoOn,//自动开始  //Rotation
  PTZ_AutoOff,//自动停止

  PTZ_TrackOn,
  PTZ_TrackOff,
  PTZ_IOOn,
  PTZ_IOOff,

  PTZ_ClearPoint,//云台复位
  PTZ_SetPoint,//设定云台定位
  PTZ_GotoPoint,//云台定位
  PTZ_SetPointRotation,
  PTZ_SetPoint_Left,
  PTZ_GotoPoint_Left,
  PTZ_SetPoint_Right,
  PTZ_GotoPoint_Right,
  PTZ_DayNightMode,//白天、夜光模式 0白天 1夜光
  PTZ_Max
}TPTZCmd;
//-----------------------------------------------------------------------------
typedef enum TPTZProtocol {                      //云台协议 sizeof 4
  Pelco_P               =0,
  Pelco_D               =1,
  Protocol_Custom       =2,
}TPTZProtocol;

typedef struct TPTZPkt {                         //PTZ 云台控制  sizeof 108
  TPTZCmd PTZCmd;                                    //=PTZ_None 为透明传输
  union {
    struct {
      TPTZProtocol Protocol;                         //云台协议
      i32 Address;                                   //云台地址
      i32 PanSpeed;                                  //云台速度
      i32 Value;                                     //保留或预设位
      i32 Flag;
      i32 sleepms;//20140722 x3 ptz add
    };
    struct {
      char100 TransBuf;
      i32 TransBufLen;
    };
  };
}TPTZPkt;
//-----------------------------------------------------------------------------
typedef struct TPlayLivePkt {                    //播放现场包//sizeof 20
  u32 VideoChlMask;//通道掩码 
  //  31 .. 19 18 17 16   15 .. 03 02 01 00
  //         0  0  0  0          0  0  0  1     
  u32 AudioChlMask;
  //  31 .. 19 18 17 16   15 .. 03 02 01 00
  //         0  0  0  0          0  0  0  1
  i32 Value;                                     //Value=0发送所有帧，Value=1只发送视频I帧
  //begin add at 2009/09/02
  u32 SubVideoChlMask;
  //11  i32 IsRecvAlarm;                               //0接收设备警报 1不接收设备警报
  //end add
  i32 Flag;                                      //保留 
}TPlayLivePkt;
//-----------------------------------------------------------------------------
typedef struct TPlayBackPkt {                    //sizeof 20
  i32 Chl;
  i32 FileType;                                  //0:普通录影 1:警报录影 2媒体文件
  i32 StartTime;                                 //开始时间
  i32 EndTime;                                   //结束时间
  i32 Flag;
}TPlayBackPkt;
//-----------------------------------------------------------------------------
typedef enum TMsgID {
  Msg_None,
  Msg_Login,//用户登录
  Msg_PlayLive,//开始播放现场
  Msg_StartPlayRecFile,//播放录影文件
  Msg_StopPlayRecFile,//停止播放录影文件
  Msg_GetRecFileLst,//取得录影文件列表
  Msg_GetDevRecFileHead,//取得设备文件文件头信息
  Msg_StartUploadFile,//开始上传文件
  Msg_AbortUploadFile,//取消上传文件
  Msg_StartUploadFileEx,//开始上传文件tftp

  Msg_StartTalk,//开始对讲
  Msg_StopTalk,//停止对讲
  Msg_PlayControl,//播放控制
  Msg_PTZControl,//云台控制
  Msg_Alarm,//警报
  Msg_ClearAlarm,//关闭警报
  Msg_GetTime,//取得时间
  Msg_SetTime,//设置时间
  Msg_SetDevReboot,//重启设备
  Msg_SetDevLoadDefault,//系统回到缺省配置 Pkt.Value= 0 不恢复IP, Pkt.Value= 1 恢复IP

  Msg_DevSnapShot,//设备拍照
  Msg_DevStartRec,//设备开始录像
  Msg_DevStopRec,//设备停止录象

  Msg_GetColors,//取得亮度、对比度、色度、饱和度
  Msg_SetColors,//设置亮度、对比度、色度、饱和度
  Msg_SetColorDefault,

  Msg_GetMulticastInfo,
  Msg_SetMulticastInfo,

  Msg_GetAllCfg,//取得所有配置
  Msg_SetAllCfg,//设置所有配置
  Msg_GetDevInfo,//取得设备信息
  Msg_SetDevInfo,//设置设备信息
  Msg_GetUserLst,//取得用户列表
  Msg_SetUserLst,//设置用户列表
  Msg_GetNetCfg,//取得网络配置
  Msg_SetNetCfg,//设置网络配置
  Msg_WiFiSearch,
  Msg_GetWiFiCfg,//取得WiFi配置
  Msg_SetWiFiCfg,//设置WiFi配置
  Msg_GetVideoCfg,//取得视频配置
  Msg_SetVideoCfg,//设置视频配置
  Msg_GetAudioCfg,//取得音频配置
  Msg_SetAudioCfg,//设置音频配置
  Msg_GetHideArea,//秘录
  Msg_SetHideArea,//秘录
  Msg_GetMDCfg,//移动侦测配置
  Msg_SetMDCfg,//移动侦测配置
  Msg_GetDiDoCfg__Disable,
  Msg_SetDiDoCfg__Disable,
  Msg_GetAlmCfg,//取得Alarm配置
  Msg_SetAlmCfg,//设置Alarm配置
  Msg_GetRS485Cfg__Disable,
  Msg_SetRS485Cfg__Disable,
  Msg_GetDiskCfg,//设置Disk配置
  Msg_SetDiskCfg,//设置Disk配置
  Msg_GetRecCfg,//取得录影配置
  Msg_SetRecCfg,//设置录影配置
  Msg_GetFTPCfg,
  Msg_SetFTPCfg,
  Msg_GetSMTPCfg,
  Msg_SetSMTPCfg,
  Msg_GetP2PCfg,
  Msg_SetP2PCfg,
  Msg_Ping,
  //begin add 2013-03-07
  Msg_GetRFCfg__Disable,
  Msg_SetRFCfg__Disable,
  Msg_RFControl__Disable,
  Msg_RFPanic__Disable,//67
  //end add 2013-03-07
  Msg_EmailTest,
  Msg_FTPTest,//69
  Msg_GetWiFiSTALst,//70
  Msg_DeleteFromWiFiSTALst,//71
  Msg_IsExistsAlarm,//72
  Msg_DOControl,//73
  Msg_GetDOStatus,//74
  Msg_ReSerialNumber,//75 20130808
  Msg_HttpGet,//76
  Msg_DeleteFile,//77

  Msg_HIISPCfg_Save,// 78
  Msg_HIISPCfg_Download,// 79
  Msg_HIISPCfg_Load,// 80
  Msg_HIISPCfg_Default,// 81

  Msg_GetAllCfgEx,//82
  Msg_MulticastSetWIFI,//83 zhb
  Msg_______
}TMsgID;
//-----------------------------------------------------------------------------
#define RECPLANLST 4
typedef struct TPlanRecPkt {                        //排程录影结构 sizeof 224
  struct {
    bool Active;
    u8 start_h;    //时 0-23
    u8 start_m;    //分 0-59
    u8 stop_h;     //时 0-23获取设备是否有警报发生
    u8 stop_m;     //分 0-59
    bool IsRun;      //当前计划是否启动
    u8 Flag1;
    u8 Flag2;
  }Week[7][RECPLANLST];                                 //日一二三四五六 每天最多4个任务
}TPlanRecPkt;
//-----------------------------------------------------------------------------
typedef enum TRecStyle {
  rs_RecManual,
  rs_RecAuto,
  rs_RecPlan,
  rs_RecAlarm
}TRecStyle;

typedef struct TRecCfgPkt {                      //录影配置包 sizeof 260
  i32 ID;
  i32 DevID;//PC端管理软件只用于存储数据库中设备编号　设备端保留
  i32 Chl;
  bool IsLoseFrameRec;//是否丢帧录影
  u8 RecStreamType;//0 主码流 1 次码流
  u8 Reserved;
  bool IsRecAudio;//录制音频 暂没有用到
  u32 Rec_AlmPrevTimeLen;//警前录影时长     5 s
  u32 Rec_AlmTimeLen;//警后录影时长        10 s
  u32 Rec_NmlTimeLen;//一般录影分割时长   600 s
  TRecStyle RecStyle;//录影类型
  TPlanRecPkt Plan;
  i32 bFlag;
}TRecCfgPkt;
//-----------------------------------------------------------------------------
/*
typedef struct TDiskCfgPkt_old {   //sizeof 888
  i32 IsFillOverlay;      // 是否覆盖早期文件(false或true,false为不覆盖,true为覆盖,缺省为false)
  char20 CurrentDiskName; // 当前正在录影的磁盘索引 0..7, ReadOnly
  struct {
    char20 DiskName;      // 磁盘 
    i32 Active;           // 是否做为录影磁盘 false or true
    u32 DiskSize;       // M ReadOnly
    u32 FreeSize;       // M
    u32 MinFreeSize;    // M
  }Disk[24];
}TDiskCfgPkt_old;
*/
//-----------------------------------------------------------------------------
typedef struct TDiskCfgPkt {   //sizeof 60
  char Reserved1[24];
  char20 DiskName;      // 磁盘 
  i32 Active;           // 是否做为录影磁盘 false or true
  u32 DiskSize;       // M ReadOnly
  u32 FreeSize;       // M
  u32 MinFreeSize;    // M
}TDiskCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TLanguage {
  cn = 0,
  tw = 1,
  en = 2 
}TLanguage;
//static char* DevLanguage[3] = {"cn","tw","en"};
//-----------------------------------------------------------------------------
typedef struct TAxInfo {//sizeof 40
  union {
    char40 BufValue;
    struct {
      bool ExistWiFi;
      bool ExistSD;

      bool ethLinkStatus;      //有线网络是否连接

      u8 HardType;      //硬件类型
      u32 VideoTypeMask;// 8
      u64 StandardMask;//16
      //u32 AutioTypeMask;//20暂未用到
      bool ExistFlash;


      u8 PlatformType;//TPlatFormType
      u8 Reserved[2];

      bool wifiStatus;
      bool upnpStatus;
      bool WlanStatus;
      bool p2pStatus;
      u64 SubStandardMask;//32
      //2011-04-06 add
      struct {
        i32 FirstDate;
        u16 TrialDays;
        u16 RunDays;
      } Sys;
    };
  };
}TAxInfo;

typedef struct TDevInfoPkt {                     //设备信息包sizeof 180
  char DevModal[12];                             //设备型号  ='7xxx'
  u32 SN;
  i32 DevType;                                   //设备类型
  char20 SoftVersion;                            //软件版本
  char20 FileVersion;                            //文件版本
  char20 DevName;                                //设备标识
  char40 DevDesc;                                //设备备注
  struct TAxInfo Info;

  i32 VideoChlCount;
  u8 AudioChlCount;
  u8 DiChlCount;
  u8 DoChlCount;
  u8 RS485DevCount;
  signed char TimeZone;
  u8 MaxUserConn;                               //最大用户连接数 default 10
  u8 OEMType;
  bool DoubleStream;                              //是否双码流 add at 2009/09/02
  struct {
    u8 w;//TTaskDayType;
    u8 start_h;//时 0-23
    u8 start_m;//分 0-59
    u8 Days;
  }RebootHM;
  //i32 Flag;
  i32 ProcRunningTime;
}TDevInfoPkt;
//-----------------------------------------------------------------------------
typedef struct TWiFiSearchPkt {//sizeof 40
  char32 SSID;
  u8 Siganl;//信号 0..100 极好 好 一般 差 极差
  u8 Channel;
  u8 EncryptType; //0=None 1=WEP 2=WPA
  u8 NetworkType;//0=Infra 1=Adhoc
  union {
    struct {
      u8 Auth;//0=AUTO 1=OPEN 2=SHARED
      u8 tag[3];
    }WEP;
    struct {
      u8 Auth;//0=AUTO 1=WPA-PSK 2=WPA2-PSK
      u8 Enc;//0=AUTO 1=TKIP 2=AES
      u8 tag[2];
    }WPA;
  };
}TWiFiSearchPkt;
//-----------------------------------------------------------------------------
typedef struct TWiFiCfgPkt {                     //无线配置包 sizeof 200
  bool Active;
  bool IsAPMode;//sta=0  ap=1
  u8 Reserved[2];
  char SSID_AP[30];
  char Password_AP[30];

  char32 SSID_STA;
  i32 Channel;//频道1..14 default 1=Auto
#define Encrypt_None   0
#define Encrypt_WEP    1
#define Encrypt_WPA    2
  i32 EncryptType;//(Encrypt_None,Encrypt_WEP,Encrypt_WPA);
  char64 Password_STA;
  i32 NetworkType;//0=Infra 1=Adhoc
  union {
    struct {
      char ValueStr[28];
    };
#define AUTH_AUTO      0
#define AUTH_OPEN      1
#define AUTH_SHARED    2
#define AUTH_TKIP      1
#define AUTH_AES       2
    struct {
      i32 Auth;//0=AUTO 1=OPEN 2=SHARED
    }WEP;
    struct {
      i32 Auth;//0=AUTO 1=WPA-PSK 2=WPA2-PSK
      i32 Enc;//0=AUTO 1=TKIP 2=AES
    }WPA;
  };
}TWiFiCfgPkt;
//-----------------------------------------------------------------------------
typedef struct TNetCfgPkt {                      //设备网络配置包sizeof 372
  i32 DataPort;                                   //命令数据端口
  i32 rtspPort;                                  //rtsp端口
  i32 HttpPort;                                  //http网页端口
  struct {
#define IP_STATIC   0
#define IP_DYNAMIC  1

    i32 IPType;
    char20 DevIP;
    char20 DevMAC;
    char20 SubMask;
    char20 Gateway;
    char20 DNS1;
    char20 DNS2;
    //bool IsActiveDHCPServer; //是否启动DHCPServer
    char Flag[4];
  }Lan;
  struct {
    i32 Active;
#define DDNS_3322     0
#define DDNS_dynDNS   1
#define DDNS_MyDDNS   2
#define DDNS_9299     3
    i32 DDNSType;                               //0=3322.ORG 1=dynDNS.ORG 2=MyDDNS 3=9299.org
    char40 DDNSDomain;                           //或DDNS SERVER IP
    union {
      struct {
        char40 HostAccount;                          //DDNS帐号
        char40 HostPassword;                         //DDNS密码
        i32 Flag;
      };
      struct {
        char40 DDNSServer;
      };
    };
  }DDNS;
  struct {
    i32 Active;
    char40 Account;
    char40 Password;
    i32 Flag;
  }PPPOE;
  struct {
    i32 Active;
    i32 Flag;
  }uPnP;
  i32 Flag;
}TNetCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TBaudRate{
  BaudRate_1200  =    1200,
  BaudRate_2400  =    2400,
  BaudRate_4800  =    4800,
  BaudRate_9600  =    9600,
  BaudRate_19200  =  19200,
  BaudRate_38400  =  38400,
  BaudRate_57600  =  57600,
  BaudRate_115200 = 115200
}TBaudRate;

typedef enum TDataBit{
  DataBit_5 = 5,
  DataBit_6 = 6,
  DataBit_7 = 7,
  DataBit_8 = 8
}TDataBit;

typedef enum TParityCheck{
  ParityCheck_None  = 0,
  ParityCheck_Odd   = 1,
  ParityCheck_Even  = 2,
  ParityCheck_Mask  = 3,
  ParityCheck_Space = 4
}TParityCheck;

typedef enum TStopBit{
  StopBit_1   = 0,
  StopBit_1_5 = 1,
  StopBit_2   = 2
}TStopBit;

typedef struct TRS485CfgPkt__Disable {                       //485通信包 sizeof 280
  i32 Chl;
  TBaudRate BPS;//波特率
  TDataBit DataBit;//数据位
  TParityCheck ParityCheck;//奇偶校验
  TStopBit StopBit;//停止位
  struct {
    u8 Address;
    u8 PTZProtocol;//云台协议
    u8 PTZSpeed;
    u8 Reserved;
  }Lst[32];//对应相应的视频通道

  //char PTZNameLst[128];//暂时未用到 format "Pelco_P\nPelco_D\nProtocol_Custom"

  i32 PTZCount;
  char20 PTZNameLst[6];
  i32 Reserved;

  i32 Flag;
}TRS485CfgPkt__Disable;

//-----------------------------------------------------------------------------
typedef struct TColorsPkt {
  i32 Chl;
  u8  Brightness;                               //亮度   0-255
  u8  Contrast;                                 //对比度 0-255
  u8  Hue;                                      //色度   0-255
  u8  Saturation;                               //饱和度 0-255
  u8  Sharpness;                                //饱和度 0-255
  u8 Reserved[3];
}TColorsPkt;
//-----------------------------------------------------------------------------
typedef struct TMulticastInfoPkt {               //多播发送信息包sizeof 556->588->628
  TDevInfoPkt DevInfo;
  TNetCfgPkt NetCfg;
  i32 Flag;// sendfrom client=0 sendfrom device=1
  TWiFiCfgPkt WiFiCfg;
  Tp2pCfgPkt p2pCfg;
  TVideoCfgPkt VideoCfg;
  TAudioCfgPkt AudioCfg;
  char20 UserName;                               //用户名 admin不能更改
  char20 Password;                               //密码
}TMulticastInfoPkt;
//-----------------------------------------------------------------------------
#define Head_CmdPkt           0xAAAAAAAA         //命令包包头
#define Head_VideoPkt         0xBBBBBBBB         //视频包包头
#define Head_AudioPkt         0xCCCCCCCC         //音频包包头
#define Head_TalkPkt          0xDDDDDDDD         //对讲包包头
#define Head_UploadPkt        0xEEEEEEEE         //上传包
#define Head_DownloadPkt      0xFFFFFFFF         //下载包//未用
#define Head_CfgPkt           0x99999999         //配置包
#define Head_SensePkt         0x88888888         //侦测包//未用
#define Head_MotionInfoPkt    0x77777777         //移动侦测阀值包头
//-----------------------------------------------------------------------------
typedef struct THeadPkt{                         //sizeof 8
  u32 VerifyCode;                              //校验码 = 0xAAAAAAAA 0XBBBBBBBB 0XCCCCCCCC 0XDDDDDDDD 0XEEEEEEEE
  u32 PktSize;                                 //本包大小=1460-8
}THeadPkt;
//-----------------------------------------------------------------------------
typedef struct TTalkHeadPkt {                    //对讲包包头  sizeof 32
  u32 VerifyCode;                              //校验码 = 0XDDDDDDDD
  u32 PktSize;                                 
  char20 TalkIP;
  u32 TalkPort;
}TTalkHeadPkt;
//-----------------------------------------------------------------------------
typedef struct TFrameInfo { //录影文件数据帧头  16 u8
  i64 FrameTime;                               //帧时间，time_t*1000000 +us
  u8 Chl;                                      //通道 0..15 对应 1..16通道
  bool IsIFrame;                                 //是否I帧
  u16 FrameID;                                  //帧索引,从0 开始,到65535，周而复始
  union {
    u32 PrevIFramePos;                         //前一个I帧文件指针，用于文件中处理或网络包发送
    i32 StreamType;                              //如果是双码流，现场包 0为主码流 1为次码流 add at 2009/09/02
    u32 DevID;                                 //单连接多设备时用到，暂保留
  };
}TFrameInfo;

typedef struct TDataFrameInfo { //录影文件数据帧头  24 u8
  THeadPkt Head;
  TFrameInfo Frame;
}TDataFrameInfo;
//-----------------------------------------------------------------------------
typedef struct TMotionInfoPkt { //移动侦测阀值  sizeof 4
  u8 AreaIndex;
  u8 ActiveNum;
  u8 Sensitive;
  u8 Tag;
}TMotionInfoPkt;
//-----------------------------------------------------------------------------
//错误代码
#define ERR_FAIL           0
#define ERR_OK             1
#define ERR_MAXUSERCONN    10001//连接用户数超过最大设定
//-----------------------------------------------------------------------------
typedef struct TLoginPkt {                       //用户登录包 sizeof 252->892
  char20 UserName;                               //用户名称
  char20 Password;                               //用户密码
  char20 DevIP;                                  //要连接的设备IP,或 host
  i32 UserGroup;                                 //Guest=1 Operator=2 Administrator=3  
  i32 SendSensePkt;                              //是否发送侦测包 0不发送 1发送
  TDevInfoPkt DevInfoPkt;
  //2009-05-12 add begin
  TVideoFormat v[4];
  TAudioFormat a[4];
  //2009-05-12 add end
  //i32 Flag;//返回是否在线　0不在线　1在线
#define SENDFROM_CLIENT    1
#define SENDFROM_NVRMOBILE 0
  i32 SendFrom;// (x2 0=手机NVR 1=客户端 )
}TLoginPkt;
//-----------------------------------------------------------------------------
typedef struct TCmdPkt {                         //sizeof 1460-8
  u32 PktHead;                                 //包头校验码 =Head_CmdPkt 0xAAAAAAAA
  TMsgID MsgID;                                  //消息
  u32 Session;                                 //网络用户许可，当发送网络登录包时此值为0，等于返回登录包的Session  //当包为程序内部通讯包时，此值忽略
  u32 Value;                                   //属性或返回值 0 or 1 or ErrorCode
  union {
    char ValueStr[1460 - 4*4 - 8];
    struct TLoginPkt LoginPkt;                   //登录包
    struct TPlayLivePkt LivePkt;                 //播放现场包
    struct TRecFilePkt RecFilePkt;               //回放录影包
    struct TPTZPkt PTZPkt;                       //云台控制台
    struct TRecFileLst RecFileLst;
    struct TPlayCtrlPkt PlayCtrlPkt;             //回放录影控制包

    struct TAlmSendPkt AlmSendPkt;               //警报上传包
    struct TDevInfoPkt DevInfoPkt;               //设备信息包
    struct TNetCfgPkt NetCfgPkt;                 //设备网络配置包
    struct TWiFiCfgPkt WiFiCfgPkt;               //无线网络配置包
    struct TDiskCfgPkt DiskCfgPkt;               //磁盘配置包
    struct TUserCfgPkt UserCfgPkt;               //用户配置包
    struct TRecCfgPkt RecCfgPkt;                 //录影配置包
    struct TMDCfgPkt MDCfgPkt;                   //移动侦测包--单通道
//    struct TDiDoCfgPkt DiDoCfgPkt;               //DIDO配置包 528
    struct TDoControlPkt DoControlPkt;           //DO控制包    
    struct THideAreaCfgPkt HideAreaCfgPkt;       //隐藏录影区域包--单通道
    struct TAlmCfgPkt AlmCfgPkt;                 //警报配置包
    struct TVideoCfgPkt VideoCfgPkt;             //视频配置包--单通道
    struct TAudioCfgPkt AudioCfgPkt;             //音频配置包--单通道    
    struct TRecFileHead FileHead;                //取得设备文件文件头信息
    struct TFilePkt FilePkt;                     //上传文件包
    //struct TRS485CfgPkt RS485CfgPkt;             //485通信包--单通道
    struct TColorsPkt Colors;                    //设置取得亮度、对比度、色度、饱和度

    struct TMulticastInfoPkt MulticastInfo;      //多播信息

    struct TFTPCfgPkt FTPCfgPkt;
    struct TSMTPCfgPkt SMTPCfgPkt;
    struct TBatchCfgPkt BatchCfgPkt;             //批量修改配置
    struct TWiFiSearchPkt WiFiSearchPkt[30];
    struct Tp2pCfgPkt p2pCfgPkt;
  };
}TCmdPkt;
//-----------------------------------------------------------------------------
typedef struct TNetCmdPkt {                      //网络发送包 sizeof 1460
  struct THeadPkt HeadPkt;
  struct TCmdPkt CmdPkt;
}TNetCmdPkt;
//-----------------------------------------------------------------------------

//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
typedef struct TNewDevInfo {//sizeof 80
  char DevModal[8];                             //设备型号  ='7xxx'
  u32 SN;
  char16 SoftVersion;                            //软件版本
  char20 DevName;                                //设备标识
  u64 StandardMask;//16
  u64 SubStandardMask;//32
  u8 DevType;                                   //设备类型
  bool ExistWiFi;
  bool ExistSD;
  bool ethLinkStatus;      //有线网络是否连接
  bool wifiStatus;
  bool upnpStatus;
  bool WlanStatus;
  bool p2pStatus;
  u8 HardType;      //硬件类型
  signed char TimeZone;
  bool DoubleStream;                              //是否双码流 add at 2009/09/02
  bool ExistFlash;
  i32 Reserved;
}TNewDevInfo;

typedef struct TNewNetCfg{//sizeof 132
  u16 DataPort;                                   //命令数据端口
  u16 rtspPort;                                  //rtsp端口
  u16 HttpPort;                                  //http网页端口
  u8 IPType;
  u8 Flag_00001;
  i32 DevIP;
  i32 SubMask;
  i32 Gateway;
  i32 DNS1;
  char20 DevMAC;
  bool ActiveuPnP;
  bool ActiveDDNS;
  u8 DDNSType;                               //0=3322.ORG 1=dynDNS.ORG 2=MyDDNS 3=9299.org
  u8 Flag_00002;
  char40 DDNSDomain;                           //或DDNS SERVER IP
  char40 DDNSServer;
  i32 Reserved;
}TNewNetCfg;

typedef struct TNewwifiCfg{//sizeof 92
  bool ActiveWIFI;
  bool IsAPMode;//sta=0  ap=1
  u8 Flag_00003;
  u8 Flag_00004;
  char20 SSID_AP;
  char20 Password_AP;
  char20 SSID_STA;
  char20 Password_STA;
  u8 Channel;//频道1..14 default 1=Auto
  u8 EncryptType;//(Encrypt_None,Encrypt_WEP,Encrypt_WPA);
  u8 Auth;//WEP(0=AUTO 1=OPEN 2=SHARED)  WPA(0=AUTO 1=WPA-PSK 2=WPA2-PSK)
  u8 Enc;//WPA(0=AUTO 1=TKIP 2=AES)
  i32 Reserved;
}TNewwifiCfg;

typedef struct TNewp2pCfg{//sizeof 64
  bool ActiveP2P;
  u8 StreamType;//0 主码流 1 次码流
  u8 p2pType;   //tutk=0 self=1
  char UID[21];
  char20 Password;
  i32 SvrIP[4];
  i32 Reserved;
}TNewp2pCfg;

typedef struct TNewVideoCfg {//sizeof 16
  u8 StandardEx0;//TStandardEx
  u8 FrameRate0;                                //帧率 1-30 MAX:PAL 25 NTSC 30
  u16 BitRate0;
  u8 StandardEx1;//TStandardEx
  u8 FrameRate1;//帧率 1-30 MAX:PAL 25 NTSC 30
  u16 BitRate1;//码流 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  bool IsMirror;                           //水平翻转 false or true
  bool IsFlip;                             //垂直翻转 false or true
  bool IsShowFrameRate;
  u8 Flag_00008;
//  i32 Reserved;
  u8 BitRateType0;                              //0定码流CBR 1定画质VBR
  u8 BitRateQuant0; 
  u8 BitRateType1;                              //0定码流CBR 1定画质VBR
  u8 BitRateQuant1; 
}TNewVideoCfg;

typedef struct TNewAudioCfg{//sizeof 12
  bool  ActiveAUDIO;                                   //是否启动声音
  u8 InputTypeAUDIO;                                 //0 MIC输入, 1 LINE输入
  u8 VolumeLineIn;
  u8 VolumeLineOut;
  u8 nChannels;                               //单声道=0 立体声=1
  u8 wBitsPerSample;                          //number of bits per sample of mono data 
  u16 nSamplesPerSec;                          //采样率
  u8 wFormatTag;
  u8 Reserved[3];
}TNewAudioCfg;

typedef struct TNewUserCfg {//sizeof 128
  char20 UserName[3];                               //用户名 admin不能更改
  char20 Password[3];                               //密码
  u8 Authority[3];                                 //3为admin
  u8 Reserved;
  i32 Reserved1;
}TNewUserCfg;

typedef struct TNewDIAlm {//sizeof 12
  bool ActiveDI;
  u8 Reserved;
  bool IsAlmRec;
  bool IsFTPUpload;
  bool ActiveDO;//DI关联DO通道 false close
  bool IsSendEmail;
  u8 Reserved1;// >0为预设位
  u8 AlmOutTimeLen;//报警输出时长(秒) 0 ..255 s
  //struct TTaskhm hm;
  i32 Reserved2;
}TNewDIAlm;

typedef struct TNewMDAlm {//sizeof 28                       //移动侦测包
  bool ActiveMD;
  u8 Sensitive;                              //侦测灵敏度 0-255
  bool IsAlmRec;
  bool IsFTPUpload;
  bool ActiveDO;//DI关联DO通道 false close
  bool IsSendEmail;
  u8 Reserved2;
  u8 AlmOutTimeLen;                    //报警输出时长(秒) 0 ..255 s
  //struct TTaskhm hm;
  struct {
    float left,top,right,bottom;
  }Rect;
  i32 Reserved;
}TNewMDAlm;

typedef struct TNewSoundAlm{//sizeof 12
  bool ActiveSoundTrigger;
  u8 Sensitive;
  bool IsAlmRec;
  bool IsFTPUpload;
  bool ActiveDO;//DI关联DO通道 false close
  bool IsSendEmail;
  u8 Reserved1;// >0为预设位
  u8 AlmOutTimeLen;                    //报警输出时长(秒) 0 ..255 s
  //struct TTaskhm hm;
  i32 Reserved2;
}TNewSoundAlm;

typedef struct TNewRecCfg{//sizeof 236                      //录影配置包
  u8 RecStreamType;//0 主码流 1 次码流
  bool IsRecAudio;//录制音频 暂没有用到
  u8 RecStyle;//录影类型
  u8 Reserved;
  TPlanRecPkt Plan;
  u16 Rec_AlmTimeLen;//警后录影时长        10 s
  u16 Rec_NmlTimeLen;//一般录影分割时长   600 s
  i32 Reserved1;
}TNewRecCfg;

typedef struct TNewDevCfg {//812->1000
  union {
    char ValueStr[1000];
    struct {
      struct TNewDevInfo DevInfo;
      struct TNewNetCfg NetCfg;
      struct TNewwifiCfg wifiCfg;
      struct TNewp2pCfg p2pCfg;
      struct TNewVideoCfg VideoCfg;
      struct TNewAudioCfg AudioCfg;
      struct TNewUserCfg UserCfg;
      struct TNewDIAlm DIAlm;
      struct TNewMDAlm MDAlm;
      struct TNewSoundAlm SoundAlm;
      struct TNewRecCfg RecCfg;
    };
  };
}TNewDevCfg;

typedef struct TNewCmdPkt {//maxsize sizeof 1008
  u32 VerifyCode;//校验码 = 0xAAAAAAAA
  u8 MsgID;//TMsgID
  u8 Result;
  u16 PktSize;
  union {
    i32 Value;
    char Buf[1000];
    struct TNewDevCfg NewDevCfg;

    struct TLoginPkt LoginPkt;                   //登录包 892
    struct TPlayLivePkt LivePkt;                 //播放现场包
    struct TRecFilePkt RecFilePkt;               //回放录影包
    struct TPTZPkt PTZPkt;                       //云台控制台
    struct TPlayCtrlPkt PlayCtrlPkt;             //回放录影控制包

    struct TAlmSendPkt AlmSendPkt;               //警报上传包
    struct TDevInfoPkt DevInfoPkt;               //设备信息包
    struct TNetCfgPkt NetCfgPkt;                 //设备网络配置包
    struct TWiFiCfgPkt WiFiCfgPkt;               //无线网络配置包
    struct TDiskCfgPkt DiskCfgPkt;               //磁盘配置包 888
    struct TRecCfgPkt RecCfgPkt;                 //录影配置包
    struct TMDCfgPkt MDCfgPkt;                   //移动侦测包--单通道
    struct TDoControlPkt DoControlPkt;           //DO控制包    
    struct THideAreaCfgPkt HideAreaCfgPkt;       //隐藏录影区域包--单通道
    struct TAlmCfgPkt AlmCfgPkt;                 //警报配置包
    struct TVideoCfgPkt VideoCfgPkt;             //视频配置包--单通道
    struct TAudioCfgPkt AudioCfgPkt;             //音频配置包--单通道    
    struct TRecFileHead FileHead;                //取得设备文件文件头信息
    struct TFilePkt FilePkt;                     //上传文件包
    struct TColorsPkt Colors;                    //设置取得亮度、对比度、色度、饱和度
    struct TFTPCfgPkt FTPCfgPkt;
    struct TSMTPCfgPkt SMTPCfgPkt;
    struct Tp2pCfgPkt p2pCfgPkt;
  };  
}TNewCmdPkt;

//#pragma option pop //end C++Builder enum 4 u8

#endif //end Ax_protocol_H

