//-----------------------------------------------------------------------------
// Author      : 深圳市南方无限智能科技有限公司
// Date        : 2013.04.20
// Version     : V 2.02
// Description : www.southipcam.com

//16位系统：long是4字节，int是2字节
//32位系统：long是4字节，int是4字节
//64位系统：long是8字节，int是4字节
//-----------------------------------------------------------------------------

#include "thSDKlib.h"
#include "common.h"
#include "skt.h"

//#define IS_NEWTUTK
//-----------------------------------------------------------------------------
u8 encode(i16 pcm)
{
#define MAX_32635 32635
  i32 exponent = 7;
  i32 expMask;
  i32 mantissa;
  u8 alaw;
  i32 sign = (pcm & 0x8000) >> 8;
  if (sign != 0) pcm = -pcm;
  if (pcm > MAX_32635) pcm = MAX_32635;
  for (expMask = 0x4000; (pcm & expMask) == 0 && exponent>0; exponent--, expMask >>= 1) {}
  mantissa = (pcm >> ((exponent == 0) ? 4 : (exponent + 3))) & 0x0f;
  alaw = (u8)(sign | exponent << 4 | mantissa);
  return (u8)(alaw^0xD5);
}

void g711a_encode(const char* src, i32 srclen, char* dst, i32 *dstlen)
{
  i32 i;
  i16* tmpBuf = (i16*)src;
  for(i=0; i<srclen/2; i++) dst[i] = encode(tmpBuf[i]);
  *dstlen = srclen/2;
}
//-----------------------------------------------------------------------------
i16 decode(u8 alaw)
{
  i32 sign, exponent, data;
  alaw ^= 0xD5;
  sign = alaw & 0x80;
  exponent = (alaw & 0x70) >> 4;
  data = alaw & 0x0f;
  data <<= 4;
  data += 8;
  if (exponent != 0) data += 0x100;
  if (exponent > 1) data <<= (exponent - 1);
  return (i16)(sign == 0 ? data : -data);
}

void g711a_decode(const char* src, i32 srclen, char* dst, i32 *dstlen)
{
  i32 i;
  i16* tmpBuf = (i16*)dst;
  for(i=0; i<srclen; i++) tmpBuf[i] = decode(src[i]);
  *dstlen = srclen*2;
}
//-----------------------------------------------------------------------------
bool DevCfg_to_NewDevCfg(TDevCfg* DevCfg, TNewDevCfg* NewDevCfg)
{
  i32 i;
  memset(NewDevCfg, 0, sizeof(TNewDevCfg));
  //DevInfo
  strcpy(NewDevCfg->DevInfo.DevModal, DevCfg->DevInfoPkt.DevModal);
  NewDevCfg->DevInfo.SN = DevCfg->DevInfoPkt.SN;
  strcpy(NewDevCfg->DevInfo.SoftVersion, DevCfg->DevInfoPkt.SoftVersion);
  strcpy(NewDevCfg->DevInfo.DevName, DevCfg->DevInfoPkt.DevName);
  NewDevCfg->DevInfo.StandardMask = DevCfg->DevInfoPkt.Info.StandardMask;
  NewDevCfg->DevInfo.SubStandardMask = DevCfg->DevInfoPkt.Info.SubStandardMask;
  NewDevCfg->DevInfo.DevType = DevCfg->DevInfoPkt.DevType;
  NewDevCfg->DevInfo.ExistWiFi = DevCfg->DevInfoPkt.Info.ExistWiFi;
  NewDevCfg->DevInfo.ExistSD = DevCfg->DevInfoPkt.Info.ExistSD;
  NewDevCfg->DevInfo.ExistFlash = DevCfg->DevInfoPkt.Info.ExistFlash;
  NewDevCfg->DevInfo.ethLinkStatus = DevCfg->DevInfoPkt.Info.ethLinkStatus;
  NewDevCfg->DevInfo.wifiStatus = DevCfg->DevInfoPkt.Info.wifiStatus;
  NewDevCfg->DevInfo.upnpStatus = DevCfg->DevInfoPkt.Info.upnpStatus;
  NewDevCfg->DevInfo.WlanStatus = DevCfg->DevInfoPkt.Info.WlanStatus;
  NewDevCfg->DevInfo.p2pStatus = DevCfg->DevInfoPkt.Info.p2pStatus;
  NewDevCfg->DevInfo.HardType = DevCfg->DevInfoPkt.Info.HardType;
  NewDevCfg->DevInfo.TimeZone = DevCfg->DevInfoPkt.TimeZone;
  NewDevCfg->DevInfo.DoubleStream = DevCfg->DevInfoPkt.DoubleStream;
  //NetCfg
  NewDevCfg->NetCfg.DataPort = DevCfg->NetCfgPkt.DataPort;
  NewDevCfg->NetCfg.rtspPort = DevCfg->NetCfgPkt.rtspPort;
  NewDevCfg->NetCfg.HttpPort = DevCfg->NetCfgPkt.HttpPort;
  NewDevCfg->NetCfg.IPType = DevCfg->NetCfgPkt.Lan.IPType;
  NewDevCfg->NetCfg.DevIP = IPToInt(DevCfg->NetCfgPkt.Lan.DevIP);
  NewDevCfg->NetCfg.SubMask = IPToInt(DevCfg->NetCfgPkt.Lan.SubMask);
  NewDevCfg->NetCfg.Gateway = IPToInt(DevCfg->NetCfgPkt.Lan.Gateway);
  NewDevCfg->NetCfg.DNS1 = IPToInt(DevCfg->NetCfgPkt.Lan.DNS1);
  strcpy(NewDevCfg->NetCfg.DevMAC, DevCfg->NetCfgPkt.Lan.DevMAC);
  NewDevCfg->NetCfg.ActiveuPnP = DevCfg->NetCfgPkt.uPnP.Active;
  NewDevCfg->NetCfg.ActiveDDNS = DevCfg->NetCfgPkt.DDNS.Active;
  NewDevCfg->NetCfg.DDNSType = DevCfg->NetCfgPkt.DDNS.DDNSType;
  strcpy(NewDevCfg->NetCfg.DDNSDomain, DevCfg->NetCfgPkt.DDNS.DDNSDomain);
  strcpy(NewDevCfg->NetCfg.DDNSServer, DevCfg->NetCfgPkt.DDNS.DDNSServer);
  //wifiCfg
  NewDevCfg->wifiCfg.ActiveWIFI = DevCfg->WiFiCfgPkt.Active;
  NewDevCfg->wifiCfg.IsAPMode = DevCfg->WiFiCfgPkt.IsAPMode;
  strcpy(NewDevCfg->wifiCfg.SSID_AP, DevCfg->WiFiCfgPkt.SSID_AP);
  strcpy(NewDevCfg->wifiCfg.Password_AP, DevCfg->WiFiCfgPkt.Password_AP);
  strcpy(NewDevCfg->wifiCfg.SSID_STA, DevCfg->WiFiCfgPkt.SSID_STA);
  strcpy(NewDevCfg->wifiCfg.Password_STA, DevCfg->WiFiCfgPkt.Password_STA);
  NewDevCfg->wifiCfg.Channel = DevCfg->WiFiCfgPkt.Channel;
  NewDevCfg->wifiCfg.EncryptType = DevCfg->WiFiCfgPkt.EncryptType;
  NewDevCfg->wifiCfg.Auth = DevCfg->WiFiCfgPkt.WPA.Auth;
  NewDevCfg->wifiCfg.Enc = DevCfg->WiFiCfgPkt.WPA.Enc;
  //p2pCfg
  NewDevCfg->p2pCfg.ActiveP2P = DevCfg->p2pCfgPkt.Active;
  NewDevCfg->p2pCfg.StreamType = DevCfg->p2pCfgPkt.StreamType;
  NewDevCfg->p2pCfg.p2pType = DevCfg->p2pCfgPkt.p2pType;
  strcpy(NewDevCfg->p2pCfg.UID, DevCfg->p2pCfgPkt.UID);
  strcpy(NewDevCfg->p2pCfg.Password, DevCfg->p2pCfgPkt.Password);
  memcpy(NewDevCfg->p2pCfg.SvrIP, DevCfg->p2pCfgPkt.SvrIP, 4*4);
  //VideoCfg
  NewDevCfg->VideoCfg.StandardEx0 = GetStandardFromWidthHeight(DevCfg->VideoCfgPkt.VideoFormat.Width, DevCfg->VideoCfgPkt.VideoFormat.Height);
  NewDevCfg->VideoCfg.FrameRate0 = DevCfg->VideoCfgPkt.VideoFormat.FrameRate;
  NewDevCfg->VideoCfg.BitRate0 = DevCfg->VideoCfgPkt.VideoFormat.BitRate / 1024;
  NewDevCfg->VideoCfg.StandardEx1 = DevCfg->VideoCfgPkt.VideoFormat.Sub.StandardEx;
  NewDevCfg->VideoCfg.FrameRate1 = DevCfg->VideoCfgPkt.VideoFormat.Sub.FrameRate;
  NewDevCfg->VideoCfg.BitRate1 = DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRate / 1024;
  NewDevCfg->VideoCfg.IsMirror = DevCfg->VideoCfgPkt.VideoFormat.IsMirror;
  NewDevCfg->VideoCfg.IsFlip = DevCfg->VideoCfgPkt.VideoFormat.IsFlip;
  NewDevCfg->VideoCfg.IsShowFrameRate = DevCfg->VideoCfgPkt.VideoFormat.IsShowFrameRate;
  NewDevCfg->VideoCfg.BitRateType0 = DevCfg->VideoCfgPkt.VideoFormat.BitRateType;
  NewDevCfg->VideoCfg.BitRateQuant0 = DevCfg->VideoCfgPkt.VideoFormat.BitRateQuant;
  NewDevCfg->VideoCfg.BitRateType1 = DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRateType;
  NewDevCfg->VideoCfg.BitRateQuant1 = DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRateQuant;
  //AudioCfg
  NewDevCfg->AudioCfg.ActiveAUDIO = DevCfg->AudioCfgPkt.Active;
  NewDevCfg->AudioCfg.InputTypeAUDIO = DevCfg->AudioCfgPkt.InputType;
  NewDevCfg->AudioCfg.VolumeLineIn = DevCfg->AudioCfgPkt.VolumeLineIn;
  NewDevCfg->AudioCfg.VolumeLineOut = DevCfg->AudioCfgPkt.VolumeLineOut;
  NewDevCfg->AudioCfg.nChannels = DevCfg->AudioCfgPkt.AudioFormat.nChannels;
  NewDevCfg->AudioCfg.wBitsPerSample = DevCfg->AudioCfgPkt.AudioFormat.wBitsPerSample;
  NewDevCfg->AudioCfg.nSamplesPerSec = DevCfg->AudioCfgPkt.AudioFormat.nSamplesPerSec;
  NewDevCfg->AudioCfg.wFormatTag = DevCfg->AudioCfgPkt.AudioFormat.wFormatTag;

  for (i=0; i<3; i++)//DevCfg->UserCfgPkt.Count
  {
    strcpy(NewDevCfg->UserCfg.UserName[i], DevCfg->UserCfgPkt.Lst[i].UserName);
    strcpy(NewDevCfg->UserCfg.Password[i], DevCfg->UserCfgPkt.Lst[i].Password);
    NewDevCfg->UserCfg.Authority[i] = DevCfg->UserCfgPkt.Lst[i].Authority;
  }
  //TDIAlm
  NewDevCfg->DIAlm.ActiveDI = DevCfg->AlmCfgPkt.DIAlm.Active;
  NewDevCfg->DIAlm.IsAlmRec = DevCfg->AlmCfgPkt.DIAlm.IsAlmRec;
  NewDevCfg->DIAlm.IsFTPUpload = DevCfg->AlmCfgPkt.DIAlm.IsFTPUpload;
  NewDevCfg->DIAlm.ActiveDO = DevCfg->AlmCfgPkt.DIAlm.ActiveDO;
  NewDevCfg->DIAlm.IsSendEmail = DevCfg->AlmCfgPkt.DIAlm.IsSendEmail;
  NewDevCfg->DIAlm.AlmOutTimeLen = DevCfg->AlmCfgPkt.AlmOutTimeLen;
  //NewDevCfg->DIAlm.hm = DevCfg->;
  //TMDAlm
  NewDevCfg->MDAlm.ActiveMD = DevCfg->MDCfgPkt.Active;
  NewDevCfg->MDAlm.Sensitive = DevCfg->MDCfgPkt.Sensitive;
  NewDevCfg->MDAlm.IsAlmRec = DevCfg->AlmCfgPkt.MDAlm.IsAlmRec;
  NewDevCfg->MDAlm.IsFTPUpload = DevCfg->AlmCfgPkt.MDAlm.IsFTPUpload;
  NewDevCfg->MDAlm.ActiveDO = DevCfg->AlmCfgPkt.MDAlm.ActiveDO;
  NewDevCfg->MDAlm.IsSendEmail = DevCfg->AlmCfgPkt.MDAlm.IsSendEmail;
  NewDevCfg->MDAlm.AlmOutTimeLen = DevCfg->AlmCfgPkt.AlmOutTimeLen;
  //NewDevCfg->MDAlm.hm = DevCfg->MDCfgPkt.hm;
  memcpy(&NewDevCfg->MDAlm.Rect, &DevCfg->MDCfgPkt.NewRect, sizeof(NewDevCfg->MDAlm.Rect));
  //SoundAlm
  NewDevCfg->SoundAlm.ActiveSoundTrigger = DevCfg->AudioCfgPkt.SoundTriggerActive;
  NewDevCfg->SoundAlm.Sensitive = DevCfg->AudioCfgPkt.SoundTriggerSensitive;
  NewDevCfg->SoundAlm.IsAlmRec = DevCfg->AlmCfgPkt.SoundAlm.IsAlmRec;
  NewDevCfg->SoundAlm.IsFTPUpload = DevCfg->AlmCfgPkt.SoundAlm.IsFTPUpload;
  NewDevCfg->SoundAlm.ActiveDO = DevCfg->AlmCfgPkt.SoundAlm.ActiveDO;
  NewDevCfg->SoundAlm.IsSendEmail = DevCfg->AlmCfgPkt.SoundAlm.IsSendEmail;
  NewDevCfg->SoundAlm.AlmOutTimeLen = DevCfg->AlmCfgPkt.AlmOutTimeLen;
  //RecCfg
  NewDevCfg->RecCfg.RecStreamType = DevCfg->RecCfgPkt.RecStreamType;
  NewDevCfg->RecCfg.IsRecAudio = DevCfg->RecCfgPkt.IsRecAudio;
  NewDevCfg->RecCfg.RecStyle = DevCfg->RecCfgPkt.RecStyle;
  NewDevCfg->RecCfg.Plan = DevCfg->RecCfgPkt.Plan;
  NewDevCfg->RecCfg.Rec_AlmTimeLen = DevCfg->RecCfgPkt.Rec_AlmTimeLen;
  NewDevCfg->RecCfg.Rec_NmlTimeLen = DevCfg->RecCfgPkt.Rec_NmlTimeLen;

  return true;
}

