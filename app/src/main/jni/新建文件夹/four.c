#include "four.h"
TDevInfoCfg Cfg[4];
TdecParam decParam[4];
int decHandle[4];

void avCallBackFour(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  if (UserCustom == NULL)
    return ;
  TDevInfoCfg* Cfg = (TDevInfoCfg*)UserCustom;
  int v4 = *((int*)Buf);
  char* tmpStr;
  if (Cfg->IsP2P)
    tmpStr = Cfg->p2pUID;
  else
    tmpStr = Cfg->SvrIP;

  if (PInfo->Head.VerifyCode == Head_VideoPkt)
     //视频格式为H264
  {
    LOGE("video ");
    if (PInfo->Frame.StreamType == 0)
      printf("mainStream ");
    if (PInfo->Frame.StreamType == 1)
      printf("subStream  ");
    LOGE("IFrame:%d Len:%7d %0.8X FromIP:%s\n", PInfo->Frame.IsIFrame, Len, v4, tmpStr);
    if (decParam[Cfg->channel].switch_cnt > 0)
    {
      decParam[Cfg->channel].switch_cnt--;
      return ;
    }
    // memcpy(decParam[Cfg->channel].encBuf, Buf, Len);
    decParam[Cfg->channel].encBuf = Buf;
    decParam[Cfg->channel].encLen = Len;
    thDecodeVideoFrame(decHandle[Cfg->channel], &decParam[Cfg->channel], Cfg->channel);
    //[[refToSelf dec] thDecodeVideoFrame:refToSelf->decHandle[Cfg->channel] param:&refToSelf->decParam[Cfg->channel] channel:Cfg->channel];

  }
  if (PInfo->Head.VerifyCode == Head_AudioPkt)
     //音频格式为PCM
  {
    if (_expand_channel == Cfg->channel)
      if (!_slience)
      {
        playAudio(Buf, Len);
      }
      LOGE("audio                     Len:%7d %0.8X FromIP:%s\n", Len, v4, tmpStr);
  }
}

//-----------------------------------------------------------------------------
void alarmCallBackFour(int AlmType, int AlmTime, int AlmChl, void* UserCustom)
{
  if (UserCustom == NULL)
    return ;
  TDevInfoCfg* Cfg = (TDevInfoCfg*)UserCustom;
  char* tmpStr;
  if (Cfg->IsP2P)
    tmpStr = Cfg->p2pUID;
  else
    tmpStr = Cfg->SvrIP;

  switch (AlmType)
  {
  case Alm_MotionDetection:
    LOGE("alarmCallBack Alm_MotionDetection FromIP:%s\n", tmpStr);
    break;



  case Alm_DigitalInput:
    LOGE("alarmCallBack Alm_DigitalInput FromIP:%s\n", tmpStr);
    break;
  }
}

jstring getUrlChannel(JNIEnv* env, jclass clazz, jstring Surl, jint channel)
{
  bool ret;
  if (!thNet_IsConnect(Cfg[channel].NetHandle))
  {
    LOGE("not connected");
    return ;
  }
  LOGE("connected");
  if (thNet_IsConnect(Cfg[channel].NetHandle))
  {

    const char* url = (*env)->GetStringUTFChars(env, Surl, NULL);
    LOGE("requre url is %s", url);
    char Buf[65536];
    char conv[65536];
    memset(Buf, 0x00, 65536);
    memset(conv, 0x00, 65536);
    int BufLen;
    ret = thNet_HttpGet(Cfg[channel].NetHandle, url, Buf, &BufLen);
    s32 pnErrC;
    UcnvConvert(conv, 65536, Buf, &pnErrC);

    jstring responseUrl = (*env)->NewStringUTF(env, conv);


    return responseUrl;
  }
  else
    return NULL;
}

void native_start_ddns_channel(JNIEnv* env, jclass clazz, jstring ip1, jstring usr1, jstring pwd1, jint prt, jint channel)
{
  const char* _ip = (*env)->GetStringUTFChars(env, ip1, NULL);
  const char* _usr = (*env)->GetStringUTFChars(env, usr1, NULL);
  const char* _pwd = (*env)->GetStringUTFChars(env, pwd1, NULL);
  bool ret;
  //memset(Cfg, 0, sizeof(Cfg));
  strcpy(Cfg[channel].UserName, _usr);
  strcpy(Cfg[channel].Password, _pwd);
  strcpy(Cfg[channel].SvrIP, _ip);
  Cfg[channel].DataPort = prt;
  Cfg[channel].IsP2P = 0;
  Cfg[channel].channel = channel;
  //Cfg[channel].NetHandle=0;
  pthread_t native_thread;
  if (pthread_create(&native_thread, NULL, vid_start_thd, (void*) &Cfg[channel]))
  {
    LOGE("callVoid: failed to create a native thread");
  }

}

