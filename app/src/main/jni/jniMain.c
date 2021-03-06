#include "jniMain.h"

TApp App;
pthread_cond_t th_sync_cond;
pthread_mutex_t th_mutex_lock;

#ifdef IS_TEST
int idec, isws, irender;
#endif


//-----------------------------------------------------------------------------
void UcnvConvert(char* dst, unsigned long dstLen, const char* src, unsigned long *pnErrC)
{
  //UcnvConvert(uDevName, 100, DevName, &pnErrC);
  typedef void (*pvUcnvFunc)(const char* lpcstrDstEcd, const char* lpcstrSrcEcd, char* dst, unsigned long dstLen, const char* src, unsigned long nInLen, unsigned long *pnErrCode);
  static pvUcnvFunc g_pvUcnvConvert = NULL;  
  static void*      g_pvUcnvDll = NULL;

  if (NULL == g_pvUcnvDll) g_pvUcnvDll = dlopen("/system/lib/libicuuc.so", RTLD_LAZY);
  if (NULL == g_pvUcnvDll) return;
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_44");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_46");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_47");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_48");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_49");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_50");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_51");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_53");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_54");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_52");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_55");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_56");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_57");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_58");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_59");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_60");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_61");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_62");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_63");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_64");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_65");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_66");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_67");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_68");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_69");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_70");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_71");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_72");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_73");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_74");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_75");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_76");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_77");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_78");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_79");
    if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_80");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_4_2");
  if (NULL == g_pvUcnvConvert) g_pvUcnvConvert = (pvUcnvFunc)dlsym(g_pvUcnvDll, "ucnv_convert_3_8");
  if (NULL == g_pvUcnvConvert) return;
  g_pvUcnvConvert("utf8", "gb2312", dst, dstLen, src, strlen(src), pnErrC);
}

//-----------------------------------------------------------------------------
void callback_AudioQueuePlay(SLAndroidSimpleBufferQueueItf bq, void* context)
{
  int len = AudioQueue_Read(App.Audio.outrb, (char*)App.Audio.nextBuffer,App.Audio.nextSize);
  if (len > 0)
  {
    (*App.Audio.bqPlayerBufferQueue)->Enqueue(App.Audio.bqPlayerBufferQueue, App.Audio.nextBuffer, App.Audio.nextSize);
    App.Audio._isplay = 1;
  }
  else
  {
    App.Audio._isplay = 0;
    usleep(500000);
  }
}
//-----------------------------------------------------------------------------
void callback_AudioQueueTalk(SLAndroidSimpleBufferQueueItf bq, void* context)
{
  int Chl = (int)context;
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  static int flag = 1;
  (*App.Audio.recorderBufferQueue)->Enqueue(App.Audio.recorderBufferQueue, App.Audio.recorderBuffer[flag], RECORDER_FRAMES * sizeof(short));
  flag = !flag;
  if (thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    thNet_SetTalk(App.Video[Chl].NetHandle, (char*)App.Audio.recorderBuffer[flag], RECORDER_FRAMES * sizeof(short));
  }
}
//-----------------------------------------------------------------------------
void PlayAudioBuf(char* Buf,int Len)
{
  if (App.Audio._isplay == 0)
  {
    AudioQueue_Write(App.Audio.outrb, Buf,Len);
    int space = AudioQueue_Check(App.Audio.outrb, 0);
    if (space < 4096) return;
    int len = AudioQueue_Read(App.Audio.outrb, (char*)App.Audio.nextBuffer,App.Audio.nextSize);
    App.Audio._isplay = 1;
    (*App.Audio.bqPlayerBufferQueue)->Enqueue(App.Audio.bqPlayerBufferQueue, App.Audio.nextBuffer, App.Audio.nextSize);
    return;
  }
  else
  {
    int space = AudioQueue_Check(App.Audio.outrb, 0);
    int size = AudioQueue_Write(App.Audio.outrb, Buf,Len);
  }
}
//-----------------------------------------------------------------------------
TAudioQueuePkt* AudioQueue_Create(int bytes)
{
  TAudioQueuePkt *p;
  if ((p = calloc(1, sizeof(TAudioQueuePkt))) == NULL) {
    return NULL;
  }
  p->size = bytes;
  p->wp = p->rp = 0;

  if ((p->buffer = calloc(bytes, sizeof(char))) == NULL) {
    free (p);
    return NULL;
  }
  return p;
}
//-----------------------------------------------------------------------------
int AudioQueue_Check(TAudioQueuePkt *p, int writeCheck)
{
  int wp = p->wp, rp = p->rp, size = p->size;
  if(writeCheck)
  {
    if (wp > rp) return rp - wp + size - 1;
    else if (wp < rp) return rp - wp - 1;
    else return size - 1;
  }
  else
  {
    if (wp > rp) return wp - rp;
    else if (wp < rp) return wp - rp + size;
    else return 0;
  } 
}
//-----------------------------------------------------------------------------
int AudioQueue_Read(TAudioQueuePkt *p, char *out, int bytes)
{
  int remaining;
  int bytesread, size = p->size;
  int i=0, rp = p->rp;
  char *buffer = p->buffer;
  if ((remaining = AudioQueue_Check(p, 0)) == 0) {
    return 0;
  }
  bytesread = bytes > remaining ? remaining : bytes;
  for(i=0; i < bytesread; i++){
    out[i] = buffer[rp++];
    if(rp == size) rp = 0;
  }
  p->rp = rp;
  return bytesread;
}
//-----------------------------------------------------------------------------
int AudioQueue_Write(TAudioQueuePkt *p, const char *in, int bytes){
  int remaining;
  int byteswrite, size = p->size;
  int i=0, wp = p->wp;
  char *buffer = p->buffer;
  if ((remaining = AudioQueue_Check(p, 1)) == 0) {
    return 0;
  }
  byteswrite = bytes > remaining ? remaining : bytes;
  for(i=0; i < byteswrite; i++){
    buffer[wp++] = in[i];
    if(wp == size) wp = 0;
  }
  p->wp = wp;
  return byteswrite;
}
//-----------------------------------------------------------------------------
int AudioQueue_Free(TAudioQueuePkt *p)
{
  if(p == NULL) return;
  free(p->buffer);
  free(p);
  return 1;
}
//-------------------------------------------------------------------------
int SaveToBmp(u8* pRGBBuffer, char* picname, int width, int height)
{
#pragma pack(2) 
  typedef struct BITMAPFILEHEADER {   
    u_int16_t bfType;   
    u_int32_t bfSize;   
    u_int16_t bfReserved1;   
    u_int16_t bfReserved2;   
    u_int32_t bfOffBits;   
  }BITMAPFILEHEADER;   

  typedef struct BITMAPINFOHEADER {   
    u_int32_t biSize;   
    u_int32_t biWidth;   
    u_int32_t biHeight;   
    u_int16_t biPlanes;   
    u_int16_t biBitCount;   
    u_int32_t biCompression;   
    u_int32_t biSizeImage;   
    u_int32_t biXPelsPerMeter;   
    u_int32_t biYPelsPerMeter;   
    u_int32_t biClrUsed;   
    u_int32_t biClrImportant;   
  }BITMAPINFOHEADER;

  BITMAPFILEHEADER bmpheader;
  BITMAPINFOHEADER bmpinfo;
  unsigned int uiTmp, uiTmp2;
  unsigned char* ucTmp = NULL;
  unsigned char ucRGB;
  int i;
  uiTmp = (width* 3+3) / 4 * 4 * height;
  uiTmp2 = width * height * 3;
  char szFilename[1024];
  sprintf(szFilename, "%s", picname); //Õº∆¨√˚◊÷Œ™ ”∆µ√˚+∫≈¬Î 
  FILE* f = fopen(szFilename, "w+b");
  if (f == NULL) return 0;
  bmpheader.bfType = 0x4D42;
  bmpheader.bfReserved1 = 0;
  bmpheader.bfReserved2 = 0;
  bmpheader.bfOffBits = sizeof(bmpheader) + sizeof(bmpinfo);
  bmpheader.bfSize = bmpheader.bfOffBits + uiTmp;
  bmpinfo.biSize = 0x28;
  bmpinfo.biWidth = width;
  bmpinfo.biHeight = height;
  bmpinfo.biPlanes = 1;
  bmpinfo.biBitCount = 24;
  bmpinfo.biCompression = 0;
  bmpinfo.biSizeImage = uiTmp; 
  bmpinfo.biXPelsPerMeter = 0; 
  bmpinfo.biYPelsPerMeter = 0;
  bmpinfo.biClrUsed = 0;
  bmpinfo.biClrImportant = 0;
  fwrite(&bmpheader, sizeof(bmpheader), 1, f);
  fwrite(&bmpinfo, sizeof(bmpinfo), 1, f);
  u8 tmp[width* 3];
  for (i = 0; i < height / 2; i++)
  {
    memcpy(tmp, &(pRGBBuffer[width* i * 3]), width* 3);
    memcpy(&(pRGBBuffer[width* i * 3]), &(pRGBBuffer[width *(height - 1-i)* 3]), width* 3);
    memcpy(&(pRGBBuffer[width *(height - 1-i)* 3]), tmp, width* 3);
  }
  fwrite(pRGBBuffer, width* height * 3, 1, f);
  fclose(f);
  LOGE("%s(%d) %s\n", __FUNCTION__, __LINE__, szFilename);
  return 1;
}
//-------------------------------------------------------------------------
void* thread_video_PlayLiveP2P(void* arg)
{
  int Chl = (int)arg;
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  bool ret;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_SetCallBack(App.Video[Chl].NetHandle, avCallBack, alarmCallBack, (void*)Chl);
    ret = thNet_Connect_P2P(App.Video[Chl].NetHandle, 0, App.Video[Chl].UID, App.Video[Chl].UIDPsd, 3000, true);
  }
  LOGE("IsConnect:%d \n", thNet_IsConnect(App.Video[Chl].NetHandle));
  int VideoType;
  int Width0;
  int Height0;
  int Width1;
  int Height1;
  int FrameRate0, FrameRate1;
  thNet_GetVideoCfg(App.Video[Chl].NetHandle, &VideoType, &Width0, &Height0, &FrameRate0, &Width1, &Height1, &FrameRate1);
  App.Video[Chl].VideoType = VideoType;
  App.Video[Chl].RecFrameRate = FrameRate1 * 0.8;

  video_decode_Free(Chl);
  video_decode_Init(Chl);
  LOGE("thNet_Play:%d %d %d \n", !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);
  thNet_Play(App.Video[Chl].NetHandle, !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);
}
//-------------------------------------------------------------------------
void* thread_video_PlayLive(void* arg)
{
  int Chl = (int)arg;
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  bool ret;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_SetCallBack(App.Video[Chl].NetHandle, avCallBack, alarmCallBack, (void*)Chl);
    ret = thNet_Connect(App.Video[Chl].NetHandle, App.Video[Chl].UserName, App.Video[Chl].Password, App.Video[Chl].DevIP, App.Video[Chl].DevPort, 3000, 1);
  }
  LOGE("IsConnect:%d \n", thNet_IsConnect(App.Video[Chl].NetHandle));

  int VideoType;
  int Width0;
  int Height0;
  int Width1;
  int Height1;
  int FrameRate0, FrameRate1;
  thNet_GetVideoCfg(App.Video[Chl].NetHandle, &VideoType, &Width0, &Height0, &FrameRate0, &Width1, &Height1, &FrameRate1);
  App.Video[Chl].VideoType = VideoType;
  App.Video[Chl].RecFrameRate = FrameRate1 * 0.8;

  video_decode_Free(Chl);
  video_decode_Init(Chl);
  thNet_Play(App.Video[Chl].NetHandle, !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);
}
//-------------------------------------------------------------------------
void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  int Chl = (int)UserCustom;
  THandle NetHandle = App.Video[Chl].NetHandle;//(THandle)UserCustom;
  if (NetHandle == 0) return;
  int v4 = *((int*)Buf);
  if (App.Video[Chl].GroupType == pt_Cmd) return;
  if (PInfo->Head.VerifyCode == Head_VideoPkt)
  {
    /*
    static i64 FrameTime1 = 0;//PInfo->Frame.FrameTime
    i64 FrameTime2;//PInfo->Frame.FrameTime
    static struct timeval tv1;
    struct timeval tv2;
    if (tv1.tv_sec == 0) gettimeofday(&tv1, NULL);
    gettimeofday(&tv2, NULL);

    if (FrameTime1 == 0) FrameTime1 = PInfo->Frame.FrameTime;
    FrameTime2 = PInfo->Frame.FrameTime;

    int idec =  timeval_dec(&tv2, &tv1);
    tv1 = tv2;
    LOGE("%s(%d) dec:%3d frametime:%3d\n", __FUNCTION__, __LINE__, idec, (FrameTime2 - FrameTime1)/1000);
    #warning " ccccccccccccccccc111cccccccccccccccccccccccccccccccccccccccccccccccc "
    FrameTime1 = FrameTime2;
    */
    if (App.Video[Chl].GroupType == pt_PlayHistory)
    {
      video_decode(Chl, (u8*)Buf, Len, PInfo->Frame.IsIFrame);
      return;
    }

    if (App.Video[Chl].GroupType == pt_PlayLive && PInfo->Frame.StreamType == App.Video[Chl].StreamType)
    {
      video_decode(Chl, (u8*)Buf, Len, PInfo->Frame.IsIFrame);
      return;
    }
  }

  if (PInfo->Head.VerifyCode == Head_AudioPkt)
  {
    PlayAudioBuf(Buf, Len);
    return;
  }
}