bool NewDevCfg_to_DevCfg(TNewDevCfg* NewDevCfg, TDevCfg* DevCfg)
{
  i32 i;
  //不能要 memset(DevCfg, 0, sizeof(DevCfg));
  //DevInfo
  strcpy(DevCfg->DevInfoPkt.DevModal, NewDevCfg->DevInfo.DevModal);
  DevCfg->DevInfoPkt.SN = NewDevCfg->DevInfo.SN;
  strcpy(DevCfg->DevInfoPkt.SoftVersion, NewDevCfg->DevInfo.SoftVersion);
  strcpy(DevCfg->DevInfoPkt.DevName, NewDevCfg->DevInfo.DevName);

  DevCfg->DevInfoPkt.Info.StandardMask = NewDevCfg->DevInfo.StandardMask;
  DevCfg->DevInfoPkt.Info.SubStandardMask = NewDevCfg->DevInfo.SubStandardMask;
  DevCfg->DevInfoPkt.DevType = NewDevCfg->DevInfo.DevType;
  DevCfg->DevInfoPkt.Info.ExistWiFi = NewDevCfg->DevInfo.ExistWiFi;
  DevCfg->DevInfoPkt.Info.ExistSD = NewDevCfg->DevInfo.ExistSD;
  DevCfg->DevInfoPkt.Info.ExistFlash = NewDevCfg->DevInfo.ExistFlash;
  DevCfg->DevInfoPkt.Info.ethLinkStatus = NewDevCfg->DevInfo.ethLinkStatus;
  DevCfg->DevInfoPkt.Info.wifiStatus = NewDevCfg->DevInfo.wifiStatus;
  DevCfg->DevInfoPkt.Info.upnpStatus = NewDevCfg->DevInfo.upnpStatus;
  DevCfg->DevInfoPkt.Info.WlanStatus = NewDevCfg->DevInfo.WlanStatus;
  DevCfg->DevInfoPkt.Info.p2pStatus = NewDevCfg->DevInfo.p2pStatus;
  DevCfg->DevInfoPkt.Info.HardType = NewDevCfg->DevInfo.HardType;
  DevCfg->DevInfoPkt.TimeZone = NewDevCfg->DevInfo.TimeZone;
  DevCfg->DevInfoPkt.DoubleStream = NewDevCfg->DevInfo.DoubleStream;
  //NetCfg
  DevCfg->NetCfgPkt.DataPort = NewDevCfg->NetCfg.DataPort;
  DevCfg->NetCfgPkt.rtspPort = NewDevCfg->NetCfg.rtspPort;
  DevCfg->NetCfgPkt.HttpPort = NewDevCfg->NetCfg.HttpPort;
  DevCfg->NetCfgPkt.Lan.IPType = NewDevCfg->NetCfg.IPType;
  strcpy(DevCfg->NetCfgPkt.Lan.DevIP, IntToIP(NewDevCfg->NetCfg.DevIP));
  strcpy(DevCfg->NetCfgPkt.Lan.SubMask, IntToIP(NewDevCfg->NetCfg.SubMask));
  strcpy(DevCfg->NetCfgPkt.Lan.Gateway, IntToIP(NewDevCfg->NetCfg.Gateway));
  strcpy(DevCfg->NetCfgPkt.Lan.DNS1, IntToIP(NewDevCfg->NetCfg.DNS1));
  strcpy(DevCfg->NetCfgPkt.Lan.DevMAC, NewDevCfg->NetCfg.DevMAC);
  DevCfg->NetCfgPkt.uPnP.Active = NewDevCfg->NetCfg.ActiveuPnP;
  DevCfg->NetCfgPkt.DDNS.Active = NewDevCfg->NetCfg.ActiveDDNS;
  DevCfg->NetCfgPkt.DDNS.DDNSType = NewDevCfg->NetCfg.DDNSType;
  strcpy(DevCfg->NetCfgPkt.DDNS.DDNSDomain, NewDevCfg->NetCfg.DDNSDomain);
  strcpy(DevCfg->NetCfgPkt.DDNS.DDNSServer, NewDevCfg->NetCfg.DDNSServer);
  //wifiCfg
  DevCfg->WiFiCfgPkt.Active = NewDevCfg->wifiCfg.ActiveWIFI;
  DevCfg->WiFiCfgPkt.IsAPMode = NewDevCfg->wifiCfg.IsAPMode;
  strcpy(DevCfg->WiFiCfgPkt.SSID_AP, NewDevCfg->wifiCfg.SSID_AP);
  strcpy(DevCfg->WiFiCfgPkt.Password_AP, NewDevCfg->wifiCfg.Password_AP);
  strcpy(DevCfg->WiFiCfgPkt.SSID_STA, NewDevCfg->wifiCfg.SSID_STA);
  strcpy(DevCfg->WiFiCfgPkt.Password_STA, NewDevCfg->wifiCfg.Password_STA);
  DevCfg->WiFiCfgPkt.Channel = NewDevCfg->wifiCfg.Channel;
  DevCfg->WiFiCfgPkt.EncryptType = NewDevCfg->wifiCfg.EncryptType;
  DevCfg->WiFiCfgPkt.WPA.Auth = NewDevCfg->wifiCfg.Auth;
  DevCfg->WiFiCfgPkt.WPA.Enc = NewDevCfg->wifiCfg.Enc;
  //p2pCfg
  DevCfg->p2pCfgPkt.Active = NewDevCfg->p2pCfg.ActiveP2P;
  DevCfg->p2pCfgPkt.StreamType = NewDevCfg->p2pCfg.StreamType;
  DevCfg->p2pCfgPkt.p2pType = NewDevCfg->p2pCfg.p2pType;
  strcpy(DevCfg->p2pCfgPkt.UID, NewDevCfg->p2pCfg.UID);
  strcpy(DevCfg->p2pCfgPkt.Password, NewDevCfg->p2pCfg.Password);
  memcpy(DevCfg->p2pCfgPkt.SvrIP, NewDevCfg->p2pCfg.SvrIP, 4*4);
  //VideoCfg
  GetWidthHeightFromStandard(NewDevCfg->VideoCfg.StandardEx0, &DevCfg->VideoCfgPkt.VideoFormat.Width, &DevCfg->VideoCfgPkt.VideoFormat.Height);
  DevCfg->VideoCfgPkt.VideoFormat.FrameRate = NewDevCfg->VideoCfg.FrameRate0;
  DevCfg->VideoCfgPkt.VideoFormat.BitRate = NewDevCfg->VideoCfg.BitRate0 * 1024;
  DevCfg->VideoCfgPkt.VideoFormat.Sub.StandardEx = NewDevCfg->VideoCfg.StandardEx1;
  DevCfg->VideoCfgPkt.VideoFormat.Sub.FrameRate = NewDevCfg->VideoCfg.FrameRate1;
  DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRate = NewDevCfg->VideoCfg.BitRate1 * 1024;
  DevCfg->VideoCfgPkt.VideoFormat.IsMirror = NewDevCfg->VideoCfg.IsMirror;
  DevCfg->VideoCfgPkt.VideoFormat.IsFlip = NewDevCfg->VideoCfg.IsFlip;
  DevCfg->VideoCfgPkt.VideoFormat.IsShowFrameRate = NewDevCfg->VideoCfg.IsShowFrameRate;

  DevCfg->VideoCfgPkt.VideoFormat.BitRateType = NewDevCfg->VideoCfg.BitRateType0;
  DevCfg->VideoCfgPkt.VideoFormat.BitRateQuant = NewDevCfg->VideoCfg.BitRateQuant0;
  DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRateType = NewDevCfg->VideoCfg.BitRateType1;
  DevCfg->VideoCfgPkt.VideoFormat.Sub.BitRateQuant = NewDevCfg->VideoCfg.BitRateQuant1;

  //AudioCfg
  DevCfg->AudioCfgPkt.Active = NewDevCfg->AudioCfg.ActiveAUDIO;
  DevCfg->AudioCfgPkt.InputType = NewDevCfg->AudioCfg.InputTypeAUDIO;
  DevCfg->AudioCfgPkt.VolumeLineIn = NewDevCfg->AudioCfg.VolumeLineIn;
  DevCfg->AudioCfgPkt.VolumeLineOut = NewDevCfg->AudioCfg.VolumeLineOut;
  DevCfg->AudioCfgPkt.AudioFormat.nChannels = NewDevCfg->AudioCfg.nChannels;
  DevCfg->AudioCfgPkt.AudioFormat.wBitsPerSample = NewDevCfg->AudioCfg.wBitsPerSample;
  DevCfg->AudioCfgPkt.AudioFormat.nSamplesPerSec = NewDevCfg->AudioCfg.nSamplesPerSec;
  DevCfg->AudioCfgPkt.AudioFormat.wFormatTag = NewDevCfg->AudioCfg.wFormatTag;

  for (i=0; i<3; i++)//DevCfg->UserCfgPkt.Count
  {
    strcpy(DevCfg->UserCfgPkt.Lst[i].UserName, NewDevCfg->UserCfg.UserName[i]);
    strcpy(DevCfg->UserCfgPkt.Lst[i].Password, NewDevCfg->UserCfg.Password[i]);
    DevCfg->UserCfgPkt.Lst[i].Authority = NewDevCfg->UserCfg.Authority[i];
  }
  //TDIAlm
  DevCfg->AlmCfgPkt.DIAlm.Active = NewDevCfg->DIAlm.ActiveDI;
  DevCfg->AlmCfgPkt.DIAlm.IsAlmRec = NewDevCfg->DIAlm.IsAlmRec;
  DevCfg->AlmCfgPkt.DIAlm.IsFTPUpload = NewDevCfg->DIAlm.IsFTPUpload;
  DevCfg->AlmCfgPkt.DIAlm.ActiveDO = NewDevCfg->DIAlm.ActiveDO;
  DevCfg->AlmCfgPkt.DIAlm.IsSendEmail = NewDevCfg->DIAlm.IsSendEmail;
  DevCfg->AlmCfgPkt.AlmOutTimeLen = NewDevCfg->DIAlm.AlmOutTimeLen;
  //NewDevCfg->DIAlm.hm = DevCfg->;
  //TMDAlm
  DevCfg->MDCfgPkt.Active = NewDevCfg->MDAlm.ActiveMD;
  DevCfg->MDCfgPkt.Sensitive = NewDevCfg->MDAlm.Sensitive;
  DevCfg->AlmCfgPkt.MDAlm.IsAlmRec = NewDevCfg->MDAlm.IsAlmRec;
  DevCfg->AlmCfgPkt.MDAlm.IsFTPUpload = NewDevCfg->MDAlm.IsFTPUpload;
  DevCfg->AlmCfgPkt.MDAlm.ActiveDO = NewDevCfg->MDAlm.ActiveDO;
  DevCfg->AlmCfgPkt.MDAlm.IsSendEmail = NewDevCfg->MDAlm.IsSendEmail;
  DevCfg->AlmCfgPkt.AlmOutTimeLen = NewDevCfg->MDAlm.AlmOutTimeLen;
  //NewDevCfg->MDAlm.hm = DevCfg->MDCfgPkt.hm;
  memcpy(&DevCfg->MDCfgPkt.NewRect, &NewDevCfg->MDAlm.Rect, sizeof(NewDevCfg->MDAlm.Rect));
  //SoundAlm
  DevCfg->AudioCfgPkt.SoundTriggerActive = NewDevCfg->SoundAlm.ActiveSoundTrigger;
  DevCfg->AudioCfgPkt.SoundTriggerSensitive = NewDevCfg->SoundAlm.Sensitive;
  DevCfg->AlmCfgPkt.SoundAlm.IsAlmRec = NewDevCfg->SoundAlm.IsAlmRec;
  DevCfg->AlmCfgPkt.SoundAlm.IsFTPUpload = NewDevCfg->SoundAlm.IsFTPUpload;
  DevCfg->AlmCfgPkt.SoundAlm.ActiveDO = NewDevCfg->SoundAlm.ActiveDO;
  DevCfg->AlmCfgPkt.SoundAlm.IsSendEmail = NewDevCfg->SoundAlm.IsSendEmail;
  DevCfg->AlmCfgPkt.AlmOutTimeLen = NewDevCfg->SoundAlm.AlmOutTimeLen;
  //RecCfg
  DevCfg->RecCfgPkt.RecStreamType = NewDevCfg->RecCfg.RecStreamType;
  DevCfg->RecCfgPkt.IsRecAudio = NewDevCfg->RecCfg.IsRecAudio;
  DevCfg->RecCfgPkt.RecStyle = NewDevCfg->RecCfg.RecStyle;
  DevCfg->RecCfgPkt.Plan = NewDevCfg->RecCfg.Plan;
  DevCfg->RecCfgPkt.Rec_AlmTimeLen = NewDevCfg->RecCfg.Rec_AlmTimeLen;
  DevCfg->RecCfgPkt.Rec_NmlTimeLen = NewDevCfg->RecCfg.Rec_NmlTimeLen;

  return true;
}
//*****************************************************************************
typedef struct TPlayParam {
  TDevCfg DevCfg;
#define MaxBufSize 1024*1024
  char RecvBuf[MaxBufSize];
  i32 RecvLen;

  char RecvDownloadBuf[MaxBufSize];
  int RecvDownloadLen;

  TAVCallBack* avEvent;
  TAlmCallBack* AlmEvent;
  void* UserCustom;

  i32 IsExit;

  u32 VideoChlMask;
  u32 AudioChlMask;
  u32 SubVideoChlMask;
  i32 IsNewStartPlay;

  char40 UserName;
  char40 Password;
  char40 DevIP;
  i32 DataPort;
  u32 TimeOut;

  i32 Isp2pConn;

  u8 p2pType;
  u8 IsStopHttpGet;
  u8 flag[2];
  char40 p2pUID;
  char20 p2pPSD;
  i32 p2p_SessionID;
  i32 p2p_avIndex;
  i32 p2p_talkIndex;
  char20 p2p_SvrIP1;
  char20 p2p_SvrIP2;
  char20 p2p_SvrIP3;
  char20 p2p_SvrIP4;

  bool IsConnect;

  u32 Session;
  THandle hSocket;
  i32 IsCreateRecvThread;

  pthread_t tHandle;
  i32 StreamType;//0主码流 1次码流
  i32 ImgWidth;
  i32 ImgHeight;

  i32 RealBitRate_av;
  i32 RealFrameRate_av;
  i32 LastSenseTime;

}TPlayParam;