void native_start_p2p_channel(JNIEnv* env, jclass clazz, jstring Suid, jstring Suidpsd, jint channel)
{

  const char* _uid = (*env)->GetStringUTFChars(env, Suid, NULL);
  const char* _uidpsd = (*env)->GetStringUTFChars(env, Suidpsd, NULL);
  bool ret;
  //memset(Cfg, 0, sizeof(Cfg));
  strcpy(Cfg[channel].p2pUID, _uid);
  strcpy(Cfg[channel].p2pPsd, _uidpsd);
  Cfg[channel].IsP2P = 1;
  Cfg[channel].channel = channel;
  //Cfg[channel].NetHandle=0;
  pthread_t native_thread;
  if (pthread_create(&native_thread, NULL, vid_start_thd, (void*) &Cfg[channel]))
  {
    LOGE("callVoid: failed to create a native thread");
  }
}

void vid_stop_channel_thd(void* id)
{

  pthread_mutex_lock(&lock);
  TDevInfoCfg* cfg = id;
  LOGE("================0,handle is %d", cfg->NetHandle);
  if (thNet_IsConnect(cfg->NetHandle))
  {
    thNet_Stop(cfg->NetHandle);
    thNet_DisConn(cfg->NetHandle);
  }

  LOGE("================3");
  pthread_mutex_unlock(&lock);
}

void native_stop_channel(JNIEnv* env, jclass clazz, jint channel)
{
  LOGE("================channel is %d", channel);
  int i = channel;
  pthread_t native_thread;
  //if(pthread_create(&native_thread, NULL, vid_stop_channel_thd, (void *)&Cfg[channel])) 
  //{
  //    LOGE("callVoid: failed to create a native thread");
  //}

  if (!thNet_IsConnect(Cfg[channel].NetHandle))
  {
    return ;
  }
  if (thNet_IsConnect(Cfg[channel].NetHandle))
  {
    thNet_Stop(Cfg[channel].NetHandle);
    thNet_DisConn(Cfg[channel].NetHandle);
  }

  // Cfg[channel].NetHandle= 0;
}

void restart_channel(JNIEnv* env, jclass clazz, jint channel) //0 ddns 1 p2p
{

  pthread_t native_thread;
  if (pthread_create(&native_thread, NULL, vid_restart_thd, (void*) &Cfg[channel]))
  {
    LOGE("callVoid: failed to create a native thread");
  }


}

void setResolution_channel(JNIEnv* env, jclass clazz, int channel, int type)
{
  if (!thNet_IsConnect(Cfg[channel].NetHandle))
  {
    return ;
  }
  decParam[channel].switch_cnt = 8;
  if (_slience)
  {
    //middle pram 0
    if (decParam[channel].play_type != type)
    {
      //switch_cnt = 3;
      usleep(100000);
      decParam[channel].play_type = type;
      if (0 == type)
      {
        thNet_Play(Cfg[channel].NetHandle, 0, 0, 1);

        decHandle[channel] = thDecodeVideoInit(&decParam[channel]);
      }
      else if (1 == type)
      {
        thNet_Play(Cfg[channel].NetHandle, 1, 0, 0);
        decHandle[channel] = thDecodeVideoInit(&decParam[channel]);

      }
    }
  }
  else
  {
    //middle pram 1 
    if (decParam[channel].play_type != type)
    {
      //switch_cnt = 3;
      usleep(100000);
      decParam[channel].play_type = type;
      if (0 == type)
      {
        thNet_Play(Cfg[channel].NetHandle, 0, 1, 1);
        decHandle[channel] = thDecodeVideoInit(&decParam[channel]);

      }
      else if (1 == type)
      {
        thNet_Play(Cfg[channel].NetHandle, 1, 1, 0);
        decHandle[channel] = thDecodeVideoInit(&decParam[channel]);
      }
    }
  }
}