void avCallBack1(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
#define ISPRINT  1
  int Chl = (int)UserCustom;
  THandle NetHandle = App.Video[Chl].NetHandle;//(THandle)UserCustom;
  if (NetHandle == 0) return;

  static int videocount = 0;
  static int audiocount = 0;
  static int dt = 0;
  static int dt1 = 0;

  dt1 = time(NULL);
  if (dt1 - dt > 0)
  {
    LOGE("Chl:%d videocount:%d audiocount:%d\n", Chl, videocount, audiocount);
    dt = dt1;
    videocount = 0;
    audiocount = 0;
  }

  int v41 = *((int*)(Buf+0));
  int v42 = *((int*)(Buf+4));
  if (PInfo->Head.VerifyCode == Head_VideoPkt)// ”∆µ∏Ò ΩŒ™H264
  {
    videocount = videocount + Len;
    if (ISPRINT) LOGE("video ");
    if (PInfo->Frame.StreamType == 0) if (ISPRINT) LOGE("mainStream ");
    if (PInfo->Frame.StreamType == 1) if (ISPRINT) LOGE("subStream  ");
    if (ISPRINT) LOGE("IFrame:%d Len:%7d %.8X %.8X\n", PInfo->Frame.IsIFrame, Len, v41, v42);
  } 
  if (PInfo->Head.VerifyCode == Head_AudioPkt)//“Ù∆µ∏Ò ΩŒ™PCM
  {
    audiocount = audiocount + Len;
    if (ISPRINT) LOGE("audio                     Len:%7d %.8X %.8X\n", Len, v41, v42);
  }
}

