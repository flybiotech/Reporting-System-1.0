//-----------------------------------------------------------------------------
// Author      : 朱红波
// Date        : 2012.01.18
// Version     : V 1.00
// Description :
//-----------------------------------------------------------------------------
#ifndef common_H
#define common_H

#include "cm_types.h"

#ifdef __cplusplus
extern "C" {
#endif


//-----------------------------------------------------------------------------
//数学函数
void myusleep(int uSec);
i32 RandomNum(i32 seed);//ok
//-----------------------------------------------------------------------------
//文件函数
//文件IO操作函数
#ifndef WIN32
void GetFileCreateModifyTime(char* fName, time_t* CreateTime, time_t* ModifyTime);
#endif
char* FileExtName(char* FileName); //取得文件扩展名//'.txt'
char* ExtractFileName(char* FileName);
bool DirectoryExists(char* Directory);
bool FileExists(char* FileName);
i32 FileGetSize(char* FileName);
bool FileDelete(char* FileName);
FILE* FileCreate(char* FileName);
FILE* FileOpen(char* FileName);
bool FileClose(FILE* f);
i32 FileGetPos(FILE* f);
i32 FileSeek(FILE* f, i32 Offset, i32 Origin); //Origin=0 1 2
bool FileRead(FILE* f, void* Buf, i32 Len);
bool FileWrite(FILE* f, void* Buf, i32 Len);
//-----------------------------------------------------------------------------
//时间函数
i32 GetTime();
i64 getutime(); //取得微秒级时间 tv.tv_sec*1000000 + tv.tv_usec
#ifndef WIN32
u32 GetTickCount();
TNetTime GetNetTime();
TDateTime GetDateTime();
TDateTime Now();
TDateTime time_tToDateTime(time_t iTime);
time_t DateTimeTotime_t(TDateTime dt);
#endif
i32 timeval_dec(struct timeval* tv2, struct timeval* tv1);//时间相减，返回毫秒

extern bool IsExit;
//*****************************************************************************
void Reboot();
//*****************************************************************************
//网络相关函数
#define NET_TIMEOUT               5000  // ms
#define NET_CONNECT_TIMEOUT       3000  // ms

#ifndef WIN32
char* GetLocalIP();
bool GetLocalIP1(char* eth, char* IP);
#endif
THandle FastConnect(char* aIP, u16 aPort, u32 TimeOut);//返回SocketHandle
bool SendBuf(THandle hSocket, char* Buf, i32 BufLen);
bool RecvBuf(THandle hSocket, char* RecvBuf, i32 BufLen);
bool WaitForData(THandle hSocket);//有问题
i32 ReceiveLength(THandle hSocket);

i32 IsLANIP(const char* IP);
i32 IPToInt(char* IP);
char* IntToIP(i32 IP);
i32 httpget1(const char* url, char* Buf, i32* BufLen, i32 IsShowHead, u32 TimeOut);
i32 httpget(const char* url, char* Buf);

#ifdef __cplusplus
}
#endif

#endif
