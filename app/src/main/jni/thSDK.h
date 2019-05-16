//-----------------------------------------------------------------------------
// Author      :
// Date        : 2013.04.20
// Version     : V 2.00
// Description : www.southipcam.com
//-----------------------------------------------------------------------------
#ifndef thSDK_H
#define thSDK_H

#include "../linuxthSDK/cm_types.h"

#pragma pack(4)//n=1,2,4,8,16
//-----------------------------------------------------------------------------
#define Port_th_CmdData        2000                        //TCP 缺省命令数据端口 对讲端口
#define Port_th_http             80
#define Port_th_RTSP            554

#define Port_th_Multicast      2000
#define Port_th_Search_Local   1999
#define IP_th_Multicast  "239.255.255.250"//uPnP IP查询 多播对讲IP
#define REC_FILE_EXT           "av"
#define REC_FILE_EXT_DOT       ".av"

//-----------------------------------------------------------------------------
#define Head_CmdPkt           0xAAAAAAAA         //命令包包头
#define Head_VideoPkt         0xBBBBBBBB         //视频包包头
#define Head_AudioPkt         0xCCCCCCCC         //音频包包头
#define Head_TalkPkt          0xDDDDDDDD         //对讲包包头
#define Head_UploadPkt        0xEEEEEEEE         //上传包
#define Head_DownloadPkt      0xFFFFFFFF         //下载包
#define Head_CfgPkt           0x99999999         //配置包
#define Head_SensePkt         0x88888888         //侦测包
//-----------------------------------------------------------------------------
typedef struct THeadPkt{                        //sizeof 8
  u32 VerifyCode;                              //校验码 = 0xAAAAAAAA 0XBBBBBBBB 0XCCCCCCCC 0XDDDDDDDD 0XEEEEEEEE
  u32 PktSize;                                //本包大小=1460-8
}THeadPkt;
//-----------------------------------------------------------------------------
typedef struct TFrameInfo {  //录影文件数据帧头  16 u8
  i64 FrameTime;                                //帧时间，time_t*1000000 +us
  u8 Chl;                                      //通道 0..15 对应 1..16通道
  bool IsIFrame;                                  //是否I帧
  u16 FrameID;                                  //帧索引,从0 开始,到65535，周而复始
  union {
    u32 PrevIFramePos;                         //前一个I帧文件指针，用于文件中处理或网络包发送
    i32 StreamType;                              //如果是双码流，现场包 0为主码流 1为次码流 add at 2009/09/02
    u32 DevID;                               //单连接多设备时用到，暂保留
  };
}TFrameInfo;

