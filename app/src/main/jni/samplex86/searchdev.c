#include "../thSDK.h"

//-----------------------------------------------------------------------------
void SearchDevCallBack(int SN, int DevType, int VideoChlCount, int DataPort, int HttpPort, char* DevName,
                       char* DevIP, char* DevMAC, char* SubMask, char* Gateway, char* DNS1,
                       char* DDNSHost, char* UID)
{
  printf("SearchDevCallBack DevIP:%s DataPort:%d HttpPort:%d DevMAC:%s\n", DevIP, DataPort, HttpPort, DevMAC);
  printf("                  DDNSHost:%s UID:%s\n", DDNSHost, UID);
}
//-----------------------------------------------------------------------------
int main_Search()
{
  time_t dt;
  thSearch_Init(SearchDevCallBack);
  printf("thSearch_Init\n");
  thSearch_SearchDevice(NULL);
  printf("thSearch_SearchDevice\n");
  dt = time(NULL);
  while(1)
  {
    usleep(1000*100);
    if (time(NULL) - dt >= 3) break;
  }
  thSearch_Free();
  printf("thSearch_Free\n");
}
//-----------------------------------------------------------------------------
int main(int argc, char** argv, char** argp)
{
  main_Search();
}
