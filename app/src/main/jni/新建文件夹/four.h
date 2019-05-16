#ifndef FOUR_H
#define FOUR_H
#include <jni.h>
#include <android/log.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include "thSDK.h"
#include "fun.h"
#include "ThDecode.h"
#define UNUSED  __attribute__((unused))
//-----------------------------------------------------------------------------
typedef struct TDevInfoCfg
{
  char SvrIP[40]; //in
  int DataPort; //in
  char UserName[20]; //in
  char Password[20]; //in
  char p2pUID[40];
  char p2pPsd[40];
  int IsP2P;
  int NetHandle; //out
  int channel;

} TDevInfoCfg;

void native_start_ddns_channel(JNIEnv* env, jclass clazz, jstring ip1, jstring usr1, jstring pwd1, jint prt, jint channel);
void native_start_p2p_channel(JNIEnv* env, jclass clazz, jstring Suid, jstring Suidpsd, jint channel);
void vid_start_thd(void* id);
void vid_restart_thd(void* id);
void vid_stop_channel_thd(void* id);
void native_stop_channel(JNIEnv* env, jclass clazz, jint channel);
jstring getUrlChannel(JNIEnv* env, jclass clazz, jstring Surl, jint channel);
void setResolution_channel(JNIEnv* env, jclass clazz, jint channel, jint type);
//
void restart_channel(JNIEnv* env, jclass clazz, jint channel); //0 ddns 1 p2p
#endif