typedef struct TDataFrameInfo {   //录影文件数据帧头  24 u8
  THeadPkt Head;
  TFrameInfo Frame;
}TDataFrameInfo;
//-----------------------------------------------------------------------------
typedef enum TAlmType {
  Alm_None             =0,//空
  Alm_MotionDetection  =1,//位移报警Motion Detection
  Alm_DigitalInput     =2,//DI报警
  Alm_SoundTrigger     =3,//声音触发报警
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
//-----------------------------------------------------------------------------
typedef enum TGroupType{                          //sizeof 4 u8
  pt_Cmd,
  pt_PlayLive,
  pt_PlayHistory,
  pt_PlayMedia
}TGroupType;
//-----------------------------------------------------------------------------
typedef void(TSearchDevCallBack)(
                                 u32 SN,            //
                                 i32 DevType,       //设备类型
                                 i32 VideoChlCount, //通道数据
                                 i32 DataPort,      //数据端口
                                 i32 HttpPort,    //WEB端口
                                 char* DevName,    //设备名称
                                 char* DevIP,       //设备IP
                                 char* DevMAC,      //设备MAC地址
                                 char* SubMask,     //设备子网掩码
                                 char* Gateway,    //设备网关
                                 char* DNS1,        //设备DNS
                                 char* DDNSServer,
                                 char* DDNSHost,    //DDNS域名
                                 char* UID          //P2P方式UID
                                 );

typedef void(TAVCallBack)(
                          TDataFrameInfo* PInfo,    //音视频帧头信息
                          char* Buf,                //音视频解码前帧数据
                          i32 Len,                  //数据长度
                          void* UserCustom         //用户自定义数据
                          );

typedef void(TAlmCallBack)(
                           i32 AlmType,              //警报类型，参见TAlmType
                           i32 AlmTime,              //警报时间time_t
                           i32 AlmChl,              //警报通道
                           void* UserCustom         //用户自定义数据
                           );

//-----------------------------------------------------------------------------
bool thNet_Init(THandle* NetHandle);
/*-----------------------------------------------------------------------------
函数描述：初始化网络播放
参数说明：
NetHandle:返回网络句柄
DevType:设备类型
X1=dt_Devx1=11
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_SetCallBack(THandle NetHandle, TAVCallBack avEvent, TAlmCallBack AlmEvent, void* UserCustom);
/*-----------------------------------------------------------------------------
函数描述：网络播放设置回调函数
参数说明：
NetHandle:网络句柄，由thNet_Init返回
avEvent:视频音频数据回调函数
AlmEvent:设备警报回调函数
UserCustom:用户自定义数据
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_Free(THandle* NetHandle);
/*-----------------------------------------------------------------------------
函数描述：播放网络播放
参数说明：
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_Connect(THandle NetHandle, char* UserName, char* Password, char* DevIP, i32 DataPort, u32 TimeOut, i32 IsCreateRecvThread);
/*-----------------------------------------------------------------------------
函数描述：连接网络设备
参数说明：
NetHandle:网络句柄，由thNet_Init返回
UserName:连接帐号
Password:连接密码
DevIP:设备IP
DataPort:设备或转发服务器端口
TimeOut:连接超时，单位ms,缺省 3000ms
IsCreateRecvThread:是否创建数据收取线程
返 回 值：成功返回true
          失败返回false
------------------------------------------------------------------------------*/
bool thNet_Connect_P2P(THandle NetHandle, i32 p2pType, char* p2pUID, char* p2pPSD, u32 TimeOut, i32 IsCreateRecvThread);
/*-----------------------------------------------------------------------------
函数描述：连接网络设备，P2P方式
参数说明：
NetHandle:网络句柄，由thNet_Init返回
UID:设备ID
TimeOut:连接超时，单位ms,缺省 3000ms
IsCreateRecvThread:是否创建数据收取线程
返 回 值：成功返回true
          失败返回false
------------------------------------------------------------------------------*/
bool thNet_DisConn(THandle NetHandle);
/*-----------------------------------------------------------------------------
函数描述：断开网络设备连接
参数说明：
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_IsConnect(THandle NetHandle);
/*-----------------------------------------------------------------------------
函数描述：设备是否连接
参数说明：
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_Play(THandle NetHandle, u32 VideoChlMask, u32 AudioChlMask, u32 SubVideoChlMask);
/*-----------------------------------------------------------------------------
函数描述：开始播放
参数说明：
NetHandle:网络句柄，由thNet_Init返回
VideoChlMask:通道掩码，
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
AudioChlMask:通道掩码，
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
SubVideoChlMask:次码流通道掩码
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_Stop(THandle NetHandle);
/*-----------------------------------------------------------------------------
函数描述：停止播放
参数说明：
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_GetVideoCfg(THandle NetHandle, i32* VideoType, i32* Width0, i32* Height0, i32* FrameRate0, i32* Width1, i32* Height1, i32* FrameRate1);
/*-----------------------------------------------------------------------------
函数描述：获取视频配置1
参数说明：
NetHandle:网络句柄，由thNet_Init返回
Standard :NTSC=0 PAL=1  60HZ=0 50HZ=1
VideoType:MPEG4=0 MJPEG=1 H264=2
IsMirror :图像是否镜像
IsFlip   :图像是否反转
Width0   :主码流宽
Height0  :主码流高
FrameRate0:主码流帧率
BitRate0:主码流码流
Width1   :次码流宽
Height1  :次码流高
FrameRate1:次码流帧率
BitRate1:次码流码流
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_SetTalk(THandle NetHandle, char* Buf, i32 BufLen);
/*-----------------------------------------------------------------------------
函数描述：发送对讲音频数据
参数说明：
NetHandle:网络句柄，由thNet_Init返回
音频采集格式，从thNet_GetAudioCfg获取
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thSearch_Init(TSearchDevCallBack SearchEvent);
/*-----------------------------------------------------------------------------
函数描述：初始化查询设备
参数说明：
SearchEvent:查询设备回调函数
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thSearch_SetWiFiCfg(char* LocalIP, char* SSID, char* Password);
bool thSearch_SearchDevice(char* LocalIP);
/*-----------------------------------------------------------------------------
函数描述：开始查询设备
LocalIP:传入的本地IP，缺省为NULL
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thSearch_Free(void); 
/*-----------------------------------------------------------------------------
函数描述：释放查询设备
返 回 值：成功返回true，失败返回false
------------------------------------------
------------------------------------------------------------------------------*/

//*****************************************************************************
bool thNet_CreateRecvThread(THandle NetHandle);
/*-----------------------------------------------------------------------------
函数描述：创建收取数据线程
参数说明：只能在thNet_Connect中IsCreateRecvThread为false时使用
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_RemoteFilePlay(THandle NetHandle, char* FileName);
/*-----------------------------------------------------------------------------
函数描述：开始播放远程文件
参数说明：
NetHandle:网络句柄，由thNet_Init返回
FileName:传入的远程录像文件名
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_RemoteFileStop(THandle NetHandle);
/*-----------------------------------------------------------------------------
函数描述：停止播放远程文件
参数说明：
NetHandle:网络句柄，由thNet_Init返回
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_RemoteFilePlayControl(THandle NetHandle, i32 PlayCtrl, i32 Speed, i32 Pos);
/*-----------------------------------------------------------------------------
函数描述：远程文件播放控制
参数说明：
NetHandle:网络句柄，由thNet_Init返回
PlayCtrl:   PS_None               =0,                 //空
            PS_Play               =1,                 //播放
            PS_Pause              =2,                 //暂停
            PS_Stop               =3,                 //停止
            PS_FastBackward       =4,                 //快退
            PS_FastForward        =5,                 //快进
            PS_StepBackward       =6,                 //步退
            PS_StepForward        =7,                 //步进
            PS_DragPos            =8,                 //拖动
Speed:如果PlayCtrl=PS_StepBackward, PS_FastForward ，则保存快进快退倍率 1 2 4 8 16 32倍率
Pos:如果PlayCtrl=PS_DragPos，则保存文件文件位置Pos
返 回 值：成功返回true，失败返回false
------------------------------------------------------------------------------*/
bool thNet_HttpGet(THandle NetHandle, char* url, char* Buf, i32* BufLen);
bool thNet_HttpGetStop(THandle NetHandle);

#endif 