//*****************************************************************************
//-----------------------------------------------------------------------------
bool GetWidthHeightFromStandard(i32 Value, i32* w, i32* h)
{
  switch (Value)
  {
  case P720x576: *w = 720; *h = 576; break;
  case P720x288: *w = 720; *h = 288; break;
  case P704x576: *w = 704; *h = 576; break;
  case P704x288: *w = 704; *h = 288; break;
  case P352x288: *w = 352; *h = 288; break;
  case P176x144: *w = 176; *h = 144; break;
  case N720x480: *w = 720; *h = 480; break;
  case N720x240: *w = 720; *h = 240; break;
  case N704x480: *w = 704; *h = 480; break;
  case N704x240: *w = 704; *h = 240; break;
  case N352x240: *w = 352; *h = 240; break;
  case N176x120: *w = 176; *h = 120; break;
  case V160x120: *w = 160; *h = 120; break;
  case V320x240: *w = 320; *h = 240; break;
  case V640x480: *w = 640; *h = 480; break;
  case V800x600: *w = 800; *h = 600; break;
  case V1024x768: *w = 1024; *h = 768; break;
  case V1280x720:  *w = 1280; *h = 720;  break;
  case V1280x800: *w = 1280; *h = 800; break;
  case V1280x960: *w = 1280; *h = 960; break;
  case V1280x1024: *w = 1280; *h = 1024; break;
  case V1360x768: *w = 1360; *h = 768; break;
  case V1400x1050: *w = 1400; *h = 1050; break;
  case V1600x1200: *w = 1600; *h = 1200; break;
    //case V1680x1050: *w = 1680; *h = 1050; break;
  case V1920x1080: *w = 1920; *h = 1080; break;
    //case V2048x1536: *w = 2048; *h = 1536; break;
    //case V2560x1600: *w = 2560; *h = 1600; break;
    //case V2560x2048: *w = 2560; *h = 2048; break;
    //case V3400x2400: *w = 3400; *h = 2400; break;
  default: return false;
  }
  return true;
}
//-----------------------------------------------------------------------------
i32 GetStandardFromWidthHeight(i32 w, i32 h)
{
  if ((w == 720)&&(h == 576)) return P720x576;
  else if ((w == 720)&&(h == 288)) return P720x288;
  else if ((w == 704)&&(h == 576)) return P704x576;
  else if ((w == 704)&&(h == 288)) return P704x288;
  else if ((w == 352)&&(h == 288)) return P352x288;
  else if ((w == 176)&&(h == 144)) return P176x144;
  else if ((w == 720)&&(h == 480)) return N720x480;
  else if ((w == 720)&&(h == 240)) return N720x240;
  else if ((w == 704)&&(h == 480)) return N704x480;
  else if ((w == 704)&&(h == 240)) return N704x240;
  else if ((w == 352)&&(h == 240)) return N352x240;
  else if ((w == 176)&&(h == 120)) return N176x120;
  else if ((w == 160)&&(h == 120)) return V160x120;
  else if ((w == 320)&&(h == 240)) return V320x240;
  else if ((w == 640)&&(h == 480)) return V640x480;
  else if ((w == 800)&&(h == 600)) return V800x600;
  else if ((w == 1024)&&(h == 768)) return V1024x768;
  else if ((w == 1280)&&(h == 720)) return V1280x720;
  else if ((w == 1280)&&(h == 800)) return V1280x800;
  //else if ((w == 1280)&&(h == 854)) return V1280x854;
  else if ((w == 1280)&&(h == 960)) return V1280x960;
  else if ((w == 1280)&&(h == 1024)) return V1280x1024;
  else if ((w == 1360)&&(h == 768)) return V1360x768;
  else if ((w == 1400)&&(h == 1050)) return V1400x1050;
  else if ((w == 1600)&&(h == 1200)) return V1600x1200;
  else if ((w == 1920)&&(h == 1080)) return V1920x1080;
  //else if ((w == 2048)&&(h == 1536)) return V2048x1536;
  //else if ((w == 2560)&&(h == 1600)) return V2560x1600;
  //else if ((w == 2560)&&(h == 2048)) return V2560x2048;
  //else if ((w == 3400)&&(h == 2400)) return V3400x2400;
  else return StandardExMin;
}
//-----------------------------------------------------------------------------
struct TudpParam udp;
u32 SearchIPLst[512];

