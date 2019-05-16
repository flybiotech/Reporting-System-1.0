//-----------------------------------------------------------------------------
// Author      : �������Ϸ��������ܿƼ����޹�˾
// Date        : 2013.04.20
// Version     : V 2.02
// Description : www.southipcam.com
//-----------------------------------------------------------------------------
#ifndef thSDK_H
#define thSDK_H

#include "cm_types.h"
#include "axdll.h"

#include "IOTCAPIs.h"
#include "AVAPIs.h"
#include "AVFRAMEINFO.h"
#include "AVIOCTRLDEFs.h"

#define IsUsedP2P

extern bool IsExit;
//*****************************************************************************
//������غ���
#define NET_TIMEOUT               5000  // ms
#define NET_CONNECT_TIMEOUT       3000  // ms

//*****************************************************************************

//-----------------------------------------------------------------------------
typedef void(TSearchDevCallBack)(
                                 u32 SN,            //
                                 i32 DevType,       //�豸����
                                 i32 VideoChlCount, //ͨ������
                                 i32 DataPort,      //���ݶ˿�
                                 i32 HttpPort,      //WEB�˿�
                                 char* DevName,     //�豸����
                                 char* DevIP,       //�豸IP
                                 char* DevMAC,      //�豸MAC��ַ
                                 char* SubMask,     //�豸��������
                                 char* Gateway,     //�豸����
                                 char* DNS1,        //�豸DNS
                                 char* DDNSServer,
                                 char* DDNSHost,    //DDNS����
                                 char* UID          //P2P��ʽUID
                                 );

typedef void(TAVCallBack)(
                          TDataFrameInfo* PInfo,    //����Ƶ֡ͷ��Ϣ
                          char* Buf,                //����Ƶ����ǰ֡����
                          i32 Len,                  //���ݳ���
                          void* UserCustom          //�û��Զ�������
                          );

typedef void(TAlmCallBack)(
                           i32 AlmType,             //�������ͣ��μ�TAlmType
                           i32 AlmTime,             //����ʱ��time_t
                           i32 AlmChl,              //����ͨ��
                           void* UserCustom         //�û��Զ�������
                           );


