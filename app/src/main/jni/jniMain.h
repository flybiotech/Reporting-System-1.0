#ifndef jniMain_h
#define jniMain_h

//#define IS_TEST
#define IS_NEW_RECORD
//#define IS_RENDER_YUV420 未完
#warning "IS_RENDER_YUV420 未完"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dlfcn.h>

#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "jni.h"

#include <SLES/OpenSLES.h>
#include "SLES/OpenSLES_Android.h"

#include "GLES/gl.h"
#include "GLES/glext.h"

#include "ffmpeg/libavcodec/avcodec.h"
#include "ffmpeg/libavformat/avformat.h"
#include "ffmpeg/libswscale/swscale.h"
#include "ffmpeg/libavutil/log.h"

#include "thSDK.h"
#include "linuxthSDK/common.h"
#include "miniupnpc/getwlanIP.h"
#include "mtk_SmartConfig.h"
#include "jpeg.h"

#define LOG_TAG    "jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct TAudioQueuePkt{
  char* buffer;
  int wp;
  int rp;
  int size;
}TAudioQueuePkt;

typedef struct TApp{
  int debug;
  struct {
    SLObjectItf engineObject;
    SLEngineItf engineEngine;
    SLObjectItf outputMixObject;
    SLObjectItf bqPlayerObject;
    SLPlayItf bqPlayerPlay;
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
    SLEffectSendItf bqPlayerEffectSend;
    SLObjectItf recorderObject;
    SLRecordItf recorderRecord;
    SLAndroidSimpleBufferQueueItf recorderBufferQueue;
    TAudioQueuePkt* outrb;
    short* nextBuffer;
    unsigned nextSize;
    int _isplay;
#define RECORDER_FRAMES 1024 * 4
    short recorderBuffer[2][RECORDER_FRAMES];
  }Audio;

#define MAX_VIDEO_CHANNEL 256
  struct {
#ifdef IS_RENDER_YUV420
    GLuint gl_texture[3];
    GLint _uniformSamplers[3];
#else
    GLuint gl_texture;
#endif
    int ScreenWidth;
    int ScreenHeight;
#define TEXTURE_WIDTH 1024
#define TEXTURE_HEIGHT 512
    u8 bufferRGB565[TEXTURE_WIDTH * TEXTURE_HEIGHT * 2];
//#define MAX_DECODE_WIDTH  1920
//#define MAX_DECODE_HEIGHT 1080
    //u8 bufferRGB565[MAX_DECODE_WIDTH * MAX_DECODE_HEIGHT * 3];

    char1024 FileName_Jpg;
    char1024 FileName_Rec;

    struct SwsContext* sws;
    AVCodec* CodecV;
    AVCodecContext* ContextV;
    AVFrame* Frame422;
    AVFrame* FrameRGB565;//tmpAV
//rec
    AVFormatContext* avContext;
    AVStream* avStreamV;

    u8* bufferRecYUV420;
    u8 avEncBuf[1000*200];
    AVFrame* avFrameV;
    AVOutputFormat* avfmt;

    THandle NetHandle;
    char DevIP[100];
    int DevPort;
    char UserName[100];
    char Password[100];
    char UID[100];
    char UIDPsd[100];
    char RemoteFileName[100];

    bool IsSnapShot;
    bool IsRec; // 是否要录冿
    bool Flag_StartRec; // 如果第一次录制，置为true ，然后置为false
    bool Flag_StopRec;
    int RecFrameRate;

    int encWidth;
    int encHeight;

    int VideoType;
    int IFrameFlag;

    int StreamType; //0=次码流 1=主码流 default 0  StreamType
    int IsAudioMute;

    TGroupType GroupType; //1 real time video 2 remote video

  }Video[MAX_VIDEO_CHANNEL];

  struct {
    char256 cls_SearchName;
    char256 SearchFuncName;
    char256 SearchParamName;

    char searchtmpBuf[1000 * 20];
#define SEARFH_SNLST_COUNT       1000
    u32 search_SNLst[SEARFH_SNLST_COUNT];
    int searchCount;
  }Search;

}TApp;
 