void OnUDPRecvEvent(char* Buf, i32 BufLen)
{
  i32 Ret = 0;
  TSearchDevCallBack* SearchEvent;
  if (strstr(Buf, "M-SEARCH") != NULL) return;

  if (BufLen != sizeof(TNetCmdPkt)) return;
  TNetCmdPkt* PPkt = (TNetCmdPkt*)Buf;
  if (PPkt->HeadPkt.VerifyCode != Head_CmdPkt) return;
  if (PPkt->HeadPkt.PktSize != sizeof(TCmdPkt)) return;

  if (PPkt->CmdPkt.MsgID == Msg_GetMulticastInfo)
  {
    SearchEvent = (TSearchDevCallBack*)udp.Flag;
    if (SearchEvent)
    {
      SearchEvent(
        PPkt->CmdPkt.MulticastInfo.DevInfo.SN,
        PPkt->CmdPkt.MulticastInfo.DevInfo.DevType,
        PPkt->CmdPkt.MulticastInfo.DevInfo.VideoChlCount,
        PPkt->CmdPkt.MulticastInfo.NetCfg.DataPort,
        PPkt->CmdPkt.MulticastInfo.NetCfg.HttpPort,
        PPkt->CmdPkt.MulticastInfo.DevInfo.DevName,
        PPkt->CmdPkt.MulticastInfo.NetCfg.Lan.DevIP,
        PPkt->CmdPkt.MulticastInfo.NetCfg.Lan.DevMAC,
        PPkt->CmdPkt.MulticastInfo.NetCfg.Lan.SubMask,
        PPkt->CmdPkt.MulticastInfo.NetCfg.Lan.Gateway,
        PPkt->CmdPkt.MulticastInfo.NetCfg.Lan.DNS1,
        PPkt->CmdPkt.MulticastInfo.NetCfg.DDNS.DDNSServer,
        PPkt->CmdPkt.MulticastInfo.NetCfg.DDNS.DDNSDomain,
        PPkt->CmdPkt.MulticastInfo.p2pCfg.UID
        );
    }
  }
}
//-----------------------------------------------------------------------------
bool thSearch_Init(TSearchDevCallBack SearchEvent)
{
  memset(&udp, 0, sizeof(TudpParam));

  udp.Port = Port_Ax_Search_Local;
  udp.OnRecvEvent = OnUDPRecvEvent;

  udp.IsMulticast = true;
  udp.TTL = 32;
  udp.LocalIP = GetLocalIP();
  udp.MultiIP = IP_Ax_Multicast;
  udp.Flag = (THandle)SearchEvent;
  return udp_Init(&udp);
}
//-----------------------------------------------------------------------------
bool thSearch_SetWiFiCfg(char* LocalIP, char* SSID, char* Password)
{
  if (LocalIP) udp.LocalIP = GetLocalIP();
  memset(SearchIPLst, 0, sizeof(SearchIPLst));
  TNetCmdPkt Pkt;
  memset(&Pkt, 0, sizeof(Pkt));

  Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
  Pkt.HeadPkt.PktSize = sizeof(TCmdPkt);
  Pkt.CmdPkt.MsgID = Msg_SetWiFiCfg;
  Pkt.CmdPkt.WiFiCfgPkt.Active = true;
  Pkt.CmdPkt.WiFiCfgPkt.IsAPMode = false;
  strcpy(Pkt.CmdPkt.WiFiCfgPkt.SSID_STA, SSID);
  strcpy(Pkt.CmdPkt.WiFiCfgPkt.Password_STA, Password);

  struct sockaddr_in Addr;
  memset(&Addr,0,sizeof(struct sockaddr_in));
  Addr.sin_family = AF_INET;

  i32 flag = 1;
  setsockopt(udp.SocketHandle, SOL_SOCKET, SO_BROADCAST, &flag, sizeof(flag));
  inet_aton("255.255.255.255", &Addr.sin_addr);
  Addr.sin_port = htons(Port_Ax_Multicast);
  sendto(udp.SocketHandle, &Pkt, sizeof(Pkt), 0, (struct sockaddr*)&Addr, sizeof(Addr));
  return true;
}
//-----------------------------------------------------------------------------
bool thSearch_SearchDevice(char* LocalIP)
{
  if (LocalIP) udp.LocalIP = GetLocalIP();
  memset(SearchIPLst, 0, sizeof(SearchIPLst));
  TNetCmdPkt Pkt;
  memset(&Pkt, 0, sizeof(Pkt));

  Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
  Pkt.HeadPkt.PktSize = sizeof(TCmdPkt);
  Pkt.CmdPkt.MsgID = Msg_GetMulticastInfo;
  struct sockaddr_in Addr;
  memset(&Addr,0,sizeof(struct sockaddr_in));
  Addr.sin_family = AF_INET;
  //inet_aton(IP_Ax_Multicast, &Addr.sin_addr);
  //Addr.sin_port = htons(Port_Ax_Multicast);
  //sendto(udp.SocketHandle, &Pkt, sizeof(Pkt), 0, (struct sockaddr*)&Addr, sizeof(Addr));

  i32 flag = 1;
  setsockopt(udp.SocketHandle, SOL_SOCKET, SO_BROADCAST, &flag, sizeof(flag));
  inet_aton("255.255.255.255", &Addr.sin_addr);
  Addr.sin_port = htons(Port_Ax_Multicast);
  sendto(udp.SocketHandle, &Pkt, sizeof(Pkt), 0, (struct sockaddr*)&Addr, sizeof(Addr));
  return true;
}
//-----------------------------------------------------------------------------
bool thSearch_Free(void)
{
  return udp_Free(&udp);
}
//-----------------------------------------------------------------------------
bool thNet_Init(THandle* NetHandle)
{
  if (*NetHandle != 0) return true;
  TPlayParam* Play = (TPlayParam*)malloc(sizeof(TPlayParam));
  if (!Play) return false;

  memset(Play, 0, sizeof(TPlayParam));
  *NetHandle = (THandle)Play;
  Play->p2p_avIndex = -1;
  Play->p2p_SessionID = -1;
  Play->p2p_talkIndex = -1;
  return (Play != NULL);
}
//-----------------------------------------------------------------------------
bool thNet_SetCallBack(THandle NetHandle, TAVCallBack avEvent, TAlmCallBack AlmEvent, void* UserCustom)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  Play->avEvent = avEvent;
  Play->AlmEvent = AlmEvent;
  Play->UserCustom = UserCustom;
  return true;
}
//-----------------------------------------------------------------------------
bool thNet_Free(THandle* NetHandle)
{
  if (*NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)(*NetHandle);
  //zhb  thNet_StopNmlRec(*NetHandle, 0);
  thNet_DisConn(*NetHandle);
  free(Play);
  *NetHandle = 0;
  return true;
}
//-----------------------------------------------------------------------------
bool thNet_Login(THandle NetHandle)//not export
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;

  if (!Play->Isp2pConn)
  {
    i32 ret, i;
    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.MsgID = Msg_Login;
    strcpy(Pkt.CmdPkt.LoginPkt.UserName, Play->UserName);
    strcpy(Pkt.CmdPkt.LoginPkt.Password, Play->Password);

    strcpy(Pkt.CmdPkt.LoginPkt.DevIP, Play->DevIP);

    Pkt.CmdPkt.LoginPkt.SendSensePkt = true;

    ret = SendBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt));
    memset(&Pkt, 0, sizeof(Pkt));
    ret = RecvBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt));
    if (Pkt.CmdPkt.Value ==0) return false;

    Play->Session = Pkt.CmdPkt.Session;
    Play->DevCfg.DevInfoPkt = Pkt.CmdPkt.LoginPkt.DevInfoPkt;
    Play->DevCfg.VideoCfgPkt.VideoFormat = Pkt.CmdPkt.LoginPkt.v[0];
    Play->DevCfg.AudioCfgPkt.AudioFormat = Pkt.CmdPkt.LoginPkt.a[0];
    TVideoFormat* fmtv = &Play->DevCfg.VideoCfgPkt.VideoFormat;
    Play->ImgWidth  = fmtv->Width;
    Play->ImgHeight = fmtv->Height;
    return true;
  }
}
//-----------------------------------------------------------------------------
bool thNet_GetAllCfg(THandle NetHandle)//not export
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;

  if (!Play->Isp2pConn)
  {
    THeadPkt Head;
    i32 ret, i;
    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Head_CmdPkt;
    Pkt.CmdPkt.MsgID = Msg_GetAllCfg;
    Pkt.CmdPkt.Session = Play->Session;
    ret = SendBuf(Play->hSocket, (char*)&Pkt, sizeof(TNetCmdPkt));

    for (i=0; i<5; i++)
    {
      ret = RecvBuf(Play->hSocket, (char*)&Head, sizeof(THeadPkt));
      if (Head.VerifyCode == Head_CfgPkt) break;
    }

    if (Head.VerifyCode != Head_CfgPkt) return false;
    if (Head.PktSize != sizeof(TDevCfg)) return false;
    ret = RecvBuf(Play->hSocket, (char*)&Play->DevCfg, sizeof(TDevCfg));

    return ret;
  }
  else
  {
#ifdef IsUsedP2P
    i32 ioType, ret;
    TNewCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(TNewCmdPkt));
    Pkt.VerifyCode = Head_CmdPkt;
    Pkt.MsgID = Msg_GetAllCfg;
    Pkt.Result = 0;
    Pkt.PktSize = 0;

    ioType = Head_CmdPkt;
    ret = avSendIOCtrl(Play->p2p_avIndex, ioType, (char*)&Pkt, 8 + Pkt.PktSize);
    if (ret < 0) return false;
    ret = avRecvIOCtrl(Play->p2p_avIndex, &ioType, (char*)&Pkt, sizeof(Pkt), 3000);
    if (ret < 0) return false;
    if (Pkt.VerifyCode == Head_CmdPkt && Pkt.MsgID == Msg_GetAllCfg)
    {
      NewDevCfg_to_DevCfg(&Pkt.NewDevCfg, &Play->DevCfg);
      return true;
    }