//-------------------------------------------------------------------------
void alarmCallBack(int AlmType, int AlmTime, int AlmChl, void* UserCustom)
{
  int Chl = (int)UserCustom;
  THandle NetHandle = App.Video[Chl].NetHandle;//(THandle)UserCustom;
  if (NetHandle == 0) return;
  //todo
}
//-------------------------------------------------------------------------
int video_decode_Free(int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  if (App.Video[Chl].sws)
  {
    sws_freeContext(App.Video[Chl].sws);
    App.Video[Chl].sws = NULL;
  }
  if (App.Video[Chl].FrameRGB565)//tmpAV
  {
    av_free(App.Video[Chl].FrameRGB565);
    App.Video[Chl].FrameRGB565 = NULL;
  }
  if (App.Video[Chl].Frame422)
  {
    av_free(App.Video[Chl].Frame422);
    App.Video[Chl].Frame422 = NULL;
  }
  if (App.Video[Chl].ContextV)
  {
    avcodec_close(App.Video[Chl].ContextV);
    av_free(App.Video[Chl].ContextV);
    App.Video[Chl].ContextV = NULL;
  }
  App.Video[Chl].CodecV = NULL;
  return 1;
}
//-------------------------------------------------------------------------
int video_decode_Init(int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  if (App.Video[Chl].encWidth  == 0) App.Video[Chl].encWidth  = 1280;
  if (App.Video[Chl].encHeight == 0) App.Video[Chl].encHeight  = 720;
  av_register_all();
  avcodec_register_all();
  App.Video[Chl].CodecV = avcodec_find_decoder(AV_CODEC_ID_H264);
  //App.Video[Chl].ContextV = avcodec_alloc_context();//old
  App.Video[Chl].ContextV = avcodec_alloc_context3(App.Video[Chl].CodecV);//new
  App.Video[Chl].ContextV->time_base.num = 1; //’‚¡Ω––£∫“ª√Î÷”25÷°
  App.Video[Chl].ContextV->time_base.den = 25;
  App.Video[Chl].ContextV->bit_rate = 25; //≥ı ºªØŒ™0
  App.Video[Chl].ContextV->frame_number = 1; //√ø∞¸“ª∏ˆ ”∆µ÷°
  App.Video[Chl].ContextV->codec_type = AVMEDIA_TYPE_VIDEO;
  App.Video[Chl].ContextV->width = App.Video[Chl].encWidth; //’‚¡Ω––£∫ ”∆µµƒøÌ∂»∫Õ∏ﬂ∂»
  App.Video[Chl].ContextV->height = App.Video[Chl].encHeight;

  App.Video[Chl].ContextV->pix_fmt = AV_PIX_FMT_YUV420P;
  avcodec_open2(App.Video[Chl].ContextV, App.Video[Chl].CodecV, NULL);

  //App.Video[Chl].Frame422 = avcodec_alloc_frame();//old
  //App.Video[Chl].FrameRGB565/*tmpAV*/ = avcodec_alloc_frame();//old
  App.Video[Chl].Frame422 = av_frame_alloc();//new
  App.Video[Chl].FrameRGB565/*tmpAV*/ = av_frame_alloc();//new
  avpicture_fill((AVPicture*)App.Video[Chl].FrameRGB565,
    (u8*)App.Video[Chl].bufferRGB565,
    AV_PIX_FMT_RGB565,
    TEXTURE_WIDTH,
    TEXTURE_HEIGHT);
  /*
  ÕºœÒÀı–°
  SWS_FAST_BILINEAR 228 ÕºœÒŒﬁ√˜œ‘ ß’Ê£¨∏–æı–ßπ˚∫‹≤ª¥Ì°£
  SWS_BILINEAR       95 ∏–æı“≤∫‹≤ª¥Ì£¨±»…œ“ª∏ˆÀ„∑®±ﬂ‘µ∆Ωª¨“ª–©°£
  SWS_BICUBIC        80 ∏–æı≤Ó≤ª∂‡£¨±»…œ…œÀ„∑®±ﬂ‘µ“™∆Ωª¨£¨±»…œ“ªÀ„∑®“™»Ò¿˚°£
  SWS_X              91 ”Î…œ“ªÕºœÒ£¨Œ“ø¥≤ª≥ˆ«¯±°£
  SWS_POINT         427 œ∏Ω⁄±»Ωœ»Ò¿˚£¨ÕºœÒ–ßπ˚±»…œÕº¬‘≤Ó“ªµ„µ„°£
  SWS_AREA          116 ”Î…œ…œÀ„∑®£¨Œ“ø¥≤ª≥ˆ«¯±°£
  SWS_BICUBLIN       87 Õ¨…œ°£
  SWS_GAUSS          80 œ‡∂‘”⁄…œ“ªÀ„∑®£¨“™∆Ωª¨(“≤ø…“‘Àµ «ƒ£∫˝)“ª–©°£
  SWS_SINC           30 œ‡∂‘”⁄…œ“ªÀ„∑®£¨œ∏Ω⁄“™«ÂŒ˙“ª–©°£
  SWS_LANCZOS        70 œ‡∂‘”⁄…œ“ªÀ„∑®£¨“™∆Ωª¨(“≤ø…“‘Àµ «ƒ£∫˝)“ªµ„µ„£¨º∏∫ıŒﬁ«¯±°£
  SWS_SPLINE         47 ∫Õ…œ“ª∏ˆÀ„∑®£¨Œ“ø¥≤ª≥ˆ«¯±°£

  ÕºœÒ∑≈¥Û
  SWS_FAST_BILINEAR 103 ÕºœÒŒﬁ√˜œ‘ ß’Ê£¨∏–æı–ßπ˚∫‹≤ª¥Ì°£
  SWS_BILINEAR      100 ∫Õ…œÕºø¥≤ª≥ˆ«¯±°£
  SWS_BICUBIC        78 œ‡∂‘…œÕº£¨∏–æıœ∏Ω⁄«ÂŒ˙“ªµ„µ„°£
  SWS_X             106 ”Î…œ…œÕºŒﬁ«¯±°£
  SWS_POINT         112 ±ﬂ‘µ”–√˜œ‘æ‚≥›°£
  SWS_AREA          114 ±ﬂ‘µ”–≤ª√˜œ‘æ‚≥›°£
  SWS_BICUBLIN       95 ”Î…œ…œ…œÕºº∏∫ıŒﬁ«¯±°£
  SWS_GAUSS          86 ±»…œÕº±ﬂ‘µ¬‘Œ¢«Â≥˛“ªµ„°£
  SWS_SINC           20 ”Î…œ…œÕºŒﬁ«¯±°£
  SWS_LANCZOS        64 ”Î…œÕºŒﬁ«¯±°£
  SWS_SPLINE         40 ”Î…œÕºŒﬁ«¯±°£
  */
  App.Video[Chl].sws = sws_getContext(
    App.Video[Chl].ContextV->width,
    App.Video[Chl].ContextV->height,
    App.Video[Chl].ContextV->pix_fmt,
    TEXTURE_WIDTH,
    TEXTURE_HEIGHT,
    AV_PIX_FMT_RGB565,
    SWS_POINT,
    NULL,
    NULL,
    NULL);
  return 1;
}
//-------------------------------------------------------------------------
void video_decode_SnapShot(int Chl)
{
  if (App.Video[Chl].IsSnapShot == false) return;
  App.Video[Chl].IsSnapShot = false;

  int w = App.Video[Chl].ContextV->width;
  int h = App.Video[Chl].ContextV->height;
  int linesize[4] = {w * 3, 0, 0, 0};
  //PIX_FMT_BGR24 && SaveToBmp
  //PIX_FMT_RGB24 && rgb24_jpg
    struct SwsContext* sws = sws_getContext(w, h, AV_PIX_FMT_YUV420P, w, h, AV_PIX_FMT_RGB24, SWS_FAST_BILINEAR, NULL, NULL, NULL);
  if (sws)
  {
    char* rgbBuf = (char*)malloc(w * h * 3);
    sws_scale(sws, 
      (const u8* const*)App.Video[Chl].Frame422->data,
      App.Video[Chl].Frame422->linesize,
      0,
      h,
      (u8**)&rgbBuf,
      linesize);
    sws_freeContext(sws);
    //    SaveToBmp(rgbBuf, App.Video[Chl].FileName_Jpg, w, h); //…˙≥…Õº∆¨
    rgb24_jpg(App.Video[Chl].FileName_Jpg, rgbBuf, w, h);
    free(rgbBuf);
  }
}
//-------------------------------------------------------------------------
int video_decode(int Chl, u8* Buf, int Len, bool IsIFrame)
{
  AVPacket packet;
  packet.data = Buf;
  packet.size = Len;
  int gotPicture = 0;
  int size = 0;
#ifdef IS_TEST
  struct timeval tv1, tv2; gettimeofday(&tv1, NULL);
#endif
  while (packet.size > 0)
  {
    size = avcodec_decode_video2(App.Video[Chl].ContextV, App.Video[Chl].Frame422, &gotPicture, &packet);
    if (gotPicture == 0)
    {
      avcodec_decode_video2(App.Video[Chl].ContextV, App.Video[Chl].Frame422, &gotPicture, &packet);      
      break;
    }
    packet.size -= size;
    packet.data += size;
  }
#ifdef IS_TEST
  gettimeofday(&tv2, NULL);
  idec = timeval_dec(&tv2, &tv1);
#endif
  if (gotPicture <= 0) return false;

  if (!App.Video[Chl].IFrameFlag) App.Video[Chl].IFrameFlag = true;
  if ((App.Video[Chl].encWidth != App.Video[Chl].ContextV->width) || (App.Video[Chl].encHeight != App.Video[Chl].ContextV->height))
  {
    App.Video[Chl].encWidth = App.Video[Chl].ContextV->width;
    App.Video[Chl].encHeight = App.Video[Chl].ContextV->height;

    sws_freeContext(App.Video[Chl].sws);

    App.Video[Chl].sws = sws_getContext(
      App.Video[Chl].ContextV->width,
      App.Video[Chl].ContextV->height,
      App.Video[Chl].ContextV->pix_fmt,
      TEXTURE_WIDTH,
      TEXTURE_HEIGHT,
      AV_PIX_FMT_RGB565,
      SWS_POINT,
      NULL,
      NULL,
      NULL);
    LOGE("sws_getContext Chl:%d w:%d h:%d\n", Chl, App.Video[Chl].encWidth, App.Video[Chl].encHeight);
  }

  video_decode_SnapShot(Chl);
  video_decode_Record(Chl, Buf, Len, IsIFrame);

  pthread_cond_signal(&th_sync_cond);

  return (gotPicture > 0);
}
//-----------------------------------------------------------------------------
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//-----------------------------------------------------------------------------
JavaVM* sJavaVM  = 0;
jclass cls_Search  = 0;

