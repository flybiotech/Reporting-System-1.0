//-----------------------------------------------------------------------------
// Author      : ��첨
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
//��ѧ����
void myusleep(int uSec);
i32 RandomNum(i32 seed);//ok
//-----------------------------------------------------------------------------
//�ļ�����
//�ļ�IO��������
#ifndef WIN32
void GetFileCreateModifyTime(char* fName, time_t* CreateTime, time_t* ModifyTime);
#endif
char* FileExtName(char* FileName); //ȡ���ļ���չ��//'.txt'
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
//ʱ�亯��
i32 GetTime();
i64 getutime(); //ȡ��΢�뼶ʱ�� tv.tv_sec*1000000 + tv.tv_usec
#ifndef WIN32
u32 GetTickCount();
TNetTime GetNetTime();
TDateTime GetDateTime();
TDateTime Now();
TDateTime time_tToDateTime(time_t iTime);
time_t DateTimeTotime_t(TDateTime dt);
#endif
i32 timeval_dec(struct timeval* tv2, struct timeval* tv1);//ʱ����������غ���

extern bool IsExit;
//*****************************************************************************
void Reboot();
//*****************************************************************************
//������غ���
#define NET_TIMEOUT               5000  // ms
#define NET_CONNECT_TIMEOUT       3000  // ms

#ifndef WIN32
char* GetLocalIP();
bool GetLocalIP1(char* eth, char* IP);
#endif
THandle FastConnect(char* aIP, u16 aPort, u32 TimeOut);//����SocketHandle
bool SendBuf(THandle hSocket, char* Buf, i32 BufLen);
bool RecvBuf(THandle hSocket, char* RecvBuf, i32 BufLen);
bool WaitForData(THandle hSocket);//������
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
