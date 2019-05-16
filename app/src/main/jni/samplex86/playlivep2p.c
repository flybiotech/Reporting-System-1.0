#include "../thSDK.h"

//-----------------------------------------------------------------------------
#define ISPRINT  1
void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  int NetHandle = (int)UserCustom;
  if (NetHandle == 0) return;

  static int videocount = 0;
  static int audiocount = 0;
  static int dt = 0;
  static int dt1 = 0;

  dt1 = time(NULL);
  if (dt1 - dt > 0)
  {
    printf("videocount:%d audiocount:%d\n", videocount, audiocount);
    dt = dt1;
    videocount = 0;
    audiocount = 0;
#warning ccccccccccccccccccccccccccccccxxxxccccccccccccccc
  }

  int v41 = *((int*)(Buf+0));
  int v42 = *((int*)(Buf+4));
  if (PInfo->Head.VerifyCode == Head_VideoPkt)//视频格式为H264
  {
    videocount = videocount + Len;
    if (ISPRINT) printf("video ");
    if (PInfo->Frame.StreamType == 0) if (ISPRINT) printf("mainStream ");
    if (PInfo->Frame.StreamType == 1) if (ISPRINT) printf("subStream  ");
    if (ISPRINT) printf("IFrame:%d Len:%7d %0.8X %0.8X\n", PInfo->Frame.IsIFrame, Len, v41, v42);
  } 
  if (PInfo->Head.VerifyCode == Head_AudioPkt)//音频格式为PCM
  {
    audiocount = audiocount + Len;
    if (ISPRINT) printf("audio                     Len:%7d %0.8X %0.8X\n", Len, v41, v42);
  }
  
}
//-----------------------------------------------------------------------------
void alarmCallBack(int AlmType, int AlmTime, int AlmChl, void* UserCustom)
{
  int NetHandle = (int)UserCustom;
  if (NetHandle == 0) return;

  printf("alarmCallBack ");
  switch (AlmType)
  {
  case Net_Disconn:
    printf("Net_Disconn \n");
    break;

  case Net_ReConn:
    printf("Net_ReConn \n");
    break;

  case Alm_MotionDetection:
    printf("Alm_MotionDetection \n");
    break;

  case Alm_SoundTrigger:
    printf("Alm_SoundTrigger \n");
    break;

  case Alm_DigitalInput:
    printf("Alm_DigitalInput \n");
    break;
  }
}

THandle NetHandle = 0;
void signal_recv(int sig)
{
  printf("SIGNAL:******** %d ********\n", sig);
  switch (sig)
  {
  case SIGINT://ctrl+c
    thNet_DisConn(NetHandle);
    thNet_Free(&NetHandle);
    printf("SIGNAL SIGINT exit\n");
    exit(0);
    break;
  }
  return;
}

//-----------------------------------------------------------------------------
int main(int argc, char** argv, char** argp)
{//FL89BHR698A7SGPPSZWT
  signal(SIGINT, signal_recv);

  char* UID = argv[1];
  char* Psd = argv[2];
  if ((UID == NULL)||(Psd == NULL))
  {
    printf("\nplaylivep2p: ./playlivep2p HVX1YZ3UJ35R7J3WJNXJ 123456");
    printf("\nplaylivep2p: ./playlivep2p HVX1YZ3UJ35R7J3WJNXJ 123456");    
    printf("\nplaylivep2p: ./playlivep2p KZX1XB3ACV5ZPJLWJ5W1 123456\n \n \n");
    return 0;
  }

  time_t dt;
  bool ret;

  int i;

  int dec; struct timeval tv1, tv2; 
  gettimeofday(&tv1, NULL);

  ret = thNet_Init(&NetHandle);
  ret = thNet_SetCallBack(NetHandle, avCallBack, alarmCallBack, (void*)NetHandle);
  ret = thNet_Connect_P2P(NetHandle, 0, UID, Psd, 3000, true);
  printf("IsConnect:%d \n", ret);
  gettimeofday(&tv2, NULL); dec = timeval_dec(&tv2, &tv1); printf("********dec_init_conn:%d\n", dec);


  gettimeofday(&tv1, NULL);

  char Buf[65536*10];
  int BufLen;
  char* url;
  url = "http://192.168.0.175/cfg.cgi?User=admin&Psd=admin&MsgID=28";//所有配置
  memset(Buf, 0, sizeof(Buf));
  ret = thNet_HttpGet(NetHandle, url, Buf, &BufLen);
  gettimeofday(&tv2, NULL); dec = timeval_dec(&tv2, &tv1); printf("********thNet_HttpGet:%d\n", dec);

  for (i=0; i<50; i++)
  {
    printf("thNet_PlaythNet_PlaythNet_PlaythNet_PlaythNet_PlaythNet_Play 1, 1, 0\n");
    gettimeofday(&tv1, NULL);

    ret = thNet_Play(NetHandle, 1, 0, 0);
    gettimeofday(&tv2, NULL); dec = timeval_dec(&tv2, &tv1); printf("********thNet_Play:%d\n", dec);
    dt = time(NULL);
    while(1)
    {
      usleep(1000*100);
      if (time(NULL) - dt > 5)
      {
        thNet_Stop(NetHandle);
        break;
      }
    }
  }

  ret = thNet_DisConn(NetHandle);
  ret = thNet_Free(&NetHandle);
  printf("exit !!!!\n");
}
