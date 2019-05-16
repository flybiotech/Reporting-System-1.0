#ifndef THDECODE_H
#define THDECODE_H

#include <dlfcn.h> 
#include <GLES/gl.h>
#include <GLES/glext.h>
#include "fun.h"
typedef struct TdecPkt
{
  AVCodecContext* CodecContextV;
  AVCodec* CodecV;
  AVFrame* FrameV;
  AVFrame* pFrameRGB;
  struct SwsContext* img_convert_ctx;
  char* s_pixels;
  char* b_pixels;

  //bool IsRec;
} TdecPkt;
typedef struct TdecParam
{
  int VideoType; //MPEG4=0, MJPEG=1  H264=2
  char* encBuf; //±ØÐëÏÈÉêÇë
  int encLen; //½âÂëºóÊý¾Ý´óÐ¡

  int decfmt;
  char* decBuf;
  int decLen;

  int decWidth;
  int decHeight;

  int decWidthMain;
  int decHeightMain;
  int play_type; //0 sd 1:hd
  int switch_cnt;

  int OutWidth;
  int OutHeight;
  int flag;
  int tag;
  int init;
} TdecParam;
int thDecodeVideoInit(TdecParam* Param);
int thDecodeVideoFrame(int decHandle, TdecParam* Param, int channel);
int thDecodeVideoFree(int* decHandle);
void native_gl_resize_channel(JNIEnv* env, jclass clazz, jint w, jint h, jint channel, jint expand);
int native_gl_render_channel(JNIEnv* env, jclass clazz, jint channel, jint expand);
#endif
