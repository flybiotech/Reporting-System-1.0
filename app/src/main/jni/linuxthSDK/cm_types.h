//-----------------------------------------------------------------------------
// Author      : ÷Ï∫Ï≤®
// Date        : 2012.01.18
// Version     : V 1.00
// Description : 
//-----------------------------------------------------------------------------
#ifndef cm_types_H
#define cm_types_H

#include <sys/types.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/timeb.h>
#include <dirent.h>
#include <sys/stat.h>
#include <stddef.h>
#include <ctype.h>
#include <time.h>
#include <getopt.h>
#include <termios.h>
#include <signal.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <netdb.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <string.h>

#ifndef __cplusplus
#include <stdbool.h>
#endif
#include <stdarg.h>

#define  i8  int8_t
#define i16 int16_t
#define i32 int32_t
#define i64 int64_t
#define  u8  uint8_t
#define u16 uint16_t
#define u32 uint32_t
#define u64 uint64_t
//#define long long;
#define ulong unsigned long;

#define TDateTime double
typedef char char2[2];
typedef char char4[4];
typedef char char8[8];
typedef char char12[12];
typedef char char16[16];
typedef char char20[20];
typedef char char28[28];
typedef char char32[32];
typedef char char40[40];
typedef char char48[48];
typedef char char50[50];
typedef char char64[64];
typedef char char80[80];
typedef char char100[100];
typedef char char256[256];
typedef char char1024[1024];
typedef char char8192[1024*8];
typedef char8192 char8k;

#define NULLHANDLE           -1

#define THandle long
//-----------------------------------------------------------------------------
typedef struct TRect {                        //sizeof 16 u8
  long left;                                    //◊Û
  long top;                                     //…œ
  long right;                                   //”“
  long bottom;                                  //œ¬
}TRect;
//#define RECT TRect

//-----------------------------------------------------------------------------
typedef struct bit {                             //◊÷∑˚Œª≤Ÿ◊˜
  char b0:1, b1:1, b2:1, b3:1, b4:1, b5:1, b6:1, b7:1;
}bit;

typedef struct TNetTime { //sizeof 8
  u8 Year;//u8; 09 + 2000
  u8 Month;//1..12;
  u8 Day;//1..31;
  u8 Hour;//0..23;
  u8 Minute;//0..59;
  u8 Second;//0..59;
  u16 MilliSecond;//0..999;
}TNetTime;

//-----------------------------------------------------------------------------
typedef struct TVersion {
  union {
    struct {u16 Major,Minor,Release,Build;};
    u16 v[4];
    u64 f;
  };
}TVersion;

typedef struct TMacAddress {
  union {
    struct {u32 m4321,m8765;};
    struct {u8 m0,m1,m2,m3,m4,m5,m6,m7;};
  };
}TMacAddress;

#ifndef max
#define max(a,b) (((a) > (b)) ? (a) : (b))
#endif
#ifndef min
#define min(a,b) (((a) < (b)) ? (a) : (b))
#endif

#define Bit(S, Bits, Value) ( (Value==1) ? (S|(1<<Bits)) : (S&(~(1<<Bits))) )
#define BitXOR(S, Bits)     ( S ^ (1 << Bits) )
#define BitValue(S, Bits)   ( (S & (1 << Bits)) != 0 )

#endif