#endif
    return false;
  }
}
//-----------------------------------------------------------------------------
void th_RecvData_TCP(THandle NetHandle)
{
  if (NetHandle == 0) return;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play) return;

  char* RecvBuffer = Play->RecvBuf;
  THeadPkt* PHead = (THeadPkt*)(RecvBuffer);
  i32 HEADPKTSIZE = sizeof(THeadPkt);
  bool ret = false;
  time_t t, t1;

starts:
  t = time(NULL);
  t1 = t;
#ifndef WIN32
  fcntl(Play->hSocket, F_SETFL, O_NONBLOCK);//非阻塞方式
#else
  i32 optsize = 1;//0
  ioctlsocket(Play->hSocket, FIONBIO, &optsize);//非阻塞方式
#endif
  Play->LastSenseTime = 0;

  for (;;)
  {
    if (Play->IsExit) return;
    //1 auto conn
    t1 = time(NULL);
    Play->LastSenseTime = t1 - t;
    if (Play->LastSenseTime >=20)
    {
      close(Play->hSocket);
      Play->hSocket = 0;
      Play->IsConnect = false;
      if (Play->AlmEvent) Play->AlmEvent(Net_Disconn, t1, 0, Play->UserCustom);

      ret = thNet_Connect(NetHandle, Play->UserName,Play->Password, Play->DevIP, Play->DataPort, Play->TimeOut, 0);

      if (ret)
      {
        if (Play->AlmEvent) Play->AlmEvent(Net_ReConn, t1, 0, Play->UserCustom);
        thNet_Play(NetHandle, Play->VideoChlMask, Play->AudioChlMask, Play->SubVideoChlMask);
      }
      Play->LastSenseTime = 0;
      t = t1;
      goto starts;
    }
    //2
    if (Play->hSocket <= 0) 
    {
      usleep(100*1000);
      continue;
    }
    //3
    ret = RecvBuf(Play->hSocket, (char*)PHead, HEADPKTSIZE);
    if (!ret) continue;

    if (
      PHead->VerifyCode!=Head_VideoPkt      &&
      PHead->VerifyCode!=Head_AudioPkt      &&
      PHead->VerifyCode!=Head_SensePkt      &&
      PHead->VerifyCode!=Head_TalkPkt       &&
      PHead->VerifyCode!=Head_UploadPkt     &&
      PHead->VerifyCode!=Head_DownloadPkt   &&
      PHead->VerifyCode!=Head_CfgPkt        &&
      PHead->VerifyCode!=Head_MotionInfoPkt &&  //add at 20130506
      PHead->VerifyCode!=Head_CmdPkt)
    {
      continue;
    }

    if (PHead->PktSize + HEADPKTSIZE > sizeof(Play->RecvBuf)) continue;

    ret = RecvBuf(Play->hSocket, RecvBuffer + HEADPKTSIZE, PHead->PktSize);//PktSize==0 return true
    if (!ret) continue;

    //Play->IsConnect = true;
    Play->LastSenseTime = 0;//最后侦测时间
    t = t1;

    switch (PHead->VerifyCode) 
    {
    case Head_VideoPkt:
    case Head_AudioPkt:
      {
        TDataFrameInfo* PInfo = (TDataFrameInfo*)RecvBuffer;
        char* Buf = &RecvBuffer[sizeof(TDataFrameInfo)];
        i32 BufLen = PHead->PktSize - 16;

        if (PHead->VerifyCode == Head_VideoPkt)
        {
          if (Play->IsNewStartPlay)
            if (PInfo->Frame.IsIFrame) Play->IsNewStartPlay = false;
          if (Play->IsNewStartPlay) break;//已包含下面的Play->avEvent 

          Play->RealBitRate_av = Play->RealBitRate_av + PHead->PktSize + HEADPKTSIZE;
          Play->RealFrameRate_av++;

          //thNet_RecordWriteData(NetHandle, PInfo, Buf, BufLen);
          if (Play->avEvent) Play->avEvent(PInfo, Buf, BufLen, Play->UserCustom);
        }

        if (PHead->VerifyCode == Head_AudioPkt)
        {
          if (Play->DevCfg.AudioCfgPkt.AudioFormat.wFormatTag == G711)
          {
            char dstBuf[4096];
            i32 dstBufLen;
            g711a_decode(Buf, BufLen, dstBuf, &dstBufLen);
            //thNet_RecordWriteData(NetHandle, PInfo, dstBuf, dstBufLen);
            if (Play->avEvent) Play->avEvent(PInfo, dstBuf, dstBufLen, Play->UserCustom);
          }
          else//PCM
          {
            //thNet_RecordWriteData(NetHandle, PInfo, Buf, BufLen);
            if (Play->avEvent) Play->avEvent(PInfo, Buf, BufLen, Play->UserCustom);
          }
        }
      }
      break;

    case Head_CmdPkt://网络命令包
      {
        TNetCmdPkt* PPkt = (TNetCmdPkt*)RecvBuffer;
        if (PPkt->CmdPkt.MsgID == Msg_Alarm)
        {
          if (Play->AlmEvent)
          {
            Play->AlmEvent(
              PPkt->CmdPkt.AlmSendPkt.AlmType, 
              PPkt->CmdPkt.AlmSendPkt.AlmTime,
              PPkt->CmdPkt.AlmSendPkt.AlmPort, 
              Play->UserCustom);
          }
        }
      }
      break;
    }//end switch
  }//end for (;;)
}
//-----------------------------------------------------------------------------
bool thNet_Connect(THandle NetHandle, char* UserName, char* Password, char* DevIP, i32 DataPort, u32 TimeOut, i32 IsCreateRecvThread)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (Play->IsConnect) return true;
  Play->IsConnect = false;
  bool ret = false;
  strcpy(Play->UserName, UserName);
  strcpy(Play->Password, Password);
  strcpy(Play->DevIP, DevIP);
  Play->DataPort = DataPort;
  Play->TimeOut = TimeOut;
  if (Play->TimeOut == 0) Play->TimeOut = 3000;
  Play->IsCreateRecvThread = IsCreateRecvThread;
  Play->Isp2pConn = false;

  Play->hSocket = FastConnect(DevIP, DataPort, TimeOut);
  Play->IsConnect = (Play->hSocket > 0);
  if (Play->IsConnect)
  {
    ret = thNet_Login(NetHandle);
    if (ret == false) 
    {
      thNet_DisConn(NetHandle);
      return false;
    }
    /*
    ret = thNet_GetAllCfg(NetHandle);
    if (ret == false) 
    {
    thNet_DisConn(NetHandle);
    return false;
    }
    */
    if (Play->IsCreateRecvThread)
    {
      Play->IsExit = false;
      if (Play->tHandle == 0)
      {
        pthread_create(&Play->tHandle, NULL, (void *(*)(void*))th_RecvData_TCP, (void*)NetHandle);
        //pthread_detach(Play->tHandle);
      }
    }

    Play->IsConnect = ret;
  }
  return Play->IsConnect;
}
//-----------------------------------------------------------------------------
#ifdef IsUsedP2P
void th_RecvData_P2P(THandle NetHandle)
{
  FRAMEINFO_t frameInfo;
  u32 frmNo;
  i32 flag = 0, cnt = 0;

  TPlayParam* Play = (TPlayParam*)NetHandle;
  TNewCmdPkt* PPkt;
  TDataFrameInfo* PInfo;
  i32 BufLen;
  BufLen = 0;
  Play->RecvLen = 0;

  while(1)
  {
    if (Play->IsExit) break;    

#ifdef IS_NEWTUTK
#else
    if (Play->VideoChlMask==0 && Play->AudioChlMask==0 && Play->SubVideoChlMask==0)
    {
      usleep(1000*1);
      continue;
    }
#endif

    i32 outBufSize = 0;
    i32 outFrmSize = 0;
    i32 outFrmInfoSize = 0;
    BufLen = avRecvFrameData2(Play->p2p_avIndex, Play->RecvBuf, MaxBufSize, 
      &outBufSize, &outFrmSize, (char*)&frameInfo, sizeof(FRAMEINFO_t), &outFrmInfoSize, &frmNo);

    if(BufLen == AV_ER_DATA_NOREADY) {usleep(1000*1); continue;}
    else if(BufLen == AV_ER_LOSED_THIS_FRAME) continue;
    else if(BufLen == AV_ER_INCOMPLETE_FRAME) continue;
    else if(BufLen == AV_ER_SESSION_CLOSE_BY_REMOTE) break;
    else if(BufLen == AV_ER_REMOTE_TIMEOUT_DISCONNECT)break;
    else if(BufLen == IOTC_ER_INVALID_SID) break;
    if (BufLen <= 0) continue;

    Play->RecvLen = BufLen;
    PPkt = (TNewCmdPkt*)(Play->RecvBuf);
    if (PPkt->VerifyCode == Head_CmdPkt && PPkt->MsgID == Msg_Alarm)
    {
      if (Play->AlmEvent) Play->AlmEvent(PPkt->AlmSendPkt.AlmType, PPkt->AlmSendPkt.AlmTime, PPkt->AlmSendPkt.AlmPort, Play->UserCustom);
      continue;
    }

    PInfo = (TDataFrameInfo*)(Play->RecvBuf);

    if (PInfo->Head.VerifyCode == Head_VideoPkt)
    {
      //thNet_RecordWriteData(NetHandle, PInfo, Play->RecvBuf+24, Play->RecvLen-24);
      if (Play->avEvent) Play->avEvent(PInfo, Play->RecvBuf+24, Play->RecvLen-24, Play->UserCustom);
      continue;
    }

    if (PInfo->Head.VerifyCode == Head_AudioPkt)
    {
      if (Play->DevCfg.AudioCfgPkt.AudioFormat.wFormatTag == G711)
      {
        char dstBuf[4096];
        i32 dstBufLen;
        g711a_decode(Play->RecvBuf+24, Play->RecvLen-24, dstBuf, &dstBufLen);
        //thNet_RecordWriteData(NetHandle, PInfo, dstBuf, dstBufLen);
        Play->avEvent(PInfo, dstBuf, dstBufLen, Play->UserCustom);
      }
      else//PCM
      {
        //thNet_RecordWriteData(NetHandle, PInfo, Play->RecvBuf+24, Play->RecvLen-24);
        if (Play->avEvent) Play->avEvent(PInfo, Play->RecvBuf+24, Play->RecvLen-24, Play->UserCustom);
      }
      continue;
    }

#ifdef IS_NEWTUTK
    if (PInfo->Head.VerifyCode == Head_DownloadPkt)
    {
      memcpy(Play->RecvDownloadBuf, Play->RecvBuf + sizeof(PInfo->Head), PInfo->Head.PktSize);
      Play->RecvDownloadLen = PInfo->Head.PktSize;
      continue;
    }
#endif
  }

  Play->IsConnect = false;
}
//-----------------------------------------------------------------------------
bool thNet_Connect_P2P(THandle NetHandle, i32 p2pType, char* p2pUID, char* p2pPSD, u32 TimeOut, i32 IsCreateRecvThread)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (Play->IsConnect) return true;
  Play->IsConnect = false;
  i32 ret = false;
  strcpy(Play->p2pUID, p2pUID);
  strcpy(Play->p2pPSD, p2pPSD);
  Play->TimeOut = TimeOut;
  if (Play->TimeOut == 0) Play->TimeOut = 5000*2;
  Play->IsCreateRecvThread = IsCreateRecvThread;

  Play->Isp2pConn = true;

  Play->p2pType = p2pType;

  //todo
  static i32 Isp2pInit = 0;

  struct timeval tv1, tv2;
  int dec; gettimeofday(&tv1, NULL);

  if (!Isp2pInit)
  {
    ret = IOTC_Initialize2(0);
    gettimeofday(&tv2, NULL); dec = timeval_dec(&tv2, &tv1); printf("********IOTC_Initialize2:%d\n", dec);
    if(ret != IOTC_ER_NoERROR) goto exits;
    avInitialize(32);
    Isp2pInit = 1;
  }
  int tmpSID = IOTC_Get_SessionID();
  Play->p2p_SessionID = IOTC_Connect_ByUID_Parallel(Play->p2pUID, tmpSID);
  if (Play->p2p_SessionID < 0) goto exits;

  unsigned long nServType;
  i32 nResend = -1;
  Play->p2p_avIndex = avClientStart2(Play->p2p_SessionID, "admin", Play->p2pPSD, 30, &nServType, 0, &nResend);
  if(Play->p2p_avIndex < 0) goto exits;

  struct st_SInfo Sinfo;
  char *mode[] = {"P2P", "RLY", "LAN"};
  memset(&Sinfo, 0, sizeof(struct st_SInfo));
  IOTC_Session_Check(Play->p2p_SessionID, &Sinfo);

  u16 val = 0;
  avSendIOCtrl(Play->p2p_avIndex, IOTYPE_INNER_SND_DATA_DELAY, (char*)&val, sizeof(u16));

