//-----------------------------------------------------------------------------
// Author      : 朱红波
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
  char RecvBuf[1024*16];//要放在第一
  i32 RecvLen;
  bool IsRecvVerifyCode;
  bool IsRecvPktSize;
  THandle SktHandle;
  struct sockaddr_in CltAddr;
  time_t LoginTime;
  THandle Session;//标注为登录与否
  struct TPlayLivePkt LivePkt; 
  i32 GroupType;//分组
  struct TFilePkt FilePkt;//上传文件结构
  struct TRecFilePkt RecFilePkt;
  struct TPlayCtrlPkt PlayCtrlPkt;
}TSktConnPkt;
//*****************************************************************************
TSktConnPkt* IndexOfSktHandle(TList* lst, THandle SocketHandle);

typedef void(*TOnConnectEvent)(TSktConnPkt* SktConnPkt); //连接事件
typedef void(*TOnRecvEvent)(TSktConnPkt* SktConnPkt); //收取事件
typedef void(*TOnDisConnEvent)(TSktConnPkt* SktConnPkt); //断开事件

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
  pthread_t tHandle; //SSkt线程句柄
  THandle SocketHandle;
  THandle Flag;
}TudpParam;

bool udp_Init(TudpParam* Param);
bool udp_Free(TudpParam* Param);

#endif
