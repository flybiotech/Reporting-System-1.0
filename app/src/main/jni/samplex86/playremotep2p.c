#include "../thSDK.h"

//-----------------------------------------------------------------------------
void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  THandle NetHandle = (THandle)UserCustom;
  if (NetHandle == 0) return;

  int v41 = *((int*)(Buf+0));
  int v42 = *((int*)(Buf+4));
  if (PInfo->Head.VerifyCode == Head_VideoPkt)//视频格式为H264
  {
    printf("video ");
    if (PInfo->Frame.StreamType == 0) printf("mainStream ");
    if (PInfo->Frame.StreamType == 1) printf("subStream  ");

    //static FILE* f = NULL;
    //if (!f) f = fopen("./aaaa.h264", "w+b");
    //fwrite(Buf, Len, 1, f);

    printf("IFrame:%d Len:%7d %0.8X %0.8X\n", PInfo->Frame.IsIFrame, Len, v41, v42);
  } 
  if (PInfo->Head.VerifyCode == Head_AudioPkt)//音频格式为PCM
  {
    printf("audio                     Len:%7d %0.8X %0.8X\n", Len, v41, v42);
  }  
}
//-----------------------------------------------------------------------------
void alarmCallBack(int AlmType, int AlmTime, int AlmChl, void* UserCustom)
{
  THandle NetHandle = (THandle)UserCustom;
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
    printf("\nplayp2p: ./playp2p KZX1XB3ACV5ZPJLWJ5W1 123456");
    printf("\nplayp2p: ./playp2p KZX1XB3ACV5ZPJLWJ5W1 123456");    
    printf("\nplayp2p: ./playp2p KZX1XB3ACV5ZPJLWJ5W1 123456\n \n \n");
    return 0;
  }

  time_t dt;
  bool ret;

  int i;

    ret = thNet_Init(&NetHandle);
    ret = thNet_SetCallBack(NetHandle, avCallBack, alarmCallBack, (void*)NetHandle);
    ret = thNet_Connect_P2P(NetHandle, 0, UID, Psd, 3000, true);
    printf("1IsConnect:%d \n", ret);

  char Buf[65536*10];
  int BufLen;
  char* url;

  for (i=0; i<5; i++)
  {
/*
    printf("thNet_PlaythNet_PlaythNet_PlaythNet_PlaythNet_PlaythNet_Play\n");
    ret = thNet_Play(NetHandle, 0, 1, 1);
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
*/
#warning " bbbbbbbbbbbbbbbbbbbbbbbbbb22222222222222 "
    printf("thNet_RemoteFilePlaythNet_RemoteFilePlaythNet_RemoteFilePlay\n");
    thNet_RemoteFilePlay(NetHandle, "/sd/20131108/20131108_122350_0.av");
    thNet_RemoteFilePlayControl(NetHandle, 1, 0, 0);//发送"播放"控制命令
    dt = time(NULL);
    while(1)
    {
      usleep(1000*100);
      if (time(NULL) - dt > 5)
      {
        thNet_RemoteFileStop(NetHandle);//同 thNet_RemoteFilePlayControl(NetHandle, 3, 0, 0);
        break;
      }
    }

  }

  ret = thNet_DisConn(NetHandle);
  ret = thNet_Free(&NetHandle);
  printf("exit !!!!\n");
}