void vid_restart_thd(void* id)
{
  int ret = 0;
  TDevInfoCfg* cfg = id;
  LOGE("-----------------------------------0");

  usleep(100000* cfg->channel);
  LOGE("-----------------------------------1");
  //cfg->NetHandle=0;
  ret = thNet_Init(&cfg->NetHandle, 11);
  ret = thNet_SetCallBack(cfg->NetHandle, avCallBackFour, alarmCallBackFour, (void*)cfg);
  if (cfg->IsP2P)
  {

    ret = thNet_Connect_P2P(cfg->NetHandle, 0, cfg->p2pUID, cfg->p2pPsd, 3000, true);
  }
  else
  {
    LOGE("-----------------------------------2");
    int n = 0;
    while (ret == 0 || ++n < 3)
      ret = thNet_Connect(cfg->NetHandle, cfg->UserName, cfg->Password, cfg->SvrIP, cfg->SvrIP, cfg->DataPort, 3000, 1);
  }
  LOGE("NetHandle[%d]:%d IsConnect:%d \n", cfg->channel, cfg->NetHandle, ret);
  if (ret)
  {
    int Standard;
    int VideoType;
    int IsMirror;
    int IsFlip;
    int Width0;
    int Height0;
    int FrameRate0;
    int BitRate0;
    int Width1;
    int Height1;
    int FrameRate1;
    int BitRate1;
    thNet_GetVideoCfg(cfg->NetHandle, &Standard, &VideoType, &IsMirror, &IsFlip,  &Width0, &Height0, &FrameRate0, &BitRate0,  &Width1, &Height1, &FrameRate1, &BitRate1);
    LOGE("width is %d,and height is %d,video type is %d\n", Width1, Height1, VideoType);
    decParam[cfg->channel].decWidth = Width1;
    decParam[cfg->channel].decHeight = Height1;

    decParam[cfg->channel].decWidthMain = Width0;
    decParam[cfg->channel].decHeightMain = Height0;
    decParam[cfg->channel].play_type = 0;
    decParam[cfg->channel].encLen = 0;
    decHandle[cfg->channel] = thDecodeVideoInit(&decParam[cfg->channel]);
    decParam[cfg->channel].init = 1;
    decParam[cfg->channel].switch_cnt = 0;
    ret = thNet_Play(cfg->NetHandle, 0, 0, 1); //取次码流及音频
  }
}

void vid_start_thd(void* id)
{
  int ret = 0;
  TDevInfoCfg* cfg = id;
  LOGE("-----------------------------------0");
  if (thNet_IsConnect(cfg->NetHandle))
  {
    thNet_Stop(cfg->NetHandle);
    thNet_DisConn(cfg->NetHandle);
  }
  LOGE("-----------------------------------1");
  usleep(100000* cfg->channel);
  LOGE("-----------------------------------1");
  //cfg->NetHandle=0;
  ret = thNet_Init(&cfg->NetHandle, 11);
  ret = thNet_SetCallBack(cfg->NetHandle, avCallBackFour, alarmCallBackFour, (void*)cfg);
  if (cfg->IsP2P)
  {

    ret = thNet_Connect_P2P(cfg->NetHandle, 0, cfg->p2pUID, cfg->p2pPsd, 3000, true);
  }
  else
  {
    LOGE("-----------------------------------2");
    int n = 0;
    while (ret == 0 || ++n < 3)
      ret = thNet_Connect(cfg->NetHandle, cfg->UserName, cfg->Password, cfg->SvrIP, cfg->SvrIP, cfg->DataPort, 3000, 1);
  }
  LOGE("NetHandle[%d]:%d IsConnect:%d \n", cfg->channel, cfg->NetHandle, ret);
  if (ret)
  {
    int Standard;
    int VideoType;
    int IsMirror;
    int IsFlip;
    int Width0;
    int Height0;
    int FrameRate0;
    int BitRate0;
    int Width1;
    int Height1;
    int FrameRate1;
    int BitRate1;
    thNet_GetVideoCfg(cfg->NetHandle, &Standard, &VideoType, &IsMirror, &IsFlip,  &Width0, &Height0, &FrameRate0, &BitRate0,  &Width1, &Height1, &FrameRate1, &BitRate1);
    LOGE("width is %d,and height is %d,video type is %d\n", Width1, Height1, VideoType);
    decParam[cfg->channel].decWidth = Width1;
    decParam[cfg->channel].decHeight = Height1;

    decParam[cfg->channel].decWidthMain = Width0;
    decParam[cfg->channel].decHeightMain = Height0;
    decParam[cfg->channel].play_type = 0;
    decParam[cfg->channel].encLen = 0;
    decHandle[cfg->channel] = thDecodeVideoInit(&decParam[cfg->channel]);
    decParam[cfg->channel].init = 1;
    decParam[cfg->channel].switch_cnt = 0;
    ret = thNet_Play(cfg->NetHandle, 0, 0, 1);//取次码流及音频
  }
}