#define AUDIO_SPEAKER_CHANNEL 5
  Play->p2p_talkIndex = avServStart(Play->p2p_SessionID, NULL, NULL, 60, 0, AUDIO_SPEAKER_CHANNEL);
  if(Play->p2p_talkIndex < 0) goto exits;

  //ret = thNet_GetAllCfg(NetHandle);
  //if (!ret) return 0;

  Play->IsExit = false;
  Play->tHandle = 0;

  if (IsCreateRecvThread)
  {
    if (Play->tHandle == 0)
    {
      pthread_create(&Play->tHandle, NULL, (void *(*)(void*))th_RecvData_P2P, (void*)NetHandle);
      //pthread_detach(Play->tHandle);
    }
  }

  Play->IsConnect = true;
  return Play->IsConnect;

exits:
  thNet_DisConn(NetHandle);
  return false;
}
#endif
//-----------------------------------------------------------------------------
bool thNet_DisConn(THandle NetHandle)
{
  i32 i;
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;

  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;
    Play->IsExit = true;
    if (Play->hSocket > 0)
    {
      close(Play->hSocket);
      Play->hSocket = 0;
    }
    Play->IsConnect = false;
    if (Play->tHandle > 0)
    {
      pthread_join(Play->tHandle, NULL);
      close(Play->tHandle);
      Play->tHandle = 0;
    }
#ifndef WIN32
    close(Play->tHandle);
    Play->tHandle = 0;
#endif
  }
  else
  {
#ifdef IsUsedP2P
    Play->IsExit = true;
    if (Play->p2p_avIndex >= 0)
    {
      avClientStop(Play->p2p_avIndex);
      Play->p2p_avIndex = -1;
    }
    if (Play->p2p_talkIndex >=0)
    {
      avServStop(Play->p2p_talkIndex);
      Play->p2p_talkIndex = -1;
    }
    if (Play->p2p_SessionID >= 0)
    {
      IOTC_Session_Close(Play->p2p_SessionID);
      Play->p2p_SessionID = -1;
    }
    Play->IsConnect = false;
    if (Play->tHandle > 0)
    {
      pthread_join(Play->tHandle, NULL);
      close(Play->tHandle);
      Play->tHandle = 0;
    }

    //avDeInitialize();
    //IOTC_DeInitialize();
#endif
  }
  return true;
}
//-----------------------------------------------------------------------------
bool thNet_IsConnect(THandle NetHandle)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  return Play->IsConnect;
}
//-----------------------------------------------------------------------------
bool thNet_Play(THandle NetHandle, u32 VideoChlMask, u32 AudioChlMask, u32 SubVideoChlMask)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;

  if (!Play->IsConnect) return false;
  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;

    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.MsgID = Msg_PlayLive;
    Pkt.CmdPkt.Session = Play->Session;

    Play->VideoChlMask = VideoChlMask;
    Play->AudioChlMask = AudioChlMask;
    Play->SubVideoChlMask = SubVideoChlMask;
    Play->IsNewStartPlay = true;

    Pkt.CmdPkt.LivePkt.VideoChlMask = VideoChlMask;//主码流
    Pkt.CmdPkt.LivePkt.AudioChlMask = AudioChlMask;
    Pkt.CmdPkt.LivePkt.SubVideoChlMask = SubVideoChlMask;
    return (SendBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt))>0);
  }
  else
  {
#ifdef IsUsedP2P
    TNewCmdPkt Pkt;
    i32 ret;
    Play->VideoChlMask = VideoChlMask;
    Play->AudioChlMask = AudioChlMask;
    Play->SubVideoChlMask = SubVideoChlMask;
    Play->IsNewStartPlay = true;

    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.VerifyCode = Head_CmdPkt;
    Pkt.PktSize = sizeof(Pkt.LivePkt);
    Pkt.MsgID = Msg_PlayLive;
    Pkt.Result = 0;

    Pkt.LivePkt.VideoChlMask = VideoChlMask;//主码流
    Pkt.LivePkt.AudioChlMask = AudioChlMask;
    Pkt.LivePkt.SubVideoChlMask = SubVideoChlMask;

    ret = avSendIOCtrl(Play->p2p_avIndex, Head_CmdPkt, (char*)&Pkt, 8 + Pkt.PktSize);
    if(ret < 0) return 0;
#endif
    return true;
  }
}
//-----------------------------------------------------------------------------
bool thNet_Stop(THandle NetHandle)
{
  return thNet_Play(NetHandle, 0, 0, 0);
}
//-----------------------------------------------------------------------------
bool thNet_SendCmdPkt(THandle NetHandle, TNetCmdPkt* sendPkt, TNetCmdPkt* recvPkt)//TCP
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  //if (!Play->IsConnect) return false;
  if (!Play->Isp2pConn)
  {
    THandle hSocket, ret, Session;
    TNetCmdPkt Pkt;

    hSocket = FastConnect(Play->DevIP, Play->DataPort, Play->TimeOut);
    if (hSocket == 0 || hSocket == -1) return false;

    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.MsgID = Msg_Login;
    strcpy(Pkt.CmdPkt.LoginPkt.UserName, Play->UserName);
    strcpy(Pkt.CmdPkt.LoginPkt.Password, Play->Password);

    strcpy(Pkt.CmdPkt.LoginPkt.DevIP, Play->DevIP);

    ret = SendBuf(hSocket, (char*)&Pkt, sizeof(TNetCmdPkt));
    memset(&Pkt, 0, sizeof(Pkt));
    ret = RecvBuf(hSocket, (char*)&Pkt, sizeof(TNetCmdPkt));
    if (Pkt.CmdPkt.Value == 0)
    {
      close(hSocket);
      return false;
    }

    Session = Pkt.CmdPkt.Session;

    sendPkt->HeadPkt.VerifyCode = Head_CmdPkt;
    sendPkt->HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    sendPkt->CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    sendPkt->CmdPkt.Session = Session;
    ret = SendBuf(hSocket, (char*)sendPkt, sizeof(TNetCmdPkt));
    if (recvPkt)
    {
      ret = RecvBuf(hSocket, (char*)recvPkt, sizeof(TNetCmdPkt));
    }
    close(hSocket);
    return ret;
  }
}
//-----------------------------------------------------------------------------
bool thNet_SetTalk(THandle NetHandle, char* Buf, i32 BufLen)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->Isp2pConn)
  {
    bool ret;
    if (Play->Session == 0) return false;
    TTalkHeadPkt Head;
    Head.VerifyCode = Head_TalkPkt;
    Head.PktSize = BufLen + 24;
    ret = SendBuf(Play->hSocket, (char*)&Head, sizeof(TTalkHeadPkt));
    if (ret) return (SendBuf(Play->hSocket, Buf, BufLen)>0);
  }
  else
  {
#ifdef IsUsedP2P
    i32 i, idiv, imod, iMaxSize, ret;
    FRAMEINFO_t framInfo;

    if (Play->p2p_SessionID < 0) return false;
    if (Play->p2p_talkIndex < 0) return false;

    memset(&framInfo, 0, sizeof(FRAMEINFO_t));
    framInfo.timestamp = 0;
    framInfo.codec_id = MEDIA_CODEC_AUDIO_PCM;
    framInfo.cam_index = 0;
    framInfo.flags = (AUDIO_SAMPLE_8K << 2) | (AUDIO_DATABITS_16 << 1) | AUDIO_CHANNEL_MONO;
    framInfo.onlineNum = 1;//online;

    iMaxSize = 1024;
    idiv = BufLen / iMaxSize;
    imod = (BufLen % iMaxSize);
    for (i=0; i<idiv; i++)
    {
      ret = avSendAudioData(Play->p2p_talkIndex, &Buf[i*iMaxSize], iMaxSize, &framInfo, sizeof(FRAMEINFO_t));
    }
    if (imod > 0) ret = avSendAudioData(Play->p2p_talkIndex, &Buf[i*iMaxSize], imod, &framInfo, sizeof(FRAMEINFO_t));
#endif
    return true;
  }
}
//-----------------------------------------------------------------------------
bool thNet_GetVideoCfg(THandle NetHandle, i32* VideoType, i32* Width0, i32* Height0, i32* FrameRate0, i32* Width1, i32* Height1, i32* FrameRate1)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  TVideoFormat* fmt = &Play->DevCfg.VideoCfgPkt.VideoFormat;
  *VideoType = fmt->VideoType;
  *Width0 = fmt->Width;
  *Height0 = fmt->Height;
  *FrameRate0 = fmt->FrameRate;
  *FrameRate1 = fmt->Sub.FrameRate;
  GetWidthHeightFromStandard(fmt->Sub.StandardEx, Width1, Height1);
  return true;
}
//-----------------------------------------------------------------------------
bool thNet_CreateRecvThread(THandle NetHandle)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  Play->IsExit = false;

  //  if (Play->tHandle == 0)
  {
    if (!Play->Isp2pConn)
    {
      if (Play->Session == 0) return false;
      pthread_create(&Play->tHandle, NULL, (void *(*)(void*))th_RecvData_TCP, (void*)NetHandle);
      //pthread_detach(Play->tHandle);
    }
    else
    {
#ifdef IsUsedP2P
      pthread_create(&Play->tHandle, NULL, (void *(*)(void*))th_RecvData_P2P, (void*)NetHandle);
      //pthread_detach(Play->tHandle);
#endif
    }
  }
  return true;
}
//-----------------------------------------------------------------------------
bool thNet_RemoteFilePlay(THandle NetHandle, char* FileName)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;
    Play->VideoChlMask = 0;
    Play->AudioChlMask = 1;
    Play->SubVideoChlMask = 1;

    bool ret = false;

    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.MsgID = Msg_StartPlayRecFile;
    Pkt.CmdPkt.Session = Play->Session;
    strcpy(Pkt.CmdPkt.RecFilePkt.FileName, FileName);
    ret = SendBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt));
    return ret;
  }
  else
  {
#ifdef IsUsedP2P
    Play->VideoChlMask = 0;
    Play->AudioChlMask = 1;
    Play->SubVideoChlMask = 1;

    i32 ret = false;
    TNewCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.VerifyCode = Head_CmdPkt;
    Pkt.MsgID = Msg_StartPlayRecFile;
    Pkt.Result = false;
    Pkt.PktSize = sizeof(Pkt.RecFilePkt);
    strcpy(Pkt.RecFilePkt.FileName, FileName);
    ret = avSendIOCtrl(Play->p2p_avIndex, Head_CmdPkt, (char*)&Pkt, 8 + Pkt.PktSize);
    if (ret < 0) return false;
#endif
    return true;
  }
}
//-----------------------------------------------------------------------------
bool thNet_RemoteFileStop(THandle NetHandle)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;
    Play->VideoChlMask = 0;
    Play->AudioChlMask = 0;
    Play->SubVideoChlMask = 0;
    i32 ret;
    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.Session = Play->Session;
    Pkt.CmdPkt.MsgID = Msg_StopPlayRecFile;
    ret = SendBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt));
    return ret;
  }
  else
  {
#ifdef IsUsedP2P
    Play->VideoChlMask = 0;
    Play->AudioChlMask = 0;
    Play->SubVideoChlMask = 0;
    i32 ret = false;
    TNewCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.VerifyCode = Head_CmdPkt;
    Pkt.MsgID = Msg_StopPlayRecFile;
    Pkt.Result = false;
    Pkt.PktSize = 0;
    ret = avSendIOCtrl(Play->p2p_avIndex, Head_CmdPkt, (char*)&Pkt, 8 + Pkt.PktSize);
    if (ret < 0) return false;
#endif
    return true;
  }
}
//-----------------------------------------------------------------------------
bool thNet_RemoteFilePlayControl(THandle NetHandle, i32 PlayCtrl, i32 Speed, i32 Pos)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;

    i32 ret;
    TNetCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.HeadPkt.VerifyCode = Head_CmdPkt;
    Pkt.HeadPkt.PktSize = sizeof(Pkt.CmdPkt);
    Pkt.CmdPkt.PktHead = Pkt.HeadPkt.VerifyCode;
    Pkt.CmdPkt.Session = Play->Session;
    Pkt.CmdPkt.MsgID = Msg_PlayControl;
    Pkt.CmdPkt.PlayCtrlPkt.PlayCtrl = PlayCtrl;
    Pkt.CmdPkt.PlayCtrlPkt.Speed = Speed;
    Pkt.CmdPkt.PlayCtrlPkt.Pos = Pos;
    ret = SendBuf(Play->hSocket, (char*)&Pkt, sizeof(Pkt));
    return ret;
  }
  else
  {
#ifdef IsUsedP2P
    i32 ret = false;
    TNewCmdPkt Pkt;
    memset(&Pkt, 0, sizeof(Pkt));
    Pkt.VerifyCode = Head_CmdPkt;
    Pkt.MsgID = Msg_PlayControl;
    Pkt.Result = false;
    Pkt.PktSize = sizeof(Pkt.PlayCtrlPkt);
    Pkt.PlayCtrlPkt.PlayCtrl = PlayCtrl;
    Pkt.PlayCtrlPkt.Speed = Speed;
    Pkt.PlayCtrlPkt.Pos = Pos;
    ret = avSendIOCtrl(Play->p2p_avIndex, Head_CmdPkt, (char*)&Pkt, 8 + Pkt.PktSize);
    if (ret < 0) return false;
    return true;
#endif
  }
}
//-----------------------------------------------------------------------------
bool thNet_HttpGetStop(THandle NetHandle)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  Play->IsStopHttpGet = true;
}
//-----------------------------------------------------------------------------
bool thNet_HttpGet(THandle NetHandle, char* url, char* Buf, i32* BufLen)
{
  if (NetHandle == 0) return false;
  TPlayParam* Play = (TPlayParam*)NetHandle;
  if (!Play->IsConnect) return false;

  i32 ret;
  if (!Play->Isp2pConn)
  {
    if (Play->Session == 0) return false;
    ret = httpget1(url, Buf, BufLen, false, 1000*15);
    return true;
  }
  else
  {
#ifdef IsUsedP2P
    char64 SvrName, HostName, UserNamePassword;
    char PageName[1024];
    i32 port, ret;
    i32 ioType;
    TNewCmdPkt SendPkt, recvPkt;

    Play->IsStopHttpGet = false;

    ret = sscanf(url, "http://%[^@]@%[^/]%s", UserNamePassword, SvrName, PageName);
    if (ret != 3)
    {
      ret = sscanf(url, "http://%[^/]%s", SvrName, PageName);
      if (ret != 2) strcpy(PageName, "/");
    }

    if (sscanf(SvrName, "%[^:]:%d", HostName, &port) == 2)
    {
      if ((strlen(HostName) == 0) || (port <= 0) || (port >0xffff)) return 0;
    }
    else
    {
      strcpy(HostName, SvrName);
      port = 80;
    }

    *BufLen = 0;
    memset(&SendPkt, 0, sizeof(TNewCmdPkt));
    SendPkt.VerifyCode = Head_CmdPkt;
    SendPkt.MsgID = Msg_HttpGet;
    SendPkt.Result = 0;
    sprintf(SendPkt.Buf, "http://localhost:%d%s", port, PageName);
    SendPkt.PktSize = strlen(SendPkt.Buf);

#ifdef IS_NEWTUTK
#warning " IS_NEWTUTK IS_NEWTUTK IS_NEWTUTK IS_NEWTUTK "
    Play->RecvDownloadLen = 0;
    ioType = Head_CmdPkt;
    ret = avSendIOCtrl(Play->p2p_avIndex, ioType, (char*)&SendPkt, 8 + SendPkt.PktSize);
    if (ret < 0) return false;

    int dt = time(NULL);
    while(1)////zhb20150906add
    {
      if (time(NULL) - dt >= 10) return false;//10s
      if (Play->IsStopHttpGet == true) return false;
      if (Play->RecvDownloadLen > 0)
      {
        memcpy(Buf, Play->RecvDownloadBuf, Play->RecvDownloadLen);
        *BufLen = Play->RecvDownloadLen;
        Play->RecvDownloadLen = 0;
        break;
      }
      usleep(1000*100);
    }
#else
    ioType = Head_CmdPkt;
    ret = avSendIOCtrl(Play->p2p_avIndex, ioType, (char*)&SendPkt, 8 + SendPkt.PktSize);
    if (ret < 0) return false;

    while(1)
    {
      if (Play->IsStopHttpGet == true) break;
      ret = avRecvIOCtrl(Play->p2p_avIndex, &ioType, (char*)&recvPkt, sizeof(recvPkt), 1000*15);
      if (ret < 0) return false;
      if (!(recvPkt.VerifyCode == Head_CmdPkt && recvPkt.MsgID == Msg_HttpGet)) return false;

      memcpy(&Buf[*BufLen], recvPkt.Buf, recvPkt.PktSize);
      *BufLen = *BufLen + recvPkt.PktSize;
      if (recvPkt.Result == 1) break;
    }
#endif
#endif
    return true;
  }
}
