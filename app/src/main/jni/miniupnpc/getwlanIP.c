#include <stdio.h>
#include "miniupnpc.h"
#include "upnpcommands.h"
//-----------------------------------------------------------------------------
int GetwlanIP(char* localIP, char* wlanIP)
{
  struct UPNPDev* devlist = 0;
  const char* multicastif = 0;
  const char* minissdpdpath = 0;
  int localport = UPNP_LOCAL_PORT_ANY;
  int error = 0;
  int ipv6 = 0;
  unsigned char ttl = 2;
  struct UPNPUrls urls;
  struct IGDdatas data;

  devlist = upnpDiscover(2000, multicastif, minissdpdpath, localport, ipv6, ttl, &error);
  if (!devlist)
  {
    sprintf(localIP, "0.0.0.0");
    sprintf(wlanIP, "0.0.0.0");
    return 0;
  }
#warning "GetwlanIP add by zhb GetwlanIP add by zhb GetwlanIP add by zhb"

  UPNP_GetValidIGD(devlist, &urls, &data, localIP, 20);

  int i = UPNP_GetExternalIPAddress(urls.controlURL, data.first.servicetype, wlanIP);
  if (i != UPNPCOMMAND_SUCCESS)
  {
    sprintf(localIP, "0.0.0.0");
    sprintf(wlanIP, "0.0.0.0");
    return 0;
  }

  FreeUPNPUrls(&urls);
  freeUPNPDevlist(devlist);
  return 1;
}