bool GetWidthHeightFromStandard(i32 Value, i32* w, i32* h);
i32 GetStandardFromWidthHeight(i32 w, i32 h);
//-----------------------------------------------------------------------------
bool thNet_Init(THandle* NetHandle);
/*-----------------------------------------------------------------------------
������������ʼ�����粥��
����˵����
NetHandle:����������
DevType:�豸����
X1=dt_Devx1=11
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_SetCallBack(THandle NetHandle, TAVCallBack avEvent, TAlmCallBack AlmEvent, void* UserCustom);
/*-----------------------------------------------------------------------------
�������������粥�����ûص�����
����˵����
NetHandle:����������thNet_Init����
avEvent:��Ƶ��Ƶ���ݻص�����
AlmEvent:�豸�����ص�����
UserCustom:�û��Զ�������
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_Free(THandle* NetHandle);
/*-----------------------------------------------------------------------------
�����������������粥��
����˵����
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_Connect(THandle NetHandle, char* UserName, char* Password, char* DevIP, i32 DataPort, u32 TimeOut, i32 IsCreateRecvThread);
/*-----------------------------------------------------------------------------
�������������������豸
����˵����
NetHandle:����������thNet_Init����
UserName:�����ʺ�
Password:��������
DevIP:�豸IP
DataPort:�豸��ת���������˿�
TimeOut:���ӳ�ʱ����λms,ȱʡ 3000ms
IsCreateRecvThread:�Ƿ񴴽�������ȡ�߳�
�� �� ֵ���ɹ�����true
          ʧ�ܷ���false
------------------------------------------------------------------------------*/
#ifdef IsUsedP2P
bool thNet_Connect_P2P(THandle NetHandle, i32 p2pType, char* p2pUID, char* p2pPSD, u32 TimeOut, i32 IsCreateRecvThread);
#endif
/*-----------------------------------------------------------------------------
�������������������豸��P2P��ʽ
����˵����
NetHandle:����������thNet_Init����
UID:�豸ID
TimeOut:���ӳ�ʱ����λms,ȱʡ 3000ms
IsCreateRecvThread:�Ƿ񴴽�������ȡ�߳�
�� �� ֵ���ɹ�����true
          ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_DisConn(THandle NetHandle);
/*-----------------------------------------------------------------------------
�����������Ͽ������豸����
����˵����
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_IsConnect(THandle NetHandle);
/*-----------------------------------------------------------------------------
�����������豸�Ƿ�����
����˵����
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_Play(THandle NetHandle, u32 VideoChlMask, u32 AudioChlMask, u32 SubVideoChlMask);
/*-----------------------------------------------------------------------------
������������ʼ����
����˵����
NetHandle:����������thNet_Init����
VideoChlMask:ͨ�����룬
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
AudioChlMask:ͨ�����룬
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
SubVideoChlMask:������ͨ������
bit: 31 .. 19 18 17 16   15 .. 03 02 01 00
0  0  0  0          0  0  0  1
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_Stop(THandle NetHandle);
/*-----------------------------------------------------------------------------
����������ֹͣ����
����˵����
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_GetVideoCfg(THandle NetHandle, i32* VideoType, i32* Width0, i32* Height0, i32* FrameRate0, i32* Width1, i32* Height1, i32* FrameRate1);
/*-----------------------------------------------------------------------------
������������ȡ��Ƶ����1
����˵����
NetHandle:����������thNet_Init����
Standard :NTSC=0 PAL=1  60HZ=0 50HZ=1
VideoType:MPEG4=0 MJPEG=1 H264=2
IsMirror :ͼ���Ƿ���
IsFlip   :ͼ���Ƿ�ת
Width0   :��������
Height0  :��������
FrameRate0:������֡��
BitRate0:����������
Width1   :��������
Height1  :��������
FrameRate1:������֡��
BitRate1:����������
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_SetTalk(THandle NetHandle, char* Buf, i32 BufLen);
/*-----------------------------------------------------------------------------
�������������ͶԽ���Ƶ����
����˵����
NetHandle:����������thNet_Init����
��Ƶ�ɼ���ʽ����thNet_GetAudioCfg��ȡ
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thSearch_Init(TSearchDevCallBack SearchEvent);
/*-----------------------------------------------------------------------------
������������ʼ����ѯ�豸
����˵����
SearchEvent:��ѯ�豸�ص�����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thSearch_SetWiFiCfg(char* LocalIP, char* SSID, char* Password);
bool thSearch_SearchDevice(char* LocalIP);
/*-----------------------------------------------------------------------------
������������ʼ��ѯ�豸
LocalIP:����ı���IP��ȱʡΪNULL
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thSearch_Free(void); 
/*-----------------------------------------------------------------------------
�����������ͷŲ�ѯ�豸
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/

//*****************************************************************************
bool thNet_CreateRecvThread(THandle NetHandle);
/*-----------------------------------------------------------------------------
����������������ȡ�����߳�
����˵����ֻ����thNet_Connect��IsCreateRecvThreadΪfalseʱʹ��
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_RemoteFilePlay(THandle NetHandle, char* FileName);
/*-----------------------------------------------------------------------------
������������ʼ����Զ���ļ�
����˵����
NetHandle:����������thNet_Init����
FileName:�����Զ��¼���ļ���
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_RemoteFileStop(THandle NetHandle);
/*-----------------------------------------------------------------------------
����������ֹͣ����Զ���ļ�
����˵����
NetHandle:����������thNet_Init����
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_RemoteFilePlayControl(THandle NetHandle, i32 PlayCtrl, i32 Speed, i32 Pos);
/*-----------------------------------------------------------------------------
����������Զ���ļ����ſ���
����˵����
NetHandle:����������thNet_Init����
PlayCtrl:   PS_None               =0,                 //��
            PS_Play               =1,                 //����
            PS_Pause              =2,                 //��ͣ
            PS_Stop               =3,                 //ֹͣ
            PS_FastBackward       =4,                 //����
            PS_FastForward        =5,                 //���
            PS_StepBackward       =6,                 //����
            PS_StepForward        =7,                 //����
            PS_DragPos            =8,                 //�϶�
Speed:���PlayCtrl=PS_StepBackward, PS_FastForward ���򱣴������˱��� 1 2 4 8 16 32����
Pos:���PlayCtrl=PS_DragPos���򱣴��ļ��ļ�λ��Pos
�� �� ֵ���ɹ�����true��ʧ�ܷ���false
------------------------------------------------------------------------------*/
bool thNet_HttpGet(THandle NetHandle, char* url, char* Buf, i32* BufLen);
bool thNet_HttpGetStop(THandle NetHandle);

#endif 