//-----------------------------------------------------------------------------
static JNINativeMethod MethodLst[] = {
  //miniupnpc
  {"jGetWlanIP", "()Ljava/lang/String;", (void*)jGetWlanIP},
  //test
  {"jtmp_Test", "()I", (void*)jtmp_Test},
  //common
  {"jGetLocalIP", "()Ljava/lang/String;", (void*)jGetLocalIP},
  //smartconfig
  {"jsmt_Init", "()I", (void*)jsmt_Init},
  {"jsmt_Stop", "()I", (void*)jsmt_Stop},
  {"jsmt_Start", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I", (void*)jsmt_Start},
  //search
  {"jthNet_SearchDev_old", "()Ljava/lang/String;", (void*)jthNet_SearchDev_old},
  {"jthSearch_Init", "()I", (void*)jthSearch_Init},
  {"jthSearch_SetCallBack", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void*)jthSearch_SetCallBack},
  {"jthSearch_SetWiFiCfg", "(Ljava/lang/String;Ljava/lang/String;)I", (void*)jthSearch_SetWiFiCfg},
  {"jthSearch_SearchDevice", "()I", (void*)jthSearch_SearchDevice},
  {"jthSearch_Free", "()I", (void*)jthSearch_Free},

  //audio
  {"jaudio_CreatePlay","()I", (void*)jaudio_CreatePlay},
  {"jaudio_Shutdown","()I", (void*)jaudio_Shutdown},
  {"jaudio_CreateTalk","(I)I",(void*)jaudio_CreateTalk},
  {"jaudio_StartTalk","(I)I",(void*)jaudio_StartTalk},
  {"jaudio_StopTalk","(I)I",(void*)jaudio_StopTalk},
  //render
  {"jopengl_Resize", "(III)I", (void*)jopengl_Resize},
  {"jopengl_Render", "(I)I", (void*)jopengl_Render},
  //local
  {"jlocal_StartRec", "(ILjava/lang/String;)I", (void*)jlocal_StartRec},
  {"jlocal_StopRec", "(I)I", (void*)jlocal_StopRec},
  {"jlocal_SnapShot", "(ILjava/lang/String;)I", (void*)jlocal_SnapShot},
  //net
  {"jthNet_SetAudioIsMute","(II)I",(void*)jthNet_SetAudioIsMute},
  {"jthNet_GetURL","(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",(void*)jthNet_GetURL},
  {"jthNet_GetImage","(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",(void*)jthNet_GetImage},

  {"jthNet_PlayLive", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;II)I", (void*)jthNet_PlayLive},
  {"jthNet_PlayLiveP2P", "(ILjava/lang/String;Ljava/lang/String;I)I", (void*)jthNet_PlayLiveP2P},
  {"jthNet_PlayRemote", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I", (void*)jthNet_PlayRemote},
  {"jthNet_PlayRemoteP2P", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void*)jthNet_PlayRemoteP2P},
  {"jthNet_StopPlay", "(I)I", (void*)jthNet_StopPlay},
  {"jthNet_SetResolution","(II)I",(void*)jthNet_SetResolution},
  {"jtmp_DisconnectP2P","(I)I",(void*)jtmp_DisconnectP2P},
};
//-----------------------------------------------------------------------------
int JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
#ifndef NELEM
#define NELEM(x) ((int)(sizeof(x) / sizeof((x)[0])))
#endif
  JNIEnv* env = NULL;
  jclass obj;
  sJavaVM = vm;
  if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_4) != JNI_OK) return  -1;
  assert(env != NULL);
  obj = (*env)->FindClass(env, CLASS_PATH_NAME);
  if (obj == NULL) return JNI_FALSE;
  if ((*env)->RegisterNatives(env, obj, MethodLst, NELEM(MethodLst)) < 0) return JNI_FALSE;
  return JNI_VERSION_1_4;
}
//-----------------------------------------------------------------------------
jstring jGetLocalIP(JNIEnv* env, jclass obj)
{
  return (*env)->NewStringUTF(env, GetLocalIP());
}
//-----------------------------------------------------------------------------
int jsmt_Init(JNIEnv* env, jclass obj)
{
  return InitSmartConnection();
}
//-----------------------------------------------------------------------------
int jsmt_Stop(JNIEnv* env, jclass obj)
{
  return StopSmartConnection();
}
//-----------------------------------------------------------------------------
int jsmt_Start(JNIEnv* env, jclass obj, jstring nSSID, jstring nPassword, jstring nTlv, jstring nTarget, int nAuthMode)
{
  const char* SSID = (*env)->GetStringUTFChars(env, nSSID, NULL);
  const char* Password = (*env)->GetStringUTFChars(env, nPassword, NULL);
  const char* Tlv = (*env)->GetStringUTFChars(env, nTlv, NULL);
  const char* Target = (*env)->GetStringUTFChars(env, nTarget, NULL);
  char AuthMode = nAuthMode;
  int ret = StartSmartConnection(SSID, Password, (unsigned char*)Tlv, strlen(Tlv), Target, AuthMode);
  (*env)->ReleaseStringUTFChars(env, nSSID, SSID);
  (*env)->ReleaseStringUTFChars(env, nPassword, Password);
  (*env)->ReleaseStringUTFChars(env, nTlv, Tlv);
  (*env)->ReleaseStringUTFChars(env, nTarget, Target);
  return ret;
}
//-----------------------------------------------------------------------------
void jniSearchDevCallBack(u32 SN, int DevType, int VideoChlCount, int DataPort, int HttpPort, char* DevName,
                          char* DevIP, char* DevMAC, char* SubMask, char* Gateway, char* DNS1,
                          char* DDNSServer, char* DDNSHost, char* UID)
{  
  /*
  1.‘⁄JNI_OnLoad÷–£¨±£¥ÊJavaVM*£¨’‚ «øÁœﬂ≥Ãµƒ£¨≥÷æ√”––ßµƒ£¨∂¯JNIEnv*‘Ú «µ±«∞œﬂ≥Ã”––ßµƒ°£
  “ªµ©∆Ù∂Øœﬂ≥Ã£¨”√AttachCurrentThread∑Ω∑®ªÒµ√env°£
  2.Õ®π˝JavaVM*∫ÕJNIEnvø…“‘≤È’“µΩjclass°£
  3.∞—jclass◊™≥…»´æ÷“˝”√£¨ π∆‰øÁœﬂ≥Ã°£
  4.»ª∫ÛæÕø…“‘’˝≥£µÿµ˜”√ƒ„œÎµ˜”√µƒ∑Ω∑®¡À°£
  5.”√ÕÍ∫Û£¨±Õ¸¡ÀdeleteµÙ¥¥Ω®µƒ»´æ÷“˝”√∫Õµ˜”√DetachCurrentThread∑Ω∑®°£
  */
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  JNIEnv* env;
  jmethodID funcID = 0;
  char uDevName[100];
  int i, ret;
  bool IsFind = false;
  for (i=0; i<SEARFH_SNLST_COUNT; i++)
  {
    if (App.Search.search_SNLst[i] == 0x00) break;
    if (App.Search.search_SNLst[i] == SN)
    {
      IsFind = true;
      break;
    }
  }
  if (IsFind) return;
  App.Search.search_SNLst[i] = SN;
  ret = (*sJavaVM)->AttachCurrentThread(sJavaVM, &env, NULL);
  if (ret < 0) return;
  unsigned long pnErrC;
  UcnvConvert(uDevName, 100, DevName, &pnErrC);
  jstring nDevName = (*env)->NewStringUTF(env, uDevName);
  jstring nDevIP = (*env)->NewStringUTF(env, DevIP);
  jstring nDDNSServer = (*env)->NewStringUTF(env, DDNSServer);
  jstring nDDNSHost = (*env)->NewStringUTF(env, DDNSHost);
  jstring nUID = ( *env)->NewStringUTF (env, UID);

  //funcID = (*env)->GetStaticMethodID(env, cls_Search, "SearchDevCallBack", "(IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
  funcID = ( *env)->GetStaticMethodID (env, cls_Search, App.Search.SearchFuncName, App.Search.SearchParamName);
  if ( !funcID) return;
  (*env)->CallStaticVoidMethod (env, cls_Search, funcID, SN, DataPort, HttpPort,
    nDevName, nDevIP, nDDNSServer, nDDNSHost, nUID);
  ret = (*sJavaVM)->DetachCurrentThread(sJavaVM);
}
//-----------------------------------------------------------------------------
int jthSearch_SetCallBack(JNIEnv* env, jclass obj, jstring ClassName, jstring FuncName, jstring ParamName)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);

  strcpy(App.Search.cls_SearchName, (*env)->GetStringUTFChars(env, ClassName, NULL));
  strcpy(App.Search.SearchFuncName, (*env)->GetStringUTFChars(env, FuncName, NULL));
  strcpy(App.Search.SearchParamName, (*env)->GetStringUTFChars(env, ParamName, NULL));

  jclass tmp = (*env)->FindClass(env, App.Search.cls_SearchName);
  cls_Search = (*env)->NewGlobalRef(env, tmp);
  //  (*env)->ReleaseStringUTFChars(env, ClassName, _cls_SearchName);
  //  (*env)->ReleaseStringUTFChars(env, FuncName, _SearchFuncName);
  //  (*env)->ReleaseStringUTFChars(env, ParamName, _SearchParamName);
  return 1;
}
//-----------------------------------------------------------------------------
int jthSearch_Init(JNIEnv* env, jclass obj)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  memset(App.Search.search_SNLst, 0, sizeof(App.Search.search_SNLst));
  return thSearch_Init(jniSearchDevCallBack);
}
//-----------------------------------------------------------------------------
int jthSearch_SetWiFiCfg(JNIEnv* env, jclass obj, jstring nSSID, jstring nPassword)
{
  const char* SSID = (*env)->GetStringUTFChars(env, nSSID, NULL);
  const char* Password = (*env)->GetStringUTFChars(env, nPassword, NULL);
  int ret = thSearch_SetWiFiCfg(NULL, (char*)SSID, (char*)Password);
  (*env)->ReleaseStringUTFChars(env, nSSID, SSID);
  (*env)->ReleaseStringUTFChars(env, nPassword, Password);
  return ret;
}
//-----------------------------------------------------------------------------
int jthSearch_SearchDevice(JNIEnv* env, jclass obj)
{
  return thSearch_SearchDevice(NULL);
}
//-----------------------------------------------------------------------------
int jthSearch_Free(JNIEnv* env, jclass obj)
{
  return thSearch_Free();
}
//-----------------------------------------------------------------------------
int jaudio_CreatePlay(JNIEnv* env, jclass clazz)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  SLresult result;
  // create engine
  result = slCreateEngine(&App.Audio.engineObject, 0, NULL, 0, NULL, NULL);
  assert(SL_RESULT_SUCCESS == result);
  // realize the engine
  result = (*App.Audio.engineObject)->Realize(App.Audio.engineObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);
  // get the engine interface, which is needed in order to create other objects
  result = (*App.Audio.engineObject)->GetInterface(App.Audio.engineObject, SL_IID_ENGINE, &App.Audio.engineEngine);
  assert(SL_RESULT_SUCCESS == result);
  // create output mix, with environmental reverb specified as a non-required interface
  const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
  const SLboolean req[1] = {SL_BOOLEAN_FALSE};
  result = (*App.Audio.engineEngine)->CreateOutputMix(App.Audio.engineEngine, &App.Audio.outputMixObject, 1, ids, req);
  assert(SL_RESULT_SUCCESS == result);
  // realize the output mix
  result = (*App.Audio.outputMixObject)->Realize(App.Audio.outputMixObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);

  //int jaudio_CreatePlayQueue(JNIEnv* env, jclass clazz)
  // configure audio source
  SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
  SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8, SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16, SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
  SLDataSource audioSrc = {&loc_bufq, &format_pcm};

  // configure audio sink
  SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, App.Audio.outputMixObject};
  SLDataSink audioSnk = {&loc_outmix, NULL};

  // create audio player
  const SLInterfaceID ids1[2] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND};
  const SLboolean req1[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
  result = (*App.Audio.engineEngine)->CreateAudioPlayer(App.Audio.engineEngine, &App.Audio.bqPlayerObject, &audioSrc, &audioSnk, 2, ids1, req1);
  assert(SL_RESULT_SUCCESS == result);

  // realize the player
  result = (*App.Audio.bqPlayerObject)->Realize(App.Audio.bqPlayerObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);

  // get the play interface
  result = (*App.Audio.bqPlayerObject)->GetInterface(App.Audio.bqPlayerObject, SL_IID_PLAY, &App.Audio.bqPlayerPlay);
  assert(SL_RESULT_SUCCESS == result);

  // get the buffer queue interface
  result = (*App.Audio.bqPlayerObject)->GetInterface(App.Audio.bqPlayerObject, SL_IID_BUFFERQUEUE,
    &App.Audio.bqPlayerBufferQueue);
  assert(SL_RESULT_SUCCESS == result);

  // register callback on the buffer queue
  result = (*App.Audio.bqPlayerBufferQueue)->RegisterCallback(App.Audio.bqPlayerBufferQueue, callback_AudioQueuePlay, NULL);
  assert(SL_RESULT_SUCCESS == result);

  // get the effect send interface
  result = (*App.Audio.bqPlayerObject)->GetInterface(App.Audio.bqPlayerObject, SL_IID_EFFECTSEND,
    &App.Audio.bqPlayerEffectSend);
  assert(SL_RESULT_SUCCESS == result);

  // set the player's state to playing
  result = (*App.Audio.bqPlayerPlay)->SetPlayState(App.Audio.bqPlayerPlay, SL_PLAYSTATE_PLAYING);
  assert(SL_RESULT_SUCCESS == result);

  App.Audio.outrb = AudioQueue_Create(2048*sizeof(short)*1000);

  App.Audio.nextBuffer = (short *) malloc ( sizeof(short) * 2048 );
  App.Audio.nextSize = 2048;
  App.Audio._isplay = 0;
}
//-----------------------------------------------------------------------------
int jaudio_CreateTalk(JNIEnv* env, jclass clazz, int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  SLresult result;
  // configure audio source
  SLDataLocator_IODevice loc_dev = {SL_DATALOCATOR_IODEVICE, SL_IODEVICE_AUDIOINPUT,
    SL_DEFAULTDEVICEID_AUDIOINPUT, NULL};
  SLDataSource audioSrc = {&loc_dev, NULL};

  // configure audio sink
  SLDataLocator_AndroidSimpleBufferQueue loc_bq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
  SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
    SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
    SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
  SLDataSink audioSnk = {&loc_bq, &format_pcm};
  // create audio recorder
  const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
  const SLboolean req[1] = {SL_BOOLEAN_TRUE};
  result = (*App.Audio.engineEngine)->CreateAudioRecorder(App.Audio.engineEngine, &App.Audio.recorderObject, &audioSrc, &audioSnk, 1, id, req);
  if (SL_RESULT_SUCCESS != result) return;

  // realize the audio recorder
  result = (*App.Audio.recorderObject)->Realize(App.Audio.recorderObject, SL_BOOLEAN_FALSE);
  if (SL_RESULT_SUCCESS != result) return;

  // get the record interface
  result = (*App.Audio.recorderObject)->GetInterface(App.Audio.recorderObject, SL_IID_RECORD, &App.Audio.recorderRecord);
  assert(SL_RESULT_SUCCESS == result);

  // get the buffer queue interface
  result = (*App.Audio.recorderObject)->GetInterface(App.Audio.recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
    &App.Audio.recorderBufferQueue);
  assert(SL_RESULT_SUCCESS == result);

  // register callback on the buffer queue
  result = (*App.Audio.recorderBufferQueue)->RegisterCallback(App.Audio.recorderBufferQueue, callback_AudioQueueTalk, (void*)Chl);
  assert(SL_RESULT_SUCCESS == result);
}
//-----------------------------------------------------------------------------
int jaudio_StartTalk(JNIEnv* env, jclass clazz, int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  SLresult result;

  // in case already recording, stop recording and clear buffer queue
  result = (*App.Audio.recorderRecord)->SetRecordState(App.Audio.recorderRecord, SL_RECORDSTATE_STOPPED);
  assert(SL_RESULT_SUCCESS == result);
  result = (*App.Audio.recorderBufferQueue)->Clear(App.Audio.recorderBufferQueue);
  assert(SL_RESULT_SUCCESS == result);

  result = (*App.Audio.recorderBufferQueue)->Enqueue(App.Audio.recorderBufferQueue, App.Audio.recorderBuffer[0], RECORDER_FRAMES * sizeof(short));
  assert(SL_RESULT_SUCCESS == result);
  // start recording
  result = (*App.Audio.recorderRecord)->SetRecordState(App.Audio.recorderRecord, SL_RECORDSTATE_RECORDING);
  assert(SL_RESULT_SUCCESS == result);
}
//-----------------------------------------------------------------------------
int jaudio_StopTalk(JNIEnv* env, jclass clazz, int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  // quit_ = true;
  SLresult result;
  // in case already recording, stop recording and clear buffer queue
  result = (*App.Audio.recorderRecord)->SetRecordState(App.Audio.recorderRecord, SL_RECORDSTATE_STOPPED);

  assert(SL_RESULT_SUCCESS == result);
  result = (*App.Audio.recorderBufferQueue)->Clear(App.Audio.recorderBufferQueue);
  assert(SL_RESULT_SUCCESS == result);
}
//-----------------------------------------------------------------------------
int jaudio_Shutdown(JNIEnv* env, jclass clazz)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  if (App.Audio.bqPlayerObject != NULL)
  {
    (*App.Audio.bqPlayerObject)->Destroy(App.Audio.bqPlayerObject);
    App.Audio.bqPlayerObject        = NULL;
    App.Audio.bqPlayerPlay          = NULL;
    App.Audio.bqPlayerBufferQueue   = NULL;

  }
  // destroy output mix object, and invalidate all associated interfaces
  if (App.Audio.outputMixObject != NULL) {
    (*App.Audio.outputMixObject)->Destroy(App.Audio.outputMixObject);
    App.Audio.outputMixObject = NULL;
  }

  // destroy engine object, and invalidate all associated interfaces
  if (App.Audio.engineObject != NULL)
  {
    (*App.Audio.engineObject)->Destroy(App.Audio.engineObject);
    App.Audio.engineObject = NULL;
    App.Audio.engineEngine = NULL;
  }
  AudioQueue_Free(App.Audio.outrb);
  App.Audio.outrb = NULL;
  free(App.Audio.nextBuffer);
  App.Audio.nextBuffer = NULL;
}
//-----------------------------------------------------------------------------
void SearchDevCallBack_old(u32 SN, int DevType, int VideoChlCount, int DataPort, int HttpPort, char* DevName, char* DevIP, char* DevMAC, char* SubMask, char* Gateway, char* DNS1, char* DDNSServer, char* DDNSHost, char* UID)
{
  char Str[1000];
  char uDevName[500];

  int i;
  bool IsFind = false;
  for (i=0; i<SEARFH_SNLST_COUNT; i++)
  {
    if (App.Search.search_SNLst[i] == 0x00) break;
    if (App.Search.search_SNLst[i] == SN)
    {
      IsFind = true;
      break;
    }
  }
  if (IsFind) return;
  App.Search.search_SNLst[i] = SN;

  memset(uDevName, 0, strlen(uDevName));
  unsigned long pnErrC;
  UcnvConvert(uDevName, 500, DevName, &pnErrC);
  memset(Str, 0, strlen(Str));
  if (App.Search.searchCount != 0) strcat(App.Search.searchtmpBuf, "@");
  if (DDNSServer[0] == 0x00) DDNSServer = "NULL";
  if (DDNSHost[0] == 0x00) DDNSHost = "NULL";
  if (UID[0]      == 0x00) UID      = "NULL";
  sprintf(Str, "%s,%.8x,%d,%d,%s,%s,%s,%s,%s", DevIP, SN, DataPort, HttpPort, DevMAC, uDevName, DDNSServer, DDNSHost, UID);
  if (strlen(App.Search.searchtmpBuf) >= 10000 - 100) return;
  strcat(App.Search.searchtmpBuf, Str);
  App.Search.searchCount++;
  //LOGE("SearchDevCallBack DevIP:%s(%d) %s|%s\n", DevIP, DataPort, DevName, uDevName);
  LOGE("SearchDevCallBack Str:%s\n", Str);
}
//-----------------------------------------------------------------------------
jstring jthNet_SearchDev_old(JNIEnv* env, jclass obj)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Search.searchCount = 0;
  memset(App.Search.searchtmpBuf, 0, sizeof(App.Search.searchtmpBuf));
  memset(App.Search.search_SNLst, 0, sizeof(u32)*SEARFH_SNLST_COUNT);

  time_t dt;
  thSearch_Init(SearchDevCallBack_old);

  dt = time(NULL);
  while (1)
  {
    thSearch_SearchDevice(NULL);
    usleep(1000* 500);
    if (time(NULL) - dt >= 3)
      break;
  }
  thSearch_Free();
  LOGE("%s(%d) Count:%d\n", __FUNCTION__, __LINE__, App.Search.searchCount);
  jstring searchMsg = NULL;
  LOGE("jthNet_SearchDev_old Str:%s\n", App.Search.searchtmpBuf);
  searchMsg = (*env)->NewStringUTF(env, App.Search.searchtmpBuf);
  return searchMsg;
}
//-----------------------------------------------------------------------------
int jthNet_GetImage(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nFileName)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  const char* UID = (*env)->GetStringUTFChars(env, nUID, NULL);
  const char* UIDPsd = (*env)->GetStringUTFChars(env, nUIDPsd, NULL);
  const char* FileName = (*env)->GetStringUTFChars(env, nFileName, NULL);
  strcpy(App.Video[Chl].UID, UID);
  strcpy(App.Video[Chl].UIDPsd, UIDPsd);
  char Buf[65536* 10];
  int BufLen;

  bool ret;
  int alm = -1;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_Connect_P2P(App.Video[Chl].NetHandle, 0, (char*)App.Video[Chl].UID, (char*)App.Video[Chl].UIDPsd, 3000, true);
  }

  if (thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    char* url = "http://192.168.0.170/cfg.cgi?User=admin&Psd=admin&MsgID=72";
    ret = thNet_HttpGet(App.Video[Chl].NetHandle, url, Buf, &BufLen);
    alm = Buf[0];
    LOGE("arm is %d\n", alm);
    if (alm != 0)
    {
      url = "http://192.168.0.170/cfg.cgi?User=admin&Psd=admin&MsgID=20&chl=1";
      ret = thNet_HttpGet(App.Video[Chl].NetHandle, url, Buf, &BufLen);
      FILE* f = fopen((char*)FileName, "w+b");
      fwrite(Buf, BufLen, 1, f);
      fclose(f);
    }
  }
  (*env)->ReleaseStringUTFChars(env, nUID, UID);
  (*env)->ReleaseStringUTFChars(env, nUIDPsd, UIDPsd);
  (*env)->ReleaseStringUTFChars(env, nFileName, FileName);
  return alm;
}
//-------------------------------------------------------------------------
jstring jthNet_GetURL(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nURL)
{
  LOGE("%s(%d) NetHandle:%ld\n", __FUNCTION__, __LINE__, App.Video[Chl].NetHandle);
  bool ret;
  jstring Result = NULL;
  const char* UID = (*env)->GetStringUTFChars(env, nUID, NULL);
  const char* UIDPsd = (*env)->GetStringUTFChars(env, nUIDPsd, NULL);
  const char* url = (*env)->GetStringUTFChars(env, nURL, NULL);

  strcpy(App.Video[Chl].UID, UID);
  strcpy(App.Video[Chl].UIDPsd, UIDPsd);

  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_Connect_P2P(App.Video[Chl].NetHandle, 0, (char*)App.Video[Chl].UID, (char*)App.Video[Chl].UIDPsd, 3000, true);
  }

  if (thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    char Buf[65536];
    char conv[65536];
    memset(Buf, 0, 65536);
    memset(conv, 0, 65536);
    int BufLen;
    ret = thNet_HttpGet(App.Video[Chl].NetHandle, (char*)url, Buf, &BufLen);
    unsigned long pnErrC;
    UcnvConvert(conv, 65536, Buf, &pnErrC);
    jstring responseUrl = (*env)->NewStringUTF(env, conv);
    Result = responseUrl;
  }
  (*env)->ReleaseStringUTFChars(env, nUID, UID);
  (*env)->ReleaseStringUTFChars(env, nUIDPsd, UIDPsd);
  (*env)->ReleaseStringUTFChars(env, nURL, url);
  return Result;
}
//-------------------------------------------------------------------------
void check_gl_error(const char* op)
{
  GLint error;
  for (error = glGetError(); error; error = glGetError())
    LOGI("after %s() glError (0x%x)\n", op, error);
}
//-------------------------------------------------------------------------
int jopengl_Resize(JNIEnv* env, jclass obj, int Chl, int w, int h)
{
  int i;
  App.Video[Chl].ScreenWidth = w; //the device width and height
  App.Video[Chl].ScreenHeight = h;

#ifdef IS_RENDER_YUV420
  glDeleteTextures(3, App.Video[Chl].gl_texture);
  glGenTextures(3, App.Video[Chl].gl_texture);
#else
  static GLuint s_disable_caps[] = {GL_FOG, GL_LIGHTING, GL_CULL_FACE, GL_ALPHA_TEST, GL_BLEND, GL_COLOR_LOGIC_OP, GL_DITHER, GL_STENCIL_TEST, GL_DEPTH_TEST, GL_COLOR_MATERIAL, 0};

  glDeleteTextures(1, &App.Video[Chl].gl_texture);
  GLuint* start = s_disable_caps;
  while (*start) glDisable(*start++);
  glEnable(GL_TEXTURE_2D);
  glGenTextures(1, &App.Video[Chl].gl_texture);
  glBindTexture(GL_TEXTURE_2D, App.Video[Chl].gl_texture);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glShadeModel(GL_FLAT);
  glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
  int rect[4] = {0, TEXTURE_HEIGHT, TEXTURE_WIDTH,  -TEXTURE_HEIGHT};
  glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, rect);
