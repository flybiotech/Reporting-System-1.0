#include "../thSDK.h"
//-----------------------------------------------------------------------------
void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  int NetHandle = (int)UserCustom;
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
    //printf("\nplayp2p: ./playp2p KZX1XB3ACV5ZPJLWJ5W1 123456\n \n \n");
    printf("\nplayp2p: ./playp2ptalk LRXHYZB2JRNRM1LWJNB1 123456\n \n \n");
    return 0;
  }
/*
1.  LRXHYZB2JRNRM1LWJNB1
2.  L3NHXZAU53NZ5JLW4NLJ
3.  HRH1ZZAK5LNZ713W4N51
4.  HVX1YZ3UJ35R7J3WJNXJ
*/
  time_t dt;
  bool ret;

  int i;

  /*
  for (i=0; i<50; i++)
  {
    //sleep(5);
    ret = thNet_Init(&NetHandle);
    ret = thNet_SetCallBack(NetHandle, avCallBack, alarmCallBack, (void*)NetHandle);
    ret = thNet_Connect_P2P(NetHandle, 0, UID, Psd, 3000, true);
    printf("1IsConnect:%d \n", ret);
    if (!ret) exit(0);

    ret = thNet_DisConn(NetHandle);
    ret = thNet_Free(&NetHandle);
  }
*/

    ret = thNet_Init(&NetHandle);
    ret = thNet_SetCallBack(NetHandle, avCallBack, alarmCallBack, (void*)NetHandle);
    ret = thNet_Connect_P2P(NetHandle, 0, UID, Psd, 3000, true);
    printf("1IsConnect:%d \n", ret);
    if (ret == 0) return;

  char Buf[65536*10];
  int BufLen;
  printf("NetHandle:%ld size:%d\n%s\n\n", NetHandle, BufLen);

  BufLen = 1024;
  FILE* f = fopen("8k_16bit_mono.pcm", "rb");
  for (i=0; i<1000; i++)
  {
    BufLen = fread(Buf, 1, 1024, f);
    ret = thNet_SetTalk(NetHandle, Buf, BufLen);
    printf("NetHandle:%ld BufLen:%d\n", NetHandle, BufLen);
    usleep(1000*20);
  }
  fclose(f);
  ret = thNet_DisConn(NetHandle);
  ret = thNet_Free(&NetHandle);
  printf("exit !!!!\n");
}