void UcnvConvert(char* dst, unsigned long dstLen, const char* src, unsigned long *pnErrC);
//
void PlayAudioBuf(char* Buf, int Len);
void callback_AudioQueueTalk(SLAndroidSimpleBufferQueueItf bq, void *context);
void callback_AudioQueuePlay(SLAndroidSimpleBufferQueueItf bq, void* context);
TAudioQueuePkt* AudioQueue_Create(int bytes);
int AudioQueue_Check(TAudioQueuePkt* p, int writeCheck);
int AudioQueue_Read(TAudioQueuePkt* p, char* out, int bytes);
int AudioQueue_Write(TAudioQueuePkt* p, const char* in, int bytes);
int AudioQueue_Free(TAudioQueuePkt* p);

int video_decode_Init(int Chl);

void video_decode_Record(int Chl, u8* Buf, int Len, bool IsIFrame);
void video_decode_SnapShot(int Chl);
int video_decode(int Chl, u8* Buf, int Len, bool IsIFrame);
int video_decode_Free(int Chl);

void* thread_video_PlayLive(void* arg);
void* thread_video_PlayLiveP2P(void* arg);

void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom);
void alarmCallBack(int AlmType, int AlmTime, int AlmChl, void* UserCustom);

int SaveToBmp(u8* pRGBBuffer, char* picname, int width, int height);

//-----------------------------------------------------------------------------

#define CLASS_PATH_NAME "com/southtech/thSDK/lib"

//miniupnpc
jstring jGetWlanIP(JNIEnv* env, jclass obj);
//test
int jtmp_Test(JNIEnv* env, jclass obj);
//common
jstring jGetLocalIP(JNIEnv* env, jclass obj);

//smartconfig
int jsmt_Init(JNIEnv* env, jclass obj);
int jsmt_Stop(JNIEnv* env, jclass obj);
int jsmt_Start(JNIEnv* env, jclass obj, jstring nSSID, jstring nPassword, jstring nTlv, jstring nTarget, int nAuthMode);

//search
jstring jthNet_SearchDev_old(JNIEnv* env, jclass obj);

int jthSearch_Init(JNIEnv* env, jclass obj);
int jthSearch_SetCallBack(JNIEnv* env, jclass obj, jstring ClassName, jstring FuncName, jstring ParamName);
int jthSearch_SetWiFiCfg(JNIEnv* env, jclass obj, jstring nSSID, jstring nPassword);
int jthSearch_SearchDevice(JNIEnv* env, jclass obj);
int jthSearch_Free(JNIEnv* env, jclass obj);

//render
int jopengl_Resize(JNIEnv* env, jclass obj, int Chl, int w, int h);
int jopengl_Render(JNIEnv* env, jclass obj, int Chl);
//audio
int jaudio_CreatePlay(JNIEnv* env, jclass obj);
int jaudio_Shutdown(JNIEnv* env, jclass obj);
int jaudio_CreateTalk(JNIEnv* env, jclass obj, int Chl);

int jaudio_StartTalk(JNIEnv* env, jclass obj, int Chl);
int jaudio_StopTalk(JNIEnv* env, jclass obj, int Chl);

//video
int jlocal_StartRec(JNIEnv* env, jclass obj, int Chl, jstring nFilename);
int jlocal_StopRec(JNIEnv* env, jclass obj, int Chl);
int jlocal_SnapShot(JNIEnv* env, jclass obj, int Chl, jstring nFileName);

int jthNet_SetAudioIsMute(JNIEnv* env, jclass obj, int Chl, int nIsAudioMute);
jstring jthNet_GetURL(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nURL);//

int jthNet_GetImage(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nFileName);
int jthNet_SetResolution(JNIEnv* env, jclass obj, int Chl, int nStreamType);

int jthNet_PlayLive(JNIEnv* env, jclass obj, int Chl, jstring nDevIP, jstring nUserName, jstring nPassword, int nPort, int nStreamType);
int jthNet_PlayLiveP2P(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, int nStreamType);

int jthNet_PlayRemote(JNIEnv* env, jclass obj, int Chl, jstring nDevIP, jstring nUserName, jstring nPassword, jstring nFileName, int nPort);
int jthNet_PlayRemoteP2P(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nFileName);

int jthNet_StopPlay(JNIEnv* env, jclass obj, int Chl);
int jtmp_DisconnectP2P(JNIEnv* env, jclass obj, int Chl);

#endif