#endif
  LOGE("%s(%d) w:%d h:%d\n", __FUNCTION__, __LINE__, w, h);
  return 1;
}
//-------------------------------------------------------------------------
int jopengl_Render(JNIEnv* env, jclass obj, int Chl)
{
  if (!App.Video[Chl].IFrameFlag) return 0;
  if (App.Video[Chl].GroupType == pt_Cmd) return 0;

  pthread_mutex_lock(&th_mutex_lock);
  pthread_cond_wait(&th_sync_cond, &th_mutex_lock);
  pthread_mutex_unlock(&th_mutex_lock);
#ifdef IS_RENDER_YUV420
#warning "1 IS_RENDER_YUV420 IS_RENDER_YUV420 IS_RENDER_YUV420 "
  u8* data = App.Video[Chl].Frame422->data;
  glClear(GL_COLOR_BUFFER_BIT);


  long pixels[3];
  pixels[0] = data + 0;
  pixels[1] = data + App.Video[Chl].encWidth * App.Video[Chl].encHeight;
  pixels[2] = data + App.Video[Chl].encWidth * App.Video[Chl].encHeight * 5 / 4;
  int widths[3] = {App.Video[Chl].encWidth, App.Video[Chl].encWidth/2, App.Video[Chl].encWidth/2};
  int heights[3] = {App.Video[Chl].encHeight, App.Video[Chl].encHeight/2, App.Video[Chl].encHeight/2};
  for (i = 0; i < 3; i++)
  {
    glActiveTexture(GL_TEXTURE0 + i);
    glBindTexture(GL_TEXTURE_2D, App.Video[Chl].gl_texture[i]);
    glUniform1i(App.Video[Chl]._uniformSamplers[i], i);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, widths[i], heights[i], 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, pixels[i]);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  }
  
  //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, App.Video[Chl].bufferRGB565);
  //glDrawTexiOES(0, 0, 0, App.Video[Chl].ScreenWidth, App.Video[Chl].ScreenHeight);
  const char g_indices[] = { 0, 3, 2, 0, 2, 1 }; 
  glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, g_indices); 
