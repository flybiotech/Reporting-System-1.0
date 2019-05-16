#include <stdio.h>
#include "getwlanIP.h"
//gcc -o getwlanIPtest getwlanIPtest.c getwlanIP.c miniupnpc.c upnpcommands.c upnpreplyparse.c upnpdev.c igd_desc_parse.c minixml.c connecthostport.c miniwget.c minissdpc.c minisoap.c portlistingparse.c receivedata.c upnperrors.c
//gcc -o tmp upnpc.c getwlanIP.c miniupnpc.c upnpcommands.c upnpreplyparse.c upnpdev.c igd_desc_parse.c minixml.c connecthostport.c miniwget.c minissdpc.c minisoap.c portlistingparse.c receivedata.c upnperrors.c
int main()
{
  char wlanIP[20];
  char localIP[20];
  GetwlanIP(localIP, wlanIP);
  printf("localIP:%s wlanIP:%s\n", localIP, wlanIP);
  return 1;
}

