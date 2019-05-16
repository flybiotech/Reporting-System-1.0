#include "../thSDK.h"

//-----------------------------------------------------------------------------
void avCallBack(TDataFrameInfo* PInfo, char* Buf, int Len, void* UserCustom)
{
  int NetHandle = (int)UserCustom;
  if (NetHandle == 0) return;

  int v4 = *((int*)Buf);
  if (PInfo->Head.VerifyCode == Head_VideoPkt)//视频格式为H264
  {
    printf("video ");
    if (PInfo->Frame.StreamType == 0) printf("mainStream ");
    if (PInfo->Frame.StreamType == 1) printf("subStream  ");

    //static FILE* f = NULL;
    //if (!f) f = fopen("./aaaa.h264", "w+b");
    //fwrite(Buf, Len, 1, f);

    printf("IFrame:%d Len:%7d %0.8X\n", PInfo->Frame.IsIFrame, Len, v4);
  } 
  if (PInfo->Head.VerifyCode == Head_AudioPkt)//音频格式为PCM
  {
    printf("audio                     Len:%7d %0.8X\n", Len, v4);
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

  case Alm_DigitalInput:
    printf("Alm_DigitalInput \n");
    break;
  }
}
//-----------------------------------------------------------------------------
int main_PlayRemote(char* UserName, char* Password, char* SvrIP, int DataPort, char* sFileName)
{
  char FileName[80];
  int FileSize;
  //Զ�̻ط�ʵ��
  time_t dt;
  bool ret;
  THandle NetHandle = 0;
  ret = thNet_Init(&NetHandle);
  ret = thNet_SetCallBack(NetHandle, avCallBack, alarmCallBack, (void*)NetHandle);
  ret = thNet_Connect(NetHandle, UserName, Password, SvrIP, DataPort, 3000,
    0//�˲������ֳ����Ų�ͬ
    );

  sprintf(FileName, "/sd/20130828/20130828_180836_0.av");

  ret = thNet_CreateRecvThread(NetHandle);//������ȡ�߳�
  ret = thNet_RemoteFilePlay(NetHandle, FileName);//����Զ�̲���ʵ��
  thNet_RemoteFilePlayControl(NetHandle, 1, 0, 0);//����"����"��������
  dt = time(NULL);
  while(1)
  {
    usleep(1000*100);
    if (time(NULL) - dt > 100)
    {
      thNet_RemoteFileStop(NetHandle);//ͬ thNet_RemoteFilePlayControl(NetHandle, 3, 0, 0);
      ret = thNet_DisConn(NetHandle);
      return 1;
    }
  }
}
//-----------------------------------------------------------------------------
int main(int argc, char** argv, char** argp)
{
  char* IP = argv[1];
  int Port = atoi(argv[2]);
  if ((IP == NULL)||(Port == 0))
  {
    printf("\n\nplayremote: ./playremote 192.168.1.168 2000 FILEName\n \n \n");
    return 0;
  }

  main_PlayRemote("admin", "admin", IP, Port, "/sd/20130821/20130821_174524_0.av");

}