#else
#ifdef IS_TEST
  struct timeval tv1, tv2, tv3;
  gettimeofday(&tv1, NULL);
#endif
  sws_scale(App.Video[Chl].sws,
    (const u8* const*)App.Video[Chl].Frame422->data,
    App.Video[Chl].Frame422->linesize,
    0,
    App.Video[Chl].ContextV->height,
    App.Video[Chl].FrameRGB565->data,
    App.Video[Chl].FrameRGB565->linesize);
#ifdef IS_TEST
  gettimeofday(&tv2, NULL); 
#endif
  glClear(GL_COLOR_BUFFER_BIT);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, App.Video[Chl].bufferRGB565);
  glDrawTexiOES(0, 0, 0, App.Video[Chl].ScreenWidth, App.Video[Chl].ScreenHeight);
#ifdef IS_TEST
  gettimeofday(&tv3, NULL);
  isws = timeval_dec(&tv2, &tv1);
  irender =  timeval_dec(&tv3, &tv2);
  LOGE("%s(%d) dec:%3d sws:%3d render:%3d\n", __FUNCTION__, __LINE__, idec, isws, irender);
#endif
#endif
  return 1;
}

//-------------------------------------------------------------------------
void video_decode_Record(int Chl, u8* Buf, int Len, bool IsIFrame)
{
#ifdef IS_NEW_RECORD
  if (!App.Video[Chl].IsRec) return;

  if (App.Video[Chl].Flag_StartRec && IsIFrame)
  {
    App.Video[Chl].Flag_StartRec = false;
  }

  if (App.Video[Chl].Flag_StartRec == false && Len > 0)
  {
    AVPacket pkt;
    av_init_packet(&pkt);
    pkt.stream_index = 0;//App.Video[Chl].avStreamV->index;
    pkt.data = Buf;
    pkt.size = Len;
    /*
    pkt.flags |= AV_PKT_FLAG_KEY;
    pts = pkt.pts;
    pkt.pts += last_pts;
    dts = pkt.dts;
    pkt.dts += last_dts;
    pkt.stream_index = 0;
    */
    static int num = 1;
    LOGE("frame %d\n", num++);
    av_interleaved_write_frame(App.Video[Chl].avContext, &pkt);
  }
#else
  if (App.Video[Chl].IsRec)
  {
    //1 Create File
    if (App.Video[Chl].Flag_StartRec)
    {
      App.Video[Chl].Flag_StartRec = false;

      App.Video[Chl].avfmt = av_guess_format(NULL, App.Video[Chl].FileName_Rec, NULL);
      if (!App.Video[Chl].avfmt) App.Video[Chl].avfmt = av_guess_format("mpeg", NULL, NULL);
      if (!App.Video[Chl].avfmt) return;
      App.Video[Chl].avContext = avformat_alloc_context();
      if (!App.Video[Chl].avContext) return;
      App.Video[Chl].avContext->oformat = App.Video[Chl].avfmt;
      snprintf(App.Video[Chl].avContext->filename, sizeof(App.Video[Chl].avContext->filename), "%s", App.Video[Chl].FileName_Rec);
      App.Video[Chl].avContext->oformat->video_codec = AV_CODEC_ID_MPEG4;//CODEC_ID_H264;
      App.Video[Chl].avfmt = App.Video[Chl].avContext->oformat;
      App.Video[Chl].avStreamV = NULL;
      if (App.Video[Chl].avfmt->video_codec != AV_CODEC_ID_NONE)
      {
        avcodec_register_all();
        //App.Video[Chl].avStreamV = av_new_stream(App.Video[Chl].avContext, 0);//old
        App.Video[Chl].avStreamV = avformat_new_stream(App.Video[Chl].avContext, 0);//old
        AVCodecContext* ic = App.Video[Chl].avStreamV->codec;
        AVCodec* codec = avcodec_find_encoder(App.Video[Chl].avfmt->video_codec);
        avcodec_get_context_defaults3(ic, codec);
        ic->codec_id = App.Video[Chl].avfmt->video_codec;
        ic->bit_rate = 3000000;
        ic->width = App.Video[Chl].ContextV->width;
        ic->height = App.Video[Chl].ContextV->height;
        ic->time_base.den = App.Video[Chl].RecFrameRate;
        ic->time_base.num = 1;
        ic->gop_size = 12;
        ic->pix_fmt = AV_PIX_FMT_YUV420P;
        if (ic->codec_id == AV_CODEC_ID_MPEG2VIDEO) ic->max_b_frames = 2;
        if (ic->codec_id == AV_CODEC_ID_MPEG1VIDEO) ic->mb_decision = 2;
        if (App.Video[Chl].avContext->oformat->flags &AVFMT_GLOBALHEADER) ic->flags |= CODEC_FLAG_GLOBAL_HEADER;
      }
      av_dump_format(App.Video[Chl].avContext, 0, App.Video[Chl].FileName_Rec, 1);
      if (App.Video[Chl].avStreamV)
      {
        AVCodecContext* c = App.Video[Chl].avStreamV->codec;
        AVCodec* codec = avcodec_find_encoder(c->codec_id);
        //if (avcodec_open(c, codec) < 0) return;//old
        //App.Video[Chl].avFrameV = avcodec_alloc_frame();//old
        if (avcodec_open2(c, codec, NULL) < 0) return;//new
        App.Video[Chl].avFrameV = av_frame_alloc();//new
        if (!App.Video[Chl].avFrameV) return;
        int size = avpicture_get_size(c->pix_fmt, c->width, c->height);
        App.Video[Chl].bufferRecYUV420 = (u8*)av_malloc(size);

        avpicture_fill((AVPicture*)App.Video[Chl].avFrameV, App.Video[Chl].bufferRecYUV420, c->pix_fmt, c->width, c->height);
      }

      if (!(App.Video[Chl].avfmt->flags &AVFMT_NOFILE))
      {
        if (avio_open(&App.Video[Chl].avContext->pb, App.Video[Chl].FileName_Rec, AVIO_FLAG_WRITE) < 0) return;
      }
      avformat_write_header(App.Video[Chl].avContext, NULL);
      App.Video[Chl].avFrameV->pts = 0;
    }

    //2 encode write data
    //int len = 0;
//int  avcodec_encode_video(AVCodecContext *avctx, uint8_t *buf, int buf_size, const AVFrame *pict);
//int avcodec_encode_video2(AVCodecContext *avctx, AVPacket *avpkt, const AVFrame *frame, int *got_packet_ptr);
    AVPacket pkt;
#warning "tttttttttttttttttttttttttttttt"
    //int got_packet = 0;
    av_init_packet(&pkt);
    pkt.stream_index = App.Video[Chl].avStreamV->index;
    pkt.data = Buf;//App.Video[Chl].avEncBuf;
    pkt.size = Len;//sizeof(App.Video[Chl].avEncBuf);
/*    len = avcodec_encode_video2(
      App.Video[Chl].avStreamV->codec,
      &pkt,
      App.Video[Chl].Frame422,
      &got_packet
      );
    if (got_packet)*/
    {
      av_interleaved_write_frame(App.Video[Chl].avContext, &pkt);
    }
/*
    len = avcodec_encode_video(
      App.Video[Chl].avStreamV->codec,
      App.Video[Chl].avEncBuf,
      sizeof(App.Video[Chl].avEncBuf),
      App.Video[Chl].Frame422);
    if (len > 0)
    {
      av_init_packet(&pkt);
      pkt.stream_index = App.Video[Chl].avStreamV->index;
      pkt.data = App.Video[Chl].avEncBuf;
      pkt.size = len;
      av_interleaved_write_frame(App.Video[Chl].avContext, &pkt);
    }
*/
  }
  //3 end write file
  int i;
  if (App.Video[Chl].IsRec == false)
  {
    if (App.Video[Chl].Flag_StopRec)
    {
      App.Video[Chl].Flag_StopRec = false;

      av_write_trailer(App.Video[Chl].avContext);
      if (App.Video[Chl].avStreamV)
      {
        for (i = 0; i < App.Video[Chl].avContext->nb_streams; i++)
        {
          av_freep(&App.Video[Chl].avContext->streams[i]->codec);
          av_freep(&App.Video[Chl].avContext->streams[i]);
        }
      }
      if (!(App.Video[Chl].avfmt->flags &AVFMT_NOFILE))
      {
        avio_close(App.Video[Chl].avContext->pb);
      }
      av_free(App.Video[Chl].avContext);

      if (App.Video[Chl].avFrameV)
      {
        av_free(App.Video[Chl].avFrameV);
        App.Video[Chl].avFrameV = NULL;
      }

      if (App.Video[Chl].bufferRecYUV420)
      {
        free(App.Video[Chl].bufferRecYUV420);
        App.Video[Chl].bufferRecYUV420 = NULL;
      }
    }
  }
#endif
}
//-------------------------------------------------------------------------
int jlocal_StartRec(JNIEnv* env, jclass obj, int Chl, jstring nFilename)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  strcpy(App.Video[Chl].FileName_Rec, (char*)(*env)->GetStringUTFChars(env, nFilename, NULL));

