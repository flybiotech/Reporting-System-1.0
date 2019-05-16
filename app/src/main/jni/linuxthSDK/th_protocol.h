//-----------------------------------------------------------------------------
// Author      : ��첨
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
#define IP_Ax_Multicast  "239.255.255.250"//uPnP IP��ѯ �ಥ�Խ�IP
#define REC_FILE_EXT           "av"
#define REC_FILE_EXT_DOT      ".av"

//#pragma option push -b //start C++Builder enum 4 u8
//#pragma option push -b- //start C++Builder enum 1 u8

typedef enum TDevType {// sizeof 4
  dt_None=0,
  dt_DevX1=11,
  dt_DevOther
}TDevType;

/*�����*/
//-----------------------------------------------------------------------------
typedef struct TSMTPCfgPkt {//sizeof 500 u8�ʼ����ð�
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
typedef struct TFTPCfgPkt {//sizeof 232 u8 FTP���ð�
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

typedef struct Tp2pCfgPkt {//sizeof 88 u8 P2P���ð�
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
  u8 StreamType;//0 ������ 1 ������
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
typedef enum TFontColor {                        //OSD ������ɫ sizeof 4 u8
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
  0x000000,//cl_Black=0,//��
  0x000080,//cl_Maroon=1,//����
  0x008000,//cl_Green=2,//����
  0x008080,//cl_Olive=3,//����
  0x800000,//cl_Navy=4,//����
  0x800080,//cl_Purple=5,//��
  0x808000,//cl_Teal=6,//����
  0x0000FF,//cl_Red=7,//��
  0x00FF00,//cl_Lime=8,//��
  0x00FFFF,//cl_Yellow=9,//��
  0xFF0000,//cl_Blue=10,//��
  0xFF00FF,//cl_Fuchsia=11,//���
  0xFFFF00,//cl_Aqua=12,//��
  0x808080,//cl_Gray=13,//���
  0xC0C0C0,//cl_Silver=14,//��
  0xFFFFFF,//cl_White=15,//��
  0xFFFFFF//cl_Transparent=255//͸��
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
typedef enum TVideoType {                        //��Ƶ��ʽsizeof 4 u8
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
typedef struct TBatchCfgPkt { //�����޸����� sizeof 16
  i32 BitRate;                                   //���� 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  u8 Standard;                                 //TStandard��ʽ PAL=1, NTSC=0
  u8 Resolution;                               //TResolution
  u8 FrameRate;                                //֡�� 1-30 MAX:PAL 25 NTSC 30
  u8 IPInterval;                               //IP֡��� 1-120 default 30
  i32 AudioActive;
  i32 DevTime;
}TBatchCfgPkt;
//-----------------------------------------------------------------------------
#define CBR    0
#define VBR    1
typedef struct TVideoFormat {                    //��Ƶ��ʽ sizeof 128
  i32  Standard;                                 //��ʽ PAL=1, NTSC=0 default=0xff
  i32  Width;                                    //�� 720 360 180 704 352 176 640 320 160
  i32  Height;                                   //�� 480 240 120 576 288 144 
  TVideoType VideoType;                          //MPEG4=0x00, MJPEG=0x01  H264=0x02
  u8  Brightness;                               //����   0-255
  u8  Contrast;                                 //�Աȶ� 0-255
  u8  Hue;                                      //ɫ��   0-255
  u8  Saturation;                               //���Ͷ� 0-255
  i32  FrameRate;                                //֡�� 1-30 MAX:PAL 25 NTSC 30
  i32  IPInterval;                               //IP֡��� 1-120 default 30
  u8 BitRateType;                              //0������CBR 1������VBR
  u8 BitRateQuant;                             //����  0..4
  u8  BrightnessNight;                          //����   0-255
  u8  ContrastNight;                            //�Աȶ� 0-255


  i32  BitRate;                                  //���� 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  i32  IsMirror;                                 //ˮƽ��ת false or true
  i32  IsFlip;                                   //��ֱ��ת false or true
  char40 Title;                                  //OSD���� 20������

  u8 HueNight;
  bool ShowTitleInDev;
  bool IsShowTitle;                            //��ʾʱ����� false or true
  u8 TitleColor;
  i16 TitleX;
  i16 TitleY;

  u8 SaturationNight;
  bool ShowTimeInDev;
  bool IsShowTime;                               //��ʾˮӡ false or true
  u8 TimeColor;
  i16 TimeX;
  i16 TimeY;

  u8 DeInterlaceType;
  bool IsDeInterlace;
  bool IsDeInterlaceSub;//add at 2010/12/01
  bool IsWDR;
  u8  Sharpness;                               //��� 0-255
  u8 IRCutSensitive;
  u8 Reserved[2];

  u8 SharpnessNight;
  bool ShowFrameRateInDev;
  bool IsShowFrameRate;
  u8 FrameRateColor;
  i16 FrameRateX;
  i16 FrameRateY;

  struct { //add at 2009/09/02
    u8 BitRateQuant;//����  0..4
    u8 StandardEx;//TStandardEx
    u8 FrameRate;//֡�� 1-30 MAX:PAL 25 NTSC 30
    u8 BitRateType;//0������ 1������
    i32 BitRate;//���� 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  }Sub;//������

  i32 Flag;
}TVideoFormat;
//-----------------------------------------------------------------------------
typedef struct TVideoCfgPkt {                    //��Ƶ���ð� sizeof 148 u8
  i32  Chl;                                      //ͨ�� 0..15 ��Ӧ 1..16ͨ��
  i32  Active;                                   //�Ƿ�����(������ʱû���õ�)
  u8 InputType;                                //��������
  u8 Reserved[3];
  struct TVideoFormat VideoFormat;               //��Ƶ��ʽ
  i32 Flag;                                      //������Ч=1
  i32 Flag1;
}TVideoCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TAudioType {                        //��Ƶ��ʽsizeof 4 u8
  PCM                   =0x0001,
  G711                  =0x0002,
  MPEGLAYER2            =0x0050,
  MPEGLAYER3            =0x0055,
}TAudioType;
//-----------------------------------------------------------------------------
typedef struct TAudioFormatEx {                    //��Ƶ��ʽ = sizeof 8 ��δ�õ�
  u8 AudioType;                              //PCM=0x01, G711 = 0x02
  u8 nChannels;                               //������=0 ������=1
  u16 wBitsPerSample;                          //number of bits per sample of mono data 
  u32 nSamplesPerSec;                          //������ 
}TAudioFormatEx;
//-----------------------------------------------------------------------------
typedef struct TAudioFormat {                    //��Ƶ��ʽ = TWaveFormatEx = sizeof 32
  u32 wFormatTag;                              //PCM=0X0001, ADPCM=0x0011, MP2=0x0050, MP3=0X0055, GSM610=0x0031
  u32 nChannels;                               //������=0 ������=1
  u32 nSamplesPerSec;                          //������ 
  u32 nAvgu8sPerSec;                         //for buffer estimation 
  u32 nBlockAlign;                             //block size of data 
  u32 wBitsPerSample;                          //number of bits per sample of mono data 
  u32 cbSize;                                  //������С
  i32 Flag;                                      
}TAudioFormat;
//-----------------------------------------------------------------------------
typedef struct TAudioCfgPkt {                    //��Ƶ���ð� sizeof 48 u8
  i32  Chl;                                      //ͨ��0..15 ��Ӧ 1..16ͨ��
  i32  Active;                                   //�Ƿ���������
  struct TAudioFormat AudioFormat;               //��Ƶ��ʽ
#define MIC_IN  0
#define LINE_IN 1
  u8 InputType;                                 //0 MIC����, 1 LINE����
  u8 VolumeMicIn;//������
  u8 VolumeLineIn;
  u8 VolumeLineOut;

  bool SoundTriggerActive;
  u8 SoundTriggerSensitive;
  bool IsAudioGain;
  u8 Reserved;
}TAudioCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TPlayCtrl {                         //���ſ���sizeof 4 u8
  PS_None               =0,                 //��
  PS_Play               =1,                 //����
  PS_Pause              =2,                 //��ͣ
  PS_Stop               =3,                 //ֹͣ
  PS_FastBackward       =4,                 //����
  PS_FastForward        =5,                 //���
  PS_StepBackward       =6,                 //����
  PS_StepForward        =7,                 //����
  PS_DragPos            =8,                 //�϶�
}TPlayCtrl; 
//-----------------------------------------------------------------------------
typedef struct TPlayCtrlPkt {
  TPlayCtrl PlayCtrl;
  u32 Speed;//���PlayCtrl=PS_StepBackward, PS_FastForward ���򱣴������˱��� 1 2 4 8 16 32����
  u32 Pos;//���PlayCtrl=PS_DragPos���򱣴��ļ��ļ�λ��Pos
}TPlayCtrlPkt;
//-----------------------------------------------------------------------------
typedef struct TRecFilePkt {                     //������ʷ�� SizeOf 100
  char20 DevIP;
  u8 Chl;
  u8 RecType;                               //0:��ͨ¼Ӱ 1:����¼Ӱ 2ý���ļ�
  u8 Reserved[2];
  i32 StartTime;                              //��ʼʱ��
  i32 EndTime;                                //����ʱ��
  char64 FileName;                               //�ļ���
  i32 Flag;                                      //����
}TRecFilePkt;
//-----------------------------------------------------------------------------
//  /sd/rec/20091120/20091120_092749_0.ra2
typedef struct TRecFileIdx {                     //¼Ӱ�ļ������� sizeof 80
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
typedef struct TRecFileHead {                    //¼Ӱ�ļ�ͷ��ʽ sizeof 256 u8
  u32 DevType;                                 //�豸���� = DEV_Ax
  u32 FileSize;                                //�ļ���С
  i32 StartTime;                              //��ʼʱ��
  i32 EndTime;                                //ֹͣʱ��
  char20 DevName;                                //�豸ID
  char20 DevIP;                                  //�豸IP
  u32 VideoChannel;                            //��Ƶͨ����ͳ��
  u32 AudioChannel;                            //��Ƶͨ����ͳ��
  struct TVideoFormat VideoFormat;               //��Ƶ��ʽ
  struct TAudioFormat AudioFormat;               //��Ƶ��ʽ
  i32 Flag;                                      //����
  i32 Flag1;                                      //����
  i32 Flag2;                                      //����
  i32 Flag3;                                      //����
  i32 Flag4;                                      //����
  i32 Flag5;                                      //����
  i32 Flag6;                                      //����
  i32 Flag7;                                      //����
}TRecFileHead;
//-----------------------------------------------------------------------------
typedef struct TFilePkt {                        //�ϴ��ļ��� sizeof 528+4
#define FILETYPE_BURNIN_UBOOT 7 //x1 burn in
#define FILETYPE_BIN          2 //x1 x2 bin ������
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
  i32 Flag;//0=�ϴ��ļ�ʧ�� 1=�ϴ��ļ����,��������,�벻Ҫ�ϵ�  2=�ϴ��ļ��ɹ� (3=�����ϴ��ļ� x5add)
  char256 DstFile;
  u32 crc;//2012-02-10
}TFilePkt;
//-----------------------------------------------------------------------------
typedef enum TAlmType {
  Alm_None             =0,//��
  Alm_MotionDetection  =1,//λ�Ʊ���Motion Detection
  Alm_DigitalInput     =2,//DI����
  Alm_SoundTrigger       =3,////������������
  Net_Disconn          =4,//�������
  Net_ReConn           =5,//��������
  Alm_HddFill          =6,//����
  Alm_VideoBlind       =7,//��Ƶ�ڵ�
  Alm_VideoLost        =8,//��Ƶ��ʧ
  Alm_Other3           =9,//��������3
  Alm_Other4           =10,//��������4
  Alm_RF               =11,
  Alm_OtherMax         =12,
}TAlmType;

typedef struct TAlmSendPkt {                     //�����ϴ���sizeof 36
  TAlmType AlmType;                              //��������
  i32 AlmTime;                                   //����ʱ��
  i32 AlmPort;                                   //�����˿�
  char20 DevIP;
  i32 Flag;                                      //MD ��������
}TAlmSendPkt;
//-----------------------------------------------------------------------------
typedef struct TDoControlPkt {                 //do���ư���sizeof 16
  i32 Chl;
  i32 Value;                                   // 0 �ء�1 ��
  i32 Reserved;
}TDoControlPkt;
//-----------------------------------------------------------------------------
typedef enum TTaskDayType{w_close,w_1,w_2,w_3,w_4,w_5,w_6,w_7,w_1_5,w_1_6,w_6_7,w_1_7,w_Interval} TTaskDayType;
typedef struct TTaskhm {
  u8 w;
  u8 Days;
  u8 Reserved[2];
  u8 start_h;//ʱ 0-23
  u8 start_m;//�� 0-59
  u8 stop_h;//ʱ 0-23
  u8 stop_m;//�� 0-59
}TTaskhm;
//-----------------------------------------------------------------------------
typedef struct THideAreaCfgPkt {                 //����¼Ӱ����� sizeof 72
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
typedef struct TMDCfgPkt {                       //�ƶ����� sizeof 96
  u8 Reserved1[8];
  bool Active;
  u8 Reserved2;
  u8 Sensitive;                              //��������� 0-255
  bool IsFloatNewRect;
  TRect Rect;                        //������� sizeof 8
  char40 Reserved3;
  struct TTaskhm hm;

  struct {
    float left, top, right, bottom;
  }NewRect;

  i32 SettingStandard;//����ʱ�ķֱ��� 20130403
}TMDCfgPkt;
//-----------------------------------------------------------------------------
typedef struct TAlmCfgItem {
  u8 AlmType;//u8(TAlmType)
  u8 Channel;
  u8 Active;//only di
  bool IsAlmRec;
  bool IsFTPUpload;//NetSend
  bool ActiveDO;//DI����DOͨ�� false close
  bool IsSendEmail;//u8 DOChannel;
  u8 Reserved2;//
}TAlmCfgItem;

typedef struct TAlmCfgPkt {                   //�������ð� sizeof 268 -> 52 20140928
  i32 AlmOutTimeLen;                    //�������ʱ��(��) 0 ..600 s
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

#define MAXUSERCOUNT             20              //����û�����
typedef struct TUserCfgPkt {                     //sizeof 1048
  i32 Count;
  struct {
    i32 UserGroup;                                 //Guest=1 Operator=2 Administrator=3
    i32 Authority;                                 //3Ϊadmin ,
    char20 UserName;                               //�û��� admin���ܸ���
    char20 Password;                               //����
    i32 Flag;
  }Lst[MAXUSERCOUNT];
  i32 Flag;
}TUserCfgPkt;
//-----------------------------------------------------------------------------
typedef enum TPTZCmd {                           //sizeof 4 u8
  PTZ_None,
  PTZ_Up,//��
  PTZ_Up_Stop,//��ֹͣ
  PTZ_Down,//��
  PTZ_Down_Stop,//��ֹͣ
  PTZ_Left,//��
  PTZ_Left_Stop,//��ֹͣ
  PTZ_Right,//��
  PTZ_Right_Stop,//��ֹͣ

  PTZ_LeftUp,//����
  PTZ_LeftUp_Stop,//����ֹͣ
  PTZ_RightUp,//����
  PTZ_RightUp_Stop,//����ֹͣ
  PTZ_LeftDown,//����
  PTZ_LeftDown_Stop,//����ֹͣ
  PTZ_RightDown,//����
  PTZ_RightDown_Stop,//����ֹͣ

  PTZ_IrisIn,//��ȦС
  PTZ_IrisInStop,//��Ȧֹͣ
  PTZ_IrisOut,//��Ȧ��
  PTZ_IrisOutStop,//��Ȧֹͣ

  PTZ_ZoomIn,//����С
  PTZ_ZoomInStop,//����ֹͣ
  PTZ_ZoomOut,//���ʴ�
  PTZ_ZoomOutStop,//����ֹͣ

  PTZ_FocusIn,//����С
  PTZ_FocusInStop,//����ֹͣ
  PTZ_FocusOut,//�����
  PTZ_FocusOutStop,//����ֹͣ

  PTZ_LightOn,//�ƹ�С
  PTZ_LightOff,//�ƹ��
  PTZ_RainBrushOn,//��ˢ��
  PTZ_RainBrushOff,//��ˢ��
  PTZ_AutoOn,//�Զ���ʼ  //Rotation
  PTZ_AutoOff,//�Զ�ֹͣ

  PTZ_TrackOn,
  PTZ_TrackOff,
  PTZ_IOOn,
  PTZ_IOOff,

  PTZ_ClearPoint,//��̨��λ
  PTZ_SetPoint,//�趨��̨��λ
  PTZ_GotoPoint,//��̨��λ
  PTZ_SetPointRotation,
  PTZ_SetPoint_Left,
  PTZ_GotoPoint_Left,
  PTZ_SetPoint_Right,
  PTZ_GotoPoint_Right,
  PTZ_DayNightMode,//���졢ҹ��ģʽ 0���� 1ҹ��
  PTZ_Max
}TPTZCmd;
//-----------------------------------------------------------------------------
typedef enum TPTZProtocol {                      //��̨Э�� sizeof 4
  Pelco_P               =0,
  Pelco_D               =1,
  Protocol_Custom       =2,
}TPTZProtocol;

typedef struct TPTZPkt {                         //PTZ ��̨����  sizeof 108
  TPTZCmd PTZCmd;                                    //=PTZ_None Ϊ͸������
  union {
    struct {
      TPTZProtocol Protocol;                         //��̨Э��
      i32 Address;                                   //��̨��ַ
      i32 PanSpeed;                                  //��̨�ٶ�
      i32 Value;                                     //������Ԥ��λ
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
typedef struct TPlayLivePkt {                    //�����ֳ���//sizeof 20
  u32 VideoChlMask;//ͨ������ 
  //  31 .. 19 18 17 16   15 .. 03 02 01 00
  //         0  0  0  0          0  0  0  1     
  u32 AudioChlMask;
  //  31 .. 19 18 17 16   15 .. 03 02 01 00
  //         0  0  0  0          0  0  0  1
  i32 Value;                                     //Value=0��������֡��Value=1ֻ������ƵI֡
  //begin add at 2009/09/02
  u32 SubVideoChlMask;
  //11  i32 IsRecvAlarm;                               //0�����豸���� 1�������豸����
  //end add
  i32 Flag;                                      //���� 
}TPlayLivePkt;
//-----------------------------------------------------------------------------
typedef struct TPlayBackPkt {                    //sizeof 20
  i32 Chl;
  i32 FileType;                                  //0:��ͨ¼Ӱ 1:����¼Ӱ 2ý���ļ�
  i32 StartTime;                                 //��ʼʱ��
  i32 EndTime;                                   //����ʱ��
  i32 Flag;
}TPlayBackPkt;
//-----------------------------------------------------------------------------
typedef enum TMsgID {
  Msg_None,
  Msg_Login,//�û���¼
  Msg_PlayLive,//��ʼ�����ֳ�
  Msg_StartPlayRecFile,//����¼Ӱ�ļ�
  Msg_StopPlayRecFile,//ֹͣ����¼Ӱ�ļ�
  Msg_GetRecFileLst,//ȡ��¼Ӱ�ļ��б�
  Msg_GetDevRecFileHead,//ȡ���豸�ļ��ļ�ͷ��Ϣ
  Msg_StartUploadFile,//��ʼ�ϴ��ļ�
  Msg_AbortUploadFile,//ȡ���ϴ��ļ�
  Msg_StartUploadFileEx,//��ʼ�ϴ��ļ�tftp

  Msg_StartTalk,//��ʼ�Խ�
  Msg_StopTalk,//ֹͣ�Խ�
  Msg_PlayControl,//���ſ���
  Msg_PTZControl,//��̨����
  Msg_Alarm,//����
  Msg_ClearAlarm,//�رվ���
  Msg_GetTime,//ȡ��ʱ��
  Msg_SetTime,//����ʱ��
  Msg_SetDevReboot,//�����豸
  Msg_SetDevLoadDefault,//ϵͳ�ص�ȱʡ���� Pkt.Value= 0 ���ָ�IP, Pkt.Value= 1 �ָ�IP

  Msg_DevSnapShot,//�豸����
  Msg_DevStartRec,//�豸��ʼ¼��
  Msg_DevStopRec,//�豸ֹͣ¼��

  Msg_GetColors,//ȡ�����ȡ��Աȶȡ�ɫ�ȡ����Ͷ�
  Msg_SetColors,//�������ȡ��Աȶȡ�ɫ�ȡ����Ͷ�
  Msg_SetColorDefault,

  Msg_GetMulticastInfo,
  Msg_SetMulticastInfo,

  Msg_GetAllCfg,//ȡ����������
  Msg_SetAllCfg,//������������
  Msg_GetDevInfo,//ȡ���豸��Ϣ
  Msg_SetDevInfo,//�����豸��Ϣ
  Msg_GetUserLst,//ȡ���û��б�
  Msg_SetUserLst,//�����û��б�
  Msg_GetNetCfg,//ȡ����������
  Msg_SetNetCfg,//������������
  Msg_WiFiSearch,
  Msg_GetWiFiCfg,//ȡ��WiFi����
  Msg_SetWiFiCfg,//����WiFi����
  Msg_GetVideoCfg,//ȡ����Ƶ����
  Msg_SetVideoCfg,//������Ƶ����
  Msg_GetAudioCfg,//ȡ����Ƶ����
  Msg_SetAudioCfg,//������Ƶ����
  Msg_GetHideArea,//��¼
  Msg_SetHideArea,//��¼
  Msg_GetMDCfg,//�ƶ��������
  Msg_SetMDCfg,//�ƶ��������
  Msg_GetDiDoCfg__Disable,
  Msg_SetDiDoCfg__Disable,
  Msg_GetAlmCfg,//ȡ��Alarm����
  Msg_SetAlmCfg,//����Alarm����
  Msg_GetRS485Cfg__Disable,
  Msg_SetRS485Cfg__Disable,
  Msg_GetDiskCfg,//����Disk����
  Msg_SetDiskCfg,//����Disk����
  Msg_GetRecCfg,//ȡ��¼Ӱ����
  Msg_SetRecCfg,//����¼Ӱ����
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
typedef struct TPlanRecPkt {                        //�ų�¼Ӱ�ṹ sizeof 224
  struct {
    bool Active;
    u8 start_h;    //ʱ 0-23
    u8 start_m;    //�� 0-59
    u8 stop_h;     //ʱ 0-23��ȡ�豸�Ƿ��о�������
    u8 stop_m;     //�� 0-59
    bool IsRun;      //��ǰ�ƻ��Ƿ�����
    u8 Flag1;
    u8 Flag2;
  }Week[7][RECPLANLST];                                 //��һ���������� ÿ�����4������
}TPlanRecPkt;
//-----------------------------------------------------------------------------
typedef enum TRecStyle {
  rs_RecManual,
  rs_RecAuto,
  rs_RecPlan,
  rs_RecAlarm
}TRecStyle;

typedef struct TRecCfgPkt {                      //¼Ӱ���ð� sizeof 260
  i32 ID;
  i32 DevID;//PC�˹������ֻ���ڴ洢���ݿ����豸��š��豸�˱���
  i32 Chl;
  bool IsLoseFrameRec;//�Ƿ�֡¼Ӱ
  u8 RecStreamType;//0 ������ 1 ������
  u8 Reserved;
  bool IsRecAudio;//¼����Ƶ ��û���õ�
  u32 Rec_AlmPrevTimeLen;//��ǰ¼Ӱʱ��     5 s
  u32 Rec_AlmTimeLen;//����¼Ӱʱ��        10 s
  u32 Rec_NmlTimeLen;//һ��¼Ӱ�ָ�ʱ��   600 s
  TRecStyle RecStyle;//¼Ӱ����
  TPlanRecPkt Plan;
  i32 bFlag;
}TRecCfgPkt;
//-----------------------------------------------------------------------------
/*
typedef struct TDiskCfgPkt_old {   //sizeof 888
  i32 IsFillOverlay;      // �Ƿ񸲸������ļ�(false��true,falseΪ������,trueΪ����,ȱʡΪfalse)
  char20 CurrentDiskName; // ��ǰ����¼Ӱ�Ĵ������� 0..7, ReadOnly
  struct {
    char20 DiskName;      // ���� 
    i32 Active;           // �Ƿ���Ϊ¼Ӱ���� false or true
    u32 DiskSize;       // M ReadOnly
    u32 FreeSize;       // M
    u32 MinFreeSize;    // M
  }Disk[24];
}TDiskCfgPkt_old;
*/
//-----------------------------------------------------------------------------
typedef struct TDiskCfgPkt {   //sizeof 60
  char Reserved1[24];
  char20 DiskName;      // ���� 
  i32 Active;           // �Ƿ���Ϊ¼Ӱ���� false or true
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

      bool ethLinkStatus;      //���������Ƿ�����

      u8 HardType;      //Ӳ������
      u32 VideoTypeMask;// 8
      u64 StandardMask;//16
      //u32 AutioTypeMask;//20��δ�õ�
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

typedef struct TDevInfoPkt {                     //�豸��Ϣ��sizeof 180
  char DevModal[12];                             //�豸�ͺ�  ='7xxx'
  u32 SN;
  i32 DevType;                                   //�豸����
  char20 SoftVersion;                            //����汾
  char20 FileVersion;                            //�ļ��汾
  char20 DevName;                                //�豸��ʶ
  char40 DevDesc;                                //�豸��ע
  struct TAxInfo Info;

  i32 VideoChlCount;
  u8 AudioChlCount;
  u8 DiChlCount;
  u8 DoChlCount;
  u8 RS485DevCount;
  signed char TimeZone;
  u8 MaxUserConn;                               //����û������� default 10
  u8 OEMType;
  bool DoubleStream;                              //�Ƿ�˫���� add at 2009/09/02
  struct {
    u8 w;//TTaskDayType;
    u8 start_h;//ʱ 0-23
    u8 start_m;//�� 0-59
    u8 Days;
  }RebootHM;
  //i32 Flag;
  i32 ProcRunningTime;
}TDevInfoPkt;
//-----------------------------------------------------------------------------
typedef struct TWiFiSearchPkt {//sizeof 40
  char32 SSID;
  u8 Siganl;//�ź� 0..100 ���� �� һ�� �� ����
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
typedef struct TWiFiCfgPkt {                     //�������ð� sizeof 200
  bool Active;
  bool IsAPMode;//sta=0  ap=1
  u8 Reserved[2];
  char SSID_AP[30];
  char Password_AP[30];

  char32 SSID_STA;
  i32 Channel;//Ƶ��1..14 default 1=Auto
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
typedef struct TNetCfgPkt {                      //�豸�������ð�sizeof 372
  i32 DataPort;                                   //�������ݶ˿�
  i32 rtspPort;                                  //rtsp�˿�
  i32 HttpPort;                                  //http��ҳ�˿�
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
    //bool IsActiveDHCPServer; //�Ƿ�����DHCPServer
    char Flag[4];
  }Lan;
  struct {
    i32 Active;
#define DDNS_3322     0
#define DDNS_dynDNS   1
#define DDNS_MyDDNS   2
#define DDNS_9299     3
    i32 DDNSType;                               //0=3322.ORG 1=dynDNS.ORG 2=MyDDNS 3=9299.org
    char40 DDNSDomain;                           //��DDNS SERVER IP
    union {
      struct {
        char40 HostAccount;                          //DDNS�ʺ�
        char40 HostPassword;                         //DDNS����
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

typedef struct TRS485CfgPkt__Disable {                       //485ͨ�Ű� sizeof 280
  i32 Chl;
  TBaudRate BPS;//������
  TDataBit DataBit;//����λ
  TParityCheck ParityCheck;//��żУ��
  TStopBit StopBit;//ֹͣλ
  struct {
    u8 Address;
    u8 PTZProtocol;//��̨Э��
    u8 PTZSpeed;
    u8 Reserved;
  }Lst[32];//��Ӧ��Ӧ����Ƶͨ��

  //char PTZNameLst[128];//��ʱδ�õ� format "Pelco_P\nPelco_D\nProtocol_Custom"

  i32 PTZCount;
  char20 PTZNameLst[6];
  i32 Reserved;

  i32 Flag;
}TRS485CfgPkt__Disable;

//-----------------------------------------------------------------------------
typedef struct TColorsPkt {
  i32 Chl;
  u8  Brightness;                               //����   0-255
  u8  Contrast;                                 //�Աȶ� 0-255
  u8  Hue;                                      //ɫ��   0-255
  u8  Saturation;                               //���Ͷ� 0-255
  u8  Sharpness;                                //���Ͷ� 0-255
  u8 Reserved[3];
}TColorsPkt;
//-----------------------------------------------------------------------------
typedef struct TMulticastInfoPkt {               //�ಥ������Ϣ��sizeof 556->588->628
  TDevInfoPkt DevInfo;
  TNetCfgPkt NetCfg;
  i32 Flag;// sendfrom client=0 sendfrom device=1
  TWiFiCfgPkt WiFiCfg;
  Tp2pCfgPkt p2pCfg;
  TVideoCfgPkt VideoCfg;
  TAudioCfgPkt AudioCfg;
  char20 UserName;                               //�û��� admin���ܸ���
  char20 Password;                               //����
}TMulticastInfoPkt;
//-----------------------------------------------------------------------------
#define Head_CmdPkt           0xAAAAAAAA         //�������ͷ
#define Head_VideoPkt         0xBBBBBBBB         //��Ƶ����ͷ
#define Head_AudioPkt         0xCCCCCCCC         //��Ƶ����ͷ
#define Head_TalkPkt          0xDDDDDDDD         //�Խ�����ͷ
#define Head_UploadPkt        0xEEEEEEEE         //�ϴ���
#define Head_DownloadPkt      0xFFFFFFFF         //���ذ�//δ��
#define Head_CfgPkt           0x99999999         //���ð�
#define Head_SensePkt         0x88888888         //����//δ��
#define Head_MotionInfoPkt    0x77777777         //�ƶ���ֵⷧ��ͷ
//-----------------------------------------------------------------------------
typedef struct THeadPkt{                         //sizeof 8
  u32 VerifyCode;                              //У���� = 0xAAAAAAAA 0XBBBBBBBB 0XCCCCCCCC 0XDDDDDDDD 0XEEEEEEEE
  u32 PktSize;                                 //������С=1460-8
}THeadPkt;
//-----------------------------------------------------------------------------
typedef struct TTalkHeadPkt {                    //�Խ�����ͷ  sizeof 32
  u32 VerifyCode;                              //У���� = 0XDDDDDDDD
  u32 PktSize;                                 
  char20 TalkIP;
  u32 TalkPort;
}TTalkHeadPkt;
//-----------------------------------------------------------------------------
typedef struct TFrameInfo { //¼Ӱ�ļ�����֡ͷ  16 u8
  i64 FrameTime;                               //֡ʱ�䣬time_t*1000000 +us
  u8 Chl;                                      //ͨ�� 0..15 ��Ӧ 1..16ͨ��
  bool IsIFrame;                                 //�Ƿ�I֡
  u16 FrameID;                                  //֡����,��0 ��ʼ,��65535���ܶ���ʼ
  union {
    u32 PrevIFramePos;                         //ǰһ��I֡�ļ�ָ�룬�����ļ��д�������������
    i32 StreamType;                              //�����˫�������ֳ��� 0Ϊ������ 1Ϊ������ add at 2009/09/02
    u32 DevID;                                 //�����Ӷ��豸ʱ�õ����ݱ���
  };
}TFrameInfo;

typedef struct TDataFrameInfo { //¼Ӱ�ļ�����֡ͷ  24 u8
  THeadPkt Head;
  TFrameInfo Frame;
}TDataFrameInfo;
//-----------------------------------------------------------------------------
typedef struct TMotionInfoPkt { //�ƶ���ֵⷧ  sizeof 4
  u8 AreaIndex;
  u8 ActiveNum;
  u8 Sensitive;
  u8 Tag;
}TMotionInfoPkt;
//-----------------------------------------------------------------------------
//�������
#define ERR_FAIL           0
#define ERR_OK             1
#define ERR_MAXUSERCONN    10001//�����û�����������趨
//-----------------------------------------------------------------------------
typedef struct TLoginPkt {                       //�û���¼�� sizeof 252->892
  char20 UserName;                               //�û�����
  char20 Password;                               //�û�����
  char20 DevIP;                                  //Ҫ���ӵ��豸IP,�� host
  i32 UserGroup;                                 //Guest=1 Operator=2 Administrator=3  
  i32 SendSensePkt;                              //�Ƿ������� 0������ 1����
  TDevInfoPkt DevInfoPkt;
  //2009-05-12 add begin
  TVideoFormat v[4];
  TAudioFormat a[4];
  //2009-05-12 add end
  //i32 Flag;//�����Ƿ����ߡ�0�����ߡ�1����
#define SENDFROM_CLIENT    1
#define SENDFROM_NVRMOBILE 0
  i32 SendFrom;// (x2 0=�ֻ�NVR 1=�ͻ��� )
}TLoginPkt;
//-----------------------------------------------------------------------------
typedef struct TCmdPkt {                         //sizeof 1460-8
  u32 PktHead;                                 //��ͷУ���� =Head_CmdPkt 0xAAAAAAAA
  TMsgID MsgID;                                  //��Ϣ
  u32 Session;                                 //�����û���ɣ������������¼��ʱ��ֵΪ0�����ڷ��ص�¼����Session  //����Ϊ�����ڲ�ͨѶ��ʱ����ֵ����
  u32 Value;                                   //���Ի򷵻�ֵ 0 or 1 or ErrorCode
  union {
    char ValueStr[1460 - 4*4 - 8];
    struct TLoginPkt LoginPkt;                   //��¼��
    struct TPlayLivePkt LivePkt;                 //�����ֳ���
    struct TRecFilePkt RecFilePkt;               //�ط�¼Ӱ��
    struct TPTZPkt PTZPkt;                       //��̨����̨
    struct TRecFileLst RecFileLst;
    struct TPlayCtrlPkt PlayCtrlPkt;             //�ط�¼Ӱ���ư�

    struct TAlmSendPkt AlmSendPkt;               //�����ϴ���
    struct TDevInfoPkt DevInfoPkt;               //�豸��Ϣ��
    struct TNetCfgPkt NetCfgPkt;                 //�豸�������ð�
    struct TWiFiCfgPkt WiFiCfgPkt;               //�����������ð�
    struct TDiskCfgPkt DiskCfgPkt;               //�������ð�
    struct TUserCfgPkt UserCfgPkt;               //�û����ð�
    struct TRecCfgPkt RecCfgPkt;                 //¼Ӱ���ð�
    struct TMDCfgPkt MDCfgPkt;                   //�ƶ�����--��ͨ��
//    struct TDiDoCfgPkt DiDoCfgPkt;               //DIDO���ð� 528
    struct TDoControlPkt DoControlPkt;           //DO���ư�    
    struct THideAreaCfgPkt HideAreaCfgPkt;       //����¼Ӱ�����--��ͨ��
    struct TAlmCfgPkt AlmCfgPkt;                 //�������ð�
    struct TVideoCfgPkt VideoCfgPkt;             //��Ƶ���ð�--��ͨ��
    struct TAudioCfgPkt AudioCfgPkt;             //��Ƶ���ð�--��ͨ��    
    struct TRecFileHead FileHead;                //ȡ���豸�ļ��ļ�ͷ��Ϣ
    struct TFilePkt FilePkt;                     //�ϴ��ļ���
    //struct TRS485CfgPkt RS485CfgPkt;             //485ͨ�Ű�--��ͨ��
    struct TColorsPkt Colors;                    //����ȡ�����ȡ��Աȶȡ�ɫ�ȡ����Ͷ�

    struct TMulticastInfoPkt MulticastInfo;      //�ಥ��Ϣ

    struct TFTPCfgPkt FTPCfgPkt;
    struct TSMTPCfgPkt SMTPCfgPkt;
    struct TBatchCfgPkt BatchCfgPkt;             //�����޸�����
    struct TWiFiSearchPkt WiFiSearchPkt[30];
    struct Tp2pCfgPkt p2pCfgPkt;
  };
}TCmdPkt;
//-----------------------------------------------------------------------------
typedef struct TNetCmdPkt {                      //���緢�Ͱ� sizeof 1460
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
  char DevModal[8];                             //�豸�ͺ�  ='7xxx'
  u32 SN;
  char16 SoftVersion;                            //����汾
  char20 DevName;                                //�豸��ʶ
  u64 StandardMask;//16
  u64 SubStandardMask;//32
  u8 DevType;                                   //�豸����
  bool ExistWiFi;
  bool ExistSD;
  bool ethLinkStatus;      //���������Ƿ�����
  bool wifiStatus;
  bool upnpStatus;
  bool WlanStatus;
  bool p2pStatus;
  u8 HardType;      //Ӳ������
  signed char TimeZone;
  bool DoubleStream;                              //�Ƿ�˫���� add at 2009/09/02
  bool ExistFlash;
  i32 Reserved;
}TNewDevInfo;

typedef struct TNewNetCfg{//sizeof 132
  u16 DataPort;                                   //�������ݶ˿�
  u16 rtspPort;                                  //rtsp�˿�
  u16 HttpPort;                                  //http��ҳ�˿�
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
  char40 DDNSDomain;                           //��DDNS SERVER IP
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
  u8 Channel;//Ƶ��1..14 default 1=Auto
  u8 EncryptType;//(Encrypt_None,Encrypt_WEP,Encrypt_WPA);
  u8 Auth;//WEP(0=AUTO 1=OPEN 2=SHARED)  WPA(0=AUTO 1=WPA-PSK 2=WPA2-PSK)
  u8 Enc;//WPA(0=AUTO 1=TKIP 2=AES)
  i32 Reserved;
}TNewwifiCfg;

typedef struct TNewp2pCfg{//sizeof 64
  bool ActiveP2P;
  u8 StreamType;//0 ������ 1 ������
  u8 p2pType;   //tutk=0 self=1
  char UID[21];
  char20 Password;
  i32 SvrIP[4];
  i32 Reserved;
}TNewp2pCfg;

typedef struct TNewVideoCfg {//sizeof 16
  u8 StandardEx0;//TStandardEx
  u8 FrameRate0;                                //֡�� 1-30 MAX:PAL 25 NTSC 30
  u16 BitRate0;
  u8 StandardEx1;//TStandardEx
  u8 FrameRate1;//֡�� 1-30 MAX:PAL 25 NTSC 30
  u16 BitRate1;//���� 64K 128K 256K 512K 1024K 1536K 2048K 2560K 3072K
  bool IsMirror;                           //ˮƽ��ת false or true
  bool IsFlip;                             //��ֱ��ת false or true
  bool IsShowFrameRate;
  u8 Flag_00008;
//  i32 Reserved;
  u8 BitRateType0;                              //0������CBR 1������VBR
  u8 BitRateQuant0; 
  u8 BitRateType1;                              //0������CBR 1������VBR
  u8 BitRateQuant1; 
}TNewVideoCfg;

typedef struct TNewAudioCfg{//sizeof 12
  bool  ActiveAUDIO;                                   //�Ƿ���������
  u8 InputTypeAUDIO;                                 //0 MIC����, 1 LINE����
  u8 VolumeLineIn;
  u8 VolumeLineOut;
  u8 nChannels;                               //������=0 ������=1
  u8 wBitsPerSample;                          //number of bits per sample of mono data 
  u16 nSamplesPerSec;                          //������
  u8 wFormatTag;
  u8 Reserved[3];
}TNewAudioCfg;

typedef struct TNewUserCfg {//sizeof 128
  char20 UserName[3];                               //�û��� admin���ܸ���
  char20 Password[3];                               //����
  u8 Authority[3];                                 //3Ϊadmin
  u8 Reserved;
  i32 Reserved1;
}TNewUserCfg;

typedef struct TNewDIAlm {//sizeof 12
  bool ActiveDI;
  u8 Reserved;
  bool IsAlmRec;
  bool IsFTPUpload;
  bool ActiveDO;//DI����DOͨ�� false close
  bool IsSendEmail;
  u8 Reserved1;// >0ΪԤ��λ
  u8 AlmOutTimeLen;//�������ʱ��(��) 0 ..255 s
  //struct TTaskhm hm;
  i32 Reserved2;
}TNewDIAlm;

typedef struct TNewMDAlm {//sizeof 28                       //�ƶ�����
  bool ActiveMD;
  u8 Sensitive;                              //��������� 0-255
  bool IsAlmRec;
  bool IsFTPUpload;
  bool ActiveDO;//DI����DOͨ�� false close
  bool IsSendEmail;
  u8 Reserved2;
  u8 AlmOutTimeLen;                    //�������ʱ��(��) 0 ..255 s
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
  bool ActiveDO;//DI����DOͨ�� false close
  bool IsSendEmail;
  u8 Reserved1;// >0ΪԤ��λ
  u8 AlmOutTimeLen;                    //�������ʱ��(��) 0 ..255 s
  //struct TTaskhm hm;
  i32 Reserved2;
}TNewSoundAlm;

typedef struct TNewRecCfg{//sizeof 236                      //¼Ӱ���ð�
  u8 RecStreamType;//0 ������ 1 ������
  bool IsRecAudio;//¼����Ƶ ��û���õ�
  u8 RecStyle;//¼Ӱ����
  u8 Reserved;
  TPlanRecPkt Plan;
  u16 Rec_AlmTimeLen;//����¼Ӱʱ��        10 s
  u16 Rec_NmlTimeLen;//һ��¼Ӱ�ָ�ʱ��   600 s
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
  u32 VerifyCode;//У���� = 0xAAAAAAAA
  u8 MsgID;//TMsgID
  u8 Result;
  u16 PktSize;
  union {
    i32 Value;
    char Buf[1000];
    struct TNewDevCfg NewDevCfg;

    struct TLoginPkt LoginPkt;                   //��¼�� 892
    struct TPlayLivePkt LivePkt;                 //�����ֳ���
    struct TRecFilePkt RecFilePkt;               //�ط�¼Ӱ��
    struct TPTZPkt PTZPkt;                       //��̨����̨
    struct TPlayCtrlPkt PlayCtrlPkt;             //�ط�¼Ӱ���ư�

    struct TAlmSendPkt AlmSendPkt;               //�����ϴ���
    struct TDevInfoPkt DevInfoPkt;               //�豸��Ϣ��
    struct TNetCfgPkt NetCfgPkt;                 //�豸�������ð�
    struct TWiFiCfgPkt WiFiCfgPkt;               //�����������ð�
    struct TDiskCfgPkt DiskCfgPkt;               //�������ð� 888
    struct TRecCfgPkt RecCfgPkt;                 //¼Ӱ���ð�
    struct TMDCfgPkt MDCfgPkt;                   //�ƶ�����--��ͨ��
    struct TDoControlPkt DoControlPkt;           //DO���ư�    
    struct THideAreaCfgPkt HideAreaCfgPkt;       //����¼Ӱ�����--��ͨ��
    struct TAlmCfgPkt AlmCfgPkt;                 //�������ð�
    struct TVideoCfgPkt VideoCfgPkt;             //��Ƶ���ð�--��ͨ��
    struct TAudioCfgPkt AudioCfgPkt;             //��Ƶ���ð�--��ͨ��    
    struct TRecFileHead FileHead;                //ȡ���豸�ļ��ļ�ͷ��Ϣ
    struct TFilePkt FilePkt;                     //�ϴ��ļ���
    struct TColorsPkt Colors;                    //����ȡ�����ȡ��Աȶȡ�ɫ�ȡ����Ͷ�
    struct TFTPCfgPkt FTPCfgPkt;
    struct TSMTPCfgPkt SMTPCfgPkt;
    struct Tp2pCfgPkt p2pCfgPkt;
  };  
}TNewCmdPkt;

//#pragma option pop //end C++Builder enum 4 u8

#endif //end Ax_protocol_H

