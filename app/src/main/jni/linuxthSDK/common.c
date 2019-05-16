//-----------------------------------------------------------------------------
// Author      : 朱红波
// Date        : 2012.01.18
// Version     : V 1.00
// Description :
//-----------------------------------------------------------------------------
#include "common.h"
//*****************************************************************************
bool IsExit = false;

//-----------------------------------------------------------------------------
void myusleep(int uSec)
{
  struct timeval tv;
  tv.tv_sec=uSec/1000000;
  tv.tv_usec=uSec%1000000;
  int err;
  do{
    err=select(0,NULL,NULL,NULL,&tv);
  }while(err<0 && errno==EINTR);
}
//-----------------------------------------------------------------------------
i32 RandomNum(i32 seed)
{
  i32 RandSeed;
#ifndef WIN32
  struct timeval tv;
  gettimeofday(&tv, NULL);
  RandSeed = tv.tv_sec + tv.tv_usec;
  srandom(RandSeed);   //srandom(time(NULL));
  return random() % seed;
#else
  RandSeed = GetTickCount();
  srand(RandSeed); 
  return rand() % seed;
#endif
}
//-----------------------------------------------------------------------------
#ifndef WIN32
void GetFileCreateModifyTime(char* fName, time_t* CreateTime, time_t* ModifyTime)
{
  *CreateTime = 0;
  *ModifyTime = 0;
  struct stat s;
  if (stat(fName, &s) == -1) return;
  *CreateTime = s.st_atime;//如果是ext2 ext3分区格式两个时间是一样的
  *ModifyTime = s.st_mtime;
}
#endif
//-----------------------------------------------------------------------------
char* FileExtName(char* FileName)//'.txt'
{
  char* Ext = NULL;
  i32 i, m;
  m = strlen(FileName);
  for (i=m-1; i>=0; i--)
  {
    if (FileName[i] != '.') continue;
    if (i < m - 1) Ext = &FileName[i];
    break;
  }
  return Ext;
}
//-----------------------------------------------------------------------------
char* ExtractFileName(char* FileName)
{
  char* Ext = FileName;
  i32 i, m;
  m = strlen(FileName);
  for (i=m-1; i>=0; i--)
  {
    if ((FileName[i] != '/')&&(FileName[i] != '\\')) continue;
    if (i < m - 1) Ext = &FileName[i+1];
    break;
  }
  return Ext;
}
//-----------------------------------------------------------------------------
bool DirectoryExists(char* Directory)
{
#ifndef WIN32
    struct stat st;
  if(stat(Directory, &st) ==  - 1)
    return false;
  else
    return S_ISDIR(st.st_mode);
#else
  i32 Code;
  Code = GetFileAttributes(Directory);
  return ((Code != -1) && ((FILE_ATTRIBUTE_DIRECTORY & Code) != 0));
#endif
}
//-----------------------------------------------------------------------------
bool FileExists(char* FileName)
{
#ifndef WIN32
  struct stat st;
  return (stat(FileName, &st) != -1);
#else
  WIN32_FIND_DATAA FindData;
  FILE* Handle = FindFirstFile(FileName, &FindData);
  if (Handle == INVALID_HANDLE_VALUE) return false;
  FindClose(Handle);
  return true;
#endif
}
//-----------------------------------------------------------------------------
i32 FileGetSize(char* FileName)
{
#ifndef WIN32
  struct stat statbuf;
  i32 i = stat(FileName, &statbuf);
  if(i<0) return 0;
  S_ISDIR(statbuf.st_mode);
  S_ISREG(statbuf.st_mode);
  return statbuf.st_size;
#else
  WIN32_FIND_DATAA FindData;
  FILE* Handle = FindFirstFile(FileName, &FindData);
  if (Handle == INVALID_HANDLE_VALUE) return -1;
  FindClose(Handle);
  return FindData.nFileSizeLow;
#endif
}
//-----------------------------------------------------------------------------
FILE* FileCreate(char* FileName)
{
#ifndef WIN32
  return fopen(FileName, "w+b");
#else
  return CreateFile(FileName, GENERIC_READ | GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, 0);
#endif
}
//-----------------------------------------------------------------------------
FILE* FileOpen(char* FileName)
{
#ifndef WIN32
  return fopen(FileName, "r+");
#else
  return CreateFile(FileName, GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
#endif
}
//-----------------------------------------------------------------------------
bool FileClose(FILE* f)
{
#ifndef WIN32
  return (fclose(f) == 0);
#else
  return CloseHandle(f);
#endif
}
//-----------------------------------------------------------------------------
bool FileWrite(FILE* f, void* Buf, i32 Len)
{
#ifndef WIN32
  return (fwrite(Buf, Len, 1, f) != 0);
#else
  i32 Result;
  WriteFile(f, Buf, Len, &Result, NULL);
  return (Result == Len);
#endif
}
//-----------------------------------------------------------------------------
bool FileRead(FILE* f, void* Buf, i32 Len)
{
#ifndef WIN32
  return (fread(Buf, Len, 1, f) != 0);
#else
  i32 Result;
  ReadFile(f, Buf, Len, &Result, NULL);
  return (Result == Len);
#endif
}
//-----------------------------------------------------------------------------
bool FileDelete(char* FileName)
{
#ifndef WIN32
  return (unlink(FileName) != -1);
#else
  return DeleteFile(FileName);
#endif
}
//-----------------------------------------------------------------------------
i32 GetTime() //取得系统时间
{
#ifndef WIN32
  return time(NULL);
#else
  SYSTEMTIME st;
  GetLocalTime(&st);
  return SystemTimeToTime_t(&st);
#endif
}
//-----------------------------------------------------------------------------
i64 getutime() //取得微秒级时间
{
#ifndef WIN32
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return (i64)(tv.tv_sec)* 1000000+tv.tv_usec;
#else
  struct timeval tv;
  SYSTEMTIME st;
  GetLocalTime(&st);
  tv.tv_sec = SystemTimeToTime_t(&st);
  tv.tv_usec = st.wMilliseconds * 1000;
  return (i64)(tv.tv_sec)* 1000000+tv.tv_usec;
#endif
}
//-----------------------------------------------------------------------------
#ifndef WIN32
u32 GetTickCount()
{
  static i32 t = 0;
  struct timeval tv;
  gettimeofday(&tv, NULL);
  if (t == 0)
  {
#define FROM_PROC_UPTIME
#ifdef FROM_PROC_UPTIME
    char buf[40];
    float a, b;
    FILE* f = fopen("/proc/uptime", "r+");
    if (f) 
    {
      fgets(buf, sizeof(buf), f);
      fclose(f);
      sscanf(buf, "%f %f", &a, &b);
      t = tv.tv_sec - (i32)a;
    } 
    else
    {
      t = tv.tv_sec;
    }
#else
    t = tv.tv_sec;
#endif
  }
  return (u32)(tv.tv_sec - t)*1000 + tv.tv_usec/1000;
}
//-----------------------------------------------------------------------------
TNetTime GetNetTime()
{
  struct TNetTime nt;
  struct timeval tv;
  struct tm* m;
  gettimeofday(&tv, NULL);
  m = localtime(&tv.tv_sec);
  nt.Year = m->tm_year - 100;
  nt.Month = m->tm_mon + 1;
  nt.Day = m->tm_mday;
  nt.Hour = m->tm_hour;
  nt.Minute = m->tm_min;
  nt.Second = m->tm_sec;
  nt.MilliSecond = tv.tv_usec / 1000;
//  printf("GetNetTime:%3d-%0.2d-%0.2d %0.2d:%0.2d:%0.2d %0.3d\n", nt.Year,nt.Month,nt.Day,nt.Hour,nt.Minute,nt.Second,nt.MilliSecond);
  return nt;
}
//-----------------------------------------------------------------------------
TDateTime time_tToDateTime(time_t iTime)
{
  return (iTime / 86400.0 + 25569);
}
//-----------------------------------------------------------------------------
time_t DateTimeTotime_t(TDateTime dt)
{
  return (time_t)((dt - 25569.0) * 86400);
}
//-----------------------------------------------------------------------------
TDateTime GetDateTime()
{
  return Now();
}
//-----------------------------------------------------------------------------
TDateTime Now()
{
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return (TDateTime)(tv.tv_sec / 86400.0 + 25569.0 + tv.tv_usec /(86400.0 * 1000000));
}
#endif
//-----------------------------------------------------------------------------
void Reboot()
{
  IsExit = true;
  char str[8];
  str[0] = 'r';
  str[1] = 'e';
  str[2] = 'b';
  str[3] = 'o';
  str[4] = 'o';
  str[5] = 't';
  str[6] = '\0';
  system(str);
}

//-----------------------------------------------------------------------------
bool GetIPPortFromAddr(struct sockaddr_in Addr, char* IP, u16* Port)
{
  /*
  CltAddr.sin_family = AF_INET;
  CltAddr.sin_addr.s_addr = inet_addr(DDNSSvrIP);
  CltAddr.sin_port = htons(Port);
  */
  if(!IP) return false;
  char* tmpIP = inet_ntoa(Addr.sin_addr);
  strcpy(IP, tmpIP);
  *Port = ntohs(Addr.sin_port);
  return true;
}
//-----------------------------------------------------------------------------
#ifndef WIN32
char* GetLocalIP()
{
  struct ifconf conf;
  struct ifreq* ifr;
  char buff[512];
  i32 num;
  i32 i;
  i32 hSkt = socket(PF_INET, SOCK_DGRAM, 0);
  conf.ifc_len = 512;
  conf.ifc_buf = buff;
  ioctl(hSkt, SIOCGIFCONF, &conf);
  num = conf.ifc_len / sizeof(struct ifreq);
  ifr = conf.ifc_req;
  for(i = 0; i<num; i ++)
  {
    struct sockaddr_in* sin = (struct sockaddr_in*)(&ifr->ifr_addr);
    ioctl(hSkt, SIOCGIFFLAGS, ifr);
    if(((ifr->ifr_flags &IFF_LOOPBACK) == 0)&&(ifr->ifr_flags &IFF_UP))
    {
      close(hSkt);
      return inet_ntoa(sin->sin_addr);
    }
    ifr ++;
  }
}
//-----------------------------------------------------------------------------
bool GetLocalIP1(char* eth, char* IP)
{
  if(!IP) return false;
  struct ifreq ifr;
  i32 hSkt = socket(AF_INET, SOCK_DGRAM, 0);
  memset(&ifr, 0, sizeof(struct ifreq));
  strcpy(ifr.ifr_name, eth);
  ioctl(hSkt, SIOCGIFADDR, &ifr);           
  u8* m = (u8*)&ifr.ifr_addr.sa_data;
  close(hSkt);
  sprintf(IP, "%d.%d.%d.%d", m[2], m[3], m[4], m[5]);
  return true;
}
#endif
//-----------------------------------------------------------------------------
THandle FastConnect(char* aIP, u16 aPort, u32 TimeOut)//返回SocketHandle
{
  if (!aIP) return NULLHANDLE;
  if (aPort == 0) return NULLHANDLE;

  THandle hSocket = NULLHANDLE;
  i32 i, Ret, Flag;
  i32 Error = 0;
  fd_set rs, ws;
  struct timeval tval;
  struct sockaddr_in CSktAddr;
  u32 tmp = INADDR_NONE;
  struct hostent* h = NULL;

  memset(&CSktAddr, 0, sizeof(struct sockaddr_in));
  CSktAddr.sin_family = AF_INET;
  CSktAddr.sin_port = htons(aPort);
  //inet_aton(aIP, &CSktAddr.sin_addr);
  h = (struct hostent*)gethostbyname(aIP);
  if (h == NULL) return NULLHANDLE;
  memcpy(&CSktAddr.sin_addr, h->h_addr_list[0], h->h_length);
  
//  hSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
//  if (hSocket <= 0) return NULLHANDLE;
  //hSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
  //if (hSocket <= 0) hSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
  for (i=0; i<10; i++)
  {
    hSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (hSocket > 0) break;
    usleep(1000*100);
  }
  if (hSocket <= 0) return NULLHANDLE;  	
  	
#ifndef WIN32
  Flag = fcntl(hSocket, F_GETFL, 0);// ==2
  fcntl(hSocket, F_SETFL, Flag | O_NONBLOCK);//O_NONBLOCK==2048 |2 = 2050
#else
  Flag = 1;//0
  ioctlsocket(hSocket, FIONBIO, &Flag);//非阻塞方式
#endif
  
  Ret = connect(hSocket, (struct sockaddr*) &CSktAddr, sizeof(struct sockaddr_in));
  if(Ret == 0) goto Done;

  FD_ZERO(&rs);
  FD_SET(hSocket, &rs);
  ws = rs;//FD_SET(hSocket, &ws);  

  tval.tv_sec  = TimeOut / 1000;//TimeOut;
  tval.tv_usec = (TimeOut % 1000) * 1000;
  if ((Ret = select(hSocket+1, &rs, &ws, NULL, TimeOut ? &tval: NULL)) == 0) 
  {
    close(hSocket);
    errno = ETIMEDOUT;
    return NULLHANDLE;
  }
  if (FD_ISSET(hSocket, &rs)||FD_ISSET(hSocket, &ws)) 
  {
    Error = 0;
    Ret = sizeof(i32);
    if (getsockopt(hSocket, SOL_SOCKET, SO_ERROR, &Error, &Ret) < 0)
    {
      close(hSocket);
      errno = ETIMEDOUT;
      return NULLHANDLE;
    }
  }

Done:
#ifndef WIN32
  fcntl(hSocket, F_SETFL, Flag);
#endif
  if (Error) 
  {
    close(hSocket);
    errno = Error;
    return NULLHANDLE;
  }
  return hSocket;
}
//-----------------------------------------------------------------------------
bool SendBuf(THandle hSocket, char* Buf, i32 BufLen)
{
#define MTUSIZE 576 //mtu = 1500 Intranet,  576 Internet
#define SPLIT_PKT_SIZE  MTUSIZE-40
#define SPLIT_PKT_SEND

  if (!Buf) return false;
  if (BufLen <= 0) return false;
  if (hSocket <= 0) return false;

#ifdef SPLIT_PKT_SEND
  i32 k = 0;
  i32 SendLen = 0;
  u32 t, t1;
  t = GetTickCount();
  while (k < BufLen)
  {
    SendLen = min(SPLIT_PKT_SIZE, BufLen - k);
#ifdef __cplusplus
    if (hSocket <=0) return false;
#endif

    SendLen = send(hSocket, (char*)Buf + k, SendLen, 0);

    if (SendLen != -1)
    {
      k = SendLen + k;
    }
    else
    {      
      if (errno == EINTR || errno == EAGAIN)//EWOULDBLOCK = EAGAIN
      {
        t1 = GetTickCount();
        if (t1 - t >= NET_TIMEOUT)
        {
          //printf("errno EWOULDBLOCK %d %s():%d \n", errno, __FUNCTION__, __LINE__);
          return false;
        }
        errno = 0;
        usleep(1000*10);
        continue;
      }
      else
      {
        if (errno != 0 )
        {
          //printf("errno %d %s():%d\n", errno, __FUNCTION__, __LINE__);
          errno = 0;
        }
        return false;
      }
    }    
  }
  return true;

#else
  i32 Ret = send(hSocket, Buf, BufLen, 0);
  return (Ret >= 0);
#endif
}
//-----------------------------------------------------------------------------
bool RecvBuf(THandle hSocket, char* Buf, i32 BufLen)
{
  if (!Buf) return false;
  if (BufLen == 0) return true;
  i32 Len, RecvLen;
  u32 t, t1;
  RecvLen = 0;
  //  if not WaitForData(TimeOut) then exit;
  t = GetTickCount();
  while (true)
  {
#ifdef __cplusplus
    if (hSocket <=0) return false;
#endif
    Len = recv(hSocket, &Buf[RecvLen], BufLen-RecvLen, 0);
    if (Len != -1)
    {
      RecvLen = RecvLen + Len;
    }
    else
    {
      if (errno == EINTR || errno == EAGAIN)//EWOULDBLOCK = EAGAIN
      {
        t1 = GetTickCount();
        if (t1 - t >= NET_TIMEOUT)
        {
          //printf("errno EWOULDBLOCK %d %s():%d \n", errno, __FUNCTION__, __LINE__);
          return false;
        }
        errno = 0;
        usleep(1000*10);
        continue;
      }
      else
      {
        if (errno != 0 )
        {
          //printf("errno %d %s():%d\n", errno, __FUNCTION__, __LINE__);
          errno = 0;
        }
        return false;
      }
    }
    if (RecvLen == BufLen) return true;
  }
}
//-----------------------------------------------------------------------------
i32 ReceiveLength(THandle hSocket)
{
  i32 Result;
#ifndef WIN32
  ioctl(hSocket, FIONREAD, &Result);
#else
  ioctlsocket(hSocket, FIONREAD, &Result);
#endif
  return Result;
}
//-----------------------------------------------------------------------------
bool WaitForData(THandle hSocket)
{
  fd_set FDSet;
  struct timeval TimeVal;
  TimeVal.tv_sec = NET_TIMEOUT / 1000;
  TimeVal.tv_usec = (NET_TIMEOUT % 1000) * 1000;
  FD_ZERO(&FDSet);
  FD_SET(hSocket, &FDSet);
  return (select(0, &FDSet, NULL, NULL, &TimeVal)>0);
}
//------------------------------------------------------------------------------
i32 IsLANIP(const char* IP)
{
  u32 nIP;
  u8 a, b;
#ifndef WIN32
  struct sockaddr_in addr;
  inet_aton(IP, &addr.sin_addr);
  memcpy(&nIP, &addr.sin_addr, 4);
#else
  nIP = inet_addr(IP);
#endif
  a = (u8)nIP;
  b = (u8)(nIP >> 8);
  return (((a==192)&&(b==168))||((a==172)&&(b>=16)&&(b<=31))||(a==0)||(b==10));
}
//-----------------------------------------------------------------------------
i32 Base64Encode(char* src, char* dst)
{
  const char* lst = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  i32 len;
  char* strnew;
  char* strold;

  if (!src) return 0;
  if (!dst) return 0;

  len = strlen(src);
  strnew = dst;
  strold = src;

  while ((len - (strold - src)) >= 3)
  {
    strnew[0] = lst[(strold[0] &(u8)0xFC) >> 2];
    strnew[1] = lst[((strold[0] &(u8)0x03) << 4) | ((strold[1] &(u8)0xF0) >> 4)];
    strnew[2] = lst[((strold[1] &(u8)0x0F) << 2) | ((strold[2] &(u8)0xC0) >> 6)];
    strnew[3] = lst[strold[2] &(u8)0x3F];
    strnew += 4;
    strold += 3;
  }

  switch (len - (strold - src))
  {
  case 1:
    strnew[0] = lst[(strold[0] &(u8)0xFC) >> 2];
    strnew[1] = lst[(strold[0] &(u8)0x03) << 4];
    strnew[2] = '=';
    strnew[3] = '=';
    strnew += 4;
    break;

  case 2:
    strnew[0] = lst[(strold[0] &(u8)0xFC) >> 2];
    strnew[1] = lst[((strold[0] &(u8)0x03) << 4) | ((strold[1] &(u8)0xF0) >> 4)];
    strnew[2] = lst[(strold[1] &(u8)0x0F) << 2];
    strnew[3] = '=';
    strnew += 4;
  }

  return 1;
}
//------------------------------------------------------------------------------
i32 IPToInt(char* IP)
{
  return inet_addr(IP);
}
//------------------------------------------------------------------------------
char* IntToIP(i32 IP)
{
 return inet_ntoa(*(struct in_addr*)&IP);
}
//-----------------------------------------------------------------------------
i32 httpget1(const char* url, char* Buf, i32* BufLen, i32 IsShowHead, u32 TimeOut)
{
typedef char char256[256];
#define SOCKET_ERROR -1
#define INVALID_SOCKET -1
#define SD_SEND 1

  char256 SvrName, HostName, UserNamePassword, b64UserNamePassword, tmpBuf1;
  char SendStr[1024];
  char PageName[1024];
  struct sockaddr_in addr;
  struct hostent* h;
  unsigned long tmp_address = INADDR_NONE;
  i32 recvLen = 0;
  i32 port = 80;
  i32 m[4]; 
  THandle hSocket = INVALID_SOCKET;
  fd_set fs;
  struct timeval tv;
  time_t t;
  i32 ret, i;
  i32 Result = 0;
  i32 iContentLength = 0;
  char* tmpBuf;
  i32 itmp;

  if (!Buf) return 0;

  Buf[0] = 0x00;
  *BufLen = 0;
  HostName[0] = 0x00;

  ret = sscanf(url, "http://%[^@]@%[^/]%s", UserNamePassword, SvrName, PageName);
  if (ret != 3)
  {
    ret = sscanf(url, "http://%[^/]%s", SvrName, PageName);
    UserNamePassword[0] = 0x00;
    if (ret != 2) sprintf(PageName, "/");
  }

  ret = sscanf(SvrName, "%[^:]:%d", HostName, &port);
  if (ret == 2)
  {
    if ((strlen(HostName) == 0) || (port <= 0) || (port >0xffff)) return 0;
  }
  else
  {
    strcpy(HostName, SvrName);
    port = 80;
  }

  addr.sin_family = AF_INET;
  addr.sin_port = htons((u16)port);

  ret = sscanf(HostName, "%d.%d.%d.%d", &m[0], &m[1], &m[2], &m[3]);
  if ((ret == 4) && (m[0] >= 0) && (m[0] <= 255) && (m[1] >= 0) && (m[1] <= 255) && (m[2] >= 0) && (m[2] <= 255) && (m[3] >= 0) && (m[3] <= 255))
  {
    if ((tmp_address = inet_addr(HostName)) != INADDR_NONE)
      memcpy(&addr.sin_addr, &tmp_address, sizeof(unsigned long));
    else return 0;
  }
  else
  {
    if ((h = gethostbyname(HostName)) != NULL)
      memcpy(&addr.sin_addr, h->h_addr_list[0], h->h_length);
    else return 0;
  }

  if (strlen(UserNamePassword) > 0)
  {
    Base64Encode(UserNamePassword, b64UserNamePassword);
    sprintf(SendStr, "GET %s HTTP/1.0\r\nHost: %s\r\nAuthorization: Basic %s\r\n\r\n", PageName, HostName, b64UserNamePassword);
  }
  else
  {
    sprintf(SendStr, "GET %s HTTP/1.0\r\nHost: %s\r\n\r\n", PageName, HostName);
  }

//建立SOCKET
  hSocket = socket(AF_INET, SOCK_STREAM, 0);
  if (hSocket == INVALID_SOCKET) return 0;

  ret = connect(hSocket, (struct sockaddr*) &addr, sizeof(struct sockaddr_in));
  if (ret == SOCKET_ERROR) goto exits;

  TimeOut =  TimeOut / 1000;
  tv.tv_sec = TimeOut;
  tv.tv_usec = 0;
  FD_ZERO(&fs);
  FD_SET(hSocket, &fs);

  ret = select(hSocket + 1, NULL, &fs, NULL, &tv);
  if (ret == SOCKET_ERROR) goto exits;
  if (ret == 0) goto exits;

  ret = send(hSocket, SendStr, strlen(SendStr), 0);
  if (ret == SOCKET_ERROR) goto exits;

//收取头
  t = time(NULL);
  *BufLen = 0;
  do 
  {
    tv.tv_sec = TimeOut - (time(NULL) - t);
    tv.tv_usec = 0;
    FD_ZERO(&fs);
    FD_SET(hSocket, &fs);

    ret = select(hSocket + 1, &fs, NULL, NULL, &tv);
    if (ret == SOCKET_ERROR) goto exits;
    if (ret == 0) goto exits;

    recvLen = recv(hSocket, &Buf[*BufLen], 1, 0);//一次收一个字节
    if (recvLen > 0) *BufLen = *BufLen + recvLen;
    if (*BufLen < 4) continue;

    if (*BufLen > 1024) goto exits;

    memcpy(&itmp, &Buf[*BufLen-4], 4);
    if (itmp != 0x0A0D0A0D) continue;

    Buf[*BufLen] = 0x00;

    for (i=0; i<*BufLen; i++) 
    {
      if (Buf[i] >='A' && Buf[i] <='Z')  Buf[i] = Buf[i] + 32;
    }

    tmpBuf = strstr(Buf, "content-length:");
    if (tmpBuf)
    {
      ret = sscanf(tmpBuf, "content-length:%d%s", &iContentLength, tmpBuf1);
      if (ret != 2)
      {
        ret = sscanf(tmpBuf, "content-length: %d%s", &iContentLength, tmpBuf1);
        if (ret != 2) goto exits;
      }
      break;
    }
  }
  while ((recvLen > 0) && ((time(NULL) - t) < TimeOut));

  if (iContentLength <= 0) goto exits;

  if (IsShowHead) iContentLength = iContentLength + *BufLen; else *BufLen = 0;

//收取数据
  t = time(NULL);
  do 
  {
    tv.tv_sec = TimeOut - (time(NULL) - t);
    tv.tv_usec = 0;
    FD_ZERO(&fs);
    FD_SET(hSocket, &fs);

    ret = select(hSocket + 1, &fs, NULL, NULL, &tv);
    if (ret == SOCKET_ERROR) goto exits;
    if (ret == 0) goto exits;

    recvLen = recv(hSocket, &Buf[*BufLen], iContentLength - *BufLen, 0);
    if (recvLen > 0) *BufLen = *BufLen + recvLen;
  }
  while ((recvLen > 0) && ((time(NULL) - t) < TimeOut));
  if (*BufLen <= 0) goto exits;
  Result = 1;
  //
exits:
  shutdown(hSocket, SD_SEND);
  close(hSocket);

  return Result;
}
//-----------------------------------------------------------------------------
i32 httpget(const char* url, char* Buf)
{
  i32 BufLen;
  return httpget1(url, Buf, &BufLen, 0, NET_TIMEOUT * 2);
}
//-----------------------------------------------------------------------------
i32 timeval_dec(struct timeval* tv2, struct timeval* tv1)//时间相减，返回毫秒
{
  return (tv2->tv_sec - tv1->tv_sec)*1000 + (tv2->tv_usec - tv1->tv_usec)/1000;
}
//-----------------------------------------------------------------------------