#ifdef IS_NEW_RECORD
#warning " IS_NEW_RECORD IS_NEW_RECORD IS_NEW_RECORD IS_NEW_RECORD "
  avcodec_register_all();
  av_register_all();
  avformat_alloc_output_context2(&App.Video[Chl].avContext, NULL, NULL, App.Video[Chl].FileName_Rec);
  if (!App.Video[Chl].avContext) return 0;
  App.Video[Chl].avStreamV = avformat_new_stream(App.Video[Chl].avContext, NULL);
  App.Video[Chl].avStreamV->index = 0;
  App.Video[Chl].avStreamV->codec->frame_number = 1;
  App.Video[Chl].avStreamV->codec->bit_rate = 3000000;//25
  App.Video[Chl].avStreamV->codec->codec_id = AV_CODEC_ID_H264;//i_video_stream->codec->codec_id;
  App.Video[Chl].avStreamV->codec->codec_type = AVMEDIA_TYPE_VIDEO;//i_video_stream->codec->codec_type;
  App.Video[Chl].avStreamV->codec->time_base.num = 1;//i_video_stream->time_base.num;
  //App.Video[Chl].avStreamV->codec->time_base.den = 25;//i_video_stream->time_base.den;
  App.Video[Chl].avStreamV->codec->time_base.den = App.Video[Chl].RecFrameRate;
  App.Video[Chl].avStreamV->codec->width = App.Video[Chl].encWidth;
  App.Video[Chl].avStreamV->codec->height = App.Video[Chl].encHeight;
  App.Video[Chl].avStreamV->codec->pix_fmt = AV_PIX_FMT_YUV420P;//i_video_stream->codec->pix_fmt;

  App.Video[Chl].avContext->oformat->flags |= CODEC_FLAG_GLOBAL_HEADER;

  avio_open(&App.Video[Chl].avContext->pb, App.Video[Chl].FileName_Rec, AVIO_FLAG_WRITE);
  avformat_write_header(App.Video[Chl].avContext, NULL);

  thNet_Play(App.Video[Chl].NetHandle, !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);

  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
#endif

  App.Video[Chl].IsRec = true;
  App.Video[Chl].Flag_StartRec = true;

  return 1;
}
//-------------------------------------------------------------------------
int jlocal_StopRec(JNIEnv* env, jclass obj, int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Video[Chl].IsRec = false;
  App.Video[Chl].Flag_StopRec = true;
#ifdef IS_NEW_RECORD
  if (App.Video[Chl].avContext)
  {
    av_write_trailer(App.Video[Chl].avContext);

    avcodec_close(App.Video[Chl].avContext->streams[0]->codec);//  App.Video[Chl].avStreamV->codec
    av_freep(&App.Video[Chl].avContext->streams[0]->codec);
    av_freep(&App.Video[Chl].avContext->streams[0]);
    avio_close(App.Video[Chl].avContext->pb);
    av_free(App.Video[Chl].avContext);
  }
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
#endif
  return 1;
}

