//-----------------------------------------------------------------------------
// Author      : ��첨
// Date        : 2012.01.18
// Version     : V 1.00
// Description : 
//-----------------------------------------------------------------------------
#ifndef TSSkt_H
#define TSSkt_H

#include "axdll.h"
#include "list.h"
#include "common.h"
#include "th_protocol.h"

typedef struct TSktConnPkt{
  char RecvBuf[1024*16];//Ҫ���ڵ�һ
  i32 RecvLen;
  bool IsRecvVerifyCode;
  bool IsRecvPktSize;
  THandle SktHandle;
  struct sockaddr_in CltAddr;
  time_t LoginTime;
  THandle Session;//��עΪ��¼���
  struct TPlayLivePkt LivePkt; 
  i32 GroupType;//����
  struct TFilePkt FilePkt;//�ϴ��ļ��ṹ
  struct TRecFilePkt RecFilePkt;
  struct TPlayCtrlPkt PlayCtrlPkt;
}TSktConnPkt;
//*****************************************************************************
TSktConnPkt* IndexOfSktHandle(TList* lst, THandle SocketHandle);

typedef void(*TOnConnectEvent)(TSktConnPkt* SktConnPkt); //�����¼�
typedef void(*TOnRecvEvent)(TSktConnPkt* SktConnPkt); //��ȡ�¼�
typedef void(*TOnDisConnEvent)(TSktConnPkt* SktConnPkt); //�Ͽ��¼�

typedef struct TSSktParam
{
  TOnConnectEvent OnConnectEvent;
  TOnDisConnEvent OnDisConnEvent;
  TOnRecvEvent OnRecvEvent;
  i32 LocalPort;

  TList* SktConnLst;
  THandle SocketHandle;
  fd_set fdset;
  pthread_t tHandle;

}TSSktParam;

bool sskt_Init(TSSktParam* Param);
bool sskt_Free(TSSktParam* Param);
//-----------------------------------------------------------------------------
typedef void(*TOnUDPRecvEvent)(char* Buf, i32 BufLen);//must
typedef struct TudpParam
{
  TOnUDPRecvEvent OnRecvEvent;
  i32 Port;
//multicast param
  bool IsMulticast;
  i32 TTL;
  char* LocalIP;
  char* MultiIP;
//multicast param
  struct sockaddr_in FromAddr;
  pthread_t tHandle; //SSkt�߳̾��
  THandle SocketHandle;
  THandle Flag;
}TudpParam;

bool udp_Init(TudpParam* Param);
bool udp_Free(TudpParam* Param);

#endif