//-------------------------------------------------------------------------
int jlocal_SnapShot(JNIEnv* env, jclass obj, int Chl, jstring nFileName)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  strcpy(App.Video[Chl].FileName_Jpg, (char*)(*env)->GetStringUTFChars(env, nFileName, NULL));
  App.Video[Chl].IsSnapShot = true;
}
//-------------------------------------------------------------------------
int jtmp_DisconnectP2P(JNIEnv* env, jclass obj, int Chl)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  bool ret;
  if (thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_DisConn(App.Video[Chl].NetHandle);
    //ret = thNet_Free(&App.Video[Chl].NetHandle);
  }
  return ret;
}
//-----------------------------------------------------------------------------
int jthNet_PlayLiveP2P(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, int nStreamType)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Video[Chl].GroupType = pt_PlayLive;
  App.Video[Chl].IFrameFlag = false;
  App.Video[Chl].IsAudioMute = true;
  App.Video[Chl].StreamType = nStreamType;

  pthread_cond_init(&th_sync_cond, NULL);
  pthread_mutex_init(&th_mutex_lock, NULL);

  const char* UID = (*env)->GetStringUTFChars(env, nUID, NULL);
  const char* UIDPsd = (*env)->GetStringUTFChars(env, nUIDPsd, NULL);
  strcpy(App.Video[Chl].UID, UID);
  strcpy(App.Video[Chl].UIDPsd, UIDPsd);
  (*env)->ReleaseStringUTFChars(env, nUID, UID);
  (*env)->ReleaseStringUTFChars(env, nUIDPsd, UIDPsd);

  pthread_t native_thread;
  pthread_create(&native_thread, NULL, thread_video_PlayLiveP2P, (void*)Chl);
  return 1;
}
//-------------------------------------------------------------------------
int jthNet_PlayLive(JNIEnv* env, jclass obj, int Chl, jstring nDevIP, jstring nUserName, jstring nPassword, int nPort, int nStreamType)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Video[Chl].GroupType = pt_PlayLive;
  App.Video[Chl].IFrameFlag = false;
  App.Video[Chl].IsAudioMute = true;
  App.Video[Chl].StreamType = nStreamType;

  pthread_cond_init(&th_sync_cond, NULL);
  pthread_mutex_init(&th_mutex_lock, NULL);
  const char* DevIP = (*env)->GetStringUTFChars(env, nDevIP, NULL);
  const char* UserName = (*env)->GetStringUTFChars(env, nUserName, NULL);
  const char* Password = (*env)->GetStringUTFChars(env, nPassword, NULL);
  strcpy(App.Video[Chl].DevIP, DevIP);
  strcpy(App.Video[Chl].UserName, UserName);
  strcpy(App.Video[Chl].Password, Password);
  App.Video[Chl].DevPort = nPort;
  (*env)->ReleaseStringUTFChars(env, nDevIP, DevIP);
  (*env)->ReleaseStringUTFChars(env, nUserName, UserName);
  (*env)->ReleaseStringUTFChars(env, nPassword, Password);
  pthread_t native_thread;
  pthread_create(&native_thread, NULL, thread_video_PlayLive, (void*)Chl);
}
//-------------------------------------------------------------------------
int jthNet_PlayRemoteP2P(JNIEnv* env, jclass obj, int Chl, jstring nUID, jstring nUIDPsd, jstring nFileName)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Video[Chl].GroupType = pt_PlayHistory;
  App.Video[Chl].IFrameFlag = false;
  App.Video[Chl].IsAudioMute = false;

  pthread_cond_init(&th_sync_cond, NULL);
  pthread_mutex_init(&th_mutex_lock, NULL);

  const char* FileName = (*env)->GetStringUTFChars(env, nFileName, NULL);
  const char* UID = (*env)->GetStringUTFChars(env, nUID, NULL);
  const char* UIDPsd = (*env)->GetStringUTFChars(env, nUIDPsd, NULL);

  strcpy(App.Video[Chl].UID, UID);
  strcpy(App.Video[Chl].UIDPsd, UIDPsd);
  strcpy(App.Video[Chl].RemoteFileName, FileName);

  (*env)->ReleaseStringUTFChars(env, nUID, UID);
  (*env)->ReleaseStringUTFChars(env, nUIDPsd, UIDPsd);
  (*env)->ReleaseStringUTFChars(env, nFileName, FileName);
  bool ret;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_Connect_P2P(App.Video[Chl].NetHandle, 0, App.Video[Chl].UID, App.Video[Chl].UIDPsd, 3000, true);
  }
  ret = thNet_SetCallBack(App.Video[Chl].NetHandle, avCallBack, alarmCallBack, (void*)Chl);
  video_decode_Free(Chl);
  video_decode_Init(Chl);
  ret = thNet_RemoteFilePlay(App.Video[Chl].NetHandle, App.Video[Chl].RemoteFileName);
  usleep(1000*400);
  thNet_RemoteFilePlayControl(App.Video[Chl].NetHandle, 1, 0, 0);
}

//-------------------------------------------------------------------------
int jthNet_PlayRemote(JNIEnv* env, jclass obj, int Chl, jstring nDevIP, jstring nUserName, jstring nPassword, jstring nFileName, int nPort)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  App.Video[Chl].GroupType = pt_PlayHistory;
  App.Video[Chl].IFrameFlag = false;
  App.Video[Chl].IsAudioMute = false;

  pthread_cond_init(&th_sync_cond, NULL);
  pthread_mutex_init(&th_mutex_lock, NULL);

  const char* DevIP = (*env)->GetStringUTFChars(env, nDevIP, NULL);
  const char* UserName = (*env)->GetStringUTFChars(env, nUserName, NULL);
  const char* Password = (*env)->GetStringUTFChars(env, nPassword, NULL);
  const char* FileName = (*env)->GetStringUTFChars(env, nFileName, NULL);
  strcpy(App.Video[Chl].DevIP, DevIP);
  strcpy(App.Video[Chl].Password, Password);
  strcpy(App.Video[Chl].UserName, UserName);
  strcpy(App.Video[Chl].RemoteFileName, FileName);
  App.Video[Chl].DevPort = nPort;
  (*env)->ReleaseStringUTFChars(env, nDevIP, DevIP);
  (*env)->ReleaseStringUTFChars(env, nUserName, UserName);
  (*env)->ReleaseStringUTFChars(env, nPassword, Password);
  (*env)->ReleaseStringUTFChars(env, nFileName, FileName);

  bool ret;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle))
  {
    ret = thNet_Init(&App.Video[Chl].NetHandle);
    ret = thNet_SetCallBack(App.Video[Chl].NetHandle, avCallBack, alarmCallBack, (void*)Chl);
    ret = thNet_Connect(App.Video[Chl].NetHandle, App.Video[Chl].UserName, App.Video[Chl].Password, App.Video[Chl].DevIP, App.Video[Chl].DevPort, 3000, 1);
  }
  LOGE("IsConnect:%d \n", thNet_IsConnect(App.Video[Chl].NetHandle));
  /*
  int VideoType;
  int Width0;
  int Height0;
  int Width1;
  int Height1;
  int FrameRate0, FrameRate1;
  thNet_GetVideoCfg(App.Video[Chl].NetHandle, &VideoType, &Width0, &Height0, &FrameRate0, &Width1, &Height1, &FrameRate1);
  App.Video[Chl].VideoType = VideoType;
  App.Video[Chl].RecFrameRate = FrameRate1 * 0.8;
  */
  video_decode_Free(Chl);
  video_decode_Init(Chl);
  ret = thNet_RemoteFilePlay(App.Video[Chl].NetHandle, App.Video[Chl].RemoteFileName); //
  usleep(1000*400);
  thNet_RemoteFilePlayControl(App.Video[Chl].NetHandle, 1, 0, 0); //
}
//-------------------------------------------------------------------------
int jthNet_StopPlay(JNIEnv* env, jclass obj, int Chl)
{
  pthread_cond_signal(&th_sync_cond);
  if (App.Video[Chl].GroupType == pt_PlayLive) thNet_Stop(App.Video[Chl].NetHandle);
  if (App.Video[Chl].GroupType == pt_PlayHistory) thNet_RemoteFileStop(App.Video[Chl].NetHandle);
  thNet_DisConn(App.Video[Chl].NetHandle);
  App.Video[Chl].GroupType = pt_Cmd;
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
}
//-------------------------------------------------------------------------
int jthNet_SetAudioIsMute(JNIEnv* env, jclass obj, int Chl, int nIsAudioMute)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  if (App.Video[Chl].IsAudioMute == nIsAudioMute) return;
  App.Video[Chl].IsAudioMute = nIsAudioMute;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle)) return;
  thNet_Play(App.Video[Chl].NetHandle, !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);
}
//-------------------------------------------------------------------------
int jthNet_SetResolution(JNIEnv* env, jclass obj, int Chl, int nStreamType)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  if (App.Video[Chl].StreamType == nStreamType) return;
  App.Video[Chl].StreamType = nStreamType;
  if (!thNet_IsConnect(App.Video[Chl].NetHandle)) return;
  thNet_Play(App.Video[Chl].NetHandle, !App.Video[Chl].StreamType, !App.Video[Chl].IsAudioMute, App.Video[Chl].StreamType);
}
//-------------------------------------------------------------------------
jstring jGetWlanIP(JNIEnv* env, jclass obj)
{
  char20 localIP, wlanIP;
  GetwlanIP(localIP, wlanIP);
  jstring nwlanIP = (*env)->NewStringUTF(env, wlanIP);
  return nwlanIP;
}
//-------------------------------------------------------------------------
int jtmp_Test(JNIEnv* env, jclass obj)
{
  LOGE("%s(%d)\n", __FUNCTION__, __LINE__);
  return 0;
}
